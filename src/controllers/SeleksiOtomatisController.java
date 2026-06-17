package controllers;

import config.DatabaseConfig;
import config.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class SeleksiOtomatisController {

    public boolean eksekusiSeleksiOtomatis(int passingGrade) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) {
            System.err.println("[SELEKSI ERROR] Koneksi database tidak tersedia.");
            return false;
        }

        String sqlFetch = 
            "SELECT id_seleksi, nilai_akademik, " +
            "((COALESCE(nilai_akademik, 0) + COALESCE(nilai_tahfidz, 0) " +
            " + COALESCE(nilai_wawancara, 0) + COALESCE(nilai_domisili, 0)) / 4) AS nilai_kalkulasi " +
            "FROM tbl_seleksi " +
            "ORDER BY nilai_kalkulasi DESC, id_seleksi ASC";
                        
        String sqlUpdate = "UPDATE tbl_seleksi SET ranking = ?, status_kelulusan = ? WHERE id_seleksi = ?";
        String sqlKuota = "SELECT COALESCE(SUM(total_kuota), 0) FROM tbl_kuota";

        try {
            conn.setAutoCommit(false);

            int totalKuota = 0;
            try (PreparedStatement psKuota = conn.prepareStatement(sqlKuota);
                 ResultSet rsKuota = psKuota.executeQuery()) {
                if (rsKuota.next()) {
                    totalKuota = rsKuota.getInt(1);
                }
            }
            if (totalKuota <= 0) {
                totalKuota = 5;
            }

            int seatCounter = 0;
            int ranking = 0;

            try (PreparedStatement psFetch = conn.prepareStatement(sqlFetch);
                 ResultSet rs = psFetch.executeQuery();
                 PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {

                while (rs.next()) {
                    ranking++;
                    double totalNilai = rs.getDouble("nilai_kalkulasi");
                    String status;

                    if (rs.getObject("nilai_akademik") == null) {
                        status = "PROSES";
                    } else if (totalNilai >= passingGrade) {
                        if (seatCounter < totalKuota) {
                            status = "DITERIMA";
                            seatCounter++;
                        } else {
                            status = "CADANGAN";
                        }
                    } else {
                        status = "TIDAK_DITERIMA";
                    }

                    psUpdate.setInt(1, ranking);
                    psUpdate.setString(2, status);
                    psUpdate.setInt(3, rs.getInt("id_seleksi"));
                    psUpdate.addBatch();
                }

                psUpdate.executeBatch();
            }

            conn.commit();
            recordAuditLog("Menjalankan engine seleksi otomatis dengan passing grade "
                    + passingGrade + ". Total kuota: " + totalKuota + ". Diterima: " + seatCounter + ".");
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("[SELEKSI ERROR] Gagal rollback: " + rollbackEx.getMessage());
            }
            System.err.println("[SELEKSI ERROR] Gagal eksekusi seleksi: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignore) {
            }
        }
    }

    public int getTotalKuota() {
        String sql = "SELECT COALESCE(SUM(total_kuota), 0) FROM tbl_kuota";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[KUOTA ERROR] Gagal ambil total kuota: " + e.getMessage());
        }
        return 0;
    }

    public int getSisaKuota() {
        String sql = "SELECT COALESCE(SUM(sisa_kuota), 0) FROM tbl_kuota";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[KUOTA ERROR] Gagal ambil sisa kuota: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalPendaftar() {
        String sql = "SELECT COUNT(*) FROM tbl_siswa";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[METRIC ERROR] Total pendaftar gagal: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalDiterima() {
        String sql = "SELECT COUNT(*) FROM tbl_seleksi WHERE status_kelulusan = 'DITERIMA'";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[METRIC ERROR] Total diterima gagal: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalCadangan() {
        String sql = "SELECT COUNT(*) FROM tbl_seleksi WHERE status_kelulusan = 'CADANGAN'";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[METRIC ERROR] Total cadangan gagal: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalProses() {
        String sql = "SELECT COUNT(*) FROM tbl_seleksi WHERE status_kelulusan = 'PROSES'";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[METRIC ERROR] Total proses gagal: " + e.getMessage());
        }
        return 0;
    }

    public void populateSeleksiTable(DefaultTableModel model, String statusFilter) {
        model.setRowCount(0);
        String sql = 
            "SELECT sel.id_seleksi, sel.id_siswa, COALESCE(b.nama_lengkap, '-') AS nama_lengkap, " +
            "sel.total_nilai, sel.ranking, sel.status_kelulusan " +
            "FROM tbl_seleksi sel " +
            "LEFT JOIN tbl_biodata_siswa b ON sel.id_siswa = b.id_siswa ";

        if (statusFilter != null && !statusFilter.equalsIgnoreCase("SEMUA")) {
            sql += "WHERE sel.status_kelulusan = ? ";
        }
        sql += "ORDER BY sel.ranking ASC, sel.total_nilai DESC";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (statusFilter != null && !statusFilter.equalsIgnoreCase("SEMUA")) {
                ps.setString(1, statusFilter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                int rowNumber = 1;
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rowNumber++);
                    row.add(rs.getInt("id_seleksi"));
                    row.add(rs.getInt("id_siswa"));
                    row.add(rs.getString("nama_lengkap"));
                    row.add(rs.getDouble("total_nilai"));
                    row.add(rs.getInt("ranking"));
                    row.add(rs.getString("status_kelulusan"));
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("[SELEKSI ERROR] Gagal memuat tabel seleksi: " + e.getMessage());
        }
    }

    public boolean updateStatusSeleksi(int idSeleksi, String newStatus) {
        String sql = "UPDATE tbl_seleksi SET status_kelulusan = ? WHERE id_seleksi = ?";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, idSeleksi);
            int updated = ps.executeUpdate();
            if (updated > 0) {
                recordAuditLog("Mengubah status seleksi id_seleksi=" + idSeleksi + " menjadi " + newStatus);
            }
            return updated > 0;
        } catch (SQLException e) {
            System.err.println("[SELEKSI ERROR] Gagal perbarui status seleksi: " + e.getMessage());
        }
        return false;
    }

    private void recordAuditLog(String aktivitas) {
        String sql = "INSERT INTO tbl_audit_logs (id_user, aksi, rincian, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, SessionManager.getUserId());
            ps.setString(2, "ENGINE_SELEKSI");
            ps.setString(3, aktivitas);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[AUDIT ERROR] Gagal simpan audit seleksi: " + e.getMessage());
        }
    }
}
