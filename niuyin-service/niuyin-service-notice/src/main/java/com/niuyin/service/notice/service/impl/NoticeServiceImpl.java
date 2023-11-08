package com.niuyin.service.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.notice.domain.Notice;
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

    @Override
    public IPage<Notice> queryUserNoticePage(PageDTO pageDTO) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getNoticeUserId, userId);
        return this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), queryWrapper);
    }
}
