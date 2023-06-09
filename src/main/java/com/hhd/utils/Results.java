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
public class Results implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String GLOBAL_ERR = "全局异常";
    public static final String EMPTY_ERROR = "不能为空";
    public static final String CHECK_ERROR = "验证码错误";
    public static final String SMS_ERR = "短信码错误";
    public static final String PHONE_EXIST = "手机号已注册";
    public static final String DISABLE_ERR = "账号被禁用,请联系管理员";
    public static final String OVERFLOW_MEMORY = "内存溢出";
    public static final String NON_REGISTER = "登录名未注册";
    public static final String CHECK_IO_ERR = "验证码生成错误";
    public static final String PASSWORD_ERR = "登录密码错误";
    public static final String EXECUTION_ERR = "执行异常";
    public static final String INTER_ERR = "网络异常";
    public static final String SEND_SMS_ERR = "短信发送失败";
    public static final String NAME_REPEAT = "名称重复";

    //    public static final String NOT_FOUND = "没有找到";
    public static final String DELETE_VA_ERR = "删除音/视频失败";
    public static final String NOT_LOGGED = "尚未登陆，无法操作";
    public static final String USER_WRONGFUL = "用户认证失败,请重新登陆";
    public static final String USER_NON_EXISTENT = "用户不存在,请重新登陆";
    public static final String LOGIN_WRONGFUL = "登录状态已过期,请重新登陆";
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

    private Results() {

    }

    private Map<String, Object> data = new HashMap<String, Object>();

    public Results data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public Results data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }

    public static Results ok() {
        Results results = new Results();
        results.setStatus(SUCCESS);
        results.setSuccess(true);
        results.setMessage("成功");
        return results;
    }

    public static Results error() {
        Results results = new Results();
        results.setSuccess(false);
        results.setStatus(ERROR);
        results.setMessage("失败");
        return results;
    }

    public Results message(String message) {
        this.setMessage(message);
        return this;
    }

    public Results code(Integer code) {
        this.setStatus(code);
        return this;
    }


}
