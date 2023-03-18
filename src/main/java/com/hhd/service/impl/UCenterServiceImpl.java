package com.hhd.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.mapper.UcenterMapper;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.vo.Register;
import com.hhd.service.IUCenterService;
import com.hhd.utils.JwtUtils;
import com.hhd.utils.MD5;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import wiremock.org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
public class UCenterServiceImpl extends ServiceImpl<UcenterMapper, UCenter> implements IUCenterService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UcenterMapper uMapper;

    private static final ThreadLocal<DateFormat> THREAD_LOCAL = ThreadLocal.withInitial(() ->
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    @Override
    public R register(Register register) {
        String mobile = register.getMobile();
        String password = register.getPassword();
        String nickname = register.getNickname();
        String portrait = register.getPortrait();
        String ckCode = register.getCheckCode();
        String smsCode = register.getSmsCode();

        //手机号、密码、昵称、验证码不能为空
        if (mobile.isEmpty() || password.isEmpty()
                || nickname.isEmpty() || ckCode.isEmpty()
                || smsCode.isEmpty()) {
            throw new CloudException(R.ERROR, R.EMPTY_ERROR);
        }

        //redis获取图片验证码判断
        String code = redisTemplate.opsForValue().get("checkCode");
        if (!ckCode.equalsIgnoreCase(code)) {
            throw new CloudException(R.ERROR, R.CHECK_ERROR);
        }
        //redis获取短信验证码判断
        String code2 = redisTemplate.opsForValue().get(mobile);
        System.out.println(smsCode);
        System.out.println(code2);
        if (!smsCode.equals(code2)) {
            throw new CloudException(R.ERROR, R.SMS_ERR);
        }

        LambdaQueryWrapper<UCenter> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UCenter::getMobile, register.getMobile());
        if (uMapper.selectCount(lqw) > 0) {
            throw new CloudException(R.ERROR, R.PHONE_EXIST);
        }

        UCenter ucenter = new UCenter();
        ucenter.setMobile(mobile);
        ucenter.setPassword(MD5.encrypt(password));
        ucenter.setNickname(nickname);
        ucenter.setPortrait(portrait);
        ucenter.setMemory(0L);
        return uMapper.insert(ucenter) > 0 ? R.ok() : R.error();
    }

    @Override
    public Map<String, UCenter> login(UCenter ucenter) {
        String mobile = ucenter.getMobile();
        String password = ucenter.getPassword();
        String code = ucenter.getCode();
        if (mobile.isEmpty() || password.isEmpty()) {
            throw new CloudException(R.ERROR, R.EMPTY_ERROR);
        }

        LambdaQueryWrapper<UCenter> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UCenter::getMobile, mobile);
        UCenter exist = uMapper.selectOne(lqw);
        if (exist == null) {
            throw new CloudException(R.ERROR, R.NON_REGISTER);
        }
        if (!exist.getStatus()) {
            throw new CloudException(R.ERROR, R.DISABLE_ERR);
        }
        if (!MD5.encrypt(password).equals(exist.getPassword())) {
            throw new CloudException(R.ERROR, R.PASSWORD_ERR);
        }
        String code1 = redisTemplate.opsForValue().get("checkCode");
        if (!code.equalsIgnoreCase(code1)) {
            throw new CloudException(R.ERROR, R.CHECK_ERROR);
        }
        String token = JwtUtils.getJwtToken(exist);
        Map<String, UCenter> map = new HashMap<>(1);
        map.put(token, exist);
        return map;
    }

    @Override
    public UCenter selectOne(String userId) {
        LambdaQueryWrapper<UCenter> lqw = new LambdaQueryWrapper<>();
        return uMapper.selectOne(lqw.eq(UCenter::getId, userId));
    }

    @Override
    public R selectOneByMobile(String mobile) {
        LambdaQueryWrapper<UCenter> lqw = new LambdaQueryWrapper<>();
        return uMapper.selectOne(lqw.eq(UCenter::getMobile, mobile)) != null ? R.ok() : R.error();
    }

    @Override
    public R upToVip(UCenter uCenter, int month) {
        LambdaUpdateWrapper<UCenter> lqw = new LambdaUpdateWrapper<>();
        DateTime newDate;
        try {
            if (uCenter.getVipTime() == null) {
                newDate = new DateTime();
            } else {
                newDate = new DateTime(THREAD_LOCAL.get().parse(uCenter.getVipTime()));
            }
            String newVipTime = THREAD_LOCAL.get().format(DateUtils.addMonths(newDate, month));
            uCenter.setVipTime(newVipTime);
            return uMapper.update(new UCenter(), lqw.set(UCenter::getVipTime, newVipTime)
                    .eq(UCenter::getId, uCenter.getId())) > 0 ? R.ok().data("user", uCenter) : R.error();
        } catch (Exception e) {
            throw new CloudException(R.ERROR, R.GLOBAL_ERR);
        }
    }

    @Override
    public void removeExpirationVip(UCenter isOrNonVipUser) {
        if (isOrNonVipUser.getVipTime() == null) {
            return;
        }
        if (new DateTime(isOrNonVipUser.getVipTime()).isBeforeOrEquals(new DateTime())) {
            uMapper.removeVip(isOrNonVipUser.getId());
        }
    }
}
