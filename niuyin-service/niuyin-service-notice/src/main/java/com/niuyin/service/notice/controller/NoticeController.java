package com.niuyin.service.notice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.notice.domain.Notice;
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
@RequestMapping("/api/v1")
public class NoticeController {

    @Resource
    private INoticeService noticeService;

    @PostMapping("/page")
    public PageDataInfo userNoticePage(@RequestBody PageDTO pageDTO) {
        IPage<Notice> noticeIPage = noticeService.queryUserNoticePage(pageDTO);
        return PageDataInfo.genPageData(noticeIPage.getRecords(), noticeIPage.getTotal());
    }

}

