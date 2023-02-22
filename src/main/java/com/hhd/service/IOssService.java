package com.hhd.service;

import com.hhd.pojo.entity.Files;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author -无心
 * @date 2023/2/18 22:17:39
 */
public interface IOssService {

    /**
     *  上传
     * @param file 文件
     * @param files 载入史册！！！
     * @return <String url ,FIle 头像>
     */
    Map<String, Files> upload(MultipartFile file, Files files);

    /**
     * 上传视频点播
     * @param file vedio and audio
     * @return
     */
    Files uploadVideo(MultipartFile file,Files files);

}
