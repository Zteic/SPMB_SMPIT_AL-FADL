package controllers;

import config.DatabaseConfig;
import models.Pendaftar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SiswaController {

    public boolean tambahPendaftar(Pendaftar p) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) {
            return false;
        }

        String sql = 
            "INSERT INTO tbl_siswa " +
            "(nomor_pendaftaran, nik, nama_lengkap, tempat_lahir, tanggal_lahir, " +
            "jenis_kelamin, agama, sekolah_asal, kelurahan, kecamatan, kabupaten, provinsi, " +
            "rt, rw, kode_pos, id_jalur, id_gelombang, id_tahun) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "        (SELECT id_gelombang FROM tbl_gelombang WHERE UPPER(status) IN ('AKTIF', 'BUKA') LIMIT 1), " +
            "        (SELECT id_tahun FROM tbl_tahun_ajaran WHERE status_aktif = 1 LIMIT 1))";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNomorDaftar());
            ps.setString(2, p.getNik());
            ps.setString(3, p.getNamaLengkap());
            ps.setString(4, p.getTempatLahir());
            ps.setString(5, p.getTanggalLahir());
            ps.setString(6, p.getJenisKelamin());
            ps.setString(7, p.getAgama());
            ps.setString(8, p.getAsalSekolah());
            ps.setString(9, p.getKelurahan());
            ps.setString(10, p.getKecamatan());
            ps.setString(11, p.getKabupaten());
            ps.setString(12, p.getProvinsi());
            ps.setString(13, p.getRt());
            ps.setString(14, p.getRw());
            ps.setString(15, p.getKodePos());
            ps.setInt(16, p.getIdJalur());

            int result = ps.executeUpdate();
            System.out.println("[SISWA] Insert sukses: " + result + " row");
            return result > 0;

        } catch (SQLException e) {
            System.err.println("[ERROR SISWA] Gagal insert pendaftar: " + e.getMessage());
            return false;
        }
    }

    public List<Pendaftar> getAllPendaftar() {
        List<Pendaftar> list = new ArrayList<>();
        String sql = "SELECT * FROM v_rekap_pendaftar_lengkap ORDER BY id_siswa DESC";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pendaftar p = new Pendaftar();
                p.setIdPendaftaran(rs.getInt("id_siswa"));
                p.setNomorDaftar(rs.getString("nomor_pendaftaran"));
                p.setNik(rs.getString("nik"));
                p.setNamaLengkap(rs.getString("nama_lengkap"));
                p.setStatusPendaftaran(rs.getString("status_pendaftaran"));
                p.setStatusBerkas(rs.getString("status_berkas"));
                p.setStatusSeleksi(rs.getString("status_kelulusan"));
                list.add(p);
            }

            System.out.println("[SISWA] Load data sukses: " + list.size());

        } catch (SQLException e) {
            System.err.println("[ERROR SISWA] Gagal load pendaftar: " + e.getMessage());
        }

        return list;
    }

    public boolean updateStatusSiswa(int idSiswa, String jenisStatus, String nilaiStatus) {
        String kolom;

        switch (jenisStatus) {
            case "status_berkas":
                kolom = "status_berkas";
                break;
            case "status_pendaftaran":
                kolom = "status_pendaftaran";
                break;
            case "status_kelulusan":
                kolom = "status_kelulusan";
                break;
            default:
                kolom = "status_pendaftaran";
        }

        String sql = "UPDATE tbl_siswa SET " + kolom + " = ? WHERE id_siswa = ?";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nilaiStatus);
            ps.setInt(2, idSiswa);

            int result = ps.executeUpdate();
            System.out.println("[SISWA] Update status sukses: " + result);
            return result > 0;

        } catch (SQLException e) {
            System.err.println("[ERROR SISWA] Update gagal: " + e.getMessage());
            return false;
        }
    }

    public boolean isNikTerdaftar(String nik) {
        String sql = "SELECT COUNT(*) FROM tbl_siswa WHERE nik = ?";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nik);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR SISWA] Validasi NIK gagal: " + e.getMessage());
        }

        return false;
    }
}
