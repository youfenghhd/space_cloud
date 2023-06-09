package com.hhd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.vo.Register;
import com.hhd.utils.Results;

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
    Results register(Register register);

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

    /**
     * 查找手机有无被注册
     *
     * @param mobile 手机号
     * @return R
     */
    Results selectOneByMobile(String mobile);


    /**
     * 用户升级会员
     *
     * @param uCenter 根据用户id修改会员信息
     * @param month   升级会员月份
     * @return 结果
     */
    Results upToVip(UCenter uCenter, int month);


    /**
     * 删除过期会员
     *
     * @param nonVipUser 根据传入的用户信息判断是否会员过期修改
     */
    void removeExpirationVip(UCenter nonVipUser);

}
