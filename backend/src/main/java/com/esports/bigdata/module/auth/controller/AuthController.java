package com.esports.bigdata.module.auth.controller;

import com.esports.bigdata.common.Result;
import com.esports.bigdata.module.auth.dto.LoginRequest;
import com.esports.bigdata.module.auth.dto.LoginResponse;
import com.esports.bigdata.module.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "鉴权模块", description = "登录、注册、当前用户")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "登录（默认账号 admin/123456，demo/123456）")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.ok(authService.login(req));
    }

    @Operation(summary = "当前用户信息（需带 X-Token）")
    @GetMapping("/me")
    public Result<LoginResponse.UserInfo> me() {
        return Result.ok(authService.me());
    }
}
