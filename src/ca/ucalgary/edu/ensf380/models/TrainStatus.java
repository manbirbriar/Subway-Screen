package ca.ucalgary.edu.ensf380.models;

import ca.ucalgary.edu.ensf380.train.StationDataImporter;

import java.util.Map;
import java.util.Optional;

/**
 * Represents the status of a train including its current position, direction, and next/previous stations.
 */
public class TrainStatus {
    private final String routeName;
    private final int trainId;
    private final String currentStationId;
    private final String movementDirection;
    private final String endStation;
    private final StationInfo currentStationInfo;
    private final Optional<StationInfo> nextStationInfo;
    private final Optional<StationInfo> previousStationInfo;

    /**
     * Constructs a new TrainStatus object by importing station data and initializing the train's status.
     *
     * @param routeName         The name of the train route.
     * @param trainId           The unique ID of the train.
     * @param currentStationId  The identifier of the current station.
     * @param movementDirection The direction of the train's movement ("forward" or "backward").
     * @param endStation        The identifier of the end station of the route.
     * @param nextStationId     The identifier of the next station (can be empty if not available).
     * @param previousStationId The identifier of the previous station (can be empty if not available).
     */
    public TrainStatus(String routeName, int trainId, String currentStationId,
                       String movementDirection, String endStation,
                       String nextStationId, String previousStationId) {
        StationDataImporter importer = new StationDataImporter();
        Map<String, StationInfo> stationData = importer.importStationData();

        this.routeName = routeName;
        this.trainId = trainId;
        this.currentStationId = currentStationId;
        this.movementDirection = movementDirection;
        this.endStation = endStation;

        this.currentStationInfo = stationData.get(currentStationId);
        this.nextStationInfo = Optional.ofNullable(stationData.get(nextStationId));
        this.previousStationInfo = Optional.ofNullable(stationData.get(previousStationId));
    }

    /**
     * Gets the name of the train route.
     *
     * @return The name of the route.
     */
    public String getRouteName() {
        return routeName;
    }

    /**
     * Gets the unique ID of the train.
     *
     * @return The train ID.
     */
    public int getTrainId() {
        return trainId;
    }

    /**
     * Gets the identifier of the current station.
     *
     * @return The current station identifier.
     */
    public String getCurrentStationId() {
        return currentStationId;
    }

    /**
     * Gets the direction of the train's movement.
     *
     * @return The movement direction ("forward" or "backward").
     */
    public String getMovementDirection() {
        return movementDirection;
    }

    /**
     * Gets the identifier of the end station of the route.
     *
     * @return The end station identifier.
     */
    public String getEndStation() {
        return endStation;
    }

    /**
     * Gets the X coordinate of the current station.
     *
     * @return The X coordinate of the current station.
     */
    public int getCurrentStationX() {
        return (int) currentStationInfo.getLocation().getLatitude();
    }

    /**
     * Gets the Y coordinate of the current station.
     *
     * @return The Y coordinate of the current station.
     */
    public int getCurrentStationY() {
        return (int) currentStationInfo.getLocation().getLongitude();
    }

    /**
     * Gets the name of the current station.
     *
     * @return The name of the current station.
     */
    public String getCurrentStationName() {
        return currentStationInfo.getStationLabel();
    }

    /**
     * Gets the name of the previous station.
     *
     * @return The name of the previous station, or an empty string if not available.
     */
    public String getPreviousStationName() {
        return previousStationInfo.map(StationInfo::getStationLabel).orElse("");
    }

    /**
     * Gets the name of the next station, with an optional offset.
     *
     * @param offset The number of stations ahead to fetch. Zero means the immediate next station.
     * @return The name of the next station at the given offset, or an empty string if not available.
     */
    public String getNextStationName(int offset) {
        if (offset == 0) {
            return nextStationInfo.map(StationInfo::getStationLabel).orElse("");
        }

        String tempStationId = this.currentStationId;
        for (int i = 0; i <= offset; i++) {
            tempStationId = calculateNextStationId(tempStationId, this.movementDirection, this.endStation);
        }

        StationDataImporter importer = new StationDataImporter();
        Map<String, StationInfo> stationData = importer.importStationData();

        return Optional.ofNullable(stationData.get(tempStationId))
                .map(StationInfo::getStationLabel)
                .orElse("");
    }

    /**
     * Calculates the ID of the next station based on the current station ID, direction, and destination.
     *
     * @param currentId    The ID of the current station.
     * @param direction    The direction of movement ("forward" or "backward").
     * @param destination  The ID of the destination station.
     * @return The ID of the next station.
     */
    private String calculateNextStationId(String currentId, String direction, String destination) {
        int currentNumber = Integer.parseInt(currentId.replaceAll("\\D", ""));
        int destNumber = Integer.parseInt(destination.replaceAll("\\D", ""));
        int nextNumber;

        if ("forward".equals(direction)) {
            nextNumber = currentNumber + 1;
            if (nextNumber > destNumber) {
                nextNumber = 999;  // Or another value to indicate end of line
            }
        } else {  // backward
            nextNumber = currentNumber - 1;
            if (nextNumber < destNumber) {
                nextNumber = 999;  // Or another value to indicate end of line
            }
        }

        return currentId.replaceAll("\\d+", String.format("%02d", nextNumber));
    }
}
