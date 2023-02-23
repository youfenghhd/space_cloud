package com.hhd.service.impl;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.*;
import com.aliyun.oss.model.*;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.mapper.FileMapper;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.entity.Files;
import com.hhd.service.IFileService;
import com.hhd.service.IOssService;
import com.hhd.service.IUCenterService;
import com.hhd.utils.InitVodClient;
import com.hhd.utils.MD5;
import com.hhd.utils.InitOssClient;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import wiremock.com.github.jknack.handlebars.Lambda;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hhd.utils.InitVodClient.initVodClient;

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
        System.out.println(files);
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


            // 填写本地文件的完整路径。
//            final File sampleFiles = new File(path);
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
        String fileName = file.getOriginalFilename();
        String title = fileName.substring(0, fileName.lastIndexOf("."));
        files.setType(fileName.substring(fileName.lastIndexOf(".")).substring(1));
        files.setFileName(title);
        files.setMd5(md5.getFileMd5String(file));

        try {
            InputStream inputStream = file.getInputStream();
            UploadStreamRequest request = new UploadStreamRequest(InitOssClient.ACCESS_KEY_ID,
                    InitOssClient.ACCESS_KEY_SECRET, title, fileName, inputStream);

            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);

            String videoId = null;
            if (response.isSuccess()) {
                videoId = response.getVideoId();
                System.out.println(response.getRequestId());
            } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。
                // 其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
                videoId = response.getVideoId();
            }
            files.setVideoId(videoId);
            return files;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public R delete(String id) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Files::getId, id);
        Files one = fService.getOne(lqw);
        String time = one.getCreateTime();
        String createTime = new DateTime(time).toDateStr();
        String fileName = one.getFileName();
        fService.delById(id);
        OSS ossClient = new OSSClientBuilder().build(InitOssClient.END_POINT,
                InitOssClient.ACCESS_KEY_ID, InitOssClient.ACCESS_KEY_SECRET);
        try {
            String fileKey = createTime + "/" + fileName;
            ossClient.deleteObject(InitOssClient.BUCKET_NAME, fileKey);
        } catch (Exception e) {
            return R.error();
        } finally {
            ossClient.shutdown();
        }
        return R.ok();
    }

    @Override
    public R deleteVa(String id) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        Files one = fService.getOne(lqw.eq(Files::getId, id));
        String videoId = one.getVideoId();
        fService.delById(id);
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
        return R.ok();
    }

    @Override
    public R downLoad(List<String> id) {
        for (String s : id) {
            OSS ossClient = new OSSClientBuilder().build(InitOssClient.END_POINT,
                    InitOssClient.ACCESS_KEY_ID, InitOssClient.ACCESS_KEY_SECRET);
            LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
            Files one = fService.getOne(lqw.eq(Files::getId, s));
            LambdaQueryWrapper<UCenter> lqw1 = new LambdaQueryWrapper<>();
            UCenter user = uService.getOne(lqw1.eq(UCenter::getId, one.getUserId()));
            String objectName = one.getUrl().substring(47);

            System.out.println(objectName);

            try {
                DownloadFileRequest downloadFileRequest = new DownloadFileRequest(InitOssClient.BUCKET_NAME, objectName);
                // 指定Object下载到本地文件的完整路径
                downloadFileRequest.setDownloadFile(user.getDownLoadAdd()+"\\"+one.getFileName()+"."+one.getType());
                downloadFileRequest.setPartSize(1 * 1024 * 1024);
                downloadFileRequest.setTaskNum(10);
                downloadFileRequest.setEnableCheckpoint(true);
                // 设置断点记录文件的完整路径
                downloadFileRequest.setCheckpointFile(user.getDownLoadAdd()+"\\"+one.getFileName()+".dcp");


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
        }
        return R.ok();
    }


}
