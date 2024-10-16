package ca.ucalgary.edu.ensf380.tts;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import java.util.logging.Logger;

/**
 * The StationAnnouncer class is responsible for announcing the next station
 * using text-to-speech (TTS) functionality.
 */
public class StationAnnouncer {
    private final Logger logger = Logger.getLogger(StationAnnouncer.class.getName());
    private final String VOICE_NAME = "kevin16";
    private final VoiceManager voiceManager;

    /**
     * Constructs a StationAnnouncer object with the default VoiceManager.
     */
    public StationAnnouncer() {
        this.voiceManager = VoiceManager.getInstance();
    }

    /**
     * Constructs a StationAnnouncer object with the specified VoiceManager.
     *
     * @param voiceManager the VoiceManager to use
     */
    public StationAnnouncer(VoiceManager voiceManager) {
        this.voiceManager = voiceManager;
    }

    /**
     * Announces the next station using TTS.
     *
     * @param nextStation The name of the next station to announce.
     */
    public void announceNextStation(String nextStation) {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        Voice voice = voiceManager.getVoice(VOICE_NAME);

        if (voice != null) {
            try {
                voice.allocate();
                voice.speak(nextStation);
            } catch (Exception e) {
                logger.severe("Error speaking the announcement: " + e.getMessage());
            } finally {
                voice.deallocate();
            }
        } else {
            System.err.println("Cannot find voice: " + VOICE_NAME);
        }
    }
}
