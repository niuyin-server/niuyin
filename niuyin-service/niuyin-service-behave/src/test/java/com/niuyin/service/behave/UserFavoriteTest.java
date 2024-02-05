package com.niuyin.service.behave;

import com.niuyin.model.behave.vo.UserFavoriteVideoVO;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.service.behave.mapper.VideoUserFavoritesMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserFavoriteTest
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/20
 **/
@Slf4j
@SpringBootTest
public class UserFavoriteTest {

    @Resource
    private VideoUserFavoritesMapper videoUserFavoritesMapper;


    @Test
    void testFavorite() {
        VideoPageDto videoPageDto = new VideoPageDto();
        videoPageDto.setUserId(2L);
        videoPageDto.setVideoTitle("启动");
        videoPageDto.setPageNum(0);
        videoPageDto.setPageSize(10);
        List<UserFavoriteVideoVO> videos = videoUserFavoritesMapper.selectUserFavoriteVideos(videoPageDto);
        videos.forEach(System.out::println);
        Long count = videoUserFavoritesMapper.selectUserFavoriteVideosCount(videoPageDto);
        log.debug("count : {}", count);
    }

}
