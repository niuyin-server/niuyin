package com.niuyin.service.behave;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niuyin.common.context.UserContext;
import com.niuyin.model.behave.domain.VideoUserLike;
import com.niuyin.model.behave.vo.UserFavoriteVideoVO;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.service.behave.mapper.VideoUserFavoritesMapper;
import com.niuyin.service.behave.mapper.VideoUserLikeMapper;
import com.niuyin.service.behave.service.IVideoUserLikeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * UserFavoriteTest
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/20
 **/
@Slf4j
@SpringBootTest
public class UserLikeTest {

    @Resource
    private VideoUserLikeMapper videoUserLikeMapper;

    @Resource
    private IVideoUserLikeService videoUserLikeService;


    @Test
    void testFavorite() {

        Page<VideoUserLike> page = videoUserLikeService.page(new Page<>(1, 20), null);
        List<String> collect = page.getRecords().stream().map(VideoUserLike::getVideoId).collect(Collectors.toList());
        log.debug("开始");
//        videoUserLikeMapper.selectImagesByVideoIds(collect);
//        collect.forEach(c-> {
//            videoUserLikeMapper.selectImagesByVideoId(c);
//        });
//
        List<CompletableFuture<Void>> futures = collect.stream()
                .map(r -> CompletableFuture.runAsync(() -> {
                            videoUserLikeMapper.selectImagesByVideoId(r);
                        })).collect(Collectors.toList());
        CompletableFuture<Object> objectCompletableFuture = new CompletableFuture<>();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.debug("结束");



    }

}
