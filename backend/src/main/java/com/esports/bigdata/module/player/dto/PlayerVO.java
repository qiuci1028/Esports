package com.esports.bigdata.module.player.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "玩家列表 VO")
public class PlayerVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String game;
    private String puuid;
    private String summonerName;
    private String region;
    private Integer level;
    private String profileIconUrl;
}
