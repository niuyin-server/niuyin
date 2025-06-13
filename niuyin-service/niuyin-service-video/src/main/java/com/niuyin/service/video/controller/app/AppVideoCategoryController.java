package com.niuyin.service.video.controller.app;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.model.video.dto.CategoryVideoPageDTO;
import com.niuyin.model.video.vo.app.AppVideoCategoryVo;
import com.niuyin.service.video.service.IVideoCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (VideoCategory)表控制层
 *
 * @author lzq
 * @since 2023-10-30 19:41:13
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/app/category")
public class AppVideoCategoryController {

    private final IVideoCategoryService videoCategoryService;

    /**
     * 获取所有可用视频父分类
     */
    @GetMapping("/parent")
    public R<List<AppVideoCategoryVo>> getNormalParentCategory() {
        return R.ok(videoCategoryService.getNormalParentCategory());
    }

    /**
     * 获取所有可用视频父分类
     */
    @GetMapping("/children/{id}")
    public R<List<AppVideoCategoryVo>> getNormalParentCategory(@PathVariable("id") Long id) {
        return R.ok(videoCategoryService.getNormalChildrenCategory(id));
    }

    /**
     * 分页分类视频
     */
    @PostMapping("/videoPage")
    public PageData<?> getVideoByCategoryId(@Validated @RequestBody CategoryVideoPageDTO pageDTO) {
        return videoCategoryService.getVideoPageByCategoryId(pageDTO);
    }

}

