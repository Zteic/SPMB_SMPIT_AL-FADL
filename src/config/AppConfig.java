package config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AppConfig {

    private static String tahunAjaranAktif = "2026/2027";

    private AppConfig() {
    }

    public static void refreshActivePeriod() {
        String sql = 
            "SELECT tahun_ajaran " +
            "FROM tbl_tahun_ajaran " +
            "WHERE status_aktif = 1 " +
            "LIMIT 1";
        
        try (Connection conn = DatabaseConfig.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            if (rs.next()) {
                tahunAjaranAktif = rs.getString("tahun_ajaran");
            }
            
        } catch (SQLException e) {
            System.err.println(
                "[CONFIG ERROR] " + e.getMessage()
            );
        }
    }

    public static String getTahunAjaranAktif() {
        return tahunAjaranAktif;
    }
}
