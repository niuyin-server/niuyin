package com.niuyin.service.video.controller.v1;

import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.file.PathUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.dto.UpdateVideoDTO;
import com.niuyin.model.video.dto.VideoFeedDTO;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.model.video.dto.VideoPublishDto;
import com.niuyin.model.video.vo.VideoUploadVO;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.video.constants.QiniuVideoOssConstants;
import com.niuyin.service.video.service.IVideoImageService;
import com.niuyin.service.video.service.IVideoPositionService;
import com.niuyin.service.video.service.IVideoService;
import com.niuyin.service.video.service.InterestPushService;
import com.niuyin.starter.file.service.AliyunOssService;
import com.niuyin.starter.file.service.FileStorageService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

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

    @DubboReference
    private DubboMemberService dubboMemberService;

    @Resource
    private InterestPushService interestPushService;

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

}
