package com.niuyin.service.notice.controller.app;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.notice.dto.NoticePageDTO;
import com.niuyin.service.notice.service.INoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 通知表(Notice)表控制层
 *
 * @author roydon
 * @since 2023-11-08 16:21:43
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/app")
public class AppNoticeController {

    @Resource
    private INoticeService noticeService;

    /**
     * 未读消息数量
     */
    @GetMapping("/unreadCount")
    public R<Long> unReadNoticeCount() {
        return R.ok(noticeService.getUnreadNoticeCount());
    }

    /**
     * 分页根据条件查询行为通知
     *
     * @param pageDTO
     * @return
     */
    @PostMapping("/behavePage")
    public PageDataInfo behaveNoticePage(@RequestBody NoticePageDTO pageDTO) {
        return noticeService.getBehaveNoticePage(pageDTO);
    }


}

