package com.niuyin.service.behave.controller.v1;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.VideoUserComment;
import com.niuyin.model.video.dto.VideoUserCommentPageDTO;
import com.niuyin.model.video.vo.VideoUserCommentVO;
import com.niuyin.service.behave.service.IVideoUserCommentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * (VideoUserComment)表控制层
 *
 * @author roydon
 * @since 2023-10-30 16:52:51
 */
@RestController
@RequestMapping("/api/v1/comment")
public class VideoUserCommentController {

    @Resource
    private IVideoUserCommentService videoUserCommentService;

    @Resource
    private RemoteMemberService remoteMemberService;

    @Resource
    private RedisService redisService;

    /**
     * 分页查询评论集合树
     */
    @PostMapping("/tree")
    public PageDataInfo queryTree(@RequestBody VideoUserCommentPageDTO pageDTO) {
        String newsId = pageDTO.getVideoId();
        if (StringUtil.isEmpty(newsId)) {
            R.ok();
        }
//        videoUserCommentService.generatorVideoCommentPageTree(VideoUserCommentPageDTO pageDTO);
        IPage<VideoUserComment> iPage = this.videoUserCommentService.getRootListByVideoId(pageDTO);
        List<VideoUserComment> rootRecords = iPage.getRecords();
        List<VideoUserCommentVO> voList = new ArrayList<>();
        rootRecords.forEach(r -> {
            // 获取用户详情
            VideoUserCommentVO appNewsCommentVO = BeanCopyUtils.copyBean(r, VideoUserCommentVO.class);
            Long userId = r.getUserId();
            // 先走redis，有就直接返回
            Member userCache = redisService.getCacheObject("member:userinfo:" + userId);
            if (StringUtils.isNotNull(userCache)) {
                appNewsCommentVO.setNickName(userCache.getNickName());
                appNewsCommentVO.setAvatar(userCache.getAvatar());
            } else {
                Member user = remoteMemberService.userInfoById(userId).getData();
                if (StringUtils.isNotNull(user)) {
                    appNewsCommentVO.setNickName(user.getNickName());
                    appNewsCommentVO.setAvatar(user.getAvatar());
                }
            }
            Long commentId = r.getCommentId();
            List<VideoUserComment> children = this.videoUserCommentService.getChildren(commentId);
            List<VideoUserCommentVO> childrenVOS = BeanCopyUtils.copyBeanList(children, VideoUserCommentVO.class);
            childrenVOS.forEach(c -> {
                Member userCache2 = redisService.getCacheObject("member:userinfo:" + c.getUserId());
                if (StringUtils.isNotNull(userCache2)) {
                    c.setNickName(userCache2.getNickName());
                    c.setAvatar(userCache2.getAvatar());
                } else {
                    Member cUser = remoteMemberService.userInfoById(c.getUserId()).getData();
                    if (StringUtils.isNotNull(cUser)) {
                        c.setNickName(cUser.getNickName());
                        c.setAvatar(cUser.getAvatar());
                    }
                }
                if (!c.getParentId().equals(commentId)) {
                    // 回复了回复
                    VideoUserComment byId = this.videoUserCommentService.getById(c.getParentId());
                    c.setReplayUserId(byId.getUserId());
                    Member userCache3 = redisService.getCacheObject("member:userinfo:" + byId.getUserId());
                    if (StringUtils.isNotNull(userCache2)) {
                        c.setReplayUserNickName(userCache3.getNickName());
                    } else {
                        Member byUser = remoteMemberService.userInfoById(byId.getUserId()).getData();
                        if (StringUtils.isNotNull(byUser)) {
                            c.setReplayUserNickName(byUser.getNickName());
                        }
                    }
                }
            });
            appNewsCommentVO.setChildren(childrenVOS);
            voList.add(appNewsCommentVO);
        });
        return new PageDataInfo(R.SUCCESS, "查询成功", voList, iPage.getTotal());
    }

    /**
     * 新增评论
     */
    @PostMapping
    public R<?> add(@RequestBody VideoUserComment videoUserComment) {
        if (StringUtils.isNull(videoUserComment.getContent()) || StringUtils.isBlank(videoUserComment.getContent())) {
            throw new CustomException(HttpCodeEnum.COMMENT_CONTENT_NULL);
        }
        videoUserComment.setCreateTime(LocalDateTime.now());
        videoUserComment.setUserId(UserContext.getUser().getUserId());
        return R.ok(this.videoUserCommentService.save(videoUserComment));
    }

    /**
     * 回复评论
     */
    @PostMapping("/replay")
    public R<?> replay(@RequestBody VideoUserComment videoUserComment) {
        return R.ok(this.videoUserCommentService.replay(videoUserComment));
    }

    /**
     * 删除数据
     */
    @DeleteMapping("{commentId}")
    public R<?> removeById(@PathVariable Long commentId) {
        return R.ok(this.videoUserCommentService.delCommentByUser(commentId));
    }

    /**
     * 获取视频评论数
     */
    @GetMapping("/{videoId}")
    public R<Long> getCommentCountByVideoId(@PathVariable("videoId") String videoId) {
        return R.ok(videoUserCommentService.queryCommentCountByVideoId(videoId));
    }
}

