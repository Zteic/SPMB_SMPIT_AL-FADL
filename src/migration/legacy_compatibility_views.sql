-- =====================================================
-- LEGACY COMPATIBILITY VIEWS
-- Maps old table names (tbl_*) to new schema tables
-- Run this after creating the main schema
-- =====================================================

USE spmb_alfadl;

-- -----------------------------------------------------
-- View: tbl_users -> users
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_users AS
SELECT 
    id_user,
    username,
    email,
    password AS password_hash,
    role,
    email_verified_at,
    password_reset_token,
    failed_login_attempts,
    last_login_at,
    is_active,
    created_at,
    CONCAT('User ', id_user) AS nama_lengkap  -- synthetic field
FROM users;

-- -----------------------------------------------------
-- View: tbl_admin -> users (ADMIN role only)
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_admin AS
SELECT 
    id_user AS id_admin,
    username,
    nama_lengkap AS nama_lengkap,
    password_hash AS password,
    email,
    role,
    is_active,
    created_at
FROM tbl_users
WHERE role IN ('ADMIN', 'SUPER_ADMIN');

-- -----------------------------------------------------
-- View: tbl_jalur -> jalur_pendaftaran
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_jalur AS
SELECT 
    id_jalur,
    nama_jalur,
    kuota_jalur AS kuota_persen,
    is_active AS status,
    created_at
FROM jalur_pendaftaran;

-- -----------------------------------------------------
-- View: tbl_siswa -> pendaftar (core fields)
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_siswa AS
SELECT 
    id_pendaftaran AS id_siswa,
    nomor_daftar AS nomor_pendaftaran,
    id_gelombang,
    id_jalur,
    id_user,
    status_pendaftaran,
    status_berkas,
    status_tahfidz,
    status_wawancara,
    status_pembayaran,
    status_seleksi AS status_akun,
    nilai_total AS total_nilai,
    nilai_seleksi_internal AS nilai_akademik,
    nilai_tahfidz,
    nilai_wawancara,
    ranking_global AS ranking,
    tanggal_daftar AS created_at,
    tanggal_submit,
    catatan_admin
FROM pendaftar;

-- -----------------------------------------------------
-- View: tbl_biodata_siswa -> pendaftar (biodata fields)
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_biodata_siswa AS
SELECT 
    id_pendaftaran AS id_siswa,
    id_pendaftaran,  -- keep for compatibility
    nik,
    nama_lengkap,
    tempat_lahir,
    tanggal_lahir,
    jenis_kelamin,
    agama,
    kewarganegaraan,
    alamat_lengkap AS alamat,
    rt_rw,
    kelurahan,
    kecamatan,
    kabupaten,
    provinsi,
    kode_pos,
    no_telepon AS nomor_hp,
    email,
    null AS nisn,  -- not in new schema, synthetic
    no_kk AS nomor_kk,
    asal_sekolah AS sekolah_asal,
    nama_ayah,
    nama_ibu,
    pekerjaan_ayah,
    pekerjaan_ibu,
    no_telepon_ortu,
    jalur_pendaftaran.nama_jalur AS jalur_pendaftaran,  -- from joined table!
    nilai_rata_rata,
    prestasi_akademik,
    prestasi_non_akademik,
    created_at
FROM pendaftar
LEFT JOIN jalur_pendaftaran ON pendaftar.id_jalur = jalur_pendaftaran.id_jalur;

-- -----------------------------------------------------
-- View: tbl_alamat -> pendaftar (address fields)
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_alamat AS
SELECT 
    id_pendaftaran AS id_siswa,
    alamat_lengkap,
    kelurahan AS desa,
    kelurahan,
    kecamatan,
    kabupaten,
    provinsi,
    kode_pos
FROM pendaftar;

-- -----------------------------------------------------
-- View: tbl_sekolah_asal -> pendaftar (school fields)
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_sekolah_asal AS
SELECT 
    id_pendaftaran AS id_siswa,
    asal_sekolah AS nama_sekolah,
    alamat_sekolah,
    tahun_lulus
FROM pendaftar;

-- -----------------------------------------------------
-- View: tbl_orang_tua -> pendaftar (parent fields)
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_orang_tua AS
SELECT 
    id_pendaftaran AS id_siswa,
    nama_ayah,
    pekerjaan_ayah,
    nama_ibu,
    pekerjaan_ibu,
    penghasilan_ortu,
    no_telepon_ortu
FROM pendaftar;

-- -----------------------------------------------------
-- View: tbl_berkas -> dokumen_pendaftar
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_berkas AS
SELECT 
    id_dokumen AS id_berkas,
    id_pendaftaran AS id_siswa,
    CASE 
        WHEN jenis_dokumen = 'AKTA' THEN 'Akta Kelahiran'
        WHEN jenis_dokumen = 'KK' THEN 'Kartu Keluarga'
        WHEN jenis_dokumen = 'KTP_ORTU' THEN 'KTP Orang Tua'
        WHEN jenis_dokumen = 'FOTO' THEN 'Pas Foto'
        WHEN jenis_dokumen = 'IJAZAH' THEN 'Ijazah / SKL'
        WHEN jenis_dokumen = 'SPJM' THEN 'Surat Pernyataan'
        ELSE jenis_dokumen
    END AS jenis_berkas,
    path_file AS nama_file,
    nama_file AS nama_file_asli,
    nama_file AS nama_file_server,
    ukuran_file,
    mime_type,
    status_verifikasi AS status,
    verified_by AS id_verifikator,
    verified_at AS tanggal_verifikasi,
    catatan_verifikasi,
    created_at AS tanggal_upload
FROM dokumen_pendaftar;

-- -----------------------------------------------------
-- View: tbl_seleksi -> pendaftar (selection data)
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_seleksi AS
SELECT 
    id_pendaftaran AS id_seleksi,
    id_pendaftaran AS id_siswa,
    nilai_seleksi_internal AS nilai_akademik,
    nilai_tahfidz AS nilai_tahfidz,
    nilai_wawancara AS nilai_wawancara,
    0 AS nilai_domisili,
    nilai_total AS total_nilai,
    ranking_global AS ranking,
    status_seleksi AS status_kelulusan,
    created_at,
    updated_at
FROM pendaftar;

-- -----------------------------------------------------
-- View: tbl_gelombang -> gelombang
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_gelombang AS
SELECT 
    id_gelombang,
    nama_gelombang,
    tahun_ajaran,
    tanggal_buka AS tanggal_mulai,
    tanggal_tutup AS tanggal_selesai,
    biaya_pendaftaran,
    CASE WHEN is_active = TRUE THEN 'BUKA' ELSE 'TUTUP' END AS status
FROM gelombang;

-- -----------------------------------------------------
-- View: tbl_tahun_ajaran -> synthesized from gelombang
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_tahun_ajaran AS
SELECT DISTINCT
    1 AS id_tahun,
    tahun_ajaran,
    TRUE AS status_aktif
FROM gelombang;

-- -----------------------------------------------------
-- View: tbl_pembayaran -> pembayaran
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_pembayaran AS
SELECT 
    id_pembayaran,
    id_pendaftaran AS id_siswa,
    kode_pembayaran,
    jenis_pembayaran,
    jumlah_tagihan,
    jumlah_dibayar,
    metode_pembayaran,
    status_pembayaran AS status,
    tanggal_tagihan,
    tanggal_pembayaran,
    tanggal_jatuh_tempo,
    bukti_pembayaran,
    verified_by,
    verified_at
FROM pembayaran;

-- -----------------------------------------------------
-- View: tbl_daftar_ulang -> daftar_ulang
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_daftar_ulang AS
SELECT 
    id_daftar_ulang,
    id_pendaftaran AS id_siswa,
    tanggal_daftar_ulang,
    batas_daftar_ulang,
    status_daftar_ulang AS status,
    dokumen_seragam,
    dokumen_buku,
    dokumen_kesehatan,
    biaya_daftar_ulang,
    status_pembayaran,
    catatan,
    processed_by,
    processed_at
FROM daftar_ulang;

-- -----------------------------------------------------
-- View: tbl_notifikasi -> notifikasi
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_notifikasi AS
SELECT 
    id_notifikasi,
    id_pendaftaran AS id_user,
    tipe_notifikasi,
    kategori,
    judul,
    pesan,
    status_kirim AS dibaca,  -- mapped loosely
    created_at
FROM notifikasi;

-- -----------------------------------------------------
-- View: tbl_pengumuman -> sistem_config (mapped as announcements)
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_pengumuman AS
SELECT 
    1 AS id_pengumuman,
    config_key AS judul,
    config_value AS isi,
    'PUBLISHED' AS status,
    created_at AS tanggal_publish
FROM sistem_config
WHERE config_key LIKE 'pengumuman_%';

-- -----------------------------------------------------
-- View: tbl_audit_logs -> audit_logs
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_audit_logs AS
SELECT 
    id_log,
    id_user,
    aktivitas AS aksi,
    detail AS rincian,
    created_at
FROM audit_logs;

-- -----------------------------------------------------
-- View: tbl_kuota -> daya_tampung with jalur data
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_kuota AS
SELECT 
    dt.id_daya_tampung AS id_kuota,
    dt.id_gelombang,
    1 AS id_tahun,
    dt.id_gelombang AS id_jalur,
    g.nama_gelombang,
    dt.kuota_total,
    dt.kuota_terisi,
    dt.sisa_kuota,
    g.tahun_ajaran
FROM daya_tampung dt
LEFT JOIN gelombang g ON dt.id_gelombang = g.id_gelombang;

-- -----------------------------------------------------
-- View: tbl_daya_tampung -> daya_tampung
-- -----------------------------------------------------
CREATE OR REPLACE VIEW tbl_daya_tampung AS
SELECT 
    id_daya_tampung,
    id_gelombang,
    tahun_ajaran,
    kuota_total,
    kuota_terisi,
    sisa_kuota,
    status,
    created_at
FROM daya_tampung;