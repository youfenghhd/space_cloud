package com.hhd.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhd.mapper.FileMapper;
import com.hhd.pojo.entity.Files;
import com.hhd.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wiremock.org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.List;

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
        return fMapper.showRecoveryAll(simpleDateFormat.format(nowTime),userid);
    }

    @Override
    public List<Files> getFileInfo(String id) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return baseMapper.selectList(lqw.eq(Files::getId, id));
    }

    @Override
    public List<Files> getCurFiles(String userDir, String id) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return baseMapper.selectList(lqw.eq(Files::getFileDir, userDir)
                .eq(Files::getUserId, id));
    }

    @Override
    public Files getFiles(String id) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return baseMapper.selectOne(lqw.eq(Files::getId, id));
    }

    @Override
    public List<Files> getFindFile(String userid, String name) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return baseMapper.selectList(lqw.eq(Files::getUserId, userid).like(Files::getFileName, name));
    }

    @Override
    public List<Files> getList(String userid, String url, int result, String name) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        List<Files> files = baseMapper.selectList(lqw.eq(Files::getUserId, userid).like(Files::getFileDir, url));
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
    public int logicDelFile(String id) {
        LambdaUpdateWrapper<Files> lqw = new LambdaUpdateWrapper<>();
        return baseMapper.update(new Files(), lqw.set(Files::getLogicDelTime, simpleDateFormat.format(DateUtils.addDays(new DateTime(), 30)))
                .eq(Files::getId, id));
    }

    @Override
    public int logicNormalFile(String id) {
        return fMapper.logicNormalFile(id);
    }

    @Override
    public List<Files> selectMd5File(String md5) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectList(lqw.eq(Files::getMd5, md5));
    }

    @Override
    public Files selectOne(String fileId) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectOne(lqw.eq(Files::getId, fileId));
    }

    @Override
    public void delById(String id) {
        fMapper.delById(id);
    }

    @Override
    public List<Files> findVideo() {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectList(lqw.eq(Files::getFileType, VIDEO));
    }

    @Override
    public List<Files> findAudio() {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectList(lqw.eq(Files::getFileType, AUDIO));
    }

    @Override
    public List<Files> findImage() {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectList(lqw.eq(Files::getFileType, IMAGE));
    }

    @Override
    public List<Files> findOther() {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        return fMapper.selectList(lqw.eq(Files::getFileType, OTHER));
    }
}
