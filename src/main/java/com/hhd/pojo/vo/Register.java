package com.hhd.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author -无心
 * @date 2023/2/16 16:01:24
 */
@Data
@ApiModel(value = "注册对象")
public class Register implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像")
    private String portrait;

    @ApiModelProperty(value = "短信验证码")
    private String smsCode;

    @ApiModelProperty(value = "图片验证码")
    private String checkCode;
}
