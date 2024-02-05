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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
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

        Page<VideoUserLike> page = videoUserLikeService.page(new Page<>(1, 10), null);
        List<String> collect = page.getRecords().stream().map(VideoUserLike::getVideoId).collect(Collectors.toList());
        log.debug("开始");
        // 1\
        videoUserLikeMapper.selectImagesByVideoIds(collect);

        // 2\1174446482950979584a979be18
//        videoUserLikeMapper.selectImagesByVideoId("1174446482950979584a979be18");

        // 3\
//        collect.forEach(c-> {
//            videoUserLikeMapper.selectImagesByVideoId(c);
//        });

        // 4\
//        List<CompletableFuture<Void>> futures = collect.stream()
//                .map(r -> CompletableFuture.runAsync(() -> {
//                            videoUserLikeMapper.selectImagesByVideoId(r);
//                        })).collect(Collectors.toList());
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.debug("结束");

    }

    @Test
    void testForkJoin() {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        CustomRecursiveTask task = new CustomRecursiveTask(new int[]{1, 2, 3, 2, 23, 32, 324, 2, 2, 23, 23, 12, 3});
        // 1、execute之后join
//        forkJoinPool.execute(task);
//        Integer res = task.join();
//        System.out.println("res = " + res);
        // 2、直接invoke拿结果
        Integer res = forkJoinPool.invoke(task);
        System.out.println("res = " + res);
        // 3、task.fork().join(); *可能会对结果的排序产生影响*
//        task.fork().join();
        forkJoinPool.shutdown();
    }

    public class CustomRecursiveTask extends RecursiveTask<Integer> {
        private int[] arr;

        private static final int THRESHOLD = 10;

        public CustomRecursiveTask(int[] arr) {
            this.arr = arr;
        }

        @Override
        protected Integer compute() {
            if (arr.length > THRESHOLD) {
                return ForkJoinTask.invokeAll(createSubtasks())
                        .stream()
                        .mapToInt(ForkJoinTask::join)
                        .sum();
            } else {
                return processing(arr);
            }
        }

        private Collection<CustomRecursiveTask> createSubtasks() {
            List<CustomRecursiveTask> dividedTasks = new ArrayList<>();
            dividedTasks.add(new CustomRecursiveTask(
                    Arrays.copyOfRange(arr, 0, arr.length / 2)));
            dividedTasks.add(new CustomRecursiveTask(
                    Arrays.copyOfRange(arr, arr.length / 2, arr.length)));
            return dividedTasks;
        }

        private Integer processing(int[] arr) {
            return Arrays.stream(arr)
                    .filter(a -> a > 10 && a < 30)
                    .map(a -> a * 10)
                    .sum();
        }
    }

}
