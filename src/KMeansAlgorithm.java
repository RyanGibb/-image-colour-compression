
import javax.xml.crypto.Data;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class KMeansAlgorithm {
    private Centroid[] centroids;
    private List<DataPoint> dataPoints;
    private int numCoords;

    //default values
    private boolean verbose = false;
    private Consumer<Integer> initialCentroidsMethod = this::initialCentroidsRandomDataPoints;

    public KMeansAlgorithm(List<DataPoint> dataPoints, Map<String, String> properties) {
        this(dataPoints);
        if (properties.containsKey("verbose")) {
            switch (properties.get("verbose")) {
                case "true":
                    verbose = true;
                    break;
                case "false":
                    verbose = false;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid \"verbose\" value. Must be \"true\" or \"false\".");
            }
        }
        if (properties.containsKey("initialization")) {
            switch (properties.get("initialization")) {
                case "k++":
                    initialCentroidsMethod = this::initialCentroidsKPlusPlus;
                    break;
                case "random coordinate":
                    initialCentroidsMethod = this::initialCentroidsRandomCoord;
                    break;
                case "random data point":
                    initialCentroidsMethod = this::initialCentroidsRandomDataPoints;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid \"initialization\" value. " +
                            "Must be \"k++\", \"random coordinate\", or \"random data point\".");
            }
        }
    }

    public KMeansAlgorithm(List<DataPoint> dataPoints) {
        if (dataPoints.size() <= 1) {
            throw new IllegalArgumentException("List of data points must be larger than 1");
        }
        numCoords = dataPoints.get(0).getCoords().length;
        for (int i = 1; i < dataPoints.size(); i++) {
            DataPoint dataPoint = dataPoints.get(i);
            if (dataPoint.getCoords().length != numCoords){
                throw new IllegalArgumentException("All data points must have the same number of coordinates.");
            }
        }
        this.dataPoints = dataPoints;
    }

    public void kmeans(int k) {
        initialCentroids(k);
        if (verbose) {
            System.out.println("Number of data points assigned to a different centroid" +
                    "\tCentroid centres coordinate change");
        }
        boolean finished = false;
        while (!finished) {
            assignDataPointsToCentroid();
            System.out.print("\t");
            finished = updateCentroids();
            System.out.println();
        }
    }

    public void kmeansWithIntermediateImages(int k, int iterations, BufferedImage image) {
        initialCentroids(k);
        boolean finished = false;
        int counter = 0;
        while (!finished) {
            assignDataPointsToCentroid();
            System.out.print("\t");
            counter++;
            if (counter % iterations == 0) {
                FilePathsAndImageIO.outputImage(ImageManipulation.modifyImageColors(image, this), "progress-images/iteration-" + counter);
            }
            finished = updatedCentroids();
            System.out.println();
        }
    }

    public void initialCentroids(int k) {
        initialCentroidsMethod.accept(k);
    }

    private void initialCentroidsRandomDataPoints(int k) {
        Random rnd = new Random();
        centroids = new Centroid[k];
        for (int i = 0; i < centroids.length; i++) {
            centroids[i] = new Centroid(dataPoints.get(rnd.nextInt(dataPoints.size())));
        }
    }

    //TODO: implement k++ initial centroids
    //See https://en.wikipedia.org/wiki/K-means%2B%2B
    //https://stackoverflow.com/questions/9330394/how-to-pick-an-item-by-its-probability
    private void initialCentroidsKPlusPlus(int k) {
        Random rnd = new Random();
        centroids = new Centroid[k];
        centroids[0] = new Centroid(dataPoints.get(rnd.nextInt(dataPoints.size())));


        for (int i = 1; i < centroids.length; i++) {
            double sumDistances = 0;
            double[] distances = new double[dataPoints.size()];
            for (int j = 0; j < dataPoints.size(); j++) {
                DataPoint dataPoint = dataPoints.get(j);
                double minDistance = dataPoint.distance(centroids[0].getCentre());
                for (int l = 0; l < i+1; l++) {
                    Centroid centroid = centroids[l];
                    double distance = dataPoint.distance(centroid.getCentre());
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }
                sumDistances += minDistance;
                distances[j] = minDistance;
            }


        }
    }

    private void initialCentroidsRandomCoord(int k) {
        centroids = new Centroid[k];

        int[] maxCoords = new int[numCoords];
        int[] minCoords = new int[numCoords];
        System.arraycopy(dataPoints.get(0).getCoords(), 0, maxCoords, 0, numCoords);
        System.arraycopy(dataPoints.get(0).getCoords(), 0, minCoords, 0, numCoords);

        for (int i = 1; i < dataPoints.size(); i++) {
            int[] coords = dataPoints.get(i).getCoords();
            for (int coordIndex = 0; coordIndex < numCoords; coordIndex++) {
                if (maxCoords[coordIndex] < coords[coordIndex]){
                    maxCoords[coordIndex] = coords[coordIndex];
                }
                if (minCoords[coordIndex] > coords[coordIndex]){
                    minCoords[coordIndex] = coords[coordIndex];
                }
            }
        }

        Random rnd = new Random();
        for (int i = 0; i < centroids.length; i++) {
            int[] coords = new int[numCoords];
            for (int coordIndex = 0; coordIndex < numCoords; coordIndex++) {
                coords[coordIndex] =
                        rnd.nextInt(
                                //+1 so upper limit is inclusive
                                maxCoords[coordIndex] + 1 - minCoords[coordIndex]
                        )
                        + minCoords[coordIndex];
            }
            centroids[i] = new Centroid(
                    new DataPoint(coords)
            );
        }
    }

    public void assignDataPointsToCentroid() {
        Arrays.stream(centroids).forEach(Centroid::resetDataPoints);
        int numberOfDataPointsReassigned = 0;
        for (DataPoint dataPoint : dataPoints) {
            Centroid minCentroid = getClosestCentroid(dataPoint);
            if (verbose) {
                if (dataPoint.getCentroid() != minCentroid) {
                    numberOfDataPointsReassigned++;
                }
            }
            dataPoint.setCentroid(minCentroid);
            minCentroid.addDataPoint(dataPoint);
        }
        if (verbose) {
            System.out.print(numberOfDataPointsReassigned);
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

    public boolean updatedCentroids() {
        int numCentroids = centroids.length;
        DataPoint[] oldCentres = new DataPoint[numCentroids];
        for (int i = 0; i < numCentroids; i++) {
            oldCentres[i] = centroids[i].getCentre();
        }

        Arrays.stream(centroids).forEach(Centroid::reassignCentre);

        DataPoint[] newCentres = Arrays.stream(centroids)
                .map(Centroid::getCentre)
                .toArray(DataPoint[]::new);

        return verbose ?
                checkCentroidCentresForChangeVerbose(oldCentres, newCentres, numCentroids)
                : checkCentroidCentresForChange(oldCentres, newCentres, numCentroids);
    }

    //old centres and new centres should have same length, numCentroids
    private boolean checkCentroidCentresForChange(DataPoint[] oldCentres, DataPoint[] newCentres, int numCentroids) {
        for (int i = 0; i < numCentroids; i++) {
            if (!newCentres[i].equals(oldCentres[i])) {
                return false;
            }
        }
        return true;
    }

    //old centres and new centres should have same length, numCentroids
    private boolean checkCentroidCentresForChangeVerbose(DataPoint[] oldCentres, DataPoint[] newCentres, int numCentroids) {
        int totalCoordValueChange = 0;
        int centreCoords = newCentres[0].getCoords().length;
        for (int i = 0; i < numCentroids; i++) {
            for (int j = 0; j < centreCoords; j++) {
                totalCoordValueChange += Math.abs(newCentres[i].getCoords()[j] - oldCentres[i].getCoords()[j]);
            }
        }
        System.out.print(totalCoordValueChange);
        return totalCoordValueChange == 0;
    }

    public Centroid[] getCentroids() {
        return centroids;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

}
