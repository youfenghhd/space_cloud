package com.hhd.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.hhd.config.AlipayConfig;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.vo.AliPay;
import com.hhd.service.IUCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author -无心
 * @date 2023/2/19 1:04:49
 */
@RestController
@RequestMapping("/alipay")
public class AliPayController {

    @Autowired
    private IUCenterService uService;
    private static final String GATEWAY_URL =
            "https://openapi.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";
    private static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    private static final String TRADE_STATUS = "trade_status";
    @Resource
    private AlipayConfig alipayConfig;

    @GetMapping("/pay")
    public void pay(AliPay aliPay, HttpServletResponse response) throws
            IOException {
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL,
                alipayConfig.getAppId(),
                alipayConfig.getAppPrivateKey(),
                FORMAT,
                CHARSET,
                alipayConfig.getAlipayPublicKey(),
                SIGN_TYPE);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", aliPay.getTraceNo());
        bizContent.put("total_amount", aliPay.getTotalAmount());
        bizContent.put("subject", aliPay.getSubject());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //设置响应结果，将返回的内容写出到浏览器
        response.setContentType("text/html;charset=" + CHARSET);
        response.getWriter().write(form);
        response.getWriter().flush();
        response.getWriter().close();
    }

    /**
     * 支付宝异步回调【必须是POST】
     */
    @CacheEvict(value = "info", allEntries = true)
    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) throws
            AlipayApiException {
        if (TRADE_SUCCESS.equals(request.getParameter(TRADE_STATUS))) {
            Map<String, String> params = new HashMap<>(8);
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }
            String outTradeNo = params.get("out_trade_no");
            String totalAmount = params.get("total_amount");
            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content,
                    sign,
                    alipayConfig.getAlipayPublicKey(),
                    "UTF-8");
            //支付宝验签
            if (checkSignature) {
                UCenter uCenter = uService.getById(outTradeNo.substring(0, 19));
                uService.upToVip(uCenter, Integer.parseInt(totalAmount.split("\\.")[0]) / 10);
                return "success";
            }
        }
        return "success";
    }
}
