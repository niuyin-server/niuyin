package com.qiniu.service.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiniu.model.user.domain.UserSensitive;
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

