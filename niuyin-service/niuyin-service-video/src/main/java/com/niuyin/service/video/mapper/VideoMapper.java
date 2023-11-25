package com.niuyin.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
     * @param videoId
     * @return
     */
    String getVideoUrlByVideoId(String videoId);

    /**
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Video> getUserLikesVideos(Long userId, int pageNum, int pageSize);

    /**
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Video> getUserFavoritesVideos(Long userId, int pageNum, int pageSize);

    /**
     * @param userId
     * @return
     */
    Long selectAllLikeNumForUser(Long userId);

    /**
     * 查询视频评论数
     *
     * @param videoId
     * @return
     */
    Long selectCommentCountByVideoId(String videoId);

    /**
     * 查询视频点赞表
     */
    Long selectUserLikeVideo(@Param("videoId") String videoId, @Param("userId") Long userId);

    /**
     * 用户是否收藏视频
     * @param videoId
     * @param userId
     * @return
     */
    Long userWeatherFavoriteVideo(@Param("videoId") String videoId, @Param("userId") Long userId);

    Long userWeatherAuthor(@Param("userId") Long userId,@Param("userFollowId") Long userFollowId);

    /**
     * 查询视频作者
     *
     * @param userId
     * @return
     */
    Member selectVideoAuthor(Long userId);

    List<Member> batchSelectVideoAuthor(List<Long> userId);

//    Member selcetMemberInfoById(Long userId);
}

