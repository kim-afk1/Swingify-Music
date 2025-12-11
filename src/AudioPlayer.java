import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.*;
import javax.sound.sampled.*;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class AudioPlayer {
    private static AudioPlayer instance;

    // For WAV files
    private Clip clip;
    private long clipPosition = 0;

    // For MP3 files
    private AdvancedPlayer mp3Player;
    private Thread mp3Thread;
    private File currentFile;
    private long mp3StartFrame = 0;
    private long mp3CurrentFrame = 0;
    private long mp3TotalFrames = 0;
    private volatile boolean mp3StopRequested = false;

    private boolean isPlaying = false;
    private boolean isPaused = false;
    private boolean isMp3 = false;
    private float volume = 0.8f;
    private List<PlayerListener> listeners = new ArrayList<>();

    private static final int FRAMES_PER_SECOND = 38; // Approximate for MP3

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

        if (fileName.endsWith(".mp3")) {
            isMp3 = true;
            loadMP3(file);
        } else if (fileName.endsWith(".wav")) {
            isMp3 = false;
            loadWAV(file);
        } else {
            throw new Exception("Unsupported file format. Only MP3 and WAV are supported.");
        }

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

    private void loadMP3(File file) throws Exception {
        try {
            mp3StartFrame = 0;
            mp3CurrentFrame = 0;

            // Estimate total frames based on file size
            // Rough estimate: 128kbps MP3, 38 frames per second
            long fileSizeInBytes = file.length();
            mp3TotalFrames = (fileSizeInBytes * 38) / 16000; // Rough approximation

        } catch (Exception e) {
            throw new Exception("Failed to load MP3 file: " + e.getMessage());
        }
    }

    public void play() {
        if (isMp3) {
            playMP3();
        } else {
            playWAV();
        }
        isPlaying = true;
        isPaused = false;
        notifyListeners();
    }

    private void playWAV() {
        if (clip != null) {
            clip.setMicrosecondPosition(clipPosition);
            clip.start();
        }
    }

    private void playMP3() {
        if (currentFile == null) return;

        mp3StopRequested = false;
        mp3Thread = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(currentFile);
                BufferedInputStream bis = new BufferedInputStream(fis);

                mp3Player = new AdvancedPlayer(bis);

                // Add playback listener to track frames
                mp3Player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        if (!mp3StopRequested) {
                            mp3CurrentFrame = mp3TotalFrames;
                            isPlaying = false;
                            notifyListeners();
                        }
                    }

                    @Override
                    public void playbackStarted(PlaybackEvent evt) {
                        // Track starting frame
                    }
                });

                // Play from the current frame position
                int startFrame = (int) mp3StartFrame;
                int endFrame = Integer.MAX_VALUE;

                // Start a timer to update current position
                javax.swing.Timer positionTimer = new javax.swing.Timer(100, e -> {
                    if (isPlaying && !mp3StopRequested) {
                        mp3CurrentFrame = mp3StartFrame +
                                (long)((System.currentTimeMillis() - mp3PlayStartTime) * FRAMES_PER_SECOND / 1000);
                        if (mp3CurrentFrame > mp3TotalFrames) {
                            mp3CurrentFrame = mp3TotalFrames;
                        }
                    }
                });
                positionTimer.start();

                mp3Player.play(startFrame, endFrame);

                positionTimer.stop();

                bis.close();
                fis.close();

            } catch (Exception e) {
                if (!mp3StopRequested) {
                    System.err.println("Error playing MP3: " + e.getMessage());
                }
            }
        });
        mp3Thread.start();
    }

    private long mp3PlayStartTime = 0;

    public void pause() {
        if (isMp3) {
            pauseMP3();
        } else {
            pauseWAV();
        }
        isPlaying = false;
        isPaused = true;
        notifyListeners();
    }

    private void pauseWAV() {
        if (clip != null && clip.isRunning()) {
            clipPosition = clip.getMicrosecondPosition();
            clip.stop();
        }
    }

    private void pauseMP3() {
        mp3StopRequested = true;
        if (mp3Player != null) {
            mp3Player.close();
            mp3Player = null;
        }
        if (mp3Thread != null) {
            mp3Thread.interrupt();
        }

        // Save current position for resume
        mp3StartFrame = mp3CurrentFrame;
    }

    public void stop() {
        if (isMp3) {
            stopMP3();
        } else {
            stopWAV();
        }
        isPlaying = false;
        isPaused = false;
        notifyListeners();
    }

    private void stopWAV() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clipPosition = 0;
        }
    }

    private void stopMP3() {
        mp3StopRequested = true;
        if (mp3Player != null) {
            mp3Player.close();
            mp3Player = null;
        }
        if (mp3Thread != null) {
            mp3Thread.interrupt();
        }
        mp3StartFrame = 0;
        mp3CurrentFrame = 0;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public long getCurrentPosition() {
        if (isMp3) {
            // Convert frames to microseconds
            // Assuming ~38 frames per second
            return (mp3CurrentFrame * 1000000) / FRAMES_PER_SECOND;
        } else {
            return clip != null ? clip.getMicrosecondPosition() : 0;
        }
    }

    public long getDuration() {
        if (isMp3) {
            // Convert total frames to microseconds
            return (mp3TotalFrames * 1000000) / FRAMES_PER_SECOND;
        } else {
            return clip != null ? clip.getMicrosecondLength() : 0;
        }
    }

    public void setPosition(long position) {
        if (isMp3) {
            // Convert microseconds to frames
            long targetFrame = (position * FRAMES_PER_SECOND) / 1000000;

            boolean wasPlaying = isPlaying;

            // Stop current playback
            if (isPlaying || isPaused) {
                pauseMP3();
            }

            // Set new start position
            mp3StartFrame = targetFrame;
            mp3CurrentFrame = targetFrame;

            // Resume if it was playing
            if (wasPlaying) {
                mp3PlayStartTime = System.currentTimeMillis();
                play();
            }
        } else {
            if (clip != null) {
                clipPosition = position;
                clip.setMicrosecondPosition(position);
            }
        }
    }

    public void setVolume(float vol) {
        volume = Math.max(0, Math.min(1, vol));

        if (!isMp3 && clip != null && clip.isOpen()) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                volumeControl.setValue(dB);
            } catch (Exception e) {
                System.err.println("Volume control not supported");
            }
        }
        // Note: MP3 volume control requires different approach with JLayer
        // For now, volume changes only affect WAV files
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