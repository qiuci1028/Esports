package com.esports.bigdata.module.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "英雄热度趋势数据点")
public class ChampionHotVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "日期 yyyy-MM-dd")
    private String date;

    @Schema(description = "英雄 ID")
    private String championId;

    @Schema(description = "英雄英文名")
    private String name;

    @Schema(description = "英雄中文名")
    private String nameCn;

    @Schema(description = "出场率")
    private BigDecimal pickRate;

    @Schema(description = "胜率")
    private BigDecimal winRate;
}