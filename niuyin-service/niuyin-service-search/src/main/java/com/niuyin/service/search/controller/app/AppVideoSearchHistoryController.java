package com.niuyin.service.search.controller.app;

import com.niuyin.common.core.domain.R;
import com.niuyin.service.search.domain.VideoSearchHistory;
import com.niuyin.service.search.service.VideoSearchHistoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * AppVideoSearchHistoryController
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/3
 * 视频搜索历史
 **/
@RestController
@RequestMapping("/api/v1/app/history")
public class AppVideoSearchHistoryController {

    @Resource
    private VideoSearchHistoryService videoSearchHistoryService;

    /**
     * app端搜索历史
     */
    @GetMapping("/load")
    public R<List<VideoSearchHistory>> findUserSearchHistoryForApp() {
        return R.ok(videoSearchHistoryService.findAppSearchHistory());
    }

}
