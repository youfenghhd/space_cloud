package com.hhd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhd.pojo.domain.UCenter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
public interface UcenterMapper extends BaseMapper<UCenter> {

    /**
     * 修改逻辑删除为正常
     *
     * @param id 根据传入id修改
     * @return 修改结果
     */

    @Update("update ucenter set logic_del_time = null where id = #{id}")
    int logicNormalUser(@Param("id") String id);

    /**
     * 改变用户状态
     *
     * @param id     根据id改
     * @param status 状态
     * @return 修改结果
     */
    @Update("update ucenter set status = #{status} where id = #{id}")
    boolean changeStatus(String id, boolean status);

    /**
     * 查询用户回收站所有（逻辑删除所有）
     *
     * @param nowTime 当前时间
     * @return 查询结果
     */
    @Select("select * from ucenter where logic_del_time is not null and logic_del_time > #{nowTime}")
    List<UCenter> showRecoveryAll(String nowTime);


    /**
     * 真实删除
     *
     * @param id 根据id真实删除
     * @return 删除结果
     */
    int delById(List<String> id);

    /**
     * 模糊查询
     *
     * @param uCenter 信息
     * @return 结果
     */
//    @Select("select * from ucenter where mobile like #{search} and logic_del_time is not null")
    List<UCenter> searchByFuzzyUsers(UCenter uCenter);

}
