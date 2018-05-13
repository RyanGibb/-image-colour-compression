
public class DataPoint{
    private int[] coords;
    private Centroid centroid;

    public DataPoint(int x, int y) {
        coords = new int[] {x, y};
    }

    public DataPoint(int x, int y, int z) {
        coords = new int[] {x, y, z};
    }

    public DataPoint(int... coords) {
        if (coords.length == 0){
            throw new IllegalArgumentException("Array of integer coordinates must not be less than 1");
        }
        this.coords = coords;
    }

    public int getCoord(int index) throws NullPointerException{
        return coords[index];
    }

    public int[] getCoords() {
        return coords;
    }

    public Centroid getCentroid() {
        return centroid;
    }

    public void setCentroid(Centroid centroid) {
        this.centroid = centroid;
    }

    public int getX() throws NullPointerException {
        return coords[0];
    }

    public int getY() throws NullPointerException {
        return coords[1];
    }

    public int getZ() throws NullPointerException {
        return coords[2];
    }

    public double distance(DataPoint that){
        if (this.getCoords().length != that.getCoords().length){
            throw new IllegalArgumentException("Data points must have same number of coordinates to calculate distance: " +
                    this.getCoords().length + " != " + that.getCoords().length);
        }
        int sumOfSquares = 0;
        for (int i = 0; i < coords.length; i++) {
            sumOfSquares += Math.pow(coords[i] - that.coords[i], 2);
        }
        return Math.sqrt(sumOfSquares);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0, coordsLength = coords.length; i < coordsLength; i++) {
            sb.append(coords[i]);
            if (i != coordsLength - 1){
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataPoint){
            DataPoint that = (DataPoint) o;
            if (this.getCoords().length != that.getCoords().length){
                return false;
            }
            for (int j = 0; j < coords.length; j++) {
                if (coords[j] != that.getCoords()[j]){
                    return false;
                }
            }
            return true;
        }
        return super.equals(o);
    }
}
