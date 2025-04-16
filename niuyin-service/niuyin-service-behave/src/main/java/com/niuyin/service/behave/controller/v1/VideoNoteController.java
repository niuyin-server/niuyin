package com.niuyin.service.behave.controller.v1;

import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.behave.domain.VideoNote;
import com.niuyin.model.behave.dto.VideoNotePageDTO;
import com.niuyin.model.behave.vo.VideoNoteVO;
import com.niuyin.service.behave.service.IVideoNoteService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 视频笔记表(VideoNote)表控制层
 *
 * @author roydon
 * @since 2024-05-05 18:51:04
 */
@RestController
@RequestMapping("/api/v1/videoNote")
public class VideoNoteController {

    @Resource
    private IVideoNoteService videoNoteService;

    /**
     * 分页查询
     */
    @PostMapping("/page")
    public PageDataInfo<VideoNoteVO> queryVideoNotePage(@Validated @RequestBody VideoNotePageDTO pageDTO) {
        return this.videoNoteService.queryVideoNotePage(pageDTO);
    }

    /**
     * 通过主键查询单条数据
     */
    @GetMapping("/{noteId}")
    public R<?> queryById(@PathVariable("noteId") Long noteId) {
        return R.ok(this.videoNoteService.getById(noteId));
    }

    /**
     * 新增数据
     */
    @PostMapping
    public R<?> add(@Validated @RequestBody VideoNote videoNote) {
        videoNote.setUserId(UserContext.getUserId());
        videoNote.setCreateTime(LocalDateTime.now());
        videoNote.setCreateBy(UserContext.getUserId());
        return R.ok(this.videoNoteService.save(videoNote));
    }

    /**
     * 编辑数据
     */
    @PutMapping
    public R<?> edit(@Validated @RequestBody VideoNote videoNote) {
        videoNote.setUpdateTime(LocalDateTime.now());
        videoNote.setUpdateBy(UserContext.getUserId());
        return R.ok(this.videoNoteService.updateById(videoNote));
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/{noteId}")
    public R<?> removeById(@PathVariable("noteId") Long noteId) {
        return R.ok(this.videoNoteService.removeById(noteId));
    }

}

