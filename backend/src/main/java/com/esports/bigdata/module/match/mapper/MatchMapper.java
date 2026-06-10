package com.esports.bigdata.module.match.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esports.bigdata.module.match.entity.Match;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MatchMapper extends BaseMapper<Match> {
}
