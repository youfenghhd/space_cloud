package com.hhd.service;

import com.hhd.mapper.AdminMapper;
import com.hhd.pojo.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
