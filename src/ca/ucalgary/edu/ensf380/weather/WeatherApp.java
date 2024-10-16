package ca.ucalgary.edu.ensf380.weather;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * A weather application that fetches and stores weather data for a given city.
 */
public class WeatherApp {
    private static final Pattern CITY_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");

    /**
     * The main entry point of the application.
     * @param args Command line arguments. Expects a single argument: the name of the city.
     */
    public static void main(String[] args) {
        if (args.length != 1 || !CITY_PATTERN.matcher(args[0]).matches()) {
            System.out.println("Please provide a valid city name as a command-line argument.");
            return;
        }

        String city = args[0];
        try {
            WeatherData weatherData = WeatherFetcher.fetchWeatherData(city);
            String currentDirectory = System.getProperty("user.dir");
            String filePath = currentDirectory + File.separator + "data" + File.separator + "weather.txt";

            File dataDir = new File(currentDirectory, "data");
            if (!dataDir.exists()) {
                boolean mkdirDone = dataDir.mkdir();
                if (!mkdirDone) {
                    System.err.println("Failed to create data directory.");
                    return;
                }
            }

            WeatherStorage.storeWeatherData(weatherData, filePath);
            System.out.println("Weather data for " + city + " has been stored in " + filePath);
        } catch (IOException e) {
            System.err.println("Error fetching or storing weather data: " + e.getMessage());
        }
    }

    /**
     * Utility class for storing weather data.
     */
    static class WeatherStorage {
        private static final Pattern LINE_PATTERN = Pattern.compile("^(.*)$", Pattern.MULTILINE);
        private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+$");

        /**
         * Stores the weather data to a file.
         * @param weatherData The weather data to store.
         * @param filePath The path of the file to store the data in.
         * @throws IOException If an I/O error occurs.
         */
        public static void storeWeatherData(WeatherData weatherData, String filePath) throws IOException {
            Path path = Paths.get(filePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
                for (WeatherLine line : weatherData.getLines()) {
                    Matcher matcher = LINE_PATTERN.matcher(line.getContent());
                    if (matcher.find()) {
                        String trimmedLine = WHITESPACE_PATTERN.matcher(matcher.group(1)).replaceAll("");
                        writer.write(trimmedLine);
                        writer.newLine();
                    }
                }
            }
        }
    }

    /**
     * Represents weather data for a specific city.
     */
    static class WeatherData {
        private final String city;
        private final List<WeatherLine> lines;

        /**
         * Constructs a new WeatherData object.
         * @param city The name of the city.
         */
        public WeatherData(String city) {
            this.city = city;
            this.lines = new ArrayList<>();
        }

        /**
         * Adds a line of weather data.
         * @param line The line to add.
         */
        public void addLine(WeatherLine line) {
            lines.add(line);
        }

        /**
         * Gets the name of the city.
         * @return The city name.
         */
        public String getCity() {
            return city;
        }

        /**
         * Gets the lines of weather data.
         * @return The list of weather lines.
         */
        public List<WeatherLine> getLines() {
            return lines;
        }
    }

    /**
     * Represents a single line of weather data.
     */
    static class WeatherLine {
        private final String content;
        private static final Pattern ANSI_ESCAPE = Pattern.compile("\\e\\[[0-9;]*[mGKH]");

        /**
         * Constructs a new WeatherLine object.
         * @param content The content of the line.
         */
        public WeatherLine(String content) {
            this.content = ANSI_ESCAPE.matcher(content).replaceAll("");
        }

        /**
         * Gets the content of the line.
         * @return The line content.
         */
        public String getContent() {
            return content;
        }
    }

    /**
     * Utility class for fetching weather data.
     */
    static class WeatherFetcher {
        private static final String BASE_URL = "https://wttr.in/";
        private static final Pattern PRE_PATTERN = Pattern.compile("<pre>(.*?)</pre>", Pattern.DOTALL);
        private static final Pattern LINE_PATTERN = Pattern.compile("^(.*)$", Pattern.MULTILINE);
        private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");

        /**
         * Fetches weather data for a given city.
         * @param city The name of the city.
         * @return A WeatherData object containing the fetched data.
         * @throws IOException If an I/O error occurs during fetching.
         */
        public static WeatherData fetchWeatherData(String city) throws IOException {
            String url = BASE_URL + city + "?0QT";
            String html = fetchHtml(url);
            WeatherData weatherData = new WeatherData(city);

            Matcher preMatcher = PRE_PATTERN.matcher(html);
            if (preMatcher.find()) {
                List<String> lines = new ArrayList<>();
                lines.add("Weather report: " + city);
                lines.add(""); // Add an empty line after the city name

                String preContent = HTML_TAG_PATTERN.matcher(preMatcher.group(1)).replaceAll("");
                Matcher lineMatcher = LINE_PATTERN.matcher(preContent);
                while (lineMatcher.find()) {
                    lines.add(lineMatcher.group(1));
                }

                for (String line : lines) {
                    weatherData.addLine(new WeatherLine(line));
                }
            }

            return weatherData;
        }

        /**
         * Fetches HTML content from a given URL.
         * @param url The URL to fetch from.
         * @return The fetched HTML content as a string.
         * @throws IOException If an I/O error occurs during fetching.
         */
        private static String fetchHtml(String url) throws IOException {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new java.net.URL(url).openStream()))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                return content.toString();
            }
        }
    }
}