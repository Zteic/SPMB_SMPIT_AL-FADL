package dao;

import config.DatabaseConfig;
import models.Siswa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardSiswaDAO {

    // =========================================================================
    // FUNGSI BAWAAN GITHUB (DIPERTAHANKAN & DIREVISI LOGIC-NYA)
    // =========================================================================

    public Siswa findSiswaByUserId(int userId) throws Exception {
        String sql = "SELECT s.id_siswa, s.nomor_pendaftaran, j.nama_jalur, s.status_pendaftaran, u.nama_lengkap "
                   + "FROM tbl_siswa s "
                   + "JOIN tbl_users u ON s.nomor_pendaftaran = u.username "
                   + "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur "
                   + "WHERE u.id_user = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Siswa s = new Siswa();
                    s.setIdSiswa(rs.getInt("id_siswa"));
                    s.setIdUser(userId);
                    s.setNamaLengkap(rs.getString("nama_lengkap"));
                    s.setNomorPendaftaran(rs.getString("nomor_pendaftaran"));
                    s.setIdJalur(rs.getString("nama_jalur"));
                    s.setStatusPendaftaran(rs.getString("status_pendaftaran"));
                    return s;
                }
            }
        }
        return null;
    }

    public Map<String, String> getStatusCards(int idSiswa, String nomorPendaftaran) throws Exception {
        Map<String, String> status = new HashMap<>();

        // Form status
        String sqlForm = "SELECT nama_lengkap FROM tbl_biodata_siswa WHERE id_siswa = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sqlForm)) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString("nama_lengkap") != null && !rs.getString("nama_lengkap").trim().isEmpty()) {
                    status.put("form", "Lengkap");
                } else {
                    status.put("form", "Belum Diisi");
                }
            }
        }

        // Berkas status (Revisi untuk hitung berapa dari berapa)
        String sqlBerkas = "SELECT status FROM tbl_berkas WHERE id_siswa = ?";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sqlBerkas)) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                int total = 0, valid = 0, ditolak = 0, pending = 0;
                while (rs.next()) {
                    total++;
                    String sts = rs.getString("status");
                    if ("DIVERIFIKASI".equalsIgnoreCase(sts)) valid++;
                    else if ("DITOLAK".equalsIgnoreCase(sts) || "PERLU_REVISI".equalsIgnoreCase(sts)) ditolak++;
                    else pending++;
                }
                status.put("berkas_valid", String.valueOf(valid));
                status.put("berkas_total", "7"); // Asumsi realita 7 berkas
                
                if (total == 0) status.put("berkas", "Belum Upload");
                else if (ditolak > 0) status.put("berkas", "Ditolak");
                else if (pending > 0) status.put("berkas", "Menunggu Verifikasi");
                else status.put("berkas", "Lengkap");
            }
        }

        // Seleksi status
        String sqlSeleksi = "SELECT status_kelulusan FROM tbl_seleksi WHERE id_siswa = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sqlSeleksi)) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    status.put("seleksi", rs.getString("status_kelulusan"));
                } else {
                    status.put("seleksi", "Belum Diproses");
                }
            }
        }

        // Pembayaran status
        String sqlBayar = "SELECT status FROM tbl_pembayaran WHERE id_siswa = ? ORDER BY id_pembayaran DESC LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sqlBayar)) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    status.put("pembayaran", rs.getString("status"));
                } else {
                    status.put("pembayaran", "BELUM_BAYAR");
                }
            }
        }

        // Pengumuman status
        status.put("pengumuman", "Belum Ada");

        // Daftar Ulang status
        String sqlDaftarUlang = "SELECT status FROM tbl_daftar_ulang WHERE id_siswa = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sqlDaftarUlang)) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    status.put("daftar_ulang", rs.getString("status"));
                } else {
                    status.put("daftar_ulang", "BELUM_DAFTAR_ULANG");
                }
            }
        }

        return status;
    }

    public List<String[]> getPengumuman(int limit) throws Exception {
        List<String[]> out = new ArrayList<>();
        String sql = "SELECT judul, isi, tanggal_publish, status FROM tbl_pengumuman WHERE status = 'PUBLISHED' ORDER BY tanggal_publish DESC LIMIT ?";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new String[]{
                        rs.getString("judul"), rs.getString("isi"),
                        rs.getString("tanggal_publish"), rs.getString("status")
                    });
                }
            }
        }
        return out;
    }

    public String getTahunAjaranAktif() throws Exception {
        String sql = "SELECT tahun_ajaran FROM tbl_tahun_ajaran WHERE status_aktif = 1 LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("tahun_ajaran");
        } catch (Exception ex) {}
        return "-";
    }

    public String getFotoProfilSiswa(int idSiswa) {
        String sql = "SELECT lokasi_file, nama_file_server FROM tbl_berkas WHERE id_siswa = ? AND jenis_berkas = 'Pas Foto' AND status = 'DIVERIFIKASI' LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String folder = rs.getString("lokasi_file");
                    String file = rs.getString("nama_file_server");
                    if (folder != null && file != null) return folder + "/" + file;
                }
            }
        } catch (Exception e) {}
        return null;
    }

    public List<Map<String, String>> getNotifikasiSiswa(int idUser) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT id_notifikasi, judul, pesan, dibaca, created_at FROM tbl_notifikasi WHERE id_user = ? ORDER BY created_at DESC LIMIT 10";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("judul", rs.getString("judul"));
                    map.put("pesan", rs.getString("pesan"));
                    map.put("dibaca", rs.getInt("dibaca") == 1 ? "true" : "false");
                    map.put("tanggal", rs.getString("created_at"));
                    list.add(map);
                }
            }
        } catch (Exception e) {}
        return list;
    }

    public void markNotifikasiRead(int idUser) {
        String sql = "UPDATE tbl_notifikasi SET dibaca = 1 WHERE id_user = ?";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ps.executeUpdate();
        } catch (Exception e) {}
    }

    // =========================================================================
    // FUNGSI BARU UNTUK KEBUTUHAN REVISI DASHBOARD REALISTIS (15 POIN)
    // =========================================================================

    public List<String> getTodoListSiswa(int idSiswa) {
        List<String> todoList = new ArrayList<>();
        try {
            Map<String, String> status = getStatusCards(idSiswa, "");
            
            if ("Lengkap".equals(status.get("form"))) todoList.add("☑ Lengkapi Biodata");
            else todoList.add("☐ Lengkapi Biodata");

            if ("Lengkap".equals(status.get("berkas"))) todoList.add("☑ Upload Berkas Wajib");
            else todoList.add("☐ Upload Berkas Wajib");

            if ("LUNAS".equals(status.get("pembayaran"))) todoList.add("☑ Upload Bukti Pembayaran");
            else todoList.add("☐ Upload Bukti Pembayaran");
            
            if ("DITERIMA".equalsIgnoreCase(status.get("seleksi"))) {
                if ("SELESAI".equalsIgnoreCase(status.get("daftar_ulang"))) {
                    todoList.add("☑ Selesaikan Daftar Ulang");
                } else {
                    todoList.add("☐ Selesaikan Daftar Ulang");
                }
            } else {
                todoList.add("☐ Menunggu Pengumuman Kelulusan");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todoList;
    }

    public List<String[]> getRiwayatAktivitas(int idUser) {
        List<String[]> riwayat = new ArrayList<>();
        String sql = "SELECT aksi, rincian, DATE_FORMAT(created_at, '%d %M %Y') as tgl FROM tbl_audit_logs WHERE id_user = ? ORDER BY id_log DESC LIMIT 7";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    riwayat.add(new String[]{ rs.getString("tgl"), rs.getString("aksi"), rs.getString("rincian") });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return riwayat;
    }

    public Map<String, String> getDetailPembayaran(int idSiswa) {
        Map<String, String> detail = new HashMap<>();
        String sql = "SELECT nomor_invoice, nominal, tanggal_bayar, status FROM tbl_pembayaran WHERE id_siswa = ? ORDER BY id_pembayaran DESC LIMIT 1";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    detail.put("nomor_invoice", rs.getString("nomor_invoice"));
                    detail.put("nominal", rs.getString("nominal"));
                    detail.put("tanggal_bayar", rs.getString("tanggal_bayar"));
                    detail.put("status", rs.getString("status"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return detail;
    }

    public boolean isFormulirLocked(int idSiswa) {
        String sql = "SELECT is_locked FROM tbl_siswa WHERE id_siswa = ?";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("is_locked") == 1;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}