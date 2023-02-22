package com.hhd.controller;

import com.hhd.exceptionhandler.CloudException;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.entity.Files;
import com.hhd.service.IFileService;
import com.hhd.service.IOssService;
import com.hhd.service.IUCenterService;
import com.hhd.utils.MD5;
import com.hhd.utils.R;
import com.hhd.utils.mimeTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping()
    public R getMd5(MultipartFile file) {
        String md51 = md5.getFileMd5String(file);
        System.out.println(md51);
        return R.ok().data("e", md51);
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
                    return R.ok().data("file",files1);
                case 1:
                    files.setFileType("video");
                    Files files2 = oService.uploadVideo(file, files);
                    fService.save(files2);
                    return R.ok().data("file",files2);
                case 2:
                    files.setFileType("image");
                    Map.Entry<String, Files> upload = oService.upload(file, files).entrySet().iterator().next();
                    Files files3 = upload.getValue();
                    fService.save(files3);
                    return R.ok().data(upload.getKey(),files3);
                default:
                    files.setFileType("file");
                    Map.Entry<String, Files> upload1 = oService.upload(file, files).entrySet().iterator().next();
                    Files files4 = upload1.getValue();
                    fService.save(files4);
                    return R.ok().data(upload1.getKey(),files4);
            }
        } else {
            throw new CloudException(R.ERROR, "内存溢出");
        }
    }
}
