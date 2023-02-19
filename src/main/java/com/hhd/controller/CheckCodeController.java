package com.hhd.controller;


import com.hhd.service.ICkCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author -无心
 * @date 2023/2/19 1:04:49
 */
@RestController
@RequestMapping("/checks")
public class CheckCodeController {

    @Autowired
    private ICkCodeService service;

    @PutMapping
    public String generate(){
        return service.generate();
    }
}
