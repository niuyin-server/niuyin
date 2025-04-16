package com.niuyin.service.member.controller.v1;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.exception.CustomException;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.service.member.service.IMemberInfoService;
import com.niuyin.starter.file.service.AliyunOssService;
import com.niuyin.starter.file.service.FileStorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;

/**
 * 会员详情表(MemberInfo)表控制层
 *
 * @author roydon
 * @since 2023-11-12 22:26:24
 */
@RestController
@RequestMapping("/api/v1/info")
public class MemberInfoController {

    @Resource
    private IMemberInfoService memberInfoService;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AliyunOssService aliyunOssService;

    /**
     * 上传用户背景图片
     */
    @PostMapping("/backImage/upload")
    public R<String> uploadBackImage(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isNull(originalFilename)) {
            throw new CustomException(HttpCodeEnum.IMAGE_TYPE_FOLLOW);
        }
        //对原始文件名进行判断
        if (originalFilename.endsWith(".png")
                || originalFilename.endsWith(".jpg")
                || originalFilename.endsWith(".jpeg")
                || originalFilename.endsWith(".gif")
                || originalFilename.endsWith(".webp")) {
//            String filePath = PathUtils.generateFilePath(originalFilename);
//            String url = fileStorageService.uploadImgFile(file, QiniuUserOssConstants.PREFIX_URL, filePath);
            return R.ok(aliyunOssService.uploadFile(file, "member/backImage"));
        } else {
            throw new CustomException(HttpCodeEnum.IMAGE_TYPE_FOLLOW);
        }
    }

    @PutMapping("/update")
    public R<Boolean> updateMemberInfo(@RequestBody MemberInfo memberInfo) {
        return R.ok(memberInfoService.saveOrUpdate(memberInfo));
    }

}

