package views.admin;

import config.DatabaseConfig;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PengaturanInformasiPanel extends JPanel {
    
    // 🎨 Palet Warna Premium 2026 (Clean SaaS Flat Design)
    private final Color WHITE = Color.WHITE;
    private final Color TEXT_DARK = new Color(15, 23, 42);      // Slate 900
    private final Color TEXT_LIGHT = new Color(100, 116, 139);  // Slate 500
    private final Color BORDER_CLR = new Color(226, 232, 240);  // Slate 200
    private final Color BG_LIGHT = new Color(248, 250, 252);    // Slate 50
    private final Color BLUE_PRIMARY = new Color(37, 99, 235);  // Blue 600
    private final Color RED_DANGER = new Color(220, 38, 38);    // Red 600
    private final Color GRAY_RESET = new Color(71, 85, 105);    // Slate 600

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtKategori, txtRingkasan;
    private JTextArea txtDetail;
    private JComboBox<String> cbStatus;
    private JButton btnSimpan, btnHapus, btnReset;
    private int selectedId = -1; 
    private java.util.List<Integer> listIds = new ArrayList<>(); 

    public PengaturanInformasiPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(BG_LIGHT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // 1. HEADER UTAMA PANEL
        JPanel panelHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        panelHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Manajemen Informasi & Rekening Pembayaran");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);
        JLabel lblSub = new JLabel("Kelola data informasi rekening, helpdesk panitia, dan linimasa yang tampil di dashboard siswa.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_LIGHT);
        panelHeader.add(lblTitle);
        panelHeader.add(lblSub);
        add(panelHeader, BorderLayout.NORTH);

        // 2. BAGIAN TENGAH (TABEL DATA MODERN)
        JPanel panelTabelOuter = new JPanel(new BorderLayout());
        panelTabelOuter.setBackground(WHITE);
        panelTabelOuter.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        
        String[] cols = {"Kategori", "Detail Informasi / Agenda", "Status Tampil"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(40); // Lebih lega dan tinggi ala web modern
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(241, 245, 249));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(239, 246, 255)); // Soft blue highlight
        table.setSelectionForeground(TEXT_DARK);

        // Styling Header Tabel
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));

        // Custom Cell Renderer untuk Margin Teks di Dalam Tabel
        DefaultTableCellRenderer paddingRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12)); // Memberikan sela teks agar tidak nempel garis
                if (!isS) {
                    comp.setBackground(r % 2 == 0 ? WHITE : new Color(251, 252, 253));
                }
                return comp;
            }
        };
        table.setDefaultRenderer(Object.class, paddingRenderer);

        // Pengaturan Lebar Kolom Proporsional
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(550);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);
        panelTabelOuter.add(scroll, BorderLayout.CENTER);
        add(panelTabelOuter, BorderLayout.CENTER);

        // 3. BAGIAN BAWAH (FORM DATA CARD LAYOUT)
        JPanel panelFormOuter = new JPanel(new BorderLayout());
        panelFormOuter.setBackground(WHITE);
        panelFormOuter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel panelFormInput = new JPanel(new GridLayout(2, 2, 20, 16));
        panelFormInput.setBackground(WHITE);

        // Input Kategori
        JPanel p1 = new JPanel(new BorderLayout(0, 6)); p1.setBackground(WHITE);
        JLabel l1 = new JLabel("Kategori Informasi"); l1.setFont(new Font("Segoe UI", Font.BOLD, 12)); l1.setForeground(TEXT_DARK);
        txtKategori = new JTextField(); txtKategori.setFont(new Font("Segoe UI", Font.PLAIN, 13)); txtKategori.setPreferredSize(new Dimension(0, 38));
        txtKategori.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        p1.add(l1, BorderLayout.NORTH); p1.add(txtKategori, BorderLayout.CENTER);

        // Input Ringkasan
        JPanel p2 = new JPanel(new BorderLayout(0, 6)); p2.setBackground(WHITE);
        JLabel l2 = new JLabel("Ringkasan Judul / No. Rekening / No. WA"); l2.setFont(new Font("Segoe UI", Font.BOLD, 12)); l2.setForeground(TEXT_DARK);
        txtRingkasan = new JTextField(); txtRingkasan.setFont(new Font("Segoe UI", Font.PLAIN, 13)); txtRingkasan.setPreferredSize(new Dimension(0, 38));
        txtRingkasan.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        p2.add(l2, BorderLayout.NORTH); p2.add(txtRingkasan, BorderLayout.CENTER);

        // Input Status ComboBox
        JPanel p3 = new JPanel(new BorderLayout(0, 6)); p3.setBackground(WHITE);
        JLabel l3 = new JLabel("Status Visibilitas di Dashboard Siswa"); l3.setFont(new Font("Segoe UI", Font.BOLD, 12)); l3.setForeground(TEXT_DARK);
        cbStatus = new JComboBox<>(new String[]{"DITAMPILKAN", "SEMBUNYI"}); cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13)); cbStatus.setPreferredSize(new Dimension(0, 38));
        p3.add(l3, BorderLayout.NORTH); p3.add(cbStatus, BorderLayout.CENTER);

        // Input Detail JTextArea
        JPanel p4 = new JPanel(new BorderLayout(0, 6)); p4.setBackground(WHITE);
        JLabel l4 = new JLabel("Rincian Instruksi Lengkap (Saat Di-klik Siswa)"); l4.setFont(new Font("Segoe UI", Font.BOLD, 12)); l4.setForeground(TEXT_DARK);
        txtDetail = new JTextArea(2, 0); txtDetail.setFont(new Font("Segoe UI", Font.PLAIN, 13)); txtDetail.setLineWrap(true); txtDetail.setWrapStyleWord(true);
        JScrollPane scrollDetail = new JScrollPane(txtDetail);
        scrollDetail.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        p4.add(l4, BorderLayout.NORTH); p4.add(scrollDetail, BorderLayout.CENTER);

        panelFormInput.add(p1); panelFormInput.add(p2);
        panelFormInput.add(p3); panelFormInput.add(p4);
        panelFormOuter.add(panelFormInput, BorderLayout.CENTER);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panelTombol.setBackground(WHITE);
        panelTombol.setBorder(new EmptyBorder(16, 0, 0, 0));

        btnSimpan = new JButton("Simpan Data"); 
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        btnSimpan.setBackground(BLUE_PRIMARY); 
        btnSimpan.setForeground(Color.BLACK); 
        btnSimpan.setPreferredSize(new Dimension(130, 38)); 
        btnSimpan.setFocusPainted(false); 
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnHapus = new JButton("Hapus"); 
        btnHapus.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        btnHapus.setBackground(RED_DANGER); 
        btnHapus.setForeground(Color.BLACK); 
        btnHapus.setPreferredSize(new Dimension(90, 38)); 
        btnHapus.setFocusPainted(false); 
        btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnReset = new JButton("Reset Form"); 
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        btnReset.setBackground(GRAY_RESET); 
        btnReset.setForeground(Color.BLACK); 
        btnReset.setPreferredSize(new Dimension(110, 38)); 
        btnReset.setFocusPainted(false); 
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panelTombol.add(btnHapus);
        panelTombol.add(btnReset);
        panelTombol.add(btnSimpan);
        panelFormOuter.add(panelTombol, BorderLayout.SOUTH);
        add(panelFormOuter, BorderLayout.SOUTH);

        initActionEvents();
        loadDataDariDatabase();
    }

    private void initActionEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && !e.getValueIsAdjusting() && row < listIds.size()) {
                selectedId = listIds.get(row);
                txtKategori.setText((String) tableModel.getValueAt(row, 0));
                txtRingkasan.setText((String) tableModel.getValueAt(row, 1));
                cbStatus.setSelectedItem((String) tableModel.getValueAt(row, 2));
                
                try (Connection conn = DatabaseConfig.getKoneksi()) {
                    String sql = "SELECT deskripsi_panjang FROM tbl_pengaturan WHERE id_pengaturan = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, selectedId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) txtDetail.setText(rs.getString("deskripsi_panjang"));
                        }
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        btnSimpan.addActionListener(e -> {
            if (txtKategori.getText().trim().isEmpty() || txtRingkasan.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kategori dan Ringkasan wajib diisi!");
                return;
            }

            try (Connection conn = DatabaseConfig.getKoneksi()) {
                if (selectedId == -1) {
                    String sql = "INSERT INTO tbl_pengaturan (kunci_parameter, nilai_parameter, deskripsi_panjang, status_tampil) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, txtKategori.getText().trim());
                        ps.setString(2, txtRingkasan.getText().trim());
                        ps.setString(3, txtDetail.getText().trim());
                        ps.setString(4, cbStatus.getSelectedItem().toString());
                        ps.executeUpdate();
                    }
                } else {
                    String sql = "UPDATE tbl_pengaturan SET kunci_parameter=?, nilai_parameter=?, deskripsi_panjang=?, status_tampil=? WHERE id_pengaturan=?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, txtKategori.getText().trim());
                        ps.setString(2, txtRingkasan.getText().trim());
                        ps.setString(3, txtDetail.getText().trim());
                        ps.setString(4, cbStatus.getSelectedItem().toString());
                        ps.setInt(5, selectedId);
                        ps.executeUpdate();
                    }
                }
                resetForm();
                loadDataDariDatabase();
                JOptionPane.showMessageDialog(this, "Data Informasi Berhasil Diperbarui secara Realtime!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal memproses data: " + ex.getMessage());
            }
        });

        btnHapus.addActionListener(e -> {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Silakan pilih baris tabel yang ingin dihapus terlebih dahulu.");
                return;
            }
            int konf = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus informasi ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (konf == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConfig.getKoneksi()) {
                    String sql = "DELETE FROM tbl_pengaturan WHERE id_pengaturan = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, selectedId);
                        ps.executeUpdate();
                    }
                    resetForm();
                    loadDataDariDatabase();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        btnReset.addActionListener(e -> resetForm());
    }

    private void loadDataDariDatabase() {
        tableModel.setRowCount(0);
        listIds.clear();
        try (Connection conn = DatabaseConfig.getKoneksi()) {
            String sql = "SELECT id_pengaturan, kunci_parameter, nilai_parameter, status_tampil FROM tbl_pengaturan ORDER BY id_pengaturan DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listIds.add(rs.getInt("id_pengaturan"));
                    tableModel.addRow(new Object[]{
                        rs.getString("kunci_parameter"),
                        rs.getString("nilai_parameter"),
                        rs.getString("status_tampil")
                    });
                }
            }
        } catch (Exception ex) {
            System.out.println("Gagal memuat database admin: " + ex.getMessage());
        }
    }

    private void resetForm() {
        selectedId = -1;
        txtKategori.setText("");
        txtRingkasan.setText("");
        txtDetail.setText("");
        cbStatus.setSelectedIndex(0);
        table.clearSelection();
    }
}