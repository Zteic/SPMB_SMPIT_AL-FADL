package config;

import java.sql.*;

/**
 * Global Configuration State Manager - PPDB SMPIT AL FADL
 * Mengelola cache memori konfigurasi aktif secara realtime (Fixed Mapping).
 * @author Rivaldi
 */
public class AppConfig {
    private static String tahunAjaranAktif = "2026/2027"; // Fallback default ter-update

    /**
     * Memperbarui cache memori langsung dari records tabel MySQL ter-aktif
     */
    public static void refreshActivePeriod() {
        // 🎯 FIXED SINKRONISASI: Menggunakan 'status_aktif = 1' sesuai kolom asli tbl_tahun_ajaran
        String sql = "SELECT tahun_ajaran FROM tbl_tahun_ajaran WHERE status_aktif = 1 LIMIT 1";
        
        try (Connection conn = DatabaseConfig.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            if (rs.next()) {
                tahunAjaranAktif = rs.getString("tahun_ajaran");
            }
            
        } catch (SQLException e) {
            System.err.println("[CONFIG ERROR] Gagal memuat konfigurasi aktif: " + e.getMessage());
        }
    }

    public static String getTahunAjaranAktif() {
        return tahunAjaranAktif;
    }
}