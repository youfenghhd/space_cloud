package com.hhd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhd.pojo.entity.Files;
import com.hhd.utils.R;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
public interface IFileService extends IService<Files> {
    /**
     * 查询当前用户所有正常的文件
     *
     * @param userid 根据用户id查询
     * @return 查询结果
     */
    List<Files> showNormalAll(String userid);

    /**
     * 查询当前用户回收站文件
     *
     * @param userid 根据用户id查询
     * @return 查询结果
     */
    List<Files> showRecoveryAll(String userid);


    /**
     * 获取当前目录下的所有文件
     *
     * @param id      根据用户id
     * @param userDir 根据用户目录
     * @return return 返回所有文件结果
     */
    List<Files> getCurFiles(String userDir, String id);

    /**
     * 查询用户id下的模糊文件
     *
     * @param userid 根据用户id查询
     * @param name   文件模糊名
     * @return 查询结果集合
     */
    List<Files> getFindFile(String userid, String name);

    /**
     * @param userId
     * @param url
     * @param result
     * @param name
     * @return
     */
    List<Files> getList(String userId, String url, int result, String name);

    /**
     * 逻辑删除
     *
     * @param id 根据文件id删除
     */
    void logicDelFile(String id);

    /**
     * 逻辑删除文件夹附带的文件
     *
     * @param userId 根据用户
     * @param url    文件夹路径
     * @return 删除成功
     */
    boolean logicDirFile(String userId, String url);

    /**
     * 逻辑删除恢复正常
     *
     * @param id 根据id
     * @return 结果
     */
    R logicNormalFile(String id);

    /**
     * 查找相同的md5值的文件
     *
     * @param md5 字面意思
     * @return 结果集合
     */
    List<Files> selectMd5File(String md5);

    /**
     * 查找用户是否重复上传
     *
     * @param md5    字面意思
     * @param userId 字面意思
     * @return 结果
     */
    boolean selectMd5OfUser(String md5, String userId);

    /**
     * 根据文件id查询文件详情或是否存在
     *
     * @param fileId 文件id
     * @return 查询结果
     */
    Files selectOne(String fileId);

    /**
     * 真删除
     *
     * @param id 删除文件的id
     * @return 成功与否
     */
    R delById(String id);

}
