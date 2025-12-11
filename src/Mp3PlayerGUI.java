import javax.swing.*;
import java.awt.*;

public class Mp3PlayerGUI extends JFrame {
    private PlaybackController controller;
    private AudioPlayer player;
    private Member currentMember;
    private JLabel userLabel;
    private MemberList memberList; // Store the member list

    public Mp3PlayerGUI(MemberList memberList) {
        this.memberList = memberList; // Keep reference to member list
        controller = new PlaybackController();
        player = AudioPlayer.getInstance();

        setTitle("Swingify");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            setIconImage(new ImageIcon(getClass().getResource("/assets/disc.png")).getImage());
        } catch (Exception e) {
            System.err.println("Warning: Could not load icon: " + e.getMessage());
        }

        setLayout(new BorderLayout());

        // Add user info panel at the top
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 25, 25));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        userLabel = new JLabel("User: Not logged in");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> logout());

        topPanel.add(userLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(new SidebarPanel(controller), BorderLayout.WEST);
        add(new MainPanel(controller), BorderLayout.CENTER);

        // Don't show the window yet - wait for login
        setVisible(false);
    }

    public void setCurrentMember(Member member) {
        this.currentMember = member;
        if (member != null) {
            userLabel.setText("User: " + member.getUsername() + " (" + member.getEmail() + ")");
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Stop any playing music
            player.stop();

            // Clear the current member
            currentMember = null;

            // Hide the music player window
            setVisible(false);

            // Show login window again with the SAME member list
            loginWindow login = new loginWindow("Music Player Login", memberList, this);
            login.pack();
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        }
    }

    public Member getCurrentMember() {
        return currentMember;
    }
}