package ca.ucalgary.edu.ensf380.weather;

import ca.ucalgary.edu.ensf380.models.Article;
import ca.ucalgary.edu.ensf380.models.WeatherReport;
import ca.ucalgary.edu.ensf380.news.ArticleDisplay;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * The WeatherDisplay class is responsible for displaying the current time and
 * weather report in a JPanel. It also integrates with the ArticleDisplay to
 * show news articles.
 */
public class WeatherDisplay extends JPanel {

    /**
    * The JLabel component to display the current time.
    */
    private final JLabel timeLabel;

    /**
     * The JTextArea component to display the weather report.
     */
    private final JTextArea weatherReportArea;

    /**
     * Path to the weather report file.
     */
    private final String FILE_PATH = "./data/weather.txt";

    /**
     * City name for the weather report.
     */
    private String CITY_NAME = "Calgary";

    /**
     * Logger for logging errors and information.
     */
    private Logger logger = Logger.getLogger(WeatherDisplay.class.getName());


    /**
     * The ExecutorService to run tasks asynchronously.
     */
    private final ExecutorService executorService;

    /**
     * Constructs a WeatherDisplay panel with a list of articles and an
     * ArticleDisplay component.
     *
     * @param articleDisplay The ArticleDisplay component.
     * @param cityName       The city name for the weather report.
     */
    public WeatherDisplay(ArticleDisplay articleDisplay, String cityName) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.CITY_NAME = cityName;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(240, 240, 240));

        // Time panel
        JPanel timePanel = new JPanel();
        timePanel.setBackground(new Color(70, 130, 180));
        timePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setForeground(Color.WHITE);
        timePanel.add(timeLabel);

        // Weather panel
        JPanel weatherPanel = new JPanel(new BorderLayout());
        weatherPanel.setBackground(new Color(230, 230, 250));
        weatherPanel.setBorder(BorderFactory.createTitledBorder("Weather Report"));
        weatherReportArea = new JTextArea(5, 20);
        weatherReportArea.setEditable(false);
        weatherReportArea.setFont(new Font("Arial", Font.PLAIN, 14));
        weatherReportArea.setLineWrap(true);
        weatherReportArea.setWrapStyleWord(true);
        JScrollPane weatherScrollPane = new JScrollPane(weatherReportArea);
        weatherPanel.add(weatherScrollPane, BorderLayout.CENTER);

        // Add components to main panel
        add(timePanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(weatherPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(articleDisplay);

        // Set up timers
        new Timer(1000, e -> updateTime()).start();
        new Timer(60000, e -> fetchWeatherReport()).start();

        // Initial display
        updateTime();
        fetchWeatherReport();
    }

    /**
     * Updates the time label with the current time.
     */
    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdf.format(new Date());
        timeLabel.setText("Current Time: " + currentTime);
    }

    /**
     * Displays the weather report by reading it from a file.
     */
    private void displayWeatherReport() {
        try {
            List<String> weatherReport = getWeatherReport();
            weatherReportArea.setText(String.join("\n", weatherReport));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching weather report.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fetches the weather report asynchronously.
     */
    private void fetchWeatherReport() {
        executorService.submit(() -> {
            List<String> weatherReport = getWeatherReport();
            SwingUtilities.invokeLater(() -> weatherReportArea.setText(String.join("\n", weatherReport)));
        });
    }

    /**
     * Gets the weather report by executing a JAR file and reading the output file.
     *
     * @return A list of strings representing the weather report.
     */
    public List<String> getWeatherReport() {
        WeatherReport weatherReport = new WeatherReport();

        WeatherFetcher weatherFetcher = new WeatherFetcher();
        if (weatherFetcher.executeJar(CITY_NAME) == 0) { // success
            // Read the file
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                String line;
                while ((line = br.readLine()) != null) {
                    weatherReport.addWeatherData(line);
                }
            } catch (IOException e) {
                logger.severe("Error reading weather report file: " + e.getMessage());
            }
        } else {
            // Return dummy data if fetching fails
            weatherReport.addWeatherData("Weather report for city " + CITY_NAME);
            weatherReport.addWeatherData("Temperature: 25C");
            weatherReport.addWeatherData("Humidity: 50%");
        }

        return weatherReport.getWeatherData();
    }
}
