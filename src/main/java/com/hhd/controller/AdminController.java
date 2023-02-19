package com.hhd.controller;


import com.hhd.pojo.domain.UCenter;
import com.hhd.service.IAdminService;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
//    @PostMapping("/save")
//    public String instr() {
//        Admin admin = new Admin();
//        admin.setAdminName("罗苡雪");
//        admin.setLoginName("lyx");
//        admin.setPassword("e10adc3949ba59abbe56e057f20f883e");
//        System.out.println(admin);
//        return service.insert(admin) > 0 ? "success" : "error";
//    }

    @GetMapping
    public R findNormal() {
        return R.ok().data("normal", service.showNormalAll());
    }

    @GetMapping("/recovery")
    public R findRecovery() {
        return R.ok().data("recovery", service.showRecoveryAll());
    }

    @PutMapping("/status")
    public R changeStatus(@RequestBody UCenter user) {
        return service.changeStatus(user) > 0 ?
                R.ok() : R.error();
    }

    @PutMapping("/del")
    public R logicDelUser(@RequestBody String id) {
        return service.logicDelUser(id) > 0 ? R.ok() : R.error();
    }

    @PutMapping("/normal")
    public R logicNormalUser(@RequestBody String id) {
        return service.logicNormalUser(id) > 0 ? R.ok() : R.error();
    }
}

