import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;
import javax.imageio.ImageIO;
import java.io.IOException;

public class SidebarPanel extends JPanel {
    private MusicLibrary library;
    private PlaybackController controller;
    private JPanel playlistContainer;
    private MoodSelector moodSelector;

    public SidebarPanel(PlaybackController controller) {
        this.controller = controller;
        this.library = MusicLibrary.getInstance();

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280, 600));
        setBackground(new Color(40, 40, 40));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createTopIcons(), BorderLayout.NORTH);

        // Create center panel with playlist and mood selector
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(40, 40, 40));

        centerPanel.add(createPlaylistSection(), BorderLayout.CENTER);

        // Add mood selector at the bottom of center panel
        moodSelector = new MoodSelector();
        centerPanel.add(moodSelector, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        library.addListener(this::refreshPlaylists);
    }

    private JPanel createTopIcons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(new Color(40, 40, 40));

        // Use createIconButtonWithImage for image icons
        JButton addMusicBtn = createIconButtonWithImage("icons/music.png", "Add Music");
        addMusicBtn.addActionListener(e -> addMusicFile());

        JButton addPlaylistBtn = createIconButtonWithImage("icons/playlist.png", "Create Playlist");
        addPlaylistBtn.addActionListener(e -> createPlaylist());

        JButton viewSongsBtn = createIconButtonWithImage("icons/folder.png", "View All Songs");
        viewSongsBtn.addActionListener(e -> showAllSongs());

        panel.add(addMusicBtn);
        panel.add(addPlaylistBtn);
        panel.add(viewSongsBtn);

        return panel;
    }

    // Helper method to load icon from file with transparency support - no borders
    private JButton createIconButtonWithImage(String imagePath, String tooltip) {
        JButton btn = new JButton();
        try {
            BufferedImage originalImg = ImageIO.read(new File(imagePath));
            Image normalSize = originalImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            Image hoverSize = originalImg.getScaledInstance(26, 26, Image.SCALE_SMOOTH);

            btn.setIcon(new ImageIcon(normalSize));

            // Add hover effect - shrink on hover (using pre-scaled images)
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setIcon(new ImageIcon(hoverSize));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setIcon(new ImageIcon(normalSize));
                }
            });
        } catch (Exception e) {
            // Fallback to text if image not found
            btn.setText("?");
            btn.setFont(new Font("Arial", Font.BOLD, 20));
        }
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false); // Make button background transparent
        btn.setBorderPainted(false); // Remove border painting
        btn.setOpaque(false); // Make completely transparent
        btn.setForeground(Color.WHITE);
        btn.setBorder(null); // Remove all borders
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(30, 30)); // Size matches icon

        return btn;
    }

    private JPanel createPlaylistSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 40, 40));

        JLabel libraryLabel = new JLabel("Your Library");
        libraryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        libraryLabel.setForeground(Color.WHITE);
        libraryLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Try to load library icon with transparency
        try {
            BufferedImage img = ImageIO.read(new File("icons/library.png"));
            Image scaledImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            libraryLabel.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            // Fallback to emoji if image not found
            libraryLabel.setText("🎵 Your Library");
        }

        playlistContainer = new JPanel();
        playlistContainer.setLayout(new BoxLayout(playlistContainer, BoxLayout.Y_AXIS));
        playlistContainer.setBackground(new Color(40, 40, 40));

        JScrollPane scrollPane = new JScrollPane(playlistContainer);
        scrollPane.setBackground(new Color(40, 40, 40));
        scrollPane.setBorder(null);

        panel.add(libraryLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void addMusicFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Audio Files", "mp3", "wav"));
        fileChooser.setMultiSelectionEnabled(true);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File[] files = fileChooser.getSelectedFiles();
                for (File file : files) {
                    String name = file.getName().replaceFirst("[.][^.]+$", "");
                    library.addSong(new Song(name, file));
                }
                JOptionPane.showMessageDialog(this, files.length + " song(s) added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding songs: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createPlaylist() {
        String name = JOptionPane.showInputDialog(this, "Enter playlist name:");
        if (name != null && !name.trim().isEmpty()) {
            library.addPlaylist(new Playlist(name.trim()));
        }
    }

    private void showAllSongs() {
        // Pass the controller so songs can be played directly
        new AllSongsDialog(SwingUtilities.getWindowAncestor(this),
                library.getAllSongs(),
                controller).setVisible(true);
    }

    private void refreshPlaylists() {
        playlistContainer.removeAll();

        for (Playlist playlist : library.getPlaylists()) {
            PlaylistAccordion accordion = new PlaylistAccordion(playlist, controller, library);
            playlistContainer.add(accordion);
            playlistContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        playlistContainer.revalidate();
        playlistContainer.repaint();
    }

    public MoodSelector getMoodSelector() {
        return moodSelector;
    }
}