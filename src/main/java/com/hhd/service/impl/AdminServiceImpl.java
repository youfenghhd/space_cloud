package com.hhd.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhd.mapper.AdminMapper;
import com.hhd.mapper.UcenterMapper;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.entity.Admin;
import com.hhd.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wiremock.org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UcenterMapper uMapper;


    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public int insert(Admin admin) {
        return adminMapper.insert(admin);
    }

    @Override
    public List<UCenter> showNormalAll() {
        return uMapper.selectList(null);
    }

    @Override
    public List<UCenter> showRecoveryAll() {
        DateTime nowTime = new DateTime();
        return uMapper.showRecoveryAll(simpleDateFormat.format(nowTime));
    }

    @Override
    public int changeStatus(UCenter uCenter) {
        LambdaUpdateWrapper<UCenter> lqw = new LambdaUpdateWrapper<>();
        lqw.set(UCenter::getStatus, uCenter.getStatus()).eq(UCenter::getId, uCenter.getId());
        return uMapper.update(new UCenter(), lqw);
    }

    @Override
    public int logicDelUser(String id) {
        LambdaUpdateWrapper<UCenter> lqw = new LambdaUpdateWrapper<>();
        return uMapper.update(new UCenter(),
                lqw.set(UCenter::getLogicDelTime, simpleDateFormat.format(DateUtils.addDays(new DateTime(), 30)))
                        .eq(UCenter::getId, id));
    }

    @Override
    public int logicNormalUser(String id) {
        return uMapper.logicNormalUser(id);
    }


}
