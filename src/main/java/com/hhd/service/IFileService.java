package com.hhd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhd.pojo.entity.Files;
import com.hhd.pojo.entity.UserDir;

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
    List<Files> showNormalAll(String userid);

    List<Files> showRecoveryAll();
    List<Files> getFileInfo(String id);

    /**
     * 获取当前目录下的所有文件
     *
     * @param userDir
     * @return return
     */
    List<Files> getCurFiles(UserDir userDir);

    Files getFiles(String id);

    List<Files> getFindFile(String userid, String name);

    List<Files> getList(String userid, String url, int result, String name);

    int logicDelFile(String id);
    int logicNormalFile(String id);
    List<Files> selectMd5File(String md5);

}
