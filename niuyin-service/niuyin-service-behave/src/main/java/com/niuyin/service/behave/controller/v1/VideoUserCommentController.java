package com.niuyin.service.behave.controller.v1;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.model.behave.domain.VideoUserComment;
import com.niuyin.model.behave.dto.VideoUserCommentPageDTO;
import com.niuyin.model.behave.vo.VideoUserCommentVO;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.enums.NoticeType;
import com.niuyin.model.notice.enums.ReceiveFlag;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.behave.mapper.VideoUserLikeMapper;
import com.niuyin.service.behave.service.IVideoUserCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_CREATE_ROUTING_KEY;
import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_DIRECT_EXCHANGE;

/**
 * (VideoUserComment)表控制层
 *
 * @author roydon
 * @since 2023-10-30 16:52:51
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/comment")
public class VideoUserCommentController {

    @Resource
    private IVideoUserCommentService videoUserCommentService;

    @Resource
    private RemoteMemberService remoteMemberService;

    @Resource
    private RedisService redisService;

    @Resource
    private VideoUserLikeMapper videoUserLikeMapper;

    @Resource
    RabbitTemplate rabbitTemplate;

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
//        List<VideoUserCommentVO> voList = new ArrayList<>();
//        rootRecords.forEach(r -> {
//            // 获取用户详情
//            VideoUserCommentVO appNewsCommentVO = BeanCopyUtils.copyBean(r, VideoUserCommentVO.class);
//            Long userId = r.getUserId();
//            // 先走redis，有就直接返回
//            Member userCache = redisService.getCacheObject("member:userinfo:" + userId);
//            if (StringUtils.isNotNull(userCache)) {
//                appNewsCommentVO.setNickName(userCache.getNickName());
//                appNewsCommentVO.setAvatar(userCache.getAvatar());
//            } else {
//                Member user = remoteMemberService.userInfoById(userId).getData();
//                if (StringUtils.isNotNull(user)) {
//                    appNewsCommentVO.setNickName(user.getNickName());
//                    appNewsCommentVO.setAvatar(user.getAvatar());
//                }
//            }
//            Long commentId = r.getCommentId();
//            List<VideoUserComment> children = this.videoUserCommentService.getChildren(commentId);
//            List<VideoUserCommentVO> childrenVOS = BeanCopyUtils.copyBeanList(children, VideoUserCommentVO.class);
//            childrenVOS.forEach(c -> {
//                Member userCache2 = redisService.getCacheObject("member:userinfo:" + c.getUserId());
//                if (StringUtils.isNotNull(userCache2)) {
//                    c.setNickName(userCache2.getNickName());
//                    c.setAvatar(userCache2.getAvatar());
//                } else {
//                    Member cUser = remoteMemberService.userInfoById(c.getUserId()).getData();
//                    if (StringUtils.isNotNull(cUser)) {
//                        c.setNickName(cUser.getNickName());
//                        c.setAvatar(cUser.getAvatar());
//                    }
//                }
//                if (!c.getParentId().equals(commentId)) {
//                    // 回复了回复
//                    VideoUserComment byId = this.videoUserCommentService.getById(c.getParentId());
//                    c.setReplayUserId(byId.getUserId());
//                    Member userCache3 = redisService.getCacheObject("member:userinfo:" + byId.getUserId());
//                    if (StringUtils.isNotNull(userCache2)) {
//                        c.setReplayUserNickName(userCache3.getNickName());
//                    } else {
//                        Member byUser = remoteMemberService.userInfoById(byId.getUserId()).getData();
//                        if (StringUtils.isNotNull(byUser)) {
//                            c.setReplayUserNickName(byUser.getNickName());
//                        }
//                    }
//                }
//            });
//            appNewsCommentVO.setChildren(childrenVOS);
//            voList.add(appNewsCommentVO);
//        });


        List<VideoUserCommentVO> voList = new ArrayList<>();
        List<CompletableFuture<Void>> futures = rootRecords.stream()
                .map(r -> CompletableFuture.runAsync(() -> {
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
                    List<CompletableFuture<Void>> futures1 = childrenVOS.stream()
                            .map(c -> CompletableFuture.runAsync(() -> {
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
                            })).collect(Collectors.toList());
                    CompletableFuture.allOf(futures1.toArray(new CompletableFuture[0])).join();

                    appNewsCommentVO.setChildren(childrenVOS);
                    voList.add(appNewsCommentVO);
                })).collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();


        return PageDataInfo.genPageData(voList, iPage.getTotal());
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
        sendNotice2MQ(videoUserComment.getVideoId(), videoUserComment.getContent(), UserContext.getUser().getUserId());
        return R.ok(this.videoUserCommentService.save(videoUserComment));
    }

    /**
     * 用户评论视频，通知mq
     *
     * @param videoId
     * @param operateUserId
     */
    private void sendNotice2MQ(String videoId, String content, Long operateUserId) {
        // 根据视频获取发布者id
        Video video = videoUserLikeMapper.selectVideoByVideoId(videoId);
        if (StringUtils.isNull(video)) {
            return;
        }
        if (operateUserId.equals(video.getUserId())) {
            return;
        }
        // 封装notice实体
        Notice notice = new Notice();
        notice.setOperateUserId(operateUserId);
        notice.setNoticeUserId(video.getUserId());
        notice.setVideoId(videoId);
        notice.setContent(content);
        notice.setRemark("评论了");
        notice.setNoticeType(NoticeType.COMMENT_ADD.getCode());
        notice.setReceiveFlag(ReceiveFlag.WAIT.getCode());
        notice.setCreateTime(LocalDateTime.now());
        // notice消息转换为json
        String msg = JSON.toJSONString(notice);
        rabbitTemplate.convertAndSend(NOTICE_DIRECT_EXCHANGE, NOTICE_CREATE_ROUTING_KEY, msg);
        log.debug(" ==> {} 发送了一条消息 ==> {}", NOTICE_DIRECT_EXCHANGE, msg);
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

    /**
     * 点赞评论接口
     */
    @GetMapping("/like/{commentId}")
    public R<Boolean> likeComment(@PathVariable("commentId") Long commentId) {

        return R.ok(true);
    }

}

