package com.hhd.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.pojo.entity.Files;
import com.hhd.pojo.entity.UserDir;
import com.hhd.pojo.vo.TreeNode;
import com.hhd.service.IFileService;
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
@CrossOrigin
@Api(tags = "文件夹")
@RequestMapping("/dirs")
public class UserDirController {
    @Autowired
    private IUserDirService uService;
    @Autowired
    private IFileService fService;
    private int result = 1;

    @PassToken
    @Operation(summary = "获取当前文件夹")
    @Cacheable(cacheNames = "getFile", unless = "#result==null")
    @GetMapping("/{id}")
    public Results getFile(@PathVariable String id) {
        return Results.ok().data("dir", uService.getUserDir(id));
    }

    @ConfirmToken
    @Operation(summary = "根据传入的路径，名字，和父文件id新建文件夹")
    @CacheEvict(value = "getFile", allEntries = true)
    @PostMapping("/{userid}/{name}/{id}")
    public Results setDir(@PathVariable long id, @PathVariable String name, @PathVariable String userid) {
        UserDir userDir = uService.getUserDir(userid);
        TreeNode tNode = JSON.parseObject(userDir.getUserDir(), new TypeReference<TreeNode>() {
        });
        TreeNode tNode1 = new TreeNode();
        tNode1.setName(name + "/");
        tNode1.setParentId(id);
        tNode1.setChildrenList(new ArrayList<>());
        insert(tNode, id, tNode1);
        String string = JSON.toJSONString(tNode);
        userDir.setUserDir(string);
        return uService.setUserDir(userDir) > 0 ? Results.ok().data("dir", userDir) : Results.error();
    }

    @ConfirmToken
    @Operation(summary = "根据传入的userId、目录路径url、和父文件夹id的删除文件夹")
    @CacheEvict(value = "getFile", allEntries = true)
    @DeleteMapping("/{userid}/{id}")
    public Results delDir(@PathVariable String userid, @PathVariable long id, @RequestBody String url) {
        UserDir userDir = uService.getUserDir(userid);
        TreeNode treeNode = JSON.parseObject(userDir.getUserDir(), new TypeReference<TreeNode>() {
        });
        try {
            uService.deleteStruct(userid, url);
            StringBuilder stringBuilder = new StringBuilder();
            delete(treeNode, id, stringBuilder);
            userDir.setUserDir(JSON.toJSONString(treeNode));
            uService.setUserDir(userDir);
            return Results.ok();
        } catch (Exception e) {
            return Results.error();
        }
    }

    @ConfirmToken
    @Operation(summary = "根据传入的名字，userId，目录路径url，父文件id修改文件夹")
    @CacheEvict(value = "getFile", allEntries = true)
    @PutMapping("/{userid}/{name}/{id}")
    public Results updateDir(@PathVariable long id, @PathVariable String name,
                             @PathVariable String userid, @RequestBody String url) {
        UserDir userDir = uService.getUserDir(userid);
        TreeNode treeNode = JSON.parseObject(userDir.getUserDir(), new TypeReference<TreeNode>() {
        });
        update(treeNode, id, name, 1);
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
        return uService.setUserDir(userDir) > 0 ? Results.ok().data("updateOk", treeNode)
                : Results.error();
    }


    public void insert(TreeNode treeNode, long id, TreeNode newNode) {
        List<TreeNode> tList = treeNode.getChildrenList();
        List<String> sList = new ArrayList<>();
        for (TreeNode node : tList) {
            sList.add(node.getName());
        }
        sList.add(newNode.getName());
        HashSet<String> hashSet = new HashSet<>(sList);
        if (!(hashSet.size() == sList.size())) {
            throw new CloudException(Results.ERROR, Results.NAME_REPEAT);
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

