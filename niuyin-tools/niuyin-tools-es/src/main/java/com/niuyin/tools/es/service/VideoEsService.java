package com.niuyin.tools.es.service;

import com.niuyin.model.search.vo.VideoSearchVO;
import com.niuyin.tools.es.repository.VideoEsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * VideoEsService
 *
 * @AUTHOR: roydon
 * @DATE: 2025/4/29
 **/
@Service
public class VideoEsService {
    private final VideoEsRepository videoEsRepository;

    public VideoEsService(VideoEsRepository videoEsRepository) {
        this.videoEsRepository = videoEsRepository;
    }

    public VideoSearchVO save(VideoSearchVO video) {
        return videoEsRepository.save(video);
    }

    // 根据 ID 查询
    public Optional<VideoSearchVO> findById(String videoId) {
        return videoEsRepository.findById(videoId);
    }

    // 删除
    public void deleteProduct(String videoId) {
        videoEsRepository.deleteById(videoId);
    }

    public List<VideoSearchVO> findByVideoTitle(String videoTitle) {
        return videoEsRepository.findByVideoTitle(videoTitle);
    }

}
