package com.esports.bigdata.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 鉴权上下文：Controller/Service 层获取当前登录用户
 */
public class AuthContext {

    public static HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? null : attrs.getRequest();
    }

    public static Long currentUserId() {
        HttpServletRequest req = currentRequest();
        return req == null ? null : (Long) req.getAttribute(JwtInterceptor.ATTR_USER_ID);
    }

    public static String currentUsername() {
        HttpServletRequest req = currentRequest();
        return req == null ? null : (String) req.getAttribute(JwtInterceptor.ATTR_USERNAME);
    }

    public static String currentRole() {
        HttpServletRequest req = currentRequest();
        return req == null ? null : (String) req.getAttribute(JwtInterceptor.ATTR_ROLE);
    }
}
