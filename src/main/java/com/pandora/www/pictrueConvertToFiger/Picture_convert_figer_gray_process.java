package com.pandora.www.pictrueConvertToFiger;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


// 首先将0 - 9 的数据大量大片的分为 10个大片列，每个大片列都是相同的数字，然后用肉眼看，对数字连成片的深浅程度，进行排序。
// 最终由浅至深是 {1, 7, 3, 2, 4, 5, 0, 6, 9, 8} 这个顺序。
// 因为是将图片转为0-9的数字，也就是转成黑白，那么就需要先将彩色图片转为灰色图片，拿到像素的灰值，然后将灰值转成相应的数字。
// 因为是将像素点转为 0- 9 的数字，所以需要将 rgb 灰值，转为 0 - 9之间的数字
// 先获得图片中的最大 rgb，最大rgb对应 0-9 的最大边界，最小rgb对0-9最小边界，就先求这个 图片中的最大 rgb 灰值，然后max / 10就是最大单位
// 只要过来的rgb除以 max / 10，得到的就是小于10的，距离10这个边界的值，然后再转为整数，再从数组中取相应的值，就拿到像素相应的替代数字

public class Picture_convert_figer_gray_process {


    final private static Logger LOG = LoggerFactory.getLogger(Picture_convert_figer_gray_process.class);

    public static void main(String[] args) throws IOException {

        String imageInputPath = "E:\\code\\IdeaProjects\\pictrue-convert\\src\\pictureInput\\image.jpg";
        String imageOutputPath = "E:\\code\\IdeaProjects\\pictrue-convert\\src\\pictureOutput\\image1.jpg";

        ImageIO.write(imageToGray(imageInputPath), "jpg", new File(imageOutputPath));

        LOG.info("转换完成 ~~ ");
    }

    /**
     * 获取整张图中的rgb max
     * @param imagePath
     * @return
     */
    public static int getMax(String imagePath) {
        BufferedImage image = null;
        int gray = 0;

        int max = 0;
        int min = Integer.MAX_VALUE;
        try {
            File imagef = new File(imagePath);
            image = ImageIO.read(imagef);

            if (!imagef.exists()) {
                System.out.println("image not fund!!");
            }

            int height = image.getHeight();
            int width = image.getWidth();
            int minX = image.getMinX();
            int minY = image.getMinY();

            for (int i = minX; i < width; i++) {
                for (int j = minY; j < height; j++) {
                    int[] RGB = {0, 0, 0};
                    RGB[0] = (image.getRGB(i, j) & 0xff0000) >> 16;
                    RGB[1] = (image.getRGB(i, j) & 0xff00) >> 8;
                    RGB[2] = (image.getRGB(i, j) & 0xff);

                    gray = (30 * RGB[0] + 59 * RGB[1] + 11 * RGB[2]) / 100;

                    int rgb_togray = ((gray & 0xff) << 16) | ((gray & 0xff) << 8) | (gray & 0xff);
                    if (rgb_togray > max) {
                        max = rgb_togray;
                    }

                    if (rgb_togray < min) {
                        min = rgb_togray;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return max;
    }

    /**
     * 计算每个像素点的灰值，并带入getMax计算整张图的 rgb max，然后将此点的rgb 灰值 和 rgb max，带入 get index
     * 得到对应像素深色的 数字，写入txt，并最终返回一张灰图，写出去。
     * @param imagePath
     * @return
     * @throws IOException
     */
    public static BufferedImage imageToGray(String imagePath) throws IOException {
        FileWriter fileWriter = new FileWriter("E:\\code\\IdeaProjects\\pictrue-convert\\src\\fileOutput\\output1.txt");

        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        BufferedImage image = null;

        BufferedImage grayImage = null;
        int gray = 0;
        try {
            File imagef = new File(imagePath);
            int max = getMax(imagePath);

            image = ImageIO.read(imagef);

            if (!imagef.exists()) {
                System.out.println("image not found!!");
                return grayImage;
            }
            int height = image.getHeight();
            int width = image.getWidth();
            int minX = image.getMinX();
            int minY = image.getMinY();

            grayImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            //之所以这样，是因为如果用 i，j就会是一张转了90度的图片。
            for (int i = minX; i < height; i++) {
                for (int j = minY; j < width; j++) {
                    int[] RGB = {0, 0, 0};
                    RGB[0] = (image.getRGB(j, i) & 0xff0000) >> 16;
                    RGB[1] = (image.getRGB(j, i) & 0xff00) >> 8;
                    RGB[2] = (image.getRGB(j, i) & 0xff);

                    gray = (30 * RGB[0] + 59 * RGB[1] + 11 * RGB[2]) / 100;

                    int rgb_togray = ((gray & 0xff) << 16) | ((gray & 0xff) << 8) | (gray & 0xff);

                    bufferedWriter.write(getindex(rgb_togray, max) + "");
                    grayImage.setRGB(j, i, rgb_togray);
                }
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grayImage;
    }



    /**
     * 所以这个 getindex就是根据我们此处的像素点rgb灰值，跟整张图的 rgb max比，是什么比例，
     * 然后根据这个比例取相应的 0-9 中的距离最大颜色深度一定比例的数字进行填充
     * @param rgb
     * @param max
     * @return
     */
    public static int getindex(int rgb, int max) {
        if (rgb == max) {
            rgb -= 1;
        }
        double one = (double) max / 10;
        double percent = (double) rgb / one;

        int ceilPercent = (int) Math.floor(percent);

        int[] arr = {1, 7, 3, 2, 4, 5, 0, 6, 9, 8};

        return arr[ceilPercent];

//        if (rgb < one) {
//            return 1;
//        } else if (rgb < one * 2) {
//            return 7;
//        } else if (rgb >= one * 2 && rgb < one * 3) {
//            return 3;
//        } else if (rgb >= one * 3 && rgb < one * 4) {
//            return 2;
//        } else if (rgb >= one * 4 && rgb < one * 5) {
//            return 4;
//        } else if (rgb >= one * 5 && rgb < one * 6) {
//            return 5;
//        } else if (rgb >= one * 6 && rgb < one * 7) {
//            return 0;
//        } else if (rgb >= one * 7 && rgb < one * 8) {
//            return 6;
//        } else if (rgb >= one * 8 && rgb < one * 9) {
//            return 9;
//        } else {
//            return 8;
//        }
    }

}
