package com.esports.bigdata.module.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "段位分布 VO")
public class RankDistributionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tier;
    private Integer count;
    private Double percentage;
}
