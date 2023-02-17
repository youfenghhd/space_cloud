package com.hhd.service;

import com.hhd.utils.R;

import java.util.concurrent.ExecutionException;

/**
 * @author -无心
 * @date 2023/2/17 16:25:47
 */
public interface SmsService {


    /**
     * 手机验证码接口
     *
     * @return 获取验证码结果
     */
    R getSmsCode(String tel) ;

}
