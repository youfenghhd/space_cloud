package com.hhd.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.pojo.domain.Admin;
import com.hhd.pojo.domain.UCenter;
import com.hhd.service.IAdminService;
import com.hhd.service.IUCenterService;
import com.hhd.utils.PassToken;
import com.hhd.utils.R;
import com.hhd.utils.ConfirmToken;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author -无心
 * @date 2023/2/24 13:47:52
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private IUCenterService uService;
    @Autowired
    private IAdminService aService;
    private static final String OPTIONS = "OPTIONS";

//    @Cacheable(value = "token", unless = "#result==null", key = "#request.getHeader(\"Authorization\")")
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse httpServletResponse, @NotNull Object object) {
        //浏览器在发送正式的请求时会先发送options类型的请求试探
        //放行该请求
        if (OPTIONS.equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        //设置允许跨域访问
        //jwt要设置跨域，不然拿不到请求头里的token
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Authorization,"
                + " Content-Type, Accept, Connection, User-Agent, Cookie,token");
        // 从 http 请求头中取出 token
        String token = request.getHeader("Authorization");
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(ConfirmToken.class)) {
            ConfirmToken confirmToken = method.getAnnotation(ConfirmToken.class);
            if (confirmToken.required()) {
                // 执行认证
                if (token == null) {
                    throw new CloudException(R.ERROR, R.NOT_LOGGED);
                }
                // 获取 token 中的 user id
                String Id;
                try {
                    Id = JWT.decode(token).getAudience().get(0);
                } catch (JWTDecodeException j) {
                    throw new CloudException(R.ERROR, R.USER_WRONGFUL);
                }
                UCenter user = uService.getById(Id);
                Admin admin = aService.getById(Id);
                if (user == null && admin == null) {

                    throw new CloudException(R.ERROR, R.USER_NON_EXISTENT);
                }
                // 验证 token
                try {
                    JWTVerifier jwtVerifier;
                    if (user != null) {
                        jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
                    } else {
                        jwtVerifier = JWT.require(Algorithm.HMAC256(admin.getPassword())).build();
                    }
                    jwtVerifier.verify(token);
                    System.out.println("token验证完毕");
                } catch (JWTVerificationException e) {
                    e.printStackTrace();
                    throw new CloudException(R.ERROR, R.LOGIN_WRONGFUL);
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
