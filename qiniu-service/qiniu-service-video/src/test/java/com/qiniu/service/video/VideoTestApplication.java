package com.qiniu.service.video;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.bean.BeanCopyUtils;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.common.utils.uniqueid.IdGenerator;
import com.qiniu.feign.user.RemoteUserService;
import com.qiniu.model.search.vo.VideoSearchVO;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.VideoCategoryRelation;
import com.qiniu.model.video.domain.VideoSensitive;
import com.qiniu.model.video.domain.VideoUserLike;
import com.qiniu.model.video.dto.VideoPublishDto;
import com.qiniu.model.video.vo.VideoUserLikeAndFavoriteVo;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.mapper.VideoSensitiveMapper;
import com.qiniu.service.video.service.IVideoCategoryRelationService;
import com.qiniu.service.video.service.IVideoCategoryService;
import com.qiniu.service.video.service.IVideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.qiniu.model.common.enums.HttpCodeEnum.*;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/29 16:07
 */
@SpringBootTest
public class VideoTestApplication {

    @Autowired
    IVideoService videoService;

    @Autowired
    private IVideoCategoryService videoCategoryService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private VideoSensitiveMapper videoSensitiveMapper;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private IVideoCategoryRelationService videoCategoryRelationService;

    @Resource
    private VideoMapper videoMapper;

//    void bindTest(){
//        VideoBindDto videoBindDto = new VideoBindDto();
//        videoBindDto.setVideoId(1);
//        videoService.bindVideoAndUser();
//    }

    @Test
    void getUser() {
        videoCategoryService.saveVideoCategoriesToRedis();

    }

    @Test
    void videoLikeTest() {
//        String videoId = "11685954002238832647a8379a1";
//        Long userId = 2L;
//        LambdaQueryWrapper<VideoUserLike> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(VideoUserLike::getVideoId, videoId).eq(VideoUserLike::getUserId, userId);
//        List<VideoUserLike> list = videoUserLikeService.list(queryWrapper);
//        if (StringUtils.isNull(list) || list.isEmpty()) {
//            VideoUserLike videoUserLike = new VideoUserLike();
//            videoUserLike.setVideoId(videoId);
//            videoUserLike.setUserId(userId);
//            videoUserLike.setCreateTime(LocalDateTime.now());
//            //将本条点赞信息存储到redis
//            likeNumIncrease(videoId);
//            videoUserLikeService.save(videoUserLike);
//        } else {
//            //将本条点赞信息从redis
//            likeNumDecrease(videoId);
//            videoUserLikeService.remove(queryWrapper);
//        }


    }

    public void likeNumIncrease(String videoId) {
        // 缓存中点赞量自增一
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_LIKE_NUM_MAP_KEY, videoId, 1);
    }

    /**
     * 缓存中点赞量自增一
     *
     * @param videoId
     */
    public void likeNumDecrease(String videoId) {
        // 缓存中阅读量自增一
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_LIKE_NUM_MAP_KEY, videoId, -1);
    }

    @Test
    void saveVideoCategoriesToRedisTest() {
        videoCategoryService.saveVideoCategoriesToRedis();

    }

    @Test
    void selectAllCategoryTest() {
        videoCategoryService.selectAllCategory();

    }

    @Test
    void sensitiveTest() {
        String s1 = "冰毒";
        String s2 = "海洛因";
        String s3 = "正常";
        String s4 = "这个是假冰毒呀";
        String s5 = "这个是假冰毒";
        String s6 = "冰毒的一百种测法";


        LambdaQueryWrapper<VideoSensitive> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(VideoSensitive::getId)
                .like(VideoSensitive::getSensitives, s1)
                .or(w -> w.like(VideoSensitive::getSensitives, s3));
        List<VideoSensitive> videoSensitives = videoSensitiveMapper.selectList(queryWrapper);
        videoSensitives.size();
    }

    @Test
    void publishTest() {
        Long userId = 3L;
        String videoTitle = "御姐";
        String videoDesc = "甜心大姐姐";
        String videoUrl = "http://s38bf8bdn.hb-bkt.clouddn.com/2023/11/02/d1e511dd3e754c3fa23e872f608dd914.mp4";
        Long categoryId = 1L;
        String coverImage = "头像路径测试";
        VideoPublishDto videoPublishDto = new VideoPublishDto();
        videoPublishDto.setVideoTitle(videoTitle);
        videoPublishDto.setVideoDesc(videoDesc);
        videoPublishDto.setVideoUrl(videoUrl);
        videoPublishDto.setCategoryId(categoryId);
        videoPublishDto.setCoverImage(coverImage);
        if (videoPublishDto.getVideoTitle().length() > 30) {
            throw new CustomException(BIND_CONTENT_TITLE_FAIL);
        }
        if (videoPublishDto.getVideoDesc().length() > 200) {
            throw new CustomException(BIND_CONTENT_DESC_FAIL);
        }
        //构建敏感词查询条件
        LambdaQueryWrapper<VideoSensitive> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(VideoSensitive::getId)
                .like(VideoSensitive::getSensitives, videoPublishDto.getVideoTitle())
                .or(w -> w.like(VideoSensitive::getSensitives, videoPublishDto.getVideoDesc()));
        List<VideoSensitive> videoSensitives = videoSensitiveMapper.selectList(queryWrapper);
        //如果结果不为空，证明有敏感词，提示异常
        if (!videoSensitives.isEmpty()) {
            throw new CustomException(SENSITIVEWORD_ERROR);
        }
        //将传过来的数据拷贝到要存储的对象中
        Video video = BeanCopyUtils.copyBean(videoPublishDto, Video.class);
        //生成id
        String videoId = IdGenerator.generatorShortId();
        //向新的对象中封装信息
        video.setVideoId(videoId);
        video.setUserId(userId);
        video.setCreateTime(LocalDateTime.now());
        video.setCreateBy(userId.toString());
        //将前端传递的分类拷贝到关联表对象
        VideoCategoryRelation videoCategoryRelation = BeanCopyUtils.copyBean(videoPublishDto, VideoCategoryRelation.class);
        //video_id存入VideoCategoryRelation（视频分类关联表）
        videoCategoryRelation.setVideoId(video.getVideoId());
        //先将video对象存入video表中
        int insert = videoMapper.insert(video);
        //再将videoCategoryRelation对象存入video_category_relation表中
        videoCategoryRelationService.saveVideoCategoryRelation(videoCategoryRelation);
        if (insert != 0) {
            // 1.发送整个video对象发送消息，
            // TODO 待添加视频封面
            VideoSearchVO videoSearchVO = new VideoSearchVO();
            videoSearchVO.setVideoId(video.getVideoId());
            videoSearchVO.setVideoTitle(video.getVideoTitle());
            // localdatetime转换为date
            videoSearchVO.setPublishTime(Date.from(video.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
            videoSearchVO.setCoverImage("null");
            videoSearchVO.setVideoUrl(video.getVideoUrl());
            videoSearchVO.setUserId(userId);
            // 获取用户信息
            User userCache = redisService.getCacheObject("userinfo:" + userId);
            if (StringUtils.isNotNull(userCache)) {
                videoSearchVO.setUserNickName(userCache.getNickName());
                videoSearchVO.setUserAvatar(userCache.getAvatar());
            } else {
                User remoteUser = remoteUserService.userInfoById(userId).getData();
                videoSearchVO.setUserNickName(remoteUser.getNickName());
                videoSearchVO.setUserAvatar(remoteUser.getAvatar());
            }
        } else {
            throw new CustomException(null);
        }
    }

    @Test
    void uerLikePageTest() {
        Long userId = 3L;
        List<Video> userLikedVideos = videoMapper.getUserLikesVideos(userId, 0, 10);
        Page<VideoUserLikeAndFavoriteVo> objectPage = new Page<>();
        List<VideoUserLikeAndFavoriteVo> videoUserLikeAndFavoriteVos = BeanCopyUtils.copyBeanList(userLikedVideos, VideoUserLikeAndFavoriteVo.class);
//        for (Video userLikedVideo : userLikedVideos) {
//            objects.add(BeanCopyUtils.copyBean(userLikedVideo, VideoUserLikeAndFavoriteVo.class));
//        }
        objectPage.setRecords(videoUserLikeAndFavoriteVos);
        System.out.println(objectPage);

    }

    @Test
    void test(){
        char s1=97;
        char s2='a';

        System.out.println(s1);
        System.out.println(s2);


    }

}
