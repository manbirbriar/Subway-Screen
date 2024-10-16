package ca.ucalgary.edu.ensf380.advertisement;

import ca.ucalgary.edu.ensf380.helper.ImageLoader;
import ca.ucalgary.edu.ensf380.models.Advertisement;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The AdvertisementDisplay class is responsible for displaying a series of advertisements and a subway map on a JPanel.
 */
public class AdvertisementDisplay extends JPanel {

    /**
     * Constructs an AdvertisementDisplay with a list of advertisements.
     *
     * @param adList the list of advertisements to display
     */
    public AdvertisementDisplay(List<Advertisement> adList) {
        setLayout(new BorderLayout());

        ContentPanel contentPanel = new ContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        DisplayManager displayManager = new DisplayManager(adList, contentPanel);
        displayManager.startRotation();
    }
}

/**
 * The ContentPanel class is a custom JPanel that displays the content of advertisements.
 */
class ContentPanel extends JPanel {
    private final JLabel titleLabel;
    private final JLabel descriptionLabel;
    private final JLabel imageLabel;

    /**
     * Constructs a ContentPanel.
     */
    public ContentPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.LIGHT_GRAY);

        titleLabel = new JLabel();
        descriptionLabel = new JLabel();
        imageLabel = new JLabel();

        add(titleLabel);
        add(descriptionLabel);
        add(imageLabel);
    }

    /**
     * Updates the content of the panel with new advertisement details.
     *
     * @param title       the title of the advertisement
     * @param description the description of the advertisement
     * @param image       the image associated with the advertisement
     */
    public void updateContent(String title, String description, ImageIcon image) {
        titleLabel.setText(title);
        descriptionLabel.setText(description);
        imageLabel.setIcon(image);
        revalidate();
        repaint();
    }
}

/**
 * The DisplayManager class manages the rotation of advertisements and the display of the subway map.
 */
class DisplayManager {
    private final List<Advertisement> adList;
    private final ContentPanel contentPanel;
    private int currentAdIndex = 0;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ImageLoader imageLoader = new ImageLoader();

    /**
     * Constructs a DisplayManager with a list of advertisements and a content panel.
     *
     * @param adList       the list of advertisements to display
     * @param contentPanel the panel to display the advertisement content
     */
    public DisplayManager(List<Advertisement> adList, ContentPanel contentPanel) {
        this.adList = adList;
        this.contentPanel = contentPanel;
    }

    /**
     * Starts the rotation of advertisements and the display of the subway map.
     */
    public void startRotation() {
        scheduler.schedule(this::showNextAd, 0, TimeUnit.SECONDS);
    }

    /**
     * Displays the next advertisement in the list.
     */
    private void showNextAd() {
        if (!adList.isEmpty()) {
            Advertisement ad = adList.get(currentAdIndex);
            ImageIcon adImage = imageLoader.loadImage(ad.mediaPath(), 600, 440);
            contentPanel.updateContent("Title: " + ad.title(),"Description: " + ad.description(), adImage);

            currentAdIndex = (currentAdIndex + 1) % adList.size();

            scheduler.schedule(this::showMap, 5, TimeUnit.SECONDS);
        }
    }

    /**
     * Displays the subway map.
     */
    private void showMap() {
        ImageIcon mapImage = imageLoader.loadImage("./data/trainmap.png", 600, 440);
        contentPanel.updateContent("Subway Map", "The city has three subway lines (Red, Green, and Blue) with approximately 120 stations.", mapImage);

        scheduler.schedule(this::showNextAd, 5, TimeUnit.SECONDS);
    }
}
