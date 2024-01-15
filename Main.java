import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;

public class Main {

    public static final String PATH = "D://KI_Plankton//Plankton_Converted//Converted";

    public static final int[] WIDTH_ARRAY = {21592, 72812, 93711, 15954, 7324, 3609, 1838, 973, 558, 317, 213, 74, 34, 53};
    public static final int[] HEIGHT_ARRAY = {19926, 60609, 99519, 19706, 9451, 4675, 2415, 1297, 692, 380, 287, 47, 20, 38};

    public static void main(String[] args) throws IOException {

        //getWidthHeight();
        //getPercentages();
        //test();

        String t = "training_snow_roi0.0406459600.tif.png";
        int i = t.lastIndexOf(".tif");
        String n = t.substring(0, i) + "_T.tif.png";
        System.out.println(t);
        System.out.println(n);

        int targetTrainAmount = 1000;
        int targetTestAmount = 200;
        int targetTrainSplit = targetTrainAmount / 2;
        int targetTestSplit = targetTestAmount / 2;
        int pullAmount = targetTrainSplit + targetTestSplit;

        System.out.println(pullAmount);

    }

    public static void getPercentages() {

        int sum = 0;
        DecimalFormat df = new DecimalFormat("0.00");

        for(int i : WIDTH_ARRAY) {
            sum += i;
        }

        System.out.println("Widths:");

        for(int i = 0; i < WIDTH_ARRAY.length; i++) {
            double percent = ((double) WIDTH_ARRAY[i] / sum) * 100.0D;
            System.out.println(i + ": " + WIDTH_ARRAY[i] + " (" + df.format(percent).replace(",", ".") + "%)");
        }

        System.out.println();
        System.out.println("Heights:");

        for(int i = 0; i < HEIGHT_ARRAY.length; i++) {
            double percent = ((double) HEIGHT_ARRAY[i] / sum) * 100.0D;
            System.out.println(i + ": " + HEIGHT_ARRAY[i] + " (" + df.format(percent).replace(",", ".") + "%)");
        }

    }

    public static void test() {

        File dir = new File(PATH);
        String[] fileNames = dir.list();

        Map<String, Integer> differentTypes = new HashMap<>();
        Map<String, Integer> differentTypes2 = new HashMap<>();

        for(String name : fileNames) {

            if(!name.startsWith("training_")) {

                int cutTo = name.lastIndexOf("_roi");
                int cutFrom = name.indexOf('_');
                String roiName = name.substring(cutFrom+1, cutTo);

                if(differentTypes2.containsKey(roiName)) {
                    differentTypes2.replace(roiName, (differentTypes2.get(roiName) + 1));
                } else {
                    differentTypes2.put(roiName, 1);
                }

                continue;
            }

            int cutTo = name.lastIndexOf("_roi");
            String roiName = name.substring(9, cutTo);

            if(differentTypes.containsKey(roiName)) {
                differentTypes.replace(roiName, (differentTypes.get(roiName) + 1));
                continue;
            }

            differentTypes.put(roiName, 1);

        }

        System.out.println("Train data:");
        differentTypes.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEach(System.out::println);
        System.out.println("total types: " + differentTypes.size() + " | total entries: " + differentTypes.values().stream().mapToInt(Integer::intValue).sum());
        System.out.println(differentTypes.values().stream().mapToInt(Integer::intValue).summaryStatistics());
        System.out.println();
        System.out.println("Test data:");
        differentTypes2.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEach(System.out::println);
        System.out.println("total types: " + differentTypes2.size() + " | total entries: " + differentTypes2.values().stream().mapToInt(Integer::intValue).sum());
        System.out.println(differentTypes2.values().stream().mapToInt(Integer::intValue).summaryStatistics());

        System.out.println("\ntotal entries combined: " + fileNames.length);

    }

    public static void getWidthHeight() throws IOException {

        long startTime = System.currentTimeMillis();

        File dir = new File(PATH);
        String[] fileNames = dir.list();

        BufferedImage bimg;
        int count = 0;
        int maxWidth = 0;
        int maxHeight = 0;

        int[] widthArr = new int[14]; //0: 0-99 //1: 100-199 //2: 200-299
        int[] heightArr = new int[14]; //0: 0-99 //1: 100-199 //2: 200-299
        List<String> extremes = new ArrayList<>();

        for(int i = 0; i < widthArr.length; i++) {
            widthArr[i] = 0;
            heightArr[i] = 0;
        }

        for(String name : fileNames) {

            bimg = ImageIO.read(new File(PATH + "//" + name));

            maxWidth = Math.max(maxWidth, bimg.getWidth());
            maxHeight = Math.max(maxHeight, bimg.getHeight());

            if(++count % 1_000 == 0) {
                System.out.println(count + " images processed");
            }

            int cWidth = bimg.getWidth();
            int cHeight = bimg.getHeight();
            int widthIndex = (int) Math.floor(((double) cWidth)/100);
            int heightIndex = (int) Math.floor(((double) cHeight)/100);

            widthArr[widthIndex]++;
            heightArr[heightIndex]++;

            if(cWidth >= 1300 || cHeight >= 1300) {
                extremes.add(name);
            }

        }

        long endTime = System.currentTimeMillis();

        System.out.println("Max: " + maxWidth + ", " + maxHeight);
        System.out.println("Widths:");

        for(int i = 0; i < widthArr.length; i++) {
            System.out.println(i + ": " + widthArr[i]);
        }

        System.out.println("Heights:");

        for(int i = 0; i < heightArr.length; i++) {
            System.out.println(i + ": " + heightArr[i]);
        }

        System.out.println("Took " + (endTime-startTime) + " ms!");
        System.out.println("Extremes (1300+)");

        for(String s : extremes) {
            System.out.println(s);
        }

    }

}