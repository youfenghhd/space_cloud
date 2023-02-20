package com.hhd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.mapper.UcenterMapper;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.vo.Register;
import com.hhd.service.IUCenterService;
import com.hhd.utils.MD5;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
        if (!ckCode.equals(code)) {
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
        lqw.eq(UCenter::getMobile, mobile)
                .eq(UCenter::getStatus, true);
        UCenter exist = uMapper.selectOne(lqw);
        System.out.println(exist);
        if (exist == null) {
            throw new CloudException(R.ERROR, R.PHONE_NON_EXIST);
        }

        if (!exist.getStatus()) {
            throw new CloudException(R.ERROR, R.DISABLE_ERR);
        }

        if (!MD5.encrypt(password).equals(exist.getPassword())) {
            throw new CloudException(R.ERROR, R.PASSWORD_ERR);
        }

        String code1 = redisTemplate.opsForValue().get("checkCode");
        if (!code.equals(code1)) {
            throw new CloudException(R.ERROR, R.CHECK_ERROR);
        }
        Map<String, UCenter> map = new HashMap<>(1);
        map.put("String", exist);
        return map;
    }


}
