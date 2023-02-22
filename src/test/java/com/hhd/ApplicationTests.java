package com.hhd;

import com.hhd.service.IAdminService;
import com.hhd.service.IUCenterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ApplicationTests {
    @Test
    void contextLoads() {
    }

    @Autowired
    private IAdminService service;
    @Autowired
    private IUCenterService uService;

    @Test
    public void test() {
        List<String> a =  new ArrayList<>();
        a.add("5");
        a.add("6");
        a.add("7");
        System.out.println(a);
        service.delById(a);
    }
}
