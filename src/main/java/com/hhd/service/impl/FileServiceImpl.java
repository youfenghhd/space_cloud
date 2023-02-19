package com.hhd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhd.mapper.FileMapper;
import com.hhd.pojo.entity.File;
import com.hhd.pojo.entity.UserDir;
import com.hhd.service.IFileService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

    @Override
    public List<File> getAllFile(String userid) {
        LambdaQueryWrapper<File> lqw = new LambdaQueryWrapper<>();
        return baseMapper.selectList(lqw.eq(File::getUserId,userid));
    }

    @Override
    public List<File> getFileInfo(String id) {
        LambdaQueryWrapper<File> lqw = new LambdaQueryWrapper<>();
        return baseMapper.selectList(lqw.eq(File::getId,id));
    }

    @Override
    public List<File> getCurFiles(UserDir userDir) {
        LambdaQueryWrapper<File> lqw = new LambdaQueryWrapper<>();
        return baseMapper.selectList(lqw.eq(File::getFileDir,userDir.getUserDir())
                .eq(File::getUserId,userDir.getUserId()));
    }

    @Override
    public File getFiles(String id) {
        LambdaQueryWrapper<File> lqw = new LambdaQueryWrapper<>();
        return baseMapper.selectOne(lqw.eq(File::getId,id));
    }

    @Override
    public List<File> getFindFile(String userid, String name) {
        LambdaQueryWrapper<File> lqw= new LambdaQueryWrapper<>();
        return baseMapper.selectList(lqw.eq(File::getUserId,userid).like(File::getFileName,name));
    }

    @Override
    public List<File> getList(String userid, String url, int result, String name) {
        LambdaQueryWrapper<File> lqw = new LambdaQueryWrapper<>();
        List<File> files = baseMapper.selectList(lqw.eq(File::getUserId, userid).like(File::getFileDir, url));
        for (File file : files) {
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
}
