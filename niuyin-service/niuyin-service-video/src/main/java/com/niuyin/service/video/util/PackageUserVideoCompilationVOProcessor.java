package com.niuyin.service.video.util;

import com.niuyin.model.behave.vo.UserFavoriteInfoVO;
import com.niuyin.model.video.vo.UserVideoCompilationVO;
import com.niuyin.service.video.service.IUserVideoCompilationService;
import org.springframework.stereotype.Component;


import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 异步封装用户视频合集VO
 */
@Component
public class PackageUserVideoCompilationVOProcessor {

    @Resource
    private IUserVideoCompilationService userVideoCompilationService;

    public void processUserVideoCompilationVOList(List<UserVideoCompilationVO> userVideoCompilationVOList) {
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(userVideoCompilationVOList.stream()
                .map(this::packageUserVideoCompilationVOAsync).toArray(CompletableFuture[]::new));
        allFutures.join();
    }

    private CompletableFuture<Void> packageUserVideoCompilationVOAsync(UserVideoCompilationVO userVideoCompilationVO) {
        return CompletableFuture.runAsync(() -> packageUserVideoCompilationVOVO(userVideoCompilationVO));
    }

    private void packageUserVideoCompilationVOVO(UserVideoCompilationVO userVideoCompilationVO) {
        CompletableFuture<Void> future1 = packageCompilationViewCountAsync(userVideoCompilationVO);
        CompletableFuture<Void> future2 = packageCompilationLikeCountAsync(userVideoCompilationVO);
        CompletableFuture<Void> future3 = packageCompilationFavoriteCountAsync(userVideoCompilationVO);
        CompletableFuture<Void> future4 = packageCompilationVideoCountAsync(userVideoCompilationVO);
        CompletableFuture.allOf(
                future1,
                future2,
                future3,
                future4
        ).join();
    }

    private CompletableFuture<Void> packageCompilationViewCountAsync(UserVideoCompilationVO userVideoCompilationVO) {
        return CompletableFuture.runAsync(() -> compilationViewCount(userVideoCompilationVO));
    }

    private CompletableFuture<Void> packageCompilationLikeCountAsync(UserVideoCompilationVO userVideoCompilationVO) {
        return CompletableFuture.runAsync(() -> compilationLikeCount(userVideoCompilationVO));
    }

    private CompletableFuture<Void> packageCompilationFavoriteCountAsync(UserVideoCompilationVO userVideoCompilationVO) {
        return CompletableFuture.runAsync(() -> compilationFavoriteCount(userVideoCompilationVO));
    }

    private CompletableFuture<Void> packageCompilationVideoCountAsync(UserVideoCompilationVO userVideoCompilationVO) {
        return CompletableFuture.runAsync(() -> compilationVideoCount(userVideoCompilationVO));
    }

    /**
     * 合集播放量
     */
    private void compilationViewCount(UserVideoCompilationVO userVideoCompilationVO) {
        userVideoCompilationVO.setViewCount(userVideoCompilationService.compilationViewCount(userVideoCompilationVO.getCompilationId()));
    }

    /**
     * 获赞量
     */
    private void compilationLikeCount(UserVideoCompilationVO userVideoCompilationVO) {
        userVideoCompilationVO.setLikeCount(userVideoCompilationService.compilationLikeCount(userVideoCompilationVO.getCompilationId()));
    }

    /**
     * 被收藏数
     */
    private void compilationFavoriteCount(UserVideoCompilationVO userVideoCompilationVO) {
        userVideoCompilationVO.setFavoriteCount(userVideoCompilationService.compilationFavoriteCount(userVideoCompilationVO.getCompilationId()));
    }

    /**
     * 视频数
     */
    private void compilationVideoCount(UserVideoCompilationVO userVideoCompilationVO) {
        userVideoCompilationVO.setVideoCount(userVideoCompilationService.compilationVideoCount(userVideoCompilationVO.getCompilationId()));
    }

}
