package com.esports.bigdata.module.player.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esports.bigdata.module.player.entity.RankingSnapshot;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RankingSnapshotMapper extends BaseMapper<RankingSnapshot> {
}
