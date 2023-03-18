package com.hhd.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author -无心
 * @date 2023/1/4 21:53:35
 */
@Data
@Component
public class AlipayConfig {
    @Value("${alipay.appId}")
    private String appId;

    /**
     * 应用私钥
     */
    @Value("${alipay.appPrivateKey}")
    private String appPrivateKey;

    /**
     * 阿里公钥
     */
    @Value("${alipay.alipayPublicKey}")

    private String alipayPublicKey;
    /**
     * 阿里调用我们的地址【内网穿透】
     */
    @Value("${alipay.notifyUrl}")
    private String notifyUrl;
}
