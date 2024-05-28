package com.pandora.www.pictrueToVideo;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Pictrue_to_vedio {
    private static Logger LOG = LoggerFactory.getLogger(Pictrue_to_vedio.class);

    public static void main(String[] args) throws FrameRecorder.Exception {
        Map<Integer, File> fileMap = fileToMap("F:\\code\\IdeaProjects\\pictrue-convert\\src\\pictureInput");
        createMp4("F:\\code\\IdeaProjects\\pictrue-convert\\src\\videoOutput\\video.mp4", fileMap, 1920, 1080);
    }

    public static Map<Integer, File> fileToMap(String filePath) {
        long startTime = System.currentTimeMillis();

        HashMap<Integer, File> imgMap = new HashMap<>();
        Integer index = 0;

        try {
            File file = new File(filePath);
            for (File listFile : file.listFiles()) {
                LOG.info("子文件路径==》" + listFile);
                imgMap.put(index, listFile);
                index ++;
            }
        } catch (Exception e) {
            LOG.error("图片转换失败！");
        }

        return imgMap;
    }

    private static final Integer FRAME = 25;

    private static final Integer SECOND_IMG = 2;

    public static void createMp4(String localPath, Map<Integer, File> imgMap, int width, int height) throws FrameRecorder.Exception {
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(localPath, width, height);

        //设置视频编码曾模式
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

        //设置视频多少帧每秒
        recorder.setFrameRate(FRAME);

        //设置视频图像数据格式
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);

        //视频格式
        recorder.setFormat("mp4");

        try {
            recorder.start();

            Java2DFrameConverter converter = new Java2DFrameConverter();

            LOG.info("共有图片：" + imgMap.size());

            for (int i = 0; i < imgMap.size(); i++) {
                BufferedImage read = ImageIO.read(imgMap.get(i));
                for (Integer j = 0; j < FRAME; j++) {
                    recorder.record(converter.getFrame(read));
                }
            }
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            recorder.stop();
            recorder.release();
        }

        LOG.info("生成视频完成==》");

    }
}
