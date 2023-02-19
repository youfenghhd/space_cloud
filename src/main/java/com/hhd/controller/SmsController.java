package com.hhd.controller;

import com.hhd.service.ISmsService;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author -无心
 * @date 2023/2/17 21:17:16
 */
@RestController
@RequestMapping("/sends")
public class SmsController {
    @Autowired
    private ISmsService service;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/{tel}")
    public R send(@PathVariable String tel) {
        if (redisTemplate.opsForValue().get(tel) != null) {
            System.out.println("短信发送成功");
            return R.ok();
        }
        return service.getSmsCode(tel) ? R.ok() : R.error().message(R.SEND_SMS_ERR);
    }

}
