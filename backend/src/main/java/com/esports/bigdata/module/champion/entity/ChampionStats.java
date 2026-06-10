package com.esports.bigdata.module.champion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("ads_champion_stats_d")
@Schema(description = "英雄每日统计宽表")
public class ChampionStats implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dt;

    private String game;
    private String patchCode;
    private String role;
    private String championId;
    private Integer games;
    private Integer wins;
    private BigDecimal pickRate;
    private BigDecimal banRate;
    private BigDecimal winRate;

    private LocalDateTime createTime;
}
