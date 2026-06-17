package views.siswa.info;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;

/**
 * Class FAQ untuk menampilkan pertanyaan umum pendaftaran
 */
public class FAQ extends JFrame {

    public FAQ() {
        setTitle("FAQ - Pertanyaan Umum PPDB");
        setSize(700, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Color hijauTua = new Color(45, 90, 65);
        Color hijauTombol = new Color(46, 125, 50); // Disamakan dengan clrHijauMain agar estetik

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

        // Header FAQ
        JLabel title = new JLabel("Frequently Asked Questions (FAQ)", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(hijauTua);
        title.setBounds(0, 30, 610, 40);
        card.add(title);

        // Area Teks FAQ dengan JTextPane agar rapi dan bisa scroll
        JTextPane txtPane = new JTextPane();
        txtPane.setContentType("text/html");
        txtPane.setEditable(false);
        txtPane.setOpaque(false);
        
        String htmlContent = "<html><body style='font-family: Segoe UI; font-size: 12px; color: #1e293b; margin: 15px;'>"
                + "<div style='line-height: 1.7;'>"
                
                + "<p style='margin-bottom: 15px;'>"
                + "<b>Q: Jalur pendaftaran apa saja yang tersedia di SPMB SMPIT AL FADL?</b><br>"
                + "<span style='color: #475569;'>A: Kami membuka 5 jalur pendaftaran resmi yang terintegrasi langsung dengan kuota sistem, yaitu: "
                + "<b>Domisili, Prestasi, Afirmasi, Mutasi, dan Yatim</b>.</span>"
                + "</p>"
                
                + "<p style='margin-bottom: 15px;'>"
                + "<b>Q: Bagaimana sistem pembagian Gelombang Pendaftaran?</b><br>"
                + "<span style='color: #475569;'>A: Pendaftaran dibuka dalam beberapa gelombang bertahap (Gelombang 1, 2, dst). "
                + "Akun pendaftaran Anda akan secara otomatis dimasukkan ke dalam gelombang pendaftaran yang sedang berstatus "
                + "<b>AKTIF / BUKA</b> oleh Panitia Pusat saat Anda melakukan submit formulir.</span>"
                + "</p>"
                
                + "<p style='margin-bottom: 15px;'>"
                + "<b>Q: Berapa batas maksimal ukuran dokumen berkas yang diunggah?</b><br>"
                + "<span style='color: #475569;'>A: Berkas persyaratan wajib diunggah dalam format gambar (JPG/PNG) atau PDF dengan ukuran "
                + "<b>maksimal 5MB per file</b>. Pastikan struktur dokumen tidak rusak agar terbaca di Cloud Preview sistem verifikasi admin.</span>"
                + "</p>"
                
                + "<p style='margin-bottom: 15px;'>"
                + "<b>Q: Kapan dan di mana hasil seleksi kelulusan diumumkan?</b><br>"
                + "<span style='color: #475569;'>A: Hasil kelulusan dihitung secara transparan oleh Engine Seleksi sekolah. "
                + "Anda dapat memantau pergeseran ranking dan status final pendaftaran Anda (<b>DITERIMA / CADANGAN / TIDAK DITERIMA</b>) "
                + "secara langsung dengan melakukan login ke Dashboard Akun Siswa masing-masing.</span>"
                + "</p>"
                
                + "<p style='margin-bottom: 15px;'>"
                + "<b>Q: Apa yang harus dilakukan jika berkas saya berstatus DITOLAK?</b><br>"
                + "lahu gunakan tombol <b>Upload Ulang Berkas</b> agar dokumen Anda masuk kembali ke antrean verifikasi admin.</span>"
                + "</p>"
                
                + "</div></body></html>";

        txtPane.setText(htmlContent);
        
        JScrollPane scrollPane = new JScrollPane(txtPane);
        scrollPane.setBounds(30, 90, 550, 340);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        card.add(scrollPane);

        // Tombol Kembali
        JButton btnOke = new JButton("SAYA MENGERTI");
        btnOke.setBounds(195, 460, 220, 45);
        btnOke.setBackground(hijauTombol);
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
