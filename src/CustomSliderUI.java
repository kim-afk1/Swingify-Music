import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class CustomSliderUI extends BasicSliderUI {
    private static final int TRACK_HEIGHT = 6;
    private static final int THUMB_SIZE = 18;
    private Color trackColor = new Color(60, 60, 60);
    private Color progressColor = new Color(205, 175, 52); // Spotify green
    private Color thumbColor = Color.WHITE;

    public CustomSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle trackBounds = trackRect;
        int cy = (trackBounds.height / 2) - (TRACK_HEIGHT / 2);

        // Draw background track (gray)
        g2d.setColor(trackColor);
        RoundRectangle2D trackShape = new RoundRectangle2D.Double(
                trackBounds.x, trackBounds.y + cy,
                trackBounds.width, TRACK_HEIGHT,
                TRACK_HEIGHT, TRACK_HEIGHT
        );
        g2d.fill(trackShape);

        // Draw progress track (green/colored part)
        int thumbPos = thumbRect.x + (thumbRect.width / 2);
        int progressWidth = thumbPos - trackBounds.x;

        g2d.setColor(progressColor);
        RoundRectangle2D progressShape = new RoundRectangle2D.Double(
                trackBounds.x, trackBounds.y + cy,
                progressWidth, TRACK_HEIGHT,
                TRACK_HEIGHT, TRACK_HEIGHT
        );
        g2d.fill(progressShape);
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw circular thumb
        int x = thumbRect.x + (thumbRect.width / 2) - (THUMB_SIZE / 2);
        int y = thumbRect.y + (thumbRect.height / 2) - (THUMB_SIZE / 2);

        // Shadow effect
        g2d.setColor(new Color(0, 0, 0, 50));
        Ellipse2D shadow = new Ellipse2D.Double(x + 1, y + 2, THUMB_SIZE, THUMB_SIZE);
        g2d.fill(shadow);

        // Thumb
        g2d.setColor(thumbColor);
        Ellipse2D thumb = new Ellipse2D.Double(x, y, THUMB_SIZE, THUMB_SIZE);
        g2d.fill(thumb);
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(THUMB_SIZE, THUMB_SIZE);
    }

    // Customization methods
    public void setTrackColor(Color color) {
        this.trackColor = color;
    }

    public void setProgressColor(Color color) {
        this.progressColor = color;
    }

    public void setThumbColor(Color color) {
        this.thumbColor = color;
    }
}

// Alternative: Square/Rectangle thumb style
class CustomSliderSquareUI extends BasicSliderUI {
    private static final int TRACK_HEIGHT = 4;
    private static final int THUMB_WIDTH = 12;
    private static final int THUMB_HEIGHT = 20;
    private Color trackColor = new Color(80, 80, 80);
    private Color progressColor = new Color(255, 70, 70); // Red
    private Color thumbColor = Color.WHITE;

    public CustomSliderSquareUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle trackBounds = trackRect;
        int cy = (trackBounds.height / 2) - (TRACK_HEIGHT / 2);

        // Background track
        g2d.setColor(trackColor);
        g2d.fillRoundRect(trackBounds.x, trackBounds.y + cy,
                trackBounds.width, TRACK_HEIGHT,
                TRACK_HEIGHT, TRACK_HEIGHT);

        // Progress track
        int thumbPos = thumbRect.x + (thumbRect.width / 2);
        int progressWidth = thumbPos - trackBounds.x;

        g2d.setColor(progressColor);
        g2d.fillRoundRect(trackBounds.x, trackBounds.y + cy,
                progressWidth, TRACK_HEIGHT,
                TRACK_HEIGHT, TRACK_HEIGHT);
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = thumbRect.x + (thumbRect.width / 2) - (THUMB_WIDTH / 2);
        int y = thumbRect.y + (thumbRect.height / 2) - (THUMB_HEIGHT / 2);

        // Rounded rectangle thumb
        g2d.setColor(thumbColor);
        g2d.fillRoundRect(x, y, THUMB_WIDTH, THUMB_HEIGHT, 6, 6);

        // Optional: border
        g2d.setColor(new Color(200, 200, 200));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x, y, THUMB_WIDTH, THUMB_HEIGHT, 6, 6);
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(THUMB_WIDTH, THUMB_HEIGHT);
    }
}