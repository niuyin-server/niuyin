package com.niuyin.starter.file.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.niuyin.starter.file.config.aliyun.AliyunOssConfig;
import com.niuyin.starter.file.config.aliyun.AliyunOssConfigProperties;
import com.niuyin.starter.file.service.AliyunOssService;
import com.niuyin.starter.file.util.PathUtils;
import com.niuyin.starter.file.util.file.FileUploadUtils;
import com.niuyin.starter.file.util.file.MimeTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.niuyin.starter.file.config.aliyun.AliyunOssConfigProperties.BUCKET_NAME;
import static com.niuyin.starter.file.config.aliyun.AliyunOssConfigProperties.END_POINT;

/**
 * OssServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/29
 **/
@Slf4j
@EnableConfigurationProperties(AliyunOssConfigProperties.class)
@Import(AliyunOssConfig.class)
public class AliyunOssServiceImpl implements AliyunOssService {

    @Autowired
    private OSSClient ossClient;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        return putObject(file, folder);
    }

    /**
     * 上传视频文件
     *
     * @param file   文件本体
     * @param folder
     * @return url
     */
    @Override
    public String uploadVideoFile(MultipartFile file, String folder) {
        return uploadCommonVideo(file, folder);
    }

    /**
     * 分片上传视频文件
     *
     * @param file   文件本体
     * @param folder oss路径前缀
     * @return url
     */
    @Override
    public String multipartUploadVideoFile(MultipartFile file, String folder) {
        // 校验视频格式
        FileUploadUtils.assertVideoAllowed(file, MimeTypeUtils.VIDEO_EXTENSION);
        log.debug("开始分片上传视频: {}", file.getOriginalFilename());
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "https://oss-cn-shenzhen.aliyuncs.com";
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        // 填写Bucket名称，例如examplebucket。
//        String bucketName = "niuyin-server";
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        // 文件存储名称：服务名称/2023/11/11/uuid.jpg
        String objectName = folder + "/" + PathUtils.generateFilePath(Objects.requireNonNull(file.getOriginalFilename()));
        // 待上传本地文件路径。
//        String filePath = "C:\\Users\\roydon\\Videos\\niuyin\\91661033546\\Super earth#宇宙.mp4";

        // 创建OSSClient实例。
//        OSS ossClient = new OSSClient(endpoint, "***", "***");
        try {
            // 创建InitiateMultipartUploadRequest对象。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(BUCKET_NAME, objectName);

            // 如果需要在初始化分片时设置请求头，请参考以下示例代码。
            ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // 指定该Object的网页缓存行为。
            // metadata.setCacheControl("no-cache");
            // 指定该Object被下载时的名称。
            // metadata.setContentDisposition("attachment;filename=oss_MultipartUpload.txt");
            // 指定该Object的内容编码格式。
            // metadata.setContentEncoding(OSSConstants.DEFAULT_CHARSET_NAME);
            // 指定初始化分片上传时是否覆盖同名Object。此处设置为true，表示禁止覆盖同名Object。
            // metadata.setHeader("x-oss-forbid-overwrite", "true");
            // 指定上传该Object的每个part时使用的服务器端加密方式。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION, ObjectMetadata.KMS_SERVER_SIDE_ENCRYPTION);
            // 指定Object的加密算法。如果未指定此选项，表明Object使用AES256加密算法。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_DATA_ENCRYPTION, ObjectMetadata.KMS_SERVER_SIDE_ENCRYPTION);
            // 指定KMS托管的用户主密钥。
            // metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_KEY_ID, "9468da86-3509-4f8d-a61e-6eab1eac****");
            // 指定Object的存储类型。
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard);
            // 指定Object的对象标签，可同时设置多个标签。
            // metadata.setHeader(OSSHeaders.OSS_TAGGING, "a:1");
            // request.setObjectMetadata(metadata);

            // 根据文件自动设置ContentType。如果不设置，ContentType默认值为application/oct-srream。
//            if (metadata.getContentType() == null) {
//                metadata.setContentType(Mimetypes.getInstance().getMimetype(new File(filePath), objectName));
//            }

            // 初始化分片。
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId。
            String uploadId = upresult.getUploadId();
            // 根据uploadId执行取消分片上传事件或者列举已上传分片的操作。
            // 如果您需要根据您需要uploadId执行取消分片上传事件的操作，您需要在调用InitiateMultipartUpload完成初始化分片之后获取uploadId。
            // 如果您需要根据您需要uploadId执行列举已上传分片的操作，您需要在调用InitiateMultipartUpload完成初始化分片之后，且在调用CompleteMultipartUpload完成分片上传之前获取uploadId。
            // System.out.println(uploadId);

            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
            List<PartETag> partETags = new ArrayList<>();
            // 每个分片的大小，用于计算文件有多少个分片。单位为字节。
            final long partSize = 512 * 1024L;   //512 KB。

            // 根据上传的数据大小计算分片数。以本地文件为例，说明如何通过File.length()获取上传数据的大小。
//            final File sampleFile = new File(file);
            long fileLength = file.getSize();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(BUCKET_NAME);
                uploadPartRequest.setKey(objectName);
                uploadPartRequest.setUploadId(uploadId);
                // 设置上传的分片流。
                // 以本地文件为例说明如何创建FIleInputstream，并通过InputStream.skip()方法跳过指定数据。
                InputStream instream = file.getInputStream();
                instream.skip(startPos);
                uploadPartRequest.setInputStream(instream);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partETags.add(uploadPartResult.getPartETag());
            }


            // 创建CompleteMultipartUploadRequest对象。
            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(BUCKET_NAME, objectName, uploadId, partETags);

            // 如果需要在完成分片上传的同时设置文件访问权限，请参考以下示例代码。
            // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.Private);
            // 指定是否列举当前UploadId已上传的所有Part。仅在Java SDK为3.14.0及以上版本时，支持通过服务端List分片数据来合并完整文件时，将CompleteMultipartUploadRequest中的partETags设置为null。
            // Map<String, String> headers = new HashMap<String, String>();
            // 如果指定了x-oss-complete-all:yes，则OSS会列举当前UploadId已上传的所有Part，然后按照PartNumber的序号排序并执行CompleteMultipartUpload操作。
            // 如果指定了x-oss-complete-all:yes，则不允许继续指定body，否则报错。
            // headers.put("x-oss-complete-all","yes");
            // completeMultipartUploadRequest.setHeaders(headers);

            // 完成分片上传。
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println(completeMultipartUploadResult.getETag());
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
//            ossClient.shutdown();
        }
        log.debug("结束分片上传视频");
        String url = "https://" + BUCKET_NAME + "." + END_POINT + "/" + objectName;
        return url;
    }

    /**
     * 上传图片方法
     *
     * @param file   限制大小100M
     * @param folder
     * @return
     */
    private String uploadCommonImage(MultipartFile file, String folder) {
        // 校验图片格式
        FileUploadUtils.assertImageAllowed(file, MimeTypeUtils.IMAGE_EXTENSION);
        return putObject(file, folder);
    }

    /**
     * 上传视频方法
     *
     * @param file   限制大小100M
     * @param folder
     * @return
     */
    private String uploadCommonVideo(MultipartFile file, String folder) {
        // 校验视频格式
        FileUploadUtils.assertVideoAllowed(file, MimeTypeUtils.VIDEO_EXTENSION);
        return putObject(file, folder);
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return url
     */
    private String putObject(MultipartFile file, String folder) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String filePath = PathUtils.generateFilePath(Objects.requireNonNull(file.getOriginalFilename()));
        // 文件存储名称：服务名称/2023/11/11/uuid.jpg
        String ossFileName = folder + "/" + filePath;
        ossClient.putObject(BUCKET_NAME, ossFileName, inputStream);
        String url = "https://" + BUCKET_NAME + "." + END_POINT + "/" + ossFileName;
        //关闭 OSSClient
//        ossClient.shutdown();
        return url;
    }

    /**
     * 删除文件
     *
     * @param url
     */
    private void deleteObject(String url) {
        String urlPrefix = "https://" + BUCKET_NAME + "." + END_POINT + "/";
        System.out.println("urlPrefix==================" + urlPrefix);
        System.out.println("url==================" + url);
        String replace = url.replace(urlPrefix, "");
        System.out.println("replace==================" + replace);
        ossClient.deleteObject(BUCKET_NAME, replace);
        ossClient.shutdown();
    }

    /**
     * 上传图片链接
     */
    public String putImageUrl(String url, String folder) {
        try {
            InputStream inputStream = new URL(url).openStream();
            String filePath = PathUtils.generateFilePathForUrl(Objects.requireNonNull(url));
            // 文件存储名称：服务名称/2023/11/11/uuid.jpg
            String ossFileName = folder + "/" + filePath;
            ossClient.putObject(BUCKET_NAME, ossFileName, inputStream);
            //关闭 OSSClient
//        ossClient.shutdown();
            return "https://" + BUCKET_NAME + "." + END_POINT + "/" + ossFileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
