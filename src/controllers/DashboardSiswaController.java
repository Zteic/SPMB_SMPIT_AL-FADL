package controllers;

import config.DatabaseConfig;
import dao.DashboardSiswaDAO;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller Sinkronisasi Pipeline Data Dashboard Siswa
 * 100% Cocok dengan Skema Tabel Fisik Database spmb_alfadl
 */
public class DashboardSiswaController {

    private final DashboardSiswaDAO dao = new DashboardSiswaDAO();

    // =========================================================================
    // FUNGSI BAWAAN GITHUB (DIPERTAHANKAN & DIREVISI LOGIC-NYA)
    // =========================================================================

    /**
     * Poin 1, 7 & 13: Menghitung persentase progres pipeline individu siswa (0-100%)
     * Urutan: Registrasi -> Biodata -> Berkas -> Verifikasi -> Pembayaran -> Seleksi -> Daftar Ulang
     */
    public Map<String, Object> hitungProgressPipelineSiswa(int idSiswa) {
        Map<String, Object> hasil = new HashMap<>();
        int persentase = 10; // Default 10% karena sudah berhasil registrasi & login
        int stepSelesai = 1;
        
        try (Connection conn = DatabaseConfig.getKoneksi()) {
            Map<String, String> statusMap = dao.getStatusCards(idSiswa, "");

            // 2. Biodata
            if ("Lengkap".equals(statusMap.get("form"))) {
                persentase += 15; stepSelesai++;
            }
            
            // 3. Upload Berkas & 4. Verifikasi Berkas
            String statusBerkas = statusMap.get("berkas");
            if ("Lengkap".equalsIgnoreCase(statusBerkas) || "Menunggu Verifikasi".equalsIgnoreCase(statusBerkas)) {
                persentase += 15; stepSelesai++;
            }
            if ("Lengkap".equalsIgnoreCase(statusBerkas) || "DIVERIFIKASI".equalsIgnoreCase(statusBerkas)) {
                persentase += 15; stepSelesai++;
            }

            // 5. Pembayaran
            String statusBayar = statusMap.get("pembayaran");
            if ("LUNAS".equalsIgnoreCase(statusBayar)) {
                persentase += 15; stepSelesai++;
            } else if ("MENUNGGU_VERIFIKASI".equalsIgnoreCase(statusBayar)) {
                persentase += 10; // Partial
            }

            // 6. Seleksi
            String statusLulus = statusMap.get("seleksi");
            if (!"PROSES".equalsIgnoreCase(statusLulus) && !"Belum Diproses".equalsIgnoreCase(statusLulus)) {
                persentase += 15; stepSelesai++;
            }

            // 7. Daftar Ulang (Hanya jika lulus)
            if ("DITERIMA".equalsIgnoreCase(statusLulus) || "Lulus".equalsIgnoreCase(statusLulus)) {
                String du = statusMap.get("daftar_ulang");
                if ("SELESAI".equalsIgnoreCase(du)) {
                    persentase += 15; stepSelesai++;
                }
            } else {
                // Jika tidak lulus, persentase disesuaikan mentok 85% atau 100% tanpa daftar ulang
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

    /**
     * Poin 8: Mengirimkan pesan/memo revisi otomatis dari admin ke siswa
     */
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

    /**
     * Poin 14: Fitur Update Data Formulir menggunakan statement UPDATE mutlak
     */
    public boolean updateFormulirSiswaKolektif(int idSiswa, String email, String hp, String provinsi, String kabupaten, String kecamatan, String kelurahan) {
        // REVISI: Mengubah 'biodata_siswa' menjadi 'tbl_biodata_siswa' agar sesuai dengan skema MySQL asli
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

    // =========================================================================
    // FUNGSI WRAPPER BARU UNTUK UI DASHBOARD
    // =========================================================================

    public DashboardSiswaDAO getDao() {
        return dao;
    }
}