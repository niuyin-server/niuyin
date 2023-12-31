package com.niuyin.service.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.member.domain.UserSensitive;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户敏感词信息表(UserSensitive)表数据库访问层
 *
 * @author roydon
 * @since 2023-10-29 20:41:17
 */
@Mapper
public interface UserSensitiveMapper extends BaseMapper<UserSensitive> {

}

