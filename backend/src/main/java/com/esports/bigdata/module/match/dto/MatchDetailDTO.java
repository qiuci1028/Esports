package com.esports.bigdata.module.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "比赛详情")
public class MatchDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "基础信息")
    private MatchBasicVO basic;

    @Schema(description = "蓝方队员")
    private List<TeamMemberVO> blueTeam;

    @Schema(description = "红方队员")
    private List<TeamMemberVO> redTeam;

    @Schema(description = "蓝方汇总")
    private TeamSummaryVO blueSummary;

    @Schema(description = "红方汇总")
    private TeamSummaryVO redSummary;

    @Data
    public static class MatchBasicVO implements Serializable {
        private String matchId;
        private String game;
        private String gameMode;
        private String mapName;
        private String patchCode;
        private Integer durationSec;
        private String creationTime;
        private String winningTeam;
    }

    @Data
    public static class TeamMemberVO implements Serializable {
        private String puuid;
        private String summonerName;
        private String teamSide;
        private String championId;
        private String championName;
        private String championIcon;
        private String rolePosition;
        private Integer kills;
        private Integer deaths;
        private Integer assists;
        private Integer goldEarned;
        private Integer damageDealt;
        private Integer visionScore;
        private Integer win;
    }

    @Data
    public static class TeamSummaryVO implements Serializable {
        private Integer totalKills;
        private Integer totalDeaths;
        private Integer totalAssists;
        private Integer totalGold;
        private Integer totalDamage;
        private Integer totalVision;
    }
}
