package views.siswa.info;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Menampilkan Gambar Program Unggulan secara Full Size
 */
public class ProgramUnggulan extends JFrame {

    public ProgramUnggulan() {
        setTitle("Program Unggulan SMPIT AL FADL");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Kita kunci resizable agar ukuran jendela pas dengan gambar asli
        setResizable(false); 

        // Warna Tema Hijau
        Color hijauTombol = new Color(95, 178, 130);

        // --- MENGAMBIL GAMBAR DARI PACKAGE pmb ---
        URL imgURL = getClass().getResource("/pmb/program_unggulan.png");
        
        if (imgURL != null) {
            ImageIcon iconAsli = new ImageIcon(imgURL);
            
            // Ambil ukuran asli gambar (Pixel)
            int imgWidth = iconAsli.getIconWidth();
            int imgHeight = iconAsli.getIconHeight();

            // Set ukuran jendela: Lebar gambar, Tinggi gambar + 85 pixel (untuk tombol di bawah)
            setSize(imgWidth, imgHeight + 85);
            setLocationRelativeTo(null); // Muncul di tengah layar

            // Panel Utama
            JPanel mainPanel = new JPanel(new BorderLayout());
            setContentPane(mainPanel);

            // Container Gambar menggunakan JScrollPane
            // Berguna jika resolusi layar pengguna lebih kecil dari resolusi gambar
            JLabel lblGambar = new JLabel(iconAsli);
            lblGambar.setHorizontalAlignment(JLabel.CENTER);
            
            JScrollPane scroll = new JScrollPane(lblGambar);
            scroll.setBorder(null);
            // Mengatur kecepatan scroll agar halus
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.getHorizontalScrollBar().setUnitIncrement(16);
            mainPanel.add(scroll, BorderLayout.CENTER);

            // --- TOMBOL TUTUP DI BAGIAN BAWAH ---
            JButton btnTutup = new JButton("KEMBALI");
            btnTutup.setPreferredSize(new Dimension(0, 55)); // Tinggi tombol 55px
            btnTutup.setBackground(hijauTombol);
            btnTutup.setForeground(hijauTombol);
            btnTutup.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnTutup.setOpaque(true);
            btnTutup.setContentAreaFilled(true);
            btnTutup.setBorderPainted(false);
            btnTutup.setFocusPainted(false);
            btnTutup.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Aksi saat tombol diklik (Menutup jendela ini saja)
            btnTutup.addActionListener(e -> this.dispose());
            mainPanel.add(btnTutup, BorderLayout.SOUTH);

        } else {
            // Pesan Error jika file gambar tidak ditemukan
            setSize(600, 1000);
            setLocationRelativeTo(null);
            JOptionPane.showMessageDialog(this, 
                "File 'program_unggulan.png' tidak ditemukan di folder src/pmb/!", 
                "Error: Gambar Hilang", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }
}
