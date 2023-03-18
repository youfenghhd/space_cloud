package com.hhd.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;


/**
 * @author -无心
 * @date 2023/2/16 16:01:24
 */
@Data
@ApiModel(value = "阿里沙箱实体类")
public class AliPay {
    private static final long serialVersionUID = 1L;
    /**
     * 订单号
     */
    private String traceNo;
    /**
     * 总金额
     */
    private Double totalAmount;
    /**
     * 主题
     */
    private String subject;
    /**
     * 阿里的流水号
     */
    private String alipayTraceNo;

}
