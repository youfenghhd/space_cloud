package com.hhd;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hhd.mapper.UcenterMapper;
import com.hhd.pojo.domain.UCenter;
import com.hhd.service.IAdminService;
import com.hhd.service.IUCenterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wiremock.org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootTest
class ApplicationTests {
    @Test
    void contextLoads() {
    }

    @Autowired
    private IAdminService service;

    @Test
    public void test() {

    }
}
