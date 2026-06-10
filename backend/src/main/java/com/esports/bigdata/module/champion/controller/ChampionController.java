package com.esports.bigdata.module.champion.controller;

import com.esports.bigdata.common.Result;
import com.esports.bigdata.module.champion.dto.ChampionHotVO;
import com.esports.bigdata.module.champion.dto.ChampionStatsVO;
import com.esports.bigdata.module.champion.service.ChampionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "英雄/特工模块", description = "英雄 BP 胜率、热度趋势、详情")
@RestController
@RequestMapping("/champion")
@RequiredArgsConstructor
public class ChampionController {

    private final ChampionService championService;

    @Operation(summary = "英雄 BP 胜率列表（按版本/位置筛选）")
    @GetMapping("/list")
    public Result<List<ChampionStatsVO>> list(
            @RequestParam(required = false, defaultValue = "LOL") String game,
            @RequestParam(required = false) String patch,
            @RequestParam(required = false) String role,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        return Result.ok(championService.listByPatch(game, patch, role, limit));
    }

    @Operation(summary = "T0 热门英雄 TOP N")
    @GetMapping("/top")
    public Result<List<ChampionStatsVO>> top(
            @RequestParam(required = false, defaultValue = "LOL") String game,
            @RequestParam(required = false) String patch,
            @RequestParam(required = false, defaultValue = "5") Integer topN) {
        return Result.ok(championService.topTier(game, patch, topN));
    }

    @Operation(summary = "24h 英雄热度趋势（近 7 天）")
    @GetMapping("/hot-trend")
    public Result<List<ChampionHotVO>> hotTrend(
            @RequestParam(required = false, defaultValue = "LOL") String game,
            @RequestParam(required = false) String patch,
            @RequestParam(required = false, defaultValue = "5") Integer topN) {
        return Result.ok(championService.hotTrend(game, patch, topN));
    }

    @Operation(summary = "英雄详情")
    @GetMapping("/detail/{championId}")
    public Result<ChampionStatsVO> detail(
            @PathVariable String championId,
            @RequestParam(required = false, defaultValue = "LOL") String game,
            @RequestParam(required = false) String patch) {
        return Result.ok(championService.detail(championId, game, patch));
    }
}
