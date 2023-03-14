package com.hhd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhd.pojo.domain.Admin;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 真实删除
     *
     * @param id 根据id真实删除
     * @return 删除结果
     */
    @Delete("delete from ucenter where id = #{id}")
    int delById(String id);

}
