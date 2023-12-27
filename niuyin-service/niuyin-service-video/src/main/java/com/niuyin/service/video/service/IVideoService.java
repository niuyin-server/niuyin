package com.niuyin.service.video.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.dto.UpdateVideoDTO;
import com.niuyin.model.video.dto.VideoFeedDTO;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.model.video.dto.VideoPublishDto;
import com.niuyin.model.video.vo.HotVideoVO;
import com.niuyin.model.video.vo.VideoUploadVO;
import com.niuyin.model.video.vo.VideoVO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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
    PageDataInfo queryMyVideoPage(VideoPageDto pageDto);

    /**
     * 分页用户视频
     *
     * @param pageDto
     * @return
     */
    PageDataInfo queryUserVideoPage(VideoPageDto pageDto);

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
     * 删除视频
     *
     * @param videoId
     */
    boolean deleteVideoByVideoId(String videoId);

    boolean deleteVideoByUser(String videoId);

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

    /**
     * 视频总获赞量
     *
     * @param userId
     * @return
     */
    Long getVideoLikeAllNumByUserId(Long userId);

    /**
     * 查询用户作品数量
     *
     * @return
     */
    Long queryUserVideoCount();

    /**
     * 查询用户的作品
     *
     * @param pageDto
     * @return
     */
    IPage<Video> queryMemberVideoPage(VideoPageDto pageDto);


    /**
     * 热门视频查询
     *
     * @param pageDTO
     * @return
     */
    PageDataInfo getHotVideos(PageDTO pageDTO);

    List<VideoVO> pushVideoList();

    /**
     * 更新视频
     */
    boolean updateVideo(UpdateVideoDTO updateVideoDTO);

}
