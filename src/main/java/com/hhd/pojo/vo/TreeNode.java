package com.hhd.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @author -无心
 * @date 2023/2/17 10:40:30
 */
@Data
public class TreeNode {
    private long id;
    private long parentId;
    private String name;
    private static int idCounter = 0;
    private List<TreeNode> childrenList;

    public TreeNode() {
        this.id = ++idCounter;
    }

    public TreeNode(String name, long parentId) {
        this.name = name;
        this.parentId = parentId;
        this.id = ++idCounter;
    }
}
