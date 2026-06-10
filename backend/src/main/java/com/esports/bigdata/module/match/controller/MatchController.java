package com.esports.bigdata.module.match.controller;

import com.esports.bigdata.common.Result;
import com.esports.bigdata.common.page.PageResult;
import com.esports.bigdata.module.match.dto.MatchDetailDTO;
import com.esports.bigdata.module.match.dto.MatchListVO;
import com.esports.bigdata.module.match.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "比赛模块", description = "比赛列表、比赛详情、双方对比")
@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @Operation(summary = "比赛分页列表")
    @GetMapping("/page")
    public Result<PageResult<MatchListVO>> page(
            @RequestParam(required = false) String game,
            @RequestParam(required = false) String gameMode,
            @RequestParam(required = false) String patch,
            @RequestParam(required = false, defaultValue = "1") Long pageNum,
            @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        return Result.ok(matchService.page(game, gameMode, patch, pageNum, pageSize));
    }

    @Operation(summary = "比赛详情（双方对比 + 队伍汇总）")
    @GetMapping("/detail/{matchId}")
    public Result<MatchDetailDTO> detail(@PathVariable String matchId) {
        return Result.ok(matchService.detail(matchId));
    }
}
