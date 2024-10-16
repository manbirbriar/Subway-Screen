package ca.ucalgary.edu.ensf380.helper;

import javax.swing.*;
import java.awt.*;

/**
 * The ImageLoader class is responsible for loading and resizing images.
 */
public class ImageLoader {

    /**
     * Constructs an ImageLoader.
     */
    public ImageLoader() {}

    /**
     * Loads an image from the specified path and resizes it to the given width and height.
     *
     * @param path   the path to the image file
     * @param width  the desired width of the image
     * @param height the desired height of the image
     * @return a resized ImageIcon
     */
    public ImageIcon loadImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}