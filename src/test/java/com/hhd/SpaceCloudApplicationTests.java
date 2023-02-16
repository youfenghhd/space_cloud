package com.hhd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hhd.mapper.UcenterMapper;
import com.hhd.pojo.Ucenter;
import com.hhd.pojo.register;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpaceCloudApplicationTests {
    @Test
    void contextLoads() {
    }
    @Autowired
    private UcenterMapper uMapper;

    private register rg;
    @Test
    void testinsert(){
        Ucenter u = new Ucenter();
        u.setPassword("122345");
        u.setMobile("125632131");
        u.setPortrait("dhashdjs");
        u.setNickname("ceshi");
        uMapper.insert(u);
    }
}
