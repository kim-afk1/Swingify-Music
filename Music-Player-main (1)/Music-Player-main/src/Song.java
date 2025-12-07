import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class Song {
    private String title;
    private String artist;
    private String length;
    private String filePath;
    private Mp3File mp3File;
    private double frameRatePerMilliseconds;

    public Song(String filePath) {
        this.filePath = filePath;
        try {
            mp3File = new Mp3File(filePath);
            frameRatePerMilliseconds = (double) mp3File.getFrameCount()/mp3File.getLengthInMilliseconds();
            length = convertToSongLengthFormat();
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            Tag tag = audioFile.getTag();
            if(tag!=null) {
                title = tag.getFirst(FieldKey.TITLE);
                artist = tag.getFirst(FieldKey.ARTIST);
            } else {
                title = "N/A";
                artist = "N/A";
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String convertToSongLengthFormat() {
        long minutes = mp3File.getLengthInSeconds()/60;
        long seconds = mp3File.getLengthInSeconds()%60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        return formattedTime;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getLength() {
        return length;
    }

    public String getFilePath() {
        return filePath;
    }

    public double getFrameRatePerMilliseconds() {
        return frameRatePerMilliseconds;
    }

    public Mp3File getMp3File() {
        return mp3File;
    }

    public String getSongLength() {
        return length;
    }
}
