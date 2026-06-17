package controllers;

import config.DatabaseConfig;
import dao.DashboardSiswaDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DashboardSiswaController {

    private final DashboardSiswaDAO dao = new DashboardSiswaDAO();

    public Map<String, Object> hitungProgressPipelineSiswa(int idSiswa) {
        Map<String, Object> hasil = new HashMap<>();
        int persentase = 10;
        int stepSelesai = 1;
        
        try (Connection conn = DatabaseConfig.getKoneksi()) {
            Map<String, String> statusMap = dao.getStatusCards(idSiswa, "");

            if ("Lengkap".equals(statusMap.get("form"))) {
                persentase += 15;
                stepSelesai++;
            }
            
            String statusBerkas = statusMap.get("berkas");
            if ("Lengkap".equalsIgnoreCase(statusBerkas) || "Menunggu Verifikasi".equalsIgnoreCase(statusBerkas)) {
                persentase += 15;
                stepSelesai++;
            }
            if ("Lengkap".equalsIgnoreCase(statusBerkas) || "DIVERIFIKASI".equalsIgnoreCase(statusBerkas)) {
                persentase += 15;
                stepSelesai++;
            }

            String statusBayar = statusMap.get("pembayaran");
            if ("LUNAS".equalsIgnoreCase(statusBayar)) {
                persentase += 15;
                stepSelesai++;
            } else if ("MENUNGGU_VERIFIKASI".equalsIgnoreCase(statusBayar)) {
                persentase += 10;
            }

            String statusLulus = statusMap.get("seleksi");
            if (!"PROSES".equalsIgnoreCase(statusLulus) && !"Belum Diproses".equalsIgnoreCase(statusLulus)) {
                persentase += 15;
                stepSelesai++;
            }

            if ("DITERIMA".equalsIgnoreCase(statusLulus) || "Lulus".equalsIgnoreCase(statusLulus)) {
                String du = statusMap.get("daftar_ulang");
                if ("SELESAI".equalsIgnoreCase(du)) {
                    persentase += 15;
                    stepSelesai++;
                }
            } else {
                if (!"Belum Diproses".equalsIgnoreCase(statusLulus)) {
                    persentase += 15;
                }
            }

        } catch (Exception e) {
            System.err.println("[CONTROLLER PIPELINE ERROR] : " + e.getMessage());
        }

        hasil.put("persentase", Math.min(100, persentase));
        hasil.put("stepSelesai", stepSelesai);
        return hasil;
    }

    public boolean kirimNotifikasiMemoAdmin(int idSiswa, String judul, String pesan) {
        String sql = "INSERT INTO tbl_notifikasi (id_user, judul, pesan, status, created_at) VALUES (?, ?, ?, 'UNREAD', NOW())";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSiswa);
            ps.setString(2, judul);
            ps.setString(3, pesan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CONTROLLER NOTIFIKASI ERROR] : " + e.getMessage());
            return false;
        }
    }

    public boolean updateFormulirSiswaKolektif(int idSiswa, String email, String hp, 
                                                String provinsi, String kabupaten, 
                                                String kecamatan, String kelurahan) {
        String sqlBio = "UPDATE tbl_biodata_siswa SET email = ?, nomor_hp = ? WHERE id_siswa = ?";
        String sqlAlamat = "UPDATE tbl_alamat SET provinsi = ?, kabupaten = ?, kecamatan = ?, kelurahan = ? WHERE id_siswa = ?";

        try (Connection conn = DatabaseConfig.getKoneksi()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement ps1 = conn.prepareStatement(sqlBio)) {
                ps1.setString(1, email);
                ps1.setString(2, hp);
                ps1.setInt(3, idSiswa);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(sqlAlamat)) {
                ps2.setString(1, provinsi);
                ps2.setString(2, kabupaten);
                ps2.setString(3, kecamatan);
                ps2.setString(4, kelurahan);
                ps2.setInt(5, idSiswa);
                ps2.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[CONTROLLER UPDATE FORMULIR CRASH] : " + e.getMessage());
            return false;
        }
    }

    public DashboardSiswaDAO getDao() {
        return dao;
    }
}
