package com.hhd.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @TableField("user_id")
    private String userId;

    /**
     * 文件名
     */
    @TableField("filename")
    private String fileName;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("modified_time")
    private Date modifiedTime;

    /**
     * 是否收藏
     */
    private Integer collection;

    /**
     * 目录
     */
    @TableField("file_dir")
    private String fileDir;

    /**
     * 文件分类类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * md5值
     */
    private String md5;

    /**
     * 视频播放id
     */
    @TableField("video_id")
    private String videoId;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 逻辑删除	null表示正常	有date_time表示逻辑删除	now_time-date_time>30表示真是删除，设置数据库事件定时清理date_time>30day的记录
     */
//    @TableLogic(value = "null")
    @TableField(value = "logic_del_time", fill = FieldFill.DEFAULT)
    private Date logicDelTime;


}
