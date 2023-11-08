package com.qiniu.service.video.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.common.dto.PageDTO;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.dto.VideoFeedDTO;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.model.video.dto.VideoPublishDto;
import com.qiniu.model.video.vo.HotVideoVO;
import com.qiniu.model.video.vo.VideoUploadVO;
import com.qiniu.model.video.vo.VideoVO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 视频表(Video)表服务接口
 *
 * @author roydon
 * @since 2023-10-25 20:33:11
 */
public interface IVideoService extends IService<Video> {

    /**
     * 上传视频文件，返回文件url
     *
     * @param file
     * @return
     */
    VideoUploadVO uploadVideo(MultipartFile file);

    Video selectById(String id);

    /**
     * 发布视频
     *
     * @param videoPublishDto
     * @return
     */
    String videoPublish(VideoPublishDto videoPublishDto);

    /**
     * 分页我的视频
     *
     * @param pageDto
     * @return
     */
    IPage<Video> queryMyVideoPage(VideoPageDto pageDto);

    /**
     * 分页用户视频
     *
     * @param pageDto
     * @return
     */
    IPage<Video> queryUserVideoPage(VideoPageDto pageDto);

    /**
     * 视频feed接口
     *
     * @param videoFeedDTO createTime
     * @return video
     */
    List<VideoVO> feedVideo(VideoFeedDTO videoFeedDTO);

    /**
     * 根据ids查询视频
     *
     * @param videoIds
     * @return
     */
    List<Video> queryVideoByVideoIds(List<String> videoIds);

    /**
     * 删除视频，并且将视频同步从 es中删除
     *
     * @param videoId
     */
    void deleteVideoByVideoIds(String videoId);

    /**
     * 筛选大于ctime的视频数据
     *
     * @param ctime
     * @return
     */
    List<Video> getVideoListLtCreateTime(LocalDateTime ctime);

    /**
     * 视频算分
     *
     * @param videoList
     * @return
     */
    List<HotVideoVO> computeHotVideoScore(List<Video> videoList);
}
