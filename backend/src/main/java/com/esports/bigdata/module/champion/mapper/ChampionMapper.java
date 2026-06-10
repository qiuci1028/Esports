package com.esports.bigdata.module.champion.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esports.bigdata.module.champion.entity.Champion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChampionMapper extends BaseMapper<Champion> {
}
