package com.hhd.utils;

import cn.hutool.core.date.DateTime;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hhd.pojo.domain.Admin;
import com.hhd.pojo.domain.UCenter;
import wiremock.org.apache.commons.lang3.time.DateUtils;

/**
 * @author -无心
 * @date 2023/2/19 22:20:50
 */
public class JwtUtils {
    public static String getJwtToken(UCenter uCenter) {
        return JWT.create().withAudience(uCenter.getId())
                .withClaim("userId", uCenter.getId())
                .withExpiresAt(DateUtils.addHours(new DateTime(), 2))
                .sign(Algorithm.HMAC256(uCenter.getPassword()));
    }

    public static String getJwtToken(Admin admin) {
        return JWT.create().withAudience(admin.getAid())
                .withClaim("userId", admin.getAid())
                .withExpiresAt(DateUtils.addHours(new DateTime(), 2))
                .sign(Algorithm.HMAC256(admin.getPassword()));
    }
}
