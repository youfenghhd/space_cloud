package com.hhd.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.entity.Files;
import com.hhd.pojo.entity.UserDir;
import com.hhd.pojo.vo.TreeNode;
import com.hhd.service.IFileService;
import com.hhd.service.IUCenterService;
import com.hhd.service.IUserDirService;
import com.hhd.utils.ConfirmToken;
import com.hhd.utils.PassToken;
import com.hhd.utils.Results;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
    @Autowired
    private IUCenterService userService;

    @ConfirmToken
    @Operation(summary = "模糊查询文件")
    @Cacheable(cacheNames = "fuzzy", unless = "#result==null")
    @PostMapping("/fuzzy")
    public Results findFuzzy(@RequestBody Files file) {
        List<Files> files = fService.getFindFile(file.getUserId(), file.getFileName());
        UserDir userDir = uService.getUserDir(file.getUserId());
        TreeNode treeNode = JSON.parseObject(userDir.getUserDir(), new TypeReference<TreeNode>() {
        });
        List<TreeNode> list = new ArrayList<>();
        findTreeNode(treeNode, file.getFileName(), list);
        return Results.ok().data("files", files).data("list", list);
    }

    @ConfirmToken
    @Operation(summary = "添加文件进数据库")
    @CacheEvict(value = {"normalFiles", "fuzzy", "currentFile"}, allEntries = true)
    @PostMapping("/addFile")
    public Results addFile(@RequestBody Files files) {
        return fService.save(files) ? Results.ok().data("addFile", files) : Results.error();
    }

    @PassToken
    @Operation(summary = "查询所有正常状态文件")
    @Cacheable(cacheNames = "normalFiles", unless = "#result==null")
    @GetMapping("/normal/{userid}")
    public Results showNormalAll(@PathVariable String userid) {
        return Results.ok().data("allFilesOfUser", fService.showNormalAll(userid));
    }

    @PassToken
    @Operation(summary = "查询回收站文件")
    @Cacheable(cacheNames = "recoveryFile", unless = "#result==null")
    @GetMapping("/recovery/{userid}")
    public Results findRecovery(@PathVariable String userid) {
        return Results.ok().data("recovery", fService.showRecoveryAll(userid));
    }

    @PassToken
    @Operation(summary = "查询文件详情")
    @Cacheable(cacheNames = "fileInfo", unless = "#result==null")
    @GetMapping("/info/{id}")
    public Results getFileInfo(@PathVariable String id) {
        return Results.ok().data("fileinfo", fService.selectOne(id));
    }

    @ConfirmToken
    @Operation(summary = "重命名文件")
    @CacheEvict(value = {"normalFiles", "fuzzy", "currentFile"}, allEntries = true)
    @PutMapping("/rename")
    public Results renameFile(@RequestBody Files files) {
        Files exist = fService.selectOne(files.getId());
        exist.setFileName(files.getFileName());
        return fService.updateById(exist) ? Results.ok() : Results.error();
    }

    @ConfirmToken
    @Operation(summary = "收藏文件")
    @CacheEvict(value = {"normalFiles", "fuzzy", "currentFile"}, allEntries = true)
    @PutMapping("/collection")
    public Results CollectionFile(@RequestParam("id") String[] id) {
        boolean flag = false;
        for (String s : id) {
            Files files = new Files();
            files.setId(s);
            files.setCollection(1);
            flag = fService.updateById(files);
        }
        return flag ? Results.ok() : Results.error();
    }

    @ConfirmToken
    @Operation(summary = "取消收藏文件")
    @CacheEvict(value = {"normalFiles", "fuzzy", "currentFile"}, allEntries = true)
    @PutMapping("/noncollecton")
    public Results nonCollectionFile(@RequestParam("id") String[] id) {
        boolean flag = false;
        for (String s : id) {
            Files files = new Files();
            files.setId(s);
            files.setCollection(0);
            flag = fService.updateById(files);
        }
        return flag ? Results.ok() : Results.error();
    }
    @PassToken
    @Operation(summary = "查询当前目录下文件")
    @Cacheable(cacheNames = "currentFile", unless = "#result==null")
    @PostMapping("/current/{id}")
    public Results getDirFile(@PathVariable String id, @RequestBody UserDir userDir) {
        return Results.ok().data("files", fService.getCurFiles(userDir.getUserDir(), id));
    }

    @ConfirmToken
    @Operation(summary = "移动文件夹")
    @CacheEvict(value = {"currentFile"}, allEntries = true)
    @PostMapping("/moveFile")
    public Results moveFile(@RequestBody UserDir userDir, @RequestParam("id") String[] id) {
        boolean flag = false;
        for (String s : id) {
            Files files = new Files();
            files.setId(s);
            files.setFileDir(userDir.getUserDir());
            flag = fService.updateById(files);
        }
        return flag ? Results.ok() : Results.error();
    }

    @ConfirmToken
    @Operation(summary = "文件加入回收站")
    @CacheEvict(value = {"normalFiles", "recoveryFile", "fuzzy", "currentFile"}, allEntries = true)
    @PutMapping("/del/{userId}")
    public Results logicDelFile(@RequestBody String[] idList, @PathVariable String userId) {
        LambdaQueryWrapper<UCenter> lqw = new LambdaQueryWrapper<>();
        for (String s : idList) {
            fService.logicDelFile(s);
        }
        return Results.ok();
    }

    @ConfirmToken
    @Operation(summary = "文件移出回收站")
    @CacheEvict(value = {"normalFiles", "recoveryFile", "fuzzy", "currentFile"}, allEntries = true)
    @PutMapping("/normal")
    public Results logicNormalFile(@RequestBody List<String> id) {
        Results results = Results.error();
        for (String s : id) {
            results = fService.logicNormalFile(s);
        }
        return results;
    }

    @ConfirmToken
    @Operation(summary = "文件真实删除")
    @CacheEvict(value = "recoveryFile", allEntries = true)
    @DeleteMapping("/delete")
    public Results Delete(@RequestBody List<String> id) {
        Results results = Results.error();
        for (String s : id) {
            results = fService.delById(s);
        }
        return results;
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
                if (list1.size() >= 1) {
                    findTreeNode(node, name, lists);
                }
            }
            findTreeNode(node, name, lists);
        }
    }
}
