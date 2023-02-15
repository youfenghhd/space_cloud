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
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 文件地址
     */
    private String url;

    /**
     * 用户id
     */
    private String user_id;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 创建时间
     */
    private LocalDateTime create_time;

    /**
     * 更新时间
     */
    private LocalDateTime modified_time;

    /**
     * 是否收藏
     */
    private Integer collection;

    /**
     * 目录
     */
    private String file_dir;

    /**
     * 文件分类类型
     */
    private String file_type;

    /**
     * md5值
     */
    private String md5;

    /**
     * 视频播放id
     */
    private String vidio_id;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 逻辑删除	null表示正常	有date_time表示逻辑删除	now_time-date_time>30表示真是删除，设置数据库事件定时清理date_time>30day的记录
     */
    @TableLogic
    private LocalDateTime logic_del_time;


}
