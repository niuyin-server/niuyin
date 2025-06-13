package com.niuyin.service.notice.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.common.cache.service.RedisService;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.dto.NoticePageDTO;
import com.niuyin.model.notice.vo.NoticeVO;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.notice.mapper.NoticeMapper;
import com.niuyin.service.notice.service.INoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 通知表(Notice)表控制层
 *
 * @author roydon
 * @since 2023-11-08 16:21:43
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class NoticeController {

    @Resource
    private INoticeService noticeService;

    @Resource
    private RedisService redisService;

    @Resource
    NoticeMapper noticeMapper;

    /**
     * 分页根据条件查询
     *
     * @param pageDTO
     * @return
     */
    @PostMapping("/page")
    public PageData userNoticePage(@RequestBody NoticePageDTO pageDTO) {
        IPage<Notice> noticeIPage = noticeService.queryUserNoticePage(pageDTO);
        List<Notice> records = noticeIPage.getRecords();
        if (records.isEmpty()) {
            return PageData.emptyPage();
        }
//        List<NoticeVO> voList = new ArrayList<>(10);
//        //封装vo
//        records.forEach(n -> {
//            NoticeVO noticeVO = BeanCopyUtils.copyBean(n, NoticeVO.class);
//            // 先走redis，有就直接返回
//            Member userCache = redisService.getCacheObject("member:userinfo:" + n.getOperateUserId());
//            if (StringUtils.isNotNull(userCache)) {
//                noticeVO.setNickName(userCache.getNickName());
//                noticeVO.setOperateAvatar(userCache.getAvatar());
//            } else {
//                Member user = new Member();
//                List<Member> members = noticeMapper.batchSelectVideoAuthor(Collections.singletonList(n.getOperateUserId()));
//                if (!members.isEmpty()) {
//                    user = members.get(0);
//                }
//                if (StringUtils.isNotNull(user)) {
//                    noticeVO.setNickName(user.getNickName());
//                    noticeVO.setOperateAvatar(user.getAvatar());
//                }
//            }
//            // 封装视频封面
//            if (StringUtils.isNotNull(n.getVideoId())) {
//                Video videoCache = redisService.getCacheObject("video:videoinfo:" + n.getVideoId());
//                if (StringUtils.isNull(videoCache)) {
//                    //缓存为空
//                    Video video = noticeMapper.selectVideoById(n.getVideoId());
//                    noticeVO.setVideoCoverImage(video.getCoverImage());
//                } else {
//                    noticeVO.setVideoCoverImage(videoCache.getCoverImage());
//                }
//            }
//            voList.add(noticeVO);
//        });
        List<CompletableFuture<NoticeVO>> futures = records.stream()
                .map(n -> CompletableFuture.supplyAsync(() -> {
                    NoticeVO noticeVO = BeanCopyUtils.copyBean(n, NoticeVO.class);
                    Member user = new Member();
                    // 获取用户信息
                    Member userCache = redisService.getCacheObject("member:userinfo:" + n.getOperateUserId());
                    if (StringUtils.isNotNull(userCache)) {
                        noticeVO.setNickName(userCache.getNickName());
                        noticeVO.setOperateAvatar(userCache.getAvatar());
                    } else {
                        List<Member> members = noticeMapper.batchSelectVideoAuthor(Collections.singletonList(n.getOperateUserId()));
                        if (!members.isEmpty()) {
                            user = members.get(0);
                        }
                        if (StringUtils.isNotNull(user)) {
                            noticeVO.setNickName(user.getNickName());
                            noticeVO.setOperateAvatar(user.getAvatar());
                        }
                    }
                    // 获取视频封面
                    if (StringUtils.isNotNull(n.getVideoId())) {
                        Video videoCache = redisService.getCacheObject("video:videoinfo:" + n.getVideoId());
                        if (StringUtils.isNull(videoCache)) {
                            // 缓存为空
                            Video video = noticeMapper.selectVideoById(n.getVideoId());
                            noticeVO.setVideoCoverImage(video.getCoverImage());
                        } else {
                            noticeVO.setVideoCoverImage(videoCache.getCoverImage());
                        }
                    }
                    return noticeVO;
                }))
                .collect(Collectors.toList());
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        List<NoticeVO> voList = allFutures.thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .join();
        return PageData.genPageData(voList, noticeIPage.getTotal());
    }

    /**
     * 删除通知
     *
     * @param noticeId
     * @return
     */
    @DeleteMapping("/{noticeId}")
    public R<Boolean> delNotice(@PathVariable("noticeId") Long noticeId) {
        return R.ok(noticeService.removeById(noticeId));
    }

    /**
     * 未读消息数量
     *
     * @return
     */
    @PostMapping("/count")
    public R<Long> noticeCount(@RequestBody Notice notice) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getNoticeUserId, UserContext.getUserId());
        queryWrapper.eq(StringUtils.isNotEmpty(notice.getReceiveFlag()), Notice::getReceiveFlag, notice.getReceiveFlag());
        return R.ok(noticeService.count(queryWrapper));
    }

}

