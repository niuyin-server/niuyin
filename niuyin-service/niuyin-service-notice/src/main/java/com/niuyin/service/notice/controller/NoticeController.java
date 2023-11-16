package com.niuyin.service.notice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.dto.NoticePageDTO;
import com.niuyin.model.notice.enums.ReceiveFlag;
import com.niuyin.model.notice.vo.NoticeVO;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.notice.mapper.NoticeMapper;
import com.niuyin.service.notice.service.INoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    RemoteMemberService remoteMemberService;

    @Resource
    NoticeMapper noticeMapper;

    /**
     * 分页根据条件查询
     *
     * @param pageDTO
     * @return
     */
    @PostMapping("/page")
    public PageDataInfo userNoticePage(@RequestBody NoticePageDTO pageDTO) {
        IPage<Notice> noticeIPage = noticeService.queryUserNoticePage(pageDTO);
        List<Notice> records = noticeIPage.getRecords();
        List<NoticeVO> voList = new ArrayList<>(10);
        if (records.isEmpty()) {
            return PageDataInfo.emptyPage();
        }
        // 封装vo
        records.forEach(n -> {
            NoticeVO noticeVO = BeanCopyUtils.copyBean(n, NoticeVO.class);
            // 先走redis，有就直接返回
            Member userCache = redisService.getCacheObject("member:userinfo:" + n.getOperateUserId());
            if (StringUtils.isNotNull(userCache)) {
                noticeVO.setNickName(userCache.getNickName());
                noticeVO.setOperateAvatar(userCache.getAvatar());
            } else {
                Member member = remoteMemberService.userInfoById(n.getOperateUserId()).getData();
                if (StringUtils.isNotNull(member)) {
                    noticeVO.setNickName(member.getNickName());
                    noticeVO.setOperateAvatar(member.getAvatar());
                }
            }
            // 封装视频封面
            if (StringUtils.isNotNull(n.getVideoId())) {
                Video videoCache = redisService.getCacheObject("video:videoinfo:" + n.getVideoId());
                if (StringUtils.isNull(videoCache)) {
                    //缓存为空
                    Video video = noticeMapper.selectVideoById(n.getVideoId());
                    noticeVO.setVideoCoverImage(video.getCoverImage());
                } else {
                    noticeVO.setVideoCoverImage(videoCache.getCoverImage());
                }
            }
            voList.add(noticeVO);
        });
        return PageDataInfo.genPageData(voList, noticeIPage.getTotal());
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

