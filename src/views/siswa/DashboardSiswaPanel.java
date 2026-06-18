package views.siswa;

import config.SessionManager;
import controllers.DashboardSiswaController;
import views.components.RoundedPanel;
import views.components.CustomButton;
import config.DatabaseConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.LineBorder;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Portal PPDB Mandiri Calon Siswa SMPIT AL FADL.
 * Kepatuhan Kompilasi: NetBeans 8.2 / JDK 8 / Standard JDBC Realtime.
 * * @author Rivaldi
 */
public class DashboardSiswaPanel extends JPanel {

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

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_BADGE  = new Font("Segoe UI", Font.BOLD, 11);

    // <-------------------- CONTROLLER -------------------->
    private final DashboardSiswaController ctrl = new DashboardSiswaController();

    // <-------------------- KOMPONEN FORM -------------------->
    private JLabel lblFoto;
    private JLabel lblNama;
    private JLabel lblNoReg;
    private JLabel lblStatusAkun;
    private JLabel lblTahun;
    private JLabel lblJalur;
    private JLabel lblProgressPersen;
    private JPanel pipelinePanel;
    private JPanel statusCardsPanel;
    private JPanel panelNotifikasi;
    private JPanel panelTodoList;
    private DefaultTableModel tabelPengumumanModel;
    private JTextArea txtDetailPengumuman;
    private List<String[]> lastPengumuman;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari DashboardSiswaPanel dan memuat data awal.
     */
    public DashboardSiswaPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(14, 18, 14, 18));
        add(buildContent(), BorderLayout.CENTER);
        loadDashboard();
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    /**
     * Menyusun susunan kontainer layout utama halaman panel siswa.
     */
    private JPanel buildContent() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0; gbc.weighty = 0.05; gbc.insets = new Insets(0, 0, 10, 0);
        main.add(buildGreeting(), gbc);

        JPanel row1 = new JPanel(new GridBagLayout());
        row1.setBackground(BG);
        GridBagConstraints g1 = new GridBagConstraints();
        g1.fill = GridBagConstraints.BOTH; g1.weighty = 1.0; g1.gridy = 0;
        g1.weightx = 0.25; g1.gridx = 0; g1.insets = new Insets(0, 0, 0, 10);
        row1.add(buildProfileCard(), g1);
        g1.weightx = 0.75; g1.gridx = 1; g1.insets = new Insets(0, 0, 0, 0);
        row1.add(buildPipelineCard(), g1);

        gbc.gridy = 1; gbc.weighty = 0.20; gbc.insets = new Insets(0, 0, 10, 0);
        main.add(row1, gbc);

        statusCardsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        statusCardsPanel.setBackground(BG);

        gbc.gridy = 2; gbc.weighty = 0.35; gbc.insets = new Insets(0, 0, 10, 0);
        main.add(statusCardsPanel, gbc);

        JPanel bottomGrid = new JPanel(new GridLayout(1, 3, 12, 0));
        bottomGrid.setBackground(BG);
        
        bottomGrid.add(buildTodoListCard());  
        bottomGrid.add(buildNotifikasiCard());
        bottomGrid.add(buildPengumumanCard());

        gbc.gridy = 3; gbc.weighty = 0.40; gbc.insets = new Insets(0, 0, 0, 0);
        main.add(bottomGrid, gbc);

        return main;
    }

    private JPanel buildGreeting() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        String nama = SessionManager.getNamaLengkap();
        if (nama == null || nama.isEmpty()) nama = "Siswa";

        JLabel title = new JLabel("Dashboard Pendaftaran");
        title.setFont(FONT_TITLE); title.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Selamat datang, " + nama + " - pantau progress pendaftaran Anda di sini.");
        sub.setFont(FONT_BODY); sub.setForeground(TEXT_LIGHT);

        JPanel left = new JPanel(); left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(BG); left.add(title); left.add(Box.createVerticalStrut(2)); left.add(sub);

        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel buildProfileCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 14, WHITE);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        lblFoto = new JLabel(); lblFoto.setPreferredSize(new Dimension(64, 64));
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        
        int idSiswa = dapatkanIdSiswaBypass();
        byte[] fotoBytes = null;
        String namaFileServer = null;

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement("SELECT file_biner, nama_file_server FROM tbl_berkas WHERE id_siswa = ? AND jenis_berkas = 'Pas Foto' LIMIT 1")) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    fotoBytes = rs.getBytes("file_biner");
                    namaFileServer = rs.getString("nama_file_server");
                }
            }
        } catch (SQLException e) {
            // Log diredam aman
        }

        // Cek validitas rendering gambar biner atau file lokal
        if (fotoBytes != null && fotoBytes.length > 0) {
            try {
                ImageIcon iconOriginal = new ImageIcon(fotoBytes);
                Image imgResized = iconOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                lblFoto.setIcon(new ImageIcon(imgResized));
            } catch (Exception ex) {
                lblFoto.setIcon(createDefaultAvatar(64, PRIMARY));
            }
        } else if (namaFileServer != null && !namaFileServer.trim().isEmpty()) {
            // Fallback Engine: Jika BLOB null, baca file fisik dari folder uploads/
            File fileFisik = new File("uploads/" + namaFileServer);
            if (fileFisik.exists()) {
                ImageIcon iconLocal = new ImageIcon(fileFisik.getAbsolutePath());
                Image imgResized = iconLocal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                lblFoto.setIcon(new ImageIcon(imgResized));
            } else {
                lblFoto.setIcon(createDefaultAvatar(64, PRIMARY));
            }
        } else {
            lblFoto.setIcon(createDefaultAvatar(64, PRIMARY));
        }

        JPanel info = new JPanel(); info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false); info.setBorder(new EmptyBorder(0, 12, 0, 0));

        lblNama = new JLabel("Memuat..."); lblNama.setFont(new Font("Segoe UI", Font.BOLD, 14)); lblNama.setForeground(TEXT_DARK);
        lblNoReg = makeInfoLabel("No. Pendaftaran: -");
        lblStatusAkun = makeInfoLabel("Status: -");
        lblTahun = makeInfoLabel("Tahun Ajaran: -");
        lblJalur = makeInfoLabel("Jalur: -");

        info.add(lblNama); info.add(Box.createVerticalStrut(3));
        info.add(lblNoReg); info.add(lblStatusAkun); info.add(lblTahun); info.add(lblJalur);

        CustomButton btnUploadFoto = new CustomButton("Unggah Foto", PRIMARY, WHITE);
        btnUploadFoto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUploadFoto.setForeground(Color.WHITE);
        btnUploadFoto.setRadius(6);
        btnUploadFoto.setPreferredSize(new Dimension(95, 20));
        btnUploadFoto.addActionListener(e -> aksiProsesUploadPasFotoSiswa());

        JPanel pnlFotoLeftWrapper = new JPanel(new BorderLayout(0, 4));
        pnlFotoLeftWrapper.setOpaque(false);
        pnlFotoLeftWrapper.add(lblFoto, BorderLayout.CENTER);
        pnlFotoLeftWrapper.add(btnUploadFoto, BorderLayout.SOUTH);

        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false);
        top.add(pnlFotoLeftWrapper, BorderLayout.WEST); top.add(info, BorderLayout.CENTER);
        card.add(top, BorderLayout.CENTER);

        JPanel accent = new JPanel(); accent.setBackground(PRIMARY); accent.setPreferredSize(new Dimension(0, 3));
        card.add(accent, BorderLayout.SOUTH);
        return card;
    }

    private JLabel makeInfoLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(FONT_SMALL); l.setForeground(TEXT_LIGHT); return l;
    }

    private JPanel buildPipelineCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 14, WHITE);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel topPanel = new JPanel(new BorderLayout()); topPanel.setOpaque(false);
        JLabel title = new JLabel("Progress Pendaftaran"); title.setFont(FONT_HEADER); title.setForeground(TEXT_DARK);
        
        lblProgressPersen = new JLabel("0%"); 
        lblProgressPersen.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        lblProgressPersen.setForeground(PRIMARY);
        
        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(lblProgressPersen, BorderLayout.EAST);
        topPanel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        card.add(topPanel, BorderLayout.NORTH);

        pipelinePanel = new JPanel(new GridBagLayout());
        pipelinePanel.setOpaque(false);
        card.add(pipelinePanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildTodoListCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 14, WHITE);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Yang Harus Diselesaikan"); 
        title.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        title.setForeground(TEXT_DARK);
        
        title.setHorizontalAlignment(SwingConstants.CENTER); 

        title.setBorder(new EmptyBorder(0, 0, 12, 0)); 
        card.add(title, BorderLayout.NORTH);

        panelTodoList = new JPanel(); 
        panelTodoList.setLayout(new BoxLayout(panelTodoList, BoxLayout.Y_AXIS));
        panelTodoList.setBackground(WHITE);

        JScrollPane scroll = new JScrollPane(panelTodoList); 
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16); 
        
        JPanel pnlScrollWrapper = new JPanel(new BorderLayout());
        pnlScrollWrapper.setOpaque(false);
        pnlScrollWrapper.add(scroll, BorderLayout.CENTER);
        
        card.add(pnlScrollWrapper, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildNotifikasiCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 14, WHITE);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Jadwal Ujian Seleksi PPDB"); 
        title.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        title.setForeground(TEXT_DARK);
        
        title.setHorizontalAlignment(SwingConstants.CENTER); 
        
        title.setBorder(new EmptyBorder(0, 0, 12, 0)); 
        card.add(title, BorderLayout.NORTH);

        panelNotifikasi = new JPanel(); 
        panelNotifikasi.setLayout(new BoxLayout(panelNotifikasi, BoxLayout.Y_AXIS));
        panelNotifikasi.setBackground(WHITE);

        JScrollPane scroll = new JScrollPane(panelNotifikasi); 
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16); 
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildPengumumanCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 14, WHITE);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Judul Rata Tengah Estetik
        JLabel title = new JLabel("Informasi Rekening & Agenda PPDB"); 
        title.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        title.setForeground(TEXT_DARK);
        title.setHorizontalAlignment(SwingConstants.CENTER); 
        title.setBorder(new EmptyBorder(0, 0, 12, 0)); 
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"Kategori", "Detail Informasi / Agenda Penting", "Aksi"};
        tabelPengumumanModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        JTable table = new JTable(tabelPengumumanModel);
        table.setFont(FONT_SMALL); 
        table.setRowHeight(38); // Tinggi baris proporsional ala SaaS modern
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false); 
        table.setGridColor(BORDER_CLR); 
        table.setBackground(WHITE);
        table.setSelectionBackground(BIRU_BG); 
        table.setSelectionForeground(TEXT_DARK);
        
        table.getTableHeader().setFont(FONT_BADGE); 
        table.getTableHeader().setBackground(new Color(249, 250, 251));
        table.getTableHeader().setForeground(TEXT_LIGHT);
        table.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));
        table.getTableHeader().setReorderingAllowed(false);
        
        // 🎯 FIX TEKS TERPOTONG: Kolom tengah diberikan ruang yang sangat luas (400px)
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (c == 2) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    comp.setForeground(PRIMARY); // Teks aksi berwarna biru link
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    comp.setForeground(TEXT_DARK);
                }
                return comp;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0 && lastPengumuman != null && row < lastPengumuman.size()) {
                    bukaPopupDetailInformasiSiswa(lastPengumuman.get(row));
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.setBackground(WHITE);
        tableScroll.getViewport().setBackground(WHITE);
        
        card.add(tableScroll, BorderLayout.CENTER);
        return card;
    }

    private void refillStatusCards(JPanel panel, Map<String, String> status, Map<String, String> detailBayar) {
        panel.removeAll();

        String formS   = status == null ? "..." : status.getOrDefault("form", "Lengkap");
        String berkasS = status == null ? "..." : status.getOrDefault("berkas", "MENUNGGU_VERIFIKASI");
        String bayarS  = status == null ? "..." : status.getOrDefault("pembayaran", "BELUM_BAYAR");
        String seleksiS= status == null ? "..." : status.getOrDefault("seleksi", "PROSES");
        String daftarS = status == null ? "..." : status.getOrDefault("daftar_ulang", "BELUM_DAFTAR_ULANG");

        RoundedPanel cForm = createCardFormulirComponent(formS); 
        RoundedPanel cBerkas = createCardBerkasComponent(berkasS, status); 
        RoundedPanel cBayar = createCardPembayaranComponent(bayarS, detailBayar); 
        RoundedPanel cSeleksi = createCardPengumumanComponent(seleksiS); 
        
        panel.add(cForm);
        panel.add(cBerkas);
        panel.add(cBayar);
        panel.add(cSeleksi);

        if ("LULUS".equalsIgnoreCase(seleksiS) || "DITERIMA".equalsIgnoreCase(seleksiS)) {
            RoundedPanel cDaftar = createCardDaftarUlangComponent(daftarS); 
            panel.add(cDaftar);
        }

        panel.revalidate();
        panel.repaint();
    }

    private RoundedPanel createCardFormulirComponent(String status) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 12, WHITE);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        Color[] colors = resolveColor(status);

        JPanel topBar = new JPanel(); barAccent(topBar, colors[0], card, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("Formulir Biodata"); lblTitle.setFont(FONT_HEADER); lblTitle.setForeground(TEXT_DARK);
        JLabel lblStatus = makeBadge(status, colors);
        
        boolean isLocked = false;
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement("SELECT is_locked FROM tbl_siswa WHERE id_siswa = ? LIMIT 1")) {
            ps.setInt(1, dapatkanIdSiswaBypass());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("is_locked") == 1) isLocked = true;
            }
        } catch (SQLException e) {}

        JPanel center = new JPanel(new GridLayout(3, 1)); center.setOpaque(false);
        center.add(lblTitle); 
        center.add(lblStatus); 
        
        if (isLocked) {
            JLabel lblKet = new JLabel("<html><span style='color:red;'>Data dikunci panitia.</span><br>Perubahan ditutup.</html>");
            lblKet.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            center.add(lblKet);
        } else {
            JLabel lblKet = new JLabel("Terakhir diperbarui: Hari ini");
            lblKet.setFont(FONT_SMALL); lblKet.setForeground(TEXT_LIGHT);
            center.add(lblKet);
        }
        card.add(center, BorderLayout.CENTER);

        CustomButton btnLihat = new CustomButton("Lihat Formulir", PRIMARY, WHITE);
        btnLihat.setFont(FONT_SMALL); 
        btnLihat.setForeground(Color.WHITE);
        btnLihat.setRadius(8);
        btnLihat.addActionListener(e -> dialogLihatFormulirReadonly());
        
        card.add(btnLihat, BorderLayout.SOUTH); 
        return card;
    }

    private RoundedPanel createCardBerkasComponent(String status, Map<String, String> map) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 12, WHITE);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        Color[] colors = resolveColor(status);

        JPanel topBar = new JPanel(); barAccent(topBar, colors[0], card, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("Berkas Persyaratan"); lblTitle.setFont(FONT_HEADER); lblTitle.setForeground(TEXT_DARK);
        JLabel lblStatus = makeBadge(status, colors);
        
        int berkasUploaded = 0;
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM tbl_berkas WHERE id_siswa = ? AND (nama_file_server IS NOT NULL OR nama_file IS NOT NULL)")) {
            ps.setInt(1, dapatkanIdSiswaBypass());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) berkasUploaded = rs.getInt(1);
            }
        } catch (SQLException e) {}

        JLabel lblKet = new JLabel(berkasUploaded + " dari 7 Lengkap"); 
        lblKet.setFont(FONT_SMALL); lblKet.setForeground(TEXT_LIGHT);

        JPanel center = new JPanel(new GridLayout(3, 1)); center.setOpaque(false);
        center.add(lblTitle); center.add(lblStatus); center.add(lblKet);
        card.add(center, BorderLayout.CENTER);

        String txtBtn = "DITOLAK".equalsIgnoreCase(status) ? "Perbaiki Berkas" : "Kelola Berkas";
        CustomButton btnAction = new CustomButton(txtBtn, colors[0], WHITE);
        btnAction.setFont(FONT_SMALL); 
        btnAction.setForeground(Color.WHITE);
        btnAction.setRadius(8);
        btnAction.addActionListener(e -> handleQuickAction("BERKAS"));
        card.add(btnAction, BorderLayout.SOUTH);

        return card;
    }

    private RoundedPanel createCardPembayaranComponent(String status, Map<String, String> det) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 12, WHITE);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        Color[] colors = resolveColor(status);

        JPanel topBar = new JPanel(); barAccent(topBar, colors[0], card, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("Tagihan SPMB"); lblTitle.setFont(FONT_HEADER); lblTitle.setForeground(TEXT_DARK);
        JLabel lblStatus = makeBadge(status, colors);
        
        String inv = "INV-2026-PENDING";
        String buktiFileServer = null;
        
        int idSiswa = dapatkanIdSiswaBypass();

        try (Connection conn = DatabaseConfig.getKoneksi()) {
            String sqlPay = "SELECT nomor_invoice, bukti_bayar FROM tbl_pembayaran WHERE id_siswa = ? LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sqlPay)) {
                ps.setInt(1, idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getString("nomor_invoice") != null) inv = rs.getString("nomor_invoice");
                        buktiFileServer = rs.getString("bukti_bayar");
                    }
                }
            }
            
            if (buktiFileServer == null || buktiFileServer.trim().isEmpty()) {
                String sqlFallback = "SELECT nama_file_server FROM tbl_berkas WHERE id_siswa = ? AND UPPER(jenis_berkas) LIKE '%PEMBAYARAN%' LIMIT 1";
                try (PreparedStatement psF = conn.prepareStatement(sqlFallback)) {
                    psF.setInt(1, idSiswa);
                    try (ResultSet rsF = psF.executeQuery()) {
                        if (rsF.next()) {
                            buktiFileServer = rsF.getString("nama_file_server");
                        }
                    }
                }
            }
            
            if (buktiFileServer == null || buktiFileServer.trim().isEmpty()) {
                String sqlLastResort = "SELECT nama_file_server FROM tbl_berkas WHERE id_siswa = ? ORDER BY id_berkas DESC LIMIT 1";
                try (PreparedStatement psL = conn.prepareStatement(sqlLastResort)) {
                    psL.setInt(1, idSiswa);
                    try (ResultSet rsL = psL.executeQuery()) {
                        if (rsL.next()) {
                            buktiFileServer = rsL.getString("nama_file_server");
                        }
                    }
                }
            }
            
        } catch (SQLException e) {
            // Error diredam aman
        }
        
        JLabel lblKet = new JLabel("<html><font size='2'>" + inv + "</font></html>"); 
        lblKet.setFont(FONT_SMALL); lblKet.setForeground(TEXT_LIGHT);

        JPanel center = new JPanel(new GridLayout(3, 1, 0, 4)); center.setOpaque(false);
        center.add(lblTitle); center.add(lblStatus); center.add(lblKet);
        card.add(center, BorderLayout.CENTER);

        JPanel pnlTombol = new JPanel();
        pnlTombol.setOpaque(false);

        if (buktiFileServer != null && !buktiFileServer.trim().isEmpty()) {
            pnlTombol.setLayout(new GridLayout(1, 2, 6, 0));
            
            CustomButton btnLihat = new CustomButton("Lihat Bukti", BIRU, WHITE);
            btnLihat.setFont(FONT_SMALL); 
            btnLihat.setForeground(Color.WHITE);
            btnLihat.setRadius(8);
            
            final String fileTarget = buktiFileServer;
            btnLihat.addActionListener(e -> aksiBukaPopupPreviewBukti(fileTarget));
            pnlTombol.add(btnLihat);

            String textKanan = "LUNAS".equalsIgnoreCase(status) ? "Invoice" : "Ubah Bukti";
            Color warnaKanan = "LUNAS".equalsIgnoreCase(status) ? HIJAU : KUNING;
            
            CustomButton btnKanan = new CustomButton(textKanan, warnaKanan, WHITE);
            btnKanan.setFont(FONT_SMALL); 
            btnKanan.setForeground(Color.WHITE);
            btnKanan.setRadius(8);
            btnKanan.addActionListener(e -> {
                if ("LUNAS".equalsIgnoreCase(status)) {
                    JOptionPane.showMessageDialog(this, "Pembayaran pendaftaran SPMB Anda telah lunas terverifikasi resmi.", "Status Invoice", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    prosesUploadBuktiKeuangan();
                }
            });
            pnlTombol.add(btnKanan);
        } else {
            pnlTombol.setLayout(new GridLayout(1, 1));
            CustomButton btnUploadSingle = new CustomButton("Upload Bukti Bayar", colors[0], WHITE);
            btnUploadSingle.setFont(FONT_SMALL); 
            btnUploadSingle.setForeground(Color.BLACK);
            btnUploadSingle.setRadius(8);
            btnUploadSingle.addActionListener(e -> prosesUploadBuktiKeuangan());
            pnlTombol.add(btnUploadSingle);
        }

        card.add(pnlTombol, BorderLayout.SOUTH);
        return card;
    }
    
    private void tampilkanPopUpBuktiBayar(String namaFileServer) {
        if (namaFileServer == null || namaFileServer.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Berkas bukti transfer belum diunggah atau tidak ditemukan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File fileGambar = new File("uploads/pembayaran/" + namaFileServer);
        if (!fileGambar.exists()) {
            fileGambar = new File("uploads/" + namaFileServer);
        }

        if (!fileGambar.exists()) {
            JOptionPane.showMessageDialog(this, "File fisik tidak ditemukan di direktori server:\n" + fileGambar.getAbsolutePath(), "Error File", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dlgPreview = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Arsip Bukti Transfer Pembayaran Calon Siswa", true);
        dlgPreview.setSize(420, 500);
        dlgPreview.setLocationRelativeTo(this);
        dlgPreview.setResizable(false);
        dlgPreview.setLayout(new BorderLayout());

        ImageIcon iconAsli = new ImageIcon(fileGambar.getAbsolutePath());
        Image imgScale = iconAsli.getImage().getScaledInstance(380, 400, Image.SCALE_SMOOTH);
        JLabel lblGambar = new JLabel(new ImageIcon(imgScale), SwingConstants.CENTER);
        
        JPanel pnlContent = new JPanel(new BorderLayout());
        pnlContent.setBackground(WHITE);
        pnlContent.setBorder(new EmptyBorder(14, 14, 14, 14));
        pnlContent.add(new JLabel("<html><b>Nama Berkas Server:</b> " + namaFileServer + "<hr></html>", SwingConstants.CENTER), BorderLayout.NORTH);
        pnlContent.add(lblGambar, BorderLayout.CENTER);

        JButton btnTutup = new JButton("Tutup Jendela");
        btnTutup.setFont(FONT_BODY);
        btnTutup.setForeground(Color.BLACK);
        btnTutup.addActionListener(e -> dlgPreview.dispose());

        dlgPreview.add(pnlContent, BorderLayout.CENTER);
        dlgPreview.add(btnTutup, BorderLayout.SOUTH);
        dlgPreview.setVisible(true);
    }

    private RoundedPanel createCardPengumumanComponent(String status) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 12, WHITE);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        Color warnaUngu = new Color(139, 92, 246);
        Color[] colors = resolveColor(status);

        JPanel topBar = new JPanel(); barAccent(topBar, warnaUngu, card, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("Hasil Seleksi"); lblTitle.setFont(FONT_HEADER); lblTitle.setForeground(TEXT_DARK);
        JLabel lblStatus = makeBadge(status, colors);

        JLabel lblKet = new JLabel("Menunggu Pusat");
        if ("LULUS".equalsIgnoreCase(status) || "DITERIMA".equalsIgnoreCase(status)) lblKet.setText("Selamat!");
        else if ("TIDAK_DITERIMA".equalsIgnoreCase(status)) lblKet.setText("Tetap Semangat");
        lblKet.setFont(FONT_SMALL); lblKet.setForeground(TEXT_LIGHT);

        JPanel center = new JPanel(new GridLayout(3, 1)); center.setOpaque(false);
        center.add(lblTitle); center.add(lblStatus); center.add(lblKet);
        card.add(center, BorderLayout.CENTER);

        CustomButton btnAction = new CustomButton("Lihat Detail Hasil", warnaUngu, WHITE);
        btnAction.setFont(FONT_SMALL); 
        btnAction.setForeground(Color.WHITE);
        btnAction.setRadius(8);
        btnAction.addActionListener(e -> dialogBukaHalamanHasilSeleksi());
        card.add(btnAction, BorderLayout.SOUTH);

        return card;
    }

    private RoundedPanel createCardDaftarUlangComponent(String status) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 12, WHITE);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        Color[] colors = resolveColor(status);

        JPanel topBar = new JPanel(); barAccent(topBar, colors[0], card, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("Daftar Ulang"); lblTitle.setFont(FONT_HEADER); lblTitle.setForeground(TEXT_DARK);
        JLabel lblStatus = makeBadge(status, colors);

        JPanel center = new JPanel(new GridLayout(3, 1)); center.setOpaque(false);
        center.add(lblTitle); center.add(lblStatus); 
        center.add(new JLabel(" ")); 
        card.add(center, BorderLayout.CENTER);

        if ("BELUM_DAFTAR_ULANG".equalsIgnoreCase(status) || "Belum Daftar Ulang".equalsIgnoreCase(status)) {
            CustomButton btnDU = new CustomButton("Mulai Daftar Ulang", HIJAU, WHITE);
            btnDU.setFont(FONT_SMALL); 
            btnDU.setForeground(Color.WHITE);
            btnDU.setRadius(8);
            btnDU.addActionListener(e -> bukaWizardDaftarUlangSiswa());
            card.add(btnDU, BorderLayout.SOUTH);
        } else {
            JLabel lblNote = new JLabel("Proses Selesai", SwingConstants.CENTER);
            lblNote.setFont(FONT_SMALL); lblNote.setForeground(ABU);
            card.add(lblNote, BorderLayout.SOUTH);
        }

        return card;
    }

    private JLabel makeBadge(String txt, Color[] cls) {
        JLabel l = new JLabel("<html><center>" + txt.replace("_", " ") + "</center></html>");
        l.setFont(FONT_BADGE); l.setForeground(cls[0]); l.setBackground(cls[1]);
        l.setOpaque(true); l.setBorder(new EmptyBorder(3, 6, 3, 6));
        return l;
    }

    private void barAccent(JPanel p, Color c, RoundedPanel rc, String position) {
        p.setBackground(c); p.setPreferredSize(new Dimension(0, 4)); rc.add(p, position);
    }

    private Color[] resolveColor(String status) {
        if (status == null || status.isEmpty() || status.equals("...") || status.equals("-")) return new Color[]{ABU, ABU_BG};
        String s = status.toLowerCase();
        if (s.contains("lengkap") || s.contains("lulus") || s.contains("lunas") || s.contains("diterima") || s.contains("selesai") || s.contains("diverifikasi")) return new Color[]{HIJAU, HIJAU_BG};
        if (s.contains("menunggu") || s.contains("diproses") || s.contains("proses") || s.contains("cadangan") || s.contains("pending")) return new Color[]{KUNING, KUNING_BG};
        if (s.contains("ditolak") || s.contains("tidak_diterima") || s.contains("tidak lulus")) return new Color[]{MERAH, MERAH_BG};
        return new Color[]{BIRU, BIRU_BG};
    }

    // <-------------------- CORE ENGINE: UPLOAD PAS FOTO SISWA BINER -------------------->
    /**
     * Memproses konversi berkas pas foto siswa menjadi biner BLOB ke cloud database.
     */
    private void aksiProsesUploadPasFotoSiswa() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Pas Foto Resmi Pendaftaran (JPG/PNG - Maks 2MB)");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images (JPG, PNG)", "jpg", "jpeg", "png"));
        int res = chooser.showOpenDialog(this);

        if (res == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
            File fileInput = chooser.getSelectedFile();
            
            if (fileInput.length() > 2097152) {
                JOptionPane.showMessageDialog(this, "Ukuran pas foto terlalu besar! Maksimal adalah 2 MB.", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idSiswa = dapatkanIdSiswaBypass();

            try (Connection conn = DatabaseConfig.getKoneksi();
                 java.io.FileInputStream fis = new java.io.FileInputStream(fileInput)) {
                
                String sqlUpdate = "UPDATE tbl_berkas SET nama_file_asli = ?, file_biner = ?, ukuran_file = ?, tanggal_upload = NOW() "
                                 + "WHERE id_siswa = ? AND jenis_berkas = 'Pas Foto'";
                
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setString(1, fileInput.getName());
                    ps.setBinaryStream(2, fis, (int) fileInput.length());
                    ps.setLong(3, fileInput.length());
                    ps.setInt(4, idSiswa);
                    int updatedRows = ps.executeUpdate();

                    if (updatedRows == 0) {
                        String sqlInsert = "INSERT INTO tbl_berkas (id_siswa, jenis_berkas, nama_file_asli, nama_file_server, file_biner, ukuran_file, mime_type, tanggal_upload, status) "
                                         + "VALUES (?, 'Pas Foto', ?, ?, ?, ?, 'image/jpeg', NOW(), 'MENUNGGU_VERIFIKASI')";
                        try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                            psInsert.setInt(1, idSiswa);
                            psInsert.setString(2, fileInput.getName());
                            psInsert.setString(3, "FOTO_" + idSiswa + "_" + System.currentTimeMillis() + ".jpg");
                            psInsert.setBinaryStream(4, fis, (int) fileInput.length());
                            psInsert.setLong(5, fileInput.length());
                            psInsert.executeUpdate();
                        }
                    }
                }

                insertNotifikasiOtomatis("Pas foto resmi pendaftaran berhasil diunggah.", conn);
                JOptionPane.showMessageDialog(this, "Pas foto berhasil diperbarui secara online!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
                loadDashboard();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal memproses unggahan pas foto biner: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // <-------------------- HELPER ENGINE METHODS -------------------->
    private int dapatkanIdSiswaBypass() {
        int idFound = 0;
        String sql = "SELECT id_siswa FROM tbl_biodata_siswa WHERE nama_lengkap = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, SessionManager.getNamaLengkap());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) idFound = rs.getInt("id_siswa");
            }
        } catch (SQLException e) {}
        return idFound;
    }

    private int dapatkanIdUserBypass() {
        int idUserFound = 0;
        String sql = "SELECT id_user FROM tbl_users WHERE nama_lengkap = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, SessionManager.getNamaLengkap());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) idUserFound = rs.getInt("id_user");
            }
        } catch (SQLException e) {}
        return idUserFound;
    }

    private void prosesUploadBerkasFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Berkas Persyaratan (PDF/JPG/PNG - Maks 5MB)");
        int res = chooser.showOpenDialog(this);
        
        if (res == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
            File fileInput = chooser.getSelectedFile();
            String nameLower = fileInput.getName().toLowerCase();
            
            if (!nameLower.endsWith(".pdf") && !nameLower.endsWith(".jpg") && !nameLower.endsWith(".jpeg") && !nameLower.endsWith(".png")) {
                JOptionPane.showMessageDialog(this, "Format ditolak! Hanya menerima PDF, JPG, JPEG, dan PNG.", "Validasi File", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (fileInput.length() > 5242880) {
                JOptionPane.showMessageDialog(this, "Ukuran file terlalu besar! Maksimal adalah 5 MB.", "Validasi Ukuran", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                File uploadFolder = new File("uploads");
                if (!uploadFolder.exists()) uploadFolder.mkdir();

                int idSiswa = dapatkanIdSiswaBypass();
                String serverName = "BERKAS_" + idSiswa + "_" + System.currentTimeMillis() + "_" + fileInput.getName();
                Files.copy(fileInput.toPath(), new File(uploadFolder, serverName).toPath(), StandardCopyOption.REPLACE_EXISTING);

                try (Connection conn = DatabaseConfig.getKoneksi();
                     PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO tbl_berkas (id_siswa, jenis_berkas, nama_file_asli, nama_file_server, ukuran_file, mime_type, tanggal_upload, status) " +
                        "VALUES (?, 'PERSYARATAN_MANDIRI', ?, ?, ?, ?, NOW(), 'MENUNGGU_VERIFIKASI') " +
                        "ON DUPLICATE KEY UPDATE nama_file_asli=?, nama_file_server=?, ukuran_file=?, tanggal_upload=NOW(), status='MENUNGGU_VERIFIKASI'")) {
                    
                    ps.setInt(1, idSiswa);
                    ps.setString(2, fileInput.getName()); ps.setString(3, serverName);
                    ps.setLong(4, fileInput.length()); ps.setString(5, nameLower.endsWith(".pdf") ? "application/pdf" : "image/jpeg");
                    ps.setString(6, fileInput.getName()); ps.setString(7, serverName); ps.setLong(8, fileInput.length());
                    ps.executeUpdate();

                    insertNotifikasiOtomatis("Berkas persyaratan berhasil diupload. Menunggu verifikasi oleh panitia.", conn);
                }
                JOptionPane.showMessageDialog(this, "Berkas persyaratan berhasil diunggah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengunggah berkas: " + ex.getMessage());
            }
        }
    }

    private void prosesUploadBuktiKeuangan() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Bukti Transfer Pembayaran (JPG/PNG - Maks 5MB)");
        int res = chooser.showOpenDialog(this);

        if (res == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
            File fileInput = chooser.getSelectedFile();
            if (fileInput.length() > 5242880) {
                JOptionPane.showMessageDialog(this, "Ukuran file bukti transfer maksimal adalah 5 MB.", "Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int idSiswa = dapatkanIdSiswaBypass();
            
            try (Connection conn = DatabaseConfig.getKoneksi();
                 java.io.FileInputStream fis = new java.io.FileInputStream(fileInput);
                 PreparedStatement ps = conn.prepareStatement(
                    "UPDATE tbl_pembayaran SET status = 'MENUNGGU_VERIFIKASI', bukti_bayar = ? WHERE id_siswa = ?")) {
                
                ps.setBinaryStream(1, fis, (int) fileInput.length());
                ps.setInt(2, idSiswa);
                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated == 0) {
                    try (PreparedStatement psInsert = conn.prepareStatement(
                            "INSERT INTO tbl_pembayaran (id_siswa, nomor_invoice, nominal, metode, status, bukti_bayar) VALUES (?, ?, 350000, 'TRANSFER', 'MENUNGGU_VERIFIKASI', ?)")) {
                        psInsert.setInt(1, idSiswa);
                        psInsert.setString(2, "INV-" + System.currentTimeMillis());
                        psInsert.setBinaryStream(3, fis, (int) fileInput.length());
                        psInsert.executeUpdate();
                    }
                }

                insertNotifikasiOtomatis("Bukti transaksi pembayaran berhasil diunggah.", conn);
                JOptionPane.showMessageDialog(this, "Bukti pembayaran berhasil dikirim secara online!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengunggah biner gambar ke database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void aksiBukaPopupPreviewBukti(String infoTeks) {
        int idSiswa = dapatkanIdSiswaBypass();
        byte[] imageBytes = null;
        String namaFileServer = infoTeks; // Parameter bawaan yang menangkap nama file dari database

        // 1. Coba ambil data biner BLOB dari tbl_pembayaran
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement("SELECT bukti_bayar, nomor_invoice FROM tbl_pembayaran WHERE id_siswa = ? LIMIT 1")) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    imageBytes = rs.getBytes("bukti_bayar");
                }
            }
        } catch (SQLException e) {
            // Error diredam aman sesuai kriteria clean code
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Bukti Transaksi Pendaftaran", true);
        dlg.setSize(400, 470);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false); 
        dlg.setLayout(new BorderLayout());

        JPanel pnlContent = new JPanel(new BorderLayout());
        pnlContent.setBackground(WHITE);
        pnlContent.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel lblMeta = new JLabel("<html><body><b>Bukti Transfer:</b> Terverifikasi Sistem<hr></body></html>", SwingConstants.CENTER);
        lblMeta.setFont(FONT_SMALL); lblMeta.setForeground(TEXT_LIGHT);
        pnlContent.add(lblMeta, BorderLayout.NORTH);

        // 2. LOGIKA CLOUD HYBRID: Validasi render biner vs file fisik lokal folder uploads/
        boolean gambarBerhasilDimuat = false;
        
        if (imageBytes != null && imageBytes.length > 0) {
            try {
                ImageIcon iconAsli = new ImageIcon(imageBytes);
                Image imageResized = iconAsli.getImage().getScaledInstance(360, 360, Image.SCALE_SMOOTH);
                JLabel lblImgContainer = new JLabel(new ImageIcon(imageResized), SwingConstants.CENTER);
                pnlContent.add(lblImgContainer, BorderLayout.CENTER);
                gambarBerhasilDimuat = true;
            } catch (Exception ex) {
                // Gagal membaca biner, biarkan dicoba lewat file fisik
            }
        } 
        
        // Fallback: Jika database biner null, cari file fisik berdasarkan nama file server di folder uploads/
        if (!gambarBerhasilDimuat && namaFileServer != null && !namaFileServer.trim().isEmpty()) {
            File fileFisik = new File("uploads/" + namaFileServer);
            if (!fileFisik.exists()) {
                fileFisik = new File("uploads/pembayaran/" + namaFileServer);
            }

            if (fileFisik.exists()) {
                try {
                    ImageIcon iconLocal = new ImageIcon(fileFisik.getAbsolutePath());
                    Image imageResized = iconLocal.getImage().getScaledInstance(360, 360, Image.SCALE_SMOOTH);
                    JLabel lblImgContainer = new JLabel(new ImageIcon(imageResized), SwingConstants.CENTER);
                    pnlContent.add(lblImgContainer, BorderLayout.CENTER);
                    gambarBerhasilDimuat = true;
                } catch (Exception ex) {
                    // Penanganan error fallback
                }
            }
        }

        // Jika kedua jalur gagal, tampilkan pesan warning terstruktur
        if (!gambarBerhasilDimuat) {
            JOptionPane.showMessageDialog(this, 
                "<html><body><b>Berkas Fisik Bukti Transfer Tidak Ditemukan!</b><br>" +
                "Data biner kosong dan file fisik tidak ada di folder server.<br>" +
                "Silakan klik tombol <b>Ubah Bukti</b> untuk mengunggah ulang berkas.</body></html>", 
                "Berkas Kosong", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JButton btnTutup = new JButton("Tutup Pratinjau");
        btnTutup.setFont(FONT_BODY); 
        btnTutup.setForeground(Color.BLACK);
        btnTutup.addActionListener(e -> dlg.dispose());

        dlg.add(pnlContent, BorderLayout.CENTER); dlg.add(btnTutup, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // 🎯 REVISI LAYOUT FULL-VIEW: Memperluas resolusi jendela dan kolom label agar BEBAS SCROLL & ANTI-POTONG
    private void dialogLihatFormulirReadonly() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Arsip Formulir Pendaftaran Calon Siswa (Read-Only)", true);
        
        // 1. PERLEBAR RESOLUSI JENDELA: Diubah dari 620x680 menjadi 820x740 agar menampung seluruh baris formulir secara utuh
        dlg.setSize(820, 740);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.setResizable(false); 

        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setBackground(WHITE);
        pnlMain.setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel pnlCenterWrapper = new JPanel(new GridBagLayout());
        pnlCenterWrapper.setOpaque(false);
        
        GridBagConstraints gbcContainer = new GridBagConstraints();
        gbcContainer.gridx = 0; gbcContainer.gridy = 0;
        gbcContainer.weightx = 1.0; gbcContainer.fill = GridBagConstraints.HORIZONTAL;

        JPanel pnlContainer = new JPanel();
        pnlContainer.setLayout(new BoxLayout(pnlContainer, BoxLayout.Y_AXIS));
        pnlContainer.setOpaque(false);
        pnlCenterWrapper.add(pnlContainer, gbcContainer);

        int idSiswa = dapatkanIdSiswaBypass();

        String sqlKolektif = 
                "SELECT s.nomor_pendaftaran, b.*, o.*, a.*, sa.*, j.nama_jalur " +
                "FROM tbl_siswa s " +
                "JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa " +
                "LEFT JOIN tbl_orang_tua o ON s.id_siswa = o.id_siswa " +
                "LEFT JOIN tbl_alamat a ON s.id_siswa = a.id_siswa " +
                "LEFT JOIN tbl_sekolah_asal sa ON s.id_siswa = sa.id_siswa " +
                "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur " + 
                "WHERE s.id_siswa = ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sqlKolektif)) {
            
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JLabel lblHead = new JLabel("<html><center><font size='5' color='#2563EB'><b>FORMULIR PENDAFTARAN RESMI</b></font><br><font size='3' color='#6B7280'>SMPIT AL FADL</font></center><hr></html>", SwingConstants.CENTER);
                    lblHead.setAlignmentX(Component.CENTER_ALIGNMENT);
                    pnlContainer.add(lblHead); pnlContainer.add(Box.createVerticalStrut(5));

                    pnlContainer.add(createSectionHeader("I. KREDENSIAL SISTEM"));
                    pnlContainer.add(createFormRow("Nomor Registrasi (Username)", rs.getString("nomor_pendaftaran")));
                    pnlContainer.add(createFormRow("Nomor Induk NISN", rs.getString("nisn")));
                    pnlContainer.add(createFormRow("No. WhatsApp Aktif", rs.getString("nomor_hp")));

                    pnlContainer.add(createSectionHeader("II. IDENTITAS DIRI CALON SISWA"));
                    pnlContainer.add(createFormRow("Nomor Induk Kependudukan (NIK)", rs.getString("nik")));
                    pnlContainer.add(createFormRow("Nama Lengkap Siswa", rs.getString("nama_lengkap")));
                    pnlContainer.add(createFormRow("Tempat, Tanggal Lahir", rs.getString("tempat_lahir") + ", " + rs.getDate("tanggal_lahir")));
                    pnlContainer.add(createFormRow("Jenis Kelamin", rs.getString("jenis_kelamin")));
                    pnlContainer.add(createFormRow("Agama / Keyakinan", rs.getString("agama")));

                    pnlContainer.add(createSectionHeader("III. JALUR PENERIMAAN"));
                    pnlContainer.add(createFormRow("Pilihan Jalur PPDB", rs.getString("nama_jalur")));

                    pnlContainer.add(createSectionHeader("IV. DATA ORANG TUA / WALI"));
                    pnlContainer.add(createFormRow("Nama Lengkap Ayah", rs.getString("nama_ayah")));
                    pnlContainer.add(createFormRow("Pekerjaan Ayah", rs.getString("pekerjaan_ayah")));
                    pnlContainer.add(createFormRow("No. HP / Telepon Ayah", rs.getString("hp_ayah")));
                    pnlContainer.add(createFormRow("Nama Lengkap Ibu", rs.getString("nama_ibu")));
                    pnlContainer.add(createFormRow("Pekerjaan Ibu", rs.getString("pekerjaan_ibu")));
                    pnlContainer.add(createFormRow("No. HP / Telepon Ibu", rs.getString("hp_ibu")));
                    pnlContainer.add(createFormRow("Total Penghasilan Bulanan", rs.getString("penghasilan_ayah")));

                    pnlContainer.add(createSectionHeader("V. DATA ALAMAT DOMISILI"));
                    pnlContainer.add(createFormRow("Wilayah / Daerah", rs.getString("kelurahan") + ", " + rs.getString("kecamatan") + ", " + rs.getString("kabupaten") + ", " + rs.getString("provinsi")));
                    pnlContainer.add(createFormRow("Kode Pos Rumah", rs.getString("kode_pos")));
                    pnlContainer.add(createFormRow("Alamat Rumah Lengkap", rs.getString("alamat_lengkap")));

                    pnlContainer.add(createSectionHeader("VI. RIWAYAT SEKOLAH ASAL"));
                    pnlContainer.add(createFormRow("Nama Sekolah Asal", rs.getString("nama_sekolah")));
                    pnlContainer.add(createFormRow("Nomor NPSN Sekolah", rs.getString("npsn")));
                    pnlContainer.add(createFormRow("Tahun Kelulusan SD/MI", rs.getString("tahun_lulus")));
                    pnlContainer.add(createFormRow("Alamat Lengkap Sekolah", rs.getString("alamat")));
                } else {
                    pnlContainer.add(new JLabel("Arsip formulir pendaftaran fisik tidak ditemukan di database server."));
                }
            }
        } catch (SQLException ex) {
            pnlContainer.add(new JLabel("Koneksi database bermasalah: " + ex.getMessage()));
        }

        JScrollPane scroll = new JScrollPane(pnlCenterWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        scroll.getVerticalScrollBar().setUnitIncrement(18); 
        
        scroll.setBackground(WHITE);
        scroll.getViewport().setBackground(WHITE);
        pnlMain.add(scroll, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlFooter.setBackground(new Color(249, 250, 251));
        pnlFooter.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        
        JButton btnClose = new JButton("Tutup Jendela Arsip");
        btnClose.setFont(FONT_BODY);
        btnClose.setForeground(Color.BLACK);
        btnClose.addActionListener(e -> dlg.dispose());
        
        pnlFooter.add(btnClose);
        dlg.add(pnlMain, BorderLayout.CENTER);
        dlg.add(pnlFooter, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private JPanel createSectionHeader(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(243, 244, 246)); 
        panel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 4, 0, 0, PRIMARY), 
            new EmptyBorder(8, 12, 8, 12)         
        ));
        
        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(PRIMARY);
        
        panel.add(l, BorderLayout.WEST);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(16, 0, 8, 0));
        wrapper.add(panel, BorderLayout.CENTER);
        
        return wrapper;
    }

    private JPanel createFormRow(String label, String value) {
        JPanel r = new JPanel(new BorderLayout(14, 0));
        r.setBackground(WHITE);
        r.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(243, 244, 246)), 
            new EmptyBorder(6, 12, 6, 12)                                         
        ));
        
        JLabel lblK = new JLabel(label); 
        lblK.setFont(FONT_SMALL); 
        lblK.setForeground(TEXT_LIGHT);
        
        // 🎯 SINKRONISASI LEBAR: Diperlebar dari 220 menjadi 300 agar kalimat label panjang tidak terpotong tepi jendela
        lblK.setPreferredSize(new Dimension(300, 24)); 
        
        JLabel lblV = new JLabel(value != null && !value.trim().isEmpty() ? value : "-"); 
        lblV.setFont(FONT_BODY); 
        lblV.setForeground(TEXT_DARK);
        lblV.setHorizontalAlignment(SwingConstants.RIGHT); 
        
        r.add(lblK, BorderLayout.WEST); 
        r.add(lblV, BorderLayout.CENTER);
        return r;
    }

    private void renderFormulirRows(JPanel container, String[][] dataFields) {
        for (String[] field : dataFields) {
            JPanel r = new JPanel(new BorderLayout()); r.setBackground(WHITE);
            r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            r.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));
            JLabel lblK = new JLabel(field[0]); lblK.setFont(FONT_HEADER); lblK.setForeground(TEXT_LIGHT);
            JLabel lblV = new JLabel(field[1] != null ? field[1] : "-"); lblV.setFont(FONT_BODY); lblV.setForeground(TEXT_DARK);
            r.add(lblK, BorderLayout.WEST); r.add(lblV, BorderLayout.EAST);
            container.add(r); container.add(Box.createVerticalStrut(5));
        }
    }

    private void dialogBukaHalamanHasilSeleksi() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manifes Lembar Hasil Kelulusan Seleksi Resmi", true);
        dlg.setSize(540, 520); 
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false); 
        dlg.setLayout(new BorderLayout());

        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setBackground(WHITE);
        pnlMain.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel pnlData = new JPanel();
        pnlData.setLayout(new BoxLayout(pnlData, BoxLayout.Y_AXIS));
        pnlData.setOpaque(false);

        int idSiswa = dapatkanIdSiswaBypass();

        String sqlSeleksi = 
                "SELECT s.*, b.nama_lengkap, j.nama_jalur, ta.tahun_ajaran, " +
                "(SELECT COUNT(*) FROM tbl_seleksi) as total_peserta " +
                "FROM tbl_seleksi s " +
                "JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa " +
                "JOIN tbl_siswa sw ON s.id_siswa = sw.id_siswa " +
                "LEFT JOIN tbl_jalur j ON sw.id_jalur = j.id_jalur " +
                "LEFT JOIN tbl_tahun_ajaran ta ON sw.id_tahun = ta.id_tahun " +
                "WHERE s.id_siswa = ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sqlSeleksi)) {
            
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nama = rs.getString("nama_lengkap");
                    String jalur = rs.getString("nama_jalur") != null ? rs.getString("nama_jalur") : "Reguler/Yatim";
                    String ta = rs.getString("tahun_ajaran") != null ? rs.getString("tahun_ajaran") : "2026/2027";
                    String statusLulus = rs.getString("status_kelulusan");
                    int rank = rs.getInt("ranking");
                    int totalPool = rs.getInt("total_peserta");

                    JLabel lblHeader = new JLabel("<html><font size='5' color='#1E3A8A'><b>HASIL SELEKSI AKADEMIK</b></font><br><font size='3' color='#9CA3AF'>Portal Pengumuman Resmi SPMB SMPIT AL FADL</font></html>");
                    lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
                    pnlData.add(lblHeader);
                    
                    JPanel line = new JPanel(); line.setBackground(new Color(243, 244, 246));
                    line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2)); line.setAlignmentX(Component.LEFT_ALIGNMENT);
                    pnlData.add(Box.createVerticalStrut(10)); pnlData.add(line); pnlData.add(Box.createVerticalStrut(12));

                    JPanel pnlProfilCard = new JPanel(new GridLayout(3, 1, 0, 6));
                    pnlProfilCard.setOpaque(false);
                    pnlProfilCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    pnlProfilCard.add(new JLabel("<html><font color='#6B7280'>Nama Calon Siswa:</font> &nbsp;<b>" + nama + "</b></html>"));
                    pnlProfilCard.add(new JLabel("<html><font color='#6B7280'>Jalur Masuk PPDB:</font> &nbsp;<font color='#2563EB'><b>" + jalur + "</b></font></html>"));
                    pnlProfilCard.add(new JLabel("<html><font color='#6B7280'>Tahun Akademik:</font> &nbsp;<b>" + ta + "</b></html>"));
                    pnlData.add(pnlProfilCard);
                    
                    pnlData.add(Box.createVerticalStrut(14));

                    JPanel pnlGridNilai = new JPanel(new GridLayout(2, 2, 10, 10));
                    pnlGridNilai.setOpaque(false);
                    pnlGridNilai.setAlignmentX(Component.LEFT_ALIGNMENT);
                    pnlGridNilai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

                    pnlGridNilai.add(createMiniCardSkor("Tes Teori Akademik", rs.getInt("nilai_akademik")));
                    pnlGridNilai.add(createMiniCardSkor("Ujian Praktek Tahfidz", rs.getInt("nilai_tahfidz")));
                    pnlGridNilai.add(createMiniCardSkor("Wawancara Internal", rs.getInt("nilai_wawancara")));
                    pnlGridNilai.add(createMiniCardSkor("Parameter Domisili", rs.getInt("nilai_domisili")));
                    pnlData.add(pnlGridNilai);

                    pnlData.add(Box.createVerticalStrut(14));

                    JPanel pnlSummaryRow = new JPanel(new GridLayout(1, 2, 10, 0));
                    pnlSummaryRow.setOpaque(false);
                    pnlSummaryRow.setAlignmentX(Component.LEFT_ALIGNMENT);
                    pnlSummaryRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

                    RoundedPanel cardTotalBox = new RoundedPanel(new BorderLayout(), 10, new Color(239, 246, 255));
                    cardTotalBox.setBorder(new EmptyBorder(6, 12, 6, 12));
                    JLabel lblTotK = new JLabel("TOTAL SKOR", SwingConstants.LEFT); lblTotK.setFont(new Font("Segoe UI", Font.BOLD, 10)); lblTotK.setForeground(new Color(29, 78, 216));
                    JLabel lblTotV = new JLabel(String.valueOf(rs.getBigDecimal("total_nilai")), SwingConstants.RIGHT); lblTotV.setFont(new Font("Segoe UI", Font.BOLD, 18)); lblTotV.setForeground(new Color(29, 78, 216));
                    cardTotalBox.add(lblTotK, BorderLayout.WEST); cardTotalBox.add(lblTotV, BorderLayout.EAST);
                    pnlSummaryRow.add(cardTotalBox);

                    RoundedPanel cardRank = new RoundedPanel(new BorderLayout(), 10, new Color(243, 244, 246));
                    cardRank.setBorder(new EmptyBorder(6, 12, 6, 12));
                    JLabel lblRankK = new JLabel("RANK POOL", SwingConstants.LEFT); lblRankK.setFont(new Font("Segoe UI", Font.BOLD, 10)); lblRankK.setForeground(TEXT_LIGHT);
                    JLabel lblRankV = new JLabel(rank + " / " + totalPool, SwingConstants.RIGHT); lblRankV.setFont(new Font("Segoe UI", Font.BOLD, 16)); lblRankV.setForeground(TEXT_DARK);
                    cardRank.add(lblRankK, BorderLayout.WEST); cardRank.add(lblRankV, BorderLayout.EAST);
                    pnlSummaryRow.add(cardRank);
                    pnlData.add(pnlSummaryRow);

                    pnlData.add(Box.createVerticalStrut(18));

                    RoundedPanel badgeStatus = new RoundedPanel(new BorderLayout(), 12, ABU_BG);
                    badgeStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
                    badgeStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
                    
                    JLabel lblStatusBadge = new JLabel("", SwingConstants.CENTER);
                    lblStatusBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    
                    if ("DITERIMA".equalsIgnoreCase(statusLulus) || "LULUS".equalsIgnoreCase(statusLulus)) {
                        badgeStatus.setBackground(HIJAU_BG); lblStatusBadge.setForeground(HIJAU);
                        lblStatusBadge.setText("SELAMAT! ANDA DINYATAKAN: LULUS SELEKSI");
                    } else if ("CADANGAN".equalsIgnoreCase(statusLulus)) {
                        badgeStatus.setBackground(KUNING_BG); lblStatusBadge.setForeground(KUNING);
                        lblStatusBadge.setText("PERINGATAN: ANDA MASUK KATEGORI PESERTA CADANGAN");
                    } else if ("PROSES".equalsIgnoreCase(statusLulus)) {
                        badgeStatus.setBackground(BIRU_BG); lblStatusBadge.setForeground(BIRU);
                        lblStatusBadge.setText("STATUS: BERKAS SEDANG DIEVALUASI TIM VERIFIKATOR");
                    } else {
                        badgeStatus.setBackground(MERAH_BG); lblStatusBadge.setForeground(MERAH);
                        lblStatusBadge.setText("MAAF, ANDA DINYATAKAN: TIDAK LULUS SELEKSI AKADEMIK");
                    }
                    badgeStatus.add(lblStatusBadge, BorderLayout.CENTER);
                    pnlData.add(badgeStatus);
                } else {
                    pnlData.add(new JLabel("Manifes penilaian kelulusan belum diterbitkan oleh panitia pusat."));
                }
            }
        } catch (SQLException ex) {
            pnlData.add(new JLabel("Koneksi spmb_alfadl terputus: " + ex.getMessage()));
        }

        pnlMain.add(pnlData, BorderLayout.CENTER);

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlSouth.setBackground(new Color(249, 250, 251));
        pnlSouth.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        
        JButton btnClose = new JButton("Tutup");
        btnClose.setFont(FONT_BODY); 
        btnClose.setForeground(Color.BLACK);
        btnClose.addActionListener(e -> dlg.dispose());
        
        pnlSouth.add(btnClose);
        pnlMain.add(pnlSouth, BorderLayout.SOUTH);

        dlg.add(pnlMain);
        dlg.setVisible(true);
    }

    private JPanel createSampleCardSkor(String namaTes, int skor) {
        return createMiniCardSkor(namaTes, skor);
    }

    private JPanel createMiniCardSkor(String namaTes, int skor) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 10, new Color(249, 250, 251));
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        JLabel lblName = new JLabel(namaTes);
        lblName.setFont(FONT_SMALL); lblName.setForeground(TEXT_LIGHT);
        
        JLabel lblValue = new JLabel(String.valueOf(skor));
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18)); lblValue.setForeground(TEXT_DARK);
        
        card.add(lblName, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }
    
    // 🎯 REVISI SMART PHOTO HYBRID ENGINE (PART 2)
    private void inisialisasiPasFotoProfilRealtime() {
        int idSiswa = dapatkanIdSiswaBypass();
        byte[] fotoBytes = null;
        String namaFileServer = null;

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement("SELECT file_biner, nama_file_server FROM tbl_berkas WHERE id_siswa = ? AND jenis_berkas = 'Pas Foto' LIMIT 1")) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    fotoBytes = rs.getBytes("file_biner");
                    namaFileServer = rs.getString("nama_file_server");
                }
            }
        } catch (SQLException e) {
            // Error diredam aman
        }

        if (lblFoto != null) {
            if (fotoBytes != null && fotoBytes.length > 0) {
                try {
                    ImageIcon iconOriginal = new ImageIcon(fotoBytes);
                    Image imgResized = iconOriginal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                    lblFoto.setIcon(new ImageIcon(imgResized));
                } catch (Exception ex) {
                    lblFoto.setIcon(createDefaultAvatar(64, PRIMARY));
                }
            } else if (namaFileServer != null && !namaFileServer.trim().isEmpty()) {
                // Fallback Engine: Render dari direktori folder lokal
                File fileFisik = new File("uploads/" + namaFileServer);
                if (fileFisik.exists()) {
                    ImageIcon iconLocal = new ImageIcon(fileFisik.getAbsolutePath());
                    Image imgResized = iconLocal.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                    lblFoto.setIcon(new ImageIcon(imgResized));
                } else {
                    lblFoto.setIcon(createDefaultAvatar(64, PRIMARY));
                }
            } else {
                lblFoto.setIcon(createDefaultAvatar(64, PRIMARY));
            }
            lblFoto.revalidate();
            lblFoto.repaint();
        }
    }

    private void lblBadge(JLabel lblStatusBadge, String statusLulus) {
        if ("PROSES".equalsIgnoreCase(statusLulus)) {
            lblStatusBadge.setBackground(BIRU_BG); lblStatusBadge.setForeground(BIRU);
            lblStatusBadge.setText("STATUS: SEDANG DIPROSES PANITIA");
        } else {
            lblStatusBadge.setBackground(MERAH_BG); lblStatusBadge.setForeground(MERAH);
            lblStatusBadge.setText("DINYATAKAN: TIDAK LULUS SELEKSI");
        }
    }

    private void bukaWizardDaftarUlangSiswa() {
        final JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Form Daftar Ulang", true);
        dlg.setSize(460, 380); dlg.setLocationRelativeTo(this);
        
        final CardLayout wizardLayout = new CardLayout();
        final JPanel pnlMainWizard = new JPanel(wizardLayout);

        JPanel step1 = new JPanel(new BorderLayout(0, 15)); step1.setBorder(new EmptyBorder(20, 20, 20, 20));
        step1.add(new JLabel("<html><h2>Selamat Anda Diterima!</h2><br>Silakan ikuti instruksi daftar ulang.</html>"), BorderLayout.CENTER);
        JButton btnNext1 = new JButton("Lanjut"); 
        btnNext1.setForeground(Color.BLACK);
        step1.add(btnNext1, BorderLayout.SOUTH);
        pnlMainWizard.add(step1, "STEP1");

        JPanel step2 = new JPanel(new BorderLayout(0, 15)); step2.setBorder(new EmptyBorder(20, 20, 20, 20));
        step2.add(new JLabel("Tekan Selesai untuk merekam Daftar Ulang ke Database."), BorderLayout.CENTER);
        JButton btnFinish = new JButton("Selesaikan Daftar Ulang"); 
        btnFinish.setForeground(Color.BLACK);
        step2.add(btnFinish, BorderLayout.SOUTH);
        pnlMainWizard.add(step2, "STEP2");

        btnNext1.addActionListener(e -> wizardLayout.show(pnlMainWizard, "STEP2"));
        btnFinish.addActionListener(e -> {
            try (Connection conn = DatabaseConfig.getKoneksi();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO tbl_daftar_ulang (id_siswa, status) VALUES (?, 'SELESAI') ON DUPLICATE KEY UPDATE status='SELESAI', tanggal=NOW()")) {
                ps.setInt(1, dapatkanIdSiswaBypass()); ps.executeUpdate();
                insertNotifikasiOtomatis("Selamat! Registrasi daftar ulang selesai.", conn);
                JOptionPane.showMessageDialog(dlg, "Berhasil Daftar Ulang!");
                dlg.dispose(); loadDashboard();
            } catch (Exception ex) {}
        });

        dlg.add(pnlMainWizard); wizardLayout.show(pnlMainWizard, "STEP1"); dlg.setVisible(true);
    }

    private void insertNotifikasiOtomatis(String pesan, Connection conn) throws SQLException {
        String sql = "INSERT INTO tbl_notifikasi (id_user, judul, pesan, dibaca, created_at) VALUES (?, 'Sistem', ?, 0, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dapatkanIdUserBypass()); ps.setString(2, pesan); ps.executeUpdate();
        }
    }

    private void handleQuickAction(String target) {
        Container c = getParent();
        while (c != null) {
            if (c instanceof SiswaMainFrame) { 
                ((SiswaMainFrame) c).showPage(target); 
                return; 
            }
            c = c.getParent();
        }
    }

    public void loadDashboard() {
        int idSiswa = dapatkanIdSiswaBypass();
        inisialisasiPasFotoProfilRealtime();
        
        try (Connection conn = DatabaseConfig.getKoneksi()) {
            
            String sqlInfo = "SELECT s.nomor_pendaftaran, b.nama_lengkap, j.nama_jalur, t.tahun_ajaran " +
                             "FROM tbl_siswa s JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa " +
                             "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur " +
                             "LEFT JOIN tbl_tahun_ajaran t ON s.id_tahun = t.id_tahun WHERE b.id_siswa = ? LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sqlInfo)) {
                ps.setInt(1, idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        lblNama.setText(rs.getString("nama_lengkap"));
                        lblNoReg.setText("No. Pendaftaran: " + rs.getString("nomor_pendaftaran"));
                        String jalurStr = rs.getString("nama_jalur") != null ? rs.getString("nama_jalur") : "Reguler/Yatim";
                        
                        if ("Yatim".equalsIgnoreCase(jalurStr)) {
                            lblJalur.setText("<html>Jalur: Yatim (Prioritas)</html>");
                        } else {
                            lblJalur.setText("Jalur: " + jalurStr);
                        }
                        lblTahun.setText("Tahun Ajaran: " + rs.getString("tahun_ajaran"));
                    }
                }
            }

            Map<String, Object> calc = ctrl.hitungProgressPipelineSiswa(idSiswa);
            int persen = (int) calc.get("persentase");
            lblProgressPersen.setText(persen + "%");

            Map<String, String> statusMap = ctrl.getDao().getStatusCards(idSiswa, "");
            Map<String, String> detBayar = ctrl.getDao().getDetailPembayaran(idSiswa);
            lblStatusAkun.setText("Status Berkas: " + statusMap.get("berkas"));
            
            refillStatusCards(statusCardsPanel, statusMap, detBayar);
            buildPipelineVisual(pipelinePanel, statusMap);

            panelTodoList.removeAll();
            List<String> todos = ctrl.getDao().getTodoListSiswa(idSiswa);
            for (String t : todos) {
                JPanel rowPanel = new JPanel(new BorderLayout(12, 0));
                rowPanel.setBackground(WHITE);
                rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
                rowPanel.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 1, 0, new Color(243, 244, 246)), 
                    new EmptyBorder(6, 8, 6, 8)                                                                           
                ));

                String cleanText = t.replace("☑", "").replace("☐", "").trim();
                boolean isDone = t.contains("☑");

                JLabel lblT = new JLabel(cleanText); 
                lblT.setFont(FONT_BODY);
                lblT.setForeground(isDone ? TEXT_LIGHT : TEXT_DARK);

                JLabel lblBadgeStatus = new JLabel(isDone ? "SELESAI" : "BELUM", SwingConstants.CENTER);
                lblBadgeStatus.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lblBadgeStatus.setOpaque(true);
                lblBadgeStatus.setBorder(new EmptyBorder(3, 8, 3, 8)); 
                
                if (isDone) {
                    lblBadgeStatus.setBackground(HIJAU_BG);
                    lblBadgeStatus.setForeground(HIJAU);
                } else {
                    lblBadgeStatus.setBackground(KUNING_BG);
                    lblBadgeStatus.setForeground(KUNING);
                }

                rowPanel.add(lblT, BorderLayout.WEST);
                rowPanel.add(lblBadgeStatus, BorderLayout.EAST);
                
                panelTodoList.add(rowPanel); 
                panelTodoList.add(Box.createVerticalStrut(4)); 
            }
            if(todos.isEmpty()) panelTodoList.add(new JLabel("Data Todo List Sedang Kosong."));

            panelNotifikasi.removeAll();
            String sqlJadwal = "SELECT tanggal, jam_mulai, jam_selesai, nama_kegiatan, lokasi "
                             + "FROM tbl_jadwal_seleksi WHERE status = 'AKTIF' "
                             + "ORDER BY tanggal ASC, jam_mulai ASC";

            try (PreparedStatement psJadwal = conn.prepareStatement(sqlJadwal);
                 ResultSet rsJadwal = psJadwal.executeQuery()) {
                while (rsJadwal.next()) {
                    JPanel item = new JPanel(new BorderLayout(5, 0)); 
                    item.setBackground(WHITE);
                    item.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                        BorderFactory.createEmptyBorder(6, 4, 6, 4)
                    ));

                    String formatTeks = String.format(
                        "<html><body style='width: 280px; font-family: Segoe UI;'> "
                        + "<div style='margin-bottom: 6px;'>"
                        + "<span style='background-color: #E0E7FF; color: #1D4ED8; font-size: 9px; font-weight: bold; padding: 2px 6px; border-radius: 4px; letter-spacing: 0.5px;'> TES </span>"
                        + "&nbsp;&nbsp;<b style='color: #111827; font-size: 13px;'>%s</b>"
                        + "</div>"
                        + "<div style='color: #4B5563; font-size: 11px; margin-left: 2px; margin-top: 2px;'>"
                        + "Waktu : <span style='color: #2563EB; font-weight: bold;'>%s</span> (%s - %s WIB)"
                        + "</div>"
                        + "<div style='color: #6B7280; font-size: 11px; margin-left: 2px; margin-top: 2px;'>"
                        + "Tempat: <span style='color: #111827; font-weight: 500;'>%s</span>"
                        + "</div>"
                        + "</body></html>",
                        rsJadwal.getString("nama_kegiatan"),
                        rsJadwal.getString("tanggal"),
                        rsJadwal.getString("jam_mulai"),
                        rsJadwal.getString("jam_selesai"),
                        rsJadwal.getString("lokasi")
                    );

                    JLabel msg = new JLabel(formatTeks); 
                    msg.setFont(FONT_SMALL);
                    item.add(msg, BorderLayout.CENTER);
                    panelNotifikasi.add(item); 
                    panelNotifikasi.add(Box.createVerticalStrut(5));
                }
            }
            
            int jumlahJadwalAktif = panelNotifikasi.getComponentCount();
            if (jumlahJadwalAktif == 0) {
                JLabel lblKosong = new JLabel("Belum ada jadwal ujian seleksi aktif saat ini.");
                lblKosong.setFont(FONT_SMALL);
                lblKosong.setForeground(TEXT_LIGHT);
                panelNotifikasi.add(lblKosong);
            }

            tabelPengumumanModel.setRowCount(0);
            lastPengumuman = new ArrayList<>();

            String sqlSetting = "SELECT kunci_parameter, nilai_parameter, deskripsi_panjang "
                              + "FROM tbl_pengaturan WHERE status_tampil = 'TAMPIL' "
                              + "ORDER BY id_pengaturan ASC";
                              
            try (PreparedStatement psSet = conn.prepareStatement(sqlSetting);
                 ResultSet rsSet = psSet.executeQuery()) {
                while (rsSet.next()) {
                    String[] rowData = {
                        rsSet.getString("kunci_parameter"), 
                        rsSet.getString("nilai_parameter"), 
                        rsSet.getString("deskripsi_panjang") 
                    };
                    tabelPengumumanModel.addRow(new Object[]{rowData[0], rowData[1], "Lihat Detail ↗"});
                    lastPengumuman.add(rowData);
                }
            } catch (SQLException e) {
            }

            String sqlGelombang = "SELECT nama_gelombang, tanggal_mulai, tanggal_selesai, biaya_pendaftaran "
                                + "FROM tbl_gelombang WHERE status_aktif = 'AKTIF' "
                                + "ORDER BY tanggal_mulai ASC";
                                
            try (PreparedStatement psGel = conn.prepareStatement(sqlGelombang);
                 ResultSet rsGel = psGel.executeQuery()) {
                while (rsGel.next()) {
                    String namaGel = rsGel.getString("nama_gelombang");
                    String tglMulai = rsGel.getString("tanggal_mulai");
                    String tglSelesai = rsGel.getString("tanggal_selesai");
                    double biaya = rsGel.getDouble("biaya_pendaftaran");

                    String ringkasanTeks = namaGel + " (" + tglMulai + " s/d " + tglSelesai + ")";
                    String detailDeskripsi = "Agenda Gelombang Aktif:\n"
                                           + "Registrasi untuk " + namaGel + " dibuka resmi dari tanggal " 
                                           + tglMulai + " sampai dengan penutupan pada " + tglSelesai + ".\n"
                                           + "Infaq / Biaya Investasi Pendaftaran: Rp " + String.format("%,.0f", biaya);

                    String[] rowData = {
                        "Agenda", 
                        ringkasanTeks, 
                        detailDeskripsi
                    };
                    tabelPengumumanModel.addRow(new Object[]{rowData[0], rowData[1], "Lihat Detail ↗"});
                    lastPengumuman.add(rowData);
                }
            } catch (SQLException e) {
            }

            sqlGelombang = "SELECT nama_gelombang, tanggal_mulai, tanggal_selesai, biaya_pendaftaran "
                         + "FROM tbl_gelombang WHERE status_aktif = 'AKTIF' "
                         + "ORDER BY tanggal_mulai ASC";
                                
            try (PreparedStatement psGel = conn.prepareStatement(sqlGelombang);
                 ResultSet rsGel = psGel.executeQuery()) {
                while (rsGel.next()) {
                    String namaGel = rsGel.getString("nama_gelombang");
                    String tglMulai = rsGel.getString("tanggal_mulai");
                    String tglSelesai = rsGel.getString("tanggal_selesai");
                    double biaya = rsGel.getDouble("biaya_pendaftaran");

                    String ringkasanTeks = namaGel + " (" + tglMulai + " s/d " + tglSelesai + ")";
                    String detailDeskripsi = "Agenda Gelombang Aktif:\n"
                                           + "Registrasi untuk " + namaGel + " dibuka resmi dari tanggal " 
                                           + tglMulai + " sampai dengan penutupan pada " + tglSelesai + ".\n"
                                           + "Infaq / Biaya Investasi Pendaftaran: Rp " + String.format("%,.0f", biaya);

                    String[] rowData = {
                        "Agenda", 
                        ringkasanTeks, 
                        detailDeskripsi
                    };
                    tabelPengumumanModel.addRow(new Object[]{rowData[0], rowData[1], "Lihat Detail"});
                    lastPengumuman.add(rowData);
                }
            } catch (SQLException e) {
            }

        } catch (Exception ex) {
        }

        revalidate(); 
        repaint();
    }

    private void buildPipelineVisual(JPanel panel, Map<String, String> st) {
        panel.removeAll(); 
        panel.setLayout(new GridBagLayout());

        String[] steps = {"Registrasi", "Biodata", "Berkas", "Pembayaran", "Seleksi", "Pengumuman", "Daftar Ulang"};
        Color[] cols = new Color[7]; java.util.Arrays.fill(cols, ABU);
        cols[0] = HIJAU;

        if ("Lengkap".equals(st.get("form"))) cols[1] = HIJAU;
        if ("Lengkap".equalsIgnoreCase(st.get("berkas")) || "DIVERIFIKASI".equalsIgnoreCase(st.get("berkas"))) cols[2] = HIJAU;
        if ("LUNAS".equalsIgnoreCase(st.get("pembayaran"))) cols[3] = HIJAU;
        
        String sel = st.getOrDefault("seleksi", "PROSES");
        if (!"PROSES".equalsIgnoreCase(sel) && !"Belum Diproses".equalsIgnoreCase(sel)) {
            cols[4] = HIJAU;
            if ("DITERIMA".equalsIgnoreCase(sel) || "Lulus".equalsIgnoreCase(sel)) cols[5] = HIJAU;
            else if ("CADANGAN".equalsIgnoreCase(sel)) cols[5] = KUNING;
            else cols[5] = MERAH;
        }

        if ("SELESAI".equalsIgnoreCase(st.get("daftar_ulang"))) cols[6] = HIJAU;

        GridBagConstraints gc = new GridBagConstraints(); 
        gc.gridy = 0; gc.fill = GridBagConstraints.NONE; gc.anchor = GridBagConstraints.CENTER;

        for (int i = 0; i < steps.length; i++) {
            gc.gridx = i * 2; gc.weightx = 0.0; 
            panel.add(makePipelineStep(steps[i], cols[i]), gc);
            if (i < steps.length - 1) {
                gc.gridx = i * 2 + 1; gc.weightx = 1.0; gc.fill = GridBagConstraints.HORIZONTAL;
                panel.add(makeConnector(cols[i]), gc); 
                gc.fill = GridBagConstraints.NONE;
            }
        }
        panel.revalidate(); panel.repaint();
    }

    private JPanel makePipelineStep(String label, final Color color) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setOpaque(false);
        JPanel circle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color); g2.fillOval(0, 0, 26, 26);
                if (color.equals(HIJAU)) {
                    g2.setColor(WHITE); g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(6, 13, 11, 19); g2.drawLine(11, 19, 20, 7);
                }
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(26, 26); }
        };
        circle.setOpaque(false);
        JPanel cw = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); cw.setOpaque(false); cw.add(circle);
        JLabel lbl = new JLabel(label); lbl.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lbl.setForeground(color.equals(ABU) ? TEXT_LIGHT : TEXT_DARK); lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(cw); p.add(Box.createVerticalStrut(3)); p.add(lbl);
        return p;
    }

    private JPanel makeConnector(final Color color) {
        JPanel line = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color.equals(ABU) ? BORDER_CLR : color); g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, 13, getWidth(), 13); g2.dispose();
            }
        };
        line.setOpaque(false); line.setPreferredSize(new Dimension(20, 26));
        return line;
    }

    private ImageIcon createDefaultAvatar(int size, Color bg) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg); g2.fillOval(0, 0, size, size);
        g2.setColor(WHITE); g2.fillOval(size/3, size/8, size/3, size/3); g2.fillOval(size/8, size/2, (int)(size*0.75), size/2);
        g2.dispose(); return new ImageIcon(img);
    }

    public void loadDashboardDataRealtime() {
        this.loadDashboard();
    }
    
    private void bukaPopupDetailInformasiSiswa(String[] data) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "INFORMASI REKENING & AGENDA PPDB", true);
        dlg.setSize(460, 280);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);
        dlg.setLayout(new BorderLayout());

        JPanel pnlContent = new JPanel(new BorderLayout(12, 12));
        pnlContent.setBackground(WHITE);
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Format HTML Header Popup
        String headerHtml = String.format(
            "<html><body style='font-family: Segoe UI;'>"
            + "<span style='background-color: #E0E7FF; color: #1D4ED8; font-size: 9px; font-weight: bold; padding: 2px 6px; border-radius: 4px;'> %s </span>"
            + "<h3 style='margin-top: 6px; margin-bottom: 2px; color: #111827;'>%s</h3>"
            + "<hr style='border: 0; border-top: 1px solid #E2E8F0;'>"
            + "</body></html>",
            data[0].toUpperCase(), data[1]
        );
        JLabel lblHeader = new JLabel(headerHtml);
        pnlContent.add(lblHeader, BorderLayout.NORTH);

        // Deskripsi Isi Instruksi Panjang
        JTextArea txtDesc = new JTextArea(data[2]);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDesc.setForeground(new Color(51, 65, 85));
        txtDesc.setEditable(false);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setBorder(null);
        pnlContent.add(scrollDesc, BorderLayout.CENTER);

        // Tombol Tutup Flat
        JButton btnClose = new JButton("Tutup Panduan");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setBackground(new Color(241, 245, 249));
        btnClose.setForeground(TEXT_DARK);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(0, 36));
        btnClose.addActionListener(e -> dlg.dispose());
        pnlContent.add(btnClose, BorderLayout.SOUTH);

        dlg.add(pnlContent, BorderLayout.CENTER);
        dlg.setVisible(true);
    }
}