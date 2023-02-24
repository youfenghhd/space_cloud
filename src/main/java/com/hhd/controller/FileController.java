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
import com.hhd.utils.TokenUtil;
import com.hhd.utils.UserLoginToken;
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
@RequestMapping("/files")
public class FileController {

    @Autowired
    private IFileService fService;
    @Autowired
    private IUserDirService uService;

    @Cacheable(cacheNames = "fuzzy", unless = "#result==null")
    @GetMapping("/{userid}/{name}")
    public R findFuzzy(@PathVariable String name, @PathVariable String userid) {
        List<Files> files = fService.getFindFile(userid, name);
        System.out.println(files);
        UserDir userDir = uService.getUserDir(userid);
        TreeNode treeNode = JSON.parseObject(userDir.getUserId(), new TypeReference<TreeNode>() {
        });
        List<TreeNode> list = new ArrayList<>();
        finTreeNode(treeNode, name, list);
        return R.ok().data("files", files).data("list", list);
    }

    @CachePut("userFiles")
    @PostMapping("/addFile")
    public R addFile(@RequestBody Files files) {
        return fService.save(files) ? R.ok().data("addFile", files) : R.error();
    }
    @UserLoginToken
    @Cacheable(cacheNames = "nomalFiles", unless = "#result==null")
    @GetMapping("/{userid}")
    public R showNormalAll(@PathVariable String userid) {
        return R.ok().data("allFilesOfUser", fService.showNormalAll(userid));
    }

    @Cacheable(cacheNames = "recoveryFile", unless = "#result==null")
    @GetMapping("/recovery")
    public R findRecovery() {
        return R.ok().data("recovery", fService.showRecoveryAll());
    }


    @Cacheable(cacheNames = "fileInfo", unless = "#result==null")
    @GetMapping("/info/{id}")
    public R getFileInfo(@PathVariable String id) {
        return R.ok().data("fileinfo", fService.getFileInfo(id));
    }


    @CachePut("userFiles")
    @PutMapping("/rename")
    public R renameFile(@RequestBody Files files) {
        LambdaQueryWrapper<Files> lqw = new LambdaQueryWrapper<>();
        Files file = fService.getOne(lqw.eq(Files::getId, files.getId()));
        file.setFileName(files.getFileName());
        return fService.updateById(file) ? R.ok() : R.error();
    }

    @CachePut("userFiles")
    @PutMapping("/collection")
    public R CollectionFile(@RequestParam("id") String[] id) {
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

    @CachePut("userFiles")
    @PutMapping("/noncollecton")
    public R nonCollectionFile(@RequestParam("id") String[] id) {
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

    @Cacheable(cacheNames = "dirFile")
    @PostMapping("/{id}")
    public R getDirFile(@RequestBody UserDir userDir) {
        return R.ok().data("filesOfDir", fService.getCurFiles(userDir));
    }

    @CachePut("dirFile")
    @PostMapping("/moveFile")
    public R moveFile(@RequestBody String movingPath, @RequestParam("id") String[] id) {
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

    public void finTreeNode(TreeNode treeNode, String name, List<TreeNode> lists) {
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
                    finTreeNode(node, name, lists);
                }
            }
            finTreeNode(node, name, lists);
        }
    }

    @CachePut("recoveryFile")
    @PutMapping("/del")
    public R logicDelFile(@RequestBody String id) {
        return fService.logicDelFile(id) > 0 ? R.ok() : R.error();
    }

    @CachePut("normalFile")
    @PutMapping("/normal")
    public R logicNormalFile(@RequestBody String id) {
        return fService.logicNormalFile(id) > 0 ? R.ok() : R.error();
    }
}

