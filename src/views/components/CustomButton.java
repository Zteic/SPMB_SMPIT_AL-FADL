package views.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Dioptimalkan dengan dukungan Background Transparan (Alpha channel)
 */
public class CustomButton extends JButton {
    private int radius = 16;
    private Color bgWarna;
    private Color fgWarna;
    private Color hoverWarna;
    private Color borderWarna = null;
    private boolean isHovered = false;

    public CustomButton(String teks, Color bg, Color fg) {
        super(teks);
        this.bgWarna = bg;
        this.fgWarna = fg;
        this.hoverWarna = bg;
        
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(fgWarna);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public void setHoverColor(Color hoverWarna) {
        this.hoverWarna = hoverWarna;
    }

    public void setCustomBorder(Color borderWarna) {
        this.borderWarna = borderWarna;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Atur blending mode khusus untuk warna transparan transparan agar tidak menumpuk aneh
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        // Efek Drop Shadow Halus
        if (radius >= 20 && bgWarna.getAlpha() == 255 && bgWarna != Color.WHITE) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fill(new RoundRectangle2D.Float(0, 3, getWidth(), getHeight() - 3, radius, radius));
        }

        // Penentuan warna background transparan / hover
        if (getModel().isPressed()) {
            g2.setColor(bgWarna.darker());
        } else if (isHovered) {
            g2.setColor(hoverWarna);
        } else {
            g2.setColor(bgWarna);
        }
        
        int yOffset = (radius >= 20 && bgWarna.getAlpha() == 255 && bgWarna != Color.WHITE) ? 3 : 0;
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() - yOffset, radius, radius));
        
        // Menggambar Border
        if (borderWarna != null) {
            g2.setColor(isHovered ? hoverWarna : borderWarna);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2 - yOffset, radius, radius));
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
