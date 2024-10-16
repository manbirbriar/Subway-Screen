package ca.ucalgary.edu.ensf380.train;

import ca.ucalgary.edu.ensf380.models.TrainStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collects and processes train data from files in the output directory.
 * <p>
 * This class is responsible for finding the latest data file, reading the train status records,
 * and converting them into {@link TrainStatus} objects.
 */
public class TrainDataCollector {
    private static final Logger LOGGER = Logger.getLogger(TrainDataCollector.class.getName());
    private final List<TrainStatus> trainStatusList;

    /**
     * Constructs a new TrainDataCollector instance.
     * Initializes an empty list to hold the collected train statuses.
     */
    public TrainDataCollector() {
        this.trainStatusList = new ArrayList<>();
    }

    /**
     * Collects train data from the most recent data file in the output directory.
     * <p>
     * This method identifies the latest data file, reads its contents, and parses each record
     * into a {@link TrainStatus} object. The resulting array of {@link TrainStatus} objects is returned.
     *
     * @return An array of {@link TrainStatus} objects representing the collected train data.
     * @throws TrainDataException If there is an error processing the train data.
     * @throws IOException If there is an error reading the data file.
     */
    public TrainStatus[] collectTrainData() throws TrainDataException, IOException {
        Path dataDirectory = Paths.get(System.getProperty("user.dir"), "out");
        Path latestDataFile = findLatestDataFile(dataDirectory);

        if (latestDataFile == null) {
            throw new TrainDataException("No data files found in the output directory.");
        }

        try {
            Files.lines(latestDataFile)
                    .skip(1)  // Skip header
                    .forEach(this::parseTrainStatusRecord);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading train data file: " + e.getMessage(), e);
            throw new TrainDataException("Failed to read train data: " + e.getMessage());
        }

        return trainStatusList.toArray(new TrainStatus[0]);
    }

    /**
     * Parses a train status record from a CSV line and adds it to the train status list.
     * <p>
     * The record is expected to have 5 fields: route name, train ID, station ID, direction, and destination.
     * It also calculates the next and previous station IDs based on the current station ID and direction.
     *
     * @param record The CSV line representing a train status record.
     */
    private void parseTrainStatusRecord(String record) {
        String[] fields = record.split(",");
        if (fields.length != 5) return;

        String routeName = fields[0].trim();
        int trainId = Integer.parseInt(fields[1].trim());
        String stationId = fields[2].trim();
        String direction = fields[3].trim();
        String destination = fields[4].trim();

        String nextStationId = calculateAdjacentStation(stationId, direction, true);
        String prevStationId = calculateAdjacentStation(stationId, direction, false);

        TrainStatus status = new TrainStatus(routeName, trainId, stationId, direction,
                destination, nextStationId, prevStationId);
        trainStatusList.add(status);
    }

    /**
     * Calculates the ID of the adjacent station based on the current station ID and direction.
     * <p>
     * This is a simplified placeholder method. The actual implementation may depend on the specific station numbering system.
     *
     * @param stationId   The ID of the current station.
     * @param direction   The direction of movement ("forward" or "backward").
     * @param isNext      If true, calculates the ID of the next station; if false, calculates the ID of the previous station.
     * @return The ID of the adjacent station.
     */
    private String calculateAdjacentStation(String stationId, String direction, boolean isNext) {
        int currentNumber = Integer.parseInt(stationId.replaceAll("\\D", ""));
        int adjacentNumber = isNext ?
                ("forward".equals(direction) ? currentNumber + 1 : currentNumber - 1) :
                ("forward".equals(direction) ? currentNumber - 1 : currentNumber + 1);
        return stationId.replaceAll("\\d+", String.format("%02d", adjacentNumber));
    }

    /**
     * Finds the latest data file in the specified directory based on the last modified time.
     * <p>
     * Returns the path to the most recently modified file or null if no files are found.
     *
     * @param directory The directory to search for data files.
     * @return The path to the latest data file, or null if no files are found.
     * @throws IOException If there is an error accessing the directory.
     */
    private Path findLatestDataFile(Path directory) throws IOException {
        return Files.list(directory)
                .filter(Files::isRegularFile)
                .max(TrainDataCollector::compareModificationTimes)
                .orElse(null);
    }

    /**
     * Compares the modification times of two files.
     *
     * @param p1 The first file.
     * @param p2 The second file.
     * @return A negative integer, zero, or a positive integer as the first file is less than, equal to, or greater than the second.
     */
    private static int compareModificationTimes(Path p1, Path p2) {
        try {
            return Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2));
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * Exception thrown when there is an error processing train data.
     */
    public static class TrainDataException extends Exception {
        public TrainDataException(String message) {
            super(message);
        }
    }
}
