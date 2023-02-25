package com.hhd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhd.pojo.entity.Files;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
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
     * @return
     */

    @Update("update file set logic_del_time = null where id = #{id}")
    int logicNormalFile(@Param("id") String id);

    /**
     * 查询回收站所有（逻辑删除所有）
     *
     * @param nowTime 当前时间
     * @return 查询结果
     */
    @Select("select * from file where logic_del_time is not null and logic_del_time > #{nowTime} and user_id = #{userId}")
    List<Files> showRecoveryAll(String nowTime,String userId);

    /**
     * 真实删除
     * @param id
     * @return
     */
    @Delete("delete from file where id = #{id}")
    int delById(String id);

}
