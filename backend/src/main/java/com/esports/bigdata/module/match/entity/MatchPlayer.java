package com.esports.bigdata.module.match.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("fact_match_player")
@Schema(description = "比赛玩家事实")
public class MatchPlayer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String matchId;
    private Long playerId;
    private String puuid;
    private String teamSide;
    private String championId;
    private String agentId;
    private String rolePosition;
    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private Integer goldEarned;
    private Integer damageDealt;
    private Integer visionScore;
    private Integer win;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
