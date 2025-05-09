package com.niuyin.service.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.model.ai.domain.ChatConversationDO;
import com.niuyin.model.ai.domain.ImageDO;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.service.ai.mapper.ImageMapper;
import com.niuyin.service.ai.service.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.Image;
import org.springframework.stereotype.Service;

/**
 * AI文生图表(AiImage)表服务实现类
 *
 * @author roydon
 * @since 2025-05-06 15:48:47
 */
@RequiredArgsConstructor
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, ImageDO> implements IImageService {
    private final ImageMapper imageMapper;

    @Override
    public IPage<ImageDO> getList(PageDTO dto) {
        LambdaQueryWrapper<ImageDO> qw = new LambdaQueryWrapper<>();
        qw.eq(ImageDO::getUserId, UserContext.getUserId());
        qw.orderByDesc(ImageDO::getCreateTime);
        return this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), qw);
    }

    /**
     * 图片生成回调
     */
    @Override
    public boolean generateImageCall(Image image) {
        return false;
    }
}
