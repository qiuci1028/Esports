package com.esports.bigdata.module.player.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@TableName("fact_ranking_snapshot")
@Schema(description = "段位快照")
public class RankingSnapshot implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long playerId;
    private String puuid;
    private String game;
    private String queueType;
    private String tier;
    @TableField("rank_tier")
    private String rankTier;
    private Integer leaguePoints;
    private Integer wins;
    private Integer losses;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate snapshotDate;
}
