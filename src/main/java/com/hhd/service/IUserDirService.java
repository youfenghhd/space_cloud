package com.hhd.service;

import com.hhd.pojo.entity.UserDir;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
public interface IUserDirService extends IService<UserDir> {
    UserDir getUserDir(String id);

    int setUserDir(UserDir userDir);

    boolean deleteStruct(String userid,String url);

}
