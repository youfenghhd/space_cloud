package com.hhd.service;

import com.hhd.pojo.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */

public interface IAdminService extends IService<Admin> {

    /**
     * 创建一个管理员
     * @param admin
     * @return
     */
    int insert(Admin admin);

}
