package com.hhd.service.impl;

import com.hhd.pojo.entity.Admin;
import com.hhd.mapper.AdminMapper;
import com.hhd.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Override
    public int insert(Admin admin) {
        return adminMapper.insert(admin);
    }
}
