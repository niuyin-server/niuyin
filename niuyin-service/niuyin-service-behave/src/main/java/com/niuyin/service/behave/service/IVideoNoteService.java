package com.niuyin.service.behave.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.model.behave.domain.VideoNote;
import com.niuyin.model.behave.dto.VideoNotePageDTO;
import com.niuyin.model.behave.vo.VideoNoteVO;

/**
 * 视频笔记表(VideoNote)表服务接口
 *
 * @author roydon
 * @since 2024-05-05 18:51:05
 */
public interface IVideoNoteService extends IService<VideoNote> {

    /**
     * 分页
     * @param pageDTO
     * @return
     */
    PageData<VideoNoteVO> queryVideoNotePage(VideoNotePageDTO pageDTO);

}
