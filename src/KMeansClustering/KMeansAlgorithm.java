package KMeansClustering;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public class KMeansAlgorithm {
    private Centroid[] centroids;
    private List<DataPoint> dataPoints;

    public KMeansAlgorithm(List<DataPoint> dataPoints) {
        if (dataPoints.size() <= 1) {
            throw new IllegalArgumentException("List of data points must be larger than 1");
        }
        this.dataPoints = dataPoints;
    }

    public void kmeans(int k) {
        initialCentroids(k);
        boolean finished = false;
        while (!finished) {
            assignDataPointsToCentroid();
            finished = updateCentroids();
        }
    }

    private void initialCentroids(int k) {
        Random rnd = new Random();
        centroids = new Centroid[k];
        for (int i = 0; i < centroids.length; i++) {
            centroids[i] = new Centroid(dataPoints.get(rnd.nextInt(dataPoints.size())));
        }
    }


    public void assignDataPointsToCentroid() {
        Arrays.stream(centroids).forEach(Centroid::resetDataPoints);
        for (DataPoint dataPoint : dataPoints) {
            Centroid minCentroid = getClosestCentroid(dataPoint);
            dataPoint.setCentroid(minCentroid);
            minCentroid.addDataPoint(dataPoint);
        }
    }

    public Centroid getClosestCentroid(DataPoint dataPoint){
        double minDistance = dataPoint.distance(centroids[0].getCentre());
        Centroid minCentroid = centroids[0];
        for (int i = 1; i < centroids.length; i++) {
            Centroid centroid = centroids[i];
            double distance = dataPoint.distance(centroid.getCentre());
            if (distance < minDistance) {
                minDistance = distance;
                minCentroid = centroid;
            }
        }
        return minCentroid;
    }

    public boolean updateCentroids() {
        int numCentroids = centroids.length;
        DataPoint[] oldCentres = new DataPoint[numCentroids];
        for (int i = 0; i < numCentroids; i++) {
            oldCentres[i] = centroids[i].getCentre();
        }

        Arrays.stream(centroids).forEach(Centroid::reassignCentre);

        DataPoint[] newCentres = Arrays.stream(centroids)
                .map(Centroid::getCentre)
                .toArray(DataPoint[]::new);

        for (int i = 0; i < numCentroids; i++) {
            if (!newCentres[i].equals(oldCentres[i])) {
                return false;
            }
        }
        return true;
    }

    public Centroid[] getCentroids() {
        return centroids;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

}
