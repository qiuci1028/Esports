package com.esports.bigdata.module.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "实时上分榜 VO")
public class TopRankPlayerVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer rankNo;
    private String summonerName;
    private String tier;
    private String rankName;
    private Integer leaguePoints;
    private Integer wins;
    private Integer losses;
    private String profileIconUrl;
}
