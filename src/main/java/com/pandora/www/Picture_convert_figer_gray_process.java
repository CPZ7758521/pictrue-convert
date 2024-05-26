package com.pandora.www;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Picture_convert_figer_gray_process {

    public static void main(String[] args) throws IOException {

        String imageInputPath = "E:\\code\\IdeaProjects\\pictrue-convert\\src\\pictureInput\\175e41b429c611b2b09174222d772285.jpg";
        String imageOutputPath = "E:\\code\\IdeaProjects\\pictrue-convert\\src\\pictureOutput\\image1.jpg";

        ImageIO.write(imageToGray(imageInputPath), "jpg", new File(imageOutputPath));
    }

    public static BufferedImage pictureProcess() throws IOException {
        BufferedImage src = ImageIO.read(new File(""));
        BufferedImage grayImage = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        System.out.println(src.getWidth() + ":" + src.getHeight());

        for (int i = 0; i < src.getWidth(); i++) {
            for (int j = 0; j < src.getHeight(); j++) {
                int rgb = grayImage.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }

        return grayImage;
    }

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

    public static BufferedImage imageToGray(String imagePath) throws IOException {
        FileWriter fileWriter = new FileWriter("E:\\code\\IdeaProjects\\pictrue-convert\\src\\fileOutput\\output.txt");

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

    public static int getindex(int rgb, int max) {
        if (rgb == max) {
            rgb = -1;
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
