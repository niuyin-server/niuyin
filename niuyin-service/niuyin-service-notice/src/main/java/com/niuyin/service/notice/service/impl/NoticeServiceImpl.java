package com.niuyin.service.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.common.core.service.RedisService;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.dubbo.api.DubboVideoService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.dto.NoticePageDTO;
import com.niuyin.model.notice.enums.ReceiveFlag;
import com.niuyin.model.notice.vo.NoticeVO;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.notice.mapper.NoticeMapper;
import com.niuyin.service.notice.service.INoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 通知表(Notice)表服务实现类
 *
 * @author roydon
 * @since 2023-11-08 16:21:45
 */
@Slf4j
@Service("noticeService")
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {
    @Resource
    private NoticeMapper noticeMapper;

    @Resource
    private RedisService redisService;

    @DubboReference(mock = "return null")
    DubboMemberService dubboMemberService;

    @DubboReference(mock = "return null")
    DubboVideoService dubboVideoService;

    /**
     * 分页未读消息
     *
     * @param pageDTO
     * @return
     */
    @Override
    public IPage<Notice> queryUserNoticePage(NoticePageDTO pageDTO) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getNoticeUserId, userId);
        queryWrapper.eq(StringUtils.isNotEmpty(pageDTO.getNoticeType()), Notice::getNoticeType, pageDTO.getNoticeType());
        queryWrapper.eq(StringUtils.isNotEmpty(pageDTO.getReceiveFlag()), Notice::getReceiveFlag, pageDTO.getReceiveFlag());
        queryWrapper.orderByDesc(Notice::getCreateTime);
        return this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), queryWrapper);
    }

    /**
     * 获取未读消息数量
     */
    @Override
    public Long getUnreadNoticeCount() {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getNoticeUserId, UserContext.getUserId());
        queryWrapper.eq(Notice::getReceiveFlag, ReceiveFlag.WAIT.getCode());
        return this.count(queryWrapper);
    }

    /**
     * 分页行为通知
     *
     * @param pageDTO
     * @return
     */
    @Override
    public PageDataInfo getBehaveNoticePage(NoticePageDTO pageDTO) {
        IPage<Notice> noticeIPage = this.queryUserBehaveNoticePage(pageDTO);
        List<Notice> records = noticeIPage.getRecords();
        if (records.isEmpty()) {
            return PageDataInfo.emptyPage();
        }
        // 封装消息通知vo
        List<NoticeVO> noticeVOList = BeanCopyUtils.copyBeanList(records, NoticeVO.class);
        noticeVOList.forEach(v -> {
            // 获取操作用户信息
            Member member = dubboMemberService.apiGetById(v.getOperateUserId());
            if (!Objects.isNull(member)) {
                v.setNickName(member.getNickName());
                v.setOperateAvatar(member.getAvatar());
            }
            // 获取视频封面
            if (!Objects.isNull(v.getVideoId())) {
                Video video = dubboVideoService.apiGetVideoByVideoId(v.getVideoId());
                v.setVideoCoverImage(video.getCoverImage());
            }
        });
        return PageDataInfo.genPageData(noticeVOList, noticeIPage.getTotal());
    }

    /**
     * 新增消息
     *
     * @param notice
     * @return
     */
    @Override
    public boolean saveNotice(Notice notice) {
        return noticeMapper.saveNotice(notice);
    }

    /**
     * 分页消息
     *
     * @param pageDTO
     * @return
     */
    public IPage<Notice> queryUserBehaveNoticePage(NoticePageDTO pageDTO) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getNoticeUserId, UserContext.getUserId());
        queryWrapper.eq(StringUtils.isNotEmpty(pageDTO.getNoticeType()), Notice::getNoticeType, pageDTO.getNoticeType());
        queryWrapper.orderByDesc(Notice::getCreateTime);
        return this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), queryWrapper);
    }
}
