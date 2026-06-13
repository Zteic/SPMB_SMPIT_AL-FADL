package views.siswa;

import config.SessionManager;
import controllers.AutentikasiController;
import views.LandingPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Core Client Portal Siswa - Modern No-Sidebar Layout.
 * Menyinkronkan seluruh sub-panel (Home, Berkas, Pengumuman) secara realtime.
 * * @author Rivaldi
 */
public class SiswaMainFrame extends JFrame {

    // <-------------------- KOMPONEN FORM -------------------->
    private JPanel content;
    private CardLayout card;
    private JLabel lblJam;
    private JButton btnBackToDashboard; 

    // <-------------------- SESSION & STATE -------------------->
    private final int idUser;
    private final String nomorDaftar;
    
    // <-------------------- CONTROLLER -------------------->
    private final AutentikasiController auth;
    
    // <-------------------- INDEPENDENT PANEL VIEWS -------------------->
    private DashboardSiswaPanel dashboardPanel;
    private KelolaBerkasPanel berkasPanel;
    private PengumumanPanel pengumumanPanel;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance utama dari frame aplikasi client portal siswa mandiri.
     * * @param idUser id unik dari user pengakses
     * @param username nomor pendaftaran siswa sebagai identitas login
     */
    public SiswaMainFrame(int idUser, String username) {
        this.idUser = idUser;
        this.nomorDaftar = username;
        this.auth = new AutentikasiController();

        setTitle("SPMB SISWA - SMPIT AL FADL");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        root.add(buildTopHeader(), BorderLayout.NORTH);

        card = new CardLayout();
        content = new JPanel(card);
        content.setBackground(new Color(243, 244, 246));

        dashboardPanel = new DashboardSiswaPanel();
        berkasPanel = new KelolaBerkasPanel();
        pengumumanPanel = new PengumumanPanel(); 
        
        content.add(dashboardPanel, "HOME");
        content.add(berkasPanel, "BERKAS");
        content.add(pengumumanPanel, "PENGUMUMAN");

        root.add(content, BorderLayout.CENTER);

        checkSession();
        startClock();
        
        if (dashboardPanel != null) {
            dashboardPanel.loadDashboard();
        }
    }
    
    // <-------------------- INISIALISASI KOMPONEN TOPBAR -------------------->
    /**
     * Membangun komponen header topbar bilah atas beserta kontrol logout.
     */
    private JPanel buildTopHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Color.WHITE);
        h.setPreferredSize(new Dimension(0, 60));
        h.setBorder(new EmptyBorder(5, 20, 5, 20));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        leftPanel.setOpaque(false);

        JLabel title = new JLabel("SPMB SISWA - SMPIT AL FADL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(17, 24, 39));
        leftPanel.add(title);

        btnBackToDashboard = new JButton("Kembali ke Beranda");
        btnBackToDashboard.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBackToDashboard.setForeground(Color.BLACK); 
        btnBackToDashboard.setBackground(Color.WHITE);
        btnBackToDashboard.setFocusPainted(false);
        btnBackToDashboard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBackToDashboard.setVisible(false); 
        btnBackToDashboard.addActionListener(e -> showPage("HOME"));
        leftPanel.add(btnBackToDashboard);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightPanel.setOpaque(false);

        lblJam = new JLabel();
        lblJam.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblJam.setForeground(new Color(107, 114, 128));
        
        JLabel user = new JLabel("No. Registrasi: " + nomorDaftar);
        user.setFont(new Font("Segoe UI", Font.BOLD, 12));
        user.setForeground(new Color(55, 65, 81));

        JButton btnLogout = new JButton("Keluar (Logout)");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(Color.BLACK); 
        btnLogout.setBackground(new Color(254, 226, 226)); 
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(252, 165, 165), 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());

        rightPanel.add(lblJam);
        rightPanel.add(user);
        rightPanel.add(btnLogout);

        h.add(leftPanel, BorderLayout.WEST);
        h.add(rightPanel, BorderLayout.EAST);
        
        JPanel bottomLine = new JPanel();
        bottomLine.setBackground(new Color(229, 231, 235));
        bottomLine.setPreferredSize(new Dimension(0, 1));
        h.add(bottomLine, BorderLayout.SOUTH);

        return h;
    }

    // <-------------------- PROSES UTILITY SECURITY -------------------->
    private void checkSession() {
        if (!SessionManager.isLoggedIn()) {
            new LandingPage().setVisible(true);
            dispose();
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin keluar dari sistem aplikasi?", "Konfirmasi Logout", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            auth.logout();
            new LandingPage().setVisible(true);
            dispose();
        }
    }

    private void startClock() {
        Timer t = new Timer(1000, e -> {
            lblJam.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
        });
        t.start();
    }

    // <-------------------- ROUTING ENGINE INTERN -------------------->
    /**
     * Mesin Routing Internal halaman Portal Siswa.
     * Memicu trigger Refresh Realtime otomatis setiap berpindah tab halaman.
     * * @param page id nama halaman target routing ("HOME", "BERKAS", "PENGUMUMAN")
     */
    public void showPage(String page) {
        if (card != null && content != null) {
            
            if ("HOME".equals(page)) {
                btnBackToDashboard.setVisible(false);
                if (dashboardPanel != null) {
                    dashboardPanel.loadDashboard(); 
                }
            } else {
                btnBackToDashboard.setVisible(true);
                
                if ("BERKAS".equals(page) && berkasPanel != null) {
                    berkasPanel.muatStatusBerkasSiswa();
                } else if ("PENGUMUMAN".equals(page) && pengumumanPanel != null) {
                    pengumumanPanel.loadDataSeleksi();
                }
            }
            
            card.show(content, page);
            revalidate();
            repaint();
        }
    }
}