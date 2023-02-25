package com.hhd;

import com.hhd.pojo.domain.UCenter;
import com.hhd.service.IAdminService;
import com.hhd.service.IFileService;
import com.hhd.service.IUCenterService;
import com.hhd.utils.JwtUtils;
import com.hhd.utils.UserLoginToken;
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
    @UserLoginToken
    public void test() {
        UCenter u = new UCenter();
        u.setId("123");
        u.setPassword("123456");
        JwtUtils.getJwtToken(u);
//        System.out.println(TokenUtil.getTokenUserId());
        System.out.println(TokenUtil.getRequest());
    }
}
