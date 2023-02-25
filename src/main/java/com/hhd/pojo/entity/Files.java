package com.hhd.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
@ApiModel(value = "文件对象")
@TableName("file")
public class Files implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "文件地址")
    private String url;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "文件名")
    @TableField("filename")
    private String fileName;

    @ApiModelProperty(value = "文件类型")
    private String type;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "modified_time", fill = FieldFill.INSERT_UPDATE)
    private String modifiedTime;

    @ApiModelProperty(value = "是否收藏")
    private Integer collection;

    @ApiModelProperty(value = "目录")
    @TableField("file_dir")
    private String fileDir;

    @ApiModelProperty(value = "文件分类类型")
    @TableField("file_type")
    private String fileType;

    @ApiModelProperty(value = "md5值")
    private String md5;

    @ApiModelProperty(value = "视频播放id")
    @TableField("video_id")
    private String videoId;

    @ApiModelProperty(value = "文件大小")
    private Long size;

    /**
     * 逻辑删除	null表示正常	有date_time表示逻辑删除	now_time-date_time>30表示真是删除，设置数据库事件定时清理date_time>30day的记录
     */
    @ApiModelProperty(value = "逻辑删除")
    @TableLogic(value = "null")
    @TableField(value = "logic_del_time", fill = FieldFill.DEFAULT)
    private String logicDelTime;


}
