package views.siswa.info;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * @author Rivaldi
 */
public class Fasilitas extends JFrame {

    public Fasilitas() {
        setTitle("Fasilitas SMPIT AL FADL");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Kita matikan resizable agar ukuran tetap konsisten dengan gambar asli
        setResizable(true); 

        // Warna Tema
        Color hijauTombol = new Color(95, 178, 130);

        // --- MENGAMBIL GAMBAR DARI PACKAGE pmb ---
        URL imgURL = getClass().getResource("/pmb/fasilitas_sekolah.png");
        
        if (imgURL != null) {
            ImageIcon iconAsli = new ImageIcon(imgURL);
            
            // --- AMBIL UKURAN ASLI GAMBAR ---
            int imgWidth = iconAsli.getIconWidth();
            int imgHeight = iconAsli.getIconHeight();

            // Atur ukuran jendela tepat sesuai ukuran gambar + ruang untuk tombol di bawah
            // Kita tambah 60-70 pixel pada height untuk menampung tombol "KEMBALI"
            setSize(imgWidth, imgHeight + 100);
            setLocationRelativeTo(null);

            // Panel Utama
            JPanel mainPanel = new JPanel(new BorderLayout());
            setContentPane(mainPanel);

            // Container Gambar (Tanpa ScrollPane karena kita ingin Full Size)
            // Jika Anda tetap ingin ada Scroll buat berjaga-jaga di layar kecil, gunakan JScrollPane
            JLabel lblGambar = new JLabel(iconAsli);
            lblGambar.setHorizontalAlignment(JLabel.CENTER);
            
            // Kita tetap bungkus ScrollPane agar jika dibuka di monitor resolusi rendah 
            // gambarnya tidak hilang/terpotong keluar layar
            JScrollPane scroll = new JScrollPane(lblGambar);
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.getHorizontalScrollBar().setUnitIncrement(16);
            mainPanel.add(scroll, BorderLayout.CENTER);

            // Tombol Tutup di Bawah
            JButton btnTutup = new JButton("KEMBALI");
            btnTutup.setPreferredSize(new Dimension(0, 50));
            btnTutup.setBackground(hijauTombol);
            btnTutup.setForeground(hijauTombol);
            btnTutup.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnTutup.setOpaque(true);
            btnTutup.setContentAreaFilled(true);
            btnTutup.setBorderPainted(false);
            btnTutup.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btnTutup.addActionListener(e -> this.dispose());
            mainPanel.add(btnTutup, BorderLayout.SOUTH);

        } else {
            setSize(550, 550);
            setLocationRelativeTo(null);
            JOptionPane.showMessageDialog(this, "File 'fasilitas_sekolah.png' tidak ditemukan!");
            this.dispose();
        }
    }
}