package com.niuyin.service.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.dto.NoticePageDTO;
import com.niuyin.service.notice.mapper.NoticeMapper;
import com.niuyin.service.notice.service.INoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
}
