package com.hhd.service;

/**
 * @author -无心
 * @date 2023/2/17 16:25:47
 */
public interface ISmsService {


    /**
     * 手机验证码接口
     *
     * @return 获取验证码结果
     */
    boolean getSmsCode(String tel);

}
