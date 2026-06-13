package views.components;

import javax.swing.*;
import java.awt.*;

/**
 * MetricCard
 * Komponen kartu statistik untuk dashboard SPMB
 */
public class MetricCard extends JPanel {

    private JLabel lblTitle;
    private JLabel lblValue;
    private Color backgroundColor;

    public MetricCard(String title, String value, Color bg) {
        this.backgroundColor = bg;

        setLayout(new GridLayout(2, 1, 5, 5));
        setOpaque(false); // penting agar paintComponent yang handle background

        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // TITLE
        lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(245, 245, 245));
        add(lblTitle);

        // VALUE
        lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(Color.WHITE);
        add(lblValue);
    }

    /**
     * Update nilai metrik secara realtime
     */
    public void setText(String value) {
        if (lblValue != null) {
            lblValue.setText(value);
            lblValue.revalidate();
            lblValue.repaint();
        }
    }

    /**
     * Background rounded card
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // shadow sederhana (biar tidak flat banget)
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

        g2.dispose();
        super.paintComponent(g);
    }
}