import java.io.IOException;
import java.util.*;

/**
 * Class to use the FilePathsAndImageIO and ImageManipulation classes to compress an image into a number of colors.
 */
public class CompressImage {
    public static final String USAGE = "java CompressImage (options) <image input path> <No. colors> (output image path/directory)";
    public static final String OPTIONS = "-h, --help: gives info on usage and options\n" +
            "-i, --initialization METHOD: specifies centroid initialization METHOD in the k-means clustering algorithm, where the METHOD is one of:\n" +
               "\t\"k++\"\n" +
               "\t\"random coordinate\"\n" +
               "\t\"random data point (default)\"\n" +
            "-v, --verbose: makes the K Means Algorithm verbose, and output progress information.\n" +
            "-p, --progress-image N: outputs an image every N iterations of the k-means clustering algorithm, where N is a positive integer\n" +
            "EXAMPLE: java CompressImage -i \"random data point\" -v --progress-image 10 image-to-compress.jpg 8";
    public static final String DEFAULT_FILE_FORMAT = "jpg";

    public static void main(String[] args) {
        HashMap<String, String> properties = new HashMap<>();
        List<String> programOperands = new ArrayList<>();
        //Can specify default options, different from those in KMeansAlgorithm, here
        for (int i = 0; i < args.length; i++){
            if(args[i].equals("-h") || args[i].equals("--help")){
                System.out.println(USAGE + "\n" + OPTIONS);
                return;
            }
            else if (args[i].equals("-i") || args[i].equals("--initialization")){
                properties.put("initialization", args[++i]);
            }
            else if (args[i].equals("-v") || args[i].equals("--verbose")){
                properties.put("verbose", "true");
            }
            else if (args[i].equals("-p") || args[i].equals("--progress-image")){
                properties.put("progress-image", args[++i]);
            }
            else{
                programOperands.add(args[i]);
            }
        }

        if (programOperands.size() < 2) {
            System.out.println(USAGE + "\n" + "use \"java CompressImage --help\" for more info");
            return;
        }

        String inputPath = programOperands.get(0);
        int k;
        try {
            k = Integer.parseInt(programOperands.get(1));
        } catch (NumberFormatException e) {
            System.out.println("The No. Colors to compress to must be an integer: " + programOperands.get(1) + " given.");
            return;
        }
        String outputPath = null;
        if (programOperands.size() > 3){
            outputPath = programOperands.get(2);
        }

        try {
            outputPath = FilePathsAndImageIO.validateOutputPath(outputPath, inputPath, k);
        } catch (IOException e) {
            System.out.println("IO Exception occurred: " + e.getMessage());
            return;
        }

        System.out.println("Input Path: " + inputPath + "\tK-Means: " + k + "\tOutput Path: " + outputPath);
        System.out.println("Properties:");
        for (Map.Entry<String, String> property : properties.entrySet()){
            System.out.println("\t" + property.getKey() + ":\t" + property.getValue());
        }

        ImageManipulation.compressImageToKColors(inputPath, outputPath, k, properties);
    }

}