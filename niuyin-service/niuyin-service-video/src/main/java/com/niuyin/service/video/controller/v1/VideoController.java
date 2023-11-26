package com.niuyin.service.video.controller.v1;

import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.file.PathUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.dto.VideoFeedDTO;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.model.video.dto.VideoPublishDto;
import com.niuyin.model.video.vo.VideoUploadVO;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.video.constants.QiniuVideoOssConstants;
import com.niuyin.service.video.service.IVideoImageService;
import com.niuyin.service.video.service.IVideoPositionService;
import com.niuyin.service.video.service.IVideoService;
import com.niuyin.starter.file.service.FileStorageService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
    private RedisService redisService;

    @Resource
    private RemoteMemberService remoteMemberService;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private IVideoImageService videoImageService;

    @Resource
    private IVideoPositionService videoPositionService;

    /**
     * 热门视频
     *
     * @param pageDTO
     * @return
     */

    @PostMapping("/hot")
    @Cacheable(value = "hotVideos", key = "'hotVideos'+#pageDTO.pageNum + '_' + #pageDTO.pageSize")
    public PageDataInfo hotVideos(@RequestBody PageDTO pageDTO) {
        return videoService.getHotvideos(pageDTO);
    }

    /**
     * 视频流接口,默认返回5条数据 todo 点赞数、评论数、收藏数单独封装
     */
    @PostMapping("/feed")
    public R<List<VideoVO>> feed(@RequestBody VideoFeedDTO videoFeedDTO) {
        return R.ok(videoService.feedVideo(videoFeedDTO));
    }

    /**
     * 视频上传
     */
    @PostMapping("/upload")
    public R<VideoUploadVO> uploadVideo(@RequestParam("file") MultipartFile file) {
        return R.ok(videoService.uploadVideo(file));
    }

    /**
     * 图片上传
     */
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
     *
     * @param videoPublishDto
     * @return
     */
    @PostMapping("/publish")
    public R<?> videoPublish(@RequestBody VideoPublishDto videoPublishDto) {
        return R.ok(videoService.videoPublish(videoPublishDto));
    }

    /**
     * 分页查询我的视频
     *
     * @param pageDto
     * @return
     */
    @PostMapping("/mypage")
    public PageDataInfo myPage(@RequestBody VideoPageDto pageDto) {
        return videoService.queryMyVideoPage(pageDto);
    }

    /**
     * 分页查询用户视频
     *
     * @param pageDto
     * @return
     */
    @PostMapping("/userpage")
    public PageDataInfo userPage(@RequestBody VideoPageDto pageDto) {
        return  videoService.queryUserVideoPage(pageDto);
    }

    /**
     * 通过ids获取video集合
     *
     * @param videoIds
     * @return
     */
    @GetMapping("{videoIds}")
    public R<List<Video>> queryVideoByVideoIds(@PathVariable("videoIds") List<String> videoIds) {
        return R.ok(videoService.queryVideoByVideoIds(videoIds));
    }

    @DeleteMapping("/{videoId}")
    public R<?> deleteVideoByVideoIds(@PathVariable("videoId") String videoId) {
        videoService.deleteVideoByVideoId(videoId);
        return null;
    }

    /**
     * 用户视频总获赞量
     *
     * @param userId
     * @return
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

//    /**
//     * todo 查询用户的作品，整到userpage 第126行
//     *
//     * @param pageDto
//     * @return
//     */
//    @PostMapping("/personVideoPage")
//    public PageDataInfo memberInfoPage(@RequestBody VideoPageDto pageDto) {
//        IPage<Video> videoIPage = videoService.queryMemberVideoPage(pageDto);
//        List<Video> records = videoIPage.getRecords();
//        if (StringUtils.isNull(records) || records.isEmpty()) {
//            return PageDataInfo.emptyPage();
//        }
//        List<VideoVO> videoVOList = new ArrayList<>();
//        records.forEach(r -> {
//            VideoVO videoVO = BeanCopyUtils.copyBean(r, VideoVO.class);
//            // 若是图文则封装图片集合
//            if (r.getPublishType().equals(PublishType.IMAGE.getCode())) {
//                List<VideoImage> videoImageList = videoImageService.queryImagesByVideoId(videoVO.getVideoId());
//                String[] imgs = videoImageList.stream().map(VideoImage::getImageUrl).toArray(String[]::new);
//                videoVO.setImageList(imgs);
//            }
//            // 若是开启定位，封装定位
//            if (r.getPositionFlag().equals(PositionFlag.OPEN.getCode())) {
//                VideoPosition videoPosition = videoPositionService.queryPositionByVideoId(videoVO.getVideoId());
//                videoVO.setPosition(videoPosition);
//            }
//            videoVOList.add(videoVO);
//        });
//        return PageDataInfo.genPageData(videoVOList, videoIPage.getTotal());
//    }

}
