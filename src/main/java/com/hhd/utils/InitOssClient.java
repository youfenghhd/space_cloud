package com.hhd.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author -无心
 * @date 2023/2/19 22:24:45
 */
@Component
public class InitOssClient implements InitializingBean {
    @Value("${aliyun.oss.file.httpsPrefix}")
    private String httpsPrefix;
    @Value("${aliyun.oss.file.endpoint}")
    private String ossEndpoint;
    @Value("${aliyun.oss.file.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.oss.file.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.file.bucketName}")
    private String bucketName;
    @Value("${aliyun.oss.file.regionId}")
    private String regionId;


    public static String HTTPS_PREFIX;
    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;
    public static String REGION_ID;

    @Override
    public void afterPropertiesSet() {
        HTTPS_PREFIX = httpsPrefix;
        END_POINT = ossEndpoint;
        ACCESS_KEY_ID = accessKeyId;
        ACCESS_KEY_SECRET = accessKeySecret;
        BUCKET_NAME = bucketName;
        REGION_ID = regionId;
    }


}
