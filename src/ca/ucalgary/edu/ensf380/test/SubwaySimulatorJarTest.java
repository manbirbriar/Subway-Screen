package ca.ucalgary.edu.ensf380.test;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Test class for the SubwaySimulator JAR file.
 * This class contains test methods to verify the behavior of the SubwaySimulator
 * by running the JAR file and checking its output.
 */
public class SubwaySimulatorJarTest {

    /**
     * Constructs a SubwaySimulatorJarTest object.
     */
    public SubwaySimulatorJarTest() {}

    /**
     * Tests the output of the SubwaySimulator JAR file.
     * This test method performs the following steps:
     * 1. Defines paths for the JAR file, input file, and output directory.
     * 2. Ensures the output directory exists and is empty.
     * 3. Starts the SubwaySimulator process.
     * 4. Waits for 10 seconds for the process to complete.
     * 5. Checks if any files were created in the output directory.
     * 6. Asserts that at least one file was created.
     * 7. Prints the number of files created.
     *
     * @throws IOException If an I/O error occurs while manipulating files or starting the process.
     * @throws InterruptedException If the thread is interrupted while waiting for the process to complete.
     */
    @Test
    public void testSubwaySimulatorJarOutput() throws IOException, InterruptedException {
        // Define the paths
        String jarPath = "./exe/SubwaySimulator.jar";
        String inputPath = "./data/subway.csv";
        String outputPath = "./out";

        // Ensure the output directory exists and is empty
        Process process = getProcess(outputPath, jarPath, inputPath);

        // Wait for 10 seconds
        boolean completed = process.waitFor(10, TimeUnit.SECONDS);

        // If the process is still running, destroy it
        if (!completed) {
            process.destroy();
        }

        // Check if there are any files in the output directory
        Path dir = Paths.get(outputPath);
        long fileCount = Files.list(dir).count();

        // Assert that there are files in the output directory
        assertTrue("No files were created in the output directory after 10 seconds", fileCount > 0);

        // Optional: Print the number of files created
        System.out.println("Number of files created: " + fileCount);
    }

    /**
     * Prepares and starts the SubwaySimulator process.
     * This method performs the following steps:
     * 1. Ensures the output directory exists and is empty.
     * 2. Builds a ProcessBuilder with the correct command to run the JAR file.
     * 3. Starts the process.
     *
     * @param outputPath The path to the output directory.
     * @param jarPath The path to the SubwaySimulator JAR file.
     * @param inputPath The path to the input CSV file.
     * @return The started Process object.
     * @throws IOException If an I/O error occurs while manipulating files or starting the process.
     */
    private static Process getProcess(String outputPath, String jarPath, String inputPath) throws IOException {
        File outputDir = new File(outputPath);
        if (outputDir.exists()) {
            for (File file : outputDir.listFiles()) {
                file.delete();
            }
        } else {
            outputDir.mkdirs();
        }

        // Build the process
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", jarPath, "--in", inputPath, "--out", outputPath);
        builder.redirectErrorStream(true);

        // Start the process
        Process process = builder.start();
        return process;
    }
}