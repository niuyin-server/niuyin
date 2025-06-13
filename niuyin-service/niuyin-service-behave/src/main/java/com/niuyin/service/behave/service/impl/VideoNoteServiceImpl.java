package com.niuyin.service.behave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.model.behave.domain.VideoNote;
import com.niuyin.model.behave.dto.VideoNotePageDTO;
import com.niuyin.model.behave.enums.VideoNoteDelFlagEnum;
import com.niuyin.model.behave.vo.VideoNoteVO;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.vo.Author;
import com.niuyin.service.behave.mapper.VideoNoteMapper;
import com.niuyin.service.behave.service.IVideoNoteService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 视频笔记表(VideoNote)表服务实现类
 *
 * @author roydon
 * @since 2024-05-05 18:51:05
 */
@Service("videoNoteService")
public class VideoNoteServiceImpl extends ServiceImpl<VideoNoteMapper, VideoNote> implements IVideoNoteService {

    @Resource
    private VideoNoteMapper videoNoteMapper;

    @DubboReference(mock = "return null")
    private DubboMemberService dubboMemberService;

    /**
     * 分页
     *
     * @param pageDTO
     * @return
     */
    @Override
    public PageData<VideoNoteVO> queryVideoNotePage(VideoNotePageDTO pageDTO) {
        LambdaQueryWrapper<VideoNote> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoNote::getDelFlag, VideoNoteDelFlagEnum.NORMAL.getCode())
                .eq(VideoNote::getVideoId, pageDTO.getVideoId())
                .orderByDesc(VideoNote::getCreateTime);
        Page<VideoNote> page = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), queryWrapper);
        List<VideoNote> records = page.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return PageData.emptyPage();
        }
        List<VideoNoteVO> voList = BeanCopyUtils.copyBeanList(records, VideoNoteVO.class);
        voList.forEach(v -> {
            v.setNoteContent(v.getNoteContent().substring(0, Math.min(v.getNoteContent().length(), 200)));
            Member member = dubboMemberService.apiGetById(v.getUserId());
            if (!Objects.isNull(member)) {
                Author author = BeanCopyUtils.copyBean(member, Author.class);
                v.setAuthor(author);
            }
        });
        return PageData.genPageData(voList, page.getTotal());
    }

}
