package views.siswa.info;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 */
public class InfoPendaftaran extends JFrame {

    public InfoPendaftaran() {
        setTitle("Informasi Resmi PPDB - SMPIT AL FADL");
        setSize(650, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Color hijauTua = new Color(45, 90, 65);
        Color hijauTombol = new Color(95, 178, 130);

        // Panel Utama dengan BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // --- Container untuk konten yang bisa di-scroll ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 1. GAMBAR HEADER
        URL imgURL = getClass().getResource("/pmb/InfoPendaftaran.png"); // Ganti dengan nama file gambarmu
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            // Scale gambar agar lebarnya pas dengan window (sekitar 550px)
            Image scaledImg = icon.getImage().getScaledInstance(550, 200, Image.SCALE_SMOOTH);
            JLabel lblHeader = new JLabel(new ImageIcon(scaledImg));
            lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(lblHeader);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        // 2. TEKS INFORMASI (MENGGUNAKAN HTML UNTUK STYLING)
        JTextPane txtInfo = new JTextPane();
        txtInfo.setContentType("text/html");
        txtInfo.setEditable(false);
        txtInfo.setOpaque(false);

        String htmlContent = "<html><body style='font-family: Segoe UI; color: #333333;'>"
                + "<div style='background-color: #F0F7F4; padding: 10px; border: 1px solid #D2E1DC;'>"
                + "<b>GELOMBANG 2: Maret – Juli</b><br>"
                + "<span style='color: red;'><b>KUOTA TERBATAS</b></span>"
                + "</div><br>"
                + "<b>Waktu Pelayanan:</b><br>"
                + "Senin – Jum’at: 08.00 s/d 15.00<br>"
                + "Sabtu: 08.00 s/d 13.00<br><br>"
                + "<b>Informasi & Pendaftaran Online:</b><br>"
                + "• Miss Henny: 0878-8608-1387<br>"
                + "• Miss Irni: 0832-1346-4797<br>"
                + "• Miss Leila: 0857-1788-4708<br><br>"
                + "<b>Alamat Sekolah:</b><br>"
                + "Jl. Raya Abdul Gani No. 33, Perum Pabuaran Indah,<br>"
                + "Kel. Pabuaran Mekar, Kec. Cibinong, Kab. Bogor<br><br>"
                + "<div style='font-size: 10px; color: #777777;'>"
                + "Informasi Resmi PPDB SMP IT AL FADL dapat diakses melalui website resmi Al Fadl Islamic School<br>"
                + "🌐 www.alfadlislamicschool.sch.id | 📷 @alfadlislamicschool<br>"
                + "🎵 alfadlislamicschool | 📌 SMP IT AL FADL"
                + "</div>"
                + "</body></html>";

        txtInfo.setText(htmlContent);
        contentPanel.add(txtInfo);

        // JScrollPane agar konten bisa di-scroll
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Tombol Tutup di bagian bawah
        JButton btnTutup = new JButton("KEMBALI");
        btnTutup.setPreferredSize(new Dimension(0, 50));
        btnTutup.setBackground(hijauTombol);
        btnTutup.setForeground(hijauTombol);
        btnTutup.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTutup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTutup.setBorderPainted(false);
        btnTutup.addActionListener(e -> this.dispose());
        mainPanel.add(btnTutup, BorderLayout.SOUTH);
    }
}
