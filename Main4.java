import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Main4 {

    public static void main(String[] args) throws IOException {

        ownTrainTestSplit();

    }

    public static void ownTrainTestSplit() throws IOException {

        final String inputRootPath = "D://KI_Plankton//Output200";
        final String outputRootPath = "D://KI_Plankton//Output200-TrainTest//";
        final int targetTrainAmount = 1_000;
        final int targetTestAmount = 200;

        File rootDir = new File(inputRootPath);
        List<PictureClass> classes = new ArrayList<>();

        for(String name : rootDir.list()) {

            int cutFrom;
            int cutTo = name.indexOf("roi") - 1;

            if(name.contains("training")) {
                cutFrom = 9;
            } else {
                cutFrom = name.indexOf('_') + 1;
            }

            String cutName = name.substring(cutFrom, cutTo);

            if (cutName.equals("pteropods") || cutName.equals("amphipods") || cutName.equals("phaeocystis") || cutName.equals("echinodermata")) {
                continue;
            }

            if (!PictureClass.contains(classes, cutName)) {
                classes.add(new PictureClass(cutName));
            }

            PictureClass pc = PictureClass.get(classes, cutName);

            if(name.contains("training")) {
                pc.getTrainingList().add(name);
            } else {
                pc.getTestList().add(name);
            }

        }

        int targetTrainSplit = targetTrainAmount / 2;
        int targetTestSplit = targetTestAmount / 2;
        int pullAmount = targetTrainSplit + targetTestSplit;

        for(PictureClass pc : classes) {

            System.out.println("Starting on PictureClass name=" + pc.getName());

            List<String> tmpTrainSplit = new ArrayList<>();
            List<String> tmpTestSplit = new ArrayList<>();

            if(pc.getTrainingList().size() >= pullAmount) {
                //TODO pull 600 random entries

                System.out.println("fill train list with random");

                Set<Integer> rndNumbers = new Random().ints(0, pc.getTrainingList().size())
                        .distinct()
                        .limit(pullAmount)
                        .boxed()
                        .collect(Collectors.toSet());

                for(Integer rnd : rndNumbers) {
                    tmpTrainSplit.add(pc.getTrainingList().get(rnd.intValue()));
                }

            } else {
                //TODO fill up to 600 entries

                System.out.println("fill train list with filling");

                int copyCount = 0;
                int currentIndex = 0;

                while(copyCount < pullAmount) {

                    tmpTrainSplit.add(pc.getTrainingList().get(currentIndex));
                    currentIndex++;

                    if(currentIndex >= pc.getTrainingList().size()) {
                        currentIndex = 0;
                    }

                    copyCount++;

                }

            }

            if(pc.getTestList().size() >= pullAmount) {
                //TODO pull 600 random entries

                System.out.println("fill test list with random");

                Set<Integer> rndNumbers = new Random().ints(0, pc.getTestList().size())
                        .distinct()
                        .limit(pullAmount)
                        .boxed()
                        .collect(Collectors.toSet());

                for(Integer rnd : rndNumbers) {
                    tmpTestSplit.add(pc.getTestList().get(rnd.intValue()));
                }

            } else {
                //TODO fill up to 600 entries

                System.out.println("fill test list with filling");

                int copyCount = 0;
                int currentIndex = 0;

                while(copyCount < pullAmount) {

                    tmpTestSplit.add(pc.getTestList().get(currentIndex));
                    currentIndex++;

                    if(currentIndex >= pc.getTestList().size()) {
                        currentIndex = 0;
                    }

                    copyCount++;

                }

            }

            //TODO copy first 500 of each as train
            //TODO copy last 100 of each as test

            System.out.println("filling train and test split lists");

            List<String> firstTrainEntries = tmpTrainSplit.stream().limit(targetTrainSplit).collect(Collectors.toList());
            List<String> firstTestEntries = tmpTestSplit.stream().limit(targetTrainSplit).collect(Collectors.toList());

            List<String> lastTrainEntries = tmpTrainSplit.subList(targetTrainSplit, pullAmount);
            List<String> lastTestEntries = tmpTestSplit.subList(targetTrainSplit, pullAmount);

            List<String> trainSplit = new ArrayList<>();
            List<String> testSplit = new ArrayList<>();

            trainSplit.addAll(firstTrainEntries);
            trainSplit.addAll(firstTestEntries);
            testSplit.addAll(lastTrainEntries);
            testSplit.addAll(lastTestEntries);

            System.out.println("copying train split");
            int copyCount = 0;

            for(String trainName : trainSplit) {

                int cutFrom;
                int cutTo = trainName.indexOf("roi") - 1;

                if(trainName.contains("training")) {
                    cutFrom = 9;
                } else {
                    cutFrom = trainName.indexOf('_') + 1;
                }

                String cutName = trainName.substring(cutFrom, cutTo);

                if(cutName.equals("appendicularia")) {
                    cutName = "appendicularia0";
                } else if(cutName.equals("larvae")) {
                    cutName = "larvae0";
                }

                String uniqueName = "training_" + cutName + "_" + copyCount + ".png";
                File input = new File(inputRootPath + "//" + trainName);
                File output = new File(outputRootPath + uniqueName);
                FileUtils.copyFile(input, output);
                copyCount++;

            }

            System.out.println("copying test split");
            copyCount = 0;

            for(String testName : testSplit) {

                int cutFrom;
                int cutTo = testName.indexOf("roi") - 1;

                if(testName.contains("training")) {
                    cutFrom = 9;
                } else {
                    cutFrom = testName.indexOf('_') + 1;
                }

                String cutName = testName.substring(cutFrom, cutTo);

                if(cutName.equals("appendicularia")) {
                    cutName = "appendicularia0";
                } else if(cutName.equals("larvae")) {
                    cutName = "larvae0";
                }

                String uniqueName = "test_" + cutName + "_" + copyCount + ".png";
                File input = new File(inputRootPath + "//" + testName);
                File output = new File(outputRootPath + uniqueName);
                FileUtils.copyFile(input, output);
                copyCount++;

            }

            System.out.println("finished class");

        }

    }

    public static class PictureClass {

        public static PictureClass get(List<PictureClass> list, String name) {
            for(PictureClass pc : list) {
                if(pc.getName().equals(name)) {
                    return pc;
                }
            }
            return null;
        }

        public static boolean contains(List<PictureClass> list, String name) {
            return get(list, name) != null;
        }

        private final String name;
        private final List<String> trainingList;
        private final List<String> testList;

        public PictureClass(String name) {
            this.name = name;
            this.trainingList = new ArrayList<>();
            this.testList = new ArrayList<>();
        }

        public String getName() {
            return this.name;
        }

        public List<String> getTrainingList() {
            return this.trainingList;
        }

        public List<String> getTestList() {
            return this.testList;
        }

    }

}
