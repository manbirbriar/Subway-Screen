package ca.ucalgary.edu.ensf380.test;

import org.junit.Test;

import ca.ucalgary.edu.ensf380.tts.StationAnnouncer;

import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test class for the StationAnnouncer.
 * This class contains various test cases to verify the functionality
 * of the StationAnnouncer, including audio detection and error handling.
 */
public class StationAnnouncerTest {

    /**
     * Constructs a StationAnnouncerTest object.
     */
    public StationAnnouncerTest() {
    }

    private StationAnnouncer announcer;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    /**
     * Set up the test environment before each test.
     * Initializes the StationAnnouncer and redirects System.out and System.err.
     */
    @Before
    public void setUp() {
        announcer = new StationAnnouncer();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    /**
     * Restore the original System.out and System.err after each test.
     */
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * Test announcing a normal station name.
     * Verifies that audio is detected and no errors occur.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @Test
    public void testAnnounceNextStation() throws InterruptedException {
        String nextStation = "Central Station";
        assertAudioDetectedAndNoErrors(nextStation, "No audio was detected during the announcement");
    }

    /**
     * Test announcing an empty string.
     * Verifies that no errors occur when announcing an empty string.
     */
    @Test
    public void testAnnounceNextStationWithEmptyString() {
        announcer.announceNextStation("");
        assertTrue("Error occurred: " + errContent, errContent.toString().isEmpty());
    }

    /**
     * Test announcing a station name with a very long name.
     * Verifies that audio is detected and no errors occur.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @Test
    public void testAnnounceNextStationWithLongName() throws InterruptedException {
        String longStationName = "This is a very long station name that exceeds the usual length of a station name";
        assertAudioDetectedAndNoErrors(longStationName, "No audio was detected during the announcement of a long station name");
    }

    /**
     * Test announcing a station name with special characters.
     * Verifies that audio is detected and no errors occur.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @Test
    public void testAnnounceNextStationWithSpecialCharacters() throws InterruptedException {
        String specialStationName = "St. John's & 3rd Ave. Station!";
        assertAudioDetectedAndNoErrors(specialStationName, "No audio was detected during the announcement with special characters");
    }

    /**
     * Test announcing a station name with numbers.
     * Verifies that audio is detected and no errors occur.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @Test
    public void testAnnounceNextStationWithNumbers() throws InterruptedException {
        String stationWithNumbers = "Platform 9 3/4";
        assertAudioDetectedAndNoErrors(stationWithNumbers, "No audio was detected during the announcement with numbers");
    }

    /**
     * Test announcing a station name in a different language.
     * Verifies that audio is detected and no errors occur.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @Test
    public void testWordsInDifferentLanguage() throws InterruptedException {
        String foreignStationName = "Mucha Gracias Aficion Siuuu!";
        assertAudioDetectedAndNoErrors(foreignStationName, "No audio was detected during the announcement in a different language");
    }

    /**
     * Test announcing multiple station names in succession.
     * Verifies that audio is detected for each announcement and no errors occur.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    @Test
    public void testAnnounceMultipleStations() throws InterruptedException {
        String[] stations = {"First Station", "Second Station", "Third Station"};
        for (String station : stations) {
            assertAudioDetectedAndNoErrors(station, "No audio was detected during the announcement of " + station);
        }
    }

    /**
     * Helper method to assert that audio is detected and no errors occur for a given station name.
     *
     * @param stationName the name of the station to announce
     * @param errorMessage the error message to display if no audio is detected
     * @throws InterruptedException if the thread is interrupted
     */
    private void assertAudioDetectedAndNoErrors(String stationName, String errorMessage) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Thread announcementThread = new Thread(() -> {
            announcer.announceNextStation(stationName);
            latch.countDown();
        });

        boolean audioDetected = detectAudio(announcementThread, latch);

        assertTrue(errorMessage, audioDetected);
        assertTrue("Error occurred: " + errContent, errContent.toString().isEmpty());
    }

    /**
     * Detects audio output from the announcement thread.
     *
     * @param announcementThread the thread running the announcement
     * @param latch the countdown latch for thread synchronization
     * @return true if audio is detected, false otherwise
     * @throws InterruptedException if the thread is interrupted
     */
    private boolean detectAudio(Thread announcementThread, CountDownLatch latch) throws InterruptedException {
        AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported");
            return false;
        }

        try (TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info)) {
            line.open(format);
            line.start();

            byte[] buffer = new byte[4096];
            boolean audioDetected = false;

            announcementThread.start();

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 5000 && !audioDetected) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                audioDetected = isAudioDetected(buffer, bytesRead);
            }

            line.stop();
            latch.await(5, TimeUnit.SECONDS);

            return audioDetected;
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if audio is detected in the given buffer.
     *
     * @param buffer the byte array containing audio data
     * @param bytesRead the number of bytes read into the buffer
     * @return true if audio is detected, false otherwise
     */
    private boolean isAudioDetected(byte[] buffer, int bytesRead) {
        for (int i = 0; i < bytesRead; i += 2) {
            short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));
            if (Math.abs(sample) > 50) {  // Adjust this threshold as needed
                return true;
            }
        }
        return false;
    }
}