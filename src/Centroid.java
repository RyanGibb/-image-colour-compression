import java.util.*;
import java.util.List;

public class Centroid {
    private List<DataPoint> dataPoints = new ArrayList<>();
    private DataPoint centre;

    public Centroid(DataPoint centre) {
        this.centre = centre;
    }

    public void resetDataPoints(){
        dataPoints = new ArrayList<>();
    }

    public void addDataPoint(DataPoint dataPoint){
        if (centre.getCoords().length != dataPoint.getCoords().length){
            throw new IllegalArgumentException("Data point must have same number of coordinates as the centre: " +
                    centre.getCoords().length + " != " + dataPoint.getCoords().length);
        }
        dataPoints.add(dataPoint);
    }

    public void reassignCentre(){
        int[] newCentreCoords = new int[centre.getCoords().length];
        int[] coords = centre.getCoords();
        if (dataPoints.size() == 0){
            return;
        }
        for (int i = 0; i < coords.length; i++) {
            int sum = 0;
            for (DataPoint dataPoint : dataPoints){
                sum += dataPoint.getCoord(i);
            }
            int mean = sum / dataPoints.size();
            newCentreCoords[i] = mean;
        }
        centre = new DataPoint(newCentreCoords);
    }

    public DataPoint getCentre() {
        return centre;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    @Override
    public String toString() {
        return centre.toString();
    }
}
