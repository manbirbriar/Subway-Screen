package ca.ucalgary.edu.ensf380.test;

import ca.ucalgary.edu.ensf380.models.WeatherReport;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The WeatherReportTest class contains unit tests for the WeatherReport class.
 */
public class WeatherReportTest {

    /**
     * Constructs a WeatherReportTest object.
     */
    public WeatherReportTest() {
    }

    /**
     * Test the constructor of the WeatherReport class.
     */
    @Test
    public void testConstructor() {
        WeatherReport weatherReport = new WeatherReport();
        assertNotNull("WeatherReport object should not be null", weatherReport);
        assertNotNull("WeatherData list should not be null", weatherReport.getWeatherData());
        assertTrue("WeatherData list should be empty", weatherReport.getWeatherData().isEmpty());
    }

    /**
     * Test the setWeatherData method of the WeatherReport class.
     */
    @Test
    public void testSetWeatherData() {
        WeatherReport weatherReport = new WeatherReport();
        List<String> data = new ArrayList<>();
        data.add("Sunny");
        data.add("Rainy");

        weatherReport.setWeatherData(data);

        assertNotNull("WeatherData list should not be null", weatherReport.getWeatherData());
        assertEquals("WeatherData list should have 2 elements", 2, weatherReport.getWeatherData().size());
        assertEquals("First element should be 'Sunny'", "Sunny", weatherReport.getWeatherData().get(0));
        assertEquals("Second element should be 'Rainy'", "Rainy", weatherReport.getWeatherData().get(1));
    }

    /**
     * Test the getWeatherData method of the WeatherReport class.
     */
    @Test
    public void testGetWeatherData() {
        WeatherReport weatherReport = new WeatherReport();
        List<String> data = new ArrayList<>();
        data.add("Cloudy");
        data.add("Windy");

        weatherReport.setWeatherData(data);

        List<String> retrievedData = weatherReport.getWeatherData();

        assertNotNull("Retrieved WeatherData list should not be null", retrievedData);
        assertEquals("Retrieved WeatherData list should have 2 elements", 2, retrievedData.size());
        assertEquals("First element should be 'Cloudy'", "Cloudy", retrievedData.get(0));
        assertEquals("Second element should be 'Windy'", "Windy", retrievedData.get(1));
    }

    /**
     * Test the addWeatherData method of the WeatherReport class.
     */
    @Test
    public void testAddWeatherData() {
        WeatherReport weatherReport = new WeatherReport();
        weatherReport.addWeatherData("Stormy");

        assertNotNull("WeatherData list should not be null", weatherReport.getWeatherData());
        assertEquals("WeatherData list should have 1 element", 1, weatherReport.getWeatherData().size());
        assertEquals("First element should be 'Stormy'", "Stormy", weatherReport.getWeatherData().getFirst());

        weatherReport.addWeatherData("Snowy");

        assertEquals("WeatherData list should have 2 elements", 2, weatherReport.getWeatherData().size());
        assertEquals("Second element should be 'Snowy'", "Snowy", weatherReport.getWeatherData().get(1));
    }
}
