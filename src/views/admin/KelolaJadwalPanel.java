package views.admin;

import config.AppTheme;
import config.DatabaseConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.Vector;

/**
 * File: KelolaJadwalPanel.java
 * Fungsi: Antarmuka Manajemen Data Operasional Jadwal Ujian PPDB (Modern UI 2026 Edition)
 */
public class KelolaJadwalPanel extends JPanel {

    // <-------------------- 1. ELEMEN STRUKTUR CLASS COMPONENTS -------------------->
    private JTextField txtTanggal;
    private JTextField txtNamaKegiatan;
    private JTextField txtJamMulai;
    private JTextField txtJamSelesai;
    private JTextField txtLokasi;
    private JTextArea txtDeskripsi;
    private JComboBox<String> cmbStatus;
    
    private DefaultTableModel modelTabel;
    private JTable tabelJadwal;
    private JTextField txtCari;
    
    private JButton btnTambah;
    private JButton btnEdit;
    private JButton btnHapus;
    private JButton btnRefresh;
    
    private DashboardAnalyticsPanel observerDashboard;

    // <-------------------- 2. CONSTRUCTOR UTAMA CLASS -------------------->
    public KelolaJadwalPanel(DashboardAnalyticsPanel dashboard) {
        this.observerDashboard = dashboard; // Hubungkan jembatan rujukan
        initComponents();
        initEventComponents();
        muatDataJadwal();
    }

    // <-------------------- 3. METODE INITIALIZATION UI (TATA LETAK MODERN) -------------------->
    private void initComponents() {
        setLayout(new BorderLayout(24, 24));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Title Header Form Formal
        JLabel lblTitle = new JLabel("PENGELOLAAN JADWAL SELEKSI PPDB");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(AppTheme.TEXT_DARK);
        add(lblTitle, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout(20, 0));
        pnlCenter.setOpaque(false);
        pnlCenter.add(buildFormPanel(), BorderLayout.WEST);
        pnlCenter.add(buildTablePanel(), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);
    }

    private JPanel buildFormPanel() {
        JPanel pnlForm = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        pnlForm.setOpaque(false);
        pnlForm.setPreferredSize(new Dimension(370, 0));
        pnlForm.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Form Title Label Inside Card
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblFormTitle = new JLabel("Input Data Kegiatan");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(AppTheme.TEXT_DARK);
        lblFormTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)));
        pnlForm.add(lblFormTitle, gbc);

        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(createFormLabel("Tanggal (YYYY-MM-DD)"), gbc);
        gbc.gridx = 0; gbc.gridy = 2; txtTanggal = createStyledTextField(); pnlForm.add(txtTanggal, gbc);

        gbc.gridx = 0; gbc.gridy = 3; pnlForm.add(createFormLabel("Nama Kegiatan"), gbc);
        gbc.gridx = 0; gbc.gridy = 4; txtNamaKegiatan = createStyledTextField(); pnlForm.add(txtNamaKegiatan, gbc);

        // Sub-panel untuk menyejajarkan Jam Mulai & Selesai secara proporsional
        JPanel pnlJam = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlJam.setOpaque(false);
        JPanel pnlJamMulai = new JPanel(new BorderLayout(0, 4)); pnlJamMulai.setOpaque(false);
        pnlJamMulai.add(createFormLabel("Jam Mulai"), BorderLayout.NORTH);
        txtJamMulai = createStyledTextField(); pnlJamMulai.add(txtJamMulai, BorderLayout.CENTER);
        
        JPanel pnlJamSelesai = new JPanel(new BorderLayout(0, 4)); pnlJamSelesai.setOpaque(false);
        pnlJamSelesai.add(createFormLabel("Jam Selesai"), BorderLayout.NORTH);
        txtJamSelesai = createStyledTextField(); pnlJamSelesai.add(txtJamSelesai, BorderLayout.CENTER);
        
        pnlJam.add(pnlJamMulai); pnlJam.add(pnlJamSelesai);
        
        gbc.gridx = 0; gbc.gridy = 5; pnlForm.add(pnlJam, gbc);

        gbc.gridx = 0; gbc.gridy = 6; pnlForm.add(createFormLabel("Lokasi Ruangan"), gbc);
        gbc.gridx = 0; gbc.gridy = 7; txtLokasi = createStyledTextField(); pnlForm.add(txtLokasi, gbc);

        gbc.gridx = 0; gbc.gridy = 8; pnlForm.add(createFormLabel("Status Aktif"), gbc);
        gbc.gridx = 0; gbc.gridy = 9; cmbStatus = new JComboBox<>(new String[]{"AKTIF", "NONAKTIF"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbStatus.setPreferredSize(new Dimension(0, 35));
        pnlForm.add(cmbStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 10; pnlForm.add(createFormLabel("Deskripsi Tambahan"), gbc);
        
        txtDeskripsi = new JTextArea(3, 15); 
        txtDeskripsi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        JScrollPane spDeskripsi = new JScrollPane(txtDeskripsi);
        spDeskripsi.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        
        gbc.gridx = 0; gbc.gridy = 11; 
        pnlForm.add(spDeskripsi, gbc);

 
        JPanel pnlTombolForm = new JPanel(new GridLayout(1, 4, 5, 0));
        pnlTombolForm.setOpaque(false);
        pnlTombolForm.setPreferredSize(new Dimension(0, 38));

        btnTambah = createStyledButton("Simpan", new Color(34, 197, 94));
        btnTambah.addActionListener(e -> eksekusiSimpan(true));

        btnEdit = createStyledButton("Ubah", new Color(59, 130, 246));
        btnEdit.addActionListener(e -> eksekusiSimpan(false));

        btnHapus = createStyledButton("Hapus", new Color(239, 68, 68));
        btnHapus.addActionListener(e -> eksekusiHapus());

        JButton btnResetFormKiri = createStyledButton("Reset", new Color(148, 163, 184));
        btnResetFormKiri.addActionListener(e -> bersihkanForm());

        pnlTombolForm.add(btnTambah); 
        pnlTombolForm.add(btnEdit); 
        pnlTombolForm.add(btnHapus); 
        pnlTombolForm.add(btnResetFormKiri);

        gbc.gridx = 0; 
        gbc.gridy = 12; 
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.insets = new Insets(16, 4, 4, 4);
        pnlForm.add(pnlTombolForm, gbc);
        
        return pnlForm;
    }

    private JPanel buildTablePanel() {
        JPanel pnlKanan = new JPanel(new BorderLayout(0, 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        pnlKanan.setOpaque(false);
        pnlKanan.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlFilter.setOpaque(false);
        pnlFilter.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    
        JLabel lblSearch = new JLabel("Cari Kegiatan:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(AppTheme.TEXT_DARK);
    
        txtCari = createStyledTextField();
        txtCari.setPreferredSize(new Dimension(200, 35)); 
    
        btnRefresh = createStyledButton("Reset", new Color(148, 163, 184)); 
        btnRefresh.setPreferredSize(new Dimension(90, 35)); 
    
        pnlFilter.add(lblSearch);
        pnlFilter.add(txtCari);
        pnlFilter.add(btnRefresh); 
    
        pnlKanan.add(pnlFilter, BorderLayout.NORTH);

        // Render JTable List
        String[] columns = {"ID", "Tanggal", "Mulai", "Selesai", "Nama Kegiatan", "Lokasi", "Status"};
        modelTabel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }
        };
        
        tabelJadwal = new JTable(modelTabel);
        tabelJadwal.setRowHeight(38); // Berjarak lega
        tabelJadwal.setShowGrid(false); // Buang line jadul kaku
        tabelJadwal.setIntercellSpacing(new Dimension(0, 0));
        tabelJadwal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelJadwal.setSelectionBackground(new Color(241, 245, 249));
        tabelJadwal.setSelectionForeground(AppTheme.TEXT_DARK);
        
        // Custom Renderer khusus kolom status (Kolom ke-6) biar jadi Kapsul Badge
        tabelJadwal.getColumnModel().getColumn(6).setCellRenderer(new TableStatusBadgeRenderer());

        JTableHeader tableHeader = tabelJadwal.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHeader.setBackground(AppTheme.SURFACE);
        tableHeader.setForeground(AppTheme.TEXT_SECONDARY);
        tableHeader.setPreferredSize(new Dimension(0, 34));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        tableHeader.setReorderingAllowed(false);

        JScrollPane spTabel = new JScrollPane(tabelJadwal);
        spTabel.setBorder(null);
        spTabel.getViewport().setBackground(Color.WHITE);
        pnlKanan.add(spTabel, BorderLayout.CENTER);
        
        return pnlKanan;
    }

    private void initEventComponents() {
        tabelJadwal.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tabelJadwal.getSelectedRow();
            if (selectedRow != -1) {
                txtTanggal.setText(modelTabel.getValueAt(selectedRow, 1).toString());
                txtJamMulai.setText(modelTabel.getValueAt(selectedRow, 2).toString());
                txtJamSelesai.setText(modelTabel.getValueAt(selectedRow, 3).toString());
                txtNamaKegiatan.setText(modelTabel.getValueAt(selectedRow, 4).toString());
                txtLokasi.setText(modelTabel.getValueAt(selectedRow, 5).toString());
                cmbStatus.setSelectedItem(modelTabel.getValueAt(selectedRow, 6).toString());
            }
        });

        btnTambah.addActionListener(e -> eksekusiSimpan(true));
        btnEdit.addActionListener(e -> eksekusiSimpan(false));
        btnHapus.addActionListener(e -> eksekusiHapus());
        btnRefresh.addActionListener(e -> { txtCari.setText(""); muatDataJadwal(); bersihkanForm(); });
        
        txtCari.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { muatDataJadwal(); }
        });
    }

    // <-------------------- 4. METODE LOGIKA TRANSITIONAL DATABASE (CRUD) -------------------->
    private void muatDataJadwal() {
        modelTabel.setRowCount(0);
        String querySql = "SELECT id_jadwal, tanggal, jam_mulai, jam_selesai, nama_kegiatan, lokasi, status "
                        + "FROM tbl_jadwal_seleksi WHERE nama_kegiatan LIKE ? ORDER BY tanggal DESC, jam_mulai ASC";
                        
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(querySql)) {
             
            ps.setString(1, "%" + txtCari.getText().trim() + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> rowData = new Vector<>();
                    rowData.add(rs.getInt("id_jadwal"));
                    rowData.add(rs.getString("tanggal"));
                    rowData.add(rs.getString("jam_mulai"));
                    rowData.add(rs.getString("jam_selesai"));
                    rowData.add(rs.getString("nama_kegiatan"));
                    rowData.add(rs.getString("lokasi"));
                    rowData.add(rs.getString("status"));
                    modelTabel.addRow(rowData);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Gagal memuat tabel jadwal seleksi: " + ex.getMessage());
        }
    }

    private void eksekusiSimpan(boolean isActionInsert) {
        if (txtTanggal.getText().isEmpty() || txtNamaKegiatan.getText().isEmpty() || txtJamMulai.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi kolom data kritikal pendaftaran!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String querySql = isActionInsert 
            ? "INSERT INTO tbl_jadwal_seleksi (tanggal, nama_kegiatan, jam_mulai, jam_selesai, lokasi, status, deskripsi) VALUES (?,?,?,?,?,?,?)"
            : "UPDATE tbl_jadwal_seleksi SET tanggal=?, nama_kegiatan=?, jam_mulai=?, jam_selesai=?, lokasi=?, status=?, deskripsi=? WHERE id_jadwal=?";
        
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(querySql)) {
             
            ps.setString(1, txtTanggal.getText().trim());
            ps.setString(2, txtNamaKegiatan.getText().trim());
            ps.setString(3, txtJamMulai.getText().trim());
            ps.setString(4, txtJamSelesai.getText().trim());
            ps.setString(5, txtLokasi.getText().trim());
            ps.setString(6, cmbStatus.getSelectedItem().toString());
            ps.setString(7, txtDeskripsi.getText().trim());
            
            if (!isActionInsert) {
                int selectedRow = tabelJadwal.getSelectedRow();
                if (selectedRow == -1) return;
                ps.setInt(8, Integer.parseInt(modelTabel.getValueAt(selectedRow, 0).toString()));
            }
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sinkronisasi data jadwal berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);            muatDataJadwal();
            if (observerDashboard != null) {
                observerDashboard.refreshAllData();
            }
            bersihkanForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memproses data SQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eksekusiHapus() {
        int selectedRow = tabelJadwal.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris jadwal yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idJadwal = Integer.parseInt(modelTabel.getValueAt(selectedRow, 0).toString());
        int konfirmasiDialog = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus jadwal ini secara permanen?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasiDialog == JOptionPane.YES_OPTION) {
            String querySql = "DELETE FROM tbl_jadwal_seleksi WHERE id_jadwal=?";
            try (Connection conn = DatabaseConfig.getKoneksi();
                 PreparedStatement ps = conn.prepareStatement(querySql)) {
                ps.setInt(1, idJadwal);
                ps.executeUpdate();
                muatDataJadwal();
                bersihkanForm();
            } catch (SQLException ex) {
                System.err.println("Gagal mengeksekusi hapus data jadwal: " + ex.getMessage());
            }
        }
    }

    private void bersihkanForm() {
        txtTanggal.setText(""); txtNamaKegiatan.setText(""); txtJamMulai.setText("");
        txtJamSelesai.setText(""); txtLokasi.setText(""); txtDeskripsi.setText("");
        cmbStatus.setSelectedIndex(0); tabelJadwal.clearSelection();
    }

    // <-------------------- 5. FACTORY UTILITY GENERATOR 2026 UI STYLE -------------------->
    private JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField fld = new JTextField();
        fld.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fld.setPreferredSize(new Dimension(0, 35));
        fld.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        return fld;
    }

    private JButton createStyledButton(String labelText, Color clrBackground) {
        JButton btn = new JButton(labelText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8); // Melengkung soft
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(clrBackground);
        btn.setForeground(Color.BLACK); // Memenuhi kewajiban mutlak: text warna BLACK
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * 🎯 INNER CLASS RENDERER: Mengubah Teks AKTIF/NONAKTIF Menjadi Rounded Badge Cantik
     */
    private static class TableStatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String val = value != null ? value.toString() : "NONAKTIF";
            
            JLabel lblBadge = new JLabel(val, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(8, 5, getWidth() - 16, getHeight() - 10, 10, 10);
                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            
            lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblBadge.setOpaque(false);
            
            if (val.equalsIgnoreCase("AKTIF")) {
                lblBadge.setBackground(new Color(220, 252, 231)); // Soft Green
                lblBadge.setForeground(new Color(21, 128, 61));
            } else {
                lblBadge.setBackground(new Color(254, 226, 226)); // Soft Red
                lblBadge.setForeground(new Color(185, 28, 28));
            }
            
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(isSelected ? new Color(241, 245, 249) : Color.WHITE);
            wrapper.add(lblBadge, BorderLayout.CENTER);
            return wrapper;
        }
    }
}
