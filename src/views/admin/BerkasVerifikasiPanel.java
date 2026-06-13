package views.admin;

import config.DatabaseConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Manajemen Verifikasi Berkas Dokumen dengan Filter Kategori, Pewarnaan Status, dan Cloud Preview Biner.
 * Terintegrasi aman dengan skema relasional tabel tbl_berkas dan tbl_siswa.
 * 🎯 REVISI SINKRONISASI UI: Layout Atas, Ukuran JComponent, dan Tombol Disamakan dengan KelolaPendaftarPanel
 * @author Rivaldi
 */
public class BerkasVerifikasiPanel extends JPanel {

    // <-------------------- KOMPONEN FORM -------------------->
    private JTable tblBerkas;
    private DefaultTableModel modelBerkas;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField txtCari;
    private JComboBox<String> cbKategori; 
    private JComboBox<String> cbFilterStatus;
    private JButton btnRefresh;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari BerkasVerifikasiPanel dan memuat data awal.
     */
    public BerkasVerifikasiPanel() {
        initUI();
        loadBerkasData();
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    /**
     * Merender dan mengonfigurasi tata letak komponen utama antarmuka verifikasi berkas.
     */
    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250)); // Warna abu-abu soft modern
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25)); // Padding luar

        // ==========================================
        // 1. PANEL ATAS (Judul & Bar Pencarian Kategori - SINKRONISASI KELOLA_DATA)
        // ==========================================
        JPanel panelUtara = new JPanel(new BorderLayout(0, 15));
        panelUtara.setBackground(new Color(245, 246, 250));

        // Judul Utama (Mengikuti gaya Kelola Data Pendaftar)
        JLabel title = new JLabel("VERIFIKASI BERKAS DOKUMEN SISWA");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(47, 53, 66));
        panelUtara.add(title, BorderLayout.NORTH);

        // Bar Kendali (Flat FlowLayout Kiri - Mencopot Cardbox Border Lama)
        JPanel pnlAksiFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlAksiFilter.setBackground(new Color(245, 246, 250));

        JLabel lblCari = new JLabel("Cari Berkas:");
        lblCari.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCari.setForeground(new Color(47, 53, 66));

        String[] listKategori = {"Semua Kolom", "No Daftar", "Nama Siswa", "Jenis Dokumen"};
        cbKategori = new JComboBox<>(listKategori);
        cbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbKategori.setPreferredSize(new Dimension(140, 35)); // Mengikuti ukuran Kelola Data Pendaftar (35px)
        cbKategori.addActionListener(e -> jalankanKombinasiFilterDataGrid());
        
        txtCari = new JTextField();
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCari.setPreferredSize(new Dimension(220, 35)); // Lebar 220px, Tinggi 35px proporsional
        
        JLabel lblStatus = new JLabel("Filter Status:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(new Color(47, 53, 66));

        cbFilterStatus = new JComboBox<>(new String[]{"SEMUA STATUS", "BELUM_UPLOAD", "MENUNGGU_VERIFIKASI", "DIVERIFIKASI", "DITOLAK"});
        cbFilterStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFilterStatus.setPreferredSize(new Dimension(145, 35)); // Tinggi 35px proporsional
        cbFilterStatus.addActionListener(e -> jalankanKombinasiFilterDataGrid());

        btnRefresh = new JButton("Reset");
        btnRefresh.setPreferredSize(new Dimension(100, 35)); // Ukuran tombol reset sinkron
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(148, 163, 184));
        btnRefresh.setForeground(Color.BLACK); // 🎯 TEXT COLOR: BLACK
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> {
            txtCari.setText("");
            cbKategori.setSelectedIndex(0);
            cbFilterStatus.setSelectedIndex(0);
            rowSorter.setRowFilter(null);
            loadBerkasData();
        });

        pnlAksiFilter.add(lblCari);
        pnlAksiFilter.add(cbKategori);
        pnlAksiFilter.add(txtCari);
        pnlAksiFilter.add(lblStatus);
        pnlAksiFilter.add(cbFilterStatus);
        pnlAksiFilter.add(btnRefresh);

        panelUtara.add(pnlAksiFilter, BorderLayout.CENTER);
        add(panelUtara, BorderLayout.NORTH);

        // ==========================================
        // 2. PANEL TENGAH (Tabel Data Grid)
        // ==========================================
        String[] columns = {"No Daftar", "Nama Siswa", "Jenis Dokumen", "Nama File", "Ukuran (KB)", "Tanggal Upload", "Status"};
        modelBerkas = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        tblBerkas = new JTable(modelBerkas);
        tblBerkas.setRowHeight(35); 
        tblBerkas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblBerkas.setShowGrid(false);
        tblBerkas.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader tableHeader = tblBerkas.getTableHeader();
        tableHeader.setReorderingAllowed(false);
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(236, 240, 241));
        tableHeader.setForeground(new Color(44, 62, 80));
        tableHeader.setPreferredSize(new Dimension(0, 35));

        tblBerkas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component component = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                
                if (!isS) {
                    component.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    component.setForeground(new Color(15, 23, 42));
                    
                    if (c == 6 && v != null) {
                        String st = v.toString();
                        if (st.equals("DIVERIFIKASI")) {
                            component.setBackground(new Color(220, 252, 231)); 
                            component.setForeground(new Color(21, 128, 61));
                        } else if (st.equals("DITOLAK")) {
                            component.setBackground(new Color(254, 226, 226)); 
                            component.setForeground(new Color(185, 28, 28));
                        } else if (st.equals("MENUNGGU_VERIFIKASI")) {
                            component.setBackground(new Color(254, 243, 199)); 
                            component.setForeground(new Color(180, 83, 9));
                        }
                    }
                } else {
                    component.setBackground(new Color(219, 234, 254)); 
                    component.setForeground(Color.BLACK);
                }
                return component;
            }
        });

        rowSorter = new TableRowSorter<>(modelBerkas);
        tblBerkas.setRowSorter(rowSorter);

        txtCari.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                jalankanKombinasiFilterDataGrid();
            }
        });

        tblBerkas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblBerkas.getSelectedRow() != -1) {
                    int rowIdx = tblBerkas.getSelectedRow();
                    int modelIdx = tblBerkas.convertRowIndexToModel(rowIdx);
                    
                    String noDaftar = modelBerkas.getValueAt(modelIdx, 0).toString();
                    String namaSiswa = modelBerkas.getValueAt(modelIdx, 1).toString();
                    String jenisDokumen = modelBerkas.getValueAt(modelIdx, 2).toString();
                    String namaFile = modelBerkas.getValueAt(modelIdx, 3).toString();
                    String statusSaatIni = modelBerkas.getValueAt(modelIdx, 6).toString();
                    
                    bukaDialogAksiVerifikasiBerkas(noDaftar, namaSiswa, jenisDokumen, namaFile, statusSaatIni);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblBerkas);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 221, 225), 1));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
    }

    private void jalankanKombinasiFilterDataGrid() {
        String teksKeyword = txtCari.getText().trim();
        String kategori = cbKategori.getSelectedItem().toString();
        String opsiStatus = cbFilterStatus.getSelectedItem().toString();
        
        java.util.List<RowFilter<Object, Object>> daftarFilters = new ArrayList<>();
        
        if (!teksKeyword.isEmpty()) {
            if (kategori.equalsIgnoreCase("No Daftar")) {
                daftarFilters.add(RowFilter.regexFilter("(?i)" + teksKeyword, 0));
            } else if (kategori.equalsIgnoreCase("Nama Siswa")) {
                daftarFilters.add(RowFilter.regexFilter("(?i)" + teksKeyword, 1));
            } else if (kategori.equalsIgnoreCase("Jenis Dokumen")) {
                daftarFilters.add(RowFilter.regexFilter("(?i)" + teksKeyword, 2));
            } else {
                daftarFilters.add(RowFilter.regexFilter("(?i)" + teksKeyword));
            }
        }
        
        if (!"SEMUA STATUS".equals(opsiStatus)) {
            daftarFilters.add(RowFilter.regexFilter("^" + opsiStatus + "$", 6));
        }
        
        if (daftarFilters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(daftarFilters));
        }
    }

    public void loadBerkasData() {
        modelBerkas.setRowCount(0);
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return;

        String sql = 
                "SELECT s.nomor_pendaftaran, b.nama_lengkap, br.jenis_berkas, br.nama_file_asli, " +
                "IFNULL(ROUND(br.ukuran_file / 1024, 2), 0) AS ukuran_kb, br.tanggal_upload, br.status " +
                "FROM tbl_berkas br " +
                "LEFT JOIN tbl_siswa s ON br.id_siswa = s.id_siswa " +
                "LEFT JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa " +
                "ORDER BY br.tanggal_upload DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("nomor_pendaftaran"));
                row.add(rs.getString("nama_lengkap") == null ? "-" : rs.getString("nama_lengkap"));
                row.add(rs.getString("jenis_berkas"));
                row.add(rs.getString("nama_file_asli") == null ? "Belum Upload" : rs.getString("nama_file_asli"));
                row.add(rs.getDouble("ukuran_kb"));
                row.add(rs.getTimestamp("tanggal_upload"));
                row.add(rs.getString("status"));
                modelBerkas.addRow(row);
            }
        } catch (SQLException e) {
            // Kronologi log kegagalan internal diredam aman
        }
    }

    private void bukaDialogAksiVerifikasiBerkas(String noDaftar, String namaSiswa, String jenisDokumen, String namaFile, String status) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parentWindow, "Manajemen Verifikasi Berkas — [" + noDaftar + "]", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(580, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(12, 12));
        ((JPanel) dlg.getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        dlg.getContentPane().setBackground(Color.WHITE);

        JPanel pnlInfo = new JPanel(new GridLayout(4, 1, 6, 6));
        pnlInfo.setOpaque(false);
        pnlInfo.add(new JLabel("Nama Calon Siswa :  " + namaSiswa));
        pnlInfo.add(new JLabel("Jenis Dokumen    :  " + jenisDokumen));
        pnlInfo.add(new JLabel("Nama File Server  :  " + namaFile));
        pnlInfo.add(new JLabel("Status Verifikasi :  " + status));
        dlg.add(pnlInfo, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout(6, 6));
        pnlCenter.setOpaque(false);
        JLabel lblNotes = new JLabel("Catatan Revisi Panitia (Wajib Diisi Jika Dokumen Ditolak/Reject):");
        lblNotes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlCenter.add(lblNotes, BorderLayout.NORTH);
        
        JTextArea txtCatatan = new JTextArea(5, 20);
        txtCatatan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        pnlCenter.add(new JScrollPane(txtCatatan), BorderLayout.CENTER);
        dlg.add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBottom.setOpaque(false);

        JButton btnPreview = new JButton("Preview Berkas Cloud");
        JButton btnApprove = new JButton("Approve");
        JButton btnReject = new JButton("Reject");
        JButton btnBatal = new JButton("Batal");

        btnPreview.setPreferredSize(new Dimension(170, 38));
        btnApprove.setPreferredSize(new Dimension(100, 38));
        btnReject.setPreferredSize(new Dimension(100, 38));
        btnBatal.setPreferredSize(new Dimension(90, 38));

        btnPreview.setBackground(new Color(52, 152, 219));
        btnPreview.setForeground(Color.BLACK); // 🎯 TEXT COLOR: BLACK
        btnApprove.setBackground(new Color(46, 204, 113));
        btnApprove.setForeground(Color.BLACK); // 🎯 TEXT COLOR: BLACK
        btnReject.setBackground(new Color(231, 76, 60));
        btnReject.setForeground(Color.BLACK); // 🎯 TEXT COLOR: BLACK
        btnBatal.setBackground(new Color(220, 221, 225));
        btnBatal.setForeground(Color.BLACK);  // 🎯 TEXT COLOR: BLACK

        btnPreview.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnApprove.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReject.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBatal.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnPreview.setFocusPainted(false);
        btnApprove.setFocusPainted(false);
        btnReject.setFocusPainted(false);
        btnBatal.setFocusPainted(false);

        btnPreview.addActionListener(ev -> {
            if ("Belum Upload".equalsIgnoreCase(namaFile) || namaFile.trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Berkas fisik belum diunggah oleh calon siswa.", "Gagal Membuka", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            byte[] fileBytes = null;
            Connection conn = DatabaseConfig.getKoneksi();
            if (conn == null) return;

            String namaKolomBlobReal = "";
            String sqlCheck = "SELECT * FROM tbl_berkas LIMIT 1";
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
                 ResultSet rsCheck = psCheck.executeQuery()) {
                
                java.sql.ResultSetMetaData metaData = rsCheck.getMetaData();
                int jumlahKolom = metaData.getColumnCount();
                
                for (int i = 1; i <= jumlahKolom; i++) {
                    String typeName = metaData.getColumnTypeName(i);
                    String columnName = metaData.getColumnName(i);
                    
                    if (typeName.contains("BLOB") || typeName.contains("BINARY") || columnName.equalsIgnoreCase("berkas") || columnName.equalsIgnoreCase("file")) {
                        namaKolomBlobReal = columnName;
                        break;
                    }
                }
            } catch (SQLException ex) {
                // Exception diredam aman
            }

            if (namaKolomBlobReal.isEmpty()) {
                namaKolomBlobReal = "berkas"; 
            }

            String queryBlob = 
                    "SELECT " + namaKolomBlobReal + " FROM tbl_berkas WHERE jenis_berkas = ? " +
                    "AND id_siswa = (SELECT id_siswa FROM tbl_siswa WHERE nomor_pendaftaran = ? LIMIT 1) LIMIT 1";
            
            try (PreparedStatement ps = conn.prepareStatement(queryBlob)) {
                ps.setString(1, jenisDokumen);
                ps.setString(2, noDaftar);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        fileBytes = rs.getBytes(namaKolomBlobReal);
                    }
                }
            } catch (SQLException ex) {
                // Exception diredam aman
            }

            if (fileBytes == null || fileBytes.length == 0) {
                JOptionPane.showMessageDialog(dlg, 
                    "<html><body><p style='width: 320px;'>"
                    + "<b>Data Gambar Tidak Ditemukan!</b><br><br>"
                    + "Nama kolom biner terdeteksi sebagai <b>'" + namaKolomBlobReal + "'</b>.<br>"
                    + "String nama file tercatat <i>(" + namaFile + ")</i>, namun field tersebut di database "
                    + "masih bernilai kosong (NULL).<br><br>"
                    + "Silakan unggah ulang file gambar asli lewat panel akun pendaftaran siswa."
                    + "</p></body></html>", 
                    "Data Storage Kosong", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            bukaPopupVisualisasiBerkasSiswa(fileBytes, jenisDokumen + " — " + namaSiswa);
        });

        btnApprove.addActionListener(ev -> {
            boolean statusSukses = eksekusiUpdateStatusDatabase(noDaftar, jenisDokumen, "DIVERIFIKASI", txtCatatan.getText());
            if (statusSukses) {
                JOptionPane.showMessageDialog(dlg, "Berkas [" + jenisDokumen + "] berhasil disetujui!", "Approve Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadBerkasData();
            }
        });

        btnReject.addActionListener(ev -> {
            if (txtCatatan.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Alasan penolakan / catatan revisi wajib diisi agar dipahami siswa!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean statusSukses = eksekusiUpdateStatusDatabase(noDaftar, jenisDokumen, "DITOLAK", txtCatatan.getText());
            if (statusSukses) {
                JOptionPane.showMessageDialog(dlg, "Berkas pendaftaran ditolak. Catatan revisi sukses dikirim ke siswa.", "Reject Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadBerkasData();
            }
        });

        btnBatal.addActionListener(ev -> dlg.dispose());

        pnlBottom.add(btnPreview);
        pnlBottom.add(btnApprove);
        pnlBottom.add(btnReject);
        pnlBottom.add(btnBatal);
        dlg.add(pnlBottom, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void bukaPopupVisualisasiBerkasSiswa(byte[] bytes, String labelJudul) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog popupImage = new JDialog(parentWindow, "Pratinjau Dokumen — " + labelJudul, Dialog.ModalityType.APPLICATION_MODAL);
        popupImage.setSize(500, 450); 
        popupImage.setLocationRelativeTo(parentWindow);
        popupImage.setLayout(new BorderLayout());
        
        JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
        pnlMain.setBackground(Color.WHITE);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblFoto = new JLabel();
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblFoto.setBackground(Color.WHITE);
        lblFoto.setOpaque(true);

        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(bytes);
            Image imgMentah = javax.imageio.ImageIO.read(bais);
            
            if (imgMentah != null) {
                ImageIcon iconFoto = new ImageIcon(imgMentah);
                lblFoto.setIcon(iconFoto);
            } else {
                triggerFallbackVisualGambarRusak(lblFoto, "FORMAT BERKAS TIDAK DIDUKUNG / RUSAK");
            }
        } catch (Exception ex) {
            triggerFallbackVisualGambarRusak(lblFoto, "GAGAL MEMPROSES PRATINJAU BINER");
        }

        JScrollPane scrollFoto = new JScrollPane(lblFoto);
        scrollFoto.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollFoto.getViewport().setBackground(Color.WHITE);
        pnlMain.add(scrollFoto, BorderLayout.CENTER);

        JPanel pnlButtonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        pnlButtonRow.setOpaque(false);
        
        JButton btnClose = new JButton("Tutup Pratinjau");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.setForeground(Color.BLACK); // 🎯 TEXT COLOR: BLACK
        btnClose.setPreferredSize(new Dimension(120, 32));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> popupImage.dispose());
        
        pnlButtonRow.add(btnClose);
        pnlMain.add(pnlButtonRow, BorderLayout.SOUTH);

        popupImage.add(pnlMain);
        popupImage.setVisible(true);
    }

    private void triggerFallbackVisualGambarRusak(JLabel labelTarget, String pesanUtama) {
        java.awt.image.BufferedImage imgFallback = new java.awt.image.BufferedImage(450, 350, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imgFallback.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(new Color(248, 250, 252));
        g2d.fillRect(0, 0, 450, 350);
        
        g2d.setColor(new Color(254, 226, 226));
        g2d.fillRoundRect(25, 25, 400, 280, 12, 12);
        g2d.setColor(new Color(239, 68, 68));
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{6.0f}, 0.0f));
        g2d.drawRoundRect(25, 25, 400, 280, 12, 12);
        
        g2d.setColor(new Color(220, 38, 38));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.drawString(pesanUtama, 60, 140);
        
        g2d.setColor(new Color(100, 116, 139));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString("Struktur biner dokumen cloud kosong atau tidak dapat di-render.", 55, 175);
        g2d.drawString("Silakan minta siswa untuk mengunggah ulang file asli", 55, 195);
        g2d.drawString("melalui dashboard pendaftaran masing-masing akun.", 55, 215);
        g2d.dispose();
        
        labelTarget.setIcon(new ImageIcon(imgFallback));
    }

    private boolean eksekusiUpdateStatusDatabase(String nomorPendaftaran, String jenisBerkas, String statusBaru, String notesPanitia) {
        String sql = 
                "UPDATE tbl_berkas SET status = ?, alasan_ditolak = ?, tanggal_upload = NOW() " +
                "WHERE jenis_berkas = ? AND id_siswa = (SELECT id_siswa FROM tbl_siswa WHERE nomor_pendaftaran = ? LIMIT 1)";
        
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, statusBaru);
            ps.setString(2, notesPanitia.trim().isEmpty() ? null : notesPanitia.trim());
            ps.setString(3, jenisBerkas);
            ps.setString(4, nomorPendaftaran);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            return false;
        }
    }
}