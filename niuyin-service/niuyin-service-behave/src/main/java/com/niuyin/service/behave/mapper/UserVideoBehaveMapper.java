package com.niuyin.service.behave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.behave.domain.UserVideoBehave;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户视频行为表(UserVideoBehave)表数据库访问层
 *
 * @author roydon
 * @since 2024-04-19 14:21:12
 */
@Mapper
public interface UserVideoBehaveMapper extends BaseMapper<UserVideoBehave> {

    /**
     * 存在则更新不存在则插入
     */
    boolean syncUserVideoBehave(UserVideoBehave userVideoBehave);
}
