package com.hhd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhd.pojo.domain.Admin;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.entity.Files;
import com.hhd.utils.Results;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */

public interface IAdminService extends IService<Admin> {
    /**
     * 管理员登录
     *
     * @param admin 管理员身份验证
     * @return String:token center:查询到的信息
     */
    Map<String, Admin> login(Admin admin);

    /**
     * 创建一个管理员
     *
     * @param admin 创建用户
     * @return 创建结果
     */
    int insert(Admin admin);

    /**
     * 请求用户信息
     *
     * @param id 根据id请求
     * @return 返回信息
     */
    Admin selectOne(String id);


    /**
     * 管理员查询所有用户
     *
     * @return 查询结果列表
     */
    List<UCenter> showNormalAll();

    /**
     * 查询回收站
     *
     * @return 查询结果
     */
    List<UCenter> showRecoveryAll();

    /**
     * 管理员查询所有文件
     *
     * @return 查询结果
     */
    List<Files> showAllFiles();

    /**
     * 管理员修改用户状态
     *
     * @param uCenter:根据id修改状态
     * @return 成功/失败
     */
    boolean changeStatus(UCenter uCenter);

    /**
     * 根据id将用户逻辑删除
     *
     * @param id ：传入的用户id
     * @return 成功/失败
     */
    int logicDelUser(String id);

    /**
     * 根据传入id将用户从逻辑删除恢复正常状态
     *
     * @param id 传入的用户id
     * @return 成功/失败
     */
    int logicNormalUser(String id);

    /**
     * 真实删除用户
     *
     * @param id 传入的id
     * @return 成功/失败
     */
    int delUserById(String id);

    /**
     * 真实删除文件
     *
     * @param files 传入的文件md5值
     * @return 成功/失败
     */
    Results delFileByMd5(Files files);

    /**
     * 模糊匹配用户
     *
     * @param uCenter 匹配项
     * @return 结果
     */
    List<UCenter> searchUsers(UCenter uCenter);

    /**
     * 模糊匹配文件
     *
     * @param files 匹配项
     * @return 结果
     */
    List<Files> searchFiles(Files files);
}
