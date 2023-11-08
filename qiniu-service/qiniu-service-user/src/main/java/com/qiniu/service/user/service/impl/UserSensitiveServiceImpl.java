package com.qiniu.service.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.model.user.domain.UserSensitive;
import com.qiniu.service.user.mapper.UserSensitiveMapper;
import com.qiniu.service.user.service.IUserSensitiveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * 用户敏感词信息表(UserSensitive)表服务实现类
 *
 * @author roydon
 * @since 2023-10-29 20:41:18
 */
@Service("userSensitiveService")
public class UserSensitiveServiceImpl extends ServiceImpl<UserSensitiveMapper, UserSensitive> implements IUserSensitiveService {
    @Resource
    private UserSensitiveMapper userSensitiveMapper;

}
