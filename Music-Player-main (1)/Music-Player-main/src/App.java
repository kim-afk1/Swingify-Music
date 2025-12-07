import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Mp3PlayerGUI mp3PlayerGUI = new Mp3PlayerGUI();

                MemberList memberList = new MemberList();
                Member adminAccount = new Member("admin", "admin@spootify.com", "admin");
                memberList.addMember(adminAccount);

                loginWindow loginDialog = new loginWindow("Login to Swingify", memberList, mp3PlayerGUI);
                loginDialog.pack();
                loginDialog.setLocationRelativeTo(null);
                loginDialog.setVisible(true);
            }
        });
    }

}
