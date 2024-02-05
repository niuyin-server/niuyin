package com.niuyin.service.behave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.behave.domain.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
    /**
     * 通过收藏夹id获取收藏夹下的视频总数
     *
     * @param favoriteId
     * @return
     */
    Long selectVideoCountByFavoriteId(@Param("favoriteId") Long favoriteId);

    /**
     * 获取收藏夹前六张视频封面
     *
     * @param favoriteId
     * @param limit
     * @return
     */
    String[] selectFavoriteVideoCoverLimit(@Param("favoriteId") Long favoriteId, @Param("limit") int limit);

}
