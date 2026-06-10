package com.esports.bigdata.module.auth.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esports.bigdata.common.ResultCode;
import com.esports.bigdata.common.exception.BusinessException;
import com.esports.bigdata.module.auth.dto.LoginRequest;
import com.esports.bigdata.module.auth.dto.LoginResponse;
import com.esports.bigdata.module.auth.entity.User;
import com.esports.bigdata.module.auth.mapper.UserMapper;
import com.esports.bigdata.security.AuthContext;
import com.esports.bigdata.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expire-hours}")
    private Long expireHours;

    public LoginResponse login(LoginRequest req) {
        User u = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (u == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在");
        }
        if (u.getStatus() != null && u.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已禁用");
        }
        if (!BCrypt.checkpw(req.getPassword(), u.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "密码错误");
        }
        String token = jwtUtil.generateToken(u.getId(), u.getUsername(), u.getRole());
        return new LoginResponse(
                token,
                expireHours,
                new LoginResponse.UserInfo(
                        u.getId(), u.getUsername(), u.getNickname(), u.getRole(), u.getAvatar()));
    }

    public LoginResponse.UserInfo me() {
        Long uid = AuthContext.currentUserId();
        if (uid == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        User u = userMapper.selectById(uid);
        if (u == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户");
        return new LoginResponse.UserInfo(
                u.getId(), u.getUsername(), u.getNickname(), u.getRole(), u.getAvatar());
    }
}
