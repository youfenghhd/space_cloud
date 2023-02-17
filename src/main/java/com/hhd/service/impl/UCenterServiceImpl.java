package com.hhd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.pojo.domain.UCenter;
import com.hhd.mapper.UcenterMapper;
import com.hhd.pojo.vo.Register;
import com.hhd.service.IUCenterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhd.utils.CheckCodeUtil;
import com.hhd.utils.MD5;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UcenterMapper uMapper;

    @Override
    public int insert(UCenter ucenter) {
        return uMapper.insert(ucenter);
    }


    @Override
    public void register(Register register) {
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

        //验证码生成，比对注册者输入的验证码
        try {
            String generate = CheckCodeUtil.generateJpg();
            if (!ckCode.equals(generate)) {
                throw new CloudException(R.ERROR, R.CHECK_ERROR);
            }
        } catch (Exception e) {
            throw new CloudException(R.ERROR, R.CHECK_IO);
        }


        //模拟获取短信验证码
        String code2 = CheckCodeUtil.generateTel(register.getMobile());
        if (!smsCode.equals(code2)) {
            throw new CloudException(R.ERROR, R.CHECK_ERROR);
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
        uMapper.insert(ucenter);
    }

    @Override
    public Map<String, UCenter> login(UCenter ucenter) {
        String mobile = ucenter.getMobile();
        String password = ucenter.getPassword();
        if (mobile.isEmpty() || password.isEmpty()) {
            throw new CloudException(R.ERROR, R.EMPTY_ERROR);
        }
        LambdaQueryWrapper<UCenter> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UCenter::getMobile, mobile);
        UCenter exist = uMapper.selectOne(lqw);
        System.out.println(exist);
        if (exist == null) {
            throw new CloudException(R.ERROR, R.PHONE_NON_EXIST);
        }

        if (!MD5.encrypt(password).equals(exist.getPassword())) {
            throw new CloudException(R.ERROR,R.PASSWORD_ERR);
        }
        Map<String, UCenter> map = new HashMap<>(1);
        map.put("String",exist);
        return map;
    }


}
