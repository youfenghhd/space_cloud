package com.hhd.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * <p>
 *
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("ucenter")
public class UCenter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String portrait;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 更新时间
     */
    @TableField(value = "modified_time", fill = FieldFill.INSERT_UPDATE)
    private String modifiedTime;

    /**
     * 内存
     */
    @TableField(fill = FieldFill.INSERT)
    private Long memory;

    /**
     * 1启用/0禁用
     */
    private Boolean status;

    /**
     * 下载地址
     */
    private String downLoadAdd;

    /**
     * 逻辑删除	null表示正常	有date_time表示逻辑删除	noew_tiame-date_time>30day表示真实删除，设置数据库事件定时清理date_time>30day的记录
     */
//    @TableLogic(value = "null")
    @TableField(value = "logic_del_time", fill = FieldFill.DEFAULT)
    private String logicDelTime;


    @TableField(exist = false)
    private String code;

}
