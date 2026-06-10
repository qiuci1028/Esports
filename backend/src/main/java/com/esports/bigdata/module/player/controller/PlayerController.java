package com.esports.bigdata.module.player.controller;

import com.esports.bigdata.common.Result;
import com.esports.bigdata.common.page.PageResult;
import com.esports.bigdata.module.player.dto.PlayerDetailDTO;
import com.esports.bigdata.module.player.dto.PlayerQueryDTO;
import com.esports.bigdata.module.player.dto.PlayerVO;
import com.esports.bigdata.module.player.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "玩家模块", description = "玩家查询、详情、搜索")
@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @Operation(summary = "玩家分页查询")
    @GetMapping("/page")
    public Result<PageResult<PlayerVO>> page(PlayerQueryDTO q) {
        return Result.ok(playerService.page(q));
    }

    @Operation(summary = "玩家详情（聚合）")
    @GetMapping("/detail")
    public Result<PlayerDetailDTO> detail(
            @Parameter(description = "玩家 PUUID") @RequestParam String puuid,
            @Parameter(description = "游戏") @RequestParam(required = false) String game) {
        return Result.ok(playerService.detail(puuid, game));
    }

    @Operation(summary = "玩家名模糊搜索（autocomplete）")
    @GetMapping("/search")
    public Result<List<PlayerVO>> search(
            @Parameter(description = "关键字") @RequestParam String keyword,
            @Parameter(description = "返回条数") @RequestParam(required = false) Integer limit) {
        return Result.ok(playerService.search(keyword, limit));
    }
}
