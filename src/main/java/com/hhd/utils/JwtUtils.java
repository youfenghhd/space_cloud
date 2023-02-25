package com.hhd.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hhd.pojo.domain.UCenter;

import java.util.Calendar;
import java.util.HashMap;

/**
 * @author -无心
 * @date 2023/2/19 22:20:50
 */
public class JwtUtils {
//    public static final String SELECT = "H!4H@3$Dh^d%AgYhG9qMp5Xs";

    public static String getJwtToken(UCenter uCenter) {
        HashMap<String, Object> headers = new HashMap<>();
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.MINUTE, 20);
        return JWT.create().withAudience(uCenter.getId())
                .withClaim("userId", uCenter.getId())
                .withExpiresAt(expires.getTime())
                .sign(Algorithm.HMAC256(uCenter.getPassword()));
    }
}
