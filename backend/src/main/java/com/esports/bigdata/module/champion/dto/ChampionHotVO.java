package com.esports.bigdata.module.champion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "英雄热度趋势 VO")
public class ChampionHotVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String date;
    private String championId;
    private String name;
    private String nameCn;
    private BigDecimal pickRate;
    private BigDecimal winRate;
}
