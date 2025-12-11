import javax.swing.*;
import java.io.File;
import java.util.*;
import javax.sound.sampled.*;

public class AudioPlayer {
    private static AudioPlayer instance;

    private Clip clip;
    private long clipPosition = 0;
    private File currentFile;

    private boolean isPlaying = false;
    private boolean isPaused = false;
    private float volume = 0.8f;
    private List<PlayerListener> listeners = new ArrayList<>();

    private AudioPlayer() {}

    public static synchronized AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    public void loadSong(File file) throws Exception {
        stop();
        currentFile = file;
        String fileName = file.getName().toLowerCase();

        if (!fileName.endsWith(".wav")) {
            throw new Exception("Unsupported file format. Only WAV files are supported.");
        }

        loadWAV(file);
        notifyListeners();
    }

    private void loadWAV(File file) throws Exception {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clipPosition = 0;
            setVolume(volume);
        } catch (Exception e) {
            throw new Exception("Failed to load WAV file: " + e.getMessage());
        }
    }

    public void play() {
        if (clip != null) {
            clip.setMicrosecondPosition(clipPosition);
            clip.start();
        }
        isPlaying = true;
        isPaused = false;
        notifyListeners();
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            clipPosition = clip.getMicrosecondPosition();
            clip.stop();
        }
        isPlaying = false;
        isPaused = true;
        notifyListeners();
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clipPosition = 0;
        }
        isPlaying = false;
        isPaused = false;
        notifyListeners();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public long getCurrentPosition() {
        return clip != null ? clip.getMicrosecondPosition() : 0;
    }

    public long getDuration() {
        return clip != null ? clip.getMicrosecondLength() : 0;
    }

    public void setPosition(long position) {
        if (clip != null) {
            clipPosition = position;
            clip.setMicrosecondPosition(position);
        }
    }

    public void setVolume(float vol) {
        volume = Math.max(0, Math.min(1, vol));

        if (clip != null && clip.isOpen()) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                volumeControl.setValue(dB);
            } catch (Exception e) {
                System.err.println("Volume control not supported");
            }
        }
    }

    public float getVolume() {
        return volume;
    }

    public void addListener(PlayerListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (PlayerListener listener : listeners) {
            listener.onPlayerStateChanged();
        }
    }

    public interface PlayerListener {
        void onPlayerStateChanged();
    }
}