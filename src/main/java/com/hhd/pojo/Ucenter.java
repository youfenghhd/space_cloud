package com.hhd.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
public class Ucenter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String portrait;

    /**
     * 创建时间
     */
    private LocalDateTime create_time;

    /**
     * 更新时间
     */
    private LocalDateTime modified_time;

    /**
     * 内存
     */
    private Long Memory;

    /**
     * 1启用/0禁用
     */
    private Boolean status;

    /**
     * 逻辑删除	null表示正常	有date_time表示逻辑删除	noew_tiame-date_time>30day表示真实删除，设置数据库事件定时清理date_time>30day的记录
     */
    @TableLogic
    private LocalDateTime logic_del_time;


}
