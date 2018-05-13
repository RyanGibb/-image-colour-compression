import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class to manipulate to calculate and compress and image to a number of colors
 *  using the K Means Algorithm (KMeansAlgorithm)
 */
public class ImageManipulation {

    public static void compressImageToKColors(String inputPath, String outputPath, int k, HashMap<String, String> properties){
        BufferedImage image = FilePathsAndImageIO.inputImage(inputPath);
        List<DataPoint> dataPoints = getDataPoints(image);

        KMeansAlgorithm kMeansAlgorithm = new KMeansAlgorithm(dataPoints, properties);

        boolean intermediateImage = false;
        if (properties.containsKey("intermediate-images")){
            intermediateImage = true;
            int iterations = 0;
            try {
                iterations = Integer.parseInt(properties.get("intermediate-images"));
                if (iterations <= 0){
                    System.out.println("intermediate-images's value must be positive. No intermediate images will be output.");
                    intermediateImage = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("intermediate-images's value must be an integer. No intermediate images will be output.");
                intermediateImage = false;
            }

            if (intermediateImage){
                kMeansAlgorithm.kmeansWithIntermediateImages(k, iterations, image);
            }
        }

        if (!intermediateImage) {
            kMeansAlgorithm.kmeans(k);
        }

        image = modifyImageColors(image, kMeansAlgorithm);
        FilePathsAndImageIO.outputImage(image, outputPath);
    }

    private static List<DataPoint> getDataPoints(BufferedImage image){
        List<DataPoint> dataPoints = new ArrayList<>();
        int[] rgbData = image.getRGB(0,0, image.getWidth(), image.getHeight(),
                null, 0,image.getWidth());
        for (int x = 0; x < image.getWidth(); x++){
            for (int y = 0; y < image.getHeight(); y++){
                int rgbValue = rgbData[(y*image.getWidth())+x];
                int colorRed = (rgbValue >> 16) & 0xFF;
                int colorGreen = (rgbValue >> 8) & 0xFF;
                int colorBlue = rgbValue & 0xFF;
                dataPoints.add(new DataPoint(colorRed, colorGreen, colorBlue));
            }
        }
        return dataPoints;
    }

    private static List<Color> getCentroidColors(Centroid[] centroids){
        List<Color> colors = new ArrayList<>();
        for (Centroid centroid : centroids){
            DataPoint centre = centroid.getCentre();
            Color color = new Color(centre.getX(), centre.getY(), centre.getZ());
            colors.add(color);
        }
        return colors;
    }

    protected static BufferedImage modifyImageColors(BufferedImage image, KMeansAlgorithm kMeansAlgorithm){
        int[] rgbData = image.getRGB(0,0, image.getWidth(), image.getHeight(),
                null, 0,image.getWidth());
        for (int x = 0; x < image.getWidth(); x++){
            for (int y = 0; y < image.getHeight(); y++) {
                int rgbValue = rgbData[(y*image.getWidth())+x];
                int colorRed = (rgbValue >> 16) & 0xFF;
                int colorGreen = (rgbValue >> 8) & 0xFF;
                int colorBlue = (rgbValue) & 0xFF;

                DataPoint dataPoint = new DataPoint(colorRed, colorGreen, colorBlue);
                DataPoint closest = kMeansAlgorithm.getClosestCentroid(dataPoint).getCentre();
                image.setRGB(x, y, new Color(closest.getX(), closest.getY(), closest.getZ()).getRGB());
            }
        }
        return image;
    }

}
