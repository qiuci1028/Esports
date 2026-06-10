package com.esports.bigdata.module.dashboard.controller;

import com.esports.bigdata.common.Result;
import com.esports.bigdata.module.champion.entity.ChampionStats;
import com.esports.bigdata.module.dashboard.dto.*;
import com.esports.bigdata.module.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "数据大屏", description = "大屏首页聚合数据")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "核心指标：总比赛/活跃玩家/平均段位/在线人数")
    @GetMapping("/overview")
    public Result<OverviewDTO> overview(@RequestParam(required = false, defaultValue = "LOL") String game) {
        return Result.ok(dashboardService.overview(game));
    }

    @Operation(summary = "全区段位人数分布")
    @GetMapping("/rank-distribution")
    public Result<List<RankDistributionDTO>> rankDistribution(
            @RequestParam(required = false, defaultValue = "LOL") String game) {
        return Result.ok(dashboardService.rankDistribution(game));
    }

    @Operation(summary = "实时上分榜 TOP N")
    @GetMapping("/top-rank")
    public Result<List<TopRankPlayerVO>> topRank(
            @RequestParam(required = false, defaultValue = "LOL") String game,
            @RequestParam(required = false, defaultValue = "10") Integer topN) {
        return Result.ok(dashboardService.topRank(game, topN));
    }

    @Operation(summary = "实时赛事流（最新 N 场）")
    @GetMapping("/realtime-matches")
    public Result<List<RealtimeMatchVO>> realtimeMatches(
            @RequestParam(required = false, defaultValue = "LOL") String game,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return Result.ok(dashboardService.realtimeMatches(game, limit));
    }

    @Operation(summary = "当前版本 T0 英雄 TOP N（pickRate 降序）")
    @GetMapping("/top-champions")
    public Result<List<ChampionStats>> topChampions(
            @RequestParam(required = false, defaultValue = "LOL") String game,
            @RequestParam(required = false) String patch,
            @RequestParam(required = false, defaultValue = "5") Integer topN) {
        return Result.ok(dashboardService.topChampionsByPatch(game, patch, topN));
    }

    @Operation(summary = "24 小时英雄热度趋势（按天聚合，按 pickRate 取 TOP N）")
    @GetMapping("/hot-trend")
    public Result<List<ChampionHotVO>> hotTrend(
            @RequestParam(required = false, defaultValue = "LOL") String game,
            @RequestParam(required = false) String patch,
            @RequestParam(required = false, defaultValue = "5") Integer topN) {
        return Result.ok(dashboardService.hotTrend(game, patch, topN));
    }
}
