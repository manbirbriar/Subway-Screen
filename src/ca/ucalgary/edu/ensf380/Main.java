package ca.ucalgary.edu.ensf380;

import ca.ucalgary.edu.ensf380.advertisement.AdvertisementDisplay;
import ca.ucalgary.edu.ensf380.advertisement.AdvertisementFetcher;
import ca.ucalgary.edu.ensf380.models.Advertisement;
import ca.ucalgary.edu.ensf380.models.Article;
import ca.ucalgary.edu.ensf380.models.TrainStatus;
import ca.ucalgary.edu.ensf380.news.ArticleDisplay;
import ca.ucalgary.edu.ensf380.news.ArticleRequester;
import ca.ucalgary.edu.ensf380.train.TrainDataCollector;
import ca.ucalgary.edu.ensf380.train.TrainMapVisualizer;
import ca.ucalgary.edu.ensf380.tts.StationAnnouncer;
import ca.ucalgary.edu.ensf380.weather.WeatherDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * The Main class is the entry point for the Subway Screen application.
 * It displays advertisements, articles, weather information, and train station information.
 */
public class Main extends JFrame {
    /**
     * Logger instance for logging messages.
     */
    private final Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * List of advertisements to be displayed.
     */
    private List<Advertisement> advertisements;

    /**
     * List of news articles to be displayed.
     */
    private List<Article> articles;

    /**
     * Index of the currently selected train.
     */
    private int currentTrain = 0;

    /**
     * Name of the city for the weather report.
     */
    private String cityName = "Calgary";
    /**
     * Query for the news articles.
     */
    private String newsQuery = "Calgary";

    /**
     * Process instance for managing external processes.
     */
    private Process process;

    /**
     * Executor service for handling asynchronous tasks.
     */
    private final ExecutorService trainExecutor = Executors.newSingleThreadExecutor();

    /**
     * Executor service for handling text-to-speech tasks.
     */
    private final ExecutorService voiceExecutor = Executors.newSingleThreadExecutor();

    /**
     * Array of Train objects representing the trains' information.
     */
    private TrainStatus[] trains;

    /**
     * Component for displaying advertisements.
     */
    private AdvertisementDisplay advertisementDisplay;

    /**
     * Component for displaying news articles.
     */
    private ArticleDisplay articleDisplay;

    /**
     * Component for displaying weather information.
     */
    private WeatherDisplay weatherReportDisplay;

    /**
     * Panel for displaying station information.
     */
    private JPanel stationPanel;

    /**
     * Label displaying the previous station's name.
     */
    private JLabel prevStationLabel;

    /**
     * Label displaying the current station's name.
     */
    private JLabel currentStationLabel;

    /**
     * Array of labels displaying the names of the next three stations.
     */
    private JLabel[] nextStationLabels;

    /**
     * Station Announcer for handling text-to-speech announcements of stations.
     */
    private final StationAnnouncer stationAnnouncer = new StationAnnouncer();

    /**
     * TrainMapCreator for generating visual representations of train maps.
     */
    private final TrainMapVisualizer trainMapCreator = new TrainMapVisualizer();

    /**
     * Constructs a Main object.
     */
    public Main() {
        super();
    }

    /**
     * The main method is the entry point of the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().startApplication(args));
    }

    /**
     * Starts the application by handling command line arguments and initializing components.
     *
     * @param args Command line arguments.
     */
    private void startApplication(String[] args) {
        handleCommandLineArgs(args);

        AdvertisementFetcher advertisementFetcher = new AdvertisementFetcher();
        advertisementFetcher.loadAdvertisements("advertisements");
        advertisements = advertisementFetcher.getAdvertisements();

        ArticleRequester articleRequester = new ArticleRequester();
        articleRequester.fetchNewsAsync(newsQuery, "relevancy", 100, fetchedArticles -> {
            articles = fetchedArticles;
            SwingUtilities.invokeLater(this::showInitialWindow);
        });
    }

    /**
     * Handles command line arguments to set the current train.
     *
     * @param args Command line arguments.
     */
    private void handleCommandLineArgs(String[] args) {
        if (args.length >= 1) {
            try {
                int trainNum = Integer.parseInt(args[0]);
                if (trainNum >= 1 && trainNum <= 12) {
                    currentTrain = trainNum - 1;
                    System.out.println("Train number provided: " + (currentTrain + 1));
                } else {
                    System.err.println("Invalid train number provided. Using default value.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid train number format. Using default value.");
            }
        }

        if (args.length >= 2) {
            cityName = args[1];
            System.out.println("City name provided: " + cityName);
        }

        if (args.length >= 3) {
            newsQuery = args[2];
            System.out.println("News query provided: " + newsQuery);
        }

        // If no arguments were provided, use default values
        if (args.length == 0) {
            System.out.println("No command line arguments provided. Using default values.");
            System.out.println("Train number: " + (currentTrain + 1));
            System.out.println("City name: " + cityName);
            System.out.println("News query: " + newsQuery);
        }
    }
    /**
     * Starts the main application window.
     */
    private void startMainApplication() {
        setTitle("Subway Screen");
        setSize(900, 570);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                stopProcess();
                // print exit msg
                System.out.println("Exiting Subway Screen");
            }
        });

        initializeDisplays();
        addComponentsToFrame();

        setLocationRelativeTo(null);
        setVisible(true);

        startProcess();
    }

    /**
     * Initializes the various displays used in the application.
     */
    private void initializeDisplays() {
        advertisementDisplay = new AdvertisementDisplay(advertisements);
        articleDisplay = new ArticleDisplay(articles);
        weatherReportDisplay = new WeatherDisplay(articleDisplay, cityName);
        stationPanel = createStationPanel();
    }

    /**
     * Adds the components to the main application frame.
     */
    private void addComponentsToFrame() {
        add(advertisementDisplay, BorderLayout.WEST);
        add(weatherReportDisplay, BorderLayout.CENTER);
        add(stationPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the station panel to display station information.
     *
     * @return The station panel.
     */
    private JPanel createStationPanel() {
        stationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        stationPanel.setPreferredSize(new Dimension(900, 50));

        prevStationLabel = new JLabel("---");
        currentStationLabel = new JLabel("---");
        nextStationLabels = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            nextStationLabels[i] = new JLabel("---");
        }

        Font stationFont = new Font("Arial", Font.BOLD, 12);

        prevStationLabel.setForeground(Color.RED);
        prevStationLabel.setFont(stationFont);
        currentStationLabel.setForeground(Color.WHITE);
        currentStationLabel.setFont(stationFont);

        for (JLabel label : nextStationLabels) {
            label.setForeground(Color.GREEN);
            label.setFont(stationFont);
        }

        stationPanel.add(prevStationLabel);
        // Create a list of arrow labels to separate the station names
        List<JLabel> arrowLabels = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            JLabel arrowLabel = new JLabel(" → ");
            arrowLabel.setForeground(Color.WHITE);
            arrowLabels.add(arrowLabel);
        }

        int i = 0;
        stationPanel.add(arrowLabels.get(i++));
        stationPanel.add(currentStationLabel);

        for (JLabel label : nextStationLabels) {
            stationPanel.add(arrowLabels.get(i++));
            stationPanel.add(label);
        }

        stationPanel.setBackground(Color.DARK_GRAY);
        return stationPanel;
    }

    /**
     * Displays the initial window before starting the main application.
     */
    private void showInitialWindow() {
        JFrame initialFrame = new JFrame("Subway Screen");
        initialFrame.setSize(300, 150);
        initialFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialFrame.setLayout(new GridBagLayout());

        JButton startButton = new JButton("Start");
        JButton exitButton = new JButton("Exit");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        initialFrame.add(startButton, gbc);
        initialFrame.add(exitButton, gbc);

        startButton.addActionListener(e -> {
            initialFrame.dispose();
            startMainApplication();
        });

        exitButton.addActionListener(e -> System.exit(0));

        initialFrame.setLocationRelativeTo(null);
        initialFrame.setVisible(true);
    }

    /**
     * Starts the process to run the Subway Simulator and handles its output.
     */
    private void startProcess() {
        if (process == null) {
            try {
                ProcessBuilder builder = new ProcessBuilder("java", "-jar", "./exe/SubwaySimulator.jar", "--in",
                        "./data/subway.csv", "--out", "./out");
                builder.redirectErrorStream(true);
                process = builder.start();

                trainExecutor.execute(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        int i = 0;

                        // Read the output of the process and update train information every 4 lines
                        while (reader.readLine() != null) {
                            i++;
                            if (4 == i) {
                                updateTrainInformation();
                                i = 0;
                            }
                        }
                    } catch (IOException e) {
                        logger.severe("Error reading process output: " + e.getMessage());
                    } catch (TrainDataCollector.TrainDataException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                logger.severe("Error starting process: " + e.getMessage());
            }
        }
    }

    /**
     * Updates train information and refreshes the map and station display.
     */
    private void updateTrainInformation() throws TrainDataCollector.TrainDataException, IOException {
        TrainDataCollector trainDataCollector = new TrainDataCollector();
        final int maxRetries = 5;
        int attempts = 0;

        while (attempts < maxRetries) {
            trains = trainDataCollector.collectTrainData();
            if (trains.length > 0) {
                break;
            } else {
                attempts++;
                try {
                    Thread.sleep(1000); // Delay before retrying
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (trains.length == 0) {
            logger.severe("Failed to read train data after multiple attempts.");
            return;
        }

        ArrayList<Integer> xCoordinates = new ArrayList<>();
        ArrayList<Integer> yCoordinates = new ArrayList<>();

        for (TrainStatus train : trains) {
            xCoordinates.add(train.getCurrentStationX());
            yCoordinates.add(train.getCurrentStationY());
        }
        trainMapCreator.renderTrainMap(xCoordinates, yCoordinates, currentTrain);

        if (currentTrain >= 0 && currentTrain <= 11 && trains.length > 0) {
            SwingUtilities.invokeLater(() -> {
                System.out.println("Updating train information for train " + (currentTrain + 1));
                System.out.println("Total trains: " + trains.length);
                TrainStatus currentTrainInfo = trains[currentTrain];
                prevStationLabel.setText(currentTrainInfo.getPreviousStationName());
                currentStationLabel.setText(currentTrainInfo.getCurrentStationName());
                for (int j = 0; j < 3; j++) {
                    nextStationLabels[j].setText(currentTrainInfo.getNextStationName(j));
                }
                stationPanel.revalidate();
                stationPanel.repaint();

                voiceExecutor.execute(() -> stationAnnouncer.announceNextStation("Next Stop" + currentTrainInfo.getNextStationName(0)));
            });
        }
    }

    /**
     * Stops the running process and releases resources.
     */
    private void stopProcess() {
        if (process != null) {
            process.destroy();
            process = null;
        }
    }
}
