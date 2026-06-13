package views.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * CustomPanel
 * Panel UI dengan background rounded (untuk card / container modern)
 */
public class CustomPanel extends JPanel {

    private int radius = 18;
    private Color backgroundColor = Color.WHITE;

    public CustomPanel() {
        setOpaque(false);
    }

    public CustomPanel(Color bg) {
        this.backgroundColor = bg;
        setOpaque(false);
    }

    public void setBackgroundColor(Color bg) {
        this.backgroundColor = bg;
        repaint();
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // background rounded
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Float(
                0, 0, getWidth(), getHeight(),
                radius, radius
        ));

        g2.dispose();
        super.paintComponent(g);
    }
}