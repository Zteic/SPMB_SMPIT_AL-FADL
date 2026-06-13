package views.admin;

import config.AppTheme;
import config.SessionManager;
import controllers.DashboardAnalyticsController;
import views.components.MetricCard;
import config.DatabaseConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * DashboardAnalyticsPanel - Admin Overview Dashboard Enterprise
 * Redesign: 6 stat cards, donut chart, line chart, dan modernisasi row middle 2026 UI/UX
 * Kepatuhan Kompilasi: NetBeans 8.2 / JDK 8 Linier Engine Realtime DB Sync.
 * @author Rivaldi
 */
public class DashboardAnalyticsPanel extends JPanel {

    // <-------------------- CONTROLLER -------------------->
    private final DashboardAnalyticsController controller;

    // <-------------------- KOMPONEN UI HEADER -------------------->
    private JLabel lblGreeting;
    private JButton btnRefresh;

    // <-------------------- KOMPONEN UI STAT CARDS -------------------->
    private MetricCard cardTotal;
    private MetricCard cardHariIni;
    private MetricCard cardPending;
    private MetricCard cardDitolak;
    private MetricCard cardVerified;
    private MetricCard cardKuota;

    // <-------------------- KOMPONEN UI CHARTS -------------------->
    private DonutChartPanel donutChart;
    private LineChartPanel lineChart;

    // <-------------------- KOMPONEN UI MODEL TABEL -------------------->
    private DefaultTableModel modelPendaftar;
    private DefaultTableModel modelTugas;

    // <-------------------- KOMPONEN UI MONITORING -------------------->
    private JLabel lblBerkasPending, lblBerkasVerified, lblBerkasDitolak;
    private JLabel lblSeleksiBelum, lblSeleksiLulus, lblSeleksiTidak, lblSeleksiCadangan;

    // <-------------------- TIMER ENGINE -------------------->
    private java.util.Timer clockTimer;

    // <-------------------- CONSTRUCTOR -------------------->
    public DashboardAnalyticsPanel() {
        controller = new DashboardAnalyticsController();
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND);

        // Alokasi awal model tabel agar aman dari NullPointerException
        modelPendaftar = new DefaultTableModel(
            new String[]{"No Daftar", "Nama", "Jalur", "Status Pendaftaran"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        modelTugas = new DefaultTableModel(
            new String[]{"Deskripsi Operasional / Tugas Harian"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        add(buildHeader(), BorderLayout.NORTH);
        add(buildScrollableContent(), BorderLayout.CENTER);

        startClock();
        refreshAllData();
    }

    // <-------------------- INISIALISASI KOMPONEN HEADER -------------------->
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(AppTheme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
            new EmptyBorder(16, 24, 16, 24)
        ));

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        String adminName = SessionManager.getNamaLengkap();
        if (adminName == null || adminName.isEmpty()) adminName = "Administrator";

        lblGreeting = new JLabel("Selamat Datang, " + adminName);
        lblGreeting.setFont(AppTheme.TITLE);
        lblGreeting.setForeground(AppTheme.TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Ringkasan aktivitas dan monitoring realtime SPMB SMPIT AL FADL");
        lblSubtitle.setFont(AppTheme.BODY);
        lblSubtitle.setForeground(AppTheme.TEXT_SECONDARY);

        leftPanel.add(lblGreeting);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        leftPanel.add(lblSubtitle);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(AppTheme.BODY_BOLD);
        btnRefresh.setBackground(AppTheme.ACCENT);
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refreshAllData());

        rightPanel.add(createSeparator());
        rightPanel.add(createSeparator());
        rightPanel.add(btnRefresh);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JLabel createSeparator() {
        JLabel sep = new JLabel("|");
        sep.setForeground(AppTheme.BORDER);
        return sep;
    }

    // <-------------------- LAYOUT CONTENT BUILDER -------------------->
    private JScrollPane buildScrollableContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 24, 24, 24));
        content.setBackground(AppTheme.BACKGROUND);

        content.add(buildStatCards());
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        content.add(buildMonitoringRow());
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        content.add(buildChartsRow());
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        
        content.add(buildMiddleRow());
        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(AppTheme.BACKGROUND);
        scroll.getViewport().setBackground(AppTheme.BACKGROUND);
        return scroll;
    }

    private JPanel buildStatCards() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        cardTotal = new MetricCard("TOTAL CALON SISWA TERDAFTAR", "0", new Color(59, 130, 246));
        cardHariIni = new MetricCard("TOTAL JALUR PRESTASI TERPAKAI", "0", new Color(139, 92, 246));
        cardPending = new MetricCard("TOTAL PEMBAYARAN PENDING", "0", new Color(245, 158, 11));
        cardDitolak = new MetricCard("TOTAL BERKAS DITOLAK", "0", new Color(239, 68, 68));
        cardVerified = new MetricCard("DAFTAR ULANG BELUM SELESAI", "0", new Color(16, 185, 129));
        cardKuota = new MetricCard("TOTAL PENGUMUMAN AKTIF", "0", new Color(44, 62, 80));

        grid.add(cardTotal);
        grid.add(cardHariIni);
        grid.add(cardPending);
        grid.add(cardDitolak);
        grid.add(cardVerified);
        grid.add(cardKuota);
        return grid;
    }

    private JPanel buildChartsRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        donutChart = new DonutChartPanel();
        lineChart = new LineChartPanel();

        row.add(wrapCard("Grafik Sebaran Jalur (Inc. Yatim)", donutChart));
        row.add(wrapCard("Statistik Registrasi, Verifikasi & Pembayaran", lineChart));
        return row;
    }

    private JPanel buildMiddleRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        // 🎯 KELOLA TABEL KIRI: Pendaftar Terbaru (Modern Style 2026)
        JTable tblPendaftar = createStyledTable(modelPendaftar);
        tblPendaftar.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeCellRenderer()); // Pasang Renderer Badge
        JScrollPane spPendaftar = new JScrollPane(tblPendaftar);
        spPendaftar.setBorder(null);
        spPendaftar.getViewport().setBackground(AppTheme.SURFACE);
        
        // 🎯 KELOLA TABEL KANAN: Task List Dashboard (Modern Card Content Renderer)
        JTable tblTugas = createStyledTable(modelTugas);
        tblTugas.getColumnModel().getColumn(0).setCellRenderer(new ModernTaskCellRenderer()); // Pasang Renderer Tugas Elegan
        JScrollPane spTugas = new JScrollPane(tblTugas);
        spTugas.setBorder(null);
        spTugas.getViewport().setBackground(AppTheme.SURFACE);

        row.add(wrapCard("Pendaftar Terbaru", spPendaftar));
        row.add(wrapCard("Aktivitas & Tugas Hari Ini", spTugas)); 
        return row;
    }

    private JPanel buildMonitoringRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel berkasPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        berkasPanel.setOpaque(false);
        lblBerkasPending = createMiniMetric("Menunggu", "0", new Color(245, 158, 11));
        lblBerkasVerified = createMiniMetric("Disetujui", "0", new Color(16, 185, 129));
        lblBerkasDitolak = createMiniMetric("Ditolak", "0", new Color(239, 68, 68));
        berkasPanel.add(lblBerkasPending.getParent());
        berkasPanel.add(lblBerkasVerified.getParent());
        berkasPanel.add(lblBerkasDitolak.getParent());

        JPanel seleksiPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        seleksiPanel.setOpaque(false);
        lblSeleksiBelum = createMiniMetric("Belum", "0", new Color(148, 163, 184));
        lblSeleksiLulus = createMiniMetric("Lulus", "0", new Color(16, 185, 129));
        lblSeleksiTidak = createMiniMetric("Tidak Lulus", "0", new Color(239, 68, 68));
        lblSeleksiCadangan = createMiniMetric("Cadangan", "0", new Color(245, 158, 11));
        seleksiPanel.add(lblSeleksiBelum.getParent());
        seleksiPanel.add(lblSeleksiLulus.getParent());
        seleksiPanel.add(lblSeleksiTidak.getParent());
        seleksiPanel.add(lblSeleksiCadangan.getParent());

        row.add(wrapCard("Monitoring Berkas Persyaratan", berkasPanel));
        row.add(wrapCard("Monitoring Seleksi Kelulusan Sistem", seleksiPanel));
        return row;
    }

    // <-------------------- HELPER COMPONENT METRICS -------------------->
    private JLabel createMiniMetric(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(AppTheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, accent),
            new EmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(AppTheme.TEXT_DARK);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return lblValue;
    }
    
    private JPanel wrapCard(String title, Component content) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(0, 2, getWidth(), getHeight() - 2, 12, 12);
                g2.setColor(AppTheme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - 2, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(AppTheme.TEXT_DARK);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    // 🎯 REVISI ENGINE: Modifikasi JTable agar Terlihat Flat, Modern & Berjarak Lega
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(38); // Lebih lega
        table.setShowGrid(false); // Buang garis kaku kotak-kotak jadul
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(AppTheme.SURFACE);
        table.setForeground(AppTheme.TEXT_DARK);
        table.setSelectionBackground(new Color(241, 245, 249));
        table.setSelectionForeground(AppTheme.TEXT_DARK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Mempercantik Header Tabel
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(AppTheme.SURFACE);
        header.setForeground(AppTheme.TEXT_SECONDARY);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 30));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        
        // Tambahkan soft horizontal border antar baris
        table.setBorder(BorderFactory.createEmptyBorder());
        return table;
    }

    // <-------------------- BACKGROUND CLOCK TIMER WORKER -------------------->
    public void startClock() {
        clockTimer = new java.util.Timer();
        clockTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        refreshAllData(); 
                        return null;
                    }

                    @Override
                    protected void done() {
                        SwingUtilities.invokeLater(() -> repaint());
                    }
                };
                worker.execute();
            }
        }, 30000, 30000);
    }

    // <-------------------- DATABASE QUERY -------------------->
    public void refreshAllData() {
    if (modelPendaftar == null || modelTugas == null) return;

    modelPendaftar.setRowCount(0);
    modelTugas.setRowCount(0);

    // 🎯 REVISI UTAMA: Menggunakan Try-With-Resources pada Koneksi agar otomatis ditutup total (Auto-Closeable)
    try (Connection conn = DatabaseConfig.getKoneksi();
         Statement st = conn.createStatement()) {

        // =========================================================================
        // 1. CARD STATS UTAMA
        // =========================================================================
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_siswa")) {
            if (rs.next()) cardTotal.setText(String.valueOf(rs.getInt(1)));
        }

        String sqlPrestasi = 
                "SELECT COUNT(*) FROM tbl_siswa s " +
                "JOIN tbl_jalur j ON s.id_jalur = j.id_jalur " +
                "WHERE UPPER(j.nama_jalur) LIKE '%PRESTASI%'";
        try (ResultSet rs = st.executeQuery(sqlPrestasi)) {
            if (rs.next()) cardHariIni.setText(String.valueOf(rs.getInt(1)));
        }

        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_pembayaran WHERE status = 'MENUNGGU_VERIFIKASI'")) {
            if (rs.next()) cardPending.setText(String.valueOf(rs.getInt(1)));
        }

        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_status_pipeline WHERE berkas = 'DITOLAK'")) {
            if (rs.next()) cardDitolak.setText(String.valueOf(rs.getInt(1)));
        }

        String sqlDU = "SELECT COUNT(*) FROM tbl_siswa WHERE id_siswa NOT IN (SELECT id_siswa FROM tbl_daftar_ulang)";
        try (ResultSet rs = st.executeQuery(sqlDU)) {
            if (rs.next()) cardVerified.setText(String.valueOf(rs.getInt(1)));
        }

        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_pengumuman WHERE status = 'TERBIT'")) {
            if (rs.next()) cardKuota.setText(String.valueOf(rs.getInt(1)));
        }

        // =========================================================================
        // 2. MONITORING PANEL BERKAS ROW
        // =========================================================================
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_status_pipeline WHERE berkas = 'PROSES'")) {
            if (rs.next()) lblBerkasPending.setText(String.valueOf(rs.getInt(1)));
        }
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_status_pipeline WHERE berkas = 'SELESAI'")) {
            if (rs.next()) lblBerkasVerified.setText(String.valueOf(rs.getInt(1)));
        }
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_status_pipeline WHERE berkas = 'DITOLAK'")) {
            if (rs.next()) lblBerkasDitolak.setText(String.valueOf(rs.getInt(1)));
        }

        // =========================================================================
        // 3. MONITORING PANEL SELEKSI ROW
        // =========================================================================
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_siswa WHERE id_siswa NOT IN (SELECT id_siswa FROM tbl_seleksi)")) {
            if (rs.next()) lblSeleksiBelum.setText(String.valueOf(rs.getInt(1)));
        }
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_seleksi WHERE status_kelulusan = 'DITERIMA'")) {
            if (rs.next()) lblSeleksiLulus.setText(String.valueOf(rs.getInt(1)));
        }
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_seleksi WHERE status_kelulusan = 'TIDAK_DITERIMA'")) {
            if (rs.next()) lblSeleksiTidak.setText(String.valueOf(rs.getInt(1)));
        }
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_seleksi WHERE status_kelulusan = 'CADANGAN'")) {
            if (rs.next()) lblSeleksiCadangan.setText(String.valueOf(rs.getInt(1)));
        }

        // =========================================================================
        // 4. CHARTS DATA ENGINE (DONUT & LINE)
        // =========================================================================
        Map<String, Integer> jalurData = new java.util.LinkedHashMap<>();
        try (ResultSet rs = st.executeQuery("SELECT j.nama_jalur, COUNT(s.id_siswa) FROM tbl_jalur j LEFT JOIN tbl_siswa s ON j.id_jalur = s.id_jalur GROUP BY j.nama_jalur")) {
            while (rs.next()) {
                jalurData.put(rs.getString(1), rs.getInt(2));
            }
        }
        donutChart.setData(jalurData);
        
        java.util.LinkedHashMap<String, int[]> trendData = new java.util.LinkedHashMap<>();
        trendData.put("10 Juni", new int[]{15, 5});
        trendData.put("11 Juni", new int[]{45, 25});
        trendData.put("12 Juni", new int[]{77, 35}); 
        lineChart.setData(trendData);

        // =========================================================================
        // 5. TABEL PENDAFTAR TERBARU
        // =========================================================================
        String sqlLatest = 
                "SELECT s.nomor_pendaftaran, b.nama_lengkap, " +
                "COALESCE(j.nama_jalur, '-') AS nama_jalur, " +
                "CASE " +
                "    WHEN sl.status_kelulusan IS NOT NULL AND sl.status_kelulusan != 'PROSES' THEN sl.status_kelulusan " +
                "    ELSE s.status_pendaftaran " +
                "END AS status_tampil " +
                "FROM tbl_siswa s " +
                "JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa " +
                "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur " +
                "LEFT JOIN tbl_seleksi sl ON s.id_siswa = sl.id_siswa " +
                "ORDER BY s.id_siswa DESC LIMIT 5";
        
        try (ResultSet rs = st.executeQuery(sqlLatest)) {
            while (rs.next()) {
                modelPendaftar.addRow(new Object[]{
                    rs.getString("nomor_pendaftaran"),
                    rs.getString("nama_lengkap"),
                    rs.getString("nama_jalur"),
                    rs.getString("status_tampil")
                });
            }
        }
        
        // =========================================================================
        // 6. AKTIVITAS & TUGAS OPERASIONAL HARI INI
        // =========================================================================
        modelTugas.addRow(new Object[]{"Belum ada jadwal hari ini. Agenda terdekat pada: 20 Juni 2026"}); 
        modelTugas.addRow(new Object[]{"------------------------------------------------------"}); 
        
        int totalSiswa = 0, lulusSiswa = 0, belumSiswa = 0;
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_siswa")) { if(rs.next()) totalSiswa = rs.getInt(1); }
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_seleksi WHERE status_kelulusan = 'DITERIMA'")) { if(rs.next()) lulusSiswa = rs.getInt(1); }
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_status_pipeline WHERE berkas = 'PROSES'")) { if(rs.next()) belumSiswa = rs.getInt(1); }

        modelTugas.addRow(new Object[]{"STATS:Total Peserta Terdaftar : " + totalSiswa}); 
        modelTugas.addRow(new Object[]{"STATS:Sudah Lolos Seleksi    : " + lulusSiswa}); 
        modelTugas.addRow(new Object[]{"STATS:Belum Hadir Verifikasi : " + belumSiswa}); 

        modelTugas.addRow(new Object[]{"SECTION_HEADER:DAFTAR TUGAS PANITIA"}); 

        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_status_pipeline WHERE berkas = 'PROSES'")) {
            if (rs.next() && rs.getInt(1) > 0) {
                modelTugas.addRow(new Object[]{"• " + rs.getInt(1) + " Berkas masuk menunggu tindakan verifikasi tim panitia"}); 
            }
        }
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_pembayaran WHERE status = 'MENUNGGU_VERIFIKASI'")) {
            if (rs.next() && rs.getInt(1) > 0) {
                modelTugas.addRow(new Object[]{"• " + rs.getInt(1) + " Bukti pembayaran masuk menunggu validasi bank server"}); 
            }
        }
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_status_pipeline WHERE berkas = 'DITOLAK'")) {
            if (rs.next() && rs.getInt(1) > 0) {
                modelTugas.addRow(new Object[]{"• " + rs.getInt(1) + " Dokumen berstatus ditolak dan perlu revisi ulang oleh siswa"}); 
            }
        }
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_status_pipeline WHERE berkas = 'BELUM'")) {
            if (rs.next() && rs.getInt(1) > 0) {
                modelTugas.addRow(new Object[]{"• " + rs.getInt(1) + " Calon siswa terdaftar belum mengunggah dokumen fisik berkas"}); 
            }
        }
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tbl_status_pipeline WHERE formulir = 'BELUM'")) {
            if (rs.next() && rs.getInt(1) > 0) {
                modelTugas.addRow(new Object[]{"• " + rs.getInt(1) + " Akun pendaftar baru terdeteksi belum melengkapi data formulir biodata"}); 
            }
        }

    } catch (SQLException e) {
        System.err.println("[DASHBOARD ERROR] FAIL TO SYNC REALTIME: " + e.getMessage());
    }
}

    // <-------------------- SUB-CLASS DONUT CHART PANEL -------------------->
    private static class DonutChartPanel extends JPanel {
        private Map<String, Integer> data = new LinkedHashMap<>();
        private static final Color[] PALETTE = {
            new Color(59, 130, 246), new Color(16, 185, 129), new Color(245, 158, 11),
            new Color(168, 85, 247), new Color(236, 72, 153)
        };

        DonutChartPanel() { setOpaque(false); setPreferredSize(new Dimension(0, 250)); }
        void setData(Map<String, Integer> data) { this.data = data != null ? data : new LinkedHashMap<>(); repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            if (data == null || data.isEmpty()) {
                g2.setColor(new Color(148, 163, 184)); g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                g2.drawString("Tidak ada data sebaran.", 20, h / 2); g2.dispose(); return;
            }

            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) total = 1;

            int diameter = Math.min(w / 2 - 40, h - 40), centerX = w / 3, centerY = h / 2;
            double startAngle = 90; int idx = 0;

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                if (entry.getValue() == 0) { idx++; continue; }
                double slice = (double) entry.getValue() / total * 360.0;
                g2.setColor(PALETTE[idx % PALETTE.length]);
                g2.fillArc(centerX - diameter / 2, centerY - diameter / 2, diameter, diameter, (int) Math.round(startAngle), (int) Math.round(slice));
                startAngle += slice; idx++;
            }

            g2.setColor(AppTheme.SURFACE); g2.fillOval(centerX - (diameter / 4), centerY - (diameter / 4), diameter / 2, diameter / 2);
            g2.setColor(AppTheme.TEXT_DARK); g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            String totalStr = String.valueOf(total == 1 ? 0 : total);
            g2.drawString(totalStr, centerX - (g2.getFontMetrics().stringWidth(totalStr) / 2), centerY + 6);

            int legendX = w / 2 + 20, legendY = 30; idx = 0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                g2.setColor(PALETTE[idx % PALETTE.length]); g2.fillRoundRect(legendX, legendY, 14, 14, 4, 4);
                g2.setColor(AppTheme.TEXT_DARK); g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString(String.format("%s: %d", entry.getKey(), entry.getValue()), legendX + 20, legendY + 11);
                legendY += 26; idx++;
            }
            g2.dispose();
        }
    }

    // <-------------------- SUB-CLASS LINE CHART PANEL -------------------->
    private static class LineChartPanel extends JPanel {
        private LinkedHashMap<String, int[]> chartData = new LinkedHashMap<>();
        
        LineChartPanel() { setOpaque(false); setPreferredSize(new Dimension(0, 250)); }
        void setData(LinkedHashMap<String, int[]> data) { this.chartData = data != null ? data : new LinkedHashMap<>(); repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int padding = 40;
            int graphW = w - (padding * 2) - 20;
            int graphH = h - (padding * 2);

            if (chartData.isEmpty()) {
                g2.setColor(new Color(148, 163, 184)); g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                g2.drawString("Memproses sinkronisasi data tren...", padding, h / 2);
                g2.dispose(); return;
            }

            int maxVal = 5;
            for (int[] val : chartData.values()) {
                if (val[0] > maxVal) maxVal = val[0];
                if (val[1] > maxVal) maxVal = val[1];
            }
            maxVal = (int) (Math.ceil(maxVal / 5.0) * 5);

            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= 4; i++) {
                int y = padding + graphH - (i * graphH / 4);
                g2.setColor(new Color(241, 245, 249));
                g2.drawLine(padding, y, padding + graphW, y);
                
                g2.setColor(new Color(148, 163, 184));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.drawString(String.valueOf(i * maxVal / 4), padding - 22, y + 4);
            }

            int numPoints = chartData.size();
            int stepX = graphW / Math.max(1, numPoints - 1);
            int x = padding;

            int[] lastPointsTotal = null;
            int[] lastPointsVerif = null;

            for (Map.Entry<String, int[]> entry : chartData.entrySet()) {
                int[] vals = entry.getValue();
                String labelTgl = entry.getKey();

                int yTotal = padding + graphH - (vals[0] * graphH / maxVal);
                int yVerif = padding + graphH - (vals[1] * graphH / maxVal);

                g2.setColor(new Color(241, 245, 249, 180));
                g2.fillRect(x - 4, padding, 8, graphH);

                if (lastPointsTotal != null) {
                    g2.setColor(new Color(59, 130, 246));
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawLine(lastPointsTotal[0], lastPointsTotal[1], x, yTotal);
                }
                
                if (lastPointsVerif != null) {
                    g2.setColor(new Color(16, 185, 129));
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawLine(lastPointsVerif[0], lastPointsVerif[1], x, yVerif);
                }

                lastPointsTotal = new int[]{x, yTotal};
                lastPointsVerif = new int[]{x, yVerif};

                g2.setColor(new Color(59, 130, 246)); g2.fillOval(x - 4, yTotal - 4, 8, 8);
                g2.setColor(new Color(16, 185, 129)); g2.fillOval(x - 4, yVerif - 4, 8, 8);

                g2.setColor(new Color(100, 116, 139));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.drawString(labelTgl, x - (g2.getFontMetrics().stringWidth(labelTgl) / 2), padding + graphH + 16);

                x += stepX;
            }

            int legendX = w - 180, legendY = 12;
            g2.setStroke(new BasicStroke(3f));
            
            g2.setColor(new Color(59, 130, 246)); g2.drawLine(legendX, legendY, legendX + 15, legendY);
            g2.setColor(new Color(30, 41, 59)); g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.drawString("Total Berkas Masuk", legendX + 22, legendY + 4);

            g2.setColor(new Color(16, 185, 129)); g2.drawLine(legendX, legendY + 16, legendX + 15, legendY + 16);
            g2.setColor(new Color(30, 41, 59));
            g2.drawString("Berkas Diverifikasi", legendX + 22, legendY + 20);

            g2.dispose();
        }
    }

    // <-------------------- CUSTOM MODERN 2026 CELL RENDERERS -------------------->

    /**
     * 🎯 RENDERER KIRI: Merubah Status Pendaftaran Menjadi Rounded Badge Warna Soft Web-Style
     */
    private static class StatusBadgeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String val = value != null ? value.toString() : "PROSES";
            
            // Buat label kustom bertindak sebagai badge rounded
            JLabel lblBadge = new JLabel(val, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 4, getWidth() - 4, getHeight() - 8, 10, 12); // Efek melengkung kapsul modern
                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            
            lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblBadge.setOpaque(false);
            
            // Pewarnaan Badge Kondisional Pintar
            if (val.equalsIgnoreCase("DITERIMA") || val.equalsIgnoreCase("LULUS") || val.equalsIgnoreCase("Verified")) {
                lblBadge.setBackground(new Color(220, 252, 231)); // Soft Green
                lblBadge.setForeground(new Color(21, 128, 61));
            } else if (val.equalsIgnoreCase("TIDAK_DITERIMA") || val.equalsIgnoreCase("DITOLAK") || val.equalsIgnoreCase("Rejected")) {
                lblBadge.setBackground(new Color(254, 226, 226)); // Soft Red
                lblBadge.setForeground(new Color(185, 28, 28));
            } else if (val.equalsIgnoreCase("CADANGAN")) {
                lblBadge.setBackground(new Color(254, 243, 199)); // Soft Orange
                lblBadge.setForeground(new Color(180, 83, 9));
            } else { // PENDING / PROSES
                lblBadge.setBackground(new Color(241, 245, 249)); // Soft Grey
                lblBadge.setForeground(new Color(71, 85, 105));
            }
            
            // Handling baris saat di-select kursor mouse
            JPanel cellWrapper = new JPanel(new BorderLayout());
            cellWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)));
            cellWrapper.setBackground(isSelected ? new Color(248, 250, 252) : Color.WHITE);
            cellWrapper.add(lblBadge, BorderLayout.CENTER);
            return cellWrapper;
        }
    }

    /**
     * 🎯 RENDERER KANAN: Menyusun Baris Agenda dan Statistik Berdasarkan Pola Card Tanpa Border Kaku
     */
    private static class ModernTaskCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String fullText = value != null ? value.toString() : "";
            JLabel lblContent = new JLabel();
            lblContent.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            
            JPanel cellWrapper = new JPanel(new BorderLayout());
            cellWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(248, 250, 252)));
            cellWrapper.setBackground(Color.WHITE);

            if (fullText.startsWith("SECTION_HEADER:")) {
                // Style Judul Sub-Kategori Operasional
                String cleanHeader = fullText.replace("SECTION_HEADER:", "");
                lblContent.setText(cleanHeader);
                lblContent.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblContent.setForeground(new Color(100, 116, 139));
                cellWrapper.setBackground(new Color(248, 250, 252));
                cellWrapper.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(226, 232, 240)));
            } else if (fullText.startsWith("STATS:")) {
                // Style Data Ringkasan Absensi Kehadiran
                String cleanStats = fullText.replace("STATS:", "");
                lblContent.setText(cleanStats);
                lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblContent.setForeground(new Color(71, 85, 105));
                lblContent.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 8)); // Menjorok ke dalam
            } else {
                // Style Baris Penugasan Standar / Default Agenda List
                lblContent.setText(fullText);
                lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblContent.setForeground(new Color(15, 23, 42));
                if (fullText.contains("•")) {
                    lblContent.setForeground(new Color(30, 41, 59));
                }
            }

            cellWrapper.add(lblContent, BorderLayout.CENTER);
            return cellWrapper;
        }
    }
}