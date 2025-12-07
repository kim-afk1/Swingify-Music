import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class Player extends PlaybackListener {
    private static final Object playSignal = new Object();
    private Mp3PlayerGUI mp3playerGUI;
    private Song currentSong;
    private boolean isPaused;
    private AdvancedPlayer advancedPlayer;
    private int currentFrame;
    private int currentTimeInMilliseconds;
    private ArrayList<Song> playlist;
    private int currentPlaylistIndex;
    private boolean songFinished;
    private boolean pressedNext;
    private boolean pressedPrev;
    private Thread playbackSliderThread;
    private volatile boolean stopSliderThread;

    public void setCurrentTimeInMilliseconds(int timeInMilliseconds) {
        currentTimeInMilliseconds = timeInMilliseconds;
    }

    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    public void loadPlaylist(File playlistFile) {
        playlist = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(playlistFile));
            String songPath;
            while((songPath = br.readLine())!=null) {
                Song song = new Song(songPath);
                playlist.add(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(playlist.size()>0) {
            mp3playerGUI.setPlaybackSliderValue(0);
            currentTimeInMilliseconds = 0;
            currentSong = playlist.get(0);
            currentFrame = 0;
            currentPlaylistIndex = 0;
            mp3playerGUI.enablePauseButtonDisablePlayButton();
            mp3playerGUI.updateSongTitleAndArtist(currentSong);
            mp3playerGUI.updatePlaybackSlider(currentSong);
            playCurrentSong();
        }
    }

    public Player(Mp3PlayerGUI mp3PlayerGUI) {
        this.mp3playerGUI = mp3PlayerGUI;
    }

    public void loadSong(Song song) {
        currentSong = song;
        playlist = null;

        if(!songFinished)
            stopSong();

        if(currentSong!=null) {
            currentFrame = 0;
            currentTimeInMilliseconds = 0;
            mp3playerGUI.setPlaybackSliderValue(0);
            playCurrentSong();
        }
    }

    public void pauseSong() {
        if(advancedPlayer!=null) {
            isPaused = true;
            stopSong();
        }
    }

    public void stopSong() {
        // Stop the slider thread first
        stopSliderThread = true;
        if(playbackSliderThread != null) {
            try {
                playbackSliderThread.join(500);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(advancedPlayer!=null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void playCurrentSong() {
        if(currentSong==null)
            return;
        try {
            songFinished = false;
            pressedNext = false;
            pressedPrev = false;
            stopSliderThread = false;

            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);
            startMusicThread();
            startPlaybackSliderThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMusicThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(isPaused) {
                        synchronized(playSignal) {
                            isPaused = false;
                            playSignal.notify();
                        }
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    } else {
                        advancedPlayer.play();
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPlaybackSliderThread() {
        playbackSliderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(isPaused) {
                    try {
                        synchronized (playSignal) {
                            playSignal.wait();
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                long startTime = System.currentTimeMillis();
                long pausedTime = currentTimeInMilliseconds;

                while(!isPaused && !songFinished && !pressedNext && !pressedPrev && !stopSliderThread) {
                    try {
                        long elapsedTime = System.currentTimeMillis() - startTime;
                        currentTimeInMilliseconds = (int)(pausedTime + elapsedTime);

                        int calculatedFrame = (int)(currentTimeInMilliseconds * currentSong.getFrameRatePerMilliseconds());

                        mp3playerGUI.setPlaybackSliderValue(calculatedFrame);

                        Thread.sleep(100);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        playbackSliderThread.start();
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void nextSong() {
        if(playlist == null)
            return;

        if(currentPlaylistIndex + 1 > playlist.size() - 1)
            return;

        pressedNext = true;

        if(!songFinished)
            stopSong();

        currentPlaylistIndex++;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMilliseconds = 0;
        mp3playerGUI.enablePauseButtonDisablePlayButton();
        mp3playerGUI.updateSongTitleAndArtist(currentSong);
        mp3playerGUI.updatePlaybackSlider(currentSong);
        mp3playerGUI.setPlaybackSliderValue(0);
        playCurrentSong();
    }

    public void prevSong() {
        if(playlist == null) {
            return;
        }

        if(currentPlaylistIndex - 1 < 0)
            return;

        pressedPrev = true;

        if(!songFinished)
            stopSong();

        currentPlaylistIndex--;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMilliseconds = 0;
        mp3playerGUI.enablePauseButtonDisablePlayButton();
        mp3playerGUI.updateSongTitleAndArtist(currentSong);
        mp3playerGUI.setPlaybackSliderValue(0);
        mp3playerGUI.updatePlaybackSlider(currentSong);
        playCurrentSong();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("Playback started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("Playback finished");

        // Stop the slider thread when playback finishes
        stopSliderThread = true;

        // Wait for slider thread to actually stop
        if(playbackSliderThread != null) {
            try {
                playbackSliderThread.join(200);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(isPaused) {
            currentFrame += (int)((double)evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        } else {
            if(pressedPrev || pressedNext)
                return;

            songFinished = true;
            if(playlist==null) {
                mp3playerGUI.enablePlayButtonDisablePauseButton();
            } else {
                if(currentPlaylistIndex == playlist.size() - 1) {
                    mp3playerGUI.enablePlayButtonDisablePauseButton();
                } else {
                    nextSong();
                }
            }
        }
    }
}