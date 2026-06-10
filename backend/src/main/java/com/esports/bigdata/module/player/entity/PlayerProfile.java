package com.esports.bigdata.module.player.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ads_player_profile")
@Schema(description = "玩家画像宽表")
public class PlayerProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long playerId;
    private String puuid;
    private String game;
    private Integer totalGames;
    private Integer totalWins;
    private BigDecimal winRate;
    private BigDecimal avgKda;
    private String mainRole;
    private Integer heroPoolSize;
    private Integer activeHour;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdate;
}
