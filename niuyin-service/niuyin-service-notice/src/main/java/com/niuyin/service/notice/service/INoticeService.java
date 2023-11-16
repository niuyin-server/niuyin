package com.niuyin.service.notice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.dto.NoticePageDTO;

/**
 * 通知表(Notice)表服务接口
 *
 * @author roydon
 * @since 2023-11-08 16:21:45
 */
public interface INoticeService extends IService<Notice> {

    /**
     * 分页查询用户通知
     *
     * @param pageDTO
     * @return
     */
    IPage<Notice> queryUserNoticePage(NoticePageDTO pageDTO);
}
