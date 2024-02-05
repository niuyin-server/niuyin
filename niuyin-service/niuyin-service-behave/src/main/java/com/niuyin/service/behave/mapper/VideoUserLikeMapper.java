package com.niuyin.service.behave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.behave.domain.VideoUserLike;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoImage;
import com.niuyin.model.video.domain.VideoPosition;
import com.niuyin.model.video.dto.VideoPageDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 点赞表(VideoUserLike)表数据库访问层
 *
 * @author lzq
 * @since 2023-10-30 14:32:59
 */
@Mapper
public interface VideoUserLikeMapper extends BaseMapper<VideoUserLike> {
    /**
     * 查询用户的点赞列表
     */
    List<Video> selectPersonLikePage(VideoPageDto videoPageDto);

    Long selectPersonLikeCount(VideoPageDto videoPageDto);

    MemberInfo selectPersonLikeShowStatus(Long userId);

    /**
     * 通过videoId获取视频
     *
     * @param videoId
     * @return
     */
    Video selectVideoByVideoId(String videoId);

    /**
     * 通过视频id查询视频图片
     *
     * @param videoId
     * @return
     */
    List<VideoImage> selectImagesByVideoId(String videoId);

    /**
     * 通过视频id查询视频定位信息
     *
     * @param videoId
     * @return
     */
    VideoPosition selectPositionByVideoId(String videoId);

    List<VideoImage> selectImagesByVideoIds(List<String> imageVideoIds);

    /**
     * 点赞数
     *
     * @param videoId
     * @return
     */
    Long selectVideoLikeCount(String videoId);
}

