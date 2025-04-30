package com.niuyin.service.search.controller.app;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.search.dto.PageDTO;
import com.niuyin.model.search.dto.VideoSearchKeywordDTO;
import com.niuyin.service.search.service.VideoSearchService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * VideoSearchController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@RestController
@RequestMapping("/api/v1/app/video")
public class AppVideoSearchController {

    @Resource
    private VideoSearchService videoSearchService;

    /**
     * 分页搜索视频
     */
    @PostMapping()
    public PageDataInfo<?> searchVideoForApp(@RequestBody VideoSearchKeywordDTO dto) {
        return videoSearchService.searchVideoFromESForApp(dto);
    }

    /**
     * 牛音热搜
     */
    @PostMapping("/hotSearch")
    @Cacheable(value = "hotSearch", key = "'hotSearch' + #pageDTO.pageNum + '_' + #pageDTO.pageSize")
    public R<?> getHotSearchForApp(@RequestBody PageDTO pageDTO) {
        return R.ok(videoSearchService.findSearchHot(pageDTO));
    }

}
