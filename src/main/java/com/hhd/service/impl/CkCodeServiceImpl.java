package com.hhd.service.impl;

import com.hhd.exceptionhandler.CloudException;
import com.hhd.service.ICkCodeService;
import com.hhd.utils.CheckCodeUtil;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author -无心
 * @date 2023/2/19 0:38:22
 */
@Service
public class CkCodeServiceImpl implements ICkCodeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String generate() {
        String generate;
        try {
            generate = CheckCodeUtil.generateJpg();
        } catch (Exception e) {
            throw new CloudException(R.ERROR, R.CHECK_IO_ERR);
        }
        redisTemplate.opsForValue().set("checkCode", generate);
        return generate;
    }
}
