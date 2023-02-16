package com.hhd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.pojo.Ucenter;
import com.hhd.mapper.UcenterMapper;
import com.hhd.pojo.register;
import com.hhd.service.IUcenterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhd.utils.MD5;
import com.hhd.utils.R;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@Service
public class UcenterServiceImpl extends ServiceImpl<UcenterMapper, Ucenter> implements IUcenterService {

    @Autowired
    private UcenterMapper uMapper;

    @Override
    public int insert(Ucenter ucenter) {
        return uMapper.insert(ucenter);
    }

    @Override
    public void register(register rg) {
        String mobile = rg.getMobile();
        String password = rg.getPassword();
        String nickname = rg.getNickname();
        String portrait = rg.getPortrait();
        String ckCode = rg.getCheckCOde();
        String smsCode = rg.getSmsCode();

        if (mobile.isEmpty() || password.isEmpty()
                || nickname.isEmpty() || ckCode.isEmpty()
                ||smsCode.isEmpty()) {
                throw new CloudException(R.ERROR,R.EMPTY_ERROR);
        }

        //模拟获取图片验证码
        String code1 = "";
        if(!ckCode.equals(code1)){
            throw new CloudException(R.ERROR,R.CHECK_ERROR);
        }


        //模拟获取短信验证码
        String code2 = "";
        if (!smsCode.equals(code2)){
            throw new CloudException(R.ERROR,R.CHECK_ERROR);
        }

        LambdaQueryWrapper<Ucenter> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Ucenter::getMobile,rg.getMobile());
        if(uMapper.selectCount(lqw)>0){
            throw new CloudException(R.ERROR,R.PHONE_EXIST);
        }

        Ucenter ucenter = new Ucenter();
        ucenter.setMobile(mobile);
        ucenter.setPassword(MD5.encrypt(password));
        ucenter.setNickname(nickname);
        ucenter.setPortrait(portrait);

    }

}
