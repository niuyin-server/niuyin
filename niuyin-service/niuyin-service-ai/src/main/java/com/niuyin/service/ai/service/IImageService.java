package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.ai.image.domain.ImageDO;
import com.niuyin.model.ai.image.dto.ImageGenDTO;
import com.niuyin.model.common.dto.PageDTO;
import org.springframework.ai.image.Image;

/**
 * AI文生图表(AiImage)表服务接口
 *
 * @author roydon
 * @since 2025-05-06 15:48:47
 */
public interface IImageService extends IService<ImageDO> {

    IPage<ImageDO> getList(PageDTO dto);

    /**
     * 保存图片生成任务
     */
    ImageDO saveImageTask(ImageGenDTO dto);

    /**
     * 图片生成回调
     *
     * @return url
     */
    ImageDO generateImageCall(boolean success, ImageDO imageNew, Image image, String errorMessage);

    ImageDO generateImage(ImageGenDTO dto);
}
