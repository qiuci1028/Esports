package com.esports.bigdata.module.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "大屏核心指标区")
public class OverviewDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "总比赛量")
    private Long totalMatches;

    @Schema(description = "环比")
    private BigDecimal matchGrowth;

    @Schema(description = "活跃玩家")
    private Long activePlayers;

    @Schema(description = "环比")
    private BigDecimal playerGrowth;

    @Schema(description = "全区平均段位等级（数值化）")
    private BigDecimal avgRankLevel;

    @Schema(description = "段位名称")
    private String avgRankName;

    @Schema(description = "环比")
    private BigDecimal rankGrowth;

    @Schema(description = "实时在线人数（基于最近 5 分钟活跃玩家）")
    private Integer onlineNow;
}
