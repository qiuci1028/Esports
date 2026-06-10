package com.esports.bigdata.module.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "比赛列表 VO")
public class MatchListVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String matchId;
    private String game;
    private String gameMode;
    private String mapName;
    private String patchCode;
    private Integer durationSec;
    private String creationTime;
    private String winningTeam;
}
