package com.niuyin.service.video.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.video.domain.UserVideoCompilationRelation;
import com.niuyin.model.video.dto.CompilationVideoPageDTO;
import com.niuyin.model.video.vo.CompilationVideoVO;

import java.util.List;

/**
 * 用户视频合集与视频关联表(UserVideoCompilationRelation)表服务接口
 *
 * @author roydon
 * @since 2023-12-08 20:21:13
 */
public interface IUserVideoCompilationRelationService extends IService<UserVideoCompilationRelation> {

    /**
     * 将视频添加到合集
     *
     * @param videoId
     * @param compilationId
     * @return
     */
    Boolean videoRelateCompilation(String videoId, Long compilationId);

    /**
     * 删除视频
     *
     * @param videoId
     * @return
     */
    boolean deleteRecordByVideoId(String videoId);

    /**
     * 分页查询合集下视频
     *
     * @param pageDTO
     * @return
     */
    IPage<UserVideoCompilationRelation> compilationVideoPage(CompilationVideoPageDTO pageDTO);

    /**
     * 合集视频分页查询
     * @param pageDTO
     * @return
     */
    List<CompilationVideoVO> compilationVideoPageList(CompilationVideoPageDTO pageDTO);
    Long compilationVideoPageCount(Long compilationId);

    Long getCompilationIdByVideoId(String videoId);
}
