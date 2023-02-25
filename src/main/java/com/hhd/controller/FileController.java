package com.hhd.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hhd.pojo.entity.Files;
import com.hhd.pojo.entity.UserDir;
import com.hhd.pojo.vo.TreeNode;
import com.hhd.service.IFileService;
import com.hhd.service.IUserDirService;
import com.hhd.utils.R;
import com.hhd.utils.UserLoginToken;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@RestController
@CrossOrigin
@RequestMapping("/files")
@Api(tags = "文件处理")
public class FileController {

    @Autowired
    private IFileService fService;
    @Autowired
    private IUserDirService uService;

    @Operation(summary = "模糊查询文件")
    //    @UserLoginToken
    @Cacheable(cacheNames = "fuzzy", unless = "#result==null")
    @GetMapping("/fuzzy")
    public R findFuzzy(@RequestBody Files file) {
        List<Files> files = fService.getFindFile(file.getUserId(), file.getFileName());
        System.out.println(files);
        UserDir userDir = uService.getUserDir(file.getUserId());
        TreeNode treeNode = JSON.parseObject(userDir.getUserDir(), new TypeReference<TreeNode>() {
                });
        List<TreeNode> list = new ArrayList<>();
        findTreeNode(treeNode, file.getFileName(), list);
        return R.ok().data("files", files).data("list", list);
    }

    @Operation(summary = "添加文件进数据库")
    //    @UserLoginToken
    @CachePut("userFiles")
    @PostMapping("/addFile")
    public R addFile(@RequestBody Files files) {
        return fService.save(files) ? R.ok().data("addFile", files) : R.error();
    }

    @Operation(summary = "查询所有正常状态文件")
//    @UserLoginToken
    @Cacheable(cacheNames = "nomalFiles", unless = "#result==null")
    @GetMapping("/normal/{userid}")
    public R showNormalAll(@PathVariable String userid) {
        return R.ok().data("allFilesOfUser", fService.showNormalAll(userid));
    }

    @Operation(summary = "查询回收站文件")
    //    @UserLoginToken
    @Cacheable(cacheNames = "recoveryFile", unless = "#result==null")
    @GetMapping("/recovery/{userid}")
    public R findRecovery(@PathVariable String userid) {
        return R.ok().data("recovery", fService.showRecoveryAll(userid));
    }

    @Operation(summary = "查询文件详情")
    //    @UserLoginToken
    @Cacheable(cacheNames = "fileInfo", unless = "#result==null")
    @GetMapping("/info/{id}")
    public R getFileInfo(@PathVariable String id) {
        return R.ok().data("fileinfo", fService.getFileInfo(id));
    }

    @Operation(summary = "重命名文件")
    //    @UserLoginToken
    @CachePut("userFiles")
    @PutMapping("/rename")
    public R renameFile(@RequestBody Files files) {
        Files exist = fService.selectOne(files.getId());
        exist.setFileName(files.getFileName());
        return fService.updateById(exist) ? R.ok() : R.error();
    }

    @Operation(summary = "收藏文件")
    //    @UserLoginToken
    @CachePut("userFiles")
    @PutMapping("/collection")
    public R CollectionFile(@RequestBody String[] id) {
        boolean flag = false;
        for (String s : id) {
            System.out.println(s);
            Files files = new Files();
            files.setId(s);
            files.setCollection(1);
            flag = fService.updateById(files);
        }
        return flag ? R.ok() : R.error();
    }

    @Operation(summary = "取消收藏文件")
    //    @UserLoginToken
    @CachePut("userFiles")
    @PutMapping("/noncollecton")
    public R nonCollectionFile(@RequestBody String[] id) {
        boolean flag = false;
        for (String s : id) {
            System.out.println(s);
            Files files = new Files();
            files.setId(s);
            files.setCollection(0);
            flag = fService.updateById(files);
        }
        return flag ? R.ok() : R.error();
    }

    @Operation(summary = "查询当前目录下文件")
    //    @UserLoginToken
    @Cacheable(cacheNames = "dirFile")
    @PostMapping("/current")
    public R getDirFile(@RequestBody String id, @RequestParam String userDir) {
        return R.ok().data("filesOfDir", fService.getCurFiles(userDir, id));
    }

    @Operation(summary = "移动文件夹")
    //    @UserLoginToken
    @CachePut("dirFile")
    @PostMapping("/moveFile")
    public R moveFile(@RequestParam String movingPath, @RequestBody String[] id) {
        boolean flag = false;
        for (String s : id) {
            System.out.println(s);
            Files files = new Files();
            files.setId(s);
            files.setFileDir(movingPath);
            flag = fService.updateById(files);
        }
        return flag ? R.ok() : R.error();
    }

    @Operation(summary = "文件加入回收站")
    //    @UserLoginToken
    @CachePut("recoveryFile")
    @PutMapping("/del")
    public R logicDelFile(@RequestBody List<String> id) {
        for (String s : id) {
            fService.logicDelFile(s);
        }
        return R.ok();
    }

    @Operation(summary = "文件移出回收站")
    //    @UserLoginToken
    @CachePut("normalFile")
    @PutMapping("/normal")
    public R logicNormalFile(@RequestBody List<String> id) {
        for (String s : id) {
            fService.logicNormalFile(s);
        }
        return R.ok();
    }

    @Operation(summary = "音频文件分类")
    //    @UserLoginToken
    @GetMapping("/findaudio")
    public R findAudio() {
        return R.ok().data("audio", fService.findAudio());
    }

    @Operation(summary = "视频文件分类")
    //    @UserLoginToken
    @GetMapping("/findvideo")
    public R findVideo() {
        return R.ok().data("audio", fService.findVideo());
    }

    @Operation(summary = "图片文件分类")
    //    @UserLoginToken
    @GetMapping("/findimage")
    public R findImage() {
        return R.ok().data("audio", fService.findImage());
    }

    @Operation(summary = "其他文件分类")
    //    @UserLoginToken
    @GetMapping("/findOther")
    public R findOther() {
        return R.ok().data("audio", fService.findOther());
    }

    public void findTreeNode(TreeNode treeNode, String name, List<TreeNode> lists) {
        List<TreeNode> list = treeNode.getChildrenList();
        if (list == null || list.isEmpty()) {
            return;
        }
        for (TreeNode node : list) {
            if (node.getName().contains(name)) {
                lists.add(node);
                List<TreeNode> list1 = node.getChildrenList();
                System.out.println(list1);
                if (list1.size() >= 1) {
                    findTreeNode(node, name, lists);
                }
            }
            findTreeNode(node, name, lists);
        }
    }
}
