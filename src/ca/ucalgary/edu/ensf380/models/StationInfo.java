package ca.ucalgary.edu.ensf380.models;

import java.util.Set;
import java.util.HashSet;

/**
 * Represents information about a train station.
 */
public class StationInfo {
    private final int stationId;
    private final String lineCode;
    private final String stationIdentifier;
    private final String stationLabel;
    private final Coordinates location;

    /**
     * Constructs a new StationInfo object.
     *
     * @param stationId         The unique ID of the station.
     * @param lineCode          The code of the line the station belongs to.
     * @param stationIdentifier The unique identifier code for the station.
     * @param stationLabel      The name or label of the station.
     * @param location          The geographical coordinates of the station.
     */
    public StationInfo(int stationId, String lineCode, String stationIdentifier,
                       String stationLabel, Coordinates location) {
        this.stationId = stationId;
        this.lineCode = lineCode;
        this.stationIdentifier = stationIdentifier;
        this.stationLabel = stationLabel;
        this.location = location;
    }

    /**
     * Gets the unique ID of the station.
     *
     * @return The unique ID of the station.
     */
    public int getStationId() {
        return stationId;
    }

    /**
     * Gets the code of the line the station belongs to.
     *
     * @return The line code of the station.
     */
    public String getLineCode() {
        return lineCode;
    }

    /**
     * Gets the unique identifier code for the station.
     *
     * @return The unique identifier code of the station.
     */
    public String getStationIdentifier() {
        return stationIdentifier;
    }

    /**
     * Gets the name or label of the station.
     *
     * @return The station name or label. If the station label is empty, a single space is returned.
     */
    public String getStationLabel() {
        return stationLabel.isEmpty() ? " " : stationLabel;
    }

    /**
     * Gets the geographical coordinates of the station.
     *
     * @return The geographical coordinates of the station.
     */
    public Coordinates getLocation() {
        return location;
    }

    /**
     * Returns a string representation of the station information.
     *
     * @return A string representing the station information.
     */
    @Override
    public String toString() {
        return String.format("Station %s: %s (Line %s) at %s",
                stationIdentifier, getStationLabel(), lineCode, location);
    }
}
