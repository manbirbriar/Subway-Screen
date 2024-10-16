package ca.ucalgary.edu.ensf380.database;

import ca.ucalgary.edu.ensf380.models.Advertisement;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * The AdvertisementDatabase class provides an abstract base class for database
 * operations related to advertisements.
 */
public abstract class AdvertisementDatabase {
    /**
     * Private Constructor of the AdvertisementDatabase.
     */
    AdvertisementDatabase() {}

    /**
     * The database connection.
     */
    protected Connection connection;

    /**
     * Logger instance for logging messages.
     */
    protected static final Logger logger = Logger.getLogger(AdvertisementDatabase.class.getName());

    /**
     * Fetches advertisements from the specified table in the database.
     *
     * @param tableName The name of the table from which to fetch advertisements.
     * @return A list of advertisements retrieved from the database.
     */
    public abstract List<Advertisement> fetchAdvertisements(String tableName);

    /**
     * Opens a connection to the database.
     */
    protected void openDatabaseConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            String url = "jdbc:mysql://localhost:3306/SubwayScreen";
            String username = "root";
            String password = "Monty@9904";

            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            logger.severe("Failed to connect to the database: " + e.getMessage());
        }
    }

    /**
     * Closes the connection to the database.
     */
    protected void closeDatabaseConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.severe("Failed to close the database connection: " + e.getMessage());
        }
    }
}
