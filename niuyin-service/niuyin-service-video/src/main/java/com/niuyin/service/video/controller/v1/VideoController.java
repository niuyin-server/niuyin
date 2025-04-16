package com.niuyin.service.video.controller.v1;

import com.niuyin.common.cache.annotations.DoubleCache;
import com.niuyin.common.cache.annotations.RedissonLock;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.common.core.exception.CustomException;
import com.niuyin.common.core.utils.file.PathUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.dto.*;
import com.niuyin.model.video.vo.VideoUploadVO;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.video.annotation.VideoRepeatSubmit;
import com.niuyin.service.video.constants.QiniuVideoOssConstants;
import com.niuyin.service.video.service.IVideoService;
import com.niuyin.service.video.service.InterestPushService;
import com.niuyin.starter.file.service.AliyunOssService;
import com.niuyin.starter.file.service.FileStorageService;
import com.niuyin.starter.video.service.FfmpegVideoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.info.MultimediaInfo;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 视频表(Video)表控制层
 *
 * @author roydon
 * @since 2023-10-25 20:33:08
 */
@RestController
@RequestMapping("/api/v1")
public class VideoController {

    @Resource
    private IVideoService videoService;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AliyunOssService aliyunOssService;

    @DubboReference(mock = "return null")
    private DubboMemberService dubboMemberService;

    @Resource
    private InterestPushService interestPushService;

    @Resource
    private FfmpegVideoService ffmpegVideoService;

    @GetMapping("/test-dubbo")
    public R<?> testDubbo() {
        return R.ok(dubboMemberService.apiGetById(UserContext.getUserId()));
    }

    /**
     * 上传视频到aliyun oss测试
     *
     * @param file
     * @return
     */
    @PostMapping("/test-upload-video")
    public R<String> testUploadVideo(@RequestParam("file") MultipartFile file) {
        return R.ok(aliyunOssService.uploadVideoFile(file, "video"));
    }

    /**
     * todo 前端在dto传入一个唯一业务字段 #videoPublishDto.uniqueKey，这个唯一key可以使用雪花生成的视频id
     *
     * @param videoPublishDto
     * @return
     */
    @VideoRepeatSubmit(key = "#videoPublishDto.coverImage")
    @PostMapping("/test-video-repeat-submit")
    public R<String> testVideoRepeatSubmit(@RequestBody VideoPublishDto videoPublishDto) {
        return R.ok();
    }

    //    /**
//     * 测试redisson分布式锁
//     *
//     * @return
//     */
//    @GetMapping("/test-redisson-lock")
//    @RedissonLock(prefixKey = "redisson:lock", key = "test")
//    public R<String> testRateLimit() {
//        return R.ok("test rate limit");
//    }

    /**
     * 测试分布式锁
     * 测试二级缓存
     * http://127.0.0.1:9301/api/v1/testRedissonLock?id=123
     *
     * @param id
     * @return
     */
    @DoubleCache(cachePrefix = "aaatest:double:cache", key = "#id", expire = 10, unit = TimeUnit.MINUTES)
    @RedissonLock(prefixKey = "aaaredisson:lock", key = "#id")
    @GetMapping("/testRedissonLock")
    public R<String> testRedissonLock(@RequestParam("id") Long id) {
        return R.ok("testRedissonLock");
    }

    /**
     * 首页推送视频
     *
     * @return
     */
    @GetMapping("/pushVideo")
    public R<?> pushVideo() {
        return R.ok(videoService.pushVideoList());
    }

    /**
     * 热门视频
     *
     * @param pageDTO
     * @return
     */
    @PostMapping("/hot")
    @Cacheable(value = "hotVideos", key = "'hotVideos'+#pageDTO.pageNum + '_' + #pageDTO.pageSize")
    public PageDataInfo hotVideos(@RequestBody PageDTO pageDTO) {
        return videoService.getHotVideos(pageDTO);
    }

    /**
     * 视频流接口,默认返回10条数据
     */
    @PostMapping("/feed")
    public R<List<VideoVO>> feed(@RequestBody VideoFeedDTO videoFeedDTO) {
        return R.ok(videoService.feedVideo(videoFeedDTO));
    }

    /**
     * 视频上传 todo 上传视频业务转移到creator创作者中心
     */
    @PostMapping("/upload")
    public R<VideoUploadVO> uploadVideo(@RequestParam("file") MultipartFile file) {
        return R.ok(videoService.uploadVideo(file));
    }

    /**
     * 图片上传
     */
    @Deprecated
    @PostMapping("/upload/image")
    public R<String> uploadImages(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isNull(originalFilename)) {
            throw new CustomException(HttpCodeEnum.IMAGE_TYPE_FOLLOW);
        }
        //对原始文件名进行判断
        if (originalFilename.endsWith(".png")
                || originalFilename.endsWith(".jpg")
                || originalFilename.endsWith(".jpeg")
                || originalFilename.endsWith(".webp")) {
            String filePath = PathUtils.generateFilePath(originalFilename);
            String url = fileStorageService.uploadImgFile(file, QiniuVideoOssConstants.VIDEO_ORIGIN_PREFIX_URL, filePath);
            return R.ok(url);
        } else {
            throw new CustomException(HttpCodeEnum.IMAGE_TYPE_FOLLOW);
        }
    }

    /**
     * 将用户上传的视频和用户信息绑定到一起
     */
    @PostMapping("/publish")
    public R<?> videoPublish(@RequestBody VideoPublishDto videoPublishDto) {
        return R.ok(videoService.videoPublish(videoPublishDto));
    }

    /**
     * 分页查询我的视频
     */
    @PostMapping("/mypage")
    public PageDataInfo myPage(@RequestBody VideoPageDto pageDto) {
        return videoService.queryMyVideoPage(pageDto);
    }

    /**
     * 分页查询用户视频
     */
    @PostMapping("/userpage")
    public PageDataInfo userPage(@RequestBody VideoPageDto pageDto) {
        return videoService.queryUserVideoPage(pageDto);
    }

    /**
     * 通过ids获取video集合
     */
    @GetMapping("/videoVO/{videoId}")
    public R<VideoVO> queryVideoVOByVideoId(@PathVariable("videoId") String videoId) {
        return R.ok(videoService.getVideoVOById(videoId));
    }

    /**
     * 通过ids获取video集合
     */
    @GetMapping("{videoIds}")
    public R<List<Video>> queryVideoByVideoIds(@PathVariable("videoIds") List<String> videoIds) {
        return R.ok(videoService.queryVideoByVideoIds(videoIds));
    }

    /**
     * 更新视频
     */
    @PutMapping("/update")
    public R<?> updateVideo(@RequestBody UpdateVideoDTO updateVideoDTO) {
        return R.ok(videoService.updateVideo(updateVideoDTO));
    }

    /**
     * 删除视频
     */
    @DeleteMapping("/{videoId}")
    public R<?> deleteVideoByVideoIds(@PathVariable("videoId") String videoId) {
        return R.ok(videoService.deleteVideoByVideoId(videoId));
    }

    /**
     * 用户视频总获赞量
     */
    @GetMapping("/likeNums/{userId}")
    public R<Long> getVideoLikeAllNumByUserId(@PathVariable("userId") Long userId) {
        return R.ok(videoService.getVideoLikeAllNumByUserId(userId));
    }

    /**
     * 查询我的作品数量
     */
    @GetMapping("/videoCount")
    public R<Long> getUserVideoNum() {
        return R.ok(videoService.queryUserVideoCount());
    }

    /**
     * 根据视频远程url获取视频详情
     */
    @PostMapping("/videoinfo")
    public R<?> getVideoInfo(@RequestBody VideoInfoDTO videoInfoDTO) {
        MultimediaInfo info = ffmpegVideoService.getVideoInfo(videoInfoDTO.getVideoUrl());
        return R.ok(info);
    }

    /**
     * 相关视频推荐
     */
    @GetMapping("/relate/{videoId}")
    public R<?> getRelateVideo(@PathVariable("videoId") String videoId) {
        return R.ok(videoService.getRelateVideoList(videoId));
    }

}
