package com.niuyin.service.video.controller.v1;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.video.dto.VideoCategoryPageDTO;
import com.niuyin.model.video.vo.VideoCategoryVo;
import com.niuyin.service.video.service.IVideoCategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (VideoCategory)表控制层
 *
 * @author lzq
 * @since 2023-10-30 19:41:13
 */
@RestController
@RequestMapping("/api/v1/category")
public class VideoCategoryController {

    @Resource
    private IVideoCategoryService videoCategoryService;

    @GetMapping()
    public R<?> getAllParentCategory() {
        List<VideoCategoryVo> categoryNames = videoCategoryService.selectAllParentCategory();
        return R.ok(categoryNames);
    }

    @PostMapping("/page")
    public PageDataInfo categoryVideoPage(@RequestBody VideoCategoryPageDTO pageDTO) {
        return videoCategoryService.selectVideoByCategory(pageDTO);
    }

    /**
     * 根据分类推送10条视频
     */
    @GetMapping("/pushVideo/{categoryId}")
    public R<?> categoryVideoPage(@PathVariable Long categoryId) {
        return R.ok(videoCategoryService.pushVideoByCategory(categoryId));
    }

    /**
     * 返回视频分类树形结构
     */
    @GetMapping("/tree")
    public R<?> getCategoryTree() {
        return R.ok(videoCategoryService.getCategoryTree());
    }

    @GetMapping("/parentList")
    public R<?> getVideoParentCategoryList() {
        return R.ok(videoCategoryService.getVideoParentCategoryList());
    }

}

