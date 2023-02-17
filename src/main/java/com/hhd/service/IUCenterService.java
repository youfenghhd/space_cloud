package com.hhd.service;

import com.hhd.pojo.domain.UCenter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hhd.pojo.vo.Register;

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
     * 插入方法
     *
     * @param ucenter 插入的信息
     * @return 1/0插入成功与否
     */
    int insert(UCenter ucenter);

    /**
     * 注册方法
     * @param register 注册VO
     */
    void register(Register register);

    /**
     * 登陆方法
     * @param center 登录用户信息
     * @return String:token center:查询到的信息
     */
    Map<String, UCenter> login(UCenter center);


}
