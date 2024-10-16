package ca.ucalgary.edu.ensf380.database;

import ca.ucalgary.edu.ensf380.models.Advertisement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The SimpleAdvertisementDatabase class provides a simple implementation for
 * fetching advertisements from a database.
 */
public class SimpleAdvertisementDatabase extends AdvertisementDatabase {

    /**
     * Constructs a SimpleAdvertisementDatabase object.
     */
    public SimpleAdvertisementDatabase() {
        super();
    }

    /**
     * Fetches advertisements from the specified table in the database.
     *
     * @param tableName The name of the table from which to fetch advertisements.
     * @return A list of advertisements retrieved from the database.
     */
    @Override
    public List<Advertisement> fetchAdvertisements(String tableName) {
        List<Advertisement> advertisements = new ArrayList<>();
        openDatabaseConnection();

        try {
            String query = "SELECT * FROM " + tableName;
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String text = resultSet.getString("description");
                    String mediaPath = resultSet.getString("media_path");
                    advertisements.add(new Advertisement(title, text, mediaPath));
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to fetch advertisements: " + e.getMessage());
        } finally {
            closeDatabaseConnection();
        }

        return advertisements;
    }
}
