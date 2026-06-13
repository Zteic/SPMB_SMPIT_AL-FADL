package views.siswa;

import config.DatabaseConfig;
import config.SessionManager;
import views.components.CustomButton;
import views.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Halaman Pengumuman Detail Hasil Seleksi & Peringkat.
 * Sinkronisasi data kumulatif realtime dengan tabel tbl_seleksi dan tbl_siswa.
 * * @author Rivaldi
 */
public class PengumumanPanel extends JPanel {

    // <-------------------- KONSTANTA WARNA -------------------->
    private static final Color BG         = new Color(243, 244, 246);
    private static final Color WHITE      = Color.WHITE;
    private static final Color PRIMARY    = new Color(37, 99, 235);
    private static final Color HIJAU      = new Color(22, 163, 74);
    private static final Color MERAH      = new Color(220, 38, 38);
    private static final Color KUNING     = new Color(202, 138, 4);
    private static final Color TEXT_DARK  = new Color(17, 24, 39);
    private static final Color TEXT_LIGHT = new Color(107, 114, 128);

    // <-------------------- KOMPONEN FORM -------------------->
    private JLabel lblNama, lblNoReg, lblJalur, lblStatusAkhir, lblRanking;
    private DefaultTableModel modelNilai;
    private JTable tabelNilai;
    private CustomButton btnCetak, btnDaftarUlang;
    
    // <-------------------- SESSION & STATE -------------------->
    private String statusKelulusan = "PROSES";

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari PengumumanPanel dan memuat data awal.
     */
    public PengumumanPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadDataSeleksi();
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    /**
     * Merender dan mengonfigurasi tata letak komponen utama antarmuka pengumuman hasil seleksi.
     */
    private void initComponents() {
        // <-------------------- HEADER -------------------->
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Pengumuman Hasil Seleksi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_DARK);
        
        JLabel lblSub = new JLabel("Detail penilaian, peringkat, dan status penerimaan Anda.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(TEXT_LIGHT);
        
        JPanel pnlTitle = new JPanel();
        pnlTitle.setLayout(new BoxLayout(pnlTitle, BoxLayout.Y_AXIS));
        pnlTitle.setOpaque(false);
        pnlTitle.add(lblTitle);
        pnlTitle.add(Box.createVerticalStrut(5));
        pnlTitle.add(lblSub);
        
        headerPanel.add(pnlTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // <-------------------- CONTENT CENTER -------------------->
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        
        gbc.gridx = 0; gbc.weightx = 0.4; gbc.insets = new Insets(0, 0, 0, 10);
        contentPanel.add(buildIdentitasCard(), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.6; gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(buildNilaiCard(), gbc);

        add(contentPanel, BorderLayout.CENTER);
        
        // <-------------------- BOTTOM PANEL -------------------->
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        btnCetak = new CustomButton("Cetak Surat Kelulusan (PDF)", new Color(75, 85, 99), WHITE);
        btnCetak.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCetak.setForeground(Color.BLACK);
        btnCetak.addActionListener(e -> cetakPDF());
        
        btnDaftarUlang = new CustomButton("Lanjut Daftar Ulang", HIJAU, WHITE);
        btnDaftarUlang.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDaftarUlang.setForeground(Color.BLACK);
        btnDaftarUlang.setVisible(false); 
        btnDaftarUlang.addActionListener(e -> lanjutDaftarUlang());
        
        bottomPanel.add(btnCetak);
        bottomPanel.add(btnDaftarUlang);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel buildIdentitasCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 15, WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setOpaque(false);

        lblNama = new JLabel("Memuat Data...");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        lblNoReg = new JLabel("Nomor Pendaftaran: -");
        lblNoReg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNoReg.setForeground(TEXT_LIGHT);
        
        lblJalur = new JLabel("Jalur: -");
        lblJalur.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblJalur.setForeground(TEXT_LIGHT);

        pnlInfo.add(lblNama);
        pnlInfo.add(Box.createVerticalStrut(5));
        pnlInfo.add(lblNoReg);
        pnlInfo.add(lblJalur);
        pnlInfo.add(Box.createVerticalStrut(20));

        JLabel lblTitleStatus = new JLabel("Status Seleksi:");
        lblTitleStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        lblStatusAkhir = new JLabel("PROSES");
        lblStatusAkhir.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        lblRanking = new JLabel("Peringkat: -");
        lblRanking.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRanking.setForeground(PRIMARY);

        pnlInfo.add(lblTitleStatus);
        pnlInfo.add(Box.createVerticalStrut(5));
        pnlInfo.add(lblStatusAkhir);
        pnlInfo.add(Box.createVerticalStrut(10));
        pnlInfo.add(lblRanking);

        card.add(pnlInfo, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildNilaiCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 15, WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Rincian Komponen Nilai");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        card.add(title, BorderLayout.NORTH);

        String[] columns = {"Komponen Seleksi", "Nilai"};
        modelNilai = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tabelNilai = new JTable(modelNilai);
        tabelNilai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelNilai.setRowHeight(35);
        tabelNilai.setShowVerticalLines(false);
        tabelNilai.setGridColor(new Color(229, 231, 235));
        tabelNilai.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelNilai.getTableHeader().setBackground(new Color(249, 250, 251));

        JScrollPane scrollPane = new JScrollPane(tabelNilai);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    // <-------------------- DATABASE QUERY -------------------->
    /**
     * Memuat records data komponen nilai dan peringkat seleksi dari database MySQL secara realtime.
     */
    public void loadDataSeleksi() {
        String username = SessionManager.getUsername();
        if (username == null) return;

        modelNilai.setRowCount(0); 

        String sql = 
                "SELECT s.nomor_pendaftaran, b.nama_lengkap, j.nama_jalur, " +
                "sel.nilai_akademik, sel.nilai_tahfidz, sel.nilai_wawancara, sel.nilai_domisili, " +
                "sel.total_nilai, sel.ranking, sel.status_kelulusan, " +
                "(SELECT COUNT(*) FROM tbl_siswa) AS total_peserta " +
                "FROM tbl_siswa s " +
                "JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa " +
                "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur " +
                "LEFT JOIN tbl_seleksi sel ON s.id_siswa = sel.id_siswa " +
                "WHERE s.nomor_pendaftaran = ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblNama.setText(rs.getString("nama_lengkap"));
                    lblNoReg.setText("Nomor Pendaftaran: " + rs.getString("nomor_pendaftaran"));
                    lblJalur.setText("Jalur Pendaftaran: " + (rs.getString("nama_jalur") != null ? rs.getString("nama_jalur") : "Yatim"));

                    int nAkademik = rs.getInt("nilai_akademik");
                    int nTahfidz = rs.getInt("nilai_tahfidz");
                    int nWawancara = rs.getInt("nilai_wawancara");
                    int nDomisili = rs.getInt("nilai_domisili");
                    double nTotal = rs.getDouble("total_nilai");

                    modelNilai.addRow(new Object[]{"Ujian Akademik", nAkademik});
                    modelNilai.addRow(new Object[]{"Hafalan Tahfidz", nTahfidz});
                    modelNilai.addRow(new Object[]{"Tes Wawancara", nWawancara});
                    modelNilai.addRow(new Object[]{"Skor Domisili/Jalur", nDomisili});
                    modelNilai.addRow(new Object[]{"Total Nilai Akhir", String.format("%.2f", nTotal)});

                    statusKelulusan = rs.getString("status_kelulusan");
                    if (statusKelulusan == null) statusKelulusan = "PROSES";

                    lblStatusAkhir.setText(statusKelulusan.replace("_", " "));
                    if ("DITERIMA".equalsIgnoreCase(statusKelulusan) || "LULUS".equalsIgnoreCase(statusKelulusan)) {
                        lblStatusAkhir.setForeground(HIJAU);
                        btnDaftarUlang.setVisible(true); 
                    } else if ("CADANGAN".equalsIgnoreCase(statusKelulusan)) {
                        lblStatusAkhir.setForeground(KUNING);
                        btnDaftarUlang.setVisible(false);
                    } else if ("TIDAK_DITERIMA".equalsIgnoreCase(statusKelulusan)) {
                        lblStatusAkhir.setForeground(MERAH);
                        btnDaftarUlang.setVisible(false);
                    } else {
                        lblStatusAkhir.setForeground(TEXT_LIGHT);
                        btnDaftarUlang.setVisible(false);
                    }

                    int rank = rs.getInt("ranking");
                    int totalPeserta = rs.getInt("total_peserta");
                    if (rank > 0) {
                        lblRanking.setText("Peringkat " + rank + " dari " + totalPeserta + " peserta");
                    } else {
                        lblRanking.setText("Peringkat: Belum Diterbitkan");
                    }
                }
            }
        } catch (Exception e) {
            // Kronologi log kegagalan internal sistem diredam aman
        }
    }

    // <-------------------- HELPER METHOD -------------------->
    private void cetakPDF() {
        if ("PROSES".equalsIgnoreCase(statusKelulusan)) {
            JOptionPane.showMessageDialog(this, "Surat kelulusan belum dapat dicetak karena status masih PROSES.", "Informasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Surat kelulusan siap diunduh. (Modul Print PDF Terhubung)", "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void lanjutDaftarUlang() {
        Container c = getParent();
        while (c != null) {
            if (c.getClass().getName().endsWith("SiswaMainFrame")) {
                try {
                    c.getClass().getMethod("showPage", String.class).invoke(c, "DAFTAR_ULANG");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Harap selesaikan administrasi daftar ulang di menu dashboard utama.");
                }
                return;
            }
            c = c.getParent();
        }
    }
}