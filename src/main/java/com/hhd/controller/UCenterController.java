package com.hhd.controller;


import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.vo.Register;
import com.hhd.service.IUCenterService;
import com.hhd.utils.MD5;
import com.hhd.utils.R;
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

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R login(@RequestBody UCenter ucenter) {
        Map.Entry<String, UCenter> entry = uService.login(ucenter).entrySet().iterator().next();
        String token = entry.getKey();
        UCenter user = entry.getValue();
        return R.ok().data("token", token).data("user", user);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R register(@RequestBody Register register) {
        return uService.register(register);
    }


    @Operation(summary = "根据id查询用户信息")
    @Cacheable(cacheNames = "info",unless = "#result==null")
    @GetMapping("getInfo/{id}")
    public R getInfo(@PathVariable String id) {
        UCenter uCenter = uService.selectOne(id);
        return R.ok().data("user", uCenter);
    }

    @Operation(summary = "用户更改信息")
    @CachePut("info")
    @PostMapping("update")
    public R updateInfo(@RequestBody UCenter uCenter) {
        UCenter original = uService.selectOne(uCenter.getId());
        UCenter end = new UCenter();
        end.setId(uCenter.getId());
        end.setMemory(original.getMemory());
        end.setPortrait(uCenter.getPortrait());
        end.setDownLoadAdd(uCenter.getDownLoadAdd());
        end.setNickname(uCenter.getNickname());
        end.setPassword(MD5.encrypt(uCenter.getPassword()));
        return uService.updateById(end) ? R.ok() : R.error();
    }
}

