package com.niuyin.common.core.utils.video;

import lombok.extern.slf4j.Slf4j;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/11/18 15:37
 */
@Slf4j
public class FfmpegUtil {

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
            System.out.println("获取多媒体文件信息异常");
            e.printStackTrace();
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
    public static MultimediaInfo getMultimediaInfoFromUrl(String url) {
        MultimediaInfo multimediaInfo = null;
        try {
            multimediaInfo = new MultimediaObject(new URL(url)).getInfo();
        } catch (Exception e) {
            System.out.println("获取多媒体文件信息异常");
            e.printStackTrace();
        }
        return multimediaInfo;
    }

    private static final int SAMPLING_RATE = 16000;
    private static final int SINGLE_CHANNEL = 1;

    /**
     * 音频格式化为wav,并设置单声道和采样率
     *
     * @param url        需要转格式的音频
     * @param targetPath 格式化后要保存的目标路径
     */
    public static boolean formatAudio(String url, String targetPath) {
        File target = new File(targetPath);
        MultimediaObject multimediaObject;
        try {
            // 若是本地文件： multimediaObject = new MultimediaObject(new File("你的本地路径"));
            multimediaObject = new MultimediaObject(new URL(url));
            // 音频参数
            // TODO: 2023/1/31 此处按需自定义音频参数
            AudioAttributes audio = new AudioAttributes();
            // 采样率
            audio.setSamplingRate(SAMPLING_RATE);
            // 单声道
            audio.setChannels(SINGLE_CHANNEL);
            Encoder encoder = new Encoder();
            EncodingAttributes attrs = new EncodingAttributes();
            // 输出格式
            attrs.setOutputFormat("wav");
            attrs.setAudioAttributes(audio);
            encoder.encode(multimediaObject, target, attrs);
            return true;
        } catch (Exception e) {
            System.out.println("格式化音频异常");
            return false;
        }
    }

    /**
     * 视频格式化为mp4
     *
     * @param url
     * @param targetPath
     * @return
     */
    public static boolean formatToMp4(String url, String targetPath) {
        File target = new File(targetPath);
        MultimediaObject multimediaObject;
        try {
            // 若是本地文件： multimediaObject = new MultimediaObject(new File("你的本地路径"));
            multimediaObject = new MultimediaObject(new URL(url));
            EncodingAttributes attributes = new EncodingAttributes();
            // 设置视频的音频参数
            AudioAttributes audioAttributes = new AudioAttributes();
            attributes.setAudioAttributes(audioAttributes);
            // 设置视频的视频参数
            VideoAttributes videoAttributes = new VideoAttributes();
            // 设置帧率
            videoAttributes.setFrameRate(25);
            attributes.setVideoAttributes(videoAttributes);
            // 设置输出格式
            attributes.setOutputFormat("mp4");
            Encoder encoder = new Encoder();
            encoder.encode(multimediaObject, target, attributes);
            return true;
        } catch (Exception e) {
            System.out.println("格式化视频异常");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取视频缩略图 获取视频第0秒的第一帧图片
     *
     * <p>执行的ffmpeg 命令为： ffmpeg -i 你的视频文件路径 -ss 指定的秒数 生成文件的全路径地址
     *
     * @param localPath  本地路径
     * @param targetPath 存放的目标路径
     * @return
     */
    public static boolean getTargetThumbnail(String localPath, String targetPath) {
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
            ffmpeg.addArgument("31");
            ffmpeg.addArgument("-s");
            ffmpeg.addArgument("960*540");
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

    /**
     * 视频增加字幕
     *
     * @param originVideoPath 原视频地址
     * @param targetVideoPath 目标视频地址
     * @param srtPath         固定格式的srt文件地址或存储位置，字母文件名： xxx.srt，样例看博客
     * @return
     * @throws Exception
     */
    public static boolean addSubtitle(String originVideoPath, String srtPath, String targetVideoPath) {
        try {
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(originVideoPath);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(srtPath);
            ffmpeg.addArgument("-c");
            ffmpeg.addArgument("copy");
            ffmpeg.addArgument(targetVideoPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
        } catch (IOException e) {
            System.out.println("字幕增加失败");
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 常用命令
     *
     * @return
     */
    public static void cmd() {
        // FIXME: 2023/1/31  还有很多类似命令 不再一一列举 ，附上命令,具体写法参考 getTargetThumbnail或addSubtitle方法
        // FIXME: 2023/1/31 ffmpeg命令网上搜索即可

        // 剪切视频
        // ffmpeg -ss 00:00:00 -t 00:00:30 -i test.mp4 -vcodec copy -acodec copy output.mp4
        // * -ss 指定从什么时间开始
        // * -t 指定需要截取多长时间
        // * -i 指定输入文件

        // ffmpeg -ss 10 -t 15 -accurate_seek -i test.mp4 -codec copy cut.mp4
        // ffmpeg -ss 10 -t 15 -accurate_seek -i test.mp4 -codec copy -avoid_negative_ts 1 cut.mp4

        // 拼接MP4
        // 第一种方法：
        // ffmpeg -i "concat:1.mp4|2.mp4|3.mp4" -codec copy out_mp4.mp4
        // 1.mp4 第一个视频文件的全路径
        // 2.mp4 第二个视频文件的全路径

        // 提取视频中的音频
        // ffmpeg -i input.mp4 -acodec copy -vn output.mp3
        // -vn: 去掉视频；-acodec: 音频选项， 一般后面加copy表示拷贝

        // 音视频合成
        // ffmpeg -y –i input.mp4 –i input.mp3 –vcodec copy –acodec copy output.mp4
        // -y 覆盖输出文件

        // 剪切视频
        //  ffmpeg -ss 0:1:30 -t 0:0:20 -i input.mp4 -vcodec copy -acodec copy output.mp4
        // -ss 开始时间; -t 持续时间

        // 视频截图
        //  ffmpeg –i test.mp4 –f image2 -t 0.001 -s 320x240 image-%3d.jpg
        // -s 设置分辨率; -f 强迫采用格式fmt;

        // 视频分解为图片
        //   ffmpeg –i test.mp4 –r 1 –f image2 image-%3d.jpg
        // -r 指定截屏频率

        // 将图片合成视频
        //  ffmpeg -f image2 -i image%d.jpg output.mp4

        // 视频拼接
        //  ffmpeg -f concat -i filelist.txt -c copy output.mp4

        // 将视频转为gif
        //    ffmpeg -i input.mp4 -ss 0:0:30 -t 10 -s 320x240 -pix_fmt rgb24 output.gif
        // -pix_fmt 指定编码

        // 视频添加水印
        //  ffmpeg -i input.mp4 -i logo.jpg
        // -filter_complex[0:v][1:v]overlay=main_w-overlay_w-10:main_h-overlay_h-10[out] -map [out] -map
        // 0:a -codec:a copy output.mp4
        // main_w-overlay_w-10 视频的宽度-水印的宽度-水印边距；

    }
}
