package com.hhd.service.impl;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.mapper.FileMapper;
import com.hhd.pojo.entity.Files;
import com.hhd.service.IFileService;
import com.hhd.utils.InitOssClient;
import com.hhd.utils.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wiremock.org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.hhd.utils.InitVodClient.initVodClient;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, Files> implements IFileService {

    private static final ThreadLocal<DateFormat> THREAD_LOCAL = ThreadLocal.withInitial(() ->
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    private static final String VIDEO = "video";
    private static final String AUDIO = "audio";
    @Autowired
    private FileMapper fMapper;

    @Override
    public List<Files> showNormalAll(String userid) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return baseMapper.selectList(lqw.eq(Files::getUserId, userid));
    }

    @Override
    public List<Files> showRecoveryAll(String userid) {
        DateTime nowTime = new DateTime();
        return fMapper.showRecoveryAll(THREAD_LOCAL.get().format(nowTime), userid);
    }

    @Override
    public List<Files> getCurFiles(String userDir, String id) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return baseMapper.selectList(lqw.eq(Files::getFileDir, userDir)
                .eq(Files::getUserId, id));
    }

    @Override
    public List<Files> getFindFile(String userid, String name) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return baseMapper.selectList(lqw.eq(Files::getUserId, userid).like(Files::getFileName, name));
    }

    @Override
    public List<Files> getList(String userId, String url, int result, String name) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        List<Files> files = baseMapper.selectList(lqw.eq(Files::getUserId, userId).like(Files::getFileDir, url));
        for (Files file : files) {
            String fileDir = file.getFileDir();
            String[] split = fileDir.split("/", -1);
            split[result] = name;

            StringBuilder builder = new StringBuilder();

            for (int i = 1; i < split.length; i++) {
                builder.append("/").append(split[i]);
            }
            file.setFileDir(builder.toString());
        }
        return files;
    }

    @Override
    public void logicDelFile(String id) {
        LambdaUpdateWrapper<Files> lqw = new LambdaUpdateWrapper<>();
        baseMapper.update(new Files(), lqw.set(Files::getLogicDelTime, THREAD_LOCAL.get().format(DateUtils.addDays(new DateTime(), 30)))
                .eq(Files::getId, id));
    }

    @Override
    public boolean logicDirFile(String userId, String url) {
        LambdaUpdateWrapper<Files> lqw = new LambdaUpdateWrapper<>();
        return baseMapper.update(new Files(), lqw.set(Files::getLogicDelTime, THREAD_LOCAL.get().format(DateUtils.addDays(new DateTime(), 30)))
                .eq(Files::getUserId, userId).eq(Files::getFileDir, url)) > 0;
    }

    @Override
    public Results logicNormalFile(String id) {
        return fMapper.logicNormalFile(id) > 0 ? Results.ok() : Results.error();
    }

    @Override
    public List<Files> selectMd5File(String md5) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectList(lqw.eq(Files::getMd5, md5));
    }

    @Override
    public boolean selectMd5OfUser(String md5, String userId) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectCount(lqw.eq(Files::getUserId, userId).eq(Files::getMd5, md5)) > 0;
    }

    @Override
    public Files selectOne(String fileId) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectOne(lqw.eq(Files::getId, fileId));
    }

    @Override
    public Results delById(String id) {
        Results results = Results.ok();
        Files files = fMapper.getOne(id);
        fMapper.delById(id);
        if (fMapper.getCount(files.getMd5()) != 0L) {
            return Results.ok();
        }
        String videoId = files.getVideoId();
        String createTime = new DateTime(files.getCreateTime()).toDateStr();
        String fileName = files.getFileName();
        String type = files.getType();
        if (VIDEO.equals(files.getFileType()) || AUDIO.equals(files.getFileType())) {
            try {
                //初始化对象
                DefaultAcsClient client = initVodClient();
                //创建删除视频request对象
                DeleteVideoRequest request = new DeleteVideoRequest();
                //向request设置视频id
                request.setVideoIds(videoId);
                //调用初始化对象的方法实现删除
                client.getAcsResponse(request);
            } catch (Exception e) {
                throw new CloudException(Results.ERROR, Results.DELETE_VA_ERR);
            }
        } else {
            OSS ossClient = new OSSClientBuilder().build(InitOssClient.END_POINT,
                    InitOssClient.ACCESS_KEY_ID, InitOssClient.ACCESS_KEY_SECRET);
            try {
                String fileKey = createTime.substring(0, 10) + "/" + fileName + "." + type;
                ossClient.deleteObject(InitOssClient.BUCKET_NAME, fileKey);
            } catch (Exception e) {
                return Results.error();
            } finally {
                ossClient.shutdown();
            }
        }
        return results;
    }
}
