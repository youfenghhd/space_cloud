package com.hhd.controller;


import com.hhd.pojo.entity.Admin;
import com.hhd.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
public class AdminController {


    @Autowired
    private IAdminService service;


    @PostMapping("/save")
    public String instr() {
        Admin admin = new Admin();
        admin.setAdminName("罗苡雪");
        admin.setLoginName("lyx");
        admin.setPassword("e10adc3949ba59abbe56e057f20f883e");
        System.out.println(admin);
        return service.insert(admin) > 0 ? "sucess" : "sdjha";
    }

}

