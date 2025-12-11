import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MoodSelector extends JPanel {
    private JLabel moodLabel;
    private String currentMood = "None";
    private JPanel moodDisplayPanel;

    public MoodSelector() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(40, 40, 40));
        setBorder(new EmptyBorder(15, 10, 15, 10));

        // Title label
        JLabel titleLabel = new JLabel("What's your mood for today?");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Mood display panel (shows selected mood)
        moodDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        moodDisplayPanel.setBackground(new Color(40, 40, 40));

        moodLabel = new JLabel("❓");
        moodLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        moodLabel.setForeground(Color.LIGHT_GRAY);
        moodDisplayPanel.add(moodLabel);

        // Button to open mood selector
        JButton selectMoodBtn = new JButton("Choose Mood");
        selectMoodBtn.setFocusPainted(false);
        selectMoodBtn.setBackground(new Color(50, 150, 50));
        selectMoodBtn.setForeground(Color.BLACK);
        selectMoodBtn.setFont(new Font("Arial", Font.BOLD, 12));
        selectMoodBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectMoodBtn.addActionListener(e -> showMoodOptions());

        // Add components
        add(titleLabel, BorderLayout.NORTH);
        add(moodDisplayPanel, BorderLayout.CENTER);
        add(selectMoodBtn, BorderLayout.SOUTH);
    }

    private void showMoodOptions() {
        JDialog moodDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Your Mood", true);
        moodDialog.setLayout(new GridLayout(2, 3, 15, 15));
        moodDialog.setSize(600, 320);
        moodDialog.setLocationRelativeTo(this);
        moodDialog.getContentPane().setBackground(new Color(30, 30, 30));

        // Add padding to the dialog
        ((JPanel) moodDialog.getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // Create mood buttons
        addMoodButton(moodDialog, "Happy", "😊", new Color(255, 215, 0));
        addMoodButton(moodDialog, "Sad", "😢", new Color(100, 149, 237));
        addMoodButton(moodDialog, "Cool", "😎", new Color(147, 112, 219));
        addMoodButton(moodDialog, "Angry", "😠", new Color(220, 20, 60));
        addMoodButton(moodDialog, "Relaxed", "😌", new Color(144, 238, 144));
        addMoodButton(moodDialog, "Excited", "🤩", new Color(255, 140, 0));

        moodDialog.setVisible(true);
    }

    private void addMoodButton(JDialog dialog, String moodName, String emoji, Color color) {
        JButton btn = new JButton();

        // Create a panel to hold emoji and text
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Add top spacing to push content down
        contentPanel.add(Box.createVerticalStrut(15));

        // Emoji label
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        emojiLabel.setForeground(Color.BLACK);
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text label
        JLabel textLabel = new JLabel(moodName);
        textLabel.setFont(new Font("Arial", Font.BOLD, 16));
        textLabel.setForeground(Color.BLACK);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(emojiLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(textLabel);

        btn.setLayout(new BorderLayout());
        btn.add(contentPanel, BorderLayout.CENTER);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Add hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        btn.addActionListener(e -> {
            setMood(moodName, emoji);
            dialog.dispose();
        });

        dialog.add(btn);
    }

    private void setMood(String mood, String emoji) {
        currentMood = mood;
        moodLabel.setText(emoji);
        moodLabel.setForeground(Color.WHITE);

        // Show a notification
        JOptionPane.showMessageDialog(this,
                "Your mood is set to: " + mood + " " + emoji,
                "Mood Updated",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public String getCurrentMood() {
        return currentMood;
    }
}