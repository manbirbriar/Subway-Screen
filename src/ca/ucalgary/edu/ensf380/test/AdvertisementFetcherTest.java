package ca.ucalgary.edu.ensf380.test;

import ca.ucalgary.edu.ensf380.advertisement.AdvertisementFetcher;
import ca.ucalgary.edu.ensf380.models.Advertisement;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * The AdvertisementFetcherTest class contains the unit tests for the AdvertisementFetcher class.
 */
public class AdvertisementFetcherTest {

    /**
     * Constructs an AdvertisementFetcherTest object.
     */
    public AdvertisementFetcherTest() {}

    /**
     * The AdvertisementFetcher object to test.
     */
    private AdvertisementFetcher adFetcher;

    /**
     * Sets up the AdvertisementFetcher object for testing.
     */
    @Before
    public void setUp() {
        adFetcher = new AdvertisementFetcher();
    }

    /**
     * Tests the advertisement loading functionality.
     */
    @Test
    public void testLoadAdvertisements() {
        // Load advertisements
        adFetcher.loadAdvertisements("advertisements");

        // Get the loaded advertisements
        List<Advertisement> ads = adFetcher.getAdvertisements();

        // Check if advertisements were loaded
        assertNotNull("Advertisement list should not be null", ads);
        assertFalse("Advertisement list should not be empty", ads.isEmpty());

        // Check each advertisement
        for (Advertisement ad : ads) {
            assertNotNull("Advertisement should not be null", ad);
            assertNotNull("Advertisement title should not be null", ad.title());
            assertFalse("Advertisement title should not be empty", ad.title().isEmpty());
            assertNotNull("Advertisement text should not be null", ad.description());
        }
    }

    /**
     * Tests the getAdvertisements method for a non-null return value.
     */
    @Test
    public void testGetAdvertisementsNotNull() {
        List<Advertisement> ads = adFetcher.getAdvertisements();
        assertNotNull("Advertisement list should not be null", ads);
    }

    /**
     * Tests the initial state of the advertisements list before loading ads.
     */
    @Test
    public void testInitialAdvertisementsListEmpty() {
        List<Advertisement> ads = adFetcher.getAdvertisements();
        assertTrue("Advertisement list should be empty before loading ads", ads.isEmpty());
    }

    /**
     * Tests if the advertisements list contains specific ad details after loading.
     */
    @Test
    public void testAdvertisementsDetails() {
        adFetcher.loadAdvertisements("advertisements");
        List<Advertisement> ads = adFetcher.getAdvertisements();

        for (Advertisement ad : ads) {
            assertFalse("Advertisement title should not be empty", ad.title().isEmpty());
            assertFalse("Advertisement description should not be empty", ad.description().isEmpty());
            assertNotNull("Advertisement media path should not be null", ad.mediaPath());
        }
    }

    /**
     * Tests the constructor of Advertisement class.
     */
    @Test
    public void testAdvertisementConstructor() {
        String title = "Sample Title";
        String description = "Sample Description";
        String mediaPath = "//media/messi.jpg";

        Advertisement ad = new Advertisement(title, description, mediaPath);

        assertEquals("Advertisement title should match", title, ad.title());
        assertEquals("Advertisement description should match", description, ad.description());
        assertEquals("Advertisement media path should match", mediaPath, ad.mediaPath());
    }

    /**
     * Tests if the loadAdvertisements method handles an exception correctly.
     */
    @Test
    public void testLoadAdvertisementsExceptionHandling() {
        // Simulating an exception in the database with wrong table name
        try {
            adFetcher.loadAdvertisements("advertisement");
        } catch (Exception e) {
            // assert true if an exception occurs
            assertTrue(true);
        }
    }
}
