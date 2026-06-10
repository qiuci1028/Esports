package com.esports.bigdata.module.player.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "玩家查询条件")
public class PlayerQueryDTO {

    @Schema(description = "游戏：LOL/VALORANT/TFT")
    private String game;

    @Schema(description = "大区：kr/na1/euw1 ...")
    private String region;

    @Schema(description = "召唤师名（模糊）")
    private String summonerName;

    @Schema(description = "当前页")
    private Long pageNum = 1L;

    @Schema(description = "每页大小")
    private Long pageSize = 10L;
}
