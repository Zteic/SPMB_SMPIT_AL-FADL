package controllers;

import config.DatabaseConfig;
import config.SessionManager;
import models.Gelombang;
import models.Jalur;
import models.Kuota;
import models.TahunAjaran;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * File: MasterDataController.java
 * Fungsi: Controller Pengelola Master Data PPDB (Tipe Data status_aktif Disinkronkan ke INT)
 */
public class MasterDataController {

    public List<TahunAjaran> fetchTahunAjaran() {
        List<TahunAjaran> list = new ArrayList<>();
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return list;

        String sql = "SELECT id_tahun, tahun_ajaran, status_aktif FROM tbl_tahun_ajaran ORDER BY id_tahun DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TahunAjaran(
                        rs.getInt("id_tahun"),
                        rs.getString("tahun_ajaran"),
                        rs.getInt("status_aktif")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal memuat tahun ajaran: " + e.getMessage());
        }
        return list;
    }

    public List<Gelombang> fetchGelombang() {
        List<Gelombang> list = new ArrayList<>();
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return list;

        String sql = "SELECT id_gelombang, nama_gelombang, tanggal_mulai, tanggal_selesai, biaya_pendaftaran, status FROM tbl_gelombang ORDER BY id_gelombang DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Gelombang(
                        rs.getInt("id_gelombang"),
                        rs.getString("nama_gelombang"),
                        rs.getString("tanggal_mulai"),
                        rs.getString("tanggal_selesai"),
                        rs.getDouble("biaya_pendaftaran"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal memuat gelombang: " + e.getMessage());
        }
        return list;
    }

    public List<Jalur> fetchJalur() {
        List<Jalur> list = new ArrayList<>();
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return list;

        String sql = "SELECT id_jalur, nama_jalur, kuota_persen, status FROM tbl_jalur ORDER BY id_jalur DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Jalur(
                        rs.getInt("id_jalur"),
                        rs.getString("nama_jalur"),
                        rs.getInt("kuota_persen"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal memuat jalur: " + e.getMessage());
        }
        return list;
    }

    public List<Kuota> fetchKuota() {
        List<Kuota> list = new ArrayList<>();
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return list;

        String sql = "SELECT k.id_kuota, k.id_tahun, k.id_jalur, k.total_kuota, k.kuota_terisi, k.sisa_kuota, t.tahun_ajaran, j.nama_jalur "
                + "FROM tbl_kuota k "
                + "LEFT JOIN tbl_tahun_ajaran t ON k.id_tahun = t.id_tahun "
                + "LEFT JOIN tbl_jalur j ON k.id_jalur = j.id_jalur "
                + "ORDER BY k.id_kuota DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Kuota kuota = new Kuota(
                        rs.getInt("id_kuota"),
                        rs.getInt("id_tahun"),
                        rs.getInt("id_jalur"),
                        rs.getInt("total_kuota"),
                        rs.getInt("kuota_terisi"),
                        rs.getInt("sisa_kuota")
                );
                kuota.setNamaTahunAjaran(rs.getString("tahun_ajaran"));
                kuota.setNamaJalur(rs.getString("nama_jalur"));
                list.add(kuota);
            }
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal memuat kuota: " + e.getMessage());
        }
        return list;
    }

    public boolean createTahunAjaran(TahunAjaran item) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "INSERT INTO tbl_tahun_ajaran (tahun_ajaran, status_aktif) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getTahunAjaran());
            ps.setInt(2, item.getStatusAktif());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal menambah tahun ajaran: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTahunAjaran(TahunAjaran item) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "UPDATE tbl_tahun_ajaran SET tahun_ajaran = ?, status_aktif = ? WHERE id_tahun = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getTahunAjaran());
            ps.setInt(2, item.getStatusAktif());
            ps.setInt(3, item.getIdTahun());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal memperbarui tahun ajaran: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTahunAjaran(int idTahun) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "DELETE FROM tbl_tahun_ajaran WHERE id_tahun = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTahun);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal menghapus tahun ajaran: " + e.getMessage());
            return false;
        }
    }

    public boolean createGelombang(Gelombang item) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "INSERT INTO tbl_gelombang (nama_gelombang, tanggal_mulai, tanggal_selesai, biaya_pendaftaran, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getNamaGelombang());
            ps.setString(2, item.getTanggalMulai());
            ps.setString(3, item.getTanggalSelesai());
            ps.setDouble(4, item.getBiayaPendaftaran());
            ps.setString(5, item.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal menambah gelombang: " + e.getMessage());
            return false;
        }
    }

    public boolean updateGelombang(Gelombang item) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "UPDATE tbl_gelombang SET nama_gelombang = ?, tanggal_mulai = ?, tanggal_selesai = ?, biaya_pendaftaran = ?, status = ? WHERE id_gelombang = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getNamaGelombang());
            ps.setString(2, item.getTanggalMulai());
            ps.setString(3, item.getTanggalSelesai());
            ps.setDouble(4, item.getBiayaPendaftaran());
            ps.setString(5, item.getStatus());
            ps.setInt(6, item.getIdGelombang());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal memperbarui gelombang: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteGelombang(int idGelombang) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "DELETE FROM tbl_gelombang WHERE id_gelombang = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGelombang);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal menghapus gelombang: " + e.getMessage());
            return false;
        }
    }

    public boolean createJalur(Jalur item) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "INSERT INTO tbl_jalur (nama_jalur, kuota_persen, status) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getNamaJalur());
            ps.setInt(2, item.getKuotaPersen());
            ps.setString(3, item.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal menambah jalur: " + e.getMessage());
            return false;
        }
    }

    public boolean updateJalur(Jalur item) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "UPDATE tbl_jalur SET nama_jalur = ?, kuota_persen = ?, status = ? WHERE id_jalur = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getNamaJalur());
            ps.setInt(2, item.getKuotaPersen());
            ps.setString(3, item.getStatus());
            ps.setInt(4, item.getIdJalur());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal memperbarui jalur: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteJalur(int idJalur) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "DELETE FROM tbl_jalur WHERE id_jalur = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJalur);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal menghapus jalur: " + e.getMessage());
            return false;
        }
    }

    public boolean createKuota(Kuota item) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "INSERT INTO tbl_kuota (id_tahun, id_jalur, total_kuota, kuota_terisi, sisa_kuota) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getIdTahun());
            ps.setInt(2, item.getIdJalur());
            ps.setInt(3, item.getTotalKuota());
            ps.setInt(4, item.getKuotaTerisi());
            ps.setInt(5, item.getSisaKuota());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal menambah kuota: " + e.getMessage());
            return false;
        }
    }

    public boolean updateKuota(Kuota item) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "UPDATE tbl_kuota SET id_tahun = ?, id_jalur = ?, total_kuota = ?, kuota_terisi = ?, sisa_kuota = ? WHERE id_kuota = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getIdTahun());
            ps.setInt(2, item.getIdJalur());
            ps.setInt(3, item.getTotalKuota());
            ps.setInt(4, item.getKuotaTerisi());
            ps.setInt(5, item.getSisaKuota());
            ps.setInt(6, item.getIdKuota());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal memperbarui kuota: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteKuota(int idKuota) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;
        String sql = "DELETE FROM tbl_kuota WHERE id_kuota = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKuota);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MASTERDATA] Gagal menghapus kuota: " + e.getMessage());
            return false;
        }
    }

    public int calculateSisa(int totalKuota, int kuotaTerisi) {
        int sisa = totalKuota - kuotaTerisi;
        return sisa < 0 ? 0 : sisa;
    }
}
