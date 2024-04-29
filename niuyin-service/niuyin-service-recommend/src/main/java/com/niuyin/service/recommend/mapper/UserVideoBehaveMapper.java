package com.niuyin.service.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.behave.domain.UserVideoBehave;
import com.niuyin.model.recommend.modal.UserVideoScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户视频行为表(UserVideoBehave)表数据库访问层
 *
 * @author roydon
 * @since 2024-04-27 18:56:47
 */
@Mapper
public interface UserVideoBehaveMapper extends BaseMapper<UserVideoBehave> {

    @Select("select user_id, video_id, SUM(user_behave) AS score from user_video_behave GROUP BY user_id, video_id;")
    List<UserVideoScore> queryAllUserVideoBehave();

}

