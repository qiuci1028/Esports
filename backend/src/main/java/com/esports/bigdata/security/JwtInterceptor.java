package com.esports.bigdata.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器：从 Header 中读取 X-Token，校验后把用户信息塞到 Request Attribute
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    public static final String ATTR_USER_ID = "currentUserId";
    public static final String ATTR_USERNAME = "currentUsername";
    public static final String ATTR_ROLE = "currentRole";

    private final JwtUtil jwtUtil;

    @Value("${jwt.header:X-Token}")
    private String headerName;

    @Value("${jwt.prefix:Bearer }")
    private String prefix;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // CORS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader(headerName);
        if (StringUtils.hasText(token) && token.startsWith(prefix)) {
            token = token.substring(prefix.length()).trim();
        }
        if (!StringUtils.hasText(token)) {
            // 无 Token：不强制拦截（开放接口可直接放行），需鉴权接口用 @Auth 注解控制
            return true;
        }
        if (jwtUtil.isExpired(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"token expired\"}");
            return false;
        }
        request.setAttribute(ATTR_USER_ID, jwtUtil.getUserId(token));
        request.setAttribute(ATTR_USERNAME, jwtUtil.getUsername(token));
        request.setAttribute(ATTR_ROLE, jwtUtil.getRole(token));
        return true;
    }
}
