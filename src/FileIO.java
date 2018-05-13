import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileIO {

    //Inputs an image from a filePath
    protected static BufferedImage inputImage(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path must not be null.");
        }
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(filePath));
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
        return image;
    }

    protected static String validateOutputPath(String outputPathString, String inputPath, int k) throws IOException {
        if (outputPathString == null) {
            return createOutputPath(inputPath, k);
        }
        File outputPath = new File(outputPathString);
        if (outputPath.isDirectory()) {
            File outputPathFile = new File(outputPath, createOutputPath(inputPath, k));
            return outputPathFile.getCanonicalPath();
        }
        return outputPath.getCanonicalPath();
    }

    protected static String createOutputPath(String inputPath, int k) {
        int i = inputPath.lastIndexOf('.');
        if (i == -1){
            return inputPath + "-output-" + k + "-colors." + CompressImage.DEFAULT_FILE_FORMAT;
        }
        else {
            return inputPath.substring(0, i) + "-output-" + k + "." + CompressImage.DEFAULT_FILE_FORMAT;
        }
    }

    //Outputs an image to a filePath
    protected static void outputImage(BufferedImage image, String filePath) {
        if (image == null) {
            throw new IllegalArgumentException("Image must not be null.");
        } else if (filePath == null) {
            throw new IllegalArgumentException("File path must not be null.");
        }
        int i = filePath.lastIndexOf('.');
        String formatName;
        //If there is no "." in the filePath
        if (i == -1) {
            //Set the file format to the default file format
            formatName = CompressImage.DEFAULT_FILE_FORMAT;
            //Appends the filePath with the default file format
            filePath += "." + CompressImage.DEFAULT_FILE_FORMAT;
        } else {
            //Otherwise get the format name from the file path extension
            formatName = filePath.substring(i + 1);
        }
        File imageFile = new File(filePath);
        try {
            ImageIO.write(image, formatName, imageFile);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }

}
