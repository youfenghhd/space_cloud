package com.hhd.service;

import com.hhd.pojo.entity.UserDir;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
public interface IUserDirService extends IService<UserDir> {
    /**
     * 获取用户的文件夹
     *
     * @param id 根据用户id查找
     * @return 查询结果
     */
    UserDir getUserDir(String id);

    /**
     * 创建用户文件夹
     *
     * @param userDir 根据文件夹对象实例
     * @return 创建结果
     */
    int setUserDir(UserDir userDir);

    /**
     * 删除用户文件夹
     *
     * @param userid 根据用户id
     * @param url    父文件夹地址
     * @return 删除结果
     */
    boolean deleteStruct(String userid, String url);

}
