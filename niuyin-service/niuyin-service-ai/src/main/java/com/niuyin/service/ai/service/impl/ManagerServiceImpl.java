package com.niuyin.service.ai.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.exception.CustomException;
import com.niuyin.common.core.utils.JwtUtil;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.model.ai.AiManagerDO;
import com.niuyin.model.ai.AiManagerUserInfo;
import com.niuyin.model.member.domain.Member;
import com.niuyin.service.ai.controller.admin.AdminManagerController;
import com.niuyin.service.ai.mapper.ManagerMapper;
import com.niuyin.service.ai.service.IManagerService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Objects;

import static com.niuyin.model.common.enums.HttpCodeEnum.PASSWORD_ERROR;
import static com.niuyin.model.common.enums.HttpCodeEnum.USER_NOT_EXISTS;

/**
 * AI管理员表(AiManager)表服务实现类
 *
 * @author roydon
 * @since 2025-05-30 23:39:17
 */
@Service
public class ManagerServiceImpl extends ServiceImpl<ManagerMapper, AiManagerDO> implements IManagerService {

    @DubboReference(retries = 3, mock = "return null")
    private DubboMemberService dubboMemberService;

    @Override
    public String login(AdminManagerController.LoginDTO dto) {
        Member member = dubboMemberService.apiGetByUsername(dto.username());
        if (Objects.isNull(member)) {
            throw new CustomException(USER_NOT_EXISTS);
        }
        // 比对密码
        String salt = member.getSalt();
        String pswd = dto.password();
        pswd = DigestUtils.md5DigestAsHex((pswd + salt).getBytes());
        if (!pswd.equals(member.getPassword())) {
            throw new CustomException(PASSWORD_ERROR);
        }
        // ai管理员表是否存在该用户
        AiManagerDO manager = this.getOne(Wrappers.<AiManagerDO>lambdaQuery().eq(AiManagerDO::getUserId, member.getUserId()));
        if (Objects.isNull(manager)) {
            throw new RuntimeException("该用户不是AI管理员");
        }
        return JwtUtil.getToken(member.getUserId());
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @Override
    public AiManagerUserInfo userInfo() {
        // todo 用token的userId为主键 查询管理员，校验状态

        // todo 查询用户信息
        Member member = dubboMemberService.apiGetById(UserContext.getUserId());
        return BeanCopyUtils.copyBean( member, AiManagerUserInfo.class);
    }
}
