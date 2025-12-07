import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class Mp3PlayerGUI extends JFrame {

    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color SIDEBAR_COLOR = new Color(18, 18, 18);

    private static final int SIDEBAR_WIDTH = 230;

    private Player musicPlayer;
    private JFileChooser fileChooser;
    private JLabel songTitle;
    private JLabel songArtist;
    private JLabel userLabel;
    private Member currentMember;

    private JPanel songListPanel;
    private DefaultListModel<String> songListModel;
    private JList<String> songList;

    private JPanel playbackBtns;
    private JSlider playbackSlider;
    private JPanel playlistPanel;
    private JButton playButton;
    private JButton pauseButton;
    private JButton prevButton;
    private JButton nextButton;

    public Mp3PlayerGUI() {
        super("Swingify");
        setSize(700, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new Player(this);
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/assets"));

        fileChooser.setFileFilter(new FileNameExtensionFilter("MP3","mp3"));

        addGUIComponents();
        loadPlaylistsFromDirectory();
    }

    public void setCurrentMember(Member member) {
        this.currentMember = member;
        if(userLabel != null && member != null) {
            userLabel.setText("<html>Hello, <font color='rgb(204,204,0)'>" + member.getUsername() + "!</font></html>");
            userLabel.repaint();
        }
    }


    private void addGUIComponents() {
        addToolbar();
        addPlaylistSidebar();

        int sidebarWidth = SIDEBAR_WIDTH;
        int playerHeight = 200; // Height of the bottom-right player panel
        int padding = 10;

        // ---------- SONG LIST PANEL WITH USER LABEL ----------
        // Parent panel for song list + user label
        JPanel songListContainer = new JPanel(new BorderLayout());
        songListContainer.setBounds(
                sidebarWidth + padding,
                20,
                getWidth() - sidebarWidth - 2 * padding,
                getHeight() - playerHeight - 40
        );
        songListContainer.setBackground(FRAME_COLOR);

        // Song list
        songListModel = new DefaultListModel<>();
        songList = new JList<>(songListModel);
        songList.setFont(new Font("Dialog", Font.PLAIN, 14));
        songList.setForeground(TEXT_COLOR);
        songList.setBackground(FRAME_COLOR);
        songList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = songList.getSelectedIndex();
                    if (index >= 0) {
                        Song selectedSong = musicPlayer.getCurrentPlaylist().get(index);
                        musicPlayer.loadSong(selectedSong);
                        updateSongTitleAndArtist(selectedSong);
                        updatePlaybackSlider(selectedSong);
                        enablePauseButtonDisablePlayButton();
                        musicPlayer.playCurrentSong();
                    }
                }
            }
        });

        JScrollPane songScrollPane = new JScrollPane(songList);
        songScrollPane.setBorder(BorderFactory.createTitledBorder("Songs"));
        songScrollPane.setBackground(FRAME_COLOR);

        // Add scroll pane to center
        songListContainer.add(songScrollPane, BorderLayout.CENTER);

        // User label at top-right
        userLabel = new JLabel("Not logged in");
        userLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        userLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5)); // padding
        songListContainer.add(userLabel, BorderLayout.NORTH);

        // Add the container to the frame
        add(songListContainer);

        // -------------------- PLAYER PANEL --------------------
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setBounds(
                sidebarWidth + padding,
                getHeight() - playerHeight - 20,
                getWidth() - sidebarWidth - 2 * padding,
                playerHeight
        );
        playerPanel.setBackground(FRAME_COLOR);
        add(playerPanel);

        // Center panel: album image + song info
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(FRAME_COLOR);
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel songImage = new JLabel(loadImage("src/assets/icon.png"));
        songImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(songImage);

        songTitle = new JLabel("Song title");
        songTitle.setFont(new Font("Dialog", Font.BOLD, 16));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(songTitle);

        songArtist = new JLabel("Artist");
        songArtist.setFont(new Font("Dialog", Font.BOLD, 14));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(songArtist);

        playerPanel.add(centerPanel, BorderLayout.CENTER);

        // South panel: slider + buttons
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBackground(FRAME_COLOR);

        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBackground(FRAME_COLOR);
        playbackSlider.setOpaque(false);
        playbackSlider.setFocusable(false);
        playbackSlider.setUI(new CustomSliderUI(playbackSlider));
        southPanel.add(playbackSlider);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonsPanel.setBackground(FRAME_COLOR);
        Dimension btnSize = new Dimension(40, 40);

        prevButton = new JButton(loadImage("src/assets/previous.png"));
        prevButton.setPreferredSize(btnSize);
        prevButton.setBorderPainted(false);
        prevButton.setContentAreaFilled(false);
        prevButton.addActionListener(e -> musicPlayer.prevSong());
        buttonsPanel.add(prevButton);

        playButton = new JButton(loadImage("src/assets/play.png"));
        playButton.setPreferredSize(btnSize);
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.addActionListener(e -> {
            enablePauseButtonDisablePlayButton();
            musicPlayer.playCurrentSong();
        });
        buttonsPanel.add(playButton);

        pauseButton = new JButton(loadImage("src/assets/pause.png"));
        pauseButton.setPreferredSize(btnSize);
        pauseButton.setBorderPainted(false);
        pauseButton.setContentAreaFilled(false);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(e -> {
            enablePlayButtonDisablePauseButton();
            musicPlayer.pauseSong();
        });
        buttonsPanel.add(pauseButton);

        nextButton = new JButton(loadImage("src/assets/next.png"));
        nextButton.setPreferredSize(btnSize);
        nextButton.setBorderPainted(false);
        nextButton.setContentAreaFilled(false);
        nextButton.addActionListener(e -> musicPlayer.nextSong());
        buttonsPanel.add(nextButton);

        southPanel.add(buttonsPanel);
        playerPanel.add(southPanel, BorderLayout.SOUTH);
    }

    private void addPlaybackBtnsToPanel(JPanel panel) {
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        // Playback buttons container
        playbackBtns = new JPanel();
        playbackBtns.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        playbackBtns.setBounds(0, panelHeight - 60, panelWidth, 50);
        playbackBtns.setBackground(FRAME_COLOR);

        Dimension btnSize = new Dimension(35, 35); // smaller buttons

        // Previous button
        prevButton = new JButton(loadImage("src/assets/previous.png"));
        prevButton.setPreferredSize(btnSize);
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.setFocusable(false);
        prevButton.addActionListener(e -> musicPlayer.prevSong());
        playbackBtns.add(prevButton);

        // Play button
        playButton = new JButton(loadImage("src/assets/play.png"));
        playButton.setPreferredSize(btnSize);
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.setFocusable(false);
        playButton.addActionListener(e -> {
            enablePauseButtonDisablePlayButton();
            musicPlayer.playCurrentSong();
        });
        playbackBtns.add(playButton);

        // Pause button
        pauseButton = new JButton(loadImage("src/assets/pause.png"));
        pauseButton.setPreferredSize(btnSize);
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.setFocusable(false);
        pauseButton.addActionListener(e -> {
            enablePlayButtonDisablePauseButton();
            musicPlayer.pauseSong();
        });
        playbackBtns.add(pauseButton);

        // Next button
        nextButton = new JButton(loadImage("src/assets/next.png"));
        nextButton.setPreferredSize(btnSize);
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.setFocusable(false);
        nextButton.addActionListener(e -> musicPlayer.nextSong());
        playbackBtns.add(nextButton);

        panel.add(playbackBtns);
    }

    private void addPlaylistSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBounds(0, 20, SIDEBAR_WIDTH, getHeight() - 20);
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setLayout(null);

        JLabel playlistTitle = new JLabel("Playlists");
        playlistTitle.setBounds(10, 10, 190, 30);
        playlistTitle.setFont(new Font("Dialog", Font.BOLD, 18));
        playlistTitle.setForeground(TEXT_COLOR);
        sidebar.add(playlistTitle);

        JButton addPlaylistBtn = new JButton("+ Add Playlist");
        addPlaylistBtn.setBounds(10, 50, 210, 35);
        addPlaylistBtn.setFont(new Font("Dialog", Font.BOLD, 12));
        addPlaylistBtn.setFocusable(false);
        addPlaylistBtn.setBackground(new Color(154, 153, 0));
        addPlaylistBtn.setForeground(Color.WHITE);
        addPlaylistBtn.setBorderPainted(false);
        addPlaylistBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MusicPlaylistDialog dialog = new MusicPlaylistDialog(Mp3PlayerGUI.this);
                dialog.setVisible(true);
                refreshPlaylistSidebar();
            }
        });
        sidebar.add(addPlaylistBtn);

        playlistPanel = new JPanel();
        playlistPanel.setLayout(new BoxLayout(playlistPanel, BoxLayout.Y_AXIS));
        playlistPanel.setBackground(SIDEBAR_COLOR);

        JScrollPane scrollPane = new JScrollPane(playlistPanel);
        scrollPane.setBounds(10, 95, 210, getHeight() - 115);
        scrollPane.setBackground(SIDEBAR_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        sidebar.add(scrollPane);

        add(sidebar);
    }

    public void addPlaylistToSidebar(String playlistName, File playlistFile) {
        JButton playlistBtn = new JButton(playlistName);
        playlistBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        playlistBtn.setFont(new Font("Dialog", Font.PLAIN, 12));
        playlistBtn.setForeground(Color.LIGHT_GRAY);
        playlistBtn.setBackground(SIDEBAR_COLOR);
        playlistBtn.setHorizontalAlignment(SwingConstants.LEFT);
        playlistBtn.setBorderPainted(false);
        playlistBtn.setFocusable(false);
        playlistBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        playlistBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                playlistBtn.setBackground(new Color(40, 40, 40));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                playlistBtn.setBackground(SIDEBAR_COLOR);
            }
        });

        playlistBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.stopSong();
                musicPlayer.loadPlaylist(playlistFile);

                // ✅ Populate song list without auto-playing
                songListModel.clear();
                for (Song song : musicPlayer.getCurrentPlaylist()) {
                    songListModel.addElement(song.getTitle() + " - " + song.getArtist());
                }
            }
        });

        playlistPanel.add(playlistBtn);
        playlistPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        playlistPanel.revalidate();
        playlistPanel.repaint();
    }

    private void addToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(), 20);

        toolBar.setFloatable(false);
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(Mp3PlayerGUI.this);
                File selectedFile = fileChooser.getSelectedFile();

                if(result == fileChooser.APPROVE_OPTION && selectedFile != null) {
                    Song song = new Song(selectedFile.getPath());
                    musicPlayer.loadSong(song);
                    updateSongTitleAndArtist(song);
                    updatePlaybackSlider(song);
                    enablePauseButtonDisablePlayButton();
                }
            }
        });
        songMenu.add(loadSong);

        JMenu accountMenu = new JMenu("Account");
        menuBar.add(accountMenu);

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        Mp3PlayerGUI.this,
                        "Are you sure you want to logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

                if(confirm == JOptionPane.YES_OPTION) {
                    musicPlayer.stopSong();
                    setVisible(false);
                    System.exit(0);
                }
            }
        });
        accountMenu.add(logoutItem);

        add(toolBar);
    }

    public void enablePauseButtonDisablePlayButton() {
        playButton.setVisible(false);
        playButton.setEnabled(false);

        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    public void enablePlayButtonDisablePauseButton() {
        playButton.setVisible(true);
        playButton.setEnabled(true);

        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private void addPlaybackBtns() {
        playbackBtns = new JPanel();
        // Consistent sidebar width for positioning
        playbackBtns.setBounds(SIDEBAR_WIDTH, 410, getWidth() - SIDEBAR_WIDTH - 20, 80);
        playbackBtns.setBackground(FRAME_COLOR);

        JButton prevButton = new JButton(loadImage("src/assets/previous.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.setFocusable(false);
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.prevSong();
            }
        });
        playbackBtns.add(prevButton);

        JButton playButton = new JButton(loadImage("src/assets/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.setFocusable(false);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePauseButtonDisablePlayButton();
                musicPlayer.playCurrentSong();
            }
        });
        playbackBtns.add(playButton);

        JButton pauseButton = new JButton(loadImage("src/assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePlayButtonDisablePauseButton();
                musicPlayer.pauseSong();
            }
        });
        playbackBtns.add(pauseButton);

        JButton nextButton = new JButton(loadImage("src/assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.setFocusable(false);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.nextSong();
            }
        });
        playbackBtns.add(nextButton);

        add(playbackBtns);
    }

    public void setPlaybackSliderValue(int frame) {
        playbackSlider.setValue(frame);
    }

    public void updateSongTitleAndArtist(Song song) {
        songTitle.setText(song.getTitle());
        songArtist.setText(song.getArtist());
    }

    public void updatePlaybackSlider(Song song) {
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

        JLabel labelBeginning = new JLabel("00:00");
        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
        labelBeginning.setForeground(TEXT_COLOR);

        JLabel labelEnd = new JLabel(song.getSongLength());
        labelEnd.setFont(new Font("Dialog", Font.BOLD, 18));
        labelEnd.setForeground(TEXT_COLOR);

        labelTable.put(0, labelBeginning);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnd);

        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    private ImageIcon loadImage(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadPlaylistsFromDirectory() {
        File playlistDir = new File("src/assets");
        if (playlistDir.exists() && playlistDir.isDirectory()) {
            File[] files = playlistDir.listFiles(new java.io.FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().toLowerCase().endsWith(".txt");
                }
            });

            if (files != null) {
                for (File file : files) {
                    String playlistName = file.getName().replace(".txt", "");
                    addPlaylistToSidebar(playlistName, file);
                }
            }
        }
    }

    public void refreshPlaylistSidebar() {
        playlistPanel.removeAll();
        loadPlaylistsFromDirectory();
        playlistPanel.revalidate();
        playlistPanel.repaint();
    }
}