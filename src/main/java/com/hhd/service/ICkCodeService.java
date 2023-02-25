package com.hhd.service;

/**
 * @author -无心
 * @date 2023/2/19 0:35:04
 */
public interface ICkCodeService {
    /**
     * 获取图片验证码
     * @return 验证码字符串答案
     */
    String generate();
}
