package ca.ucalgary.edu.ensf380.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * The WeatherFetcher class is responsible for executing an external JAR file
 * to fetch weather data.
 */
public class WeatherFetcher {
    private Logger logger = Logger.getLogger(WeatherFetcher.class.getName());

    /**
     * Private constructor to prevent instantiation of the class.
     */
    public WeatherFetcher() {
    }

    /**
     * Executes the WeatherData JAR file with the specified city name.
     *
     * @param cityName The name of the city for which to fetch weather data.
     * @return The exit code of the JAR execution (0 if successful).
     */
    public int executeJar(String cityName) {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "./exe/" + "WeatherData.jar", cityName);

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

            // if errorReader Contains 404 error, return 1
            if (errorReader.toString().contains("404")) {
                return 1;
            }

            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.severe("Error executing JAR file: " + e.getMessage());
        }
        return 1;
    }
}
