package com.hhd.service;

import com.hhd.pojo.entity.Files;
import com.hhd.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author -无心
 * @date 2023/2/18 22:17:39
 */
public interface IOssService {

    /**
     * 上传
     *
     * @param file  image,file等
     * @param files 自定义入库的实例
     * @return <String url ,FIle 头像>
     */
    Map<String, Files> upload(MultipartFile file, Files files);

    /**
     * 上传视频点播
     *
     * @param file  video and audio
     * @param files 自定义入库的实例
     * @return 返回入库结果
     */
    Files uploadVideo(MultipartFile file, Files files);

    /**
     * 删oss
     *
     * @param id 文件id
     * @return 成功/失败
     */
    R delete(String id);

    /**
     * 删vod点播顺便删oss
     *
     * @param id videoId
     * @return 成功/失败
     */
    R deleteVa(String id);

    /**
     * 断点续传下载
     *
     * @param id 根据文件下载
     */
    void downLoad(String id);
}
