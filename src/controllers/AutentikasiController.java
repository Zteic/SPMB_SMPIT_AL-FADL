package controllers;

import config.DatabaseConfig;
import config.SessionManager;
import models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AutentikasiController {

    public User login(String identifier, String password) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) {
            System.err.println("[AUTH] Database tidak tersedia.");
            return null;
        }

        String sql = 
            "SELECT u.* " +
            "FROM tbl_users u " +
            "LEFT JOIN tbl_siswa s ON u.username = s.nomor_pendaftaran " +
            "LEFT JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa " +
            "WHERE u.username = ? OR u.email = ? OR u.no_hp = ? " +
            "   OR b.nisn = ? OR b.nomor_hp = ? " +
            "LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String cleanIdentifier = identifier.trim();
            
            ps.setString(1, cleanIdentifier);
            ps.setString(2, cleanIdentifier);
            ps.setString(3, cleanIdentifier);
            ps.setString(4, cleanIdentifier);
            ps.setString(5, cleanIdentifier);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("[AUTH] Identifier tidak ditemukan.");
                    return null;
                }

                int idUser = rs.getInt("id_user");
                String username = rs.getString("username"); 
                String storedPassword = rs.getString("password_hash");
                String namaLengkap = rs.getString("nama_lengkap");
                String role = rs.getString("role");
                String status = rs.getString("status");

                if (!"AKTIF".equalsIgnoreCase(status)) {
                    System.out.println("[AUTH] Akun tidak aktif.");
                    return null;
                }

                boolean loginBerhasil = false;
                boolean perluMigrasi = false;

                if (storedPassword != null && 
                    (storedPassword.startsWith("$2a$") || 
                     storedPassword.startsWith("$2b$") || 
                     storedPassword.startsWith("$2y$"))) {
                    loginBerhasil = BCrypt.checkpw(password, storedPassword);
                } else {
                    loginBerhasil = password.equals(storedPassword);
                    perluMigrasi = loginBerhasil;
                }

                if (!loginBerhasil) {
                    safeAudit(idUser, "LOGIN_GAGAL", "Password salah");
                    return null;
                }

                if (perluMigrasi) {
                    migrasikanKeBCrypt(idUser, password);
                }

                User user = new User();
                user.setIdUser(idUser);
                user.setUsername(username); 
                user.setNamaLengkap(namaLengkap);
                user.setRole(role);
                user.setStatus(status);

                SessionManager.setCurrentUser(user);
                safeAudit(idUser, "LOGIN", "Login berhasil sebagai " + role);
                System.out.println("[AUTH SUCCESS] " + username + " berhasil login.");
                return user;
            }
        } catch (SQLException e) {
            System.err.println("[AUTH ERROR] " + e.getMessage());
        }
        return null;
    }

    public void logout() {
        if (!SessionManager.isLoggedIn()) {
            return;
        }
        User user = SessionManager.getCurrentUser();
        safeAudit(user.getIdUser(), "LOGOUT", "User keluar dari sistem");
        SessionManager.clearSession();
    }

    private void migrasikanKeBCrypt(int idUser, String plainPassword) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) {
            return;
        }
        
        String sql = "UPDATE tbl_users SET password_hash = ? WHERE id_user = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String hashBaru = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
            ps.setString(1, hashBaru);
            ps.setInt(2, idUser);
            ps.executeUpdate();
            System.out.println("[SECURITY] Password berhasil dimigrasi ke BCrypt.");
        } catch (SQLException e) {
            System.err.println("[MIGRATION ERROR] " + e.getMessage());
        }
    }

    public void recordAuditLog(int idUser, String aksi, String rincian) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) {
            return;
        }
        
        String sql = 
            "INSERT INTO tbl_audit_logs " +
            "(id_user, aksi, rincian, created_at) " +
            "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ps.setString(2, aksi);
            ps.setString(3, rincian);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }

    private void safeAudit(int idUser, String aksi, String rincian) {
        try {
            recordAuditLog(idUser, aksi, rincian);
        } catch (Exception e) {
        }
    }
}
