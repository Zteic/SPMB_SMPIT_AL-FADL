package views.admin;

import config.DatabaseConfig;
import config.SessionManager;
import controllers.AdminController;
import controllers.AutentikasiController;
import controllers.SeleksiOtomatisController;
import models.User;
import pmb.LoginFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Enterprise Admin Main Dashboard Frame (MVC Architecture Verified)
 * 1 Menu Sidebar = 1 Panel View Mandiri = 1 Spesifik SQL Query Filter
 * 100% Selaras dengan Skema Kolom Tabel Fisik Database modern: tbl_siswa, tbl_biodata_siswa, tbl_alamat
 */
public class AdminMainFrame extends JFrame {

    // <-------------------- KONSTANTA -------------------->
    private final Color CLR_BG_MAIN = new Color(245, 247, 250);       
    private final Color CLR_PRIMARY = new Color(44, 62, 80);          
    private final Color CLR_ACCENT = new Color(52, 152, 219);         
    private final Color CLR_SUCCESS = new Color(46, 204, 113);        
    private final Color CLR_WARNING = new Color(241, 196, 15);        
    private final Color CLR_DANGER = new Color(231, 76, 60);          
    private final Color CLR_SIDEBAR_HOVER = new Color(52, 73, 94);    
    private final Color COLOR_TEXT_TITLE = new Color(15, 23, 42);     
    private final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);   

    // <-------------------- ROUTER ENGINE CARDLAYOUT VARIABEL -------------------->
    private JPanel areaKontenTengah;
    private CardLayout pengendaliHalaman;

    // <-------------------- IDENTITAS SUB-PANEL BERBASIS INDEPENDENT CLASS -------------------->
    private DashboardAnalyticsPanel viewDashboardAnalytics;
    private DataSiswaPanel viewSemuaPendaftar;
    private DataSiswaPanel viewPendaftarBaru;
    private DataSiswaPanel viewPendaftarVerified;
    private DataSiswaPanel viewPendaftarRejected;
    private DataSiswaPanel viewPendaftarYatim;
    private KelolaPendaftarPanel viewValidasiData;
    private BerkasVerifikasiPanel viewBerkasDokumen;
    private PengaturanKuotaPanel viewSeleksiSistem;
    private PengumumanAdminPanel viewPengumumanLulus;
    private MasterDataPanel viewMasterData;
    private PengaturanInformasiPanel viewPengaturanInformasi; 
    private AuditLogPanel viewAuditLogs;

    // <-------------------- KOMPONEN FORM -------------------->
    private JLabel lblJamDigital;
    private JLabel lblCountdownTimer;
    private JLabel lblCountdownStatus;
    private String tanggalPenutupanTarget = "2026-06-30 23:59:59";
    private JLabel lblNamaUser;
    private JLabel lblSekolah;
    private JButton btnNotifications;
    private JButton btnProfile;
    private JButton btnToggleSidebar;
    private JPanel sidebarContainer;
    private JButton btnDashboard;
    private JButton btnDataPendaftar;
    private JButton btnDayaTampung;
    private JButton btnLogout;
    private JButton btnBerkas;
    private JButton btnPengaturanInfo; 
    

    // <-------------------- SESSION & STATE -------------------->
    private int currentUserId;
    private boolean sidebarCollapsed = false;

    // <-------------------- CONTROLLER -------------------->
    private final AutentikasiController authController;
    private final AdminController adminController;
    private final SeleksiOtomatisController seleksiController;

    // <-------------------- CONSTRUCTOR -------------------->
    public AdminMainFrame(int idUser, String username, String role) {
        this.currentUserId = idUser;
        this.authController = new AutentikasiController();
        this.adminController = new AdminController();
        this.seleksiController = new SeleksiOtomatisController();

        setTitle("PPDB Management Console - SMPIT AL FADL [MVC Engine]");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        initUI(username, role);
        terapkanHakAksesDinamis();
        mulaiJamDigital();
        mulaiCountdownSPMB();
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    private void initUI(String username, String role) {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(CLR_BG_MAIN);
        setContentPane(rootPanel);

        rootPanel.add(buatHeaderTopBar(username, role), BorderLayout.NORTH);
        sidebarContainer = new JPanel(new BorderLayout());
        sidebarContainer.add(buatSidebarModernClean(), BorderLayout.CENTER);
        rootPanel.add(sidebarContainer, BorderLayout.WEST);

        pengendaliHalaman = new CardLayout();
        areaKontenTengah = new JPanel(pengendaliHalaman);
        areaKontenTengah.setBackground(CLR_BG_MAIN);

        viewDashboardAnalytics = new DashboardAnalyticsPanel();
        viewSemuaPendaftar = new DataSiswaPanel("Semua Data Pendaftar", "ALL");
        viewPendaftarBaru = new DataSiswaPanel("Pendaftar Pending", "Pending");
        viewPendaftarVerified = new DataSiswaPanel("Pendaftar Terverifikasi", "Verified");
        viewPendaftarRejected = new DataSiswaPanel("Pendaftar Ditolak Sistem", "Rejected");
        viewPendaftarYatim = new DataSiswaPanel("Pendaftar Yatim", "YATIM");
        
        viewValidasiData = new KelolaPendaftarPanel();
        viewBerkasDokumen = new BerkasVerifikasiPanel();
        viewSeleksiSistem = new PengaturanKuotaPanel();
        viewPengumumanLulus = new PengumumanAdminPanel();
        viewMasterData = new MasterDataPanel();
        viewPengaturanInformasi = new PengaturanInformasiPanel(); 
        viewAuditLogs = new AuditLogPanel();

        areaKontenTengah.add(viewDashboardAnalytics, "PANEL_DASHBOARD");
        areaKontenTengah.add(viewSemuaPendaftar, "PANEL_ALL");
        areaKontenTengah.add(viewPendaftarBaru, "PANEL_BARU");
        areaKontenTengah.add(viewPendaftarVerified, "PANEL_VERIFIED");
        areaKontenTengah.add(viewPendaftarRejected, "PANEL_REJECTED");
        areaKontenTengah.add(viewPendaftarYatim, "PANEL_YATIM");
        areaKontenTengah.add(viewValidasiData, "PANEL_FORMULIR");
        areaKontenTengah.add(viewBerkasDokumen, "PANEL_BERKAS");
        areaKontenTengah.add(viewSeleksiSistem, "PANEL_SELEKSI");
        areaKontenTengah.add(viewPengumumanLulus, "PANEL_PENGUMUMAN");
        areaKontenTengah.add(viewMasterData, "PANEL_MASTER");
        areaKontenTengah.add(viewPengaturanInformasi, "PANEL_PENGATURAN_INFO"); 
        areaKontenTengah.add(viewAuditLogs, "PANEL_AUDIT");
        
        KelolaJadwalPanel viewKelolaJadwal = new KelolaJadwalPanel(viewDashboardAnalytics);
        areaKontenTengah.add(viewKelolaJadwal, "PANEL_KELOLA_JADWAL");

        viewDashboardAnalytics.refreshAllData();
        rootPanel.add(areaKontenTengah, BorderLayout.CENTER);
    }

    private JPanel buatHeaderTopBar(String username, String role) {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 72));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // 🎯 BEST PRACTICE: Sinkronisasi awal mengambil status teraktif dari memory cache
        config.AppConfig.refreshActivePeriod();
        String teksTahun = config.AppConfig.getTahunAjaranAktif();

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        JLabel lblLogo = new JLabel("SPMB");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(CLR_PRIMARY);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        JPanel schoolInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        schoolInfo.setOpaque(false);
        
        // 🎯 FIXED GLOBAL INSTANCE: Menggunakan global field variabel
        lblSekolah = new JLabel("SMPIT AL FADL - Admin Dashboard (TA " + teksTahun + ")");
        lblSekolah.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSekolah.setForeground(CLR_PRIMARY);
        
        JLabel lblContext = new JLabel("Gelombang 1 Sistem Connected");
        lblContext.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContext.setForeground(COLOR_TEXT_MUTED);
        
        schoolInfo.add(lblSekolah);
        schoolInfo.add(lblContext);

        leftPanel.add(lblLogo, BorderLayout.WEST);
        leftPanel.add(schoolInfo, BorderLayout.CENTER);

        JPanel pnlCountdownBox = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlCountdownBox.setOpaque(false);
        pnlCountdownBox.setBorder(BorderFactory.createEmptyBorder(16, 0, 14, 0));
        
        lblCountdownStatus = new JLabel("Status : Menghitung...", SwingConstants.CENTER);
        lblCountdownStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCountdownStatus.setForeground(COLOR_TEXT_MUTED);
        
        lblCountdownTimer = new JLabel("00 Hari 00 Jam 00 Menit", SwingConstants.CENTER);
        lblCountdownTimer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCountdownTimer.setForeground(CLR_PRIMARY);
        
        pnlCountdownBox.add(lblCountdownStatus);
        pnlCountdownBox.add(lblCountdownTimer);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 18));
        rightPanel.setOpaque(false);

        btnNotifications = new JButton("Notifikasi (0)");
        btnNotifications.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnNotifications.setBackground(new Color(249, 250, 251));
        btnNotifications.setForeground(Color.BLACK);
        btnNotifications.setFocusPainted(false);
        btnNotifications.addActionListener(e -> showNotificationPopup());

        btnProfile = new JButton(username + " | " + role.toUpperCase());
        btnProfile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnProfile.setBackground(new Color(249, 250, 251));
        btnProfile.setForeground(Color.BLACK);
        btnProfile.setFocusPainted(false);
        btnProfile.addActionListener(e -> showProfileMenu(btnProfile));

        lblJamDigital = new JLabel();
        lblJamDigital.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblJamDigital.setForeground(COLOR_TEXT_MUTED);
        
        rightPanel.add(lblJamDigital);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(pnlCountdownBox, BorderLayout.CENTER); 
        header.add(rightPanel, BorderLayout.EAST);

        updateNotificationBadge();
        return header;
    }

    private JPanel buatSidebarModernClean() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(CLR_PRIMARY);
        container.setPreferredSize(new Dimension(280, 0));
        container.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(52, 73, 94)));

        JPanel listMenuPanel = new JPanel();
        listMenuPanel.setLayout(new BoxLayout(listMenuPanel, BoxLayout.Y_AXIS));
        listMenuPanel.setBackground(CLR_PRIMARY);
        listMenuPanel.setBorder(new EmptyBorder(15, 10, 15, 10));

        listMenuPanel.add(buatSidebarSectionLabel("MONITORING & DASHBOARD"));
        btnDashboard = buatRouterMenuButton("Overview Dashboard", e -> {
            viewDashboardAnalytics.refreshAllData();
            showPanel("PANEL_DASHBOARD");
        });
        btnDashboard.setForeground(Color.BLACK); 
        listMenuPanel.add(btnDashboard);

        listMenuPanel.add(Box.createVerticalStrut(10));
        listMenuPanel.add(buatSidebarSectionLabel("DATA REGISTRASI SISWA"));
        
        btnDataPendaftar = buatRouterMenuButton("Semua Data Pendaftar", e -> {
            viewSemuaPendaftar.loadData();
            showPanel("PANEL_ALL");
        });
        btnDataPendaftar.setForeground(Color.BLACK);
        listMenuPanel.add(btnDataPendaftar);
        
        listMenuPanel.add(Box.createVerticalStrut(4));
        JButton btnPendaftarBaru = buatRouterMenuButton("Pendaftar Baru", e -> {
            viewPendaftarBaru.loadData();
            showPanel("PANEL_BARU");
        });
        btnPendaftarBaru.setForeground(Color.BLACK);
        listMenuPanel.add(btnPendaftarBaru);
        
        listMenuPanel.add(Box.createVerticalStrut(4));
        JButton btnPendaftarVerified = buatRouterMenuButton("Pendaftar Diverifikasi", e -> {
            viewPendaftarVerified.loadData();
            showPanel("PANEL_VERIFIED");
        });
        btnPendaftarVerified.setForeground(Color.BLACK);
        listMenuPanel.add(btnPendaftarVerified);
        
        listMenuPanel.add(Box.createVerticalStrut(4));
        JButton btnPendaftarDitolak = buatRouterMenuButton("Pendaftar Ditolak", e -> {
            viewPendaftarRejected.loadData();
            showPanel("PANEL_REJECTED");
        });
        btnPendaftarDitolak.setForeground(Color.BLACK);
        listMenuPanel.add(btnPendaftarDitolak);

        listMenuPanel.add(Box.createVerticalStrut(4));
        JButton btnPendaftarYatim = buatRouterMenuButton("Pendaftar Yatim", e -> {
            viewPendaftarYatim.loadData();
            showPanel("PANEL_YATIM");
        });
        btnPendaftarYatim.setForeground(Color.BLACK);
        listMenuPanel.add(btnPendaftarYatim);

        listMenuPanel.add(Box.createVerticalStrut(10));
        listMenuPanel.add(buatSidebarSectionLabel("VERIFIKASI & BERKAS"));
        
        JButton btnFormulir = buatRouterMenuButton("Formulir & Validasi Data", e -> showPanel("PANEL_FORMULIR"));
        btnFormulir.setForeground(Color.BLACK);
        listMenuPanel.add(btnFormulir);
        
        listMenuPanel.add(Box.createVerticalStrut(4));
        btnBerkas = buatRouterMenuButton("Verifikasi Berkas Dokumen", e -> showPanel("PANEL_BERKAS"));
        btnBerkas.setForeground(Color.BLACK);
        listMenuPanel.add(btnBerkas);

        listMenuPanel.add(Box.createVerticalStrut(10));
        listMenuPanel.add(buatSidebarSectionLabel("AKADEMIK SELEKSI"));
        
        btnDayaTampung = buatRouterMenuButton("Proses Seleksi & Ranking", e -> showPanel("PANEL_SELEKSI"));
        btnDayaTampung.setForeground(Color.BLACK);
        listMenuPanel.add(btnDayaTampung);
        
        listMenuPanel.add(Box.createVerticalStrut(4));
        JButton btnPengumuman = buatRouterMenuButton("Pengumuman Kelulusan", e -> showPanel("PANEL_PENGUMUMAN"));
        btnPengumuman.setForeground(Color.BLACK);
        listMenuPanel.add(btnPengumuman);

        listMenuPanel.add(Box.createVerticalStrut(10));
        listMenuPanel.add(buatSidebarSectionLabel("PENGATURAN DATA MASTER"));
        
        JButton btnMaster = buatRouterMenuButton("Kelola Kuota & Jalur", e -> showPanel("PANEL_MASTER"));
        btnMaster.setForeground(Color.BLACK);
        listMenuPanel.add(btnMaster);
        
        // 🎯 FIX PRESISI SPACING: Memberikan sela strut (4px) konstan di antara boks tombol baru
        listMenuPanel.add(Box.createVerticalStrut(4));
        btnPengaturanInfo = buatRouterMenuButton("Pengaturan Informasi", e -> showPanel("PANEL_PENGATURAN_INFO"));
        btnPengaturanInfo.setForeground(Color.BLACK);
        listMenuPanel.add(btnPengaturanInfo);
        
        listMenuPanel.add(Box.createVerticalStrut(4));
        JButton btnKelolaJadwal = buatRouterMenuButton("Kelola Jadwal Ujian", e -> showPanel("PANEL_KELOLA_JADWAL"));
        btnKelolaJadwal.setForeground(Color.BLACK); 
        listMenuPanel.add(btnKelolaJadwal);
        
        listMenuPanel.add(Box.createVerticalStrut(4));
        JButton btnAudit = buatRouterMenuButton("Audit Trail Logging", e -> showPanel("PANEL_AUDIT"));
        btnAudit.setForeground(Color.BLACK);
        listMenuPanel.add(btnAudit);

        JScrollPane scrollPane = new JScrollPane(listMenuPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setBackground(new Color(34, 49, 63));
        panelBawah.setBorder(new EmptyBorder(10, 12, 10, 12));

        btnLogout = new JButton("   Keluar Aplikasi [Logout]");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(Color.BLACK); 
        btnLogout.setBackground(CLR_DANGER);
        btnLogout.setOpaque(true);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new Dimension(0, 40));
        btnLogout.addActionListener(e -> logout());
        
        panelBawah.add(btnLogout, BorderLayout.CENTER);
        container.add(panelBawah, BorderLayout.SOUTH);

        return container;
    }

    private JLabel buatSidebarSectionLabel(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(148, 163, 184));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(52, 73, 94)),
            BorderFactory.createEmptyBorder(12, 10, 6, 0)
        ));
        return label;
    }

    private JButton buatRouterMenuButton(String label, java.awt.event.ActionListener eventClick) {
        JButton btn = new JButton("   " + label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(241, 245, 249));
        btn.setBackground(CLR_PRIMARY);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(260, 38));
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { if (btn.getBackground() != CLR_ACCENT) btn.setBackground(CLR_SIDEBAR_HOVER); }
            @Override
            public void mouseExited(MouseEvent e) { if (btn.getBackground() != CLR_ACCENT) btn.setBackground(CLR_PRIMARY); }
        });

        btn.addActionListener(e -> {
            Component[] siblings = btn.getParent().getComponents();
            for (Component c : siblings) { if (c instanceof JButton) c.setBackground(CLR_PRIMARY); }
            btn.setBackground(CLR_ACCENT);
        });
        btn.addActionListener(eventClick);
        return btn;
    }

    private void showNotificationPopup() {
        java.util.List<Object[]> items = adminController.getRecentNotifications(currentUserId, 8);
        JDialog dialog = new JDialog(this, "Notifikasi", true);
        dialog.setSize(420, 340);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Pusat Notifikasi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Judul", "Waktu"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        for (Object[] item : items) {
            model.addRow(new Object[]{item[0], item[2]});
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setReorderingAllowed(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnClose = new JButton("Tutup");
        btnClose.setBackground(CLR_ACCENT);
        btnClose.setForeground(Color.BLACK);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dialog.dispose());
        panel.add(btnClose, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void showProfileMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem profil = new JMenuItem("Profil");
        JMenuItem gantiPassword = new JMenuItem("Ganti Password");
        JMenuItem logoutItem = new JMenuItem("Logout");

        profil.addActionListener(e -> JOptionPane.showMessageDialog(this, "Profil admin tidak tersedia di versi ini.", "Profil", JOptionPane.INFORMATION_MESSAGE));
        gantiPassword.addActionListener(e -> JOptionPane.showMessageDialog(this, "Fitur ganti password belum diimplementasi.", "Ganti Password", JOptionPane.INFORMATION_MESSAGE));
        logoutItem.addActionListener(e -> logout());

        menu.add(profil);
        menu.add(gantiPassword);
        menu.addSeparator();
        menu.add(logoutItem);
        menu.show(invoker, 0, invoker.getHeight());
    }

    private void toggleSidebar() {
        if (sidebarCollapsed) {
            sidebarContainer.setPreferredSize(new Dimension(280, 0));
        } else {
            sidebarContainer.setPreferredSize(new Dimension(80, 0));
        }
        sidebarCollapsed = !sidebarCollapsed;
        sidebarContainer.revalidate();
    }

    private void showPanel(String name) {
        pengendaliHalaman.show(areaKontenTengah, name);
    }

    public void updateSidebarPendingBadges() {
        String sql = 
                "SELECT " +
                "  (SELECT COUNT(*) FROM tbl_berkas WHERE status = 'MENUNGGU_VERIFIKASI') AS b, " +
                "  (SELECT COUNT(*) FROM tbl_pembayaran WHERE status = 'MENUNGGU_VERIFIKASI') AS p, " +
                "  (SELECT COUNT(*) FROM tbl_siswa WHERE status_pendaftaran = 'Pending') AS s";
        
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                int berkasPending = rs.getInt("b");
                int bayarPending = rs.getInt("p");
                int seleksiPending = rs.getInt("s");

                if (btnBerkas != null) {
                    btnBerkas.setText("   Verifikasi Berkas Dokumen " + (berkasPending > 0 ? "(" + berkasPending + ")" : ""));
                }
                if (btnDayaTampung != null) {
                    btnDayaTampung.setText("   Proses Seleksi & Ranking " + (seleksiPending > 0 ? "(" + seleksiPending + ")" : ""));
                }
                if (btnNotifications != null) {
                    btnNotifications.setText("Antrean: " + (berkasPending + bayarPending) + " Berkas");
                }
            }
        } catch (SQLException e) {
            // Error diredam aman
        }
    }

    private void updateNotificationBadge() {
        int unread = adminController.getUnreadNotifications(currentUserId);
        btnNotifications.setText("Notifikasi (" + unread + ")");
    }

    private void logout() {
        int opsi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (opsi == JOptionPane.YES_OPTION) {
            authController.logout();
            SessionManager.clearSession();
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }

    private void terapkanHakAksesDinamis() {
        if (!SessionManager.isLoggedIn()) {
            new LoginFrame().setVisible(true);
            this.dispose();
            return;
        }
        String role = SessionManager.getCurrentUser().getRole();
        boolean admin = role.equalsIgnoreCase("ADMIN");
        boolean verifikator = role.equalsIgnoreCase("VERIFIKATOR");

        btnDashboard.setVisible(admin || verifikator);
        btnDataPendaftar.setVisible(admin);
        btnDayaTampung.setVisible(admin);
        
        if (btnPengaturanInfo != null) {
            btnPengaturanInfo.setVisible(admin);
        }
    }

    private void mulaiJamDigital() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy | HH:mm:ss");
            lblJamDigital.setText(sdf.format(new Date()));
        });
        timer.start();
    }
    
    public void bukaDialogDetailSiswaDinamis(String nomorPendaftaran) {
        JDialog dlg = new JDialog(this, "Master Data Detail Calon Siswa — [" + nomorPendaftaran + "]", true);
        dlg.setSize(750, 580);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JTabbedPane tabContainer = new JTabbedPane();
        tabContainer.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tabContainer.addTab("Biodata Siswa", membuatPanelLabelDetail(nomorPendaftaran, "BIODATA"));
        tabContainer.addTab("Orang Tua", membuatPanelLabelDetail(nomorPendaftaran, "ORANG_TUA"));
        tabContainer.addTab("Alamat Rumah", membuatPanelLabelDetail(nomorPendaftaran, "ALAMAT"));
        tabContainer.addTab("Sekolah Asal", membuatPanelLabelDetail(nomorPendaftaran, "SEKOLAH"));
        tabContainer.addTab("Pembayaran", membuatPanelLabelDetail(nomorPendaftaran, "PEMBAYARAN"));
        tabContainer.addTab("Seleksi & Nilai", membuatPanelLabelDetail(nomorPendaftaran, "SELEKSI"));
        tabContainer.addTab("Riwayat Audit", membuatPanelLabelDetail(nomorPendaftaran, "AUDIT"));

        dlg.add(tabContainer, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Tutup");
        btnClose.setForeground(Color.BLACK);
        btnClose.addActionListener(e -> dlg.dispose());
        pnlBottom.add(btnClose);
        dlg.add(pnlBottom, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    public JPanel membuatPanelLabelDetail(String noDaftar, String tipeTab) {
        JPanel pnlRoot = new JPanel(new CardLayout());
        pnlRoot.setBackground(new Color(248, 250, 252));

        JPanel pnlLoading = new JPanel(new GridBagLayout());
        pnlLoading.setBackground(new Color(248, 250, 252));
        JPanel pnlLoadBox = new JPanel();
        pnlLoadBox.setLayout(new BoxLayout(pnlLoadBox, BoxLayout.Y_AXIS));
        pnlLoadBox.setOpaque(false);
        JLabel lblLoad = new JLabel("Memuat data...");
        lblLoad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLoad.setAlignmentX(Component.CENTER_ALIGNMENT);
        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setPreferredSize(new Dimension(150, 4));
        progress.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlLoadBox.add(lblLoad);
        pnlLoadBox.add(Box.createVerticalStrut(8));
        pnlLoadBox.add(progress);
        pnlLoading.add(pnlLoadBox);

        pnlRoot.add(pnlLoading, "LOADING");
        CardLayout cl = (CardLayout) pnlRoot.getLayout();
        cl.show(pnlRoot, "LOADING");

        new SwingWorker<JPanel, Void>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                return buildActualDetailPanel(noDaftar, tipeTab);
            }

            @Override
            protected void done() {
                try {
                    JPanel pnlHasil = get();
                    pnlRoot.add(pnlHasil, "CONTENT");
                    cl.show(pnlRoot, "CONTENT");
                } catch (Exception e) {
                    pnlRoot.add(createErrorStatePanel(), "ERROR");
                    cl.show(pnlRoot, "ERROR");
                }
            }
        }.execute();

        return pnlRoot;
    }

    private JPanel buildActualDetailPanel(String noDaftar, String tipeTab) {
        JPanel pnlMain = new JPanel(new BorderLayout(12, 12));
        pnlMain.setBackground(new Color(248, 250, 252));
        pnlMain.setBorder(new EmptyBorder(12, 12, 12, 12));

        int idSiswa = 0;
        String sqlCek = "SELECT id_siswa FROM tbl_siswa WHERE nomor_pendaftaran = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sqlCek)) {
            ps.setString(1, noDaftar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idSiswa = rs.getInt("id_siswa");
                }
            }
        } catch (Exception e) {
            return createErrorStatePanel();
        }

        if (idSiswa == 0) return createEmptyStatePanel();

        JPanel pnlBanner = new JPanel(new BorderLayout(12, 0)) {
            @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 60); }
            @Override public Dimension getPreferredSize() { return new Dimension(Integer.MAX_VALUE, 60); }
        };
        pnlBanner.setBackground(Color.WHITE);
        pnlBanner.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1), BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        
        String bTitle = "Detail Pendaftar", bSub = "Informasi arsip PPDB.";
        if ("BIODATA".equals(tipeTab)) { bTitle = "Profil Biodata Siswa"; bSub = "Identitas utama calon siswa."; }
        else if ("ORANG_TUA".equals(tipeTab)) { bTitle = "Data Orang Tua / Wali"; bSub = "Kontak penanggung jawab pendaftar."; }
        else if ("ALAMAT".equals(tipeTab)) { bTitle = "Alamat Domisili Rumah"; bSub = "Lokasi tempat tinggal saat ini."; }
        else if ("SEKOLAH".equals(tipeTab)) { bTitle = "Sekolah Asal Kelulusan"; bSub = "Riwayat instansi pendidikan sebelumnya."; }
        else if ("PEMBAYARAN".equals(tipeTab)) { bTitle = "Status Pembayaran Keuangan"; bSub = "Rincian log tagihan masuk."; }
        else if ("SELEKSI".equals(tipeTab)) { bTitle = "Hasil Seleksi Akademik"; bSub = "Status kelulusan sistem."; }
        else if ("AUDIT".equals(tipeTab)) { bTitle = "Audit Trail Logging"; bSub = "Jejak mutasi data pendaftar."; }

        JLabel lblIcon = new JLabel(""); lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20)); pnlBanner.add(lblIcon, BorderLayout.WEST);
        JPanel pnlText = new JPanel(new GridLayout(2, 1, 1, 1)); pnlText.setOpaque(false);
        JLabel title = new JLabel(bTitle); title.setFont(new Font("Segoe UI", Font.BOLD, 13)); title.setForeground(new Color(15, 23, 42));
        JLabel sub = new JLabel(bSub); sub.setFont(new Font("Segoe UI", Font.PLAIN, 11)); sub.setForeground(new Color(148, 163, 184));
        pnlText.add(title); pnlText.add(sub); pnlBanner.add(pnlText, BorderLayout.CENTER);
        pnlMain.add(pnlBanner, BorderLayout.NORTH);

        JPanel pnlGrid = new JPanel(new GridLayout(0, 2, 12, 12));
        pnlGrid.setOpaque(false);

        try (Connection conn = DatabaseConfig.getKoneksi()) {
            if ("BIODATA".equals(tipeTab)) {
                String sqlBio = "SELECT nama_lengkap, nik, nisn, jenis_kelamin, tempat_lahir, tanggal_lahir, agama, nomor_hp, email FROM tbl_biodata_siswa WHERE id_siswa = ? LIMIT 1";
                try (PreparedStatement ps = conn.prepareStatement(sqlBio)) {
                    ps.setInt(1, idSiswa);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            pnlGrid.add(createInfoCard("Nama Lengkap", rs.getString("nama_lengkap")));
                            pnlGrid.add(createInfoCard("NIK", rs.getString("nik")));
                            pnlGrid.add(createInfoCard("NISN", rs.getString("nisn")));
                            pnlGrid.add(createInfoCard("Jenis Kelamin", rs.getString("jenis_kelamin")));
                            pnlGrid.add(createInfoCard("Tempat Lahir", rs.getString("tempat_lahir")));
                            pnlGrid.add(createInfoCard("Tanggal Lahir", String.valueOf(rs.getDate("tanggal_lahir"))));
                            pnlGrid.add(createInfoCard("Agama", rs.getString("agama")));
                            pnlGrid.add(createInfoCard("Nomor HP", rs.getString("nomor_hp")));
                            pnlGrid.add(createInfoCard("Email", rs.getString("email")));
                        } else { return createEmptyStatePanel(); }
                    }
                } catch (Exception e) { pnlGrid.add(createInfoCard("Status Biodata", "Data Terbatas")); }

            } else if ("ORANG_TUA".equals(tipeTab)) {
                String sqlOrtu = "SELECT nama_ayah, hp_ayah, pekerjaan_ayah, nama_ibu, hp_ibu, pekerjaan_ibu FROM tbl_orang_tua WHERE id_siswa = ? LIMIT 1";
                try (PreparedStatement ps = conn.prepareStatement(sqlOrtu)) {
                    ps.setInt(1, idSiswa);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            pnlGrid.add(createInfoCard("Nama Ayah", rs.getString("nama_ayah")));
                            pnlGrid.add(createInfoCard("HP Ayah", rs.getString("hp_ayah")));
                            pnlGrid.add(createInfoCard("Pekerjaan Ayah", rs.getString("pekerjaan_ayah")));
                            pnlGrid.add(createInfoCard("Nama Ibu", rs.getString("nama_ibu")));
                            pnlGrid.add(createInfoCard("HP Ibu", rs.getString("hp_ibu")));
                            pnlGrid.add(createInfoCard("Pekerjaan Ibu", rs.getString("pekerjaan_ibu")));
                        } else { return createEmptyStatePanel(); }
                    }
                } catch (Exception e) { pnlGrid.add(createInfoCard("Data Orang Tua", "Belum Lengkap")); }

            } else if ("ALAMAT".equals(tipeTab)) {
                String sqlAlmt = "SELECT provinsi, kabupaten, kecamatan, kelurahan, rt, rw, kode_pos FROM tbl_alamat WHERE id_siswa = ? LIMIT 1";
                try (PreparedStatement ps = conn.prepareStatement(sqlAlmt)) {
                    ps.setInt(1, idSiswa);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            pnlGrid.add(createInfoCard("Provinsi", rs.getString("provinsi")));
                            pnlGrid.add(createInfoCard("Kabupaten", rs.getString("kabupaten")));
                            pnlGrid.add(createInfoCard("Kecamatan", rs.getString("kecamatan")));
                            pnlGrid.add(createInfoCard("Kelurahan", rs.getString("kelurahan")));
                            pnlGrid.add(createInfoCard("RT", rs.getString("rt")));
                            pnlGrid.add(createInfoCard("RW", rs.getString("rw")));
                            pnlGrid.add(createInfoCard("Kode Pos", rs.getString("kode_pos")));
                        } else { return createEmptyStatePanel(); }
                    }
                } catch (Exception e) { return createEmptyStatePanel(); }

            } else if ("SEKOLAH".equals(tipeTab)) {
                String sqlSch = "SELECT nama_sekolah, npsn, status_sekolah, alamat FROM tbl_sekolah_asal WHERE id_siswa = ? LIMIT 1";
                try (PreparedStatement ps = conn.prepareStatement(sqlSch)) {
                    ps.setInt(1, idSiswa);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            pnlGrid.add(createInfoCard("Nama Sekolah", rs.getString("nama_sekolah")));
                            pnlGrid.add(createInfoCard("NPSN Sekolah", rs.getString("npsn")));
                            pnlGrid.add(createInfoCard("Status Sekolah", rs.getString("status_sekolah")));
                            pnlGrid.add(createInfoCard("Alamat Sekolah", rs.getString("alamat")));
                        } else { return createEmptyStatePanel(); }
                    }
                } catch (Exception e) { return createEmptyStatePanel(); }

            } else if ("PEMBAYARAN".equals(tipeTab)) {
                String sqlBayar = "SELECT nomor_invoice, nominal, metode, status, bukti_bayar FROM tbl_pembayaran WHERE id_siswa = ? LIMIT 1";
                try (PreparedStatement ps = conn.prepareStatement(sqlBayar)) {
                    ps.setInt(1, idSiswa);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            pnlGrid.add(createInfoCard("Nomor Invoice", rs.getString("nomor_invoice")));
                            pnlGrid.add(createInfoCard("Total Tagihan", "Rp " + rs.getDouble("nominal")));
                            pnlGrid.add(createInfoCard("Metode Pembayaran", rs.getString("metode")));
                            pnlGrid.add(createInfoCard("Status Pembayaran", rs.getString("status")));
                            
                            byte[] imgBytes = rs.getBytes("bukti_bayar");
                            JPanel pnlAksiBayar = new JPanel(new GridBagLayout());
                            pnlAksiBayar.setOpaque(false);
                            
                            JButton btnLihatBukti = new JButton("Lihat Bukti Transfer Cloud");
                            btnLihatBukti.setFont(new Font("Segoe UI", Font.BOLD, 12));
                            btnLihatBukti.setBackground(new Color(52, 152, 219)); 
                            btnLihatBukti.setForeground(Color.BLACK);
                            btnLihatBukti.setFocusPainted(false);
                            btnLihatBukti.setPreferredSize(new Dimension(220, 40));
                            
                            String noInvoice = rs.getString("nomor_invoice"); 
                            btnLihatBukti.addActionListener(e -> {
                                if (imgBytes == null || imgBytes.length == 0) {
                                    JOptionPane.showMessageDialog(this, "Siswa belum mengunggah foto bukti fisik pembayaran.", "Bukti Kosong", JOptionPane.WARNING_MESSAGE);
                                } else {
                                    bukaPopupPratinjauBuktiAdmin(imgBytes, noInvoice);
                                }
                            });
                            
                            pnlAksiBayar.add(btnLihatBukti);
                            pnlGrid.add(pnlAksiBayar); 
                        } else { return createEmptyStatePanel(); }
                    }
                } catch (Exception e) { return createEmptyStatePanel(); }

            } else if ("SELEKSI".equals(tipeTab)) {
                String sqlSeleksi = 
                        "SELECT nilai_akademik, nilai_tahfidz, nilai_wawancara, nilai_domisili, total_nilai, ranking, status_kelulusan " +
                        "FROM tbl_seleksi " +
                        "WHERE id_siswa = ? LIMIT 1";
                try (PreparedStatement ps = conn.prepareStatement(sqlSeleksi)) {
                    ps.setInt(1, idSiswa);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            Object akad = rs.getObject("nilai_akademik");
                            Object tahf = rs.getObject("nilai_tahfidz");
                            Object wawa = rs.getObject("nilai_wawancara");
                            Object domi = rs.getObject("nilai_domisili");
                            Object tot  = rs.getObject("total_nilai");
                            int rnk     = rs.getInt("ranking");
                            String stl  = rs.getString("status_kelulusan");

                            pnlGrid.add(createInfoCard("Nilai Tes Akademik", akad != null ? akad.toString() : "Belum Ujian"));
                            pnlGrid.add(createInfoCard("Nilai Hafalan Tahfidz", tahf != null ? tahf.toString() : "Belum Ujian"));
                            pnlGrid.add(createInfoCard("Nilai Wawancara", wawa != null ? wawa.toString() : "Belum Ujian"));
                            pnlGrid.add(createInfoCard("Nilai Point Domisili", domi != null ? domi.toString() : "0"));
                            pnlGrid.add(createInfoCard("Rata-Rata Total Nilai", tot != null ? tot.toString() : "0.0"));
                            pnlGrid.add(createInfoCard("Peringkat Ranking", rnk > 0 ? "# " + rnk : "Belum Perankingan"));
                            pnlGrid.add(createInfoCard("Status Kelulusan", (stl != null && !stl.trim().isEmpty()) ? stl.trim() : "PROSES"));
                        } else {
                            JPanel pnlStandby = new JPanel(new BorderLayout()); 
                            pnlStandby.setOpaque(false);
                            JLabel lblInfoStandby = new JLabel("<html><center><b>HASIL BELUM TERSEDIA</b><br><font color='#64748B'>Siswa baru mendaftar. Jalankan modul kelulusan sistem pada menu<br><b>'Proses Seleksi & Ranking'</b> di sidebar kiri.</font></center></html>", SwingConstants.CENTER);
                            lblInfoStandby.setFont(new Font("Segoe UI", Font.BOLD, 13));
                            pnlStandby.add(lblInfoStandby, BorderLayout.CENTER);
                            return pnlStandby;
                        }
                    }
                } catch (Exception e) { return createErrorStatePanel(); }

            } else if ("AUDIT".equals(tipeTab)) {
                JPanel pnlTimeline = new JPanel(); pnlTimeline.setLayout(new BoxLayout(pnlTimeline, BoxLayout.Y_AXIS));
                pnlTimeline.setBackground(Color.WHITE); pnlTimeline.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                
                String queryLog = 
                        "SELECT l.aksi, l.rincian, l.created_at, u.nama_lengkap AS nama_operator, u.role " +
                        "FROM tbl_audit_logs l " +
                        "LEFT JOIN tbl_users u ON l.id_user = u.id_user " +
                        "WHERE l.rincian LIKE ? OR l.rincian LIKE ? " +
                        "ORDER BY l.id_log DESC LIMIT 30";
                
                try (PreparedStatement ps = conn.prepareStatement(queryLog)) {
                    ps.setString(1, "%" + noDaftar + "%");
                    ps.setString(2, "%id_siswa: " + idSiswa + "%");
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        boolean adaLog = false;
                        while(rs.next()) {
                            adaLog = true;
                            String aksi = rs.getString("aksi");
                            String rincian = rs.getString("rincian");
                            String operator = rs.getString("nama_operator");
                            String role = rs.getString("role");
                            java.sql.Timestamp time = rs.getTimestamp("created_at");
                            
                            String aktorLabel = "Sistem";
                            if (operator != null) {
                                aktorLabel = operator + " (" + role + ")";
                            } else if (aksi.contains("SISWA") || rincian.toLowerCase().contains("pendaftar")) {
                                aktorLabel = "Calon Siswa (Mandiri)";
                            }

                            JLabel lblItem = new JLabel("<html><body><span style='color:#94A3B8; font-family:monospace;'>[" + time + "]</span>&nbsp;&nbsp;<b>" + aksi + "</b> oleh <span style='color:#2563EB;'><b>" + aktorLabel + "</b></span><br><span style='color:#334155;'>Detail: " + rincian + "</span><br><br></body></html>");
                            lblItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                            pnlTimeline.add(lblItem);
                        }
                        
                        if (!adaLog) {
                            JLabel lblEmpty = new JLabel("<html><body><center><font color='#94A3B8'><b>Belum Ada Riwayat Khusus</b><br>Belum ada riwayat mutasi atau interaksi verifikasi admin pada nomor pendaftaran ini.</font></center></body></html>", SwingConstants.CENTER);
                            lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
                            pnlTimeline.add(lblEmpty);
                        }
                    }
                } catch (SQLException e) {
                    pnlTimeline.add(new JLabel("Gagal memproses kronologi log data."));
                }
                
                JScrollPane scrollTimeline = new JScrollPane(pnlTimeline);
                scrollTimeline.setBorder(null);
                scrollTimeline.setOpaque(false);
                scrollTimeline.getViewport().setOpaque(false);
                pnlMain.add(scrollTimeline, BorderLayout.CENTER);
                return pnlMain;
            }
        } catch (SQLException e) {
            return createErrorStatePanel();
        }

        JScrollPane scrollGrid = new JScrollPane(pnlGrid);
        scrollGrid.setBorder(null); scrollGrid.setOpaque(false); scrollGrid.getViewport().setOpaque(false);
        pnlMain.add(scrollGrid, BorderLayout.CENTER);
        return pnlMain;
    }

    private void bukaPopupPratinjauBuktiAdmin(byte[] bytes, String inv) {
        JDialog dlg = new JDialog(this, "Pratinjau Bukti Bayar — " + inv, true);
        dlg.setLayout(new BorderLayout()); 
        dlg.setSize(500, 600); 
        dlg.setLocationRelativeTo(this);

        try {
            Image rawImage = Toolkit.getDefaultToolkit().createImage(bytes);
            ImageIcon imageIcon = new ImageIcon(rawImage);
            
            if (imageIcon.getIconWidth() > 0 && imageIcon.getIconHeight() > 0) {
                Image scaled = rawImage.getScaledInstance(440, 490, Image.SCALE_SMOOTH);
                JLabel imgLbl = new JLabel(new ImageIcon(scaled));
                imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
                dlg.add(new JScrollPane(imgLbl), BorderLayout.CENTER);
            } else {
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                BufferedInputStream bis = new BufferedInputStream(bais);
                Image fallbackImg = ImageIO.read(bis);
                
                if (fallbackImg != null) {
                    Image scaled = fallbackImg.getScaledInstance(440, 490, Image.SCALE_SMOOTH);
                    JLabel imgLbl = new JLabel(new ImageIcon(scaled));
                    imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
                    dlg.add(new JScrollPane(imgLbl), BorderLayout.CENTER);
                } else {
                    JLabel lblError = new JLabel("<html><center><b>Berkas Corrupt / Tidak Valid</b><br>Data biner bukti bayar di database rusak.<br>Minta siswa upload ulang lewat panel siswa.</center></html>", SwingConstants.CENTER);
                    lblError.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    lblError.setForeground(new Color(220, 38, 38));
                    dlg.add(lblError, BorderLayout.CENTER);
                }
            }
        } catch (Exception ex) { 
            return; 
        }

        JButton close = new JButton("Tutup Pratinjau"); 
        close.setBackground(new Color(44, 62, 80)); close.setForeground(Color.BLACK); close.setFocusPainted(false);
        close.setPreferredSize(new Dimension(120, 35));
        close.addActionListener(e -> dlg.dispose());
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); bar.add(close);
        dlg.add(bar, BorderLayout.SOUTH); 
        dlg.setVisible(true);
    }

    private JPanel createInfoCard(String dbColumn, String rawValue) {
        JPanel card = new JPanel(new BorderLayout(4, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel lblKey = new JLabel(formatLabel(dbColumn));
        lblKey.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblKey.setForeground(new Color(148, 163, 184));

        JLabel lblVal = new JLabel(safeValue(rawValue));
        lblVal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblVal.setForeground(new Color(30, 41, 59));

        card.add(lblKey, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        return card;
    }

    private String formatLabel(String dbColumn) {
        if (dbColumn == null) return "Informasi";
        String[] words = dbColumn.split("_");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (w.equalsIgnoreCase("hp")) { sb.append("HP "); continue; }
            if (w.equalsIgnoreCase("nik")) { sb.append("NIK "); continue; }
            if (w.equalsIgnoreCase("nisn")) { sb.append("NISN "); continue; }
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private String safeValue(String s) {
        if (s == null || s.trim().isEmpty() || s.trim().equalsIgnoreCase("null") || s.trim().equals("-")) {
            return "Belum Diisi";
        }
        return s.trim();
    }

    private JPanel createEmptyStatePanel() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(30, 30, 30, 30));
        JLabel lblIcon = new JLabel(""); lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 40)); lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblTitle = new JLabel("Data tidak ditemukan"); lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13)); lblTitle.setForeground(new Color(100, 116, 139)); lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblSub = new JLabel("Berkas registrasi pendaftar belum terisi."); lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11)); lblSub.setForeground(new Color(148, 163, 184)); lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnl.add(lblIcon); pnl.add(Box.createVerticalStrut(8)); pnl.add(lblTitle); pnl.add(Box.createVerticalStrut(2)); pnl.add(lblSub);
        return pnl;
    }

    private JPanel createErrorStatePanel() {
        JPanel pnl = new JPanel(new GridBagLayout()); pnl.setBackground(Color.WHITE);
        JPanel card = new JPanel(); card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); card.setBackground(new Color(254, 226, 226));
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(239, 68, 68), 1), BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        JLabel title = new JLabel("Terjadi gangguan struktur tabel sistem."); title.setFont(new Font("Segoe UI", Font.BOLD, 12)); title.setForeground(new Color(185, 28, 28)); title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = new JLabel("Silakan periksa kembali kecocokan field skema database."); sub.setFont(new Font("Segoe UI", Font.PLAIN, 11)); sub.setForeground(new Color(185, 28, 28)); sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title); card.add(Box.createVerticalStrut(4)); card.add(sub); pnl.add(card);
        return pnl;
    }

    public void eksekusiCetakKartuPesertaSiswa(String nomorDaftar) {
        String pathFile = "C:/Users/Rivaldi/Garasiku/SPMB_SMPIT-ALFADL/Kartu_Peserta_" + nomorDaftar.replace("/", "_") + ".txt";
        String sql = 
                "SELECT b.nama_lengkap, b.nik, b.nisn, j.nama_jalur, s.created_at " +
                "FROM tbl_siswa s " +
                "LEFT JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa " +
                "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur " +
                "WHERE s.nomor_pendaftaran = ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomorDaftar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    File file = new File(pathFile);
                    try (PrintWriter pw = new PrintWriter(file)) {
                        pw.println("=================================================");
                        pw.println("        KARTU PESERTA UJIAN MASUK PPDB           ");
                        pw.println("               SMPIT AL FADL                     ");
                        pw.println("=================================================");
                        pw.println(" No. Registrasi : " + nomorDaftar);
                        pw.println(" Nama Lengkap   : " + rs.getString("nama_lengkap"));
                        pw.println(" NIK            : " + rs.getString("nik"));
                        pw.println(" NISN Asal      : " + rs.getString("nisn"));
                        pw.println(" Jalur Pilihan  : " + rs.getString("nama_jalur"));
                        pw.println(" Waktu Daftar   : " + rs.getTimestamp("created_at"));
                        pw.println("=================================================");
                        pw.println(" * Bawa kartu ini saat tes wawancara & tahfidz   ");
                        pw.flush();
                        
                        JOptionPane.showMessageDialog(this, "Kartu Peserta Berhasil Dicetak di lokasi:\n" + pathFile, "Cetak Sukses", JOptionPane.INFORMATION_MESSAGE);
                        Desktop.getDesktop().open(file);
                    }
                }
            }
        } catch (Exception e) {
            // Error stream diredam aman
        }
    }

    private class DataSiswaPanel extends JPanel {
        private final DefaultTableModel modelTabel;
        private final String filterStatusCode;

        public DataSiswaPanel(String subSectionTitle, String statusCode) {
            this.filterStatusCode = statusCode;
            setLayout(new BorderLayout(0, 15));
            setBackground(CLR_BG_MAIN);
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JPanel topHeader = new JPanel(new GridLayout(2, 1));
            topHeader.setOpaque(false);
            JLabel t = new JLabel(subSectionTitle); t.setFont(new Font("Segoe UI", Font.BOLD, 22)); t.setForeground(new Color(15, 23, 42));
            JLabel s = new JLabel("Skema Sumber Hubungan Database: tbl_siswa, tbl_biodata_siswa, tbl_alamat, tbl_orang_tua, tbl_sekolah_asal");
            s.setFont(new Font("Segoe UI", Font.PLAIN, 13)); s.setForeground(new Color(100, 116, 139));
            topHeader.add(t); topHeader.add(s);

            JPanel pnlFilterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            pnlFilterBar.setOpaque(false);
            pnlFilterBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

            JLabel lblCari = new JLabel("Cari Data:");
            lblCari.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblCari.setForeground(COLOR_TEXT_TITLE);

            String[] listKategori = {"Semua Kategori", "No Daftar", "Nama", "Jalur", "Asal Sekolah"};
            JComboBox<String> cbKategori = new JComboBox<>(listKategori);
            cbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            cbKategori.setPreferredSize(new Dimension(140, 32));

            JTextField txtCari = new JTextField();
            txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            txtCari.setPreferredSize(new Dimension(250, 32));
            
            JButton btnCari = new JButton("Filter");
            btnCari.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnCari.setBackground(CLR_ACCENT);
            btnCari.setForeground(Color.BLACK);
            btnCari.setPreferredSize(new Dimension(90, 32));
            btnCari.setFocusPainted(false);

            JButton btnReset = new JButton("Reset");
            btnReset.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnReset.setBackground(new Color(148, 163, 184));
            btnReset.setForeground(Color.BLACK);
            btnReset.setPreferredSize(new Dimension(90, 32));
            btnReset.setFocusPainted(false);

            pnlFilterBar.add(lblCari);
            pnlFilterBar.add(cbKategori);
            pnlFilterBar.add(txtCari);
            pnlFilterBar.add(btnCari);
            pnlFilterBar.add(btnReset);

            JPanel pnlNorthContainer = new JPanel();
            pnlNorthContainer.setLayout(new BoxLayout(pnlNorthContainer, BoxLayout.Y_AXIS));
            pnlNorthContainer.setOpaque(false);
            pnlNorthContainer.add(topHeader);
            pnlNorthContainer.add(Box.createVerticalStrut(8));
            pnlNorthContainer.add(pnlFilterBar);
            
            add(pnlNorthContainer, BorderLayout.NORTH);

            String[] headerLabels = {
                    "No", "No Daftar", "Nama Lengkap", "NIK", "Tempat Tgl Lahir",
                    "JK", "Agama", "Asal Sekolah", "Kel/Kec", "Kota/Prov",
                    "Jalur", "Nama Ayah", "Nama Ibu", "St. Pendaftaran", "St. Seleksi"
            };
            modelTabel = new DefaultTableModel(headerLabels, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            JTable tableData = new JTable(modelTabel);
            tableData.setRowHeight(35);
            tableData.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tableData.setShowGrid(false);
            tableData.setIntercellSpacing(new Dimension(0, 0));

            JTableHeader tableHeader = tableData.getTableHeader();
            tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
            tableHeader.setBackground(new Color(235, 243, 250));
            tableHeader.setForeground(CLR_PRIMARY);
            tableHeader.setPreferredSize(new Dimension(0, 35));

            tableData.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                    Component component = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                    if (!isS) {
                        component.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                        component.setForeground(new Color(15, 23, 42)); 
                    } else {
                        component.setBackground(new Color(219, 234, 254)); 
                        component.setForeground(Color.BLACK); 
                    }
                    return component;
                }
            });

            tableData.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && tableData.getSelectedRow() != -1) {
                        int rowIdx = tableData.getSelectedRow();
                        String noDaftar = tableData.getValueAt(rowIdx, 1).toString();
                        bukaDialogDetailSiswaDinamis(noDaftar);
                    }
                }
            });

            btnCari.addActionListener(e -> {
                String keyword = txtCari.getText();
                String kategori = (String) cbKategori.getSelectedItem();
                adminController.cariPendaftarBerdasarkanKategori(modelTabel, keyword, kategori);
            });

            txtCari.addActionListener(e -> btnCari.doClick());

            btnReset.addActionListener(e -> {
                txtCari.setText("");
                cbKategori.setSelectedIndex(0);
                loadData(); 
            });

            JScrollPane scrollPane = new JScrollPane(tableData);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
            scrollPane.getViewport().setBackground(Color.WHITE);
            add(scrollPane, BorderLayout.CENTER);
        }

        public void loadData() {
            adminController.populateTableByStatus(modelTabel, filterStatusCode);
        }

        public void searchPendaftar(String keyword) {
            adminController.cariPendaftarBerdasarkanKategori(modelTabel, keyword, "Semua Kategori");
        }
    }

    private class DynamicTablePlaceholder extends JPanel {
        public DynamicTablePlaceholder(String subTitleName, String relationTable) {
            setLayout(new BorderLayout());
            setBackground(CLR_BG_MAIN);
            JLabel lbl = new JLabel("<html><center><b>" + subTitleName + "</b><br><font color='#64748B'>Modul Skema Terelasi: " + relationTable + "</font></center></html>", SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            add(lbl, BorderLayout.CENTER);
        }
    }
    
    private void mulaiCountdownSPMB() {
        // 🎯 FIXED COLUMN NAME: Mengubah 'status' menjadi 'status_operasional' agar selaras dengan skema database terbaru
        String sqlCekConfig = "SELECT tanggal_penutupan FROM tbl_tahun_ajaran WHERE UPPER(status_operasional) = 'AKTIF' LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sqlCekConfig);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getTimestamp("tanggal_penutupan") != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                tanggalPenutupanTarget = sdf.format(rs.getTimestamp("tanggal_penutupan"));
            }
        } catch (SQLException e) {
            // Jika kolom bermasalah atau data kosong, berikan fallback waktu masehi 2026 yang aman
            tanggalPenutupanTarget = "2026-06-30 23:59:59";
        }

        Timer timerCountdown = new Timer(60000, e -> jalankanKalkulasiMundur());
        timerCountdown.start();
        
        jalankanKalkulasiMundur();
    }

    private void jalankanKalkulasiMundur() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateTarget = sdf.parse(tanggalPenutupanTarget);
            Date dateSekarang = new Date(); 

            long selisihMilidetik = dateTarget.getTime() - dateSekarang.getTime();

            if (selisihMilidetik <= 0) {
                lblCountdownTimer.setText("MASA PENDAFTARAN TELAH DITUTUP");
                lblCountdownTimer.setForeground(CLR_DANGER);
                lblCountdownStatus.setText("Status: Masa Pendaftaran Berakhir");
                lblCountdownStatus.setForeground(CLR_DANGER);
                return;
            }

            long sisaHari = selisihMilidetik / (24 * 60 * 60 * 1000);
            long sisaJam = (selisihMilidetik % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
            long sisaMenit = (selisihMilidetik % (60 * 60 * 1000)) / (60 * 1000);

            lblCountdownTimer.setText(sisaHari + " Hari " + sisaJam + " Jam " + sisaMenit + " Menit");

            if (sisaHari > 30) {
                lblCountdownTimer.setForeground(new Color(21, 128, 61)); 
                lblCountdownStatus.setText("Status : Normal");
                lblCountdownStatus.setForeground(COLOR_TEXT_MUTED);
            } else if (sisaHari >= 7 && sisaHari <= 30) {
                lblCountdownTimer.setForeground(new Color(180, 83, 9)); 
                lblCountdownStatus.setText("Status : Segera Berakhir");
                lblCountdownStatus.setForeground(new Color(180, 83, 9));
            } else {
                lblCountdownTimer.setForeground(CLR_DANGER); 
                lblCountdownStatus.setText("Status : Pendaftaran Akan Ditutup!");
                lblCountdownStatus.setForeground(CLR_DANGER);
            }

        } catch (Exception ex) {
            lblCountdownTimer.setText("Kalkulasi Waktu...");
        }
    }
    /**
     * Memicu pembaruan teks judul header secara realtime tanpa perlu restart aplikasi
     */
    public void sinkronisasiUIPeriodeAktif() {
        config.AppConfig.refreshActivePeriod();
        if (lblSekolah != null) {
            lblSekolah.setText("SMPIT AL FADL - Admin Dashboard (TA " + config.AppConfig.getTahunAjaranAktif() + ")");
        }
    }
}