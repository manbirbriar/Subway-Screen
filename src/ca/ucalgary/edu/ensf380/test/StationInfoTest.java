package ca.ucalgary.edu.ensf380.test;

import ca.ucalgary.edu.ensf380.models.StationInfo;
import ca.ucalgary.edu.ensf380.models.Coordinates;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link StationInfo} class.
 */
public class StationInfoTest {

    private StationInfo station;

    /**
     * Sets up the test environment before each test.
     * Initializes a {@link StationInfo} instance with sample data and a set of common stations.
     */
    @Before
    public void setUp() {
        Coordinates location = new Coordinates(29589.9197, 31378.7673);
        station = new StationInfo(1, "C", "CC1", "Dhoby Ghaut", location);
    }

    /**
     * Tests the constructor of the {@link StationInfo} class to ensure the object is created.
     */
    @Test
    public void testConstructor() {
        assertNotNull("Station object should not be null", station);
    }

    /**
     * Tests the {@link StationInfo#getStationId()} method to ensure it returns the correct ID.
     */
    @Test
    public void testGetStationId() {
        assertEquals("ID should be 1", 1, station.getStationId());
    }

    /**
     * Tests the {@link StationInfo#getLineCode()} method to ensure it returns the correct line code.
     */
    @Test
    public void testGetLineCode() {
        assertEquals("Line code should be C", "C", station.getLineCode());
    }

    /**
     * Tests the {@link StationInfo#getStationIdentifier()} method to ensure it returns the correct station identifier.
     */
    @Test
    public void testGetStationIdentifier() {
        assertEquals("Station identifier should be CC1", "CC1", station.getStationIdentifier());
    }

    /**
     * Tests the {@link StationInfo#getStationLabel()} method to ensure it returns the correct station label.
     */
    @Test
    public void testGetStationLabel() {
        assertEquals("Station label should be Dhoby Ghaut", "Dhoby Ghaut", station.getStationLabel());
    }

    /**
     * Tests the {@link StationInfo#getStationLabel()} method when the station label is empty.
     * Ensures that an empty station label is represented as a single space.
     */
    @Test
    public void testGetStationLabelEmpty() {
        Coordinates location = new Coordinates(29749.3429, 31190.4399);
        StationInfo emptyLabelStation = new StationInfo(2, "C", "CC2", "", location);
        assertEquals("Empty station label should return a space", " ", emptyLabelStation.getStationLabel());
    }

    /**
     * Tests the {@link StationInfo#getLocation()} method to ensure it returns the correct location coordinates.
     */
    @Test
    public void testGetLocation() {
        double latitude = 29589.9197;
        double longitude = 31378.7673;

        Coordinates expectedLocation = new Coordinates(latitude, longitude);
        // compare actual values to expected values
        assertEquals("Location should match", (int) expectedLocation.getLatitude(), (int) station.getLocation().getLatitude());
    }

}
