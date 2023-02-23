package com.hhd.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.pojo.entity.Files;
import com.hhd.pojo.entity.UserDir;
import com.hhd.pojo.vo.TreeNode;
import com.hhd.service.IFileService;
import com.hhd.service.IUserDirService;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
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
@RequestMapping("/dirs")
public class UserDirController {
    @Autowired
    private IUserDirService uService;
    @Autowired
    private IFileService fService;
    private int result = 1;

    @GetMapping("/{id}")
    public R getFile(@PathVariable String id) {
        System.out.println(id);
        UserDir userDir = uService.getUserDir(id);
        System.out.println(userDir);
        return R.ok().data("dir", userDir);
    }


    @PostMapping("/{userid}/{name}/{id}")
    public R setDir(@PathVariable long id, @PathVariable String name, @PathVariable String userid) {
        UserDir userDir = uService.getUserDir(userid);
        TreeNode tNode = JSON.parseObject(userDir.getUserDir(), new TypeReference<TreeNode>() {
        });
        TreeNode tNode1 = new TreeNode();
        tNode1.setName(name + "/");
        tNode1.setParentId(id);
        tNode1.setChildrenList(new ArrayList<>());
        insert(tNode, id, tNode1);
        System.out.println(tNode);
        String string = JSON.toJSONString(tNode);
        System.out.println(string);
        userDir.setUserDir(string);
        return uService.setUserDir(userDir) > 0 ? R.ok().data("dir", userDir) : R.error();

    }


    @DeleteMapping("/{userid}/{id}")
    public R delDir(@PathVariable String userid, @PathVariable long id, @RequestBody String url) {
        UserDir userDir = uService.getUserDir(userid);
        TreeNode treeNode = JSON.parseObject(userDir.getUserDir(), new TypeReference<TreeNode>() {
        });
        if (uService.deleteStruct(userid, url)) {
            System.out.println("ok");
            StringBuilder stringBuilder = new StringBuilder();
            delete(treeNode, id, stringBuilder);
            userDir.setUserDir(JSON.toJSONString(treeNode));
            uService.setUserDir(userDir);
            return R.ok();
        }
        return R.error();
    }

    @PutMapping("/{userid}/{name}/{id}")
    public R updateDir(@PathVariable long id, @PathVariable String name, @PathVariable String userid, @RequestBody String url) {
        UserDir userDir = uService.getUserDir(userid);
        TreeNode treeNode = JSON.parseObject(userDir.getUserDir(), new TypeReference<TreeNode>() {
        });
        update(treeNode, id, name, 1);
        System.out.println(result);
        String string = JSON.toJSONString(treeNode);
        userDir.setUserDir(string);
        List<Files> list = fService.getList(userid, url, result, name);
        for (Files file : list) {
            String id1 = file.getId();
            Files files = new Files();
            files.setId(id1);
            files.setSize(file.getSize());
            files.setFileDir(file.getFileDir());
            fService.updateById(files);
        }
        return uService.setUserDir(userDir) > 0 ? R.ok().data("updateOk", treeNode) : R.error();
    }


    public void insert(TreeNode treeNode, long id, TreeNode newNode) {
        List<TreeNode> tList = treeNode.getChildrenList();
        List<String> sList = new ArrayList<>();
        for (TreeNode node : tList) {
            sList.add(node.getName());
        }
        sList.add(newNode.getName());
        System.out.println(sList);
        HashSet<String> hashSet = new HashSet<>(sList);
        if (hashSet.size() == sList.size()) {
            throw new CloudException(R.ERROR, R.NAME_REPEAT);
        }
        if (id == treeNode.getId()) {
            treeNode.getChildrenList().add(newNode);
            return;
        }
        if (tList.isEmpty()) {
            return;
        }
        for (TreeNode node : tList) {
            insert(node, id, newNode);
        }
    }


    public void delete(TreeNode treeNode, long id, StringBuilder stringBuilder) {
        stringBuilder.append("/").append(treeNode.getId());
        List<TreeNode> list = treeNode.getChildrenList();
        if (null == list || list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            if (id == list.get(i).getId()) {
                list.remove(i);
                delete(new TreeNode(), id, stringBuilder);
                break;
            }
            delete(list.get(i), id, stringBuilder);
        }
    }


    public void update(TreeNode treeNode, long id, String name, int d) {
        if (treeNode.getId() == id) {
            treeNode.setName(name + "/");
            result = d;
            return;
        }
        List<TreeNode> list = treeNode.getChildrenList();
        if (null == list || list.isEmpty()) {
            return;
        }
        for (TreeNode node : list) {
            update(node, id, name, d++);
        }
    }
}

