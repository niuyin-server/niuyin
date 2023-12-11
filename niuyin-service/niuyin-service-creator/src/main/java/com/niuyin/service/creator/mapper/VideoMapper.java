package com.niuyin.service.creator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.creator.dto.VideoPageDTO;
import com.niuyin.model.creator.dto.videoCompilationPageDTO;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.UserVideoCompilation;
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
     */
    List<Video> selectVideoPage(VideoPageDTO videoPageDTO);

    Long selectVideoPageCount(VideoPageDTO videoPageDTO);

    /**
     * 视频合集分页
     */
    List<UserVideoCompilation> selectVideoCompilationPage(videoCompilationPageDTO videoCompilationPageDTO);

    Long selectVideoCompilationPageCount(videoCompilationPageDTO videoCompilationPageDTO);

    // 视频播放量
    Long selectVideoPlayAmount(Long userId);
    Long selectVideoPlayAmountAdd(Long userId);
    List<Long> selectVideoPlayAmount7Day(Long userId);
    // 粉丝量
    Long selectFansAmount(Long userId);
    Long selectFansAmountAdd(Long userId);
    List<Long> selectFansAmount7Day(Long userId);
    // 作品获赞量
    Long selectVideoLikeAmount(Long userId);
    Long selectVideoLikeAmountAdd(Long userId);
    List<Long> selectVideoLikeAmount7Day(Long userId);
    // 作品评论量
    Long selectVideoCommentAmount(Long userId);
    Long selectVideoCommentAmountAdd(Long userId);
    List<Long> selectVideoCommentAmount7Day(Long userId);
}

