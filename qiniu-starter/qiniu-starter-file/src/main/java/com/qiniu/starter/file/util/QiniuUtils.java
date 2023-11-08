package com.qiniu.starter.file.util;

import cn.hutool.core.util.IdUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import lombok.extern.slf4j.Slf4j;

/**
 * QiniuUtils
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/3
 **/
@Slf4j
public class QiniuUtils {

    //删除文件
    public static void deleteFileFromQiniu(String fileName, String accessKey, String secretKey, String bucket) {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration();
        String key = fileName;
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }

    /**
     * 转码
     */
    public static void transcoding(String name, String accessKey, String secretKey, String bucket) {

        Auth auth = Auth.create(accessKey, secretKey);
//        String key = "ce104c91-7f82-493a-9ea6-71afae7e76c44.mp4";
        //存储空间中视频的文件名称
        String newName = IdUtil.fastUUID() + ".mp4"; //转码后，另存的文件名称
        log.info("转码后文件名称：{}", newName);
//        String newKey = "H264_type.mp4";
        String pipeline = "default.sys";  //处理队列
        Configuration cfg = new Configuration();
        String saveAs = UrlSafeBase64.encodeToString(bucket + ":" + newName);        //saveas接口 参数
        String fops = "avthumb/mp4/vcodec/libx264|saveas/" + saveAs;                //处理命令 avthumb 和 saveas 通过管道符 |  进行连接
        OperationManager operationMgr = new OperationManager(auth, cfg);
        try {
            //执行转码和另存 操作
            String persistentId = operationMgr.pfop(bucket, name, fops, new StringMap().put("persistentPipeline", pipeline));
            System.out.println(persistentId);
        } catch (QiniuException e) {
            System.out.println(e.response.statusCode);
            e.printStackTrace();
        }
    }
}
