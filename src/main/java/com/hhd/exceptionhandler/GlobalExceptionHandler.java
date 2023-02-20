package com.hhd.exceptionhandler;

import com.hhd.utils.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author -无心
 * @date 2023/2/16 16:16:34
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public R error(Exception e) {
        System.out.println(e);
        return R.error().message(R.GLOBAL_ERR);
    }


    @ResponseBody
    @ExceptionHandler(CloudException.class)
    public R error(CloudException c) {
        return R.error().code(c.getCode()).message(c.getMsg());
    }

}
