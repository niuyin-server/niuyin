package com.niuyin.service.member.controller.v1;

import cn.hutool.core.util.PhoneUtil;
import com.niuyin.common.core.constant.Constants;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.exception.CustomException;
import com.niuyin.common.cache.service.RedisService;
import com.niuyin.common.core.utils.EmailUtils;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.member.dto.LoginUserDTO;
import com.niuyin.model.member.dto.RegisterBody;
import com.niuyin.model.member.dto.UpdatePasswordDTO;
import com.niuyin.model.member.enums.LoginTypeEnum;
import com.niuyin.model.member.vo.MemberInfoVO;
import com.niuyin.service.member.constants.UserCacheConstants;
import com.niuyin.service.member.service.IMemberInfoService;
import com.niuyin.service.member.service.IMemberService;
import com.niuyin.service.member.strategy.context.LoginStrategyContext;
import com.niuyin.starter.file.service.AliyunOssService;
import com.niuyin.starter.file.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * UserController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class MemberController {

    @Resource
    private IMemberService memberService;

    @Resource
    private RedisService redisService;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private IMemberInfoService memberInfoService;

    @Resource
    private AliyunOssService aliyunOssService;

    @Resource
    LoginStrategyContext loginStrategyContext;

    /**
     * 登录
     *
     * @param loginUserDTO
     * @return
     */
    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody LoginUserDTO loginUserDTO) {
        log.debug("登录用户：{}", loginUserDTO);
//        String token = memberService.login(loginUserDTO);
        String token = loginStrategyContext.executeLoginStrategy(loginUserDTO, LoginTypeEnum.UP);
        Map<String, String> map = new HashMap<>();
        map.put(Constants.TOKEN, token);
        return R.ok(map);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody RegisterBody registerBody) {
        log.debug("注册用户：{}", registerBody);
        boolean b = memberService.register(registerBody);
        return R.ok(b);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public R<Member> save(@RequestBody Member user) {
        // 校验邮箱
        if (StringUtils.isNotEmpty(user.getEmail()) && !EmailUtils.isValidEmail(user.getEmail())) {
            throw new CustomException(HttpCodeEnum.EMAIL_VALID_ERROR);
        }
        // 校验手机号
        if (StringUtils.isNotEmpty(user.getTelephone()) && !PhoneUtil.isPhone(user.getTelephone())) {
            throw new CustomException(HttpCodeEnum.TELEPHONE_VALID_ERROR);
        }
        return R.ok(memberService.updateUserInfo(user));
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public R<Member> userInfoById(@PathVariable Long userId) {
        return R.ok(getUserFromCache(userId));
    }


    /**
     * 通过token获取用户信息
     */
    @GetMapping("/userinfo")
    public R<MemberInfoVO> userInfo() {
        Long userId = UserContext.getUser().getUserId();
        if (StringUtils.isNull(userId)) {
            R.fail(HttpCodeEnum.NEED_LOGIN.getCode(), "请先登录");
        }
        return R.ok(getUserFromCache(userId));
    }

    /**
     * 从缓存获取用户详情
     */
    private MemberInfoVO getUserFromCache(Long userId) {
        Member userCache = redisService.getCacheObject(UserCacheConstants.USER_INFO_PREFIX + userId);
        // 用户详情
        MemberInfo memberInfo = memberInfoService.queryInfoByUserId(userId);
        if (StringUtils.isNotNull(userCache)) {
            MemberInfoVO memberInfoVO = BeanCopyUtils.copyBean(userCache, MemberInfoVO.class);
            memberInfoVO.setMemberInfo(memberInfo);
            return memberInfoVO;
        }
        Member user = memberService.queryById(userId);
        user.setPassword(null);
        user.setSalt(null);
        MemberInfoVO memberInfoVO = BeanCopyUtils.copyBean(user, MemberInfoVO.class);
        memberInfoVO.setMemberInfo(memberInfo);
        // 设置缓存
        redisService.setCacheObject(UserCacheConstants.USER_INFO_PREFIX + userId, user);
        redisService.expire(UserCacheConstants.USER_INFO_PREFIX + userId, UserCacheConstants.USER_INFO_EXPIRE_TIME, TimeUnit.SECONDS);
        return memberInfoVO;
    }

    /**
     * 修改密码
     *
     * @param dto 原密码，新密码，确认密码
     * @return user
     */
    @PostMapping("/updatepass")
    public R<Boolean> updatePass(@RequestBody UpdatePasswordDTO dto) {
        return R.ok(memberService.updatePass(dto));
    }

    /**
     * 头像上传
     *
     * @param file 图片文件，大小限制1M
     * @return url
     */
    @PostMapping("/avatar")
    public R<String> avatar(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isNull(originalFilename)) {
            throw new CustomException(HttpCodeEnum.IMAGE_TYPE_FOLLOW);
        }
        //对原始文件名进行判断
        if (originalFilename.endsWith(".png")
                || originalFilename.endsWith(".jpg")
                || originalFilename.endsWith(".jpeg")
                || originalFilename.endsWith(".webp")) {
            return R.ok(aliyunOssService.uploadFile(file, "member"));
        } else {
            throw new CustomException(HttpCodeEnum.IMAGE_TYPE_FOLLOW);
        }
    }

}
