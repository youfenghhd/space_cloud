package com.hhd.exceptionhandler;

import com.hhd.utils.Results;
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
    public Results error(Exception e) {
        System.out.println(e);
        return Results.error().message(Results.GLOBAL_ERR);
    }


    @ResponseBody
    @ExceptionHandler(CloudException.class)
    public Results error(CloudException c) {
        return Results.error().code(c.getCode()).message(c.getMsg());
    }

}
