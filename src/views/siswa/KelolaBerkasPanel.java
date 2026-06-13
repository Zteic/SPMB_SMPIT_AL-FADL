package views.siswa;

import config.DatabaseConfig;
import config.SessionManager;
import controllers.AutentikasiController;
import views.components.RoundedPanel;
import views.components.CustomButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Pengelolaan Dokumen Persyaratan Mandiri Calon Siswa (Anti Duplicate Insert Engine).
 * Sinkronisasi Rapor Semester 5 & 6, Validasi Batas 5 MB, dan Audit Logs.
 * * @author Rivaldi
 */
public class KelolaBerkasPanel extends JPanel {

    // <-------------------- KONSTANTA WARNA & FONT -------------------->
    private static final Color BG         = new Color(243, 244, 246);
    private static final Color WHITE      = Color.WHITE;
    private static final Color PRIMARY    = new Color(37, 99, 235);
    private static final Color HIJAU      = new Color(22, 163, 74);
    private static final Color HIJAU_BG   = new Color(220, 252, 231);
    private static final Color KUNING     = new Color(202, 138, 4);
    private static final Color KUNING_BG  = new Color(254, 249, 195);
    private static final Color MERAH      = new Color(220, 38, 38);
    private static final Color MERAH_BG   = new Color(254, 226, 226);
    private static final Color BIRU       = new Color(59, 130, 246);
    private static final Color BIRU_BG    = new Color(219, 234, 254);
    private static final Color ABU        = new Color(107, 114, 128);
    private static final Color ABU_BG     = new Color(243, 244, 246);
    private static final Color TEXT_DARK  = new Color(17, 24, 39);
    private static final Color TEXT_LIGHT = new Color(107, 114, 128);
    private static final Color BORDER_CLR = new Color(229, 231, 235);

    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_BADGE  = new Font("Segoe UI", Font.BOLD, 11);

    private static final long MAX_BYTES    = 5L * 1024 * 1024;
    private static final String DIR_ROOT   = "uploads/";

    // <-------------------- CONTROLLER -------------------->
    private final AutentikasiController auditCtrl = new AutentikasiController();

    // <-------------------- KOMPONEN FORM -------------------->
    private JLabel lblNama, lblNoReg, lblJalur, lblStatusVerif;
    private JProgressBar progressBar;
    private JLabel lblProgressText;
    private JPanel docsContainer;
    private Map<String, String[]> documentData = new LinkedHashMap<>();
    private List<String> requiredDocs = new ArrayList<>();
    private DefaultTableModel riwModel;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari KelolaBerkasPanel dan menginisialisasi folder lokal.
     */
    public KelolaBerkasPanel() {
        buatFolderLokal();
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(14, 18, 14, 18));
        add(buildContent(), BorderLayout.CENTER);
        loadAllData();
    }

    // <-------------------- UTILITY -------------------->
    private void buatFolderLokal() {
        if (!new File(DIR_ROOT).exists()) new File(DIR_ROOT).mkdirs();
        for (String sub : new String[]{"kk", "akta", "foto", "stpjm", "ijazah", "rapor", "sertifikat", "pembayaran"}) {
            File f = new File(DIR_ROOT + sub);
            if (!f.exists()) f.mkdirs();
        }
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    /**
     * Menyusun susunan kontainer layout utama halaman kelola berkas siswa.
     */
    private JPanel buildContent() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.gridx = 0;

        gbc.gridy = 0; gbc.weighty = 0.13; gbc.insets = new Insets(0, 0, 10, 0);
        main.add(buildHeaderCard(), gbc);

        gbc.gridy = 1; gbc.weighty = 0.55; gbc.insets = new Insets(0, 0, 10, 0);
        main.add(buildBerkasCard(), gbc);

        gbc.gridy = 2; gbc.weighty = 0.32; gbc.insets = new Insets(0, 0, 0, 0);
        main.add(buildRiwayatCard(), gbc);

        return main;
    }

    private JPanel buildHeaderCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 14, WHITE);
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Pengelolaan Dokumen Persyaratan");
        title.setFont(FONT_HEADER); title.setForeground(TEXT_DARK);

        JPanel infoGrid = new JPanel(new GridBagLayout());
        infoGrid.setOpaque(false);
        infoGrid.setBorder(new EmptyBorder(12, 0, 4, 0)); 

        GridBagConstraints gbcInfo = new GridBagConstraints();
        gbcInfo.fill = GridBagConstraints.HORIZONTAL;
        gbcInfo.weighty = 1.0;

        lblNama        = makeVal("-");
        lblNoReg       = makeVal("-");
        lblJalur       = makeVal("-");
        lblStatusVerif = makeVal("-");

        gbcInfo.gridx = 0; gbcInfo.gridy = 0; gbcInfo.weightx = 0.0;
        gbcInfo.insets = new Insets(6, 12, 6, 4); 
        infoGrid.add(makeLbl("Nama Lengkap"), gbcInfo);

        gbcInfo.gridx = 1; gbcInfo.gridy = 0; gbcInfo.weightx = 0.4; 
        gbcInfo.insets = new Insets(6, 0, 6, 32); 
        infoGrid.add(lblNama, gbcInfo);

        gbcInfo.gridx = 2; gbcInfo.gridy = 0; gbcInfo.weightx = 0.0;
        gbcInfo.insets = new Insets(6, 12, 6, 4);
        infoGrid.add(makeLbl("No. Pendaftaran"), gbcInfo);

        gbcInfo.gridx = 3; gbcInfo.gridy = 0; gbcInfo.weightx = 0.4;
        gbcInfo.insets = new Insets(6, 0, 6, 12);
        infoGrid.add(lblNoReg, gbcInfo);

        gbcInfo.gridx = 0; gbcInfo.gridy = 1; gbcInfo.weightx = 0.0;
        gbcInfo.insets = new Insets(6, 12, 6, 4);
        infoGrid.add(makeLbl("Jalur Pendaftaran"), gbcInfo);

        gbcInfo.gridx = 1; gbcInfo.gridy = 1; gbcInfo.weightx = 0.4;
        gbcInfo.insets = new Insets(6, 0, 6, 32);
        infoGrid.add(lblJalur, gbcInfo);

        gbcInfo.gridx = 2; gbcInfo.gridy = 1; gbcInfo.weightx = 0.0;
        gbcInfo.insets = new Insets(6, 12, 6, 4);
        infoGrid.add(makeLbl("Status Kelengkapan"), gbcInfo);

        gbcInfo.gridx = 3; gbcInfo.gridy = 1; gbcInfo.weightx = 0.4;
        gbcInfo.insets = new Insets(6, 0, 6, 12);
        infoGrid.add(lblStatusVerif, gbcInfo);

        JPanel accent = new JPanel(); accent.setBackground(PRIMARY); accent.setPreferredSize(new Dimension(0, 3));

        card.add(title, BorderLayout.NORTH); 
        card.add(infoGrid, BorderLayout.CENTER); 
        card.add(accent, BorderLayout.SOUTH);
        return card;
    }

    private JLabel makeLbl(String text) {
        JLabel l = new JLabel(text + ":");
        l.setFont(FONT_SMALL); l.setForeground(TEXT_LIGHT);
        return l;
    }

    private JLabel makeVal(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BADGE); l.setForeground(TEXT_DARK);
        return l;
    }

    private JPanel buildBerkasCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 14, WHITE);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel progressArea = new JPanel(new BorderLayout(10, 4));
        progressArea.setOpaque(false); progressArea.setBorder(new EmptyBorder(0, 0, 12, 0));

        lblProgressText = new JLabel("Kelengkapan Berkas: 0 / 0 Dokumen");
        lblProgressText.setFont(FONT_HEADER); lblProgressText.setForeground(TEXT_DARK);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0); progressBar.setStringPainted(false); progressBar.setForeground(HIJAU);
        progressBar.setBackground(new Color(229, 231, 235)); progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setPreferredSize(new Dimension(0, 10));

        progressArea.add(lblProgressText, BorderLayout.NORTH);
        progressArea.add(progressBar, BorderLayout.CENTER);

        docsContainer = new JPanel();
        docsContainer.setLayout(new BoxLayout(docsContainer, BoxLayout.Y_AXIS));
        docsContainer.setBackground(WHITE);
        
        JScrollPane scroll = new JScrollPane(docsContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false); btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnRefresh = actionBtn("Refresh Sinkronisasi", ABU);
        btnRefresh.setForeground(Color.WHITE); 
        btnRefresh.addActionListener(e -> loadAllData());
        btnPanel.add(btnRefresh);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(progressArea, BorderLayout.NORTH); center.add(scroll, BorderLayout.CENTER); center.add(btnPanel, BorderLayout.SOUTH);

        card.add(center, BorderLayout.CENTER);
        return card;
    }

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(FONT_SMALL); t.setRowHeight(30); t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false); t.setGridColor(BORDER_CLR); t.setBackground(WHITE);
        t.setSelectionBackground(new Color(219, 234, 254)); t.setSelectionForeground(TEXT_DARK);
        t.getTableHeader().setFont(FONT_BADGE); t.getTableHeader().setBackground(new Color(249, 250, 251));
        t.getTableHeader().setForeground(TEXT_DARK); t.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));
        t.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer lr = new DefaultTableCellRenderer(); lr.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < model.getColumnCount(); i++) t.getColumnModel().getColumn(i).setCellRenderer(lr);
        return t;
    }

    private JButton actionBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground()); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setBackground(color); btn.setForeground(Color.BLACK); btn.setFont(FONT_BADGE);
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setOpaque(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(6, 14, 6, 14));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(color.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }

    private JPanel buildRiwayatCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 14, WHITE);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel title = new JLabel("Riwayat Verifikasi Berkas (Realtime)");
        title.setFont(FONT_HEADER); title.setForeground(TEXT_DARK); title.setBorder(new EmptyBorder(0, 0, 8, 0));
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"Jenis Dokumen", "Tanggal Upload", "Status", "Nama Verifikator", "Tanggal Verifikasi", "Catatan Admin"};
        riwModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable riwTable = buildStyledTable(riwModel);
        riwTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        riwTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        riwTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        riwTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        riwTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        riwTable.getColumnModel().getColumn(5).setPreferredWidth(160);

        card.add(new JScrollPane(riwTable), BorderLayout.CENTER);
        return card;
    }

    // <-------------------- DATABASE QUERY -------------------->
    private void loadHeaderInfo() {
        if (!SessionManager.isLoggedIn()) return;
        String username = SessionManager.getCurrentUser().getUsername();

        try (Connection conn = DatabaseConfig.getKoneksi()) {
            String sql = 
                    "SELECT u.nama_lengkap AS nama_user, s.nomor_pendaftaran, COALESCE(j.nama_jalur, '-') AS jalur " +
                    "FROM tbl_users u " +
                    "LEFT JOIN tbl_siswa s ON u.username = s.nomor_pendaftaran " +
                    "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur " +
                    "WHERE u.username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        final String nama  = rs.getString("nama_user");
                        final String noReg = rs.getString("nomor_pendaftaran") != null ? rs.getString("nomor_pendaftaran") : username;
                        final String jalur = rs.getString("jalur");
                        SwingUtilities.invokeLater(() -> {
                            lblNama.setText(nama); lblNoReg.setText(noReg); lblJalur.setText(jalur);
                        });
                    }
                }
            }
        } catch (SQLException e) {
            // SQL Exception diredam aman
        }
    }

    private void loadBerkasData() {
        if (!SessionManager.isLoggedIn()) return;

        try (Connection conn = DatabaseConfig.getKoneksi()) {
            int idSiswa = getIdSiswa(conn);
            if (idSiswa == -1) return;

            String jalur = getJalurSiswa(conn, idSiswa);
            boolean bTahfidz = checkTahfidzSiswa(conn, idSiswa);

            List<String> wajib = new ArrayList<>(Arrays.asList(
                "Kartu Keluarga", "Akta Kelahiran", "Pas Foto", "Ijazah / SKL", 
                "Rapor Semester 5", "Rapor Semester 6", "KTP Ayah", "KTP Ibu", 
                "Surat Pernyataan Orang Tua"
            ));

            if (jalur != null) {
                String j = jalur.toUpperCase();
                if (j.contains("PRESTASI")) wajib.add("Sertifikat Prestasi");
                if (j.contains("AFIRMASI")) wajib.add("KIP / PKH");
                if (j.contains("MUTASI")) wajib.add("Surat Pindah Tugas Orang Tua");
            }
            if (bTahfidz) wajib.add("Sertifikat Tahfidz");
            wajib.add("Bukti Pembayaran");

            Map<String, String[]> uploads = new LinkedHashMap<>();
            String sql = 
                    "SELECT jenis_berkas, nama_file_asli, ukuran_file, tanggal_upload, status, nama_file_server, catatan_verifikator " +
                    "FROM tbl_berkas WHERE id_siswa = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    while (rs.next()) {
                        Timestamp ts = rs.getTimestamp("tanggal_upload");
                        String dbStatus = rs.getString("status");
                        if (dbStatus == null) dbStatus = "BELUM_UPLOAD";
                        
                        uploads.put(rs.getString("jenis_berkas"), new String[]{
                                rs.getString("nama_file_asli") == null ? "Belum Diunggah" : rs.getString("nama_file_asli"),
                                rs.getLong("ukuran_file") == 0 ? "-" : (rs.getLong("ukuran_file") / 1024) + " KB",
                                ts == null ? "-" : sdf.format(ts),
                                dbStatus,
                                rs.getString("nama_file_server"),
                                rs.getString("catatan_verifikator") 
                        });
                    }
                }
            }

            final List<String> docList = wajib;
            final Map<String, String[]> up = uploads;

            SwingUtilities.invokeLater(() -> {
                requiredDocs = docList; documentData = up;
                docsContainer.removeAll();
                int uploadedCount = 0; int no = 1;

                for (String doc : requiredDocs) {
                    String[] data = documentData.getOrDefault(doc, new String[]{"Belum Diunggah", "-", "-", "BELUM_UPLOAD", null, ""});
                    if (data[4] != null && !"BELUM_UPLOAD".equalsIgnoreCase(data[3])) {
                        uploadedCount++;
                    }
                    docsContainer.add(buildDocRow(no++, doc, data));
                }

                int total = requiredDocs.size();
                int pct = total == 0 ? 0 : (uploadedCount * 100 / total);
                progressBar.setValue(pct);
                progressBar.setForeground(pct == 100 ? HIJAU : pct > 50 ? KUNING : MERAH);
                lblProgressText.setText(uploadedCount + " / " + total + " Dokumen (" + pct + "%)");
                
                if (uploadedCount >= total) {
                    lblStatusVerif.setText("Siap Diverifikasi / Selesai"); lblStatusVerif.setForeground(HIJAU);
                } else {
                    lblStatusVerif.setText("Belum Lengkap"); lblStatusVerif.setForeground(MERAH);
                }
                docsContainer.revalidate(); docsContainer.repaint();
            });

        } catch (SQLException e) {
            // SQL Exception diredam aman
        }
    }

    private JPanel buildDocRow(int no, final String jenisDoc, String[] data) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));

        JPanel row = new JPanel(new BorderLayout(10, 10));
        row.setBackground(WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); left.setOpaque(false);
        JLabel lblNo = new JLabel(String.valueOf(no) + "."); lblNo.setFont(FONT_BODY); lblNo.setForeground(TEXT_LIGHT); lblNo.setPreferredSize(new Dimension(20, 20));
        JLabel lblJenis = new JLabel(jenisDoc); lblJenis.setFont(new Font("Segoe UI", Font.BOLD, 12)); lblJenis.setForeground(TEXT_DARK); lblJenis.setPreferredSize(new Dimension(180, 20));
        JLabel lblFileName = new JLabel(data[0]); lblFileName.setFont(FONT_SMALL); lblFileName.setForeground(TEXT_LIGHT); lblFileName.setPreferredSize(new Dimension(150, 20));
        
        left.add(lblNo); left.add(lblJenis); left.add(lblFileName);
        
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); center.setOpaque(false);
        JLabel lblStatus = new JLabel(mapStatus(data[3])); lblStatus.setFont(FONT_BADGE); lblStatus.setOpaque(true); lblStatus.setBorder(new EmptyBorder(2, 8, 2, 8));
        
        String status = data[3].toUpperCase();
        if ("DIVERIFIKASI".equals(status)) {
            lblStatus.setForeground(HIJAU); lblStatus.setBackground(HIJAU_BG);
        } else if ("MENUNGGU_VERIFIKASI".equals(status)) {
            lblStatus.setForeground(KUNING); lblStatus.setBackground(KUNING_BG);
        } else if ("DITOLAK".equals(status) || "PERLU_REVISI".equals(status)) {
            lblStatus.setForeground(MERAH); lblStatus.setBackground(MERAH_BG);
        } else {
            lblStatus.setForeground(ABU); lblStatus.setBackground(ABU_BG);
        }
        center.add(lblStatus);
        
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5)); right.setOpaque(false);
        final String fileServerName = data[4];
        final String catatanAdmin = (data.length > 5 && data[5] != null) ? data[5] : "";

        if (fileServerName == null || "BELUM_UPLOAD".equals(status)) {
            JButton btnUpload = actionBtn("Upload Berkas", PRIMARY);
            btnUpload.setForeground(Color.WHITE); 
            btnUpload.addActionListener(e -> eksekusiUploadSpecific(jenisDoc, false));
            right.add(btnUpload);
        } else {
            JButton btnLihat = actionBtn("Lihat", new Color(217, 119, 6));
            btnLihat.setForeground(Color.WHITE); 
            btnLihat.addActionListener(e -> eksekusiLihatSpecific(jenisDoc, fileServerName));
            right.add(btnLihat);
        

            if ("DITOLAK".equals(status) || "PERLU_REVISI".equals(status)) {
                JButton btnGanti = actionBtn("Upload Ulang", MERAH);
                btnGanti.addActionListener(e -> eksekusiUploadSpecific(jenisDoc, true));
                right.add(btnGanti);
            } else if ("MENUNGGU_VERIFIKASI".equals(status)) {
                JButton btnHapus = actionBtn("Batalkan", MERAH);
                btnHapus.addActionListener(e -> eksekusiHapusSpecific(jenisDoc, fileServerName));
                right.add(btnHapus);
            }
        }
        
        row.add(left, BorderLayout.WEST); row.add(center, BorderLayout.CENTER); row.add(right, BorderLayout.EAST);
        wrapper.add(row, BorderLayout.CENTER);

        if (("DITOLAK".equals(status) || "PERLU_REVISI".equals(status)) && !catatanAdmin.isEmpty()) {
            JPanel pnlCatatan = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 5));
            pnlCatatan.setBackground(new Color(254, 242, 242)); 
            JLabel lblCat = new JLabel("Catatan Penolakan Admin: " + catatanAdmin);
            lblCat.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblCat.setForeground(MERAH);
            pnlCatatan.add(lblCat);
            wrapper.add(pnlCatatan, BorderLayout.SOUTH);
        }

        return wrapper;
    }

    private void loadRiwayat() {
        if (!SessionManager.isLoggedIn()) return;

        try (Connection conn = DatabaseConfig.getKoneksi()) {
            int idSiswa = getIdSiswa(conn);
            if (idSiswa == -1) return;

            boolean adaPetugasColumn = false;
            boolean adaTanggalVerifColumn = false;
            
            try {
                DatabaseMetaData meta = conn.getMetaData();
                try (ResultSet columns = meta.getColumns(null, null, "tbl_berkas", null)) {
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        if ("petugas_verifikasi".equalsIgnoreCase(columnName)) adaPetugasColumn = true;
                        if ("tanggal_verifikasi".equalsIgnoreCase(columnName)) adaTanggalVerifColumn = true;
                    }
                }
            } catch (Exception ignored) {}

            String sql = "SELECT jenis_berkas, tanggal_upload, status, " +
                         "COALESCE(catatan_verifikator, '-') AS catatan ";
                         
            if (adaPetugasColumn) sql += ", COALESCE(petugas_verifikasi, '-') AS verifikator ";
            if (adaTanggalVerifColumn) sql += ", COALESCE(tanggal_verifikasi, NULL) AS tgl_verif ";
            
            sql += "FROM tbl_berkas WHERE id_siswa = ? ORDER BY tanggal_upload DESC";
                         
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Object[]> rows = new ArrayList<>();
                    while (rs.next()) {
                        String vName = adaPetugasColumn ? rs.getString("verifikator") : "-";
                        String vTime = "-";
                        if (adaTanggalVerifColumn && rs.getTimestamp("tgl_verif") != null) {
                            vTime = rs.getTimestamp("tgl_verif").toString();
                        }

                        rows.add(new Object[]{
                                rs.getString("jenis_berkas"),
                                rs.getTimestamp("tanggal_upload") != null ? rs.getTimestamp("tanggal_upload").toString() : "-",
                                mapStatus(rs.getString("status")),
                                vName,
                                vTime,
                                rs.getString("catatan")
                        });
                    }
                    SwingUtilities.invokeLater(() -> {
                        riwModel.setRowCount(0);
                        if (rows.isEmpty()) {
                            riwModel.addRow(new Object[]{"Belum ada riwayat verifikasi.", "-", "-", "-", "-", "-"});
                        } else {
                            for (Object[] r : rows) riwModel.addRow(r);
                        }
                    });
                }
            }
        } catch (SQLException e) {
            // SQL Exception diredam aman
        }
    }

    // <-------------------- PROSES UPLOAD BERKAS -------------------->
    private void eksekusiUploadSpecific(String jenis, boolean isReplace) {
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Pilih Dokumen - " + jenis);
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Dokumen Valid (PDF, JPG, JPEG, PNG)", "pdf", "jpg", "jpeg", "png"));

        if (jfc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File src = jfc.getSelectedFile();
        if (src.length() > MAX_BYTES) {
            showError("Ukuran berkas melebihi batas maksimal 5 MB."); return;
        }

        String namaAsli = src.getName();
        String ext = namaAsli.substring(namaAsli.lastIndexOf(".")).toLowerCase();
        String subDir = resolveSubDir(jenis);
        String namaServer = "FILE_" + subDir.toUpperCase() + "_" + System.currentTimeMillis() + ext;

        try (Connection conn = DatabaseConfig.getKoneksi();
             java.io.FileInputStream fis = new java.io.FileInputStream(src)) {
            
            int idSiswa = getIdSiswa(conn);
            String mime = ext.equals(".pdf") ? "application/pdf" : "image/" + ext.replace(".", "");
            boolean exists = checkExists(conn, idSiswa, jenis);

            if (exists) {
                String sql = "UPDATE tbl_berkas SET nama_file_asli=?, nama_file_server=?, file_biner=?, " +
                             "ukuran_file=?, mime_type=?, status='MENUNGGU_VERIFIKASI', tanggal_upload=NOW() " +
                             "WHERE id_siswa=? AND jenis_berkas=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, namaAsli); 
                    ps.setString(2, namaServer);
                    ps.setBinaryStream(3, fis, (int) src.length()); 
                    ps.setLong(4, src.length()); 
                    ps.setString(5, mime); 
                    ps.setInt(6, idSiswa); 
                    ps.setString(7, jenis);
                    ps.executeUpdate();
                }
            } else {
                String sql = "INSERT INTO tbl_berkas (id_siswa, jenis_berkas, nama_file_asli, " +
                             "nama_file_server, file_biner, ukuran_file, mime_type, status, tanggal_upload) " +
                             "VALUES (?,?,?,?,?,?,?,'MENUNGGU_VERIFIKASI', NOW())";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idSiswa); 
                    ps.setString(2, jenis); 
                    ps.setString(3, namaAsli);
                    ps.setString(4, namaServer); 
                    ps.setBinaryStream(5, fis, (int) src.length()); 
                    ps.setLong(6, src.length()); 
                    ps.setString(7, mime);
                    ps.executeUpdate();
                }
            }
            
            String logAction = isReplace ? "GANTI BERKAS" : "UPLOAD BERKAS";
            auditCtrl.recordAuditLog(SessionManager.getCurrentUser().getIdUser(), logAction, "Aksi cloud pada berkas: " + jenis + " (" + namaAsli + ")");

            JOptionPane.showMessageDialog(this, "Berkas '" + jenis + "' berhasil disimpan ke cloud database!\nStatus: Menunggu Verifikasi",
                    "Upload Berhasil", JOptionPane.INFORMATION_MESSAGE);
            
            loadAllData();

        } catch (Exception ex) {
            showError("Gagal memproses simpan biner file ke database: " + ex.getMessage());
        }
    }

    private void eksekusiLihatSpecific(String jenis, String namaFileServer) {
        byte[] fileBytes = null;
        String mime = "";

        try (Connection conn = DatabaseConfig.getKoneksi()) {
            int idSiswa = getIdSiswa(conn);
            String sql = "SELECT file_biner, mime_type FROM tbl_berkas WHERE id_siswa=? AND jenis_berkas=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idSiswa); ps.setString(2, jenis);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        fileBytes = rs.getBytes("file_biner");
                        mime = rs.getString("mime_type");
                    }
                }
            }
        } catch (Exception ex) { 
            showError("Gagal mengambil data berkas dari server: " + ex.getMessage()); 
            return;
        }

        if (fileBytes == null || fileBytes.length == 0) {
            showWarn("Dokumen fisik kosong atau tidak ditemukan di database cloud.");
            return;
        }

        if (mime != null && mime.contains("pdf")) {
            try {
                File tempFile = File.createTempFile("PREVIEW_" + jenis.replace(" ", "_") + "_", ".pdf");
                tempFile.deleteOnExit();
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(fileBytes);
                }
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(tempFile);
                } else {
                    showWarn("Sistem operasi tidak mendukung pembukaan file PDF otomatis.");
                }
            } catch (Exception e) {
                showError("Gagal merender dokumen PDF: " + e.getMessage());
            }
        } else {
            previewImageFromBytes(fileBytes, jenis);
        }
    }
    
    private void previewImageFromBytes(byte[] bytes, String jenis) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pratinjau Cloud - " + jenis, true);
        dlg.setLayout(new BorderLayout()); dlg.setSize(600, 700); dlg.setLocationRelativeTo(this);

        try {
            ImageIcon iconAsli = new ImageIcon(bytes);
            Image scaled = iconAsli.getImage().getScaledInstance(550, 630, Image.SCALE_SMOOTH);
            JLabel imgLbl = new JLabel(new ImageIcon(scaled));
            imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
            dlg.add(new JScrollPane(imgLbl), BorderLayout.CENTER);
        } catch (Exception ex) { 
            showError("Gagal memuat struktur biner gambar: " + ex.getMessage()); 
            return; 
        }

        JButton close = new JButton("Tutup Pratinjau"); 
        close.setBackground(PRIMARY); close.setForeground(Color.BLACK); close.setFocusPainted(false);
        close.addActionListener(e -> dlg.dispose());
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bar.add(close);
        dlg.add(bar, BorderLayout.SOUTH); 
        dlg.setVisible(true);
    }

    private void previewImage(File file, String jenis) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pratinjau - " + jenis, true);
        dlg.setLayout(new BorderLayout()); dlg.setSize(600, 700); dlg.setLocationRelativeTo(this);

        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) { showError("Format gambar tidak didukung."); return; }
            Image scaled = img.getScaledInstance(550, 650, Image.SCALE_SMOOTH);
            JLabel imgLbl = new JLabel(new ImageIcon(scaled));
            imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
            dlg.add(new JScrollPane(imgLbl), BorderLayout.CENTER);
        } catch (IOException ex) { showError("Gagal memuat gambar: " + ex.getMessage()); return; }

        JButton close = new JButton("Tutup"); close.setBackground(PRIMARY); close.setForeground(Color.BLACK); close.setFocusPainted(false);
        close.addActionListener(e -> dlg.dispose());
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bar.add(close);
        dlg.add(bar, BorderLayout.SOUTH); dlg.setVisible(true);
    }

    private void eksekusiHapusSpecific(String jenis, String namaFileServer) {
        int opt = JOptionPane.showConfirmDialog(this,
                "Hapus berkas '" + jenis + "' secara permanen?\nAnda harus mengupload ulang untuk melengkapi pendaftaran.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConfig.getKoneksi()) {
            int idSiswa = getIdSiswa(conn);
            String subDir = resolveSubDir(jenis);
            File f = new File(DIR_ROOT + subDir + "/" + namaFileServer);
            if (f.exists()) f.delete();

            String upd = "UPDATE tbl_berkas SET nama_file=NULL, nama_file_asli=NULL, nama_file_server=NULL, " +
                         "ukuran_file=0, mime_type=NULL, status='BELUM_UPLOAD', tanggal_upload=NULL WHERE id_siswa=? AND jenis_berkas=?";
            try (PreparedStatement ps = conn.prepareStatement(upd)) {
                ps.setInt(1, idSiswa); ps.setString(2, jenis); ps.executeUpdate();
            }
            auditCtrl.recordAuditLog(SessionManager.getCurrentUser().getIdUser(), "HAPUS BERKAS", "Menghapus dokumen persyaratan: " + jenis);
        } catch (SQLException ex) { showError("Gagal melakukan operasi hapus: " + ex.getMessage()); return; }

        loadBerkasData(); loadRiwayat();
    }

    // <-------------------- HELPER METHODS -------------------->
    private int getIdSiswa(Connection conn) throws SQLException {
        if (!SessionManager.isLoggedIn()) return -1;
        String sql = "SELECT id_siswa FROM tbl_siswa WHERE nomor_pendaftaran=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, SessionManager.getCurrentUser().getUsername());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id_siswa") : -1;
            }
        }
    }

    private String getJalurSiswa(Connection conn, int idSiswa) throws SQLException {
        String sql = "SELECT j.nama_jalur FROM tbl_siswa s LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur WHERE s.id_siswa=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getString("nama_jalur") : null; }
        }
    }

    private boolean checkTahfidzSiswa(Connection conn, int idSiswa) throws SQLException {
        boolean adaHafalanColumn = false;
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet columns = meta.getColumns(null, null, "tbl_biodata_siswa", "hafalan_juz")) {
                if (columns.next()) adaHafalanColumn = true;
            }
        } catch (Exception ignored) {}

        if (!adaHafalanColumn) return false;

        String sql = "SELECT hafalan_juz FROM tbl_biodata_siswa WHERE id_siswa=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) { 
                return rs.next() && rs.getInt("hafalan_juz") > 0; 
            }
        }
    }

    private boolean checkExists(Connection conn, int idSiswa, String jenis) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tbl_berkas WHERE id_siswa=? AND jenis_berkas=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSiswa); ps.setString(2, jenis);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        }
    }

    private String resolveSubDir(String jenis) {
        String j = jenis.toLowerCase();
        if (j.contains("keluarga")) return "kk";
        if (j.contains("akta")) return "akta";
        if (j.contains("foto")) return "foto";
        if (j.contains("ijazah") || j.contains("skl")) return "ijazah";
        if (j.contains("rapor")) return "rapor";
        if (j.contains("pembayaran")) return "pembayaran";
        if (j.contains("prestasi") || j.contains("tahfidz") || j.contains("kip") || j.contains("pkh")) return "sertifikat";
        return "stpjm";
    }

    private String mapStatus(String raw) {
        if (raw == null) return "Belum Upload";
        switch (raw.toUpperCase()) {
            case "MENUNGGU_VERIFIKASI": return "Menunggu Verifikasi";
            case "PROSES":              return "Menunggu Verifikasi";
            case "DIVERIFIKASI":        return "Diverifikasi";
            case "DITOLAK":             return "Ditolak";
            case "PERLU_REVISI":        return "Ditolak";
            case "BELUM_UPLOAD":        return "Belum Upload";
            default:                    return raw;
        }
    }

    private void copyFile(File src, File dst) throws IOException {
        try (FileChannel s = new FileInputStream(src).getChannel();
             FileChannel d = new FileOutputStream(dst).getChannel()) { d.transferFrom(s, 0, s.size()); }
    }

    public void muatStatusBerkasSiswa() {
        loadAllData();
    }

    private void loadAllData() {
        loadHeaderInfo();
        loadBerkasData();
        loadRiwayat();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Kesalahan Sistem", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarn(String message) {
        JOptionPane.showMessageDialog(this, message, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}