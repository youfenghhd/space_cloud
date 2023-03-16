package com.hhd.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "用户对象")
public class UCenter implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "头像")
    private String portrait;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "modified_time", fill = FieldFill.INSERT_UPDATE)
    private String modifiedTime;

    @ApiModelProperty(value = "内存")
    @TableField(fill = FieldFill.INSERT)
    private Long memory;

    @ApiModelProperty(value = "禁用/启用状态")
    private Boolean status;

    @ApiModelProperty(value = "下载地址")
    @TableField(value = "download_add")
    private String downLoadAdd;

    /**
     * vip	null表示普通用户	有date_time表示vip用户	noew_time>=date_time表示
     */
    @ApiModelProperty(value = "vip用户")
    @TableField(value = "vip_time", fill = FieldFill.DEFAULT)
    private String vipTime;
    /**
     * 逻辑删除	null表示正常	有date_time表示逻辑删除	now_time-date_time>30day表示真实删除（可设置数据库事件定时清理date_time>30day的记录）
     */
    @ApiModelProperty(value = "逻辑删除")
    @TableLogic(value = "null")
    @TableField(value = "logic_del_time", fill = FieldFill.DEFAULT)
    private String logicDelTime;

    @ApiModelProperty(value = "图片验证码")
    @TableField(exist = false)
    private String code;

}
