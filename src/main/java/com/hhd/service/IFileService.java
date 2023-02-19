package com.hhd.service;

import com.hhd.pojo.entity.File;
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
public interface IFileService extends IService<File> {
    List<File> getAllFileInfo(String userid);

    List<File> getFileInfo(String id);

    /**
     * 获取当前目录下的所有文件
     * @param
     * @return
     */
    List<File> getCurFiles(String userDir, String id);

    File getFiles(String id);

    List<File> getFindFile(String userid, String name);

    List<File> getList(String userid, String url, int result, String name);
}
