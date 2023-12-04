package com.niuyin.starter.file.service.impl;

import com.niuyin.starter.file.config.FfmpegConfig;
import com.niuyin.starter.file.config.FfmpegConfigProperties;
import com.niuyin.starter.file.service.FfmpegVideoService;
import com.niuyin.starter.file.util.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/11/18 23:56
 *
 * @author haose
 */
@Slf4j
@EnableConfigurationProperties(FfmpegConfigProperties.class)
@Import(FfmpegConfig.class)
public class FfmpegVideoServiceImpl implements FfmpegVideoService {

    @Resource
    private FfmpegConfigProperties ffmpegConfigProperties;

    /**
     * 获取视频详情
     *
     * @param url
     * @return
     */
    @Override
    public MultimediaInfo getVideoInfo(String url) {
        return getMultimediaInfoByUrl(url);
    }

    /**
     * 通过本地路径获取多媒体文件信息(宽，高，时长，编码等)
     *
     * @param localPath 本地路径
     * @return MultimediaInfo 对象,包含 (宽，高，时长，编码等)
     * @throws EncoderException
     */
    public static MultimediaInfo getMultimediaInfo(String localPath) {
        MultimediaInfo multimediaInfo = null;
        try {
            multimediaInfo = new MultimediaObject(new File(localPath)).getInfo();
        } catch (EncoderException e) {
            log.debug("获取多媒体文件信息异常：{}", e.getMessage());
        }
        return multimediaInfo;
    }

    /**
     * 通过URL获取多媒体文件信息
     *
     * @param url 网络url
     * @return MultimediaInfo 对象,包含 (宽，高，时长，编码等)
     * @throws EncoderException
     */
    public static MultimediaInfo getMultimediaInfoByUrl(String url) {
        MultimediaInfo multimediaInfo = null;
        try {
            multimediaInfo = new MultimediaObject(new URL(url)).getInfo();
        } catch (Exception e) {
            log.debug("获取多媒体文件信息异常：{}", e.getMessage());
        }
        return multimediaInfo;
    }

    /**
     * 根据视频远程url生成首帧截图
     *
     * @param url
     * @param fileName
     * @return
     */
    @Override
    public String getTargetThumbnail(String url, String fileName) {
        String thumbnailPath = null;
        try {
            // ffmpeg -i 输入视频文件名 -ss 时间戳 -vframes 1 输出图片文件名
            thumbnailPath = ffmpegConfigProperties.getTargetPath() + PathUtils.generateDataPath() + fileName;
            File file = new File(thumbnailPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(url);
            ffmpeg.addArgument("-ss");
            ffmpeg.addArgument(ffmpegConfigProperties.getTimestamp());
            ffmpeg.addArgument("-vframes");
            ffmpeg.addArgument("1");
            ffmpeg.addArgument("-q:v");
            ffmpeg.addArgument(ffmpegConfigProperties.getQuantity());
            ffmpeg.addArgument(thumbnailPath);
            ffmpeg.execute();
            BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()));
            blockFfmpeg(br);
        } catch (Exception e) {
            log.debug("获取视频缩略图失败：{}", e.getMessage());
        }
        return thumbnailPath;
    }

    /**
     * 为远程视频生成三张预览图
     *
     * @param url
     * @param fileName
     * @return
     */
    @Override
    public String[] generatePreviewCover(String url, String fileName) {
        // 先获取视频时长
        MultimediaInfo multimediaInfo = getVideoInfo(url);
        long duration = multimediaInfo.getDuration();
        ArrayList<String> res = new ArrayList<>(3);
        if (duration == -1L) {
            // 获取视频时长失败，返回一个首帧截图
            String oneCover = getTargetThumbnail(url, fileName);
            res.add(oneCover);
            res.add(oneCover);
            res.add(oneCover);
            return res.toArray(new String[3]);
        }
        // 根据视频时长生成三张截图
        ArrayList<String> durationList = new ArrayList<>(3);
        durationList.add("00:00:00");
        durationList.add(formatTime2HHMMSS(duration / 8));
        durationList.add(formatTime2HHMMSS(duration / 4));
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < durationList.size(); i++) {
            // 根据时间生成截图
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                log.debug("开始生成指定时长的截图：{}", durationList.get(finalI));
                String realFileName = fileName + "-" + finalI;
                res.add(generateCoverByTime(url, realFileName, durationList.get(finalI)));
            });
            futures.add(future);
        }
        // 等待所有异步任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allFutures.join(); // 等待所有异步任务完成

        return res.toArray(new String[3]);
    }

    /**
     * 毫秒时间转换
     *
     * @param timeInMillis
     * @return
     */
    public String formatTime2HHMMSS(long timeInMillis) {
        long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        log.debug("时间：{}", formattedTime);
        return formattedTime;
    }

    /**
     * 指定时间生成截图
     *
     * @param url
     * @param fileName
     * @param timestamp 00:00:01
     * @return
     */
    public String generateCoverByTime(String url, String fileName, String timestamp) {
        String thumbnailPath = null;
        try {
            // ffmpeg -i 输入视频文件名 -ss 时间戳 -vframes 1 输出图片文件名
            thumbnailPath = ffmpegConfigProperties.getTargetPath() + PathUtils.generateDataPath() + fileName + ".jpg";
            File file = new File(thumbnailPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(url);
            ffmpeg.addArgument("-ss");
            ffmpeg.addArgument(timestamp);
            ffmpeg.addArgument("-vframes");
            ffmpeg.addArgument("1");
            ffmpeg.addArgument("-q:v");
            ffmpeg.addArgument(ffmpegConfigProperties.getQuantity());
            ffmpeg.addArgument(thumbnailPath);
            ffmpeg.execute();
            BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()));
            blockFfmpeg(br);
        } catch (Exception e) {
            log.debug("获取视频缩略图失败：{}", e.getMessage());
        }
        return thumbnailPath;
    }

    /**
     * 等待命令执行成功，退出
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
     */
    private static void doNothing(String line) {
        System.out.println(line);
    }

}
