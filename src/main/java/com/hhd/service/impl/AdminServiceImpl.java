package com.hhd.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.mapper.AdminMapper;
import com.hhd.mapper.UcenterMapper;
import com.hhd.pojo.domain.Admin;
import com.hhd.pojo.domain.UCenter;
import com.hhd.service.IAdminService;
import com.hhd.utils.JwtUtils;
import com.hhd.utils.MD5;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import wiremock.org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private AdminMapper aMapper;
    @Autowired
    private UcenterMapper uMapper;

    @Autowired
    private RedisTemplate<String, String> template;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Map<String, Admin> login(Admin admin) {
        String loginName = admin.getLoginName();
        String password = admin.getPassword();
        String code = admin.getCode();
        if (loginName.isEmpty() || password.isEmpty()) {
            throw new CloudException(R.ERROR, R.EMPTY_ERROR);
        }
        LambdaUpdateWrapper<Admin> lqw = new LambdaUpdateWrapper<>();
        Admin exist = aMapper.selectOne(lqw.eq(Admin::getLoginName, loginName));
        if (exist == null) {
            throw new CloudException(R.ERROR, R.NON_REGISTER);
        }
        if (!MD5.encrypt(password).equals(exist.getPassword())) {
            throw new CloudException(R.ERROR, R.PASSWORD_ERR);
        }
        String code1 = template.opsForValue().get("checkCode");
        if (!code.equals(code1)) {
            throw new CloudException(R.ERROR, R.CHECK_ERROR);
        }
        String token = JwtUtils.getJwtToken(exist);
        Map<String, Admin> map = new HashMap<>(1);
        map.put(token, exist);
        return map;
    }

    @Override
    public int insert(Admin admin) {
        return aMapper.insert(admin);
    }

    @Override
    public Admin selectOne(String id) {
        LambdaUpdateWrapper<Admin> lqw = new LambdaUpdateWrapper<>();
        return aMapper.selectOne(lqw.eq(Admin::getAid,id));
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

    @Override
    public int delById(List<String> id) {
        return uMapper.delById(id);
    }


}
