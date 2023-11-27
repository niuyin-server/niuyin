package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.video.domain.UserVideoCompilation;
import com.niuyin.service.video.mapper.UserVideoCompilationMapper;
import com.niuyin.service.video.service.IUserVideoCompilationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户视频合集表(UserVideoCompilation)表服务实现类
 *
 * @author roydon
 * @since 2023-11-27 18:08:39
 */
@Service("userVideoCompilationService")
public class UserVideoCompilationServiceImpl extends ServiceImpl<UserVideoCompilationMapper, UserVideoCompilation> implements IUserVideoCompilationService {
    @Resource
    private UserVideoCompilationMapper userVideoCompilationMapper;

}
