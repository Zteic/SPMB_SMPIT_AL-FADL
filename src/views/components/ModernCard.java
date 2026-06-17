package views.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * Dioptimalkan: Efek transparan tanpa menghilangkan Icon Unicode dan Teks Menu
 */
public class ModernCard extends JButton {
    private int radius;
    private Color bgColor;
    private String iconUnicode = "";
    private String textLabel = "";
    private boolean isHovered = false;
    
    // Border abu-abu transparan tipis
    private static final Color BORDER_DEFAULT = new Color(180, 185, 190, 150); 
    private static final Color BORDER_HOVER = new Color(46, 125, 50); 
    // Saat di-hover, buat warna hijau transparan yang lebih pekat agar kontras
    private static final Color BG_HOVER = new Color(220, 240, 225, 235); 

    public ModernCard(int radius, Color bgColor) {
        super();
        this.radius = radius;
        this.bgColor = bgColor;
        initCardSettings();
    }

    public void setCardContent(String iconUnicode, String textLabel) {
        this.iconUnicode = iconUnicode;
        this.textLabel = textLabel;
        repaint();
    }

    private void initCardSettings() {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int yOffset = isHovered ? 0 : 2;
        
        // 1. Gambar Soft Shadow tipis di paling bawah card
        g2.setColor(new Color(0, 0, 0, 12));
        g2.fillRoundRect(1, 4, getWidth() - 2, getHeight() - 4, radius, radius);
        
        // 2. Atur Composite khusus transparan HANYA untuk background kotak kartu
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        if (isHovered) {
            g2.setColor(BG_HOVER);
        } else {
            g2.setColor(bgColor); // Menggunakan warna transparan yang dikirim dari LandingPage
        }
        g2.fillRoundRect(0, yOffset, getWidth() - 1, getHeight() - 3, radius, radius);
        
        // 3. Gambar Garis Border Kotak Kartu
        if (isHovered) {
            g2.setColor(BORDER_HOVER);
            g2.setStroke(new BasicStroke(1.5f));
        } else {
            g2.setColor(BORDER_DEFAULT);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawRoundRect(0, yOffset, getWidth() - 1, getHeight() - 3, radius, radius);

        // 4. KUNCI PERBAIKAN: Reset composite ke SOLID (100% Opaque) khusus untuk teks dan ikon 
        // agar warna tulisan tidak ikut pudar/hilang karena background transparan
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // Gambar Unicode Icon (Warna mengikuti hijau tua / default agar kontras)
        if (!iconUnicode.isEmpty()) {
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26)); // Diperbesar sedikit agar pas
            g2.setColor(isHovered ? BORDER_HOVER : new Color(45, 90, 65));
            FontMetrics fmIcon = g2.getFontMetrics();
            int iconX = (getWidth() - fmIcon.stringWidth(iconUnicode)) / 2;
            g2.drawString(iconUnicode, iconX, yOffset + 38);
        }

        // Gambar Deskripsi Teks di bagian bawah kartu
        if (!textLabel.isEmpty()) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            // Warna teks diatur ke abu-abu gelap kehitaman (#1E293B) supaya sangat terbaca di atas background transparan
            g2.setColor(new Color(30, 41, 59)); 
            FontMetrics fmText = g2.getFontMetrics();
            int textX = (getWidth() - fmText.stringWidth(textLabel)) / 2;
            g2.drawString(textLabel, textX, yOffset + 68);
        }

        g2.dispose();
    }
}
