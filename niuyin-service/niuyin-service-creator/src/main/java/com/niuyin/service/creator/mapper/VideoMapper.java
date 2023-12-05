package com.niuyin.service.creator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.creator.dto.VideoPageDTO;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 视频表(Video)表数据库访问层
 *
 * @author roydon
 * @since 2023-10-25 20:33:09
 */
@Mapper
public interface VideoMapper extends BaseMapper<Video> {

    /**
     * 视频分页
     *
     * @param videoPageDTO
     * @return
     */
    List<Video> selectVideoPage(VideoPageDTO videoPageDTO);
    Long selectVideoPageCount(VideoPageDTO videoPageDTO);

}

