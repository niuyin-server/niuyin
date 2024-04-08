package com.niuyin.service.social.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.date.DateUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.dubbo.api.DubboVideoService;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.enums.NoticeType;
import com.niuyin.model.notice.enums.ReceiveFlag;
import com.niuyin.model.social.domain.UserFollow;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.social.mapper.UserFollowMapper;
import com.niuyin.service.social.service.IUserFollowService;
import com.niuyin.service.social.service.SocialDynamicsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.niuyin.model.cache.SocialCacheConstants.FOLLOW;
import static com.niuyin.model.constants.VideoConstants.IN_FOLLOW;
import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_CREATE_ROUTING_KEY;
import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_DIRECT_EXCHANGE;

/**
 * 用户关注表(UserFollow)表服务实现类
 *
 * @author roydon
 * @since 2023-10-30 15:54:21
 */
@Slf4j
@Service("userFollowService")
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements IUserFollowService {
    @Resource
    private UserFollowMapper userFollowMapper;

    @Resource
    private RemoteMemberService remoteMemberService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @DubboReference
    private DubboMemberService dubboMemberService;

    @DubboReference(mock = "fail:return null")
    private DubboVideoService dubboVideoService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedisService redisService;

    @Resource
    private SocialDynamicsService socialDynamicsService;

    /**
     * 关注用户
     *
     * @param userId 被关注用户id
     */
    @Override
    public boolean followUser(Long userId) {
        Long loginUserId = UserContext.getUserId();
        if (StringUtils.isNull(userId)) {
            return false;
        }
        if (loginUserId.equals(userId)) {
            // 不可关注自己
            throw new CustomException(HttpCodeEnum.NOT_ALLOW_FOLLOW_YOURSELF);
        }
        Member user = remoteMemberService.userInfoById(userId).getData();
        if (StringUtils.isNull(user)) {
            // 用户不存在
            throw new CustomException(HttpCodeEnum.USER_NOT_EXIST);
        }
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserId, loginUserId);
        queryWrapper.eq(UserFollow::getUserFollowId, userId);
        List<UserFollow> list = list(queryWrapper);
        if (!list.isEmpty()) {
            // 已关注
            throw new CustomException(HttpCodeEnum.ALREADY_FOLLOW);
        }
        LocalDateTime now = LocalDateTime.now();
        boolean save = this.save(new UserFollow(loginUserId, userId, now));
        if (save) {
            // 发送消息到mq
            sendNotice2MQ(loginUserId, userId);
            // 初始化用户关注视频收件箱
            dubboVideoService.apiInitFollowVideoFeed(loginUserId, this.getFollowList(loginUserId).stream().map(UserFollow::getUserFollowId).collect(Collectors.toList()));
            // 缓存如用户关注缓存
            redisService.setCacheZSet(FOLLOW + loginUserId, userId, DateUtils.toDate(now).getTime());
        }
        return save;
    }

    /**
     * 用户关注，通知mq
     *
     * @param operateUserId
     */
    private void sendNotice2MQ(Long operateUserId, Long userId) {
        if (operateUserId.equals(userId)) {
            return;
        }
        // 封装notice实体
        Notice notice = new Notice();
        notice.setOperateUserId(operateUserId);
        notice.setNoticeUserId(userId);
        notice.setContent("关注了你");
        notice.setNoticeType(NoticeType.FOLLOW.getCode());
        notice.setReceiveFlag(ReceiveFlag.WAIT.getCode());
        notice.setCreateTime(LocalDateTime.now());
        // notice消息转换为json
        String msg = JSON.toJSONString(notice);
        rabbitTemplate.convertAndSend(NOTICE_DIRECT_EXCHANGE, NOTICE_CREATE_ROUTING_KEY, msg);
        log.debug(" ==> {} 发送了一条消息 ==> {}", NOTICE_DIRECT_EXCHANGE, msg);
    }

    /**
     * 取消关注
     *
     * @param userId 取消关注用户id
     */
    @Override
    public boolean unFollowUser(Long userId) {
        Long loginUserId = UserContext.getUser().getUserId();
        if (StringUtils.isNull(userId) || StringUtils.isNull(loginUserId)) {
            return false;
        }
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserId, loginUserId);
        queryWrapper.eq(UserFollow::getUserFollowId, userId);
        return this.remove(queryWrapper);
    }

    /**
     * 分页查询用户关注列表
     *
     * @param pageDTO 分页对象
     * @return IPage<User>
     */
    @Override
    public IPage<UserFollow> followPage(PageDTO pageDTO) {
        LambdaQueryWrapper<UserFollow> userFollowLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFollowLambdaQueryWrapper.eq(UserFollow::getUserId, UserContext.getUserId());
        return this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), userFollowLambdaQueryWrapper);
    }

    /**
     * 分页查询我的关注
     *
     * @param pageDTO
     * @return
     */
    @Override
    public PageDataInfo getFollowPage(PageDTO pageDTO) {
        if (StringUtils.isNull(pageDTO)) {
            LambdaQueryWrapper<UserFollow> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserFollow::getUserId, UserContext.getUserId());
            List<UserFollow> list = this.list(lambdaQueryWrapper);
            List<Member> res = new ArrayList<>();
            CompletableFuture.allOf(list.stream()
                    .map(l -> CompletableFuture.runAsync(() -> {
                        Member user = dubboMemberService.apiGetById(l.getUserFollowId());
                        res.add(user);
                    })).toArray(CompletableFuture[]::new)).join();
            return PageDataInfo.genPageData(res, res.size());
        }
        IPage<UserFollow> userFollowIPage = this.followPage(pageDTO);
        List<Member> userList = new ArrayList<>();
        userFollowIPage.getRecords().forEach(uf -> {
            Member user = dubboMemberService.apiGetById(uf.getUserFollowId());
            userList.add(user);
        });
        return PageDataInfo.genPageData(userList, userFollowIPage.getTotal());
    }

    /**
     * 分页用户粉丝
     *
     * @param pageDTO
     * @return
     */
    @Override
    public PageDataInfo queryUserFansPage(PageDTO pageDTO) {
        Long userId = UserContext.getUserId();
        // 查询粉丝ids
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserFollowId, userId);
        Page<UserFollow> page = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), queryWrapper);
        List<UserFollow> records = page.getRecords();
        if (records.isEmpty()) {
            return PageDataInfo.emptyPage();
        }
        List<Long> fansUserIds = records.stream().map(UserFollow::getUserId).collect(Collectors.toList());
        List<Member> memberList = dubboMemberService.apiGetInIds(fansUserIds);
        return PageDataInfo.genPageData(memberList, page.getTotal());
    }

    @Override
    public List<UserFollow> getFollowList(Long userId) {
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserId, userId);
        return this.list(queryWrapper);
    }

    @Override
    public void initFollowVideoFeed() {
        Long loginUserId = UserContext.getUserId();
        // 初始化用户关注视频收件箱
        dubboVideoService.apiInitFollowVideoFeed(loginUserId, this.getFollowList(loginUserId).stream().map(UserFollow::getUserFollowId).collect(Collectors.toList()));
    }

    /**
     * 关注流
     *
     * @param lastTime 滚动分页参数，首次为null，后续为上次的末尾视频时间
     * @return
     */
    @Override
    public List<Video> followVideoFeed(Long lastTime) {
        Long userId = UserContext.getUserId();
        // 是否存在
        Set<Object> videoIds = redisTemplate.opsForZSet().reverseRangeByScore(IN_FOLLOW + userId,
                0,
                lastTime == null ? new Date().getTime() : lastTime,
                lastTime == null ? 0 : 1,
                10);
        if (ObjectUtils.isEmpty(videoIds)) {
            // 可能只是缓存中没有了,缓存只存储7天内的关注视频,继续往后查看关注的用户太少了,不做考虑 - feed流必然会产生的问题
            return new ArrayList<>();
        }
        // 这里不会按照时间排序，需要手动排序
        List<Video> videos = new ArrayList<>();
        videoIds.forEach(id -> {
            Video video = dubboVideoService.apiGetVideoByVideoId((String) id);
            videos.add(video);
        });
        return videos;
    }

    /**
     * 获取社交动态分页
     *
     * @param pageDTO
     * @return
     */
    @Override
    public PageDataInfo<VideoVO> getSocialDynamicVideoPage(PageDTO pageDTO) {
        PageDataInfo<String> socialDynamics = socialDynamicsService.getSocialDynamics(pageDTO);
        // 封装视频vo
        List<String> videoIds = socialDynamics.getRows();
        if (videoIds.isEmpty()) {
            return PageDataInfo.emptyPage();
        }
        List<VideoVO> videoVOList = dubboVideoService.apiGetVideoVOListByVideoIds(UserContext.getUserId(),videoIds);
        return PageDataInfo.genPageData(videoVOList, socialDynamics.getTotal());
    }
}
