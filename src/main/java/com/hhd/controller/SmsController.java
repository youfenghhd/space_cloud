package com.hhd.controller;

import com.hhd.service.SmsService;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author -无心
 * @date 2023/2/17 21:17:16
 */
@RestController
@RequestMapping("/sendsms")
public class SmsController {
    @Autowired
    private SmsService service;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/{tel}")
    public R send(@PathVariable String tel) {
        if (redisTemplate.opsForValue().get(tel) != null) {
            return service.getSmsCode(tel);
        }
        return R.error().message(R.SEND_SMS_ERR);
    }

}
