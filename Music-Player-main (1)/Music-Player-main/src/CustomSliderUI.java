import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class CustomSliderUI extends BasicSliderUI {
    private static final int TRACK_HEIGHT = 6;
    private static final int THUMB_SIZE = 18;
    private Color trackColor = new Color(60, 60, 60);
    private Color progressColor = new Color(205, 175, 52); // Spotify-like gold
    private Color thumbColor = Color.WHITE;

    public CustomSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle trackBounds = trackRect;
        int cy = (trackBounds.height / 2) - (TRACK_HEIGHT / 2);
        int trackY = trackBounds.y + cy;

        // Draw background track (gray)
        g2d.setColor(trackColor);
        RoundRectangle2D trackShape = new RoundRectangle2D.Double(
                trackBounds.x, trackY,
                trackBounds.width, TRACK_HEIGHT,
                TRACK_HEIGHT, TRACK_HEIGHT
        );
        g2d.fill(trackShape);

        // Draw progress track (colored part) - only up to thumb position
        int thumbCenter = thumbRect.x + (thumbRect.width / 2);
        int progressWidth = Math.max(0, thumbCenter - trackBounds.x);

        // Ensure progress doesn't exceed track bounds
        progressWidth = Math.min(progressWidth, trackBounds.width);

        if (progressWidth > 0) {
            g2d.setColor(progressColor);
            RoundRectangle2D progressShape = new RoundRectangle2D.Double(
                    trackBounds.x, trackY,
                    progressWidth, TRACK_HEIGHT,
                    TRACK_HEIGHT, TRACK_HEIGHT
            );
            g2d.fill(progressShape);
        }

        g2d.dispose();
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw circular thumb centered
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

        g2d.dispose();
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