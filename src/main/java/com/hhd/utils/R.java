package com.hhd.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author -无心
 * @date 2023/2/16 16:05:42
 * 表现层和前端传输数据协议
 * 统一返回结果的类
 */
@Data
public class R implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String GLOBAL_ERR = "全局异常";
    public static final String EMPTY_ERROR = "不能为空";
    public static final String CHECK_ERROR = "验证码错误";
    public static final String SMS_ERR = "短信码错误";
    public static final String PHONE_EXIST = "手机号已注册";
    public static final String DISABLE_ERR = "账号被禁用";
    public static final String PHONE_NON_EXIST = "手机号未注册";
    public static final String CHECK_IO_ERR = "验证码生成错误";
    public static final String PASSWORD_ERR = "登录密码错误";
    public static final String EXECUTION_ERR = "执行异常";
    public static final String INTER_ERR = "网络异常";
    public static final String SEND_SMS_ERR = "短信发送失败";
    public static final String NAME_REPEAT = "名称重复";
    public static final Integer SUCCESS = 20000;
    public static final Integer ERROR = 20001;

    public static final Integer SAVE_OK = 20011;
    public static final Integer DELETE_OK = 20021;
    public static final Integer UPDATE_OK = 20031;
    public static final Integer GET_OK = 20041;

    public static final Integer SAVE_ERR = 20010;
    public static final Integer DELETE_ERR = 20020;
    public static final Integer UPDATE_ERR = 20030;
    public static final Integer GET_ERR = 20040;


    private Boolean success;
    private Integer status;
    private String message;

    private R() {

    }

    private Map<String, Object> data = new HashMap<String, Object>();

    public R data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public R data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }

    public static R ok() {
        R r = new R();
        r.setStatus(SUCCESS);
        r.setSuccess(true);
        r.setMessage("成功");
        return r;
    }

    public static R error() {
        R r = new R();
        r.setSuccess(false);
        r.setStatus(ERROR);
        r.setMessage("失败");
        return r;
    }

    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    public R code(Integer code) {
        this.setStatus(code);
        return this;
    }


}
