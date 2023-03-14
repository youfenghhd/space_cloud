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
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wiremock.org.apache.commons.lang3.time.DateUtils;

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


    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String VIDEO = "video";
    private final String AUDIO = "audio";
    private final String IMAGE = "image";
    private final String OTHER = "file";

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
        return fMapper.showRecoveryAll(simpleDateFormat.format(nowTime), userid);
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
            System.out.println(builder);
            file.setFileDir(builder.toString());
        }
        System.out.println(files);

        return files;
    }

    @Override
    public void logicDelFile(String id) {
        LambdaUpdateWrapper<Files> lqw = new LambdaUpdateWrapper<>();
        baseMapper.update(new Files(), lqw.set(Files::getLogicDelTime, simpleDateFormat.format(DateUtils.addDays(new DateTime(), 30)))
                .eq(Files::getId, id));
    }

    @Override
    public boolean logicDirFile(String userId, String url) {
        LambdaUpdateWrapper<Files> lqw = new LambdaUpdateWrapper<>();
        return baseMapper.update(new Files(), lqw.set(Files::getLogicDelTime, simpleDateFormat.format(DateUtils.addDays(new DateTime(), 30)))
                .eq(Files::getUserId, userId).eq(Files::getFileDir, url)) > 0;
    }

    @Override
    public R logicNormalFile(String id) {
        return fMapper.logicNormalFile(id)>0?R.ok():R.error();
    }

    @Override
    public List<Files> selectMd5File(String md5) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectList(lqw.eq(Files::getMd5, md5));
    }

    @Override
    public boolean selectMd5OfUser(String md5, String userId) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectCount(lqw.eq(Files::getUserId,userId).eq(Files::getMd5,md5))>0;
    }

    @Override
    public Files selectOne(String fileId) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectOne(lqw.eq(Files::getId, fileId));
    }

    @Override
    public R delById(String id) {
        R r = R.ok();
        Files files = fMapper.getOne(id);
        fMapper.delById(id);
        System.out.println(fMapper.getCount(files.getMd5()));
        if (fMapper.getCount(files.getMd5()) != 0L) {
            return R.ok();
        }
        String videoId = files.getVideoId();
        String createTime = new DateTime(files.getCreateTime()).toDateStr();
        String fileName = files.getFileName();
        String type = files.getType();
        if ("video".equals(files.getFileType()) || "audio".equals(files.getFileType())) {
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
                throw new CloudException(R.ERROR, R.DELETE_VA_ERR);
            }
        } else {
            OSS ossClient = new OSSClientBuilder().build(InitOssClient.END_POINT,
                    InitOssClient.ACCESS_KEY_ID, InitOssClient.ACCESS_KEY_SECRET);
            try {
                String fileKey = createTime.substring(0, 10) + "/" + fileName + "." + type;
                ossClient.deleteObject(InitOssClient.BUCKET_NAME, fileKey);
            } catch (Exception e) {
                return R.error();
            } finally {
                ossClient.shutdown();
            }
        }
        return r;
    }

    @Override
    public List<Files> findVideo(String userId) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectList(lqw.eq(Files::getFileType, VIDEO)
                .eq(Files::getUserId, userId));
    }

    @Override
    public List<Files> findAudio(String userId) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectList(lqw.eq(Files::getFileType, AUDIO)
                .eq(Files::getUserId, userId));
    }

    @Override
    public List<Files> findImage(String userId) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectList(lqw.eq(Files::getFileType, IMAGE)
                .eq(Files::getUserId, userId));
    }

    @Override
    public List<Files> findOther(String userId) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectList(lqw.eq(Files::getFileType, OTHER)
                .eq(Files::getUserId, userId));
    }
}
