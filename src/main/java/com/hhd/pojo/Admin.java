package com.hhd.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员id
     */
    @TableId(value = "aid", type = IdType.ASSIGN_ID)
    private String aid;

    /**
     * 管理员名字
     */
    private String adminname;

    /**
     * 管理员登录名
     */
    private String loginname;

    /**
     * 管理员密码
     */
    private String password;


}
