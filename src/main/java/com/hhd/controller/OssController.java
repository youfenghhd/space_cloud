package com.hhd.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoRequest;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.entity.Files;
import com.hhd.service.IFileService;
import com.hhd.service.IOssService;
import com.hhd.service.IUCenterService;
import com.hhd.utils.InitOssClient;
import com.hhd.utils.MD5;
import com.hhd.utils.R;
import com.hhd.utils.MimeTypeUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author -无心
 * @date 2023/2/19 22:18:49
 */
@RestController
@CrossOrigin
@Api(tags = "上传下载处理")
@RequestMapping("/oss")
public class OssController {

    //    private final MD5 md5 = new MD5();
    @Autowired
    private IOssService oService;
    @Autowired
    private IFileService fService;
    @Autowired
    private IUCenterService uService;

    private static final long ONE_G = 1073741824L;
    private static final long TWO_G = 2147483648L;

    @Operation(summary = "上传头像")
    @PostMapping("/portrait")
    public R setPortrait(MultipartFile file) {
        Map.Entry<String, Files> entry = oService.upload(file, new Files()).entrySet().iterator().next();
        System.out.println(entry.getKey());
        return R.ok().data("url", entry.getKey());
    }

    @Operation(summary = "分片式断点续传上传文件")
    @CacheEvict(value = {"normalFiles", "fuzzy","currentFile"}, allEntries = true)
    @PostMapping("/upload/{userId}")
    public R upload(MultipartFile file, @RequestParam String dir, @PathVariable String userId) {
        UCenter user = uService.selectOne(userId);
        long memory = user.getMemory() + file.getSize();
        long spaceSize;
        if (user.getVipTime() == null) {
            spaceSize = ONE_G;
        } else {
            spaceSize = TWO_G;
        }
        if (memory < spaceSize) {
            user.setMemory(memory);
            user.setId(userId);
            uService.updateById(user);
            String md5 = MD5.getFileMd5String(file);
            if (fService.selectMd5OfUser(md5, userId)) {
                return R.error().message("您的文件已存在，不可重复上传");
            }
            List<Files> filesList = fService.selectMd5File(md5);
            for (Files exist : filesList) {
                if (exist != null) {
                    exist.setUserId(userId);
                    exist.setFileDir(dir);
                    exist.setId(null);
                    return fService.save(exist) ?
                            R.ok().data("url", exist.getUrl()).data("file", exist).message("文件已实现秒传") : R.error();
                }
            }
            Files files = new Files();
            files.setSize(file.getSize());
            files.setUserId(userId);
            files.setFileDir(dir);
            switch (MimeTypeUtils.getMimeType(file)) {
                case 0:
                    files.setFileType("audio");
                    Files files1 = oService.uploadVideo(file, files);
                    fService.save(files1);
                    return R.ok().data("file", files1);
                case 1:
                    files.setFileType("video");
                    Files files2 = oService.uploadVideo(file, files);
                    fService.save(files2);
                    return R.ok().data("file", files2);
                case 2:
                    files.setFileType("image");
                    Map.Entry<String, Files> upload = oService.upload(file, files).entrySet().iterator().next();
                    Files files3 = upload.getValue();
                    fService.save(files3);
                    return R.ok().data("url", upload.getKey()).data("file", files3);
                default:
                    files.setFileType("file");
                    Map.Entry<String, Files> upload1 = oService.upload(file, files).entrySet().iterator().next();
                    Files files4 = upload1.getValue();
                    fService.save(files4);
                    return R.ok().data("url", upload1.getKey()).data("file", files4);
            }
        } else {
            throw new CloudException(R.ERROR, "内存溢出");
        }
    }

    @Operation(summary = "根据FileId获取播放地址")
    @Cacheable(cacheNames = "getPlay", unless = "#result==null")
    @PostMapping("/getPlay")
    public R getPlay(@RequestParam("isList") List<String> isList) {
        ArrayList<Map<String, Object>> urlList = new ArrayList<>();
        Files file = new Files();
        // 创建SubmitMediaInfoJob实例并初始化
        // 点播服务所在的地域ID，中国大陆地域请填cn-shanghai
        DefaultProfile profile = DefaultProfile.getProfile(InitOssClient.REGION_ID,
                InitOssClient.ACCESS_KEY_ID, InitOssClient.ACCESS_KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        GetPlayInfoRequest request = new GetPlayInfoRequest();
        // 视频ID。
        for (String s : isList) {
            System.out.println(s);
            Map<String, Object> map = new HashMap<>(8);
            file.setVideoId(s);
            map.put("videoId", s);
            request.setVideoId(s);
            try {
                GetPlayInfoResponse response = client.getAcsResponse(request);
                for (GetPlayInfoResponse.PlayInfo playInfo : response.getPlayInfoList()) {
                    // 播放地址
                    System.out.println("PlayInfo.PlayURL = " + playInfo.getPlayURL());
                    file.setUrl(playInfo.getPlayURL());
                    map.put("url", playInfo.getPlayURL());
                    urlList.add(map);
                    System.out.println(urlList);
                    request.setVideoId(null);
                }
            } catch (ServerException e) {
                e.printStackTrace();
                return R.error();
            } catch (ClientException e) {
                System.out.println("ErrCode:" + e.getErrCode());
                System.out.println("ErrMsg:" + e.getErrMsg());
                System.out.println("RequestId:" + e.getRequestId());
                return R.error();
            }

        }
        return R.ok().data("urlList", urlList);
    }

    @Operation(summary = "多线程分片式断点续传下载文件")
    @PostMapping("/downLoad")
    public R downLoad(@RequestBody List<String> id) {
        for (String thread : id) {
            oService.downLoad(thread);
        }
        return R.ok();
    }
}

