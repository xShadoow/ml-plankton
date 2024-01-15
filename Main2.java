import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main2 {

    public static final String PATH = "D://KI_Plankton//Plankton_Converted//Converted";

    public static final int[] WIDTHS_ARR = {21592, 72812, 93711, 15954, 7324, 3609, 1838, 973, 558, 317, 213, 74, 34, 53};
    public static final int[] HEIGHTS_ARR = {19926, 60609, 99519, 19706, 9451, 4675, 2415, 1297, 692, 380, 287, 47, 20, 38};

    public static void main(String[] args) throws Exception {

        calculateWidthHeight();
        //getPercentages();
        //resizeImages(); //not used only for single testing
        //resizeAll();
        //printNameDetails();

        /*

        final String rootPath = "C://Users//sandr//Downloads//Plankton_Converted//Test//";

        File test1 = new File(rootPath + "test1.png");
        File test2 = new File(rootPath + "test2.png");
        File test3 = new File(rootPath + "test3.png");
        File test4 = new File(rootPath + "test4.png");
        File test5 = new File(rootPath + "test5.png");
        File test6 = new File(rootPath + "test6.png");

        File out1 = new File(rootPath + "out1.png");
        File out2 = new File(rootPath + "out2.png");
        File out3 = new File(rootPath + "out3.png");
        File out4 = new File(rootPath + "out4.png");
        File out5 = new File(rootPath + "out5.png");
        File out6 = new File(rootPath + "out6.png");

        resizeImage(350, 350, test1, out1);
        resizeImage(350, 350, test2, out2);
        resizeImage(350, 350, test3, out3);
        resizeImage(350, 350, test4, out4);
        resizeImage(350, 350, test5, out5);
        resizeImage(350, 350, test6, out6);
         */

    }

    public static void printNameDetails() {

        File dir = new File(PATH);
        String[] names = dir.list();

        Map<String, Integer> trainingMap = new HashMap<>();
        Map<String, Integer> testMap = new HashMap<>();

        for(String name : names) {

            if(name.contains("training")) {

                int cutFrom = 9;
                int cutTo = name.indexOf("roi") - 1;
                String cutName = name.substring(cutFrom, cutTo);

                if(trainingMap.containsKey(cutName)) {
                    trainingMap.put(cutName, trainingMap.get(cutName) + 1);
                } else {
                    trainingMap.put(cutName, 1);
                }

            } else {

                int cutFrom = name.indexOf('_') + 1;
                int cutTo = name.indexOf("roi") - 1;
                String cutName = name.substring(cutFrom, cutTo);

                if(testMap.containsKey(cutName)) {
                    testMap.put(cutName, testMap.get(cutName) + 1);
                } else {
                    testMap.put(cutName, 1);
                }

            }

        }

        System.out.println("--- training ---");
        trainingMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEach(System.out::println);
        System.out.println(trainingMap.values().stream().mapToInt(Integer::intValue).summaryStatistics());
        System.out.println();
        System.out.println("--- test ---");
        testMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEach(System.out::println);
        System.out.println(testMap.values().stream().mapToInt(Integer::intValue).summaryStatistics());

    }

    public static void resizeAll() throws Exception {

        final String inputRootPath = "D://KI_Plankton//Plankton_Converted//Converted";
        final String outputRootPath = "D://KI_Plankton//Output200//";
        final int targetWidth = 200;
        final int targetHeight = 200;

        File rootDir = new File(inputRootPath);
        int i = 0;

        for(String pictureName : rootDir.list()) {

            File input = new File(inputRootPath + "//" + pictureName);
            File output = new File(outputRootPath + pictureName);
            resizeImage(targetWidth, targetHeight, input, output);

            if(++i % 1_000 == 0) {
                System.out.println("processed " + i + " pictures");
            }

        }

    }

    public static void resizeImage(int targetWidth, int targetHeight, File input, File output) throws Exception {

        BufferedImage bufferedImage = ImageIO.read(input);

        boolean fitHeight = bufferedImage.getHeight() > bufferedImage.getWidth();
        Scalr.Mode scaleMode = fitHeight ? Scalr.Mode.FIT_TO_HEIGHT : Scalr.Mode.FIT_TO_WIDTH;

        BufferedImage scaledImage = resizeImage(bufferedImage, targetWidth, targetHeight, scaleMode);

        int y_val = (int) ((targetHeight/2.0D) - (scaledImage.getHeight()/2.0D));
        int x_val = (int) ((targetWidth/2.0D) - (scaledImage.getWidth()/2.0D));

        BufferedImage newImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.setPaint(new Color(0, 0, 0));
        graphics2D.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());

        if(fitHeight) {
            graphics2D.drawImage(scaledImage, x_val, 0, null);
        } else {
            graphics2D.drawImage(scaledImage, 0, y_val, null);
        }

        graphics2D.dispose();
        ImageIO.write(newImage, "png" , output);

    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight, Scalr.Mode mode) throws Exception {
        Scalr.Method method = Scalr.Method.QUALITY;
        return Scalr.resize(originalImage, method, mode, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
    }

    public static void resizeImages() throws Exception {

        final String path = "C://Users//sandr//Downloads//Plankton_Converted//Test//training_snow_roi3.3487104500.tif.png";
        final String savePath = "C://Users//sandr//Downloads//Plankton_Converted//Test//output.png";

        BufferedImage bufferedImage = ImageIO.read(new File(path));

        int targetWidth = 350;
        int targetHeight = 350;

        BufferedImage scaledImage = resizeImage(bufferedImage, targetWidth, targetHeight, Scalr.Mode.FIT_TO_WIDTH);
        int y_val = (int) ((350/2.0D) - (scaledImage.getHeight()/2.0D));

        BufferedImage newImage = new BufferedImage(350, 350, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.setPaint(new Color(0, 0, 0));
        graphics2D.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
        graphics2D.drawImage(scaledImage, 0, y_val, null);
        graphics2D.dispose();

        File outputFile = new File(savePath);
        ImageIO.write(newImage, "png" , outputFile);

        /*

        if(bufferedImage.getWidth() > bufferedImage.getHeight()) {
            targetWidth = 350;
            targetHeight = (int) (bufferedImage.getHeight() * (350.0D/ bufferedImage.getHeight()));
        }

        Image scaledImage = bufferedImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

        BufferedImage newImage = new BufferedImage(350, 350, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.setPaint(new Color(0, 0, 0));
        graphics2D.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());

        int y_val = (int) ((350/2.0D) - (scaledImage.getHeight(null)/2.0D));

        graphics2D.drawImage(scaledImage, 0, y_val, null);
        graphics2D.dispose();

        File outputFile = new File(savePath);
        ImageIO.write(newImage, "png", outputFile);

        */

    }

    public static void getPercentages() {

        DecimalFormat df = new DecimalFormat("0.00");
        int sum = 0;

        for(int entry : WIDTHS_ARR) {
            sum += entry;
        }

        System.out.println("Widths:");

        for(int i = 0; i < WIDTHS_ARR.length; i++) {
            double percent = ((double) WIDTHS_ARR[i] / sum) * 100.0D;
            System.out.println(i + ": " + WIDTHS_ARR[i] + " (" + df.format(percent) + "%)");
        }

        System.out.println();
        System.out.println("Heights:");

        for(int i = 0; i < HEIGHTS_ARR.length; i++) {
            double percent = ((double) HEIGHTS_ARR[i] / sum) * 100.0D;
            System.out.println(i + ": " + HEIGHTS_ARR[i] + " (" + df.format(percent) + "%)");
        }

    }

    public static void calculateWidthHeight() throws IOException {

        File file = new File(PATH);
        System.out.println(file.isDirectory());

        long start = System.currentTimeMillis();
        String[] flist = file.list();
        int i = 0;

        BufferedImage bimg;
        int maxWidth = 0;
        int maxHeight = 0;
        int[] widthArray = new int[14];
        int[] heightArray = new int[14];

        for(String s : flist) {

            bimg = ImageIO.read(new File(PATH + "//" + s));

            int cWidth = bimg.getWidth();
            int cHeight = bimg.getHeight();
            int widthIndex = (int) Math.floor((double) cWidth / 100);
            int heightIndex = (int) Math.floor((double) cHeight / 100);

            widthArray[widthIndex]++;
            heightArray[heightIndex]++;

            if(i % 1_000 == 0) {
                System.out.println("processed " + i + " pictures");
            }

            i++;

        }

        long end = System.currentTimeMillis();
        System.out.println(end-start);
        System.out.println("maxWidth: " + maxWidth + ", maxHeight: " + maxHeight);

        System.out.println("Widths:");

        for(int index = 0; index < widthArray.length; index++) {
            System.out.println(index + ": " + widthArray[index]);
        }

        System.out.println("Heights:");

        for(int index = 0; index < heightArray.length; index++) {
            System.out.println(index + ": " + heightArray[index]);
        }

        System.out.println(widthArray);
        System.out.println(heightArray);

    }

}