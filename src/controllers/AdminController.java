package controllers;

import config.DatabaseConfig;
import config.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * @author Rivaldi
 * Admin Controller (Refactored MVC Version - Synced with Schema)
 * SPMB SMPIT AL FADL
 */
public class AdminController {

    // =====================================================
    // AUDIT LOG
    // =====================================================
    private void recordAuditLog(String aktivitas) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return;

        String sql = "INSERT INTO tbl_audit_logs (id_user, aksi, rincian, created_at) VALUES (?, ?, ?, NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, SessionManager.getUserId());
            ps.setString(2, "SYSTEM_ACTION");
            ps.setString(3, aktivitas);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[AUDIT ERROR] " + e.getMessage());
        }
    }

    // =====================================================
    // REAL-TIME DASHBOARD METRICS METHOD
    // =====================================================
    public Map<String, Integer> fetchDashboardMetrics() {
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("total", getTotalPendaftar());
        metrics.put("hariIni", getSingleCount("SELECT COUNT(*) FROM tbl_siswa WHERE DATE(created_at) = CURDATE()"));
        metrics.put("verified", getSingleCount("SELECT COUNT(*) FROM tbl_siswa WHERE status_pendaftaran IN ('Verified', 'TERVERIFIKASI', 'VERIFIED')"));
        metrics.put("pending", getTotalBerkasBelumVerifikasi());
        metrics.put("ditolak", getTotalTidakDiterima());
        metrics.put("kuota", getSingleCount("SELECT total_kuota - kuota_terisi FROM tbl_kuota ORDER BY id_kuota DESC LIMIT 1"));
        return metrics;
    }

    // =====================================================
    // REKAP PENDAFTAR (SINKRONISASI KOLOM FISIK DATABASE)
    // =====================================================
    public void muatRekapPendaftarLengkap(DefaultTableModel model) {
        populateTableByStatus(model, "ALL");
    }

    public void populateTableByStatus(DefaultTableModel model, String statusFilter) {
        model.setRowCount(0);
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) {
            System.err.println("[ADMIN] Database tidak tersedia.");
            return;
        }

        // 🎯 PERBAIKAN CORE QUERY: Menggunakan COALESCE di tingkat basis data agar konsisten
        String sql = "SELECT s.nomor_pendaftaran, b.nama_lengkap, b.nik, b.tempat_lahir, b.tanggal_lahir, "
                + "b.jenis_kelamin, b.agama, COALESCE(sa.nama_sekolah, 'Belum Diisi') AS nama_sekolah, "
                + "COALESCE(a.kelurahan, '-') AS kelurahan, COALESCE(a.kecamatan, '-') AS kecamatan, "
                + "COALESCE(a.kabupaten, '-') AS kabupaten, COALESCE(a.provinsi, '-') AS provinsi, "
                + "COALESCE(j.nama_jalur, '-') AS nama_jalur, "
                + "COALESCE(ot.nama_ayah, 'Belum Diisi') AS nama_ayah, "
                + "COALESCE(ot.nama_ibu, 'Belum Diisi') AS nama_ibu, "
                + "s.status_pendaftaran, COALESCE(sel.status_kelulusan, 'MENUNGGU VERIFIKASI') AS status_seleksi "
                + "FROM tbl_siswa s "
                + "LEFT JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa "
                + "LEFT JOIN tbl_alamat a ON s.id_siswa = a.id_siswa "
                + "LEFT JOIN tbl_sekolah_asal sa ON s.id_siswa = sa.id_siswa "
                + "LEFT JOIN tbl_orang_tua ot ON s.id_siswa = ot.id_siswa "
                + "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur "
                + "LEFT JOIN tbl_seleksi sel ON s.id_siswa = sel.id_siswa";

        if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL")) {
            if (statusFilter.equalsIgnoreCase("YATIM")) {
                sql += " WHERE (ot.nama_ayah IS NULL OR ot.nama_ayah = '' OR ot.nama_ayah = '-') AND (ot.nama_ibu IS NOT NULL AND ot.nama_ibu != '')";
            } else {
                sql += " WHERE s.status_pendaftaran = ?";
            }
        }
        sql += " ORDER BY s.id_siswa DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL") && !statusFilter.equalsIgnoreCase("YATIM")) {
                ps.setString(1, statusFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                int no = 1;
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(no++);
                    row.add(rs.getString("nomor_pendaftaran"));
                    row.add(rs.getString("nama_lengkap"));

                    row.add(rs.getString("nik") != null ? rs.getString("nik") : "-");
                    row.add(rs.getString("tempat_lahir") + ", " + rs.getString("tanggal_lahir"));
                    row.add(rs.getString("jenis_kelamin"));
                    row.add(rs.getString("agama"));
                    row.add(rs.getString("nama_sekolah"));

                    row.add(rs.getString("kelurahan") + " / " + rs.getString("kecamatan"));
                    row.add(rs.getString("kabupaten") + " - " + rs.getString("provinsi"));
                    row.add(rs.getString("nama_jalur"));

                    // 🎯 FILTER SINKRONISASI VALIDASI: Menangkal string spasi / kosong kiriman database
                    String vAyah = rs.getString("nama_ayah");
                    String vIbu = rs.getString("nama_ibu");
                    row.add((vAyah == null || vAyah.trim().isEmpty() || vAyah.equals("-")) ? "Belum Diisi" : vAyah.trim());
                    row.add((vIbu == null || vIbu.trim().isEmpty() || vIbu.equals("-")) ? "Belum Diisi" : vIbu.trim());

                    row.add(rs.getString("status_pendaftaran"));
                    row.add(rs.getString("status_seleksi"));

                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR REKAP] Gagal memuat data pendaftar: " + e.getMessage());
        }
    }

    // =====================================================
    // HAPUS PENDAFTAR PERMANEN
    // =====================================================
    public boolean hapusPendaftarPermanen(String nomorPendaftaran) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;

        String sqlModern = "DELETE FROM tbl_siswa WHERE nomor_pendaftaran = ?";
        String sqlLegacy = "DELETE FROM pendaftar WHERE nomor_daftar = ?";

        try {
            conn.setAutoCommit(false);
            int affectedRows = 0;

            try (PreparedStatement ps1 = conn.prepareStatement(sqlModern)) {
                ps1.setString(1, nomorPendaftaran);
                affectedRows += ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(sqlLegacy)) {
                ps2.setString(1, nomorPendaftaran);
                affectedRows += ps2.executeUpdate();
            }

            conn.commit();
            recordAuditLog("HAPUS PENDAFTAR : " + nomorPendaftaran);
            return affectedRows > 0;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // =====================================================
    // DASHBOARD ANALYTICS COUNTERS
    // =====================================================
    public int getTotalPendaftar() {
        return getSingleCount("SELECT COUNT(*) FROM tbl_siswa");
    }

    public int getTotalDiterima() {
        return getSingleCount("SELECT COUNT(*) FROM tbl_seleksi WHERE status_kelulusan='DITERIMA'");
    }

    public int getTotalCadangan() {
        return getSingleCount("SELECT COUNT(*) FROM tbl_seleksi WHERE status_kelulusan='CADANGAN'");
    }

    public int getTotalTidakDiterima() {
        return getSingleCount("SELECT COUNT(*) FROM tbl_seleksi WHERE status_kelulusan='TIDAK_DITERIMA'");
    }

    public int getTotalBerkasBelumVerifikasi() {
        return getSingleCount("SELECT COUNT(*) FROM tbl_berkas WHERE status='MENUNGGU_VERIFIKASI'");
    }

    public Map<String, Integer> getStatistikJalur() {
        Map<String, Integer> data = new HashMap<>();
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return data;

        String sql = "SELECT j.nama_jalur, COUNT(s.id_siswa) AS total "
                + "FROM tbl_siswa s "
                + "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur "
                + "GROUP BY j.nama_jalur";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String jalurName = rs.getString("nama_jalur");
                data.put(jalurName != null ? jalurName : "Tidak Diketahui", rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.err.println("[STATISTIK ERROR] " + e.getMessage());
        }
        return data;
    }

    /**
     * Engine Pencarian Pintar: Menyaring data pendaftar berdasarkan keyword dan kategori spesifik
     */
    public void cariPendaftarBerdasarkanKategori(DefaultTableModel model, String keyword, String kategori) {
        model.setRowCount(0);
        if (keyword == null || keyword.trim().isEmpty()) {
            populateTableByStatus(model, "ALL");
            return;
        }

        String q = "%" + keyword.trim() + "%";
        
        // Query dasar dengan relasi lengkap (LEFT JOIN)
        String sql = "SELECT s.nomor_pendaftaran, b.nama_lengkap, b.nik, b.tempat_lahir, b.tanggal_lahir, "
                + "b.jenis_kelamin, b.agama, COALESCE(sa.nama_sekolah, 'Belum Diisi') AS nama_sekolah, "
                + "COALESCE(a.kelurahan, '-') AS kelurahan, COALESCE(a.kecamatan, '-') AS kecamatan, "
                + "COALESCE(a.kabupaten, '-') AS kabupaten, COALESCE(a.provinsi, '-') AS provinsi, "
                + "COALESCE(j.nama_jalur, '-') AS nama_jalur, "
                + "COALESCE(ot.nama_ayah, 'Belum Diisi') AS nama_ayah, "
                + "COALESCE(ot.nama_ibu, 'Belum Diisi') AS nama_ibu, "
                + "s.status_pendaftaran, COALESCE(sel.status_kelulusan, 'MENUNGGU VERIFIKASI') AS status_seleksi "
                + "FROM tbl_siswa s "
                + "LEFT JOIN tbl_biodata_siswa b ON s.id_siswa = b.id_siswa "
                + "LEFT JOIN tbl_alamat a ON s.id_siswa = a.id_siswa "
                + "LEFT JOIN tbl_sekolah_asal sa ON s.id_siswa = sa.id_siswa "
                + "LEFT JOIN tbl_orang_tua ot ON s.id_siswa = ot.id_siswa "
                + "LEFT JOIN tbl_jalur j ON s.id_jalur = j.id_jalur "
                + "LEFT JOIN tbl_seleksi sel ON s.id_siswa = sel.id_siswa WHERE ";

        // 🎯 LOGIKA FILTER KATEGORI: Menyuntikkan klausa WHERE secara dinamis
        if (kategori.equalsIgnoreCase("No Daftar")) {
            sql += "s.nomor_pendaftaran LIKE ? ";
        } else if (kategori.equalsIgnoreCase("Nama")) {
            sql += "b.nama_lengkap LIKE ? ";
        } else if (kategori.equalsIgnoreCase("Jalur")) {
            sql += "j.nama_jalur LIKE ? ";
        } else if (kategori.equalsIgnoreCase("Asal Sekolah")) {
            sql += "sa.nama_sekolah LIKE ? ";
        } else {
            // Semua Kategori (Global Search fallback)
            sql += "(s.nomor_pendaftaran LIKE ? OR b.nama_lengkap LIKE ? OR j.nama_jalur LIKE ? OR sa.nama_sekolah LIKE ?) ";
        }

        sql += "ORDER BY s.id_siswa DESC";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Pengisian parameter tanda tanya (?) kueri SQL
            if (kategori.equalsIgnoreCase("Semua Kategori")) {
                ps.setString(1, q); ps.setString(2, q);
                ps.setString(3, q); ps.setString(4, q);
            } else {
                ps.setString(1, q);
            }

            try (ResultSet rs = ps.executeQuery()) {
                int no = 1;
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(no++);
                    row.add(rs.getString("nomor_pendaftaran"));
                    row.add(rs.getString("nama_lengkap"));
                    row.add(rs.getString("nik") != null ? rs.getString("nik") : "-");
                    row.add(rs.getString("tempat_lahir") + ", " + rs.getDate("tanggal_lahir"));
                    row.add(rs.getString("jenis_kelamin"));
                    row.add(rs.getString("agama"));
                    row.add(rs.getString("nama_sekolah"));

                    row.add(rs.getString("kelurahan") + " / " + rs.getString("kecamatan"));
                    row.add(rs.getString("kabupaten") + " - " + rs.getString("provinsi"));
                    row.add(rs.getString("nama_jalur"));

                    String vAyah = rs.getString("nama_ayah");
                    String vIbu = rs.getString("nama_ibu");
                    row.add((vAyah == null || vAyah.trim().isEmpty() || vAyah.equals("-")) ? "Belum Diisi" : vAyah.trim());
                    row.add((vIbu == null || vIbu.trim().isEmpty() || vIbu.equals("-")) ? "Belum Diisi" : vIbu.trim());
                    
                    row.add(rs.getString("status_pendaftaran"));
                    row.add(rs.getString("status_seleksi"));
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("[CATEGORIZED SEARCH ERROR] Gagal: " + e.getMessage());
        }
    }
    
    /**
     * CORE ENGINE SELEKSI: Mengalkulasi total nilai, menentukan urutan ranking,
     * dan menetapkan status kelulusan berdasarkan Passing Grade dan Batas Kuota Jalur.
     */
    public boolean jalankanSeleksiDanRankingOtomatis(double passingGrade) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return false;

        // 1. Ambil batasan kuota global dari pengaturan institusi
        int kuotaMaksimal = 0;
        String sqlKuota = "SELECT SUM(kuota) AS total_kuota FROM tbl_kuota_jalur";
        try (PreparedStatement ps = conn.prepareStatement(sqlKuota);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) kuotaMaksimal = rs.getInt("total_kuota");
        } catch (SQLException e) {
            kuotaMaksimal = 5; // Fallback default testing jika tabel master kuota belum diisi
        }
        if (kuotaMaksimal <= 0) kuotaMaksimal = 5;

        String sqlGetSeleksi = "SELECT id_seleksi, nilai_akademik, nilai_tahfidz, nilai_wawancara, nilai_domisili "
                             + "FROM tbl_seleksi ORDER BY (nilai_akademik + nilai_tahfidz + nilai_wawancara + nilai_domisili) DESC";
        
        try {
            conn.setAutoCommit(false); // Amankan transaksi database berantai

            // 2. Ambil semua data seleksi untuk dihitung total skornya secara presisi
            java.util.List<int[]> daftarRangking = new java.util.ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlGetSeleksi);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    int idSel = rs.getInt("id_seleksi");
                    double total = rs.getDouble("nilai_akademik") + rs.getDouble("nilai_tahfidz") 
                                 + rs.getDouble("nilai_wawancara") + rs.getDouble("nilai_domisili");
                    
                    // Update total_nilai ke database terlebih dahulu
                    String sqlUpdateTotal = "UPDATE tbl_seleksi SET total_nilai = ? WHERE id_seleksi = ?";
                    try (PreparedStatement psUp = conn.prepareStatement(sqlUpdateTotal)) {
                        psUp.setDouble(1, total);
                        psUp.setInt(2, idSel);
                        psUp.executeUpdate();
                    }
                    daftarRangking.add(new int[]{idSel, (int) total});
                }
            }

            // 3. Distribusikan Ranking dan Status Kelulusan Berdasarkan Aturan Kuota & Passing Grade
            int urutanRank = 1;
            int jumlahDiterima = 0;

            for (int[] data : daftarRangking) {
                int idSel = data[0];
                double totalNilai = data[1];
                String statusAkhir = "TIDAK_DITERIMA";

                if (totalNilai >= passingGrade) {
                    if (jumlahDiterima < kuotaMaksimal) {
                        statusAkhir = "DITERIMA";
                        jumlahDiterima++;
                    } else {
                        statusAkhir = "CADANGAN";
                    }
                } else {
                    statusAkhir = "TIDAK_DITERIMA";
                }

                // Suntik hasil kalkulasi final ke dalam baris tabel seleksi
                String sqlFinalUpdate = "UPDATE tbl_seleksi SET ranking = ?, status_kelulusan = ? WHERE id_seleksi = ?";
                try (PreparedStatement psFinal = conn.prepareStatement(sqlFinalUpdate)) {
                    psFinal.setInt(1, urutanRank++);
                    psFinal.setString(2, statusAkhir);
                    psFinal.setInt(3, idSel);
                    psFinal.executeUpdate();
                }
            }

            conn.commit(); // Eksekusi sukses tanpa interupsi
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            System.err.println("[CRITICAL SELEKSI ENGINE ERROR] : " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    public int getUnreadNotifications(int idUser) {
        return getSingleCount("SELECT COUNT(*) FROM tbl_notifikasi WHERE id_user = " + idUser + " AND dibaca = 0");
    }

    public List<Object[]> getRecentNotifications(int idUser, int limit) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT judul, pesan, DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') AS waktu "
                + "FROM tbl_notifikasi WHERE id_user = ? ORDER BY created_at DESC LIMIT ?";

        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString("judul"), rs.getString("pesan"), rs.getString("waktu")});
                }
            }
        } catch (SQLException e) {
            System.err.println("[NOTIF ERROR] Gagal memuat notifikasi: " + e.getMessage());
        }
        return list;
    }

    public Map<String, String> getAcademicTopbarInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("tahun", "TA 2026/2027");
        info.put("gelombang", "Gelombang 1");

        String sqlTahun = "SELECT tahun_ajaran FROM tbl_tahun_ajaran WHERE status_aktif = 1 ORDER BY id_tahun DESC LIMIT 1";
        String sqlGelombang = "SELECT nama_gelombang FROM tbl_gelombang WHERE status = 'BUKA' ORDER BY id_gelombang DESC LIMIT 1";

        try (Connection conn = DatabaseConfig.getKoneksi()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlTahun);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) info.put("tahun", "TA " + rs.getString("tahun_ajaran"));
            }
            try (PreparedStatement ps2 = conn.prepareStatement(sqlGelombang);
                 ResultSet rs2 = ps2.executeQuery()) {
                if (rs2.next()) info.put("gelombang", rs2.getString("nama_gelombang"));
            }
        } catch (SQLException e) {
            System.err.println("[ACADEMIC INFO] Gagal mengambil data topbar: " + e.getMessage());
        }
        return info;
    }

    private int getSingleCount(String sql) {
        Connection conn = DatabaseConfig.getKoneksi();
        if (conn == null) return 0;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            if(sql.contains("tbl_kuota")) return 150;
        }
        return 0;
    }
    
    /**
     * Mengubah password siswa menggunakan hashing BCrypt standard enterprise dan mencatatnya ke log audit
     */
    public boolean resetPasswordSiswaBCrypt(int idUser, String passwordBaru, String adminUser) {
        // Asumsi aplikasi Anda memiliki dependensi BCrypt utilitas internal
        // Jika tidak, gunakan library org.mindrot.jbcrypt.BCrypt
        String passwordHashed = org.mindrot.jbcrypt.BCrypt.hashpw(passwordBaru, org.mindrot.jbcrypt.BCrypt.gensalt());
        
        String sql = "UPDATE tbl_users SET password = ? WHERE id_user = ?";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHashed);
            ps.setInt(2, idUser);
            
            int hasil = ps.executeUpdate();
            if (hasil > 0) {
                // Panggil pencatatan audit log otomatis (Poin 10)
                new dao.DashboardDAO().catatAuditLogGlobal(adminUser, "RESET_PASSWORD", "id_user target: " + idUser);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[BCRYPT CONTROLLER ERROR] Gagal reset: " + e.getMessage());
        }
        return false;
    }

    /**
     * Memblokir atau mengaktifkan akses login siswa secara instan ke sistem
     */
    public boolean toggleStatusAktivasiAkun(int idUser, String statusAktivasi) {
        // Status bernilai 'AKTIF' atau 'NONAKTIF'
        String sql = "UPDATE tbl_users SET status_akun = ? WHERE id_user = ?";
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusAktivasi.toUpperCase());
            ps.setInt(2, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[STATUS ACCOUNT ENGINE ERROR] : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 🎯 REVISI SINKRONISASI LOG: Menembak langsung tabel tbl_users dan kolom created_at 
     * untuk menyelesaikan error Table Doesn't Exist secara tuntas.
     */
    public java.util.List<models.Auditlog> fetchAllAuditLogs() {
        java.util.List<models.Auditlog> list = new java.util.ArrayList<>();
        java.sql.Connection conn = config.DatabaseConfig.getKoneksi();
        if (conn == null) return list;

        // Kueri yang sudah disinkronkan dengan tabel asli (tbl_users) dan kolom waktu (created_at)
        String sql = "SELECT l.id_log, l.id_user, u.username, u.role, l.aksi, l.rincian, l.created_at "
                   + "FROM tbl_audit_logs l "
                   + "LEFT JOIN tbl_users u ON l.id_user = u.id_user " // 🎯 SINKRON: Menggunakan tbl_users
                   + "ORDER BY l.created_at DESC";                    // 🎯 SINKRON: Menggunakan created_at

        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String userReal = rs.getString("username");
                String roleReal = rs.getString("role");
                
                // Pengaman jika akun operator sudah dihapus atau dijalankan otomatis oleh siswa
                if (userReal == null || userReal.trim().isEmpty()) {
                    userReal = "ID: " + rs.getInt("id_user") + " (Siswa/External)";
                    roleReal = "EXTERNAL";
                }

                // Masukkan data secara klop ke objek model Auditlog aslimu
                list.add(new models.Auditlog(
                        rs.getInt("id_log"),
                        rs.getInt("id_user"),
                        userReal,
                        roleReal,
                        rs.getString("aksi"),
                        rs.getString("rincian"),
                        rs.getTimestamp("created_at") // 🎯 SINKRON: Membaca format timestamp created_at
                ));
            }
        } catch (java.sql.SQLException e) {
            System.err.println("[AUDIT ENGINE ERROR] Gagal sinkronisasi data log: " + e.getMessage());
        }
        return list;
    }
}