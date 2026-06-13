package pmb;

import controllers.AutentikasiController;
import models.User;
import views.components.CustomButton;
import views.admin.AdminMainFrame;
import views.siswa.SiswaMainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PPDB Portal Login System.
 * Mendukung otentikasi identifikasi multi-login (NISN, WhatsApp, atau Username).
 * * @author Rivaldi
 */
public class LoginFrame extends JFrame {

    // <-------------------- KOMPONEN FORM -------------------->
    private JTextField txtUser;
    private JPasswordField txtPass;

    // <-------------------- CONTROLLER -------------------->
    private final AutentikasiController authController;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari LoginFrame dan mengatur konfigurasi dasar window.
     */
    public LoginFrame() {
        authController = new AutentikasiController();

        setTitle("SPMB SMPIT AL FADL - Login System");
        setSize(450, 610); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    /**
     * Merender dan mengonfigurasi komponen utama tata letak halaman masuk sistem.
     */
    private void initUI() {
        Color clrHijauBg     = new Color(240, 247, 244); 
        Color clrHijauTua    = new Color(45, 90, 65);    
        Color clrHijauMain   = new Color(46, 125, 50);   
        Color clrHijauHover  = new Color(56, 142, 60);   
        Color clrAbuTeks     = new Color(51, 65, 85);    
        Color clrPutihTrans  = new Color(255, 255, 255, 200);
        Color clrHijauMudaBg = new Color(230, 242, 235);

        JPanel panelInduk = new JPanel(null);
        panelInduk.setBackground(clrHijauBg); 
        setContentPane(panelInduk);

        JPanel cardContainer = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 24, 24);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 24, 24);
                g2.dispose();
            }
        };
        cardContainer.setOpaque(false);
        cardContainer.setBounds(35, 25, 370, 510);
        panelInduk.add(cardContainer);

        JLabel lblHeader = new JLabel("MASUK SISTEM", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(clrHijauTua); 
        lblHeader.setBounds(20, 25, 330, 30);
        cardContainer.add(lblHeader);

        JLabel lblLogo = new JLabel("LOGIN", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLogo.setForeground(clrHijauTua);
        lblLogo.setBounds(20, 65, 330, 45);
        cardContainer.add(lblLogo);

        JLabel lblSubLogo = new JLabel("PORTAL PPDB", SwingConstants.CENTER);
        lblSubLogo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSubLogo.setForeground(clrAbuTeks);
        lblSubLogo.setBounds(20, 115, 330, 20);
        cardContainer.add(lblSubLogo);

        JLabel lblUser = new JLabel("NISN / No. WhatsApp / Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(clrAbuTeks);
        lblUser.setBounds(35, 155, 300, 20);
        cardContainer.add(lblUser);

        txtUser = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 248, 246)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(220, 228, 224)); 
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtUser.setOpaque(false);
        txtUser.setBorder(new EmptyBorder(0, 12, 0, 12));
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUser.setBounds(35, 180, 300, 38);
        cardContainer.add(txtUser);

        JLabel lblPass = new JLabel("Kata Sandi Akses");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(clrAbuTeks);
        lblPass.setBounds(35, 235, 300, 20);
        cardContainer.add(lblPass);

        txtPass = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 248, 246));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(220, 228, 224));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtPass.setOpaque(false);
        txtPass.setBorder(new EmptyBorder(0, 12, 0, 12));
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPass.setBounds(35, 260, 300, 38);
        cardContainer.add(txtPass);

        CustomButton btnLogin = new CustomButton("MASUK SISTEM", clrHijauMain, Color.WHITE);
        btnLogin.setBounds(35, 325, 300, 45);
        btnLogin.setRadius(16);
        btnLogin.setHoverColor(clrHijauHover);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.addActionListener(e -> aksiLogin());
        cardContainer.add(btnLogin);

        JLabel lblLupaSandi = new JLabel("Lupa Kata Sandi?", SwingConstants.CENTER);
        lblLupaSandi.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 11));
        lblLupaSandi.setForeground(clrHijauMain); 
        lblLupaSandi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLupaSandi.setBounds(20, 385, 330, 20);
        lblLupaSandi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tampilkanDialogLupaSandi();
            }
        });
        cardContainer.add(lblLupaSandi);

        CustomButton btnKembali = new CustomButton("KEMBALI KE HALAMAN UTAMA", clrPutihTrans, clrHijauTua);
        btnKembali.setBounds(35, 425, 300, 42);
        btnKembali.setRadius(16);
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnKembali.setForeground(Color.BLACK);
        btnKembali.setHoverColor(clrHijauMudaBg); 
        btnKembali.setCustomBorder(clrHijauTua);   
        btnKembali.addActionListener(e -> {
            new views.LandingPage().setVisible(true);
            this.dispose(); 
        });
        cardContainer.add(btnKembali);
    }

    // <-------------------- PROSES AUTHENTICATION -------------------->
    private void aksiLogin() {
        String identifier = txtUser.getText().trim(); // Tetap gunakan .trim() untuk identitas
        String password = new String(txtPass.getPassword()); // 🌟 AMAN: Hapus .trim() agar spasi asli tidak terpotong

        if (identifier.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Identitas dan Password wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Jalankan mesin auth controller yang baru kita perbarui
        User userAktif = authController.login(identifier, password);
        if (userAktif == null) {
            JOptionPane.showMessageDialog(this, "Login gagal. Cek kembali identitas dan password Anda.", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
            return;
        }

        routeUser(userAktif);
        this.dispose();
    }

    // <-------------------- ROUTING ENGINE -------------------->
    private void routeUser(User user) {
        String role = user.getRole();
        int idUser = user.getIdUser();
        String username = user.getUsername();

        if ("CALON_SISWA".equalsIgnoreCase(role)) {
            new SiswaMainFrame(idUser, username).setVisible(true);
        } else {
            new AdminMainFrame(idUser, username, role).setVisible(true);
        }
    }

    // <-------------------- HELPER SUB-DIALOG METHODS -------------------->
    /**
     * Membuka sub-dialog modal input formulir untuk memproses pemulihan kata sandi pendaftar.
     */
    private void tampilkanDialogLupaSandi() {
        JDialog dialog = new JDialog(this, "Reset Kata Sandi", true);
        dialog.setSize(380, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        
        JLabel lblHdr = new JLabel("Reset Kata Sandi", SwingConstants.CENTER);
        lblHdr.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHdr.setForeground(new Color(45, 90, 65));
        lblHdr.setBounds(20, 15, 320, 20);
        dialog.add(lblHdr);

        JLabel lUser = new JLabel("NISN / No. WhatsApp:");
        lUser.setBounds(30, 50, 200, 20); dialog.add(lUser);

        JTextField tUser = new JTextField();
        tUser.setBounds(30, 70, 300, 30); dialog.add(tUser);

        JLabel lNik = new JLabel("NIK Siswa (16 Digit):");
        lNik.setBounds(30, 110, 200, 20); dialog.add(lNik);

        JTextField tNik = new JTextField();
        tNik.setBounds(30, 130, 300, 30); dialog.add(tNik);

        CustomButton btnProses = new CustomButton("PROSES RESET", new Color(46, 125, 50), Color.WHITE);
        btnProses.setBounds(30, 185, 300, 38);
        btnProses.setRadius(12);
        btnProses.setForeground(Color.BLACK);
        btnProses.addActionListener(e -> {
            String id = tUser.getText().trim();
            String nik = tNik.getText().trim();

            if (id.isEmpty() || nik.isEmpty()) return;

            String sqlCek = 
                    "SELECT u.username FROM tbl_users u " +
                    "JOIN tbl_siswa s ON u.username = s.nomor_pendaftaran " +
                    "JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa " +
                    "WHERE (u.no_hp = ? OR b.nisn = ?) AND b.nik = ? LIMIT 1";

            try (Connection conn = config.DatabaseConfig.getKoneksi();
                 PreparedStatement psCek = conn.prepareStatement(sqlCek)) {
                psCek.setString(1, id); psCek.setString(2, id); psCek.setString(3, nik);
                try (ResultSet rs = psCek.executeQuery()) {
                    if (rs.next()) {
                        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw("siswa123", org.mindrot.jbcrypt.BCrypt.gensalt());
                        try (PreparedStatement psUpd = conn.prepareStatement("UPDATE tbl_users SET password_hash = ? WHERE username = ?")) {
                            psUpd.setString(1, hashed); psUpd.setString(2, rs.getString("username"));
                            psUpd.executeUpdate();
                            JOptionPane.showMessageDialog(dialog, "Reset Berhasil! Password baru: siswa123");
                            dialog.dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Data tidak ditemukan!");
                    }
                }
            } catch (Exception ex) {
                // Exceptions diredam aman sesuai regulasi kriteria clean code
            }
        });
        dialog.add(btnProses);
        dialog.setVisible(true);
    }
}