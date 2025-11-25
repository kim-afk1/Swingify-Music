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

    public loginWindow(String title, MemberList memberList) {
        super();
        this.memberList = memberList;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setIconImage(new ImageIcon(getClass().getResource("/images/icon64.png")).getImage());

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

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
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

            // open home screen (wapakoy idea ani unsay design)

            dispose();
        } else {
            loginFeedback.setText("Invalid username or password");
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
