package com.qiniu.service.search.controller.v1;

import com.qiniu.common.domain.R;
import com.qiniu.service.search.service.VideoSearchHistoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * VideoSearchHistoryController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@RestController
@RequestMapping("/api/v1/history")
public class VideoSearchHistoryController {

    @Resource
    private VideoSearchHistoryService videoSearchHistoryService;

    @GetMapping("/load")
    public R<?> findUserSearch() {
        return R.ok(videoSearchHistoryService.findAllSearch());
    }

    @DeleteMapping("/del/{id}")
    public R<?> delUserSearch(@PathVariable("id") String id) {
        return R.ok(videoSearchHistoryService.delSearchHistory(id));
    }

}
