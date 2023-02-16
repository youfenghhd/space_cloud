package com.hhd.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author -无心
 * @date 2023/2/16 16:23:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudException extends RuntimeException {
    private Integer code;
    private String msg;
}
