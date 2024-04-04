package com.niuyin.service.behave.controller.app;

import com.niuyin.common.domain.vo.PageDataInfo;
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
    public PageDataInfo queryTree(@Validated @RequestBody VideoUserCommentPageDTO pageDTO) {
        return videoUserCommentService.getCommentParentPage(pageDTO);
    }

}
