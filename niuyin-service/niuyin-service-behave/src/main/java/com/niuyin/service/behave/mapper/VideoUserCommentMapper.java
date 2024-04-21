package com.niuyin.service.behave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.behave.domain.VideoUserComment;
import com.niuyin.model.behave.dto.VideoCommentReplayPageDTO;
import com.niuyin.model.behave.dto.VideoUserCommentPageDTO;
import com.niuyin.model.behave.vo.app.AppVideoUserCommentParentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (VideoUserComment)表数据库访问层
 *
 * @author roydon
 * @since 2023-10-30 16:52:51
 */
@Mapper
public interface VideoUserCommentMapper extends BaseMapper<VideoUserComment> {

    /**
     * 分页视频父评论
     *
     * @param pageDTO
     * @return
     */
    List<AppVideoUserCommentParentVO> selectCommentParentPage(VideoUserCommentPageDTO pageDTO);

    List<AppVideoUserCommentParentVO> selectCommentParentPageOrderByCreateTime(VideoUserCommentPageDTO pageDTO);

    List<AppVideoUserCommentParentVO> selectCommentParentPageOrderByLikeNum(VideoUserCommentPageDTO pageDTO);

    List<VideoUserComment> selectCommentReplayPageByOriginId(VideoCommentReplayPageDTO pageDTO);

    Long selectCommentReplayPageCountByOriginId(VideoCommentReplayPageDTO pageDTO);

    /**
     * 获取用户评论视频记录
     *
     * @param userId
     * @return
     */
    List<String> queryUserCommentVideoIdsRecord(Long userId);
}

