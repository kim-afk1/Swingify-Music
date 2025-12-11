import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;
import java.io.IOException;

public class MusicLibrary {
    private static MusicLibrary instance;
    private List<Song> allSongs;
    private List<Playlist> playlists;
    private List<LibraryListener> listeners;

    private MusicLibrary() {
        allSongs = new ArrayList<>();
        playlists = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public static synchronized MusicLibrary getInstance() {
        if (instance == null) {
            instance = new MusicLibrary();
        }
        return instance;
    }

    public void addSong(Song song) {
        allSongs.add(song);
        Collections.sort(allSongs, Comparator.comparing(Song::getName));
        notifyListeners();
    }

    // NEW METHOD: Remove song from the actual library
    public void removeSong(Song song) {
        allSongs.remove(song);
        notifyListeners();
    }

    public List<Song> getAllSongs() {
        return new ArrayList<>(allSongs);
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
        notifyListeners();
    }

    public void removePlaylist(Playlist playlist) {
        playlists.remove(playlist);
        notifyListeners();
    }

    public List<Playlist> getPlaylists() {
        return new ArrayList<>(playlists);
    }

    public void addListener(LibraryListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (LibraryListener listener : listeners) {
            listener.onLibraryChanged();
        }
    }

    public interface LibraryListener {
        void onLibraryChanged();
    }
}