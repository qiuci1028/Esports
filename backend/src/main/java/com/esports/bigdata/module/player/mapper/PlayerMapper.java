package com.esports.bigdata.module.player.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esports.bigdata.module.player.entity.Player;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlayerMapper extends BaseMapper<Player> {
}
