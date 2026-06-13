package views.siswa.info;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;

/**
 * @author Rivaldi
 */
public class PanduanDaftar extends JFrame {

    public PanduanDaftar() {
        setTitle("Panduan Pendaftaran SPMB - SMP Negeri Harapan Jaya");
        setSize(700, 650); // Ukuran sedikit lebih tinggi agar lega
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Color hijauTua = new Color(45, 90, 65);
        Color hijauTombol = new Color(95, 178, 130);

        // Panel Utama
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(240, 247, 244));
        setContentPane(mainPanel);

        // Card Panel Putih
        JPanel card = new JPanel(null);
        card.setBounds(40, 30, 610, 540);
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(210, 220, 215), 1));
        mainPanel.add(card);

        // Header Panduan
        JLabel title = new JLabel("Alur Pendaftaran Siswa Baru", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Ukuran judul diperbesar
        title.setForeground(hijauTua);
        title.setBounds(0, 30, 610, 40);
        card.add(title);

        // Menggunakan JTextPane agar teks bisa wrap dan menggunakan HTML untuk styling
        JTextPane txtPane = new JTextPane();
        txtPane.setContentType("text/html");
        txtPane.setEditable(false);
        txtPane.setOpaque(false); // Agar background tetap putih mengikuti card
        
        // CSS untuk mengatur ukuran font dan jarak antar baris
        String htmlContent = "<html><body style='font-family: Segoe UI; font-size: 13px; color: #333333; margin: 20px;'>"
                + "<div style='line-height: 1.6;'>"
                + "<b>1. Registrasi Akun</b><br>"
                + "Klik tombol 'Daftar Sekarang' pada halaman utama dashboard.<br><br>"
                + "<b>2. Pengisian Formulir</b><br>"
                + "Lengkapi seluruh data diri, NISN, dan asal sekolah dengan benar.<br><br>"
                + "<b>3. Unggah Dokumen</b><br>"
                + "Unggah pindaian (scan) Kartu Keluarga dan Ijazah asli Anda.<br><br>"
                + "<b>4. Verifikasi Data</b><br>"
                + "Tunggu verifikasi berkas oleh admin sekolah dalam 1x24 jam.<br><br>"
                + "<b>5. Pembayaran</b><br>"
                + "Lakukan pembayaran administrasi setelah status akun diverifikasi.<br><br>"
                + "<b>6. Cetak Kartu</b><br>"
                + "Login kembali untuk mencetak kartu ujian masuk sekolah."
                + "</div></body></html>";

        txtPane.setText(htmlContent);
        
        // Letakkan JTextPane di dalam JScrollPane agar jika teks panjang bisa di-scroll
        JScrollPane scrollPane = new JScrollPane(txtPane);
        scrollPane.setBounds(40, 90, 530, 340);
        scrollPane.setBorder(null); // Hilangkan border scroll agar menyatu dengan card
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        card.add(scrollPane);

        // Tombol Mengerti
        JButton btnOke = new JButton("SAYA MENGERTI");
        btnOke.setBounds(215, 460, 180, 45);
        btnOke.setBackground(hijauTombol);
        btnOke.setForeground(Color.WHITE);
        btnOke.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOke.setOpaque(true);
        btnOke.setContentAreaFilled(true);
        btnOke.setBorderPainted(false);
        btnOke.setFocusPainted(false);
        btnOke.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnOke.addActionListener(e -> this.dispose());
        card.add(btnOke);
    }
}