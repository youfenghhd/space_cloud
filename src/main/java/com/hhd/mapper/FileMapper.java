package com.hhd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhd.pojo.entity.Files;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@Mapper
public interface FileMapper extends BaseMapper<Files> {
    /**
     * 修改逻辑删除
     *
     * @param id 根据传入id修改
     * @return 查询结果
     */

    @Update("update file set logic_del_time = null , file_dir = '/root' where id = #{id}")
    int logicNormalFile(@Param("id") String id);

    /**
     * 查询文件回收站所有（逻辑删除所有）
     *
     * @param nowTime 当前时间
     * @return 查询结果
     */
    @Select("select * from file where logic_del_time is not null and logic_del_time > #{nowTime}")
    List<Files> showRecoveryAllFiles(String nowTime);

    /**
     * 查询回收站所有（逻辑删除所有）
     *
     * @param userId  给出用户的回收站
     * @param nowTime 当前时间
     * @return 查询结果
     */
    @Select("select * from file where logic_del_time is not null and logic_del_time > #{nowTime} and user_id = #{userId}")
    List<Files> showRecoveryAll(String nowTime, String userId);

    /**
     * 找存在
     * @param id 按id
     * @return 结果
     */
    @Select("select * from file where id = #{id}")
    Files getOne(String id);

    /**
     * 查询同款md5
     *
     * @param md5 按md5值查询
     * @return 查询结果
     */
    @Select("select count(*) from file where md5 = #{md5}")
    int getCount(String md5);

    /**
     * 真实删除
     *
     * @param id
     * @return
     */
    @Delete("delete from file where id = #{id}")
    int delById(String id);


    /**
     * 模糊查询
     *
     * @param files 文件
     * @return 结果
     */
    List<Files> searchFuzzyFiles(Files files);




}
