package com.hhd.service.impl;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.service.ISmsService;
import com.hhd.utils.CheckCodeUtils;
import com.hhd.utils.InitOssClient;
import com.hhd.utils.Results;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author -无心
 * @date 2023/2/17 16:33:43
 */
@Service
public class SmsServiceImpl implements ISmsService {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean getSmsCode(String tel) {
        String code = CheckCodeUtils.generateTel(tel);
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(InitOssClient.ACCESS_KEY_ID)
                .accessKeySecret(InitOssClient.ACCESS_KEY_SECRET)
                .build());

        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                )
                .build();

        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName("黄辉达网盘毕业项目")
                .templateCode("SMS_270150313")
                .phoneNumbers(tel)
                .templateParam("{\"code\":\"" + code + "\"}")
                .build();
        try {
            CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
            SendSmsResponse resp = response.get();
            System.out.println(new Gson().toJson(resp));
        } catch (InterruptedException e) {
            throw new CloudException(Results.ERROR, Results.INTER_ERR);
        } catch (ExecutionException e) {
            throw new CloudException(Results.ERROR, Results.EXECUTION_ERR);
        }
        redisTemplate.opsForValue().set(tel, code, 1, TimeUnit.MINUTES);
        return true;
    }
}
