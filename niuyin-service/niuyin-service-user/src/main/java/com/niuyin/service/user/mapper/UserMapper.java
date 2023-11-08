package com.niuyin.service.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表(User)表数据库访问层
 *
 * @author roydon
 * @since 2023-10-24 19:18:24
 */
@Mapper
public interface UserMapper extends BaseMapper<User>{
}

