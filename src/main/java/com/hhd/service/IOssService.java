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
     *  上传
     * @param file 文件
     * @param files 载入史册！！！
     * @return <String url ,FIle 头像>
     */
    Map<String, Files> upload(MultipartFile file, Files files);

    /**
     * 上传视频点播
     * @param file vedio and audio
     * @param files 你管那么多干嘛
     * @return
     */
    Files uploadVideo(MultipartFile file,Files files);

    /**
     * 删东西啊还能干嘛
     * @param id 文件id
     * @return 成功/失败
     */
    R delete(String id);

    /**
     * 删点播呗
     * @param id videoId
     * @return 成功/失败
     */
    R deleteVa(String id);

    /**
     * 断点续传下载
     * @param id 根据文件下载
     * @return 返回状态
     */
    R downLoad(String id);
}
