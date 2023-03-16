package com.hhd.controller;


import com.hhd.service.ICkCodeService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author -无心
 * @date 2023/2/19 1:04:49
 */
@RestController
@RequestMapping("/checks")
@CrossOrigin
@Api(tags = "图片验证码处理")
public class CheckCodeController {
    @Autowired
    private ICkCodeService service;

    @Operation(summary = "获取图片验证码")
    @GetMapping
    public byte[] generate() {
        return service.generate();
    }
}
