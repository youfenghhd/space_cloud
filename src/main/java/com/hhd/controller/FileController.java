package com.hhd.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hhd.pojo.entity.File;
import com.hhd.pojo.entity.UserDir;
import com.hhd.pojo.vo.TreeNode;
import com.hhd.service.IFileService;
import com.hhd.service.IUserDirService;
import com.hhd.utils.R;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{userid}/{name}")
    public R findFuzzy(@PathVariable String name, @PathVariable String userid) {
        List<File> files = fService.getFindFile(userid, name);
        System.out.println(files);
        UserDir userDir = uService.getUserDir(userid);
        TreeNode treeNode = JSON.parseObject(userDir.getUserId(), new TypeReference<TreeNode>() {
        });
        List<TreeNode> list = new ArrayList<>();
        finTreeNode(treeNode, name, list);
        return R.ok().data("files", files).data("list", list);
    }

    @PostMapping("/addFile")
    public R addFile(@RequestBody File file) {
        return fService.save(file) ? R.ok().data("addFile", file) : R.error();
    }

    @GetMapping("/{userid}")
    public R getAllFile(@PathVariable String userid) {
        return R.ok().data("allFilesOfUser", fService.getAllFile(userid));
    }

    @GetMapping("/info/{id}")
    public R getFileInfo(@PathVariable String id) {
        return R.ok().data("fileinfo", fService.getFileInfo(id));
    }

    //    @PutMapping("/{id}/{fileName}")
//    public R renameFile(@PathVariable String id, @PathVariable String fileName) {
//        LambdaQueryWrapper<File> lqw = new LambdaQueryWrapper<>();
//        File one = fService.getOne(lqw.eq(File::getId, id));
//        File file = new File();
//        file.setId(id);
//        file.setFileName(fileName);
//        file.setSize(one.getSize());
//        return fService.updateById(file) ? R.ok() : R.error();
//    }
    @PutMapping("/rename")
    public R renameFile1(@RequestBody File files) {
        LambdaQueryWrapper<File> lqw = new LambdaQueryWrapper<>();
        File file = fService.getOne(lqw.eq(File::getId, files.getId()));
        file.setFileName(files.getFileName());
        return fService.updateById(file) ? R.ok() : R.error();
    }


    @PutMapping("/collection")
    public R CollectionFile(@RequestParam("id") String[] id) {
        boolean flag = false;
        for (String s : id) {
            System.out.println(s);
            File file = new File();
            file.setId(s);
            file.setCollection(1);
            flag = fService.updateById(file);
        }
        return flag ? R.ok() : R.error();
    }

    @PutMapping("/noncollecton")
    public R nonCollectionFile(@RequestParam("id") String[] id) {
        boolean flag = false;
        for (String s : id) {
            System.out.println(s);
            File file = new File();
            file.setId(s);
            file.setCollection(0);
            flag = fService.updateById(file);
        }
        return flag ? R.ok() : R.error();
    }

    @PostMapping("/{id}")
    public R getDirFile(@RequestBody UserDir userDir) {
        return R.ok().data("filesOfDir", fService.getCurFiles(userDir));
    }

    @PostMapping("/moveFile")
    public R moveFile(@RequestBody String movingPath, @RequestParam("id") String[] id) {
        boolean flag = false;
        for (String s : id) {
            System.out.println(s);
            File file = new File();
            file.setId(s);
            file.setFileDir(movingPath);
            flag = fService.updateById(file);
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
}

