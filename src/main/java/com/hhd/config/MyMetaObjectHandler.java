package com.hhd.config;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;


/**
 * @author -无心
 * @date 2023/2/16 19:47:24
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", new DateTime().toString(), metaObject);
        this.setFieldValByName("modifiedTime", new DateTime().toString(), metaObject);
        this.setFieldValByName("memory", 0L, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("modifiedTime", new DateTime().toString(), metaObject);
    }

}
