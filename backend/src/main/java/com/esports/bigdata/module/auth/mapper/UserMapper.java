package com.esports.bigdata.module.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esports.bigdata.module.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
