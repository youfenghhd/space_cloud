package com.hhd.controller;

import com.hhd.service.ISmsService;
import com.hhd.utils.PassToken;
import com.hhd.utils.Results;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author -无心
 * @date 2023/2/17 21:17:16
 */
@RestController
@CrossOrigin
@Api(tags = "短信验证码处理")
@RequestMapping("/sends")
public class SmsController {
    @Autowired
    private ISmsService service;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PassToken
    @Operation(summary = "获取短信验证码")
    @PostMapping("/{tel}")
    public Results send(@PathVariable String tel) {
        if (redisTemplate.opsForValue().get(tel) != null) {
            return Results.ok();
        }
        return service.getSmsCode(tel) ? Results.ok() : Results.error().message(Results.SEND_SMS_ERR);
    }

}
