package com.hhd.controller;


import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.vo.Register;
import com.hhd.service.IUCenterService;
import com.hhd.utils.*;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@RestController
@CrossOrigin
@Api(tags = "用户模块")
@RequestMapping("/users")
public class UCenterController {
    @Autowired
    private IUCenterService uService;
    private static final int MD5LENGTH = 32;

    @PassToken
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Results login(@RequestBody UCenter ucenter) {
        Map.Entry<String, UCenter> entry = uService.login(ucenter).entrySet().iterator().next();
        String token = entry.getKey();
        UCenter user = entry.getValue();
        uService.removeExpirationVip(user);
        return Results.ok().data("token", token).data("user", user);
    }

    @PassToken
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Results register(@RequestBody Register register) {
        return uService.register(register);
    }

    @PassToken
    @Operation(summary = "根据id查询用户信息")
    @Cacheable(cacheNames = "info", unless = "#result==null", key = "#id")
    @GetMapping("getInfo/{id}")
    public Results getInfo(@PathVariable String id) {
        return Results.ok().data("user", uService.selectOne(id));
    }

    @ConfirmToken
    @Operation(summary = "用户更改信息")
    @CachePut(value = "info", key = "#uCenter.id")
    @PostMapping("update")
    public Results updateInfo(@RequestBody UCenter uCenter) {
        if (uCenter.getPassword().length() != MD5LENGTH) {
            uCenter.setPassword(ShaThree.encrypt(uCenter.getPassword()));
        }
        return uService.updateById(uCenter) ? Results.ok().data("user", uCenter) : Results.error();
    }

    @PassToken
    @Operation(summary = "查询手机有无被注册")
    @Cacheable("mobile")
    @GetMapping("mobile/{mobile}")
    public Results selectMobileUser(@PathVariable String mobile) {
        return uService.selectOneByMobile(mobile);
    }

    @ConfirmToken
    @Operation(summary = "充值会员")
    @CachePut(value = "info", key = "#uCenter.id")
    @PostMapping("/vip/{month}")
    public Results recharge(@RequestBody UCenter uCenter, @PathVariable int month) {
        return uService.upToVip(uCenter, month);
    }
}

