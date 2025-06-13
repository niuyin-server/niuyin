package com.niuyin.service.ai.controller.web.image;

import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.model.ai.image.domain.ImageDO;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.service.ai.service.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * AI文生图表(AiImage)表控制层
 *
 * @author roydon
 * @since 2025-05-06 15:48:46
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/image")
public class ImageController {

    private final IImageService imageService;
    private final SnowFlake snowFlake;

    /**
     * 分页
     */
    @PostMapping("/list")
    public PageData<ImageDO> queryByPage(@Validated @RequestBody PageDTO dto) {
        return PageData.page(imageService.getList(dto));
    }

    /**
     * 查询单条
     */
    @GetMapping("/{id}")
    public R<?> queryById(@PathVariable("id") Long id) {
        return R.ok(imageService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    public R<?> add(@RequestBody ImageDO imageDO) {
        imageDO.setId(snowFlake.nextId());
        imageDO.setUserId(UserContext.getUserId());
        imageDO.setCreateBy(UserContext.getUser().getUserName());
        LocalDateTime localDateTime = LocalDateTime.now();
        imageDO.setCreateTime(localDateTime);
        return R.ok(imageService.save(imageDO));
    }

    /**
     * 编辑
     */
    @PutMapping
    public R<?> edit(ImageDO imageDO) {
        imageDO.setUpdateBy(UserContext.getUser().getUserName());
        imageDO.setUpdateTime(LocalDateTime.now());
        return R.ok(imageService.updateById(imageDO));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public R<?> removeById(@PathVariable("id") Long id) {
        return R.ok(imageService.removeById(id));
    }

}

