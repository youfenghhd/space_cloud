package com.hhd.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoRequest;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
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
import com.hhd.utils.mimeTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/oss")
public class OssController {

    private final MD5 md5 = new MD5();
    @Autowired
    private IOssService oService;
    @Autowired
    private IFileService fService;
    @Autowired
    private IUCenterService uService;

    private static final long TWO_G = 2147483648L;

    @PostMapping("/portrait")
    public R setPortrait(MultipartFile file) {
        Map.Entry<String, Files> entry = oService.upload(file, new Files()).entrySet().iterator().next();
        return R.ok().data("url", entry.getKey());
    }

    @PostMapping("/upload")
    public R upload(MultipartFile file, String dir, String userId) {
        UCenter user = uService.selectOne(userId);
        long memory = user.getMemory() + file.getSize();
        if (memory < TWO_G) {
            user.setMemory(memory);
            user.setId(userId);
            uService.updateById(user);
            List<Files> filesList = fService.selectMd5File(md5.getFileMd5String(file));
            for (Files exist : filesList) {
                if (exist != null) {
                    exist.setUserId(userId);
                    exist.setFileDir(dir);
                    exist.setId(null);
                    return fService.save(exist) ? R.ok().data(exist.getUrl(), exist) : R.error();
                }
            }
            Files files = new Files();
            files.setSize(file.getSize());
            files.setUserId(userId);
            files.setFileDir(dir);
            switch (mimeTypeUtils.getMimeType(file)) {
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
                    return R.ok().data("url", upload.getKey()).data("files", files3);
                default:
                    files.setFileType("file");
                    Map.Entry<String, Files> upload1 = oService.upload(file, files).entrySet().iterator().next();
                    Files files4 = upload1.getValue();
                    fService.save(files4);
                    return R.ok().data("url", upload1.getKey()).data("files", files4);
            }
        } else {
            throw new CloudException(R.ERROR, "内存溢出");
        }
    }


    @DeleteMapping("/remove")
    public R remove(@RequestBody String[] idList, String userId) {
        LambdaQueryWrapper<UCenter> lqw = new LambdaQueryWrapper<>();
        UCenter user = uService.getOne(lqw.eq(UCenter::getId, userId));
        for (String s : idList) {
            LambdaQueryWrapper<Files> lqw1 = new LambdaQueryWrapper<>();
            Files files = fService.getFiles(s);
            user.setMemory(user.getMemory() - files.getSize());
            user.setId(userId);
            uService.updateById(user);
            if ("video".equals(files.getFileType()) || "audio".equals(files.getFileType())) {
                return oService.deleteVa(s);
            }
            return oService.delete(s);
        }
        return null;
    }

    @PostMapping("/getPlay")
    public R getPlay(@RequestBody List<String> list) {
        ArrayList<Map<String, Object>> urlList = new ArrayList<>();
        Files file = new Files();
        // 创建SubmitMediaInfoJob实例并初始化
        // 点播服务所在的地域ID，中国大陆地域请填cn-shanghai
        DefaultProfile profile = DefaultProfile.getProfile(InitOssClient.REGION_ID,
                InitOssClient.ACCESS_KEY_ID, InitOssClient.ACCESS_KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        GetPlayInfoRequest request = new GetPlayInfoRequest();
        // 视频ID。
        for (String s : list) {
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
            } catch (ClientException e) {
                System.out.println("ErrCode:" + e.getErrCode());
                System.out.println("ErrMsg:" + e.getErrMsg());
                System.out.println("RequestId:" + e.getRequestId());
            }

        }
        return R.ok().data("urlList", urlList);
    }

    @PostMapping("/downLoad")
    public R downLoad(@RequestBody List<String> id){
        for (String thread : id) {
            oService.downLoad(thread);
        }
        return R.ok();
    }
}

