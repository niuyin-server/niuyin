package com.qiniu.service.social.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiniu.model.social.UserFollow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户关注表(UserFollow)表数据库访问层
 *
 * @author roydon
 * @since 2023-10-30 15:54:19
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow>{

}

