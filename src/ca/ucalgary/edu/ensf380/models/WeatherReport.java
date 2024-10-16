package ca.ucalgary.edu.ensf380.models;

import java.util.ArrayList;
import java.util.List;

/**
 * The WeatherReport class represents a report containing weather data.
 */
public class WeatherReport {
    private List<String> weatherData;

    /**
     * Constructs a new WeatherReport object with an empty list of weather data.
     */
    public WeatherReport() {
        this.weatherData = new ArrayList<>();
    }

    /**
     * Sets the weather data for this WeatherReport.
     *
     * @param weatherData a list of weather data strings to set
     */
    public void setWeatherData(List<String> weatherData) {
        this.weatherData = weatherData;
    }

    /**
     * Gets the weather data for this WeatherReport.
     *
     * @return a list of weather data strings
     */
    public List<String> getWeatherData() {
        return weatherData;
    }

    /**
     * Adds a weather data string to the list of weather data.
     *
     * @param data the weather data string to add
     */
    public void addWeatherData(String data) {
        weatherData.add(data);
    }
}
