package com.hhd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.vo.Register;
import com.hhd.utils.R;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
public interface IUCenterService extends IService<UCenter> {
    /**
     * 注册方法
     *
     * @param register 注册VO
     * @return 返回注册是否成功
     */
    R register(Register register);

    /**
     * 登陆方法
     *
     * @param center 登录用户信息
     * @return String:token center:查询到的信息
     */
    Map<String, UCenter> login(UCenter center);

    /**
     * 查找用户
     *
     * @param userId 根据id查找
     * @return 查找结果
     */
    UCenter selectOne(String userId);

}
