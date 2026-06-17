package views.admin;

import config.DatabaseConfig;
import controllers.AutentikasiController;
import config.SessionManager;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import views.admin.EditDataSiswaDialog;

/**
 * Menggunakan arsitektur MVC dan JDBC terintegrasi ke MySQL.
 * * @author Rivaldi
 */
public class KelolaPendaftarPanel extends JPanel {

    // <-------------------- KOMPONEN FORM -------------------->
    private DefaultTableModel tableModel;
    private JTable tblPendaftar;
    private JScrollPane scrollPane;
    private JTextField txtCari;
    private JComboBox<String> cmbKategori; 
    private JComboBox<String> cmbFilterKelulusan;
    private JButton btnRefresh;
    private JButton btnHapus;
    private JButton btnDetail;
    private JButton btnEditData;
    
    // <-------------------- CONTROLLER -------------------->
    private AutentikasiController authController;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari KelolaPendaftarPanel.
     */
    public KelolaPendaftarPanel() {
        authController = new AutentikasiController();
        initEnhancedUI(); 
        muatDataPendaftar(); 
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    /**
     * Merender ulang antarmuka dengan filter bar multi-kategori dinamis.
     */
    private void initEnhancedUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250)); 
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25)); 

        // <-------------------- PANEL ATAS -------------------->
        JPanel panelUtara = new JPanel(new BorderLayout(0, 15));
        panelUtara.setBackground(new Color(245, 246, 250));

        JLabel lblJudul = new JLabel("MANAJEMEN KELOLA DATA PENDAFTAR");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblJudul.setForeground(new Color(47, 53, 66));
        panelUtara.add(lblJudul, BorderLayout.NORTH);

        JPanel panelKendali = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelKendali.setBackground(new Color(245, 246, 250));

        JLabel lblCari = new JLabel("Cari Pendaftar:");
        lblCari.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCari.setForeground(new Color(47, 53, 66));

        String[] listKategori = {"Semua Kategori", "No Daftar", "Nama", "Jalur", "Asal Sekolah"};
        cmbKategori = new JComboBox<>(listKategori);
        cmbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbKategori.setPreferredSize(new Dimension(140, 35));
        cmbKategori.addActionListener(e -> {
            cariDataPendaftar(txtCari.getText(), cmbKategori.getSelectedItem().toString(), cmbFilterKelulusan.getSelectedItem().toString());
        });
        
        txtCari = new JTextField();
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCari.setPreferredSize(new Dimension(220, 35));
        txtCari.setToolTipText("Ketik kata kunci pencarian...");
        
        views.components.InputValidator.kunciHanyaAngka(txtCari);
        
        txtCari.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                cariDataPendaftar(txtCari.getText(), cmbKategori.getSelectedItem().toString(), cmbFilterKelulusan.getSelectedItem().toString());
            }
        });

        JLabel lblFilter = new JLabel("Filter Kelulusan:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFilter.setForeground(new Color(47, 53, 66));
        
        cmbFilterKelulusan = new JComboBox<>(new String[]{"Semua Status", "PROSES", "DITERIMA", "TIDAK_DITERIMA", "CADANGAN"});
        cmbFilterKelulusan.setPreferredSize(new Dimension(145, 35));
        cmbFilterKelulusan.addActionListener(e -> {
            cariDataPendaftar(txtCari.getText(), cmbKategori.getSelectedItem().toString(), cmbFilterKelulusan.getSelectedItem().toString());
        });

        btnRefresh = new JButton("Reset");
        btnRefresh.setPreferredSize(new Dimension(100, 35));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(148, 163, 184));
        btnRefresh.setForeground(Color.BLACK); 
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> {
            txtCari.setText("");
            cmbKategori.setSelectedIndex(0);
            cmbFilterKelulusan.setSelectedIndex(0);
            muatDataPendaftar();
        });

        panelKendali.add(lblCari);
        panelKendali.add(cmbKategori);
        panelKendali.add(txtCari);
        panelKendali.add(lblFilter);
        panelKendali.add(cmbFilterKelulusan);
        panelKendali.add(btnRefresh);

        panelUtara.add(panelKendali, BorderLayout.CENTER);
        add(panelUtara, BorderLayout.NORTH);

        // <-------------------- PANEL TENGAH -------------------->
        String[] kolom = {
            "No. Pendaftaran", "NIK Siswa", "Nama Lengkap", "Gender", 
            "Sekolah Asal", "Jalur Seleksi", "Total Nilai", "Status Akhir"
        };
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblPendaftar = new JTable(tableModel);
        tblPendaftar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblPendaftar.setRowHeight(35); 
        tblPendaftar.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblPendaftar.getTableHeader().setBackground(new Color(236, 240, 241));
        tblPendaftar.getTableHeader().setReorderingAllowed(false);

        scrollPane = new JScrollPane(tblPendaftar);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 221, 225), 1));
        add(scrollPane, BorderLayout.CENTER);

        // <-------------------- PANEL BAWAH -------------------->
        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelAksi.setBackground(new Color(245, 246, 250));

        btnEditData = new JButton("Edit Data");
        btnEditData.setPreferredSize(new Dimension(160, 40));
        btnEditData.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEditData.setBackground(new Color(52, 152, 219));
        btnEditData.setForeground(Color.BLACK); 
        btnEditData.setFocusPainted(false);
        btnEditData.addActionListener(e -> bukaDialogEditData());

        btnDetail = new JButton("Lihat Detail / Verifikasi");
        btnDetail.setPreferredSize(new Dimension(180, 40));
        btnDetail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDetail.setBackground(new Color(46, 204, 113)); 
        btnDetail.setForeground(Color.BLACK); 
        btnDetail.setFocusPainted(false);
        btnDetail.addActionListener(e -> openDetailEditDialog());

        btnHapus = new JButton("Hapus Data Terpilih");
        btnHapus.setPreferredSize(new Dimension(180, 40));
        btnHapus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHapus.setBackground(new Color(231, 76, 60)); 
        btnHapus.setForeground(Color.BLACK); 
        btnHapus.setFocusPainted(false);
        btnHapus.addActionListener(e -> eksekusiHapusData());

        panelAksi.add(btnEditData);
        panelAksi.add(btnDetail);
        panelAksi.add(btnHapus);
        add(panelAksi, BorderLayout.SOUTH);
    }

    // <-------------------- LOAD DATA SISWA -------------------->
    public void muatDataPendaftar() {
        cariDataPendaftar("", "Semua Kategori", "Semua Status");
    }

    // <-------------------- DATABASE QUERY -------------------->
    /**
     * Menyaring dan menampilkan data pendaftar dari database berdasarkan parameter input.
     * * @param keyword kata kunci pencarian
     * @param kategori kategori kolom filter pencarian
     * @param statusKelulusan status kelulusan pendaftar
     */
    public void cariDataPendaftar(String keyword, String kategori, String statusKelulusan) {
        tableModel.setRowCount(0);
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return;
        
        String sql = 
                "SELECT s.nomor_pendaftaran, b.nik, b.nama_lengkap, b.jenis_kelamin, COALESCE(sa.nama_sekolah, '-') AS sekolah_asal, " +
                "COALESCE(j.nama_jalur, '-') AS nama_jalur, COALESCE(sl.total_nilai, 0) as total_nilai, COALESCE(sl.status_kelulusan, 'PROSES') as status_kelulusan " +
                "FROM tbl_siswa s " +
                "LEFT JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa " +
                "LEFT JOIN tbl_sekolah_asal sa ON s.id_siswa = sa.id_siswa " +
                "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur " +
                "LEFT JOIN tbl_seleksi sl ON s.id_siswa = sl.id_siswa WHERE 1=1 ";

        if (keyword != null && !keyword.trim().isEmpty()) {
            if (kategori.equalsIgnoreCase("No Daftar")) {
                sql += "AND s.nomor_pendaftaran LIKE ? ";
            } else if (kategori.equalsIgnoreCase("Nama")) {
                sql += "AND b.nama_lengkap LIKE ? ";
            } else if (kategori.equalsIgnoreCase("Jalur")) {
                sql += "AND j.nama_jalur LIKE ? ";
            } else if (kategori.equalsIgnoreCase("Asal Sekolah")) {
                sql += "AND sa.nama_sekolah LIKE ? ";
            } else {
                sql += "AND (s.nomor_pendaftaran LIKE ? OR b.nama_lengkap LIKE ? OR j.nama_jalur LIKE ? OR sa.nama_sekolah LIKE ?) ";
            }
        }

        if (!statusKelulusan.equals("Semua Status")) {
            sql += "AND COALESCE(sl.status_kelulusan, 'PROSES') = ? ";
        }
        
        sql += " ORDER BY sl.total_nilai DESC, s.id_siswa ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIdx = 1;
            String queryKeyword = keyword != null ? "%" + keyword.trim() + "%" : "";
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                if (kategori.equalsIgnoreCase("Semua Kategori")) {
                    ps.setString(paramIdx++, queryKeyword); ps.setString(paramIdx++, queryKeyword);
                    ps.setString(paramIdx++, queryKeyword); ps.setString(paramIdx++, queryKeyword);
                } else {
                    ps.setString(paramIdx++, queryKeyword);
                }
            }
            
            if (!statusKelulusan.equals("Semua Status")) {
                ps.setString(paramIdx++, statusKelulusan);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("nomor_pendaftaran"));
                    row.add(rs.getString("nik") == null ? "-" : rs.getString("nik"));
                    row.add(rs.getString("nama_lengkap") == null ? "Nama Belum Diisi" : rs.getString("nama_lengkap"));
                    row.add(rs.getString("jenis_kelamin") == null ? "-" : rs.getString("jenis_kelamin"));
                    row.add(rs.getString("sekolah_asal"));
                    row.add(rs.getString("nama_jalur"));
                    row.add(rs.getDouble("total_nilai"));
                    row.add(rs.getString("status_kelulusan"));
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            // Kronologi log kegagalan internal sistem diredam aman
        }
    }

    // <-------------------- PROSES HAPUS DATA -------------------->
    /**
     * Menghapus permanen data pendaftar terpilih secara transaksional aman.
     */
    private void eksekusiHapusData() {
        int row = tblPendaftar.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris pendaftar pada tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomorDaftar = tableModel.getValueAt(row, 0).toString();
        String nama = tableModel.getValueAt(row, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda YAKIN ingin menghapus pendaftar: " + nama + " (" + nomorDaftar + ")?\n\n"
            + "Tindakan ini akan menghapus permanen seluruh riwayat nilai, berkas, dan akun login siswa bersangkutan!", 
            "Peringatan Zona Kritis", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = DatabaseConfig.getKoneksi();
            try {
                conn.setAutoCommit(false);

                int idSiswa = -1;
                String getIdSql = "SELECT id_siswa FROM tbl_siswa WHERE nomor_pendaftaran = ?";
                try (PreparedStatement ps = conn.prepareStatement(getIdSql)) {
                    ps.setString(1, nomorDaftar);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            idSiswa = rs.getInt("id_siswa");
                        }
                    }
                }

                if (idSiswa != -1) {
                    String[] deleteAnak = {
                        "DELETE FROM tbl_seleksi WHERE id_siswa = ?",
                        "DELETE FROM tbl_berkas WHERE id_siswa = ?",
                        "DELETE FROM tbl_biodata_siswa WHERE id_siswa = ?"
                    };
                    for (String q : deleteAnak) {
                        try (PreparedStatement ps = conn.prepareStatement(q)) {
                            ps.setInt(1, idSiswa);
                            ps.executeUpdate();
                        }
                    }

                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tbl_siswa WHERE id_siswa = ?")) {
                        ps.setInt(1, idSiswa);
                        ps.executeUpdate();
                    }
                }

                conn.commit();

                if (SessionManager.isLoggedIn()) {
                    authController.recordAuditLog(SessionManager.getCurrentUser().getIdUser(), "HAPUS PENDAFTAR", "Menghapus akun pendaftar: " + nomorDaftar);
                }

                JOptionPane.showMessageDialog(this, "Data pendaftar " + nama + " berhasil dibersihkan dari server.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                cariDataPendaftar(txtCari.getText(), cmbKategori.getSelectedItem().toString(), cmbFilterKelulusan.getSelectedItem().toString());

            } catch (SQLException ex) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { }
                JOptionPane.showMessageDialog(this, "Gagal menghapus data. Transaksi dibatalkan: " + ex.getMessage(), "Error Transaksional", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { }
            }
        }
    }

    // <-------------------- HELPER METHOD -------------------->
    private void bukaDialogEditData() {
        int row = tblPendaftar.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih pendaftar terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomorDaftar = tableModel.getValueAt(row, 0).toString();
        
        EditDataSiswaDialog dialog = new EditDataSiswaDialog(
                SwingUtilities.getWindowAncestor(this),
                nomorDaftar,
                () -> {
                    cariDataPendaftar(txtCari.getText(), cmbKategori.getSelectedItem().toString(), cmbFilterKelulusan.getSelectedItem().toString());
                }
        );
        dialog.setVisible(true);
    }

    /**
     * Membuka modal popup detail peninjauan komponen biodata fisik lengkap pendaftar.
     */
    private void openDetailEditDialog() {
        int row = tblPendaftar.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih baris pendaftar untuk melihat atau mengedit detail.", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
        String nomorDaftar = tableModel.getValueAt(row, 0).toString();

        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return;
        int idSiswa = -1;
        try (PreparedStatement ps = conn.prepareStatement("SELECT id_siswa FROM tbl_siswa WHERE nomor_pendaftaran = ?")) {
            ps.setString(1, nomorDaftar);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) idSiswa = rs.getInt("id_siswa"); }
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Gagal mengambil id siswa: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); return; }

        if (idSiswa == -1) { JOptionPane.showMessageDialog(this, "Data siswa tidak ditemukan di database.", "Error", JOptionPane.ERROR_MESSAGE); return; }
        final int finalIdSiswa = idSiswa;

        String nik = "", nama = "", tempat = "", tanggal = "", jenis = "", sekolah = "";
        try (PreparedStatement ps = conn.prepareStatement("SELECT b.nik, b.nama_lengkap, b.tempat_lahir, b.tanggal_lahir, b.jenis_kelamin, sa.nama_sekolah FROM tbl_biodata_siswa b LEFT JOIN tbl_sekolah_asal sa ON b.id_siswa = sa.id_siswa WHERE b.id_siswa = ?")) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nik = rs.getString("nik"); nama = rs.getString("nama_lengkap"); tempat = rs.getString("tempat_lahir"); tanggal = rs.getString("tanggal_lahir"); jenis = rs.getString("jenis_kelamin"); sekolah = rs.getString("nama_sekolah");
                }
            }
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Gagal memuat biodata: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); return; }

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Data Pendaftar: " + nomorDaftar, Dialog.ModalityType.APPLICATION_MODAL);
        JPanel p = new JPanel(new BorderLayout(8, 8)); p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField fNik = new JTextField(nik == null ? "" : nik);
        JTextField fNama = new JTextField(nama == null ? "" : nama);
        JTextField fTempat = new JTextField(tempat == null ? "" : tempat);
        JTextField fTanggal = new JTextField(tanggal == null ? "" : tanggal);
        JComboBox<String> fJenis = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        if (jenis != null) {
            if (jenis.equalsIgnoreCase("L") || jenis.equalsIgnoreCase("Laki-laki")) {
                fJenis.setSelectedItem("Laki-laki");
            } else if (jenis.equalsIgnoreCase("P") || jenis.equalsIgnoreCase("Perempuan")) {
                fJenis.setSelectedItem("Perempuan");
            }
        }
        JTextField fSekolah = new JTextField(sekolah == null ? "" : sekolah);

        form.add(new JLabel("NIK")); form.add(fNik);
        form.add(new JLabel("Nama Lengkap")); form.add(fNama);
        form.add(new JLabel("Tempat Lahir")); form.add(fTempat);
        form.add(new JLabel("Tanggal Lahir (YYYY-MM-DD)")); form.add(fTanggal);
        form.add(new JLabel("Jenis Kelamin (L/P)")); form.add(fJenis);
        form.add(new JLabel("Sekolah Asal")); form.add(fSekolah);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSimpan = new JButton("Simpan Perubahan");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.BLACK); 
        btnSimpan.setFocusPainted(false);
        
        btnSimpan.addActionListener((ActionEvent e) -> {
            try {
                conn.setAutoCommit(false);
                String jenisKelaminValue = fJenis.getSelectedItem() != null ? fJenis.getSelectedItem().toString() : "";
                if (jenisKelaminValue.equalsIgnoreCase("L")) {
                    jenisKelaminValue = "Laki-laki";
                } else if (jenisKelaminValue.equalsIgnoreCase("P")) {
                    jenisKelaminValue = "Perempuan";
                }
                try (PreparedStatement psB = conn.prepareStatement("UPDATE tbl_biodata_siswa SET nik = ?, nama_lengkap = ?, tempat_lahir = ?, tanggal_lahir = ?, jenis_kelamin = ? WHERE id_siswa = ?")) {
                    psB.setString(1, fNik.getText().trim()); psB.setString(2, fNama.getText().trim()); psB.setString(3, fTempat.getText().trim()); psB.setString(4, fTanggal.getText().trim()); psB.setString(5, jenisKelaminValue); psB.setInt(6, finalIdSiswa);
                    psB.executeUpdate();
                }

                try (PreparedStatement psC = conn.prepareStatement("SELECT COUNT(1) as cnt FROM tbl_sekolah_asal WHERE id_siswa = ?")) {
                    psC.setInt(1, finalIdSiswa);
                    try (ResultSet rs = psC.executeQuery()) {
                        if (rs.next() && rs.getInt("cnt") > 0) {
                            try (PreparedStatement up = conn.prepareStatement("UPDATE tbl_sekolah_asal SET nama_sekolah = ? WHERE id_siswa = ?")) {
                                up.setString(1, fSekolah.getText().trim()); up.setInt(2, finalIdSiswa); up.executeUpdate();
                            }
                        } else {
                            try (PreparedStatement ins = conn.prepareStatement("INSERT INTO tbl_sekolah_asal (id_siswa, nama_sekolah) VALUES (?, ?)")) {
                                ins.setInt(1, finalIdSiswa); ins.setString(2, fSekolah.getText().trim()); ins.executeUpdate();
                            }
                        }
                    }
                }

                conn.commit();
                JOptionPane.showMessageDialog(d, "Perubahan berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                d.dispose();
                
                cariDataPendaftar(txtCari.getText(), cmbKategori.getSelectedItem().toString(), cmbFilterKelulusan.getSelectedItem().toString());
            } catch (SQLException ex) {
                try { conn.rollback(); } catch (SQLException roll) { }
                JOptionPane.showMessageDialog(d, "Gagal menyimpan perubahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { }
            }
        });

        JButton btnCancel = new JButton("Batal"); 
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setBackground(new Color(231, 76, 60));
        btnCancel.setForeground(Color.BLACK); 
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(a -> d.dispose());
        
        btnRow.add(btnSimpan); btnRow.add(btnCancel);

        p.add(form, BorderLayout.CENTER);
        p.add(btnRow, BorderLayout.SOUTH);
        d.setContentPane(p); d.pack(); d.setLocationRelativeTo(this); d.setVisible(true);
    }
}
