import java.io.IOException;

public class CompressImage {
    public static final String USAGE = "CompressImage <image input path> <No. colors> (output image path/directory)";
    public static final String DEFAULT_FILE_FORMAT = "jpg";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(USAGE);
            return;
        }

        String inputPath = args[0];
        int k = Integer.parseInt(args[1]);
        String outputPath = null;
        if (args.length > 3){
            outputPath = args[2];
        }

        try {
            outputPath = FileIO.validateOutputPath(outputPath, inputPath, k);
        } catch (IOException e) {
            System.out.println("IO Exception occurred: " + e.getMessage());
            return;
        }

        System.out.println("Input Path: " + inputPath + "\tK-Means: " + k + "\tOutput Path: " + outputPath);

        ImageManipulation.compressImageToKColors(inputPath, outputPath, k);
    }

}