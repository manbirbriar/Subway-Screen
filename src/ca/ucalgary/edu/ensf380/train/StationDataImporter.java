package ca.ucalgary.edu.ensf380.train;

import ca.ucalgary.edu.ensf380.models.Coordinates;
import ca.ucalgary.edu.ensf380.models.StationInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The StationDataImporter class is responsible for importing station data
 * from a CSV file and storing it in a directory (map) for easy retrieval.
 * This class reads a predefined CSV file containing station information,
 * processes each record, and creates StationInfo objects which are then
 * stored in a map with the station identifier as the key.
 */
public class StationDataImporter {

    private static final Logger LOGGER = Logger.getLogger(StationDataImporter.class.getName());
    private String dataSource = "./data/subway.csv";
    private Map<String, StationInfo> stationDirectory;

    /**
     * Constructs a new StationDataImporter with an empty station directory.
     */
    public StationDataImporter() {
        this.stationDirectory = new HashMap<>();
    }

    /**
     * Imports station data from the CSV file specified by the dataSource attribute.
     *
     * @return a map containing station identifiers as keys and StationInfo objects as values.
     */
    public Map<String, StationInfo> importStationData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(dataSource))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                processStationRecord(line);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading station data: " + e.getMessage(), e);
        }
        return stationDirectory;
    }

    /**
     * Processes a single line of the CSV file, creating a StationInfo object
     * and adding it to the station directory.
     *
     * @param record a single line of the CSV file representing a station record.
     */
    private void processStationRecord(String record) {
        String[] fields = record.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        if (fields.length < 7) return;

        int id = Integer.parseInt(fields[0].trim());
        String lineCode = fields[1].trim();
        String identifier = fields[3].trim();
        String label = fields[4].trim();
        Coordinates location = new Coordinates(
                Double.parseDouble(fields[5].trim()),
                Double.parseDouble(fields[6].trim())
        );

        StationInfo station = new StationInfo(id, lineCode, identifier, label, location);

        stationDirectory.put(identifier, station);
    }


    /**
     * Sets the data source file path.
     *
     * @param source the file path of the data source.
     */
    public void setDataSource(String source) {
        this.dataSource = source;
    }

    /**
     * Gets the current data source file path.
     *
     * @return the file path of the data source.
     */
    public String getDataSource() {
        return dataSource;
    }
}
