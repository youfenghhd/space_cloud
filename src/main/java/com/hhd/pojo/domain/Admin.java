package com.hhd.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value = "管理员对象")
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "管理员id")
    @TableId(value = "aid", type = IdType.ASSIGN_ID)
    private String aid;

    @ApiModelProperty(value = "管理员名字")
    @TableField("adminname")
    private String adminName;

    @ApiModelProperty(value = "管理员登录名")
    @TableField("loginname")
    private String loginName;

    @ApiModelProperty(value = "头像")
    private String portrait;

    @ApiModelProperty(value = "管理员密码")
    private String password;

    @ApiModelProperty(value = "图片验证码")
    @TableField(exist = false)
    private String code;

}
