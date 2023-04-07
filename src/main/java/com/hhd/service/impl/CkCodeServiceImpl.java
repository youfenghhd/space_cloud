package com.hhd.service.impl;

import com.hhd.exceptionhandler.CloudException;
import com.hhd.service.ICkCodeService;
import com.hhd.utils.CheckCodeUtils;
import com.hhd.utils.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author -无心
 * @date 2023/2/19 0:38:22
 */
@Service
public class CkCodeServiceImpl implements ICkCodeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public byte[] generate() {
        Map.Entry<String, byte[]> entry;
        try {
            entry = CheckCodeUtils.generateJpg().entrySet().iterator().next();
        } catch (Exception e) {
            throw new CloudException(Results.ERROR, Results.CHECK_IO_ERR);
        }
        redisTemplate.opsForValue().set("checkCode", entry.getKey());
        return entry.getValue();
    }
}
