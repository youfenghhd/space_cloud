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
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;

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
    public R findFile(@PathVariable String name, @PathVariable String userid) {
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

    @GetMapping("/{id}")
    public R getFileInfo(@PathVariable String id){
        return  R.ok().data("fileInfo",fService.getFileInfo(id));
    }

    @PostMapping("/{id}/{fileName}")
    public R renameFile(@PathVariable String id, @PathVariable String fileName){
        LambdaQueryWrapper<File> lqw = new LambdaQueryWrapper<>();
        File one = fService.getOne(lqw.eq(File::getId,id));
        File file = new File();
        file.setId(id);
        file.setFileName(fileName);
        file.setSize(one.getSize());
        return fService.updateById(file)?R.ok():R.error();
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

