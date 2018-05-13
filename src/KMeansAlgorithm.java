
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class KMeansAlgorithm {
    private Centroid[] centroids;
    private List<DataPoint> dataPoints;

    //default values
    private boolean verbose = false;
    private Consumer<Integer> initialCentroidsMethod = this::initialCentroidsRandomDataPoints;

    public KMeansAlgorithm(List<DataPoint> dataPoints, Map<String, String> properties) {
        this(dataPoints);
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
        /*Random rnd = new Random();
        centroids = new Centroid[k];
        //TODO: Optimize this method
        Integer[][] dataPointRanges = null;
        if (dataPoints == null) {
            int numCoords = dataPoints.get(0).getCoords().length;
            dataPointRanges = new Integer[2][numCoords];
            Integer[] minCoords = new Integer[numCoords];
            for (int i = 0; i < numCoords; i++) {
                minCoords[i] = dataPoints.get(0).getCoords()[i];
            }
            for (DataPoint dataPoint : dataPoints) {
                int[] coords = dataPoint.getCoords();
                for (int i = 0; i < numCoords; i++) {
                    int coord = coords[i];
                    if (coord < minCoords[i]){
                        minCoords[i] = coord;
                    }
                }
            }

            Integer[] maxCoords = new Integer[numCoords];
            for (int i = 0; i < numCoords; i++) {
                minCoords[i] = dataPoints.get(0).getCoords()[i];
            }
            for (DataPoint dataPoint : dataPoints) {
                int[] coords = dataPoint.getCoords();
                for (int i = 0; i < numCoords; i++) {
                    int coord = coords[i];
                    if (coord > minCoords[i]){
                        maxCoords[i] = coord;
                    }
                }
            }
            dataPointRanges = new Integer[][] {minCoords, maxCoords};
        }
        for (int i = 0; i < centroids.length; i++) {
            centroids[i] = new Centroid(new DataPoint(

            ));
        }*/
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
            System.out.println("Number of data points assigned to a different centroid: " + numberOfDataPointsReassigned);
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
        System.out.println("Centroid centres coordinate change: " + totalCoordValueChange);
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
