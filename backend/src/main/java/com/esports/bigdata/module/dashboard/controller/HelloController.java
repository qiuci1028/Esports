package com.esports.bigdata.module.dashboard.controller;

import com.esports.bigdata.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "健康检查")
@RestController
@RequestMapping("/hello")
public class HelloController {

    @Operation(summary = "健康检查")
    @GetMapping
    public Result<Map<String, Object>> hello() {
        Map<String, Object> data = new HashMap<>();
        data.put("service", "esports-bigdata-backend");
        data.put("version", "1.0.0");
        data.put("ts", LocalDateTime.now().toString());
        return Result.ok(data);
    }
}
