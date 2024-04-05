package com.niuyin.service.behave.controller.app;

import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.behave.domain.VideoUserComment;
import com.niuyin.model.behave.dto.VideoCommentReplayPageDTO;
import com.niuyin.model.behave.dto.VideoUserCommentPageDTO;
import com.niuyin.service.behave.service.IVideoUserCommentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 视频评论
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/4
 **/
@RestController
@RequestMapping("/api/v1/app/comment")
public class AppVideoUserCommentController {

    @Resource
    private IVideoUserCommentService videoUserCommentService;

    /**
     * 获取视频父评论
     */
    @PostMapping("/parent")
    public PageDataInfo queryCommentParentPage(@Validated @RequestBody VideoUserCommentPageDTO pageDTO) {
        return videoUserCommentService.getCommentParentPage(pageDTO);
    }

    /**
     * 评论视频
     */
    @PostMapping
    public R<Boolean> commentVideo(@Validated @RequestBody VideoUserComment videoUserComment) {
        return R.ok(videoUserCommentService.commentVideo(videoUserComment));
    }

    /**
     * 分页评论回复
     */
        @PostMapping("/replyPage")
    public PageDataInfo queryCommentReplyPage(@Validated @RequestBody VideoCommentReplayPageDTO pageDTO) {
        return videoUserCommentService.getCommentReplyPage(pageDTO);
    }

}
