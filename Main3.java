import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Main3 {

    public static void main(String[] args) throws IOException {

        overAndUnderSample();

    }

    public static void overAndUnderSample() throws IOException {

        final String inputRootPath = "D://KI_Plankton//Output200";
        final String outputRootPath = "D://KI_Plankton//Output200-1000//";
        final int targetTrainAmount = 1_000;
        final int targetTestAmount = 200;

        File rootDir = new File(inputRootPath);
        List<PictureClass> classes = new ArrayList<>();

        for(String name : rootDir.list()) {

            if (name.contains("training")) {

                int cutFrom = 9;
                int cutTo = name.indexOf("roi") - 1;
                String cutName = name.substring(cutFrom, cutTo);

                if (cutName.equals("pteropods") || cutName.equals("amphipods") || cutName.equals("phaeocystis") || cutName.equals("echinodermata")) {
                    continue;
                }

                if (!PictureClass.contains(classes, cutName, true)) {
                    classes.add(new PictureClass(cutName, true));
                }

                PictureClass.get(classes, cutName, true).getList().add(name);

            } else {

                int cutFrom = name.indexOf('_') + 1;
                int cutTo = name.indexOf("roi") - 1;
                String cutName = name.substring(cutFrom, cutTo);

                if (!PictureClass.contains(classes, cutName, false)) {
                    classes.add(new PictureClass(cutName, false));
                }

                PictureClass.get(classes, cutName, false).getList().add(name);

            }

        }

        for(PictureClass pc : classes) {

            System.out.println("Starting on PictureClass: name=" + pc.getName() + ", train=" + pc.isTraining());

            if(pc.isTraining()) {

                if(pc.getList().size() >= targetTrainAmount) {

                    System.out.println("Using random");

                    Set<Integer> rndNumbers = new Random().ints(0, pc.getList().size())
                            .distinct()
                            .limit(targetTrainAmount)
                            .boxed()
                            .collect(Collectors.toSet());

                    for(Integer rnd : rndNumbers) {

                        String pictureName = pc.getList().get(rnd.intValue());
                        File input = new File(inputRootPath + "//" + pictureName);
                        File output = new File(outputRootPath + pictureName);
                        FileUtils.copyFile(input, output);

                    }

                } else {

                    System.out.println("Using fill");

                    int copyCount = 0;
                    int currentIndex = 0;

                    while(copyCount < targetTrainAmount) {

                        String pictureName = pc.getList().get(currentIndex);
                        String uniqueName = pictureName.substring(0, pictureName.lastIndexOf(".tif")) + "_" + copyCount + ".tif.png";
                        File input = new File(inputRootPath + "//" + pictureName);
                        File output = new File(outputRootPath + uniqueName);
                        FileUtils.copyFile(input, output);

                        currentIndex++;

                        if(currentIndex >= pc.getList().size()) {
                            currentIndex = 0;
                        }

                        copyCount++;

                    }

                }

            } else {

                if(pc.getList().size() >= targetTestAmount) {

                    System.out.println("Using random");

                    Set<Integer> rndNumbers = new Random().ints(0, pc.getList().size())
                            .distinct()
                            .limit(targetTestAmount)
                            .boxed()
                            .collect(Collectors.toSet());

                    for(Integer rnd : rndNumbers) {

                        String pictureName = pc.getList().get(rnd.intValue());
                        File input = new File(inputRootPath + "//" + pictureName);
                        File output = new File(outputRootPath + pictureName);
                        FileUtils.copyFile(input, output);

                    }

                } else {

                    System.out.println("Using fill");

                    int copyCount = 0;
                    int currentIndex = 0;

                    while(copyCount < targetTestAmount) {

                        String pictureName = pc.getList().get(currentIndex);
                        String uniqueName = pictureName.substring(0, pictureName.lastIndexOf(".tif")) + "_" + copyCount + ".tif.png";
                        File input = new File(inputRootPath + "//" + pictureName);
                        File output = new File(outputRootPath + uniqueName);
                        FileUtils.copyFile(input, output);

                        currentIndex++;

                        if(currentIndex >= pc.getList().size()) {
                            currentIndex = 0;
                        }

                        copyCount++;

                    }

                }

            }

            System.out.println("Finished PictureClass: name=" + pc.getName() + ", train=" + pc.isTraining());

        }

    }

    public static class PictureClass {

        public static PictureClass get(List<PictureClass> list, String name, boolean training) {
            for(PictureClass pc : list) {
                if(pc.getName().equals(name) && pc.isTraining() == training) {
                    return pc;
                }
            }
            return null;
        }

        public static boolean contains(List<PictureClass> list, String name, boolean training) {
            return get(list, name, training) != null;
        }

        private final String name;
        private final boolean training;
        private final List<String> list;

        public PictureClass(String name, boolean training) {
            this.name = name;
            this.training = training;
            this.list = new ArrayList<>();
        }

        public String getName() {
            return this.name;
        }

        public boolean isTraining() {
            return this.training;
        }

        public List<String> getList() {
            return this.list;
        }

    }

}
