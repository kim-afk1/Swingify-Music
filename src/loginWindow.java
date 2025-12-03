import javax.swing.*;
import java.awt.event.*;

public class loginWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField userField;
    private JPasswordField passField;
    private JLabel loginTitle;
    private JLabel passLabel;
    private JLabel userLabel;
    private JButton createAccountButton;
    private JLabel loginFeedback;
    private MemberList memberList;
    private Mp3PlayerGUI mp3PlayerGUI;

    public loginWindow(String title, MemberList memberList, Mp3PlayerGUI mp3PlayerGUI) {
        super();
        this.memberList = memberList;
        this.mp3PlayerGUI = mp3PlayerGUI;
        setTitle(title);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        try {
            setIconImage(new ImageIcon(getClass().getResource("/assets/icon.png")).getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccount dialog = new createAccount(memberList);
                dialog.pack();
                dialog.setLocationRelativeTo(loginWindow.this);
                dialog.setVisible(true);
            }
        });
    }

    private void onOK() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if(username.isEmpty() || password.isEmpty()) {
            loginFeedback.setText("Enter credentials");
            return;
        }

        Member member = memberList.findMember(username, password);
        if(member != null) {
            loginFeedback.setText("Login successful!");

            // Set the current member in the MP3 player
            mp3PlayerGUI.setCurrentMember(member);

            // Close login window first
            dispose();

            // Show the MP3 player and bring it to front
            mp3PlayerGUI.setVisible(true);
            mp3PlayerGUI.toFront();
            mp3PlayerGUI.requestFocus();
            mp3PlayerGUI.setState(JFrame.NORMAL);
        } else {
            loginFeedback.setText("Invalid username or password");
        }
    }

    private void onCancel() {
        // Exit the application when login is cancelled
        System.exit(0);
    }
}