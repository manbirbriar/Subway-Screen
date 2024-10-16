package ca.ucalgary.edu.ensf380.test;

import ca.ucalgary.edu.ensf380.models.StationInfo;
import ca.ucalgary.edu.ensf380.train.StationDataImporter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for the StationDataImporter class.
 */
public class StationDataImporterTest {

    private StationDataImporter stationDataImporter;

    /**
     * A temporary folder for creating temporary files during testing.
     */
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Sets up the test environment before each test.
     */
    @Before
    public void setUp() {
        stationDataImporter = new StationDataImporter();
    }

    /**
     * Tests importing station data from a valid CSV file.
     *
     * @throws IOException if an I/O error occurs while creating the temporary CSV file
     */
    @Test
    public void testImportStationData_ValidFile() throws IOException {
        // Create a temporary CSV file with test data
        File csvFile = createTempCsvFile("id,line_code,station_number,station_code,station_name,x_coordinate,y_coordinate,common_stations\n" +
                "1,A,01,A01,Station A,10,20,\"B01, C01\"\n" +
                "2,B,02,B02,Station B,30,40,A01\n");

        // Set the file path in StationDataImporter
        stationDataImporter.setDataSource(csvFile.getAbsolutePath());

        Map<String, StationInfo> result = stationDataImporter.importStationData();

        assertNotNull(result);
        assertEquals(2, result.size());

        StationInfo stationA = result.get("A01");
        assertNotNull(stationA);
        assertEquals(1, stationA.getStationId());
        assertEquals("A", stationA.getLineCode());
        assertEquals("A01", stationA.getStationIdentifier());
        assertEquals("Station A", stationA.getStationLabel());
        assertEquals(10, stationA.getLocation().getLatitude(), 0);
        assertEquals(20, stationA.getLocation().getLongitude(), 0);

        StationInfo stationB = result.get("B02");
        assertNotNull(stationB);
        assertEquals(2, stationB.getStationId());
        assertEquals("B", stationB.getLineCode());
        assertEquals("B02", stationB.getStationIdentifier());
        assertEquals("Station B", stationB.getStationLabel());
        assertEquals(30, stationB.getLocation().getLatitude(), 0);
        assertEquals(40, stationB.getLocation().getLongitude(), 0);
    }

    /**
     * Tests importing station data from an empty CSV file.
     *
     * @throws IOException if an I/O error occurs while creating the temporary CSV file
     */
    @Test
    public void testImportStationData_EmptyFile() throws IOException {
        // Create a temporary CSV file with only headers
        File csvFile = createTempCsvFile("id,line_code,station_number,station_code,station_name,x_coordinate,y_coordinate,common_stations\n");

        stationDataImporter.setDataSource(csvFile.getAbsolutePath());

        Map<String, StationInfo> result = stationDataImporter.importStationData();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests importing station data from an invalid file path.
     */
    @Test
    public void testImportStationData_InvalidFile() {
        stationDataImporter.setDataSource("non_existent_file.csv");

        Map<String, StationInfo> result = stationDataImporter.importStationData();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Creates a temporary CSV file with the specified content.
     *
     * @param content the content to write to the CSV file
     * @return the created temporary CSV file
     * @throws IOException if an I/O error occurs while creating the temporary CSV file
     */
    private File createTempCsvFile(String content) throws IOException {
        File csvFile = tempFolder.newFile("test_stations.csv");
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write(content);
        }
        return csvFile;
    }
}
