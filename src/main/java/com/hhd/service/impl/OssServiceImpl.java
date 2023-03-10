package com.hhd.service.impl;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.entity.Files;
import com.hhd.service.IFileService;
import com.hhd.service.IOssService;
import com.hhd.service.IUCenterService;
import com.hhd.utils.InitOssClient;
import com.hhd.utils.MD5;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author -无心
 * @date 2023/2/18 22:18:00
 */
@Service
public class OssServiceImpl implements IOssService {
    private final MD5 md5 = new MD5();
    @Autowired
    private IFileService fService;
    @Autowired
    private IUCenterService uService;

    @Override
    public Map<String, Files> upload(MultipartFile file, Files files) {
        String objectName = new DateTime().toDateStr() + "/" + file.getOriginalFilename();
        files.setType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")).substring(1));
        files.setFileName(file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf(".")));
        files.setMd5(md5.getFileMd5String(file));
        String url = InitOssClient.HTTPS_PREFIX + InitOssClient.BUCKET_NAME + "." + InitOssClient.END_POINT + "/" + objectName;
        files.setUrl(url);

        OSS ossClient = new OSSClientBuilder().build(InitOssClient.END_POINT, InitOssClient.ACCESS_KEY_ID, InitOssClient.ACCESS_KEY_SECRET);
        try {
            // 创建InitiateMultipartUploadRequest对象。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(InitOssClient.BUCKET_NAME, objectName);

            // 初始化分片。
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            String uploadId = upresult.getUploadId();

            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
            List<PartETag> parteTags = new ArrayList<>();
            // 每个分片的大小，用于计算文件有多少个分片。单位为字节。
            final long partSize = 1024 * 1024L;

            // 计算文件分片数
            long fileLength = file.getSize();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                InputStream instream = file.getInputStream();
                // 跳过已经上传的分片。
                instream.skip(startPos);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(InitOssClient.BUCKET_NAME);
                uploadPartRequest.setKey(objectName);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(instream);
                uploadPartRequest.setPartSize(curPartSize);
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                parteTags.add(uploadPartResult.getPartETag());
            }

            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(InitOssClient.BUCKET_NAME, objectName, uploadId, parteTags);

            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println(completeMultipartUploadResult.getETag());
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        Map<String, Files> map = new HashMap<>(8);
        map.put(url, files);
        return map;
    }


    @Override
    public Files uploadVideo(MultipartFile file, Files files) {
        files.setType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")).substring(1));
        files.setFileName(file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf(".")));
        files.setMd5(md5.getFileMd5String(file));
//        upload(file, files);

        LambdaQueryWrapper<UCenter> lqw = new LambdaQueryWrapper<>();
        UCenter one = uService.getOne(lqw.eq(UCenter::getId, files.getUserId()));
        Path pathCreate;
        try {
            pathCreate = java.nio.file.Files.createDirectories(Paths.get(one.getDownLoadAdd() + "\\temp"));
        } catch (Exception e) {
            throw new CloudException(R.ERROR, R.EXECUTION_ERR);
        }
        String name = file.getOriginalFilename();
        String fileName = pathCreate.toString() + file.getOriginalFilename();
        //上传文件名称需为上传文件绝对路径，MultipartFile无法直接获取，转储指定地址
        File f = new File(fileName);
        try {
            file.transferTo(f);
        } catch (Exception e) {
            throw new CloudException(R.ERROR, R.EXECUTION_ERR);
        }
        UploadVideoRequest request = new UploadVideoRequest(InitOssClient.ACCESS_KEY_ID,
                InitOssClient.ACCESS_KEY_SECRET, files.getFileName(), fileName);
        request.setPartSize(2 * 1024 * 1024L);
        request.setTaskNum(5);
        request.setEnableCheckpoint(true);
        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadVideoResponse response = uploader.uploadVideo(request);
        if (response.isSuccess()) {
            files.setVideoId(response.getVideoId());
            System.out.print("VideoId=" + response.getVideoId() + "\n");
        } else {
            /* 如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因 */
            System.out.print("VideoId=" + response.getVideoId() + "\n");
            System.out.print("ErrorCode=" + response.getCode() + "\n");
            System.out.print("ErrorMessage=" + response.getMessage() + "\n");
        }
        f.delete();
        return files;
    }

    @Async
    @Override
    public void downLoad(String id) {
        System.out.println(Thread.currentThread().getName());
        OSS ossClient = new OSSClientBuilder().build(InitOssClient.END_POINT,
                InitOssClient.ACCESS_KEY_ID, InitOssClient.ACCESS_KEY_SECRET);
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        Files one = fService.getOne(lqw.eq(Files::getId, id));
        LambdaQueryWrapper<UCenter> lqw1 = new LambdaQueryWrapper<>();
        UCenter user = uService.getOne(lqw1.eq(UCenter::getId, one.getUserId()));
        String objectName = one.getUrl().substring(50);
        //创建用户自定义的下载目录文件夹
        try {
            java.nio.file.Files.createDirectories(Paths.get(user.getDownLoadAdd()));
        } catch (Exception e) {
            throw new CloudException(R.ERROR, R.EXECUTION_ERR);
        }
        try {
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(InitOssClient.BUCKET_NAME, objectName);
            // 指定Object下载到本地文件的完整路径
            downloadFileRequest.setDownloadFile(user.getDownLoadAdd() + "\\" + one.getFileName() + "." + one.getType());
            downloadFileRequest.setPartSize(1 * 1024 * 1024);
            downloadFileRequest.setTaskNum(10);
            downloadFileRequest.setEnableCheckpoint(true);
            // 设置断点记录文件的完整路径
            downloadFileRequest.setCheckpointFile(user.getDownLoadAdd() + "\\" + one.getFileName() + ".dcp");

            DownloadFileResult downloadRes = ossClient.downloadFile(downloadFileRequest);

            ObjectMetadata objectMetadata = downloadRes.getObjectMetadata();

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (Throwable ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        R.ok();
    }
}
