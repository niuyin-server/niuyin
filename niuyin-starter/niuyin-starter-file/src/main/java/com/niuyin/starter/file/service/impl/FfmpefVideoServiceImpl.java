package com.niuyin.starter.file.service.impl;

import com.niuyin.starter.file.config.FfmpegConfig;
import com.niuyin.starter.file.config.FfmpegConfigProperties;
import com.niuyin.starter.file.config.QiniuOssConfig;
import com.niuyin.starter.file.config.QiniuOssConfigProperties;
import com.niuyin.starter.file.service.FfmpefVideoService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/11/18 23:56
 * @author haose
 */
@EnableConfigurationProperties(FfmpegConfigProperties.class)
@Import(FfmpegConfig.class)
public class FfmpefVideoServiceImpl implements FfmpefVideoService {

    @Resource
    private FfmpegConfigProperties ffmpegConfigProperties;



    @Override
    public  boolean getTargetThumbnail(String localPath, String targetPath) {
        // FIXME: 2023/1/31  该方法基本可作为执行ffmpeg命令的模板方法，之后的几个方法与此类似
        try {
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(localPath);
//            ffmpeg.addArgument("-ss");
//            // 此处可自定义视频的秒数
//            ffmpeg.addArgument("0");
//            ffmpeg.addArgument("-q:v");
//            ffmpeg.addArgument("2");
            ffmpeg.addArgument("-vf");
            ffmpeg.addArgument("\"select=eq(n\\,10)\"");
            ffmpeg.addArgument("-q:v");
            ffmpeg.addArgument(ffmpegConfigProperties.getFbl()[2]);
            ffmpeg.addArgument("-s");
            ffmpeg.addArgument(ffmpegConfigProperties.getFbl()[3]);
            ffmpeg.addArgument(targetPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
        } catch (IOException e) {
            System.out.println("获取视频缩略图失败");
            e.printStackTrace();
            return false;
        }
//        System.out.println("ffmpegConfigProperties.getVfrme() = " + (ffmpegConfigProperties.getFbl()[0]));
        return true;
    }

    /**
     * 等待命令执行成功，退出
     *
     * @param br
     * @throws IOException
     */
    private static void blockFfmpeg(BufferedReader br) throws IOException {
        String line;
        // 该方法阻塞线程，直至合成成功
        while ((line = br.readLine()) != null) {
            doNothing(line);
        }
    }
    /**
     * 打印日志
     *
     * @param line
     */
    private static void doNothing(String line) {
        // FIXME: 2023/1/31 正式使用时注释掉此行，仅用于观察日志
        System.out.println(line);
    }

}
