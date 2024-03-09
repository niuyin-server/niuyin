package com.niuyin.service.member.strategy.login;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.utils.IpUtils;
import com.niuyin.common.utils.ServletUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.member.dto.LoginUserDTO;
import com.niuyin.model.member.enums.LoginTypeEnum;
import com.niuyin.service.member.service.IMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.niuyin.model.common.enums.HttpCodeEnum.*;

/**
 * UpLoginStrategy
 *
 * @AUTHOR: roydon
 * @DATE: 2024/3/9
 **/
@Slf4j
@Service("upLoginStrategy")
public class UpLoginStrategy extends AbstractLoginStrategyService<LoginUserDTO> {


    @Resource
    private IMemberService memberService;

    /**
     * 策略名称
     */
    @Override
    protected String getStrategyName() {
        return LoginTypeEnum.UP.getStrategy();
    }

    /**
     * 校验用户
     *
     * @param loginDto
     */
    @Override
    protected Long verifyCredentials(LoginUserDTO loginDto) {
        log.debug("UpLoginStrategy verifyCredentials");
        //1.检查参数
        if (StringUtils.isBlank(loginDto.getUsername()) || StringUtils.isBlank(loginDto.getPassword())) {
            throw new CustomException(SYSTEM_ERROR);
        }
        //2.查询用户
        Member dbUser = memberService.getOne(Wrappers.<Member>lambdaQuery().eq(Member::getUserName, loginDto.getUsername()));
        if (dbUser == null) {
            throw new CustomException(USER_NOT_EXISTS);
        }
        //3.比对密码
        String salt = dbUser.getSalt();
        String pswd = loginDto.getPassword();
        pswd = DigestUtils.md5DigestAsHex((pswd + salt).getBytes());
        if (pswd.equals(dbUser.getPassword())) {
            return dbUser.getUserId();
        } else {
            throw new CustomException(PASSWORD_ERROR);
        }
    }

    @Override
    protected void recordLoginUserInfo(Long userId) {
        Member user = new Member();
        user.setUserId(userId);
        user.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        user.setLoginLocation(IpUtils.getIpLocation(ServletUtils.getRequest()));
        user.setLoginDate(LocalDateTime.now());
        memberService.updateById(user);
    }
}
