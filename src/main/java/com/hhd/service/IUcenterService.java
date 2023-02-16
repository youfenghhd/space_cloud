package com.hhd.service;

import com.hhd.pojo.Ucenter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hhd.pojo.register;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
public interface IUcenterService extends IService<Ucenter> {
    /**
     * 插入方法
     * @param ucenter
     * @return
     */
    int insert(Ucenter ucenter);

    /**
     * 注册方法
     */
    void register(register register);

}
