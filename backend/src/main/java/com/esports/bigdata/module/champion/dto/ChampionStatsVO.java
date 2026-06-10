package com.esports.bigdata.module.champion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "英雄统计 VO")
public class ChampionStatsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String championId;
    private String name;
    private String nameCn;
    private String role;
    private String iconUrl;
    private Integer games;
    private Integer wins;
    private BigDecimal pickRate;
    private BigDecimal banRate;
    private BigDecimal winRate;
    private String patchCode;
}
