package com.hhd.service;

import com.hhd.pojo.entity.File;
import com.baomidou.mybatisplus.extension.service.IService;
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
public interface IFileService extends IService<File> {
    List<File> getAllFile(String userid);

    List<File> getFileInfo(String id);

    /**
     * 获取当前目录下的所有文件
     * @param
     * @return
     */
    List<File> getCurFiles(UserDir userDir);

    File getFiles(String id);

    List<File> getFindFile(String userid, String name);

    List<File> getList(String userid, String url, int result, String name);
}
