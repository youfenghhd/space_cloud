package com.hhd.controller;


import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.vo.Register;
import com.hhd.service.IUCenterService;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/ucenter")
public class UCenterController {
//    @Value("${aliyun.accessKeyId}")
//    private String accid;
//    @Value("${aliyun.accessKeySecret}")
//    private String accp;
    @Autowired
    private IUCenterService service;

    @PostMapping("/login")
    public R login(@RequestBody UCenter ucenter) {
        Map.Entry<String, UCenter> entry = service.login(ucenter).entrySet().iterator().next();
        String token = entry.getKey();
        UCenter user = entry.getValue();
        return R.ok().data("token", token).data("user", user);

    }

    @PostMapping("/register")
    public R register(@RequestBody Register register) {
        service.register(register);
        return R.ok();
    }

//    @GetMapping("/test")
//    public R test() {
//        System.out.println(accid);
//        System.out.println(accp);
//        return R.ok();
//    }

}

