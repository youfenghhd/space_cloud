package com.hhd.utils;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author -无心
 * @date 2023/2/16 16:05:42
 * 表现层和前端传输数据协议
 * 统一返回结果的类
 */
@Data
public class R {
    public static final String EMPTY_ERROR = "不能为空";
    public static final String CHECK_ERROR = "验证码错误";
    public static final String PHONE_EXIST = "手机号已存在";
    public static final Integer SUCCESS = 20000;
    public static final Integer ERROR = 20001;

    private Boolean success;
    private Integer code;
    private String message;

    private R(){

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

    public static R ok(){
        R r = new R();
        r.setCode(SUCCESS);
        r.setSuccess(true);
        r.setMessage("成功");
        return r;
    }

    public static R error() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ERROR);
        r.setMessage("失败");
        return r;
    }

    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    public R code(Integer code) {
        this.setCode(code);
        return this;
    }


}
