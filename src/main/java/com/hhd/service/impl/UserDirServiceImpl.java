package com.hhd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhd.mapper.UserDirMapper;
import com.hhd.pojo.entity.UserDir;
import com.hhd.service.IFileService;
import com.hhd.service.IUserDirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@Service
public class UserDirServiceImpl extends ServiceImpl<UserDirMapper, UserDir> implements IUserDirService {

    @Autowired
    private IFileService service;

    @Override
    public UserDir getUserDir(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    public int setUserDir(UserDir userDir) {
        return baseMapper.updateById(userDir);
    }

    @Override
    public boolean deleteStruct(String userid, String url) {
        return service.logicDirFile(userid, url);
    }
}
