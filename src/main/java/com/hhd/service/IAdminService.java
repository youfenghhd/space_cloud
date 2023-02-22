package com.hhd.service;

import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
     * 创建一个管理员
     *
     * @param admin
     * @return
     */
    int insert(Admin admin);

    /**
     * 管理员查询所有用户
     *
     * @return
     */
    List<UCenter> showNormalAll();

    /**
     * 查询回收站
     *
     * @return 查询结果
     */
    List<UCenter> showRecoveryAll();

    /**
     * 管理员修改用户状态
     *
     * @param uCenter:根据id修改状态
     * @return
     */
    int changeStatus(UCenter uCenter);

    /**
     * 根据id讲用户逻辑删除
     *
     * @param id ：传入的id
     * @return
     */
    int logicDelUser(String id);

    /**
     * 根据传入id将用户从逻辑删除恢复正常状态
     *
     * @param id 传入的id
     * @return
     */
    int logicNormalUser(String id);

    /**
     * 删除
     * @param id
     * @return
     */
    int delById(List<String> id);
}
