package com.niuyin.service.behave;

import com.niuyin.model.behave.enums.UserVideoBehaveEnum;
import com.niuyin.service.behave.service.IUserVideoBehaveService;
import com.niuyin.service.behave.service.IVideoUserCommentService;
import com.niuyin.service.behave.service.IVideoUserFavoritesService;
import com.niuyin.service.behave.service.IVideoUserLikeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户视频行为表(UserVideoBehave)表服务接口
 *
 * @author roydon
 * @since 2024-04-19 14:21:13
 */
@Slf4j
@SpringBootTest
public class UserVideoBehaveTest {

    @Resource
    private IUserVideoBehaveService userVideoBehaveService;

    @Resource
    private IVideoUserLikeService videoUserLikeService;

    @Resource
    private IVideoUserCommentService videoUserCommentService;

    @Resource
    private IVideoUserFavoritesService videoUserFavoritesService;

    @Test
    @DisplayName("同步用户行为数据")
    void syncUserBehaveData() {
        // 用户 2
        Long userId = 32L;
        // 同步观看数据

        // 同步点赞数据
        List<String> videoIdsByUserId = videoUserLikeService.getVideoIdsByUserId(userId);
        videoIdsByUserId.forEach(videoId -> {
            userVideoBehaveService.syncUserVideoBehave(userId, videoId, UserVideoBehaveEnum.LIKE);
        });
        // 同步评论数据
        List<String> userCommentVideoIdsRecord = videoUserCommentService.getUserCommentVideoIdsRecord(userId);
        userCommentVideoIdsRecord.forEach(videoId -> {
            userVideoBehaveService.syncUserVideoBehave(userId, videoId, UserVideoBehaveEnum.COMMENT);
        });
        // 同步收藏数据
        List<String> favoriteVideoIdListByUserId = videoUserFavoritesService.getFavoriteVideoIdListByUserId(userId);
        favoriteVideoIdListByUserId.forEach(videoId -> {
            userVideoBehaveService.syncUserVideoBehave(userId, videoId, UserVideoBehaveEnum.FAVORITE);
        });

    }

}
