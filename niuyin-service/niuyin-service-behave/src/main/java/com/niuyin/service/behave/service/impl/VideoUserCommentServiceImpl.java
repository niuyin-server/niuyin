package com.niuyin.service.behave.service.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.model.behave.domain.VideoUserComment;
import com.niuyin.model.behave.dto.VideoUserCommentPageDTO;
import com.niuyin.model.behave.vo.VideoUserCommentVO;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.enums.NoticeType;
import com.niuyin.model.notice.enums.ReceiveFlag;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.behave.enums.VideoCommentStatus;
import com.niuyin.service.behave.mapper.VideoUserCommentMapper;
import com.niuyin.service.behave.mapper.VideoUserLikeMapper;
import com.niuyin.service.behave.service.IVideoUserCommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_CREATE_ROUTING_KEY;
import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_DIRECT_EXCHANGE;

/**
 * (VideoUserComment)表服务实现类
 *
 * @author roydon
 * @since 2023-10-30 16:52:53
 */
@Slf4j
@Service("videoUserCommentService")
public class VideoUserCommentServiceImpl extends ServiceImpl<VideoUserCommentMapper, VideoUserComment> implements IVideoUserCommentService {
    @Resource
    private VideoUserCommentMapper videoUserCommentMapper;

    @Resource
    private VideoUserLikeMapper videoUserLikeMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RemoteMemberService remoteMemberService;

    @Resource
    private RedisService redisService;

    @DubboReference
    private DubboMemberService dubboMemberService;

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
        sendNotice2MQ(videoUserComment.getVideoId(), videoUserComment.getContent(), UserContext.getUser().getUserId());
        return this.save(videoUserComment);
    }

    /**
     * 用户评回复评论，通知mq
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
        notice.setRemark("回复了你的评论");
        notice.setNoticeType(NoticeType.COMMENT_ADD.getCode());
        notice.setReceiveFlag(ReceiveFlag.WAIT.getCode());
        notice.setCreateTime(LocalDateTime.now());
        // notice消息转换为json
        String msg = JSON.toJSONString(notice);
        rabbitTemplate.convertAndSend(NOTICE_DIRECT_EXCHANGE, NOTICE_CREATE_ROUTING_KEY, msg);
        log.debug(" ==> {} 发送了一条消息 ==> {}", NOTICE_DIRECT_EXCHANGE, msg);
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
     * 查找指定视频评论量 对逻辑删除过的评论进行过滤
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

    /**
     * 分页查询评论树
     *
     * @param pageDTO
     * @return
     */
    @Override
    public PageDataInfo getCommentPageTree(VideoUserCommentPageDTO pageDTO) {
        String newsId = pageDTO.getVideoId();
        if (StringUtil.isEmpty(newsId)) {
            return PageDataInfo.emptyPage();
        }
        IPage<VideoUserComment> iPage = this.getRootListByVideoId(pageDTO);
        List<VideoUserComment> rootRecords = iPage.getRecords();

        List<VideoUserCommentVO> voList = new ArrayList<>();
        CompletableFuture.allOf(rootRecords.stream()
                .map(r -> CompletableFuture.runAsync(() -> {
                    // 获取用户详情
                    VideoUserCommentVO appNewsCommentVO = BeanCopyUtils.copyBean(r, VideoUserCommentVO.class);
                    Member user = dubboMemberService.apiGetById(r.getUserId());
                    if (StringUtils.isNotNull(user)) {
                        appNewsCommentVO.setNickName(StringUtils.isEmpty(user.getNickName()) ? "-" : user.getNickName());
                        appNewsCommentVO.setAvatar(StringUtils.isEmpty(user.getAvatar()) ? "" : user.getAvatar());
                    }
                    Long commentId = r.getCommentId();
                    List<VideoUserComment> children = this.getChildren(commentId);
                    List<VideoUserCommentVO> childrenVOS = BeanCopyUtils.copyBeanList(children, VideoUserCommentVO.class);
                    CompletableFuture.allOf(childrenVOS.stream()
                            .map(c -> CompletableFuture.runAsync(() -> {
                                Member cUser = dubboMemberService.apiGetById(c.getUserId());
                                if (StringUtils.isNotNull(cUser)) {
                                    c.setNickName(StringUtils.isEmpty(cUser.getNickName()) ? "-" : cUser.getNickName());
                                    c.setAvatar(StringUtils.isEmpty(cUser.getAvatar()) ? "" : cUser.getAvatar());
                                }
                                if (!c.getParentId().equals(commentId)) {
                                    // 回复了回复
                                    VideoUserComment byId = this.getById(c.getParentId());
                                    Long userId1 = byId.getUserId();
                                    c.setReplayUserId(byId.getUserId());
                                    Member byUser = dubboMemberService.apiGetById(userId1);
                                    if (StringUtils.isNotNull(byUser)) {
                                        c.setReplayUserNickName(byUser.getNickName());
                                    }
                                }
                            })).toArray(CompletableFuture[]::new)).join();
                    appNewsCommentVO.setChildren(childrenVOS);
                    voList.add(appNewsCommentVO);
                })).toArray(CompletableFuture[]::new)).join();
        return PageDataInfo.genPageData(voList, iPage.getTotal());
    }

    /**
     * 删除视频所有评论
     *
     * @param videoId
     * @return
     */
    @Override
    public boolean removeCommentByVideoId(String videoId) {
        LambdaQueryWrapper<VideoUserComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserComment::getVideoId, videoId);
        return this.remove(queryWrapper);
    }
}
