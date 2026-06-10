package com.esports.bigdata.module.match.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esports.bigdata.module.match.entity.MatchPlayer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MatchPlayerMapper extends BaseMapper<MatchPlayer> {
}
