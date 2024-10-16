package ca.ucalgary.edu.ensf380.train;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

/**
 * The TrainMapVisualizer class provides functionality to generate visual representations
 * of train positions on a map. It loads a base map image, overlays train positions,
 * and saves the resulting image.
 */
public class TrainMapVisualizer {

	private static final Logger LOGGER = Logger.getLogger(TrainMapVisualizer.class.getName());
	private static final String IMAGE_FORMAT = "png";
	private static final int TRAIN_MARKER_SIZE = 24;
	private static final double X_SCALE_FACTOR = 2.65;
	private static final double Y_SCALE_FACTOR = 1.52;

	private final String baseMapPath;
	private final String outputMapPath;
	private final String trainImagePath;
	private final String trainRedImagePath;

	/**
	 * Constructs a TrainMapVisualizer with default file paths.
	 * The base map is expected to be in the 'data' directory with the name 'Trains.png'.
	 * The output map will be saved in the 'data' directory as 'trainmap.png'.
	 */
	public TrainMapVisualizer() {
		String basePath = System.getProperty("user.dir");
		this.baseMapPath = Paths.get(basePath, "data", "Trains.png").toString();
		this.outputMapPath = Paths.get("data", "trainmap.png").toString();
		this.trainImagePath = Paths.get(basePath, "data", "train.jpg").toString();
		this.trainRedImagePath = Paths.get(basePath, "data", "trainRed.jpg").toString();
	}

	/**
	 * Renders an image depicting train positions based on provided coordinates.
	 * This method orchestrates the entire process of loading the base map,
	 * drawing train positions, and saving the final image.
	 *
	 * @param xPositions    List of x-coordinates for train positions
	 * @param yPositions    List of y-coordinates for train positions
	 * @param focusedTrain  Index of the train to highlight (will be drawn in orange)
	 */
	public void renderTrainMap(List<Integer> xPositions, List<Integer> yPositions, int focusedTrain) {
		try {
			BufferedImage baseMap = loadBaseMap();
			BufferedImage trainMap = createTrainMap(baseMap, xPositions, yPositions, focusedTrain);
			saveTrainMap(trainMap);
		} catch (IOException e) {
			LOGGER.severe("Failed to process train map: " + e.getMessage());
		}
	}

	/**
	 * Loads the base map image from the file system.
	 *
	 * @return BufferedImage representing the base map
	 * @throws IOException if there's an error reading the image file
	 */
	private BufferedImage loadBaseMap() throws IOException {
		return ImageIO.read(new File(baseMapPath));
	}

	/**
	 * Creates a new image with train positions overlaid on the base map.
	 *
	 * @param baseMap       The original map image
	 * @param xPositions    List of x-coordinates for train positions
	 * @param yPositions    List of y-coordinates for train positions
	 * @param focusedTrain  Index of the train to highlight
	 * @return BufferedImage with train positions drawn on it
	 */
	private BufferedImage createTrainMap(BufferedImage baseMap, List<Integer> xPositions, List<Integer> yPositions, int focusedTrain) {
		int width = baseMap.getWidth();
		int height = baseMap.getHeight();
		BufferedImage trainMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = trainMap.createGraphics();
		try {
			g2d.setBackground(Color.WHITE);
			g2d.clearRect(0, 0, width, height);
			g2d.drawImage(baseMap, 0, 0, null);

			drawTrainPositions(g2d, xPositions, yPositions, focusedTrain);
		} finally {
			g2d.dispose();
		}

		return trainMap;
	}

	/**
	 * Draws train positions on the given Graphics2D object.
	 * The focused train is drawn in orange, while others are drawn in black.
	 *
	 * @param g2d           Graphics2D object to draw on
	 * @param xPositions    List of x-coordinates for train positions
	 * @param yPositions    List of y-coordinates for train positions
	 * @param focusedTrain  Index of the train to highlight
	 */
	private void drawTrainPositions(Graphics2D g2d, List<Integer> xPositions, List<Integer> yPositions, int focusedTrain) {
		try {
			BufferedImage trainImage = ImageIO.read(new File(trainImagePath));
			BufferedImage trainRedImage = ImageIO.read(new File(trainRedImagePath));
			BufferedImage resizedTrainImage = resizeImage(trainImage, TRAIN_MARKER_SIZE, TRAIN_MARKER_SIZE);
			BufferedImage resizedTrainRedImage = resizeImage(trainRedImage, TRAIN_MARKER_SIZE, TRAIN_MARKER_SIZE);

			for (int i = 0; i < xPositions.size(); i++) {
				int x = (int) (xPositions.get(i) / X_SCALE_FACTOR);
				int y = (int) (yPositions.get(i) / Y_SCALE_FACTOR);

				if (i == focusedTrain) {
					g2d.drawImage(resizedTrainRedImage, x, y, null);
				} else {
					g2d.drawImage(resizedTrainImage, x, y, null);
				}
			}
		} catch (IOException e) {
			LOGGER.severe("Failed to load train images: " + e.getMessage());
		}
	}
	
	private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
		Image tmp = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return resized;
	}

	/**
	 * Saves the generated train map image to the file system.
	 *
	 * @param trainMap BufferedImage to be saved
	 * @throws IOException if there's an error writing the image file
	 */
	private void saveTrainMap(BufferedImage trainMap) throws IOException {
		File outputFile = new File(outputMapPath);
		ImageIO.write(trainMap, IMAGE_FORMAT, outputFile);
	}
}