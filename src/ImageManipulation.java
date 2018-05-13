import KMeansClustering.Centroid;
import KMeansClustering.DataPoint;
import KMeansClustering.KMeansAlgorithm;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageManipulation {

    public static void compressImageToKColors(String inputPath, String outputPath, int k){
        BufferedImage image = FileIO.inputImage(inputPath);
        List<DataPoint> dataPoints = getDataPoints(image);

        KMeansAlgorithm kMeansAlgorithm = new KMeansAlgorithm(dataPoints);
        kMeansAlgorithm.setVerbose(true);

        kMeansAlgorithm.kmeans(k);

        modifyImageColors(image, kMeansAlgorithm);
        FileIO.outputImage(image, outputPath);
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

    private static BufferedImage modifyImageColors(BufferedImage image, KMeansAlgorithm kMeansAlgorithm){
        int[] rgbData = image.getRGB(0,0, image.getWidth(), image.getHeight(),
                null, 0,image.getWidth());
        for (int x = 0; x < image.getWidth(); x++){
            for (int y = 0; y < image.getHeight(); y++) {
                int colorRed=(rgbData[(y*image.getWidth())+x] >> 16) & 0xFF;
                int colorGreen=(rgbData[(y*image.getWidth())+x] >> 8) & 0xFF;
                int colorBlue=(rgbData[(y*image.getWidth())+x]) & 0xFF;

                DataPoint dataPoint = new DataPoint(colorRed, colorGreen, colorBlue);
                DataPoint closest = kMeansAlgorithm.getClosestCentroid(dataPoint).getCentre();
                image.setRGB(x, y, new Color(closest.getX(), closest.getY(), closest.getZ()).getRGB());
            }
        }
        return image;
    }

}
