package com.niuyin.service.behave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.model.behave.domain.UserFavorite;
import com.niuyin.model.behave.vo.UserFavoriteInfoVO;
import com.niuyin.model.behave.vo.app.FavoriteFolderVO;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.common.enums.DelFlagEnum;
import com.niuyin.service.behave.mapper.UserFavoriteMapper;
import com.niuyin.service.behave.service.IUserFavoriteService;
import com.niuyin.service.behave.service.IUserFavoriteVideoService;
import com.niuyin.service.behave.util.PackageCollectionInfoPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * (UserFavorite)表服务实现类
 *
 * @author lzq
 * @since 2023-11-13 16:37:53
 */
@Slf4j
@Service("userFavoriteService")
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements IUserFavoriteService {
    @Resource
    private UserFavoriteMapper userFavoriteMapper;

    @Resource
    PackageCollectionInfoPageProcessor packageCollectionInfoPageProcessor;

    @Resource
    private IUserFavoriteVideoService userFavoriteVideoService;

    /**
     * 用户新建收藏夹
     *
     * @param userFavorite
     * @return
     */
    @Override
    public boolean saveFavorite(UserFavorite userFavorite) {
        //从token中获取userId
        userFavorite.setUserId(UserContext.getUserId());
        //从枚举类中获取默认的删除标志参数
        userFavorite.setDelFlag(DelFlagEnum.EXIST.getCode());
        userFavorite.setCreateTime(LocalDateTime.now());
        return this.save(userFavorite);
    }

    /**
     * 分页查询用户收藏夹
     *
     * @param pageDTO
     * @return
     */
    @Override
    public IPage<UserFavorite> queryCollectionPage(PageDTO pageDTO) {
        LambdaQueryWrapper<UserFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFavorite::getUserId, UserContext.getUserId());
        queryWrapper.orderByDesc(UserFavorite::getCreateTime);
        return this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), queryWrapper);
    }

    /**
     * 我的收藏夹集合详情分页查询
     *
     * @param pageDTO
     * @return
     */
    @Override
    public PageDataInfo queryMyCollectionInfoPage(PageDTO pageDTO) {
        LambdaQueryWrapper<UserFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFavorite::getUserId, UserContext.getUserId());
        queryWrapper.orderByDesc(UserFavorite::getCreateTime);
        IPage<UserFavorite> userFavoriteIPage = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), queryWrapper);
        List<UserFavorite> records = userFavoriteIPage.getRecords();
        if (records.isEmpty()) {
            return PageDataInfo.emptyPage();
        }
        List<UserFavoriteInfoVO> collectionInfoList = BeanCopyUtils.copyBeanList(records, UserFavoriteInfoVO.class);
        // 封装VO
        packageCollectionInfoPageProcessor.processUserFavoriteInfoList(collectionInfoList);
        return PageDataInfo.genPageData(collectionInfoList, userFavoriteIPage.getTotal());
    }

    /**
     * 查询收藏集详情
     */
    @Override
    public List<UserFavoriteInfoVO> queryCollectionInfoList() {
        // 1、先获取用户收藏夹集合
        LambdaQueryWrapper<UserFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFavorite::getUserId, UserContext.getUserId());
        List<UserFavorite> collectionList = this.list(queryWrapper);
        // 2、遍历获取收藏的视频总数与前六张封面
        List<UserFavoriteInfoVO> collectionInfoList = BeanCopyUtils.copyBeanList(collectionList, UserFavoriteInfoVO.class);
        collectionInfoList.forEach(c -> {
            // 2.1、获取视频总数
            c.setVideoCount(userFavoriteMapper.selectVideoCountByFavoriteId(c.getFavoriteId()));
            // 2.2、获取前六张封面
            c.setVideoCoverList(userFavoriteMapper.selectFavoriteVideoCoverLimit(c.getFavoriteId(), 6));
        });
        return collectionInfoList;
    }

    /**
     * 收藏夹列表
     */
    @Override
    public List<FavoriteFolderVO> userFavoritesFolderList(String videoId) {
        LambdaQueryWrapper<UserFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFavorite::getUserId, UserContext.getUserId());
        List<UserFavorite> favoriteList = this.list(queryWrapper);
        List<FavoriteFolderVO> favoriteFolderVOS = BeanCopyUtils.copyBeanList(favoriteList, FavoriteFolderVO.class);
        favoriteFolderVOS.forEach(f -> {
            f.setVideoCount(userFavoriteMapper.selectVideoCountByFavoriteId(f.getFavoriteId()));
            f.setWeatherFavorite(userFavoriteVideoService.videoWeatherInFavoriteFolder(f.getFavoriteId(), videoId));
        });
        return favoriteFolderVOS;
    }
}
