import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Mp3PlayerGUI().setVisible(true);

                //Song song = new Song("src/assets/Kung Maging Akin Ka.mp3");
                //System.out.println(song.getTitle());
                //System.out.println(song.getArtist());
            }
        });
    }
}
