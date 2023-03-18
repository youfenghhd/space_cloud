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

    //阿里网关地址
    private static final String GATEWAY_URL =
            "https://openapi.alipaydev.com/gateway.do";
    //数据格式
    private static final String FORMAT = "JSON";
    //编码格式
    private static final String CHARSET = "UTF-8";
    //签名方式
    private static final String SIGN_TYPE = "RSA2";
    @Resource
    private AlipayConfig alipayConfig;

    @GetMapping("/pay")
    public void pay(AliPay aliPay, HttpServletResponse response) throws
            IOException {
        System.out.println(aliPay);
        System.out.println("alipayConfig:" + alipayConfig);
        //1.创建client，通过阿里SDK提供的client，负责调用支付宝的API
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
     *
     * @return
     */
    @CacheEvict(value = "info", allEntries = true)
    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) throws
            AlipayApiException {
        System.out.println("异步回调了。" + request);
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("-------------支付宝异步回调----");
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            System.out.println("-----params-----------");
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                System.out.println(name + " " + request.getParameter(name));
            }
            String outTradeNo = params.get("out_trade_no");
            String gmtPayment = params.get("gmt_payment");//支付时间
            String alipayTradeNo = params.get("trade_no");
            String totalAmount = params.get("total_amount");
            String sign = params.get("sign");//拿到签名
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content,
                    sign,
                    alipayConfig.getAlipayPublicKey(),
                    "UTF-8");//验证签名
            //支付宝验签
            if (checkSignature) {
                //验签通过
                System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " +
                        params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " +
                        params.get("buyer_pay_amount"));
                // 查询订单
                UCenter uCenter = uService.getById(outTradeNo.substring(0, 19));
                uService.upToVip(uCenter,Integer.parseInt(totalAmount.split("\\.")[0])/10);
                //TODO 支付成功，操作数据库，创建对应订单，修改对应商品数据
                return "success";
            }
        }
        return "success";
    }
}
