package controllers;

import config.DatabaseConfig;
import config.SessionManager;
import models.BiodataSiswa;

import java.sql.*;

public class BiodataSiswaController {

    public BiodataSiswa getByNomorPendaftaran(String nomorPendaftaran) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) {
            return null;
        }

        try {
            String getIdSql = "SELECT id_siswa FROM tbl_siswa WHERE nomor_pendaftaran = ?";
            int idSiswa = -1;
            try (PreparedStatement ps = conn.prepareStatement(getIdSql)) {
                ps.setString(1, nomorPendaftaran);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idSiswa = rs.getInt("id_siswa");
                    } else {
                        return null;
                    }
                }
            }

            BiodataSiswa siswa = new BiodataSiswa();
            siswa.setIdSiswa(idSiswa);
            siswa.setNomorPendaftaran(nomorPendaftaran);

            String biodataSql = "SELECT nik, nisn, nomor_kk, nama_lengkap, email, nomor_hp, tempat_lahir, tanggal_lahir, jenis_kelamin, agama FROM tbl_biodata_siswa WHERE id_siswa = ?";
            try (PreparedStatement ps = conn.prepareStatement(biodataSql)) {
                ps.setInt(1, idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        siswa.setNik(rs.getString("nik"));
                        siswa.setNisn(rs.getString("nisn"));
                        siswa.setNomorKk(rs.getString("nomor_kk"));
                        siswa.setNamaLengkap(rs.getString("nama_lengkap"));
                        siswa.setEmail(rs.getString("email"));
                        siswa.setNomorHp(rs.getString("nomor_hp"));
                        siswa.setTempatLahir(rs.getString("tempat_lahir"));
                        siswa.setTanggalLahir(rs.getDate("tanggal_lahir"));
                        siswa.setJenisKelamin(rs.getString("jenis_kelamin"));
                        siswa.setAgama(rs.getString("agama"));
                    }
                }
            }

            String sekolahSql = "SELECT nama_sekolah FROM tbl_sekolah_asal WHERE id_siswa = ?";
            try (PreparedStatement ps = conn.prepareStatement(sekolahSql)) {
                ps.setInt(1, idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        siswa.setSekolahAsal(rs.getString("nama_sekolah"));
                    }
                }
            }

            String alamatSql = "SELECT alamat_lengkap, kelurahan AS desa, kecamatan, kabupaten, provinsi FROM tbl_alamat WHERE id_siswa = ?";
            try (PreparedStatement ps = conn.prepareStatement(alamatSql)) {
                ps.setInt(1, idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        siswa.setAlamatLengkap(rs.getString("alamat_lengkap"));
                        siswa.setDesa(rs.getString("desa"));
                        siswa.setKecamatan(rs.getString("kecamatan"));
                        siswa.setKabupaten(rs.getString("kabupaten"));
                        siswa.setProvinsi(rs.getString("provinsi"));
                    }
                }
            }

            String orangTuaSql = "SELECT nama_ayah, pekerjaan_ayah, nama_ibu, pekerjaan_ibu FROM tbl_orang_tua WHERE id_siswa = ?";
            try (PreparedStatement ps = conn.prepareStatement(orangTuaSql)) {
                ps.setInt(1, idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        siswa.setNamaAyah(rs.getString("nama_ayah"));
                        siswa.setPekerjaanAyah(rs.getString("pekerjaan_ayah"));
                        siswa.setNamaIbu(rs.getString("nama_ibu"));
                        siswa.setPekerjaanIbu(rs.getString("pekerjaan_ibu"));
                    }
                }
            }

            return siswa;

        } catch (SQLException ex) {
            System.err.println("[BiodataSiswaController] Gagal mengambil biodata: " + ex.getMessage());
            return null;
        }
    }

    public boolean updateDataSiswa(BiodataSiswa siswa) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) {
            return false;
        }

        try {
            conn.setAutoCommit(false);

            String updateBiodata = "UPDATE tbl_biodata_siswa SET nik = ?, nisn = ?, nomor_kk = ?, nama_lengkap = ?, email = ?, nomor_hp = ?, tempat_lahir = ?, tanggal_lahir = ?, jenis_kelamin = ?, agama = ? WHERE id_siswa = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBiodata)) {
                ps.setString(1, siswa.getNik());
                ps.setString(2, siswa.getNisn());
                ps.setString(3, siswa.getNomorKk());
                ps.setString(4, siswa.getNamaLengkap());
                ps.setString(5, siswa.getEmail());
                ps.setString(6, siswa.getNomorHp());
                ps.setString(7, siswa.getTempatLahir());
                if (siswa.getTanggalLahir() != null) {
                    ps.setDate(8, new java.sql.Date(siswa.getTanggalLahir().getTime()));
                } else {
                    ps.setNull(8, Types.DATE);
                }
                ps.setString(9, siswa.getJenisKelamin());
                ps.setString(10, siswa.getAgama());
                ps.setInt(11, siswa.getIdSiswa());
                ps.executeUpdate();
            }

            upsertStringData(conn, "tbl_sekolah_asal", "SELECT COUNT(1) as cnt FROM tbl_sekolah_asal WHERE id_siswa = ?", "UPDATE tbl_sekolah_asal SET nama_sekolah = ? WHERE id_siswa = ?", "INSERT INTO tbl_sekolah_asal (id_siswa, nama_sekolah) VALUES (?, ?)", siswa.getIdSiswa(), siswa.getSekolahAsal());
            upsertAlamat(conn, siswa);
            upsertOrangTua(conn, siswa);

            conn.commit();

            if (SessionManager.isLoggedIn()) {
                AutentikasiController authController = new AutentikasiController();
                authController.recordAuditLog(SessionManager.getCurrentUser().getIdUser(),
                        "ACTION_UPDATE_BIODATA",
                        "Admin mengubah biodata siswa dengan nomor pendaftaran " + siswa.getNomorPendaftaran());
            }

            return true;
        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("[BiodataSiswaController] Rollback gagal: " + rollbackEx.getMessage());
            }
            System.err.println("[BiodataSiswaController] Gagal memperbarui biodata: " + ex.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("[BiodataSiswaController] Gagal mengembalikan auto commit: " + ex.getMessage());
            }
        }
    }

    private void upsertStringData(Connection conn, String tableName, String checkSql, String updateSql, String insertSql, int idSiswa, String value) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, idSiswa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("cnt") > 0) {
                    try (PreparedStatement up = conn.prepareStatement(updateSql)) {
                        up.setString(1, value);
                        up.setInt(2, idSiswa);
                        up.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                        ins.setInt(1, idSiswa);
                        ins.setString(2, value);
                        ins.executeUpdate();
                    }
                }
            }
        }
    }

    private void upsertAlamat(Connection conn, BiodataSiswa siswa) throws SQLException {
        String checkSql = "SELECT COUNT(1) as cnt FROM tbl_alamat WHERE id_siswa = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, siswa.getIdSiswa());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("cnt") > 0) {
                    try (PreparedStatement up = conn.prepareStatement("UPDATE tbl_alamat SET alamat_lengkap = ?, kelurahan = ?, kecamatan = ?, kabupaten = ?, provinsi = ? WHERE id_siswa = ?")) {
                        up.setString(1, siswa.getAlamatLengkap());
                        up.setString(2, siswa.getDesa());
                        up.setString(3, siswa.getKecamatan());
                        up.setString(4, siswa.getKabupaten());
                        up.setString(5, siswa.getProvinsi());
                        up.setInt(6, siswa.getIdSiswa());
                        up.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ins = conn.prepareStatement("INSERT INTO tbl_alamat (id_siswa, alamat_lengkap, kelurahan, kecamatan, kabupaten, provinsi) VALUES (?, ?, ?, ?, ?, ?)") ) {
                        ins.setInt(1, siswa.getIdSiswa());
                        ins.setString(2, siswa.getAlamatLengkap());
                        ins.setString(3, siswa.getDesa());
                        ins.setString(4, siswa.getKecamatan());
                        ins.setString(5, siswa.getKabupaten());
                        ins.setString(6, siswa.getProvinsi());
                        ins.executeUpdate();
                    }
                }
            }
        }
    }

    private void upsertOrangTua(Connection conn, BiodataSiswa siswa) throws SQLException {
        String checkSql = "SELECT COUNT(1) as cnt FROM tbl_orang_tua WHERE id_siswa = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, siswa.getIdSiswa());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("cnt") > 0) {
                    try (PreparedStatement up = conn.prepareStatement("UPDATE tbl_orang_tua SET nama_ayah = ?, pekerjaan_ayah = ?, nama_ibu = ?, pekerjaan_ibu = ? WHERE id_siswa = ?")) {
                        up.setString(1, siswa.getNamaAyah());
                        up.setString(2, siswa.getPekerjaanAyah());
                        up.setString(3, siswa.getNamaIbu());
                        up.setString(4, siswa.getPekerjaanIbu());
                        up.setInt(5, siswa.getIdSiswa());
                        up.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ins = conn.prepareStatement("INSERT INTO tbl_orang_tua (id_siswa, nama_ayah, pekerjaan_ayah, nama_ibu, pekerjaan_ibu) VALUES (?, ?, ?, ?, ?)") ) {
                        ins.setInt(1, siswa.getIdSiswa());
                        ins.setString(2, siswa.getNamaAyah());
                        ins.setString(3, siswa.getPekerjaanAyah());
                        ins.setString(4, siswa.getNamaIbu());
                        ins.setString(5, siswa.getPekerjaanIbu());
                        ins.executeUpdate();
                    }
                }
            }
        }
    }
}
