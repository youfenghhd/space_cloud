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
class SpaceCloudApplicationTests {
    @Test
    void contextLoads() {
    }

//    @Autowired
//    private UcenterMapper uMapper;
//
//    @Test
//    public void findAll() {
//        List<UCenter> uCenters = uMapper.selectList(null);
//        uCenters.forEach(System.out::println);
//    }

//    @Test
////    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
////    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
//    public void testlogic() {
//        String id = "3";
//        Date dateTime= DateUtils.addDays(new DateTime(),30);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String format = simpleDateFormat.format(dateTime);
//        LambdaUpdateWrapper<UCenter> lqw = new LambdaUpdateWrapper<>();
//        System.out.println(uMapper.update(new UCenter(),lqw.set(UCenter::getLogicDelTime, format).eq(UCenter::getId, id)));
//    }
//
//    @Test
//    public void TestLogicNo(){
//        String id = "3";
//       uMapper.logicNormalUser(id);
//    }

    @Autowired
    private IAdminService service;

    @Test
    public void test() {
//        service.logicDelUser("3");
//        service.logicNormalUser("3");
        service.changeStatus("1626999701099225090","1");
//        service.showNormalAll();
//        service.showRecoveryAll();
//        service.logicNormalUser("2");
//        service.logicNormalUser("3");
//        service.logicDelUser("3");



    }
}
