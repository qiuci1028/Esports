package com.esports.bigdata.module.player.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "玩家详情聚合")
public class PlayerDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "基础信息")
    private PlayerBasicVO basic;

    @Schema(description = "当前段位")
    private RankVO currentRank;

    @Schema(description = "近 3 赛季段位走势")
    private List<RankHistoryVO> rankHistory;

    @Schema(description = "近 20 场 KDA")
    private List<KdaVO> kdaList;

    @Schema(description = "英雄熟练度 TOP5")
    private List<MasteryVO> topMastery;

    @Schema(description = "活跃时段分布（24h）")
    private List<ActiveHourVO> activeHour;

    @Schema(description = "综合胜率")
    private BigDecimal winRate;

    @Schema(description = "主玩位置")
    private String mainRole;

    @Schema(description = "英雄池广度")
    private Integer heroPoolSize;

    @Data
    public static class PlayerBasicVO implements Serializable {
        private Long id;
        private String game;
        private String puuid;
        private String summonerName;
        private String region;
        private Integer level;
        private String profileIconUrl;
        private LocalDateTime lastActive;
    }

    @Data
    public static class RankVO implements Serializable {
        private String tier;
        private String rankTier;
        private Integer leaguePoints;
        private Integer wins;
        private Integer losses;
    }

    @Data
    public static class RankHistoryVO implements Serializable {
        private String tier;
        private String rankTier;
        private Integer leaguePoints;
        private String snapshotDate;
    }

    @Data
    public static class KdaVO implements Serializable {
        private String matchId;
        private String championId;
        private Integer kills;
        private Integer deaths;
        private Integer assists;
        private Integer win;
    }

    @Data
    public static class MasteryVO implements Serializable {
        private String championId;
        private String championName;
        private Integer masteryPoints;
        private Integer masteryLevel;
    }

    @Data
    public static class ActiveHourVO implements Serializable {
        private Integer hour;
        private Integer gameCount;
    }
}
