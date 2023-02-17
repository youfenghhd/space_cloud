package com.hhd.pojo.vo;

import lombok.Data;

/**
 * @author -无心
 * @date 2023/2/16 16:01:24
 */
@Data
public class Register {
    private String mobile;
    private String password;
    private String nickname;
    private String portrait;
    private String smsCode;
    private String checkCode;
}
