package com.hhd.controller;


import com.hhd.pojo.domain.Admin;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.entity.Files;
import com.hhd.service.IAdminService;
import com.hhd.utils.*;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
@RequestMapping("/admins")
@Api(tags = "管理员")
@CrossOrigin
public class AdminController {
    @Autowired
    private IAdminService service;

    private static final int SHA3_LENGTH = 64;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    @PassToken
    public Results login(@RequestBody Admin admin) {
        Map.Entry<String, Admin> entry = service.login(admin).entrySet().iterator().next();
        String token = entry.getKey();
        Admin admins = entry.getValue();
        return Results.ok().data("token", token).data("admin", admins);
    }

    @PassToken
    @Operation(summary = "根据id查询管理员信息")
    @Cacheable(cacheNames = "info", unless = "#result==null", key = "#aid")
    @GetMapping("getInfo/{aid}")
    public Results getInfo(@PathVariable String aid) {
        return Results.ok().data("admin", service.selectOne(aid));
    }

    @PassToken
    @Operation(summary = "查找所有正常的用户")
    @Cacheable(value = "normalUsers", unless = "#result==null")
    @GetMapping
    public Results findNormal() {
        return Results.ok().data("normal", service.showNormalAll());
    }

    @ConfirmToken
    @Operation(summary = "管理员更改信息")
    @CachePut(value = "info", key = "#admin.aid")
    @PostMapping("update")
    public Results updateInfo(@RequestBody Admin admin) {
        if (admin.getPassword().length() != SHA3_LENGTH) {
            admin.setPassword(ShaThree.encrypt(admin.getPassword()));
        }
        return service.updateById(admin) ? Results.ok().data("admin", admin) : Results.error();
    }

    @PassToken
    @Operation(summary = "查找用户回收站")
    @Cacheable(value = "recoveryUsers", unless = "#result==null")
    @GetMapping("/recovery")
    public Results findRecovery() {
        return Results.ok().data("recovery", service.showRecoveryAll());
    }

    @PassToken
    @Operation(summary = "查找全部文件")
    @Cacheable(value = "normalFiles", unless = "#result==null")
    @GetMapping("/files")
    public Results allFiles() {
        return Results.ok().data("files", service.showAllFiles());
    }

    @ConfirmToken
    @Operation(summary = "禁用/启用状态")
    @CacheEvict(value = {"searchUsers", "recoveryUsers", "normalUsers"}, allEntries = true)
    @PutMapping("/status")
    public Results changeStatus(@RequestBody UCenter user) {
        return service.changeStatus(user) ?
                Results.ok() : Results.error();
    }

    @ConfirmToken
    @Operation(summary = "模糊匹配登录名或者昵称")
    @Cacheable(cacheNames = "searchUsers", unless = "#result==null", key = "#uCenter")
    @PostMapping("/searchUsers")
    public Results searchUsers(@RequestBody UCenter uCenter) {
        return Results.ok().data("search", service.searchUsers(uCenter));
    }

    @ConfirmToken
    @Operation(summary = "模糊匹配文件名或者类型")
    @Cacheable(cacheNames = "searchFiles", unless = "#result==null", key = "#files")
    @PostMapping("/searchFiles")
    public Results searchFiles(@RequestBody Files files) {
        return Results.ok().data("search", service.searchFiles(files));
    }

    @ConfirmToken
    @Operation(summary = "用户加入回收站")
    @CacheEvict(value = {"searchUsers", "recoveryUsers", "normalUsers"}, allEntries = true)
    @PutMapping("/del")
    public Results logicDelUser(@RequestBody UCenter uCenter) {
        return service.logicDelUser(uCenter.getId()) > 0 ? Results.ok() : Results.error();
    }

    @ConfirmToken
    @Operation(summary = "用户移出回收站")
    @CacheEvict(value = {"searchUsers", "recoveryUsers", "normalUsers"}, allEntries = true)
    @PutMapping("/normal")
    public Results logicNormalUser(@RequestBody UCenter uCenter) {
        return service.logicNormalUser(uCenter.getId()) > 0 ? Results.ok() : Results.error();
    }

    @ConfirmToken
    @Operation(summary = "用户真实删除")
    @CacheEvict(value = {"searchUsers", "recoveryUsers", "normalUsers"}, allEntries = true)
    @DeleteMapping("/delUser")
    public Results delUser(@RequestBody String id) {
        return service.delUserById(id) > 0 ? Results.ok() : Results.error();
    }

    @ConfirmToken
    @Operation(summary = "文件真实删除")
    @CacheEvict(value = {"searchFiles", "normalFiles"}, allEntries = true)
    @DeleteMapping("/delFile")
    public Results delFiler(@RequestBody Files files) {
        return service.delFileByMd5(files);
    }

    @ConfirmToken
    @Operation(summary = "获取密码的md5值")
    @Cacheable("md5")
    @GetMapping("/md5/{password}")
    public Results getMd5(@PathVariable String password) {
        return Results.ok().data("md5", ShaThree.encrypt(password));
    }

}

