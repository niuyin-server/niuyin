package com.qiniu.service.behave.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.video.domain.VideoUserComment;
import com.qiniu.model.video.dto.VideoUserCommentPageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
}
