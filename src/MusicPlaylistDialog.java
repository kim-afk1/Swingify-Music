import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class MusicPlaylistDialog extends JDialog {
    private Mp3PlayerGUI mp3PlayerGUI;
    private ArrayList<String> songPaths;

    public MusicPlaylistDialog(Mp3PlayerGUI mp3PlayerGUI) {
        this.mp3PlayerGUI = new Mp3PlayerGUI();
        songPaths = new ArrayList<>();
        setTitle("Create Playlist");
        setSize(400, 400);
        setResizable(false);
        getContentPane().setBackground(Mp3PlayerGUI.FRAME_COLOR);
        setLayout(null);
        setModal(true);
        setLocationRelativeTo(mp3PlayerGUI);

        addDialogComponents();
    }

    private void addDialogComponents() {
        JPanel songContainer = new JPanel();
        songContainer.setLayout(new BoxLayout(songContainer, BoxLayout.Y_AXIS));
        songContainer.setBounds((int)(getWidth()*0.025), 10, (int)(getWidth()*0.9), (int)(getHeight()*0.75));
        add(songContainer);

        JButton addSongButton = new JButton("Add");
        addSongButton.setBounds(60, (int)(getHeight() * 0.80), 100, 25);
        addSongButton.setFont(new Font("Dialog", Font.BOLD, 14));
        addSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setCurrentDirectory(new File("src/assets"));
                jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3","mp3"));
                int result = jFileChooser.showOpenDialog(MusicPlaylistDialog.this);

                File selectedFile = jFileChooser.getSelectedFile();
                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                    JLabel filePathLabel = new JLabel(selectedFile.getPath());
                    filePathLabel.setFont(new Font("Dialog", Font.BOLD, 12));
                    filePathLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    songPaths.add(filePathLabel.getText());
                    songContainer.add(filePathLabel);
                    songContainer.revalidate();
                }
            }
        });
        add(addSongButton);

        JButton savePlaylistButton = new JButton("Save");
        savePlaylistButton.setBounds(215,(int)(getHeight() * 0.80), 100, 25);
        savePlaylistButton.setFont(new Font("Dialog", Font.BOLD, 14));
        savePlaylistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setCurrentDirectory(new File("src/assets"));
                    int result = jFileChooser.showSaveDialog(MusicPlaylistDialog.this);
                    if(result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = jFileChooser.getSelectedFile();
                        if(!selectedFile.getName().substring(selectedFile.getName().length()-4).equalsIgnoreCase(".txt")) {
                            selectedFile = new File(selectedFile.getAbsoluteFile() + ".txt");
                        }
                        selectedFile.createNewFile();
                        BufferedWriter bw = new BufferedWriter(new FileWriter(selectedFile));

                        for(String songPath : songPaths) {
                            bw.write(songPath + "\n");
                        }
                        bw.close();
                        JOptionPane.showMessageDialog(MusicPlaylistDialog.this, "Successfully created playlist.");
                        MusicPlaylistDialog.this.dispose();
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        add(savePlaylistButton);
    }
}
