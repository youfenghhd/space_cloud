package com.hhd;

import com.hhd.service.IAdminService;
import com.hhd.service.IFileService;
import com.hhd.service.IUCenterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {
    @Test
    void contextLoads() {
    }

    @Autowired
    private IAdminService service;
    @Autowired
    private IUCenterService uService;
    @Autowired
    private IFileService fService;

    @Test
    public void test() {

    }
}
