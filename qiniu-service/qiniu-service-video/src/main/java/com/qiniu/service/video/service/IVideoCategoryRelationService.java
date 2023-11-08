package com.qiniu.service.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.video.domain.VideoCategoryRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 视频分类关联表(VideoCategoryRelation)表服务接口
 *
 * @author lzq
 * @since 2023-10-31 14:44:35
 */
public interface IVideoCategoryRelationService extends IService<VideoCategoryRelation> {

    boolean saveVideoCategoryRelation(VideoCategoryRelation videoCategoryRelation);
}
