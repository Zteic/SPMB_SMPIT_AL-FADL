package views.siswa.info;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.swing.border.LineBorder;

/**
 * Jendela Otomatis Menyesuaikan Ukuran Asli Gambar Syarat Pendaftaran
 */
public class SyaratPendaftaran extends JFrame {

    public SyaratPendaftaran() {
        setTitle("Gambar Syarat Pendaftaran PPDB");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true); // Biarkan user bisa resize jika perlu

        // Warna Tema
        Color hijauTua = new Color(45, 90, 65);
        Color hijauTombol = new Color(95, 178, 130);

        // --- MENGAMBIL GAMBAR DARI PACKAGE pmb ---
        URL imgURL = getClass().getResource("/pmb/syarat_pmb.png");
        
        if (imgURL != null) {
            ImageIcon iconAsli = new ImageIcon(imgURL);
            Image img = iconAsli.getImage();

            // --- HITUNG UKURAN JENDELA OTOMATIS ---
            int imgWidth = img.getWidth(null);
            int imgHeight = img.getHeight(null);

            // Batas maksimal window agar tidak melebihi layar monitor
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            int maxWidth = (int)(screen.width * 0.9);
            int maxHeight = (int)(screen.height * 0.9);

            // Jika gambar lebih besar dari layar, gunakan batas maksimal
            int winWidth = Math.min(imgWidth, maxWidth);
            int winHeight = Math.min(imgHeight, maxHeight);

            setSize(winWidth, winHeight);
            setLocationRelativeTo(null); // Center screen

            // Panel Utama dengan Scroll
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(240, 247, 244)); // Background hijau muda tipis
            setContentPane(mainPanel);

            // Container untuk Gambar
            JPanel imgContainer = new JPanel(new GridBagLayout()); // GridBagLayout agar gambar di tengah
            imgContainer.setBackground(Color.WHITE);
            imgContainer.setBorder(new LineBorder(new Color(220, 230, 225), 2));

            // JLabel yang memegang Gambar Asli (Tidak Di-scale)
            JLabel lblGambar = new JLabel(iconAsli);
            imgContainer.add(lblGambar);

            // JScrollPane agar gambar bisa di-scroll jika lebih besar dari window
            JScrollPane scroll = new JScrollPane(imgContainer);
            scroll.setBorder(null); // Hilangkan border scrollbar agar rapi
            scroll.getVerticalScrollBar().setUnitIncrement(16); // Scroll lebih mulus
            scroll.getHorizontalScrollBar().setUnitIncrement(16);
            mainPanel.add(scroll, BorderLayout.CENTER);

            // Panel Bawah (Tombol Tutup)
            JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
            botPanel.setOpaque(false);
            
            JButton btnTutup = new JButton("TUTUP");
            btnTutup.setPreferredSize(new Dimension(200, 45));
            btnTutup.setBackground(Color.WHITE);
            btnTutup.setForeground(hijauTombol);
            btnTutup.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnTutup.setOpaque(true);
            btnTutup.setContentAreaFilled(true);
            btnTutup.setBorderPainted(false);
            btnTutup.setFocusPainted(false);
            btnTutup.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btnTutup.addActionListener(e -> this.dispose());
            botPanel.add(btnTutup);
            mainPanel.add(botPanel, BorderLayout.SOUTH);

        } else {
            // Jika gambar tidak ditemukan
            setSize(400, 300);
            setLocationRelativeTo(null);
            JOptionPane.showMessageDialog(this, 
                "File gambar 'syarat_pmb.png' tidak ditemukan di package pmb!", 
                "Error Gambar", JOptionPane.ERROR_MESSAGE);
            this.dispose(); // Langsung tutup frame jika gambar error
        }
    }
}
