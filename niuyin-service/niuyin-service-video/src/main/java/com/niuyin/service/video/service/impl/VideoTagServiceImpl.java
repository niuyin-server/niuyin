package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.exception.CustomException;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.service.video.mapper.VideoTagMapper;
import com.niuyin.service.video.service.IVideoTagService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 视频标签表(VideoTag)表服务实现类
 *
 * @author roydon
 * @since 2023-11-11 16:05:09
 */
@Service("videoTagService")
public class VideoTagServiceImpl extends ServiceImpl<VideoTagMapper, VideoTag> implements IVideoTagService {
    @Resource
    private VideoTagMapper videoTagMapper;

    /**
     * 保存标签
     *
     * @param videoTag
     * @return
     */
    @Override
    public VideoTag saveTag(VideoTag videoTag) {
        // 首先查询该标签是否已存在
        VideoTag queriedTag = this.queryByTag(videoTag.getTag().trim());
        if (StringUtils.isNotNull(queriedTag)) {
            // 存在此标签立即返回标签id
            return queriedTag;
        }
        // 该标签不存在，执行新增逻辑
        videoTag.setTag(videoTag.getTag().trim()); //去空格
        this.save(videoTag);
        return videoTag;
    }

    /**
     * 根据tag返回标签
     *
     * @param tag
     * @return
     */
    @Override
    public VideoTag queryByTag(String tag) {
        LambdaQueryWrapper<VideoTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoTag::getTag, tag.trim());
        return getOne(queryWrapper);
    }

    /**
     * 随机获取标签 todo 获取热门的标签，且可动态指定数量
     *
     * @return
     */
    @Override
    public List<VideoTag> random10VideoTags() {
        return this.page(new Page<>(1, 10), null).getRecords();
    }
}
