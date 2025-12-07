import javax.swing.*;
import java.awt.event.*;

public class createAccount extends JDialog {
    private JPanel contentPane;
    private JButton createCA;
    private JButton cancelCA;
    private JTextField userFieldCA;
    private JPasswordField passFieldCA;
    private JLabel userCA;
    private JLabel passCA;
    private JPasswordField confirmPassCA;
    private JLabel feedback;
    private JTextField emailFieldCA;
    private MemberList memberList;

    public createAccount(MemberList memberList) {
        this.memberList = memberList;
        setTitle("Create a new account");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(createCA);
        setIconImage(new ImageIcon(getClass().getResource("/assets/icon.png")).getImage());

        createCA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        cancelCA.addActionListener(new ActionListener() {
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
    }

    private void onOK() {
        String username = userFieldCA.getText();
        String password = new String(passFieldCA.getPassword());
        String confirmPassword = new String(confirmPassCA.getPassword());
        String email = emailFieldCA.getText();

        // check fields
        if(username.isEmpty()) {
            feedback.setText("Enter a username!");
            return;
        }

        if(password.isEmpty()) {
            feedback.setText("Create a password.");
            return;
        }

        if(!confirmPassword.equals(password)) {
            feedback.setText("Passwords do not match!");
            return;
        }

        if(!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            feedback.setText("Invalid email!");
            return;
        }

        Member newMember = new Member(username, email, password);
        memberList.addMember(newMember);

        JOptionPane.showMessageDialog(this, "Account created successfully!");
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}