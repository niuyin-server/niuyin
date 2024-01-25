package com.niuyin.service.video;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.Mimetypes;
import com.aliyun.oss.model.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niuyin.common.utils.video.FfmpegUtil;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.uniqueid.IdGenerator;
import com.niuyin.model.search.vo.VideoSearchVO;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoCategoryRelation;
import com.niuyin.model.video.domain.VideoImage;
import com.niuyin.model.video.domain.VideoSensitive;
import com.niuyin.model.video.dto.VideoPublishDto;
import com.niuyin.model.behave.vo.VideoUserLikeAndFavoriteVo;
import com.niuyin.service.video.constants.VideoCacheConstants;
import com.niuyin.service.video.mapper.VideoMapper;
import com.niuyin.service.video.mapper.VideoSensitiveMapper;
import com.niuyin.service.video.service.IVideoCategoryRelationService;
import com.niuyin.service.video.service.IVideoCategoryService;
import com.niuyin.service.video.service.IVideoImageService;
import com.niuyin.service.video.service.IVideoService;
import com.aliyun.oss.ClientException;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.niuyin.starter.video.service.FfmpegVideoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ws.schild.jave.info.MultimediaInfo;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.niuyin.model.common.enums.HttpCodeEnum.*;
import static com.niuyin.service.video.constants.VideoCacheConstants.VIDEO_IMAGES_PREFIX_KEY;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/29 16:07
 */
@Slf4j
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
    private RemoteMemberService remoteMemberService;

    @Resource
    private IVideoCategoryRelationService videoCategoryRelationService;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    FfmpegVideoService ffmpegVideoService;

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
        queryWrapper.select(VideoSensitive::getId).like(VideoSensitive::getSensitives, s1).or(w -> w.like(VideoSensitive::getSensitives, s3));
        List<VideoSensitive> videoSensitives = videoSensitiveMapper.selectList(queryWrapper);
        videoSensitives.size();
    }

    @Test
    void publishTest() {
        Long userId = 3L;
        String videoTitle = "御姐";
        String videoDesc = "甜心大姐姐";
        String videoUrl = "http://s4vqrd8fr.hn-bkt.clouddn.com/2023/11/02/d1e511dd3e754c3fa23e872f608dd914.mp4";
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
        queryWrapper.select(VideoSensitive::getId).like(VideoSensitive::getSensitives, videoPublishDto.getVideoTitle()).or(w -> w.like(VideoSensitive::getSensitives, videoPublishDto.getVideoDesc()));
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
    void test() {
        char s1 = 97;
        char s2 = 'a';

        System.out.println(s1);
        System.out.println(s2);

    }

    @Test
    void selVideoLike() {
        Long l = videoMapper.selectUserLikeVideo("117393770688387481650cb56ac", 2L);
        System.out.println("l = " + l);
    }

    @DisplayName("测试依据创建时间算分")
    @Test
    void testCreateTimeScore() {
        Video video = videoService.getById("1175158246101483520422e68d2");
        LocalDateTime createTime = video.getCreateTime();
        Duration between = Duration.between(LocalDateTime.now(), createTime);
        long minutes = between.toMinutes(); //相差多少分钟
        System.out.println("minutes = " + minutes);
        long hours = between.toHours(); // 相差多少小时
        System.out.println("hours = " + hours);
//        int second = LocalDateTime.now().getMinute() - createTime.getMinute();
//        System.out.println("second = " + second);
    }

    @Test
    void videoTrans() {

//        URL url = new URL("http://s4vqrd8fr.hn-bkt.clouddn.com/niuyin9161201e467d4889b247a3b7a106e8e4video.mp4");
//        URLConnection urlConnection = url.openConnection();
//        InputStream inputStream = urlConnection.getInputStream();
//        File file =File.createTempFile("temp",".tmp");
//        file.deleteOnExit();
//        try(FileOutputStream outputStream = new FileOutputStream(file)) {
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//        }

        String url = "http://s4bgg8hwg.hb-bkt.clouddn.com/2023/11/18/1234.avi";
        String s = "D:\\haose\\Videos\\12\\12.mp4";
        FfmpegUtil.formatToMp4(url, s);
    }

    /**
     * 获取视频某一帧的截图
     */
    @Test
    void videoPicture() {
//        File file = new File("D:\\haose\\Videos\\1235.mp4");
//        String s="D:\\haose\\Videos\\12\\12";
//        FfmpegUtil.getVideoInfoAndGenerateThumbnail(file,s);
        String url = "http://s4bgg8hwg.hb-bkt.clouddn.com/2023/11/18/1234.avi";
        String s = "D:\\haose\\Videos\\12\\1234567.png";
        FfmpegUtil.getTargetThumbnail(url, s);
    }

    @Test
    @DisplayName("获取视频详情")
    void getVideoInfo() {
        // 横屏视频
//        String urlheng = "http://s4bi8902v.hb-bkt.clouddn.com/2023/11/27/9829cce9da304b66902fdd19c7cfbfc8.mp4";
        String urlheng = "https://niuyin-server.oss-cn-shenzhen.aliyuncs.com/video/2023/12/30/d8dcdb16964c4b8ba2bb70a72d7451ff.mp4";

        MultimediaInfo info = ffmpegVideoService.getVideoInfo(urlheng);
        log.debug("视频详情：{}", info);
    }

    @Test
    @DisplayName("视频时长毫秒值转小时HH:mm:ss")
    void videoInfoTransfer() {
        long milliseconds = 291690L; // 毫秒数

        // 使用 Duration 类将毫秒数转换为 Duration 对象
        Duration duration = Duration.ofMillis(milliseconds);

        // 使用 LocalTime.MIDNIGHT.plus() 方法将 Duration 对象与午夜时间相加得到小时时间
        LocalTime time = LocalTime.MIDNIGHT.plus(duration);

        // 使用 DateTimeFormatter 格式化 LocalTime 对象
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = time.format(formatter);

        System.out.println(formattedTime);
        log.debug("视频时长：{}", formattedTime);
    }


    @Test
    @DisplayName("远程视频生成首帧截图")
    void videoPictureTest() {
        log.debug("开始生成缩略图：");
        // 竖屏视频
        String urlshu = "http://s4vq4byfr.hn-bkt.clouddn.com/2023/11/29/18d83770d4a2480b8bfe917b5388ab96.mp4";
        String sshu = "18d83770d4a2480b8bfe917b5388ab96.jpg";

        String targetThumbnail = ffmpegVideoService.getTargetThumbnail(urlshu, sshu);
        log.debug("生成截图地址：{}", targetThumbnail);

    }

    @Test
    @DisplayName("远程视频生成三张预览截图")
    void testGenPreview() {

        log.debug("开始生成缩略图：");
//        // 竖屏视频
//        String urlshu = "http://s4vq4byfr.hn-bkt.clouddn.com/2023/11/29/18d83770d4a2480b8bfe917b5388ab96.mp4";
//        String sshu = "18d83770d4a2480b8bfe917b5388ab96-1.jpg";
//        // 横屏视频
        String urlheng = "http://s4bi8902v.hb-bkt.clouddn.com/2023/11/27/9829cce9da304b66902fdd19c7cfbfc8.mp4";
        String sheng = "9829cce9da304b66902fdd19c7cfbfc8";
//
        String[] strs = ffmpegVideoService.generatePreviewCover(urlheng, sheng);
        for (String str : strs) {
            log.debug("生成截图地址：{}", str);
        }

        log.debug("结束生成缩略图：");

    }

    @Test
    @DisplayName("同步所有视频到redis")
    void syncVideoToRedis() {
        List<Video> videoList = videoService.list();
        videoList.forEach(v -> {
            redisService.setCacheObject(VideoCacheConstants.VIDEO_INFO_PREFIX + v.getVideoId(), v);
        });
    }

    @Resource
    IVideoImageService videoImageService;

    @Test
    @DisplayName("建立视频缓存")
    void testRedis() {
        String videoId = "117685379035194982434524946";
        List<VideoImage> videoImageList = videoImageService.queryImagesByVideoId(videoId);
        String[] imgs = videoImageList.stream().map(VideoImage::getImageUrl).toArray(String[]::new);
        // 重建缓存
        redisService.setCacheObject(VIDEO_IMAGES_PREFIX_KEY + videoId, imgs);
        redisService.expire(VIDEO_IMAGES_PREFIX_KEY + videoId, 1, TimeUnit.DAYS);
    }

    /**
     *     endpoint: oss-cn-shenzhen.aliyuncs.com
     *     bucketName: niuyin-server
     *     accessKeyId: ***
     *     accessKeySecret: ***
     */
    @Test
    @DisplayName("测试视频分片上传")
    void testVideoPartUpload() {
        log.debug("开始分片上传视频");
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-shenzhen.aliyuncs.com";
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "niuyin-server";
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName = "test/exampleobject.mp4";
        // 待上传本地文件路径。
        String filePath = "C:\\Users\\roydon\\Videos\\niuyin\\这次不卡了   地平线，启动！.mp4";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClient(endpoint, "***", "***");
        try {
            // 创建InitiateMultipartUploadRequest对象。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName);

            // 如果需要在初始化分片时设置请求头，请参考以下示例代码。
            ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // 指定该Object的网页缓存行为。
            // metadata.setCacheControl("no-cache");
            // 指定该Object被下载时的名称。
            // metadata.setContentDisposition("attachment;filename=oss_MultipartUpload.txt");
            // 指定该Object的内容编码格式。
            // metadata.setContentEncoding(OSSConstants.DEFAULT_CHARSET_NAME);
            // 指定初始化分片上传时是否覆盖同名Object。此处设置为true，表示禁止覆盖同名Object。
            // metadata.setHeader("x-oss-forbid-overwrite", "true");
            // 指定上传该Object的每个part时使用的服务器端加密方式。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION, ObjectMetadata.KMS_SERVER_SIDE_ENCRYPTION);
            // 指定Object的加密算法。如果未指定此选项，表明Object使用AES256加密算法。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_DATA_ENCRYPTION, ObjectMetadata.KMS_SERVER_SIDE_ENCRYPTION);
            // 指定KMS托管的用户主密钥。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_KEY_ID, "9468da86-3509-4f8d-a61e-6eab1eac****");
            // 指定Object的存储类型。
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard);
            // 指定Object的对象标签，可同时设置多个标签。
            // metadata.setHeader(OSSHeaders.OSS_TAGGING, "a:1");
            // request.setObjectMetadata(metadata);

            // 根据文件自动设置ContentType。如果不设置，ContentType默认值为application/oct-srream。
            if (metadata.getContentType() == null) {
                metadata.setContentType(Mimetypes.getInstance().getMimetype(new File(filePath), objectName));
            }

            // 初始化分片。
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId。
            String uploadId = upresult.getUploadId();
            // 根据uploadId执行取消分片上传事件或者列举已上传分片的操作。
            // 如果您需要根据您需要uploadId执行取消分片上传事件的操作，您需要在调用InitiateMultipartUpload完成初始化分片之后获取uploadId。
            // 如果您需要根据您需要uploadId执行列举已上传分片的操作，您需要在调用InitiateMultipartUpload完成初始化分片之后，且在调用CompleteMultipartUpload完成分片上传之前获取uploadId。
            // System.out.println(uploadId);

            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
            List<PartETag> partETags = new ArrayList<PartETag>();
            // 每个分片的大小，用于计算文件有多少个分片。单位为字节。
            final long partSize = 1024 * 1024L;   //1 MB。

            // 根据上传的数据大小计算分片数。以本地文件为例，说明如何通过File.length()获取上传数据的大小。
            final File sampleFile = new File(filePath);
            long fileLength = sampleFile.length();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(objectName);
                uploadPartRequest.setUploadId(uploadId);
                // 设置上传的分片流。
                // 以本地文件为例说明如何创建FIleInputstream，并通过InputStream.skip()方法跳过指定数据。
                InputStream instream = new FileInputStream(sampleFile);
                instream.skip(startPos);
                uploadPartRequest.setInputStream(instream);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partETags.add(uploadPartResult.getPartETag());
            }


            // 创建CompleteMultipartUploadRequest对象。
            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);

            // 如果需要在完成分片上传的同时设置文件访问权限，请参考以下示例代码。
            // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.Private);
            // 指定是否列举当前UploadId已上传的所有Part。仅在Java SDK为3.14.0及以上版本时，支持通过服务端List分片数据来合并完整文件时，将CompleteMultipartUploadRequest中的partETags设置为null。
            // Map<String, String> headers = new HashMap<String, String>();
            // 如果指定了x-oss-complete-all:yes，则OSS会列举当前UploadId已上传的所有Part，然后按照PartNumber的序号排序并执行CompleteMultipartUpload操作。
            // 如果指定了x-oss-complete-all:yes，则不允许继续指定body，否则报错。
            // headers.put("x-oss-complete-all","yes");
            // completeMultipartUploadRequest.setHeaders(headers);

            // 完成分片上传。
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println(completeMultipartUploadResult.getETag());
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }
        log.debug("结束分片上传视频");

    }


}
