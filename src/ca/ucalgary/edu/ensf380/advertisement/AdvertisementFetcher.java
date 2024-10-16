package ca.ucalgary.edu.ensf380.advertisement;

import ca.ucalgary.edu.ensf380.database.SimpleAdvertisementDatabase;
import ca.ucalgary.edu.ensf380.models.Advertisement;

import java.util.ArrayList;
import java.util.List;

/**
 * The AdvertisementFetcher class is responsible for fetching advertisements
 * from a database and storing them in a list.
 */
public class AdvertisementFetcher {
    /**
     * List of advertisements to be displayed.
     */
    private List<Advertisement> advertisementsMedia;

    /**
     * Instance of SimpleAdvertisementDatabase for database operations.
     */
    private final SimpleAdvertisementDatabase database;

    /**
     * Constructs an AdvertisementFetcher object.
     */
    public AdvertisementFetcher() {
        advertisementsMedia = new ArrayList<>();
        database = new SimpleAdvertisementDatabase();
    }

    /**
     * Loads advertisements from the specified table in the database.
     *
     * @param tableName The name of the table from which to load advertisements.
     */
    public void loadAdvertisements(String tableName) {
        advertisementsMedia = database.fetchAdvertisements(tableName);
    }

    /**
     * Retrieves the list of fetched advertisements.
     *
     * @return The list of fetched advertisements.
     */
    public List<Advertisement> getAdvertisements() {
        return advertisementsMedia;
    }
}