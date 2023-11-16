package com.niuyin.service.behave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.model.behave.domain.VideoUserLike;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.dto.VideoPageDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 点赞表(VideoUserLike)表数据库访问层
 *
 * @author lzq
 * @since 2023-10-30 14:32:59
 */
@Mapper
public interface VideoUserLikeMapper extends BaseMapper<VideoUserLike> {
    /**
     * 查询用户的点赞列表
     *
     * @param userId
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<Video> selectPersonLikePage(Long userId, Integer pageSize, Integer pageNum);

    MemberInfo selectPersonLikeShowStatus(Long userId);

    /**
     * 通过videoId获取视频
     * @param videoId
     * @return
     */
    Video selectVideoByVideoId(String videoId);
}

