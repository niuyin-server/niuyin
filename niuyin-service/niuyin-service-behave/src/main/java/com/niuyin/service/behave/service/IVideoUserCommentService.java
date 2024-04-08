package com.niuyin.service.behave.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.behave.domain.VideoUserComment;
import com.niuyin.model.behave.dto.VideoCommentReplayPageDTO;
import com.niuyin.model.behave.dto.VideoUserCommentPageDTO;

import java.util.List;

/**
 * (VideoUserComment)表服务接口
 *
 * @author roydon
 * @since 2023-10-30 16:52:53
 */
public interface IVideoUserCommentService extends IService<VideoUserComment> {

    /**
     * 回复评论
     *
     * @param videoUserComment
     * @return
     */
    boolean replay(VideoUserComment videoUserComment);

    /**
     * 用户删除自己的评论
     *
     * @param commentId
     * @return
     */
    boolean delCommentByUser(Long commentId);

    /**
     * 分页根据视频id获取评论根id
     *
     * @param pageDTO
     * @return
     */
    IPage<VideoUserComment> getRootListByVideoId(VideoUserCommentPageDTO pageDTO);

    /**
     * 获取子评论
     *
     * @param commentId
     * @return
     */
    List<VideoUserComment> getChildren(Long commentId);

    /**
     * 查找指定视频评论量
     *
     * @param videoId
     * @return
     */
    Long queryCommentCountByVideoId(String videoId);

    /**
     * 分页查询评论树
     *
     * @param pageDTO
     * @return
     * @throws InterruptedException
     */
    PageDataInfo getCommentPageTree(VideoUserCommentPageDTO pageDTO);

    /**
     * 删除视频所有评论
     *
     * @param videoId
     * @return
     */
    boolean removeCommentByVideoId(String videoId);

    /**
     * 分页视频父评论
     *
     * @param pageDTO
     * @return
     */
    PageDataInfo getCommentParentPage(VideoUserCommentPageDTO pageDTO);

    /**
     * 评论视频
     *
     * @param videoUserComment
     * @return
     */
    boolean commentVideo(VideoUserComment videoUserComment);

    /**
     * 视频评论回复分页
     */
    PageDataInfo getCommentReplyPage(VideoCommentReplayPageDTO pageDTO);

}
