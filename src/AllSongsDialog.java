import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AllSongsDialog extends JDialog {
    private PlaybackController controller;
    private MusicLibrary library;
    private DefaultListModel<Song> listModel;
    private JList<Song> songList;
    private List<Song> songs;
    private JLabel titleLabel;

    public AllSongsDialog(Window owner, List<Song> songs, PlaybackController controller) {
        super(owner, "All Songs", ModalityType.MODELESS);
        this.controller = controller;
        this.library = MusicLibrary.getInstance();
        this.songs = songs;

        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(25, 25, 25));

        titleLabel = new JLabel("All Uploaded Songs (" + songs.size() + ")");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JButton playAllBtn = new JButton("Play All");
        playAllBtn.setFocusPainted(false);
        playAllBtn.addActionListener(e -> playAllSongs(songs));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(playAllBtn, BorderLayout.EAST);

        // Song list with custom renderer
        listModel = new DefaultListModel<>();
        for (Song song : songs) {
            listModel.addElement(song);
        }

        songList = new JList<>(listModel);
        songList.setFont(new Font("Arial", Font.PLAIN, 14));
        songList.setBackground(new Color(30, 30, 30));
        songList.setForeground(Color.WHITE);
        songList.setSelectionBackground(new Color(50, 150, 50));
        songList.setSelectionForeground(Color.WHITE);

        // Custom cell renderer for better appearance
        songList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof Song) {
                    Song song = (Song) value;
                    label.setText("  ♪ " + song.getName());
                    label.setBorder(new EmptyBorder(8, 10, 8, 10));
                }

                if (!isSelected) {
                    label.setBackground(index % 2 == 0 ? new Color(30, 30, 30) : new Color(35, 35, 35));
                }

                return label;
            }
        });

        // Double-click to play song
        songList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = songList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        playSongFromLibrary(songs, index);
                    }
                }
            }
        });

        // Right-click context menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem playItem = new JMenuItem("Play");
        JMenuItem playNextItem = new JMenuItem("Play Next");
        JMenuItem addToQueueItem = new JMenuItem("Add to Queue");
        JMenuItem deleteItem = new JMenuItem("Delete");

        playItem.addActionListener(e -> {
            int index = songList.getSelectedIndex();
            if (index >= 0) {
                playSongFromLibrary(songs, index);
            }
        });

        playNextItem.addActionListener(e -> {
            int index = songList.getSelectedIndex();
            if (index >= 0) {
                JOptionPane.showMessageDialog(this, "Play Next feature coming soon!");
            }
        });

        addToQueueItem.addActionListener(e -> {
            int index = songList.getSelectedIndex();
            if (index >= 0) {
                JOptionPane.showMessageDialog(this, "Add to Queue feature coming soon!");
            }
        });

        deleteItem.addActionListener(e -> {
            int index = songList.getSelectedIndex();
            if (index >= 0) {
                Song selectedSong = listModel.getElementAt(index);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete '" + selectedSong.getName() + "' from library?\nThis will remove it from all playlists.",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteSong(selectedSong, index);
                }
            }
        });

        popup.add(playItem);
        popup.add(playNextItem);
        popup.add(addToQueueItem);
        popup.addSeparator();
        popup.add(deleteItem);

        // Modified: Only show popup menu when right-clicking on a selected item
        songList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    handlePopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    handlePopup(e);
                }
            }

            private void handlePopup(MouseEvent e) {
                int index = songList.locationToIndex(e.getPoint());
                if (index >= 0 && songList.getCellBounds(index, index).contains(e.getPoint())) {
                    songList.setSelectedIndex(index);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(25, 25, 25));
        bottomPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JButton closeBtn = new JButton("Close");
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());

        bottomPanel.add(closeBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Dark theme
        getContentPane().setBackground(new Color(25, 25, 25));
    }

    private void playSongFromLibrary(List<Song> songs, int index) {
        controller.setPlaylist(songs);
        controller.playSong(index);
    }

    private void playAllSongs(List<Song> songs) {
        if (!songs.isEmpty()) {
            controller.setPlaylist(songs);
            controller.playSong(0);
        }
    }

    private void deleteSong(Song song, int index) {
        // CRITICAL: Must call library's remove method (not just get the list and modify it)
        // Getting the list returns a copy, not the actual list!
        library.removeSong(song);

        // Remove from all playlists
        for (Playlist playlist : library.getPlaylists()) {
            playlist.removeSong(song);
        }

        // Remove from the local list
        songs.remove(song);

        // Remove from the list model (updates UI)
        listModel.remove(index);

        // Update the header label with new count
        titleLabel.setText("All Uploaded Songs (" + songs.size() + ")");

        // Show confirmation message
        JOptionPane.showMessageDialog(this,
                "Song deleted successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}