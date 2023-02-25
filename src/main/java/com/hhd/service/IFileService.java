package com.hhd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhd.pojo.entity.Files;

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
     * 查询所有正常的文件
     *
     * @param userid 根据用户查询
     * @return 差距结果
     */
    List<Files> showNormalAll(String userid);

    List<Files> showRecoveryAll(String userid);

    List<Files> getFileInfo(String id);

    /**
     * 获取当前目录下的所有文件
     *
     * @param userDir
     * @return return
     */
    List<Files> getCurFiles(String userDir, String id);

    Files getFiles(String id);

    List<Files> getFindFile(String userid, String name);

    List<Files> getList(String userid, String url, int result, String name);

    int logicDelFile(String id);

    int logicNormalFile(String id);

    List<Files> selectMd5File(String md5);

    Files selectOne(String fileId);

    /**
     * 真删除
     *
     * @param id 删除文件的id
     */
    void delById(String id);

    List<Files> findVideo();

    List<Files> findAudio();

    List<Files> findImage();

    List<Files> findOther();

}
