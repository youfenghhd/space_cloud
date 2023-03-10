package com.hhd.controller;


import com.hhd.pojo.domain.Admin;
import com.hhd.pojo.domain.UCenter;
import com.hhd.service.IAdminService;
import com.hhd.utils.MD5;
import com.hhd.utils.R;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
@RequestMapping("/admins")
@Api(tags = "管理员")
@CrossOrigin
public class AdminController {
    @Autowired
    private IAdminService service;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R login(@RequestBody Admin admin) {
        Map.Entry<String, Admin> entry = service.login(admin).entrySet().iterator().next();
        String token = entry.getKey();
        Admin admins = entry.getValue();
        return R.ok().data("token", token).data("admin", admins);
    }

    @Operation(summary = "根据id查询管理员信息")
//    @Cacheable(cacheNames = "info", unless = "#result==null")
    @GetMapping("getInfo/{id}")
    public R getInfo(@PathVariable String id) {
        return R.ok().data("admin", service.selectOne(id));
    }

    @Operation(summary = "管理员更改信息")
//    @CachePut(value = "info")
    @PostMapping("update")
    public R updateInfo(@RequestBody Admin admin) {
        admin.setPassword(MD5.encrypt(admin.getPassword()));
        return service.updateById(admin) ? R.ok() : R.error();
    }
    @Cacheable(cacheNames = "normalUser", unless = "#result==null")
    @Operation(summary = "查找所有正常的用户")
    @GetMapping
    public R findNormal() {
        return R.ok().data("normal", service.showNormalAll());
    }

    @Operation(summary = "查找回收站")
    @Cacheable(cacheNames = "recoveryUser", unless = "#result==null")
    @GetMapping("/recovery")
    public R findRecovery() {
        return R.ok().data("recovery", service.showRecoveryAll());
    }

    @Operation(summary = "禁用/启用状态")
    @CachePut("normalUser")
    @PutMapping("/status")
    public R changeStatus(@RequestBody UCenter user) {
        return service.changeStatus(user) > 0 ?
                R.ok() : R.error();
    }

    @Operation(summary = "用户加入回收站")
    @CachePut("recoveryUser")
    @PutMapping("/del")
    public R logicDelUser(@RequestBody String id) {
        return service.logicDelUser(id) > 0 ? R.ok() : R.error();
    }

    @Operation(summary = "用户移出回收站")
    @CachePut("normalUser")
    @PutMapping("/normal")
    public R logicNormalUser(@RequestBody String id) {
        return service.logicNormalUser(id) > 0 ? R.ok() : R.error();
    }

    @Operation(summary = "从回收站彻底删除")
    @DeleteMapping("/delUser")
    public R delUserById(@RequestBody List<String> id) {
        return service.delById(id) > 0 ? R.ok() : R.error();
    }
}

