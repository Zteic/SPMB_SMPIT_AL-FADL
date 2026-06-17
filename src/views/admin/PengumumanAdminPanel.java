package views.admin;

import config.DatabaseConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Manajemen Berita & Pengumuman Kelulusan Resmi (UI/UX Premium Overhaul).
 * Terintegrasi aman dengan tabel tbl_pengumuman melalui JDBC MySQL.
 * * @author Rivaldi
 */
public class PengumumanAdminPanel extends JPanel {

    // <-------------------- KOMPONEN FORM -------------------->
    private JTable tblPengumuman;
    private DefaultTableModel announcementsModel;
    private JTextArea txtDetail;
    private JButton btnRefresh;
    private JButton btnTambah;
    private JButton btnEdit;
    private JButton btnHapus;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari PengumumanAdminPanel dan memuat data awal.
     */
    public PengumumanAdminPanel() {
        initUI();
        loadAnnouncements();
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    /**
     * Merender dan mengonfigurasi tata letak komponen utama antarmuka kelola pengumuman.
     */
    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250)); 
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // <-------------------- SECTION HEADER ATAS -------------------->
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 15));
        pnlHeader.setOpaque(false);

        JPanel pnlTitleText = new JPanel(new GridLayout(2, 1, 4, 4));
        pnlTitleText.setOpaque(false);
        
        JLabel title = new JLabel("Pengumuman Kelulusan Resmi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(15, 23, 42));

        JLabel subtitle = new JLabel("Publikasikan berita kelulusan, jadwal registrasi ulang, dan maklumat administrasi sekolah.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(100, 116, 139));
        
        pnlTitleText.add(title);
        pnlTitleText.add(subtitle);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        btnTambah = createStyledButton("Tambah Baru", new Color(37, 99, 235)); 
        btnEdit = createStyledButton("Edit Pengumuman", new Color(245, 158, 11)); 
        btnHapus = createStyledButton("Hapus", new Color(220, 38, 38)); 
        btnRefresh = createStyledButton("Refresh", new Color(71, 85, 105)); 
        
        btnTambah.addActionListener(e -> openAnnouncementDialog(null));
        btnEdit.addActionListener(e -> editSelectedAnnouncement());
        btnHapus.addActionListener(e -> deleteSelectedAnnouncement());
        btnRefresh.addActionListener(e -> loadAnnouncements());

        pnlActions.add(btnTambah); 
        pnlActions.add(btnEdit); 
        pnlActions.add(btnHapus); 
        pnlActions.add(btnRefresh);

        pnlHeader.add(pnlTitleText, BorderLayout.WEST);
        pnlHeader.add(pnlActions, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // <-------------------- SECTION DATA GRID TABLE -------------------->
        String[] columns = {"ID", "Judul Pengumuman resmi", "Tanggal Publikasi", "Status Edaran", "Isi Berita"};
        announcementsModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        tblPengumuman = new JTable(announcementsModel);
        tblPengumuman.setRowHeight(38); 
        tblPengumuman.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblPengumuman.setShowGrid(false);
        tblPengumuman.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader headerTable = tblPengumuman.getTableHeader();
        headerTable.setReorderingAllowed(false);
        headerTable.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerTable.setBackground(new Color(235, 243, 250));
        headerTable.setForeground(new Color(44, 62, 80));
        headerTable.setPreferredSize(new Dimension(0, 38));

        tblPengumuman.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                if (!isS) {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    comp.setForeground(new Color(15, 23, 42));
                    
                    if (c == 3 && v != null) {
                        String st = v.toString();
                        if ("DIUMUMKAN".equals(st)) {
                            comp.setBackground(new Color(220, 252, 231)); 
                            comp.setForeground(new Color(21, 128, 61));
                        } else {
                            comp.setBackground(new Color(254, 243, 199)); 
                            comp.setForeground(new Color(180, 83, 9));
                        }
                    }
                } else {
                    comp.setBackground(new Color(219, 234, 254)); 
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        });

        int[] hiddenCols = {0, 4};
        for (int colIdx : hiddenCols) {
            tblPengumuman.getColumnModel().getColumn(colIdx).setMinWidth(0);
            tblPengumuman.getColumnModel().getColumn(colIdx).setMaxWidth(0);
            tblPengumuman.getColumnModel().getColumn(colIdx).setWidth(0);
        }
        
        tblPengumuman.getSelectionModel().addListSelectionListener(e -> showSelectedDetail());

        JScrollPane scrollTable = new JScrollPane(tblPengumuman);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollTable.getViewport().setBackground(Color.WHITE);

        // <-------------------- SECTION DETAIL VIEW PANEL -------------------->
        JPanel pnlDetailWrapper = new JPanel(new BorderLayout());
        pnlDetailWrapper.setBackground(Color.WHITE);
        pnlDetailWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblDetailTitle = new JLabel("Konten Isi Dokumen Maklumat / Berita:");
        lblDetailTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDetailTitle.setForeground(new Color(51, 65, 85));
        lblDetailTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        pnlDetailWrapper.add(lblDetailTitle, BorderLayout.NORTH);

        txtDetail = new JTextArea();
        txtDetail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDetail.setEditable(false);
        txtDetail.setLineWrap(true);
        txtDetail.setWrapStyleWord(true);
        txtDetail.setForeground(new Color(15, 23, 42));
        txtDetail.setBackground(new Color(250, 251, 253));
        txtDetail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollDetail = new JScrollPane(txtDetail);
        scrollDetail.setBorder(null);
        pnlDetailWrapper.add(scrollDetail, BorderLayout.CENTER);

        JSplitPane splitPaneLayout = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTable, pnlDetailWrapper);
        splitPaneLayout.setResizeWeight(0.5);
        splitPaneLayout.setDividerLocation(260);
        splitPaneLayout.setDividerSize(6);
        splitPaneLayout.setBorder(BorderFactory.createEmptyBorder());

        add(splitPaneLayout, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // <-------------------- DATABASE QUERY -------------------->
    /**
     * Memuat daftar maklumat pengumuman resmi institusi dari database MySQL.
     */
    private void loadAnnouncements() {
        announcementsModel.setRowCount(0);
        txtDetail.setText("");
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return;
        String sql = "SELECT id_pengumuman, judul, tanggal_publish, status, isi FROM tbl_pengumuman ORDER BY tanggal_publish DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id_pengumuman"));
                row.add(rs.getString("judul"));
                row.add(rs.getDate("tanggal_publish"));
                row.add(rs.getString("status"));
                row.add(rs.getString("isi"));
                announcementsModel.addRow(row);
            }
        } catch (SQLException e) {
            // Kronologi log kegagalan internal sistem diredam aman
        }
    }

    // <-------------------- HELPER SUB-DIALOG METHODS -------------------->
    /**
     * Membuka sub-dialog modal input formulir penambahan/modifikasi konten edaran pengumuman.
     * * @param id id unik pengumuman dari database (bernilai null jika entri baru)
     */
    private void openAnnouncementDialog(Integer id) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog d = new JDialog(parentWindow, id == null ? "Form Tambah Maklumat Baru" : "Form Modifikasi Data Pengumuman", Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(520, 500);
        d.setLocationRelativeTo(this);
        
        JPanel p = new JPanel(new BorderLayout(12, 12)); 
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        p.setBackground(Color.WHITE);

        JTextField txtJudul = new JTextField();
        txtJudul.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtJudul.setPreferredSize(new Dimension(0, 32));

        JTextArea txtIsi = new JTextArea(8, 40);
        txtIsi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtIsi.setLineWrap(true); txtIsi.setWrapStyleWord(true);
        
        JScrollPane scrollIsi = new JScrollPane(txtIsi);
        scrollIsi.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"DIUMUMKAN", "DRAFT"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatus.setPreferredSize(new Dimension(0, 32));

        JTextField txtTanggal = new JTextField();
        txtTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTanggal.setPreferredSize(new Dimension(0, 32));

        if (id == null) {
            txtTanggal.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        } else {
            Connection conn = DatabaseConfig.getKoneksi();
            try (PreparedStatement ps = conn.prepareStatement("SELECT judul, isi, status, tanggal_publish FROM tbl_pengumuman WHERE id_pengumuman = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        txtJudul.setText(rs.getString("judul"));
                        txtIsi.setText(rs.getString("isi"));
                        cmbStatus.setSelectedItem(rs.getString("status"));
                        txtTanggal.setText(rs.getString("tanggal_publish"));
                    }
                }
            } catch (SQLException ex) {
                // Storage exception logging diredam aman
            }
        }

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.setOpaque(false);
        
        JLabel l1 = new JLabel("Judul Pengumuman / Edaran:"); l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(l1); form.add(txtJudul);
        
        JLabel l2 = new JLabel("Status Edaran Publik:"); l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(l2); form.add(cmbStatus);
        
        JLabel l3 = new JLabel("Tanggal Publish (YYYY-MM-DD):"); l3.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(l3); form.add(txtTanggal);
        
        JLabel l4 = new JLabel("Isi Konten Berita Lengkap:"); l4.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JPanel pnlCenterForm = new JPanel(new BorderLayout(5, 5));
        pnlCenterForm.setOpaque(false);
        pnlCenterForm.add(form, BorderLayout.NORTH);
        pnlCenterForm.add(l4, BorderLayout.CENTER);
        pnlCenterForm.add(scrollIsi, BorderLayout.SOUTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        
        JButton btnSave = new JButton(id == null ? "Simpan Data" : "Perbarui Dokumen");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSave.setBackground(new Color(22, 163, 74)); btnSave.setForeground(Color.BLACK);
        btnSave.setPreferredSize(new Dimension(140, 35)); bsetCur(btnSave);
        
        btnSave.addActionListener(e -> {
            String judul = txtJudul.getText().trim();
            String isi = txtIsi.getText().trim();
            String status = cmbStatus.getSelectedItem().toString();
            String tanggal = txtTanggal.getText().trim();
            if (judul.isEmpty() || isi.isEmpty() || tanggal.isEmpty()) {
                JOptionPane.showMessageDialog(d, "Judul, isi, dan tanggal publish tidak boleh kosong", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                java.sql.Date.valueOf(tanggal);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(d, "Format tanggal publish harus YYYY-MM-DD", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try (Connection conn = DatabaseConfig.getKoneksi()) {
                if (id == null) {
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO tbl_pengumuman (judul, isi, status, tanggal_publish) VALUES (?, ?, ?, ?)")) {
                        ps.setString(1, judul); ps.setString(2, isi); ps.setString(3, status); ps.setString(4, tanggal);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE tbl_pengumuman SET judul = ?, isi = ?, status = ?, tanggal_publish = ? WHERE id_pengumuman = ?")) {
                        ps.setString(1, judul); ps.setString(2, isi); ps.setString(3, status); ps.setString(4, tanggal); ps.setInt(5, id);
                        ps.executeUpdate();
                    }
                }
                loadAnnouncements();
                d.dispose();
            } catch (SQLException ex) { JOptionPane.showMessageDialog(d, "Gagal menyimpan pengumuman: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });
        
        JButton btnCancel = new JButton("Batal"); 
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setForeground(Color.BLACK);
        btnCancel.setPreferredSize(new Dimension(80, 35)); bsetCur(btnCancel);
        btnCancel.addActionListener(e -> d.dispose()); 
        
        btnRow.add(btnSave); 
        btnRow.add(btnCancel);

        p.add(pnlCenterForm, BorderLayout.CENTER);
        p.add(btnRow, BorderLayout.SOUTH);
        d.setContentPane(p);
        d.setLocationRelativeTo(this); d.setVisible(true);
    }

    private void bsetCur(JButton b) { b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.setFocusPainted(false); }

    // <-------------------- PROCESS LOGIC ACTIONS -------------------->
    private void editSelectedAnnouncement() {
        int r = tblPengumuman.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Silakan pilih salah satu data pengumuman pada tabel untuk diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
        int modelRow = tblPengumuman.convertRowIndexToModel(r);
        Integer id = (Integer) announcementsModel.getValueAt(modelRow, 0);
        openAnnouncementDialog(id);
    }

    private void deleteSelectedAnnouncement() {
        int r = tblPengumuman.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Silakan pilih data pengumuman pada tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
        int modelRow = tblPengumuman.convertRowIndexToModel(r);
        Integer id = (Integer) announcementsModel.getValueAt(modelRow, 0);
        int ok = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus pengumuman resmi terpilih secara permanen?", "Konfirmasi Penghapusan", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        try (Connection conn = DatabaseConfig.getKoneksi(); PreparedStatement ps = conn.prepareStatement("DELETE FROM tbl_pengumuman WHERE id_pengumuman = ?")) {
            ps.setInt(1, id); ps.executeUpdate(); loadAnnouncements();
            JOptionPane.showMessageDialog(this, "Pengumuman resmi berhasil dibersihkan dari database.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void showSelectedDetail() {
        int selectedRow = tblPengumuman.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = tblPengumuman.convertRowIndexToModel(selectedRow);
            Object value = announcementsModel.getValueAt(modelRow, 4);
            txtDetail.setText(value != null ? value.toString() : "");
        }
    }
}
