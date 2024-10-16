package ca.ucalgary.edu.ensf380.models;

/**
 * Represents geographical coordinates with latitude and longitude.
 */
public class Coordinates {
    private final double latitude;
    private final double longitude;

    /**
     * Constructs a new Coordinates object.
     *
     * @param latitude  The latitude of the location.
     * @param longitude The longitude of the location.
     */
    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the latitude of the location.
     *
     * @return The latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude of the location.
     *
     * @return The longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns a string representation of the coordinates in the format (latitude, longitude).
     *
     * @return A string representing the coordinates.
     */
    @Override
    public String toString() {
        return String.format("(%.6f, %.6f)", latitude, longitude);
    }
}
