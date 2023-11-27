package com.niuyin.service.behave.util;

import com.niuyin.model.behave.vo.UserFavoriteInfoVO;
import com.niuyin.service.behave.mapper.UserFavoriteMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class PackageCollectionInfoPageProcessor {

    @Resource
    private UserFavoriteMapper userFavoriteMapper;

    private static final int collectionCoverLimit = 6; // 收藏夹详情默认展示六张视频封面

    public void processUserFavoriteInfoList(List<UserFavoriteInfoVO> collectionInfoList) {
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(collectionInfoList.stream()
                .map(this::packageUserFavoriteInfoVOAsync).toArray(CompletableFuture[]::new));
        allFutures.join();
    }

    public CompletableFuture<Void> packageUserFavoriteInfoVOAsync(UserFavoriteInfoVO userFavoriteInfoVO) {
        return CompletableFuture.runAsync(() -> packageUserFavoriteInfoVO(userFavoriteInfoVO));
    }

    public void packageUserFavoriteInfoVO(UserFavoriteInfoVO userFavoriteInfoVO) {
        CompletableFuture<Void> packageCollectionVideoCountFuture = packageCollectionVideoCountAsync(userFavoriteInfoVO);
        CompletableFuture<Void> packageCollectionCoverListFuture = packageCollectionCoverListAsync(userFavoriteInfoVO);
        CompletableFuture.allOf(
                packageCollectionVideoCountFuture,
                packageCollectionCoverListFuture
        ).join();
    }

    public CompletableFuture<Void> packageCollectionVideoCountAsync(UserFavoriteInfoVO userFavoriteInfoVO) {
        return CompletableFuture.runAsync(() -> collectionVideoCount(userFavoriteInfoVO));
    }

    public CompletableFuture<Void> packageCollectionCoverListAsync(UserFavoriteInfoVO userFavoriteInfoVO) {
        return CompletableFuture.runAsync(() -> collectionCoverList(userFavoriteInfoVO));
    }

    /**
     * 收藏夹视频总数
     */
    public void collectionVideoCount(UserFavoriteInfoVO userFavoriteInfoVO) {
        userFavoriteInfoVO.setVideoCount(userFavoriteMapper.selectVideoCountByFavoriteId(userFavoriteInfoVO.getFavoriteId()));
    }

    /**
     * 获取前六张封面
     */
    public void collectionCoverList(UserFavoriteInfoVO userFavoriteInfoVO) {
        userFavoriteInfoVO.setVideoCoverList(userFavoriteMapper.selectFavoriteVideoCoverLimit(userFavoriteInfoVO.getFavoriteId(), collectionCoverLimit));
    }
}
