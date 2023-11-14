package com.niuyin.service.behave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.model.behave.domain.VideoUserComment;
import com.niuyin.model.behave.dto.VideoUserCommentPageDTO;

import com.niuyin.service.behave.enums.VideoCommentStatus;
import com.niuyin.service.behave.mapper.VideoUserCommentMapper;
import com.niuyin.service.behave.service.IVideoUserCommentService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * (VideoUserComment)表服务实现类
 *
 * @author roydon
 * @since 2023-10-30 16:52:53
 */
@Service("videoUserCommentService")
public class VideoUserCommentServiceImpl extends ServiceImpl<VideoUserCommentMapper, VideoUserComment> implements IVideoUserCommentService {
    @Resource
    private VideoUserCommentMapper videoUserCommentMapper;

    /**
     * 回复评论
     *
     * @param videoUserComment
     * @return
     */
    @Override
    public boolean replay(VideoUserComment videoUserComment) {
        videoUserComment.setCreateTime(LocalDateTime.now());
        // 前端需要携带parentId
        videoUserComment.setParentId(videoUserComment.getParentId());
        videoUserComment.setOriginId(videoUserComment.getOriginId());
        videoUserComment.setUserId(UserContext.getUser().getUserId());
        return this.save(videoUserComment);
    }

    /**
     * 用户删除自己的评论
     *
     * @param commentId
     * @return
     */
    @Override
    public boolean delCommentByUser(Long commentId) {
        Long userId = UserContext.getUser().getUserId();
        LambdaUpdateWrapper<VideoUserComment> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(VideoUserComment::getUserId, userId);
        queryWrapper.eq(VideoUserComment::getCommentId, commentId);
        queryWrapper.set(VideoUserComment::getStatus, VideoCommentStatus.DELETED.getCode());
        // 隐式删除
        boolean update = update(queryWrapper);
        // 异步删除子评论
        deleteOriginChildren(commentId);
        return update;
    }

    /**
     * 批量删除祖先评论下的所有子评论
     */
    @Async
    public void deleteOriginChildren(Long commentId) {
        // 先查出此评论
        VideoUserComment byId = this.getById(commentId);
        if (byId.getParentId() == 0 && byId.getOriginId() == 0) {
            // 该评论为顶级评论，删除其子评论
            LambdaUpdateWrapper<VideoUserComment> queryWrapper = new LambdaUpdateWrapper<>();
            queryWrapper.eq(VideoUserComment::getOriginId, commentId);
            this.remove(queryWrapper);
        }
    }

    /**
     * 分页根据视频id获取评论根id
     *
     * @param pageDTO
     * @return
     */
    @Override
    public IPage<VideoUserComment> getRootListByVideoId(VideoUserCommentPageDTO pageDTO) {
        LambdaQueryWrapper<VideoUserComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserComment::getVideoId, pageDTO.getVideoId());
        queryWrapper.eq(VideoUserComment::getParentId, 0);
        queryWrapper.eq(VideoUserComment::getStatus, VideoCommentStatus.NORMAL.getCode());
        return this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), queryWrapper);
    }

    /**
     * 获取子评论
     *
     * @param commentId
     * @return
     */
    @Override
    public List<VideoUserComment> getChildren(Long commentId) {
        LambdaQueryWrapper<VideoUserComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserComment::getOriginId, commentId);
        queryWrapper.eq(VideoUserComment::getStatus, VideoCommentStatus.NORMAL.getCode());
        return list(queryWrapper);
    }

    /**
     * 查找指定视频评论量 todo 对逻辑删除过的子评论进行过滤
     *
     * @param videoId
     * @return
     */
    @Override
    public Long queryCommentCountByVideoId(String videoId) {
        LambdaQueryWrapper<VideoUserComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserComment::getVideoId, videoId);
        queryWrapper.eq(VideoUserComment::getStatus, VideoCommentStatus.NORMAL.getCode());
        return this.count(queryWrapper);
    }
}
