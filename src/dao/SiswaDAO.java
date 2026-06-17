package dao;

import config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class SiswaDAO {

    public Vector<Vector<Object>> fetchSiswaByStatus(String statusFilter) {
        Vector<Vector<Object>> data = new Vector<>();
        String sql = 
            "SELECT s.id_siswa, s.nomor_pendaftaran, b.nama_lengkap, b.nik, " +
            "j.nama_jalur, s.status_pendaftaran, s.created_at " +
            "FROM tbl_siswa s " +
            "LEFT JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa " +
            "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur";
        
        if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL")) {
            sql += " WHERE s.status_pendaftaran = ?";
        }
        sql += " ORDER BY s.id_siswa DESC";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL")) {
                ps.setString(1, statusFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id_siswa"));
                    row.add(rs.getString("nomor_pendaftaran"));
                    row.add(rs.getString("nama_lengkap"));
                    
                    String rawNik = rs.getString("nik");
                    String maskedNik = (rawNik != null && rawNik.trim().length() >= 4) ? 
                        rawNik.trim().substring(0, 4) + "************" : "****************";
                    
                    row.add(maskedNik);
                    row.add(rs.getString("nama_jalur"));
                    row.add(rs.getString("status_pendaftaran"));
                    row.add(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at") : "-");
                    data.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public boolean hardDeleteSiswaCascadeTransactional(int idSiswa) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getKoneksi();
            conn.setAutoCommit(false);

            String[] queries = {
                "DELETE FROM tbl_seleksi WHERE id_siswa = ?",
                "DELETE FROM tbl_berkas WHERE id_siswa = ?",
                "DELETE FROM tbl_pembayaran WHERE id_siswa = ?",
                "DELETE FROM tbl_alamat WHERE id_siswa = ?",
                "DELETE FROM tbl_orang_tua WHERE id_siswa = ?",
                "DELETE FROM tbl_wali WHERE id_siswa = ?",
                "DELETE FROM tbl_biodata_siswa WHERE id_siswa = ?",
                "DELETE FROM tbl_siswa WHERE id_siswa = ?"
            };

            for (String sql : queries) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idSiswa);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("[TRANSACTION ROLLBACK] " + e.getMessage());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try { 
                    conn.close(); 
                } catch (SQLException ignored) {
                }
            }
        }
    }
}
