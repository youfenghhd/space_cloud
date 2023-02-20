package com.hhd.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author -无心
 * @date 2023/2/16 16:01:24
 */
@Data
public class Register implements Serializable {
    private static final long serialVersionUID = 1L;
    private String mobile;
    private String password;
    private String nickname;
    private String portrait;
    private String smsCode;
    private String checkCode;
}
