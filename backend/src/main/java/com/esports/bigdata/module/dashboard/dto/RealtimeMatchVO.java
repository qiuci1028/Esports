package com.esports.bigdata.module.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "实时赛事流 VO")
public class RealtimeMatchVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String matchId;
    private String game;
    private String blueTeamName;
    private String redTeamName;
    private Integer blueScore;
    private Integer redScore;
    private String status;     // LIVE / FINISHED
    private String startTime;
}
