package ca.ucalgary.edu.ensf380.test;

import org.junit.After;
import org.junit.Test;

import ca.ucalgary.edu.ensf380.weather.WeatherFetcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * The WeatherFetcherTest class contains the unit tests for the WeatherFetcher class.
 */
public class WeatherFetcherTest {

    /**
     * Path to the weather file.
     */
    private static final String WEATHER_FILE_PATH = "./data/weather.txt";
    private final Logger logger = Logger.getLogger(WeatherFetcherTest.class.getName());

    /**
     * Constructs a WeatherFetcherTest object.
     */
    public WeatherFetcherTest() {
    }

    /**
     * Test the weather data fetch functionality.
     *
     * @throws IOException If an I/O error occurs.
     */
    @Test
    public void testWeatherDataFetch() throws IOException {
        String cityName = "NewYork";
        WeatherFetcher weatherFetcher = new WeatherFetcher();
        int exitCode = weatherFetcher.executeJar(cityName);

        // Check if the JAR execution was successful
        assertEquals("JAR execution should be successful", 0, exitCode);

        // Check if the weather file was created
        File weatherFile = new File(WEATHER_FILE_PATH);
        assertTrue("Weather file should be created", weatherFile.exists());

        // Read the content of the weather file
        String content = new String(Files.readAllBytes(Paths.get(WEATHER_FILE_PATH)));

        // Check if the file is not empty
        assertFalse("Weather file should not be empty", content.isEmpty());

        // Check if the content contains the city name
        assertTrue("Weather data should contain the city name", content.contains(cityName));
    }

    /**
     * Test the weather data fetch functionality with an invalid city name.
     */
    @Test
    public void testWeatherDataFetchInvalidCity() {
        String invalidCityName = "InvalidCityName";
        WeatherFetcher weatherFetcher = new WeatherFetcher();
        weatherFetcher.executeJar(invalidCityName);

        // Check if the weather file was not created
        File weatherFile = new File(WEATHER_FILE_PATH);
        // Read the file and check if it contains 404
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(WEATHER_FILE_PATH)));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading weather file: " + e.getMessage());
        }

        // File shouldn't exist, if exist then it should contain the error 404
        if(weatherFile.exists()) {
            assertTrue("Weather data should contain 404 for invalid city name", content.contains("404"));
        } // File not exists assert true
        else {
            assertTrue("Weather file should not be created for invalid city name", true);
        }
    }

    /**
     * Test the weather data fetch functionality when the JAR file is missing.
     */
    @Test
    public void testWeatherDataFetchJarMissing() {
        String cityName = "New York";
        WeatherFetcher weatherFetcher = new WeatherFetcher() {
            @Override
            public int executeJar(String cityName) {
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "./exe/" + "InvalidWeatherData.jar", cityName);
                processBuilder.redirectErrorStream(true);

                try {
                    Process process = processBuilder.start();

                    // Capture and print the output from the JAR file
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }

                    // Capture and print any errors
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    while ((line = errorReader.readLine()) != null) {
                        System.err.println(line);
                    }

                    return process.waitFor();
                } catch (IOException | InterruptedException e) {
                    logger.log(Level.SEVERE, "Error executing JAR file: " + e.getMessage());
                }
                return 1;
            }
        };
        int exitCode = weatherFetcher.executeJar(cityName);

        // Check if the JAR execution failed
        assertNotEquals("JAR execution should fail if the JAR file is missing", 0, exitCode);

        // Check if the weather file was not created
        File weatherFile = new File(WEATHER_FILE_PATH);
        assertFalse("Weather file should not be created if the JAR file is missing", weatherFile.exists());
    }

    /**
     * Test the weather data fetch functionality when the weather file cannot be written.
     */
    @Test
    public void testWeatherDataFetchCanWriteEmptyFile() {
        String cityName = "New York";
        File weatherFile = new File(WEATHER_FILE_PATH);

        // Create a file with the same name to simulate the file write issue
        if (!weatherFile.exists()) {
            try {
                weatherFile.createNewFile();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error creating weather file: " + e.getMessage());
            }
        }

        WeatherFetcher weatherFetcher = new WeatherFetcher();
        int exitCode = weatherFetcher.executeJar(cityName);

        // Check if the JAR execution failed
        assertEquals("JAR execution should pass if the empty weather file can be written", 0, exitCode);

        // Cleanup the directory created for the test
        weatherFile.delete();
    }

    /**
     * Cleans up the weather file after each test.
     */
    @After
    public void cleanup() {
        // Delete the weather file after the test
        File weatherFile = new File(WEATHER_FILE_PATH);
        if (weatherFile.exists() && weatherFile.isFile()) {
            weatherFile.delete();
        }
    }
}
