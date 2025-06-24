package com.niuyin.service.ai.controller.web.image;

import com.niuyin.common.core.domain.R;
import com.niuyin.model.ai.image.domain.ImageDO;
import com.niuyin.model.ai.image.dto.ImageGenDTO;
import com.niuyin.service.ai.service.IImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ImageBotController
 *
 * @AUTHOR: roydon
 * @DATE: 2025/4/28
 **/
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/image")
public class ImageBotController {

    private final IImageService imageService;

    /**
     * 文生图
     * 同步调用
     *
     * @return url
     */
    @GetMapping("/generate")
    public R<ImageDO> generate(@Valid ImageGenDTO dto) {
        return R.ok(imageService.generateImage(dto));
    }

}
