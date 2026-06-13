-- =====================================================
-- DATABASE SCHEMA SPMB SMPIT AL-FADL
-- =====================================================
-- Version: 1.0
-- Date: 6 Juni 2026
-- Database: MySQL 8.0.28
-- Schema: spmb_alfadl
--
-- Description: Complete database schema implementation
-- based on FINAL_DATABASE_DESIGN.md
-- =====================================================

-- =====================================================
-- 1. CREATE DATABASE
-- =====================================================

DROP DATABASE IF EXISTS spmb_alfadl;
CREATE DATABASE spmb_alfadl
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE spmb_alfadl;

-- =====================================================
-- 2. CREATE TABLES
-- =====================================================

-- -----------------------------------------------------
-- Table: users (Enhanced Authentication)
-- -----------------------------------------------------
CREATE TABLE users (
    id_user INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NULL,
    password VARCHAR(255) NOT NULL COMMENT 'BCrypt hashed password',
    role ENUM('SUPER_ADMIN', 'ADMIN', 'OPERATOR', 'CALON_SISWA') NOT NULL DEFAULT 'CALON_SISWA',
    email_verified_at TIMESTAMP NULL,
    password_reset_token VARCHAR(100) NULL,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    last_login_at TIMESTAMP NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_user)
) ENGINE=InnoDB COMMENT='User authentication and authorization';

-- -----------------------------------------------------
-- Table: gelombang (Multi-batch Registration)
-- -----------------------------------------------------
CREATE TABLE gelombang (
    id_gelombang INT NOT NULL AUTO_INCREMENT,
    nama_gelombang VARCHAR(50) NOT NULL COMMENT 'Gelombang 1, Gelombang 2, dst',
    tahun_ajaran VARCHAR(10) NOT NULL COMMENT 'Format: 2026-2027',
    tanggal_buka DATE NOT NULL,
    tanggal_tutup DATE NOT NULL,
    kuota_gelombang INT NOT NULL DEFAULT 0,
    biaya_pendaftaran DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    keterangan TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_gelombang)
) ENGINE=InnoDB COMMENT='Multi-batch registration management';

-- -----------------------------------------------------
-- Table: jalur_pendaftaran (Enhanced Path Master)
-- -----------------------------------------------------
CREATE TABLE jalur_pendaftaran (
    id_jalur INT NOT NULL AUTO_INCREMENT,
    nama_jalur VARCHAR(50) NOT NULL COMMENT 'Domisili, Prestasi, Afirmasi, Mutasi',
    deskripsi TEXT NULL,
    kuota_jalur INT NOT NULL DEFAULT 0,
    bobot_prioritas DECIMAL(3,2) NOT NULL DEFAULT 1.00 COMMENT 'Bobot untuk ranking (1.00-2.00)',
    persyaratan_umum TEXT NULL,
    persyaratan_khusus JSON NULL COMMENT 'Flexible requirements structure',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    urutan_prioritas INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_jalur)
) ENGINE=InnoDB COMMENT='Master jalur masuk siswa';

-- -----------------------------------------------------
-- Table: pendaftar (ENHANCED - Merged with biodata_siswa)
-- -----------------------------------------------------
CREATE TABLE pendaftar (
    -- PRIMARY KEY & REFERENCES
    id_pendaftaran INT NOT NULL AUTO_INCREMENT,
    nomor_daftar VARCHAR(20) NOT NULL COMMENT 'Auto-generated unique number',
    id_gelombang INT NOT NULL,
    id_jalur INT NOT NULL,
    id_user INT NULL COMMENT 'Optional link to user account',
    
    -- BIODATA DASAR
    nik VARCHAR(16) NOT NULL COMMENT 'NIK 16 digit',
    nama_lengkap VARCHAR(100) NOT NULL,
    tempat_lahir VARCHAR(50) NOT NULL,
    tanggal_lahir DATE NOT NULL,
    jenis_kelamin ENUM('L', 'P') NOT NULL,
    agama VARCHAR(20) NOT NULL,
    kewarganegaraan VARCHAR(20) NOT NULL DEFAULT 'Indonesia',
    
    -- ALAMAT
    alamat_lengkap TEXT NOT NULL,
    rt_rw VARCHAR(10) NULL,
    kelurahan VARCHAR(50) NOT NULL,
    kecamatan VARCHAR(50) NOT NULL,
    kabupaten VARCHAR(50) NOT NULL,
    provinsi VARCHAR(50) NOT NULL,
    kode_pos VARCHAR(10) NULL,
    
    -- KONTAK
    no_telepon VARCHAR(15) NULL,
    email VARCHAR(100) NULL,
    
    -- PENDIDIKAN
    asal_sekolah VARCHAR(100) NOT NULL,
    alamat_sekolah TEXT NULL,
    tahun_lulus YEAR NULL,
    nilai_rata_rata DECIMAL(5,2) NULL COMMENT 'Nilai rata-rata rapor',
    
    -- ORANG TUA
    nama_ayah VARCHAR(100) NOT NULL,
    nama_ibu VARCHAR(100) NOT NULL,
    pekerjaan_ayah VARCHAR(50) NULL,
    pekerjaan_ibu VARCHAR(50) NULL,
    penghasilan_ortu ENUM('<2jt', '2-5jt', '5-10jt', '10-20jt', '>20jt') NULL,
    no_telepon_ortu VARCHAR(15) NULL,
    
    -- KELUARGA
    no_kk VARCHAR(16) NULL COMMENT 'Nomor Kartu Keluarga',
    anak_ke INT NULL,
    jumlah_saudara INT NULL,
    
    -- KESEHATAN
    berat_badan INT NULL COMMENT 'Berat badan (kg)',
    tinggi_badan INT NULL COMMENT 'Tinggi badan (cm)',
    golongan_darah ENUM('A', 'B', 'AB', 'O') NULL,
    riwayat_penyakit TEXT NULL,
    
    -- PRESTASI & MINAT
    prestasi_akademik TEXT NULL,
    prestasi_non_akademik TEXT NULL,
    hobi VARCHAR(200) NULL,
    cita_cita VARCHAR(100) NULL,
    
    -- STATUS SISTEM
    status_pendaftaran ENUM('DRAFT', 'SUBMITTED', 'FINALIZED') NOT NULL DEFAULT 'DRAFT',
    status_berkas ENUM('BELUM_LENGKAP', 'PENDING', 'DIVERIFIKASI', 'DITOLAK') NOT NULL DEFAULT 'BELUM_LENGKAP',
    status_tahfidz ENUM('BELUM_TES', 'SCHEDULED', 'COMPLETED', 'PASSED', 'FAILED') NOT NULL DEFAULT 'BELUM_TES',
    status_wawancara ENUM('BELUM_JADWAL', 'SCHEDULED', 'COMPLETED', 'PASSED', 'FAILED') NOT NULL DEFAULT 'BELUM_JADWAL',
    status_pembayaran ENUM('BELUM_BAYAR', 'PENDING', 'VERIFIED', 'FAILED') NOT NULL DEFAULT 'BELUM_BAYAR',
    status_seleksi ENUM('BELUM_DIPROSES', 'DITERIMA', 'TIDAK_DITERIMA', 'CADANGAN') NOT NULL DEFAULT 'BELUM_DIPROSES',
    
    -- NILAI & RANKING
    nilai_seleksi_internal DECIMAL(5,2) NULL COMMENT 'Nilai seleksi internal (0-100)',
    nilai_tahfidz DECIMAL(5,2) NULL COMMENT 'Nilai tes tahfidz (0-100)',
    nilai_wawancara DECIMAL(5,2) NULL COMMENT 'Nilai wawancara (0-100)',
    nilai_total DECIMAL(5,2) NULL COMMENT 'Nilai total (calculated)',
    ranking_jalur INT NULL COMMENT 'Ranking dalam jalur',
    ranking_global INT NULL COMMENT 'Ranking global',
    
    -- TIMESTAMP & NOTES
    tanggal_daftar TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tanggal_submit TIMESTAMP NULL,
    tanggal_verifikasi TIMESTAMP NULL,
    catatan_admin TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (id_pendaftaran)
) ENGINE=InnoDB COMMENT='Enhanced pendaftar with merged biodata';

-- -----------------------------------------------------
-- Table: dokumen_pendaftar (Enhanced Document Management)
-- -----------------------------------------------------
CREATE TABLE dokumen_pendaftar (
    id_dokumen INT NOT NULL AUTO_INCREMENT,
    id_pendaftaran INT NOT NULL,
    jenis_dokumen ENUM('AKTA', 'KK', 'KTP_ORTU', 'FOTO', 'IJAZAH', 'SPJM') NOT NULL,
    nama_file VARCHAR(255) NOT NULL COMMENT 'Original filename',
    path_file VARCHAR(500) NOT NULL COMMENT 'File path in server',
    ukuran_file BIGINT NOT NULL DEFAULT 0 COMMENT 'File size in bytes',
    mime_type VARCHAR(100) NOT NULL COMMENT 'MIME type (image/jpeg, application/pdf)',
    checksum VARCHAR(64) NULL COMMENT 'SHA256 checksum for integrity',
    status_verifikasi ENUM('BELUM_UPLOAD', 'UPLOADED', 'PENDING', 'DIVERIFIKASI', 'DITOLAK') NOT NULL DEFAULT 'BELUM_UPLOAD',
    verified_by INT NULL COMMENT 'FK to users (admin verifikator)',
    verified_at TIMESTAMP NULL,
    catatan_verifikasi TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_dokumen)
) ENGINE=InnoDB COMMENT='Enhanced document management with file tracking';

-- -----------------------------------------------------
-- Table: pembayaran (Enhanced Payment System)
-- -----------------------------------------------------
CREATE TABLE pembayaran (
    id_pembayaran INT NOT NULL AUTO_INCREMENT,
    id_pendaftaran INT NOT NULL,
    kode_pembayaran VARCHAR(50) NOT NULL COMMENT 'Unique payment code',
    jenis_pembayaran ENUM('PENDAFTARAN', 'DAFTAR_ULANG', 'DENDA') NOT NULL DEFAULT 'PENDAFTARAN',
    jumlah_tagihan DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    jumlah_dibayar DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    metode_pembayaran ENUM('BANK_TRANSFER', 'E_WALLET', 'VA', 'CASH') NULL,
    status_pembayaran ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED', 'EXPIRED') NOT NULL DEFAULT 'PENDING',
    tanggal_tagihan TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tanggal_pembayaran TIMESTAMP NULL,
    tanggal_jatuh_tempo TIMESTAMP NULL,
    deskripsi TEXT NULL,
    bukti_pembayaran VARCHAR(255) NULL COMMENT 'Path to payment proof file',
    reference_id VARCHAR(100) NULL COMMENT 'Payment gateway reference ID',
    gateway_response JSON NULL COMMENT 'Payment gateway response',
    verified_by INT NULL COMMENT 'FK to users (verifikator)',
    verified_at TIMESTAMP NULL,
    catatan_verifikasi TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_pembayaran)
) ENGINE=InnoDB COMMENT='Enhanced payment system with gateway integration';

-- -----------------------------------------------------
-- Table: tahfidz_test (Quran Memorization Test)
-- -----------------------------------------------------
CREATE TABLE tahfidz_test (
    id_tahfidz_test INT NOT NULL AUTO_INCREMENT,
    id_pendaftaran INT NOT NULL,
    jadwal_test DATETIME NULL,
    juz_hafalan VARCHAR(50) NULL COMMENT 'Juz yang dihafalkan (1-30, Juz Amma)',
    surah_hafalan TEXT NULL COMMENT 'Daftar surah yang dihafalkan',
    nilai_kelancaran DECIMAL(5,2) NULL COMMENT 'Nilai kelancaran (0-100)',
    nilai_tajwid DECIMAL(5,2) NULL COMMENT 'Nilai tajwid (0-100)',
    nilai_fashohah DECIMAL(5,2) NULL COMMENT 'Nilai fashohah (0-100)',
    nilai_total DECIMAL(5,2) NULL COMMENT 'Nilai total tahfidz (calculated)',
    penguji VARCHAR(100) NULL,
    catatan_penguji TEXT NULL,
    status_test ENUM('SCHEDULED', 'COMPLETED', 'ABSENT') NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_tahfidz_test)
) ENGINE=InnoDB COMMENT='Quran memorization test management';

-- -----------------------------------------------------
-- Table: wawancara (Interview Management)
-- -----------------------------------------------------
CREATE TABLE wawancara (
    id_wawancara INT NOT NULL AUTO_INCREMENT,
    id_pendaftaran INT NOT NULL,
    jadwal_wawancara DATETIME NULL,
    pewawancara VARCHAR(100) NULL,
    tempat_wawancara VARCHAR(100) NULL,
    aspek_kepribadian DECIMAL(5,2) NULL COMMENT 'Nilai kepribadian (0-100)',
    aspek_komunikasi DECIMAL(5,2) NULL COMMENT 'Nilai komunikasi (0-100)',
    aspek_motivasi DECIMAL(5,2) NULL COMMENT 'Nilai motivasi (0-100)',
    aspek_pengetahuan DECIMAL(5,2) NULL COMMENT 'Nilai pengetahuan (0-100)',
    nilai_total DECIMAL(5,2) NULL COMMENT 'Nilai total wawancara (calculated)',
    catatan_pewawancara TEXT NULL,
    rekomendasi ENUM('SANGAT_DIREKOMENDASIKAN', 'DIREKOMENDASIKAN', 'KURANG_DIREKOMENDASIKAN', 'TIDAK_DIREKOMENDASIKAN') NULL,
    status_wawancara ENUM('SCHEDULED', 'COMPLETED', 'ABSENT', 'RESCHEDULE') NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_wawancara)
) ENGINE=InnoDB COMMENT='Interview scheduling and results management';

-- -----------------------------------------------------
-- Table: daftar_ulang (Re-registration Process)
-- -----------------------------------------------------
CREATE TABLE daftar_ulang (
    id_daftar_ulang INT NOT NULL AUTO_INCREMENT,
    id_pendaftaran INT NOT NULL,
    tanggal_daftar_ulang DATE NULL,
    batas_daftar_ulang DATE NOT NULL,
    status_daftar_ulang ENUM('BELUM_DAFTAR', 'PROSES', 'SELESAI', 'TERLAMBAT', 'BATAL') NOT NULL DEFAULT 'BELUM_DAFTAR',
    dokumen_seragam BOOLEAN NOT NULL DEFAULT FALSE,
    dokumen_buku BOOLEAN NOT NULL DEFAULT FALSE,
    dokumen_kesehatan BOOLEAN NOT NULL DEFAULT FALSE,
    biaya_daftar_ulang DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status_pembayaran ENUM('BELUM_BAYAR', 'LUNAS', 'CICILAN') NOT NULL DEFAULT 'BELUM_BAYAR',
    catatan TEXT NULL,
    processed_by INT NULL COMMENT 'FK to users (admin yang proses)',
    processed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_daftar_ulang)
) ENGINE=InnoDB COMMENT='Re-registration process for accepted students';

-- -----------------------------------------------------
-- Table: audit_logs (Enhanced Audit Trail)
-- -----------------------------------------------------
CREATE TABLE audit_logs (
    id_log INT NOT NULL AUTO_INCREMENT,
    id_user INT NOT NULL,
    module VARCHAR(50) NOT NULL COMMENT 'Module name (PENDAFTAR, PEMBAYARAN, etc)',
    aktivitas VARCHAR(100) NOT NULL COMMENT 'Action (CREATE, UPDATE, DELETE, LOGIN, etc)',
    detail TEXT NULL,
    old_values JSON NULL COMMENT 'Data before changes',
    new_values JSON NULL COMMENT 'Data after changes',
    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_log)
) ENGINE=InnoDB COMMENT='Enhanced audit trail with JSON fields';

-- -----------------------------------------------------
-- Table: notifikasi (Notification System)
-- -----------------------------------------------------
CREATE TABLE notifikasi (
    id_notifikasi INT NOT NULL AUTO_INCREMENT,
    id_pendaftaran INT NULL COMMENT 'Optional link to pendaftar',
    tipe_notifikasi ENUM('EMAIL', 'SMS', 'PUSH', 'SYSTEM') NOT NULL,
    kategori ENUM('PENDAFTARAN', 'PEMBAYARAN', 'SELEKSI', 'PENGUMUMAN', 'REMINDER') NOT NULL,
    judul VARCHAR(200) NOT NULL,
    pesan TEXT NOT NULL,
    email_tujuan VARCHAR(100) NULL,
    no_hp_tujuan VARCHAR(15) NULL,
    status_kirim ENUM('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'BOUNCED') NOT NULL DEFAULT 'PENDING',
    tanggal_kirim TIMESTAMP NULL,
    tanggal_dibaca TIMESTAMP NULL,
    provider VARCHAR(50) NULL COMMENT 'Provider pengiriman (SMTP, Twilio, etc)',
    response_id VARCHAR(100) NULL COMMENT 'Response ID from provider',
    error_message TEXT NULL,
    retry_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_notifikasi)
) ENGINE=InnoDB COMMENT='Notification system for email/SMS';

-- -----------------------------------------------------
-- Table: daya_tampung (Enhanced Capacity Management)
-- -----------------------------------------------------
CREATE TABLE daya_tampung (
    id_daya_tampung INT NOT NULL AUTO_INCREMENT,
    id_gelombang INT NOT NULL,
    tahun_ajaran VARCHAR(10) NOT NULL COMMENT 'Format: 2026-2027',
    kuota_total INT NOT NULL DEFAULT 0,
    kuota_terisi INT NOT NULL DEFAULT 0,
    sisa_kuota INT NOT NULL DEFAULT 0 COMMENT 'Calculated field',
    status ENUM('DRAFT', 'ACTIVE', 'CLOSED', 'ARCHIVED') NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_daya_tampung)
) ENGINE=InnoDB COMMENT='Enhanced capacity management per gelombang';

-- -----------------------------------------------------
-- Table: sistem_config (Dynamic Configuration)
-- -----------------------------------------------------
CREATE TABLE sistem_config (
    id_config INT NOT NULL AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL,
    config_value TEXT NOT NULL,
    config_type ENUM('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'JSON', 'DATE') NOT NULL DEFAULT 'STRING',
    deskripsi TEXT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_config)
) ENGINE=InnoDB COMMENT='Dynamic application configuration';

-- =====================================================
-- 3. UNIQUE KEYS
-- =====================================================

-- users table
ALTER TABLE users ADD UNIQUE KEY uk_users_username (username);
ALTER TABLE users ADD UNIQUE KEY uk_users_email (email);

-- gelombang table
ALTER TABLE gelombang ADD UNIQUE KEY uk_gelombang_nama_tahun (nama_gelombang, tahun_ajaran);

-- jalur_pendaftaran table  
ALTER TABLE jalur_pendaftaran ADD UNIQUE KEY uk_jalur_nama (nama_jalur);

-- pendaftar table
ALTER TABLE pendaftar ADD UNIQUE KEY uk_pendaftar_nomor (nomor_daftar);
ALTER TABLE pendaftar ADD UNIQUE KEY uk_pendaftar_nik (nik);

-- dokumen_pendaftar table
ALTER TABLE dokumen_pendaftar ADD UNIQUE KEY uk_dokumen_pendaftar_jenis (id_pendaftaran, jenis_dokumen);

-- pembayaran table
ALTER TABLE pembayaran ADD UNIQUE KEY uk_pembayaran_kode (kode_pembayaran);

-- tahfidz_test table  
ALTER TABLE tahfidz_test ADD UNIQUE KEY uk_tahfidz_pendaftar (id_pendaftaran);

-- wawancara table
ALTER TABLE wawancara ADD UNIQUE KEY uk_wawancara_pendaftar (id_pendaftaran);

-- daftar_ulang table
ALTER TABLE daftar_ulang ADD UNIQUE KEY uk_daftar_ulang_pendaftar (id_pendaftaran);

-- sistem_config table
ALTER TABLE sistem_config ADD UNIQUE KEY uk_config_key (config_key);

-- =====================================================
-- 4. FOREIGN KEYS
-- =====================================================

-- pendaftar foreign keys
ALTER TABLE pendaftar 
ADD CONSTRAINT fk_pendaftar_gelombang 
FOREIGN KEY (id_gelombang) REFERENCES gelombang (id_gelombang) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE pendaftar 
ADD CONSTRAINT fk_pendaftar_jalur 
FOREIGN KEY (id_jalur) REFERENCES jalur_pendaftaran (id_jalur) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE pendaftar 
ADD CONSTRAINT fk_pendaftar_user 
FOREIGN KEY (id_user) REFERENCES users (id_user) ON DELETE SET NULL ON UPDATE CASCADE;

-- dokumen_pendaftar foreign keys
ALTER TABLE dokumen_pendaftar 
ADD CONSTRAINT fk_dokumen_pendaftar 
FOREIGN KEY (id_pendaftaran) REFERENCES pendaftar (id_pendaftaran) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE dokumen_pendaftar 
ADD CONSTRAINT fk_dokumen_verified_by 
FOREIGN KEY (verified_by) REFERENCES users (id_user) ON DELETE SET NULL ON UPDATE CASCADE;

-- pembayaran foreign keys
ALTER TABLE pembayaran 
ADD CONSTRAINT fk_pembayaran_pendaftar 
FOREIGN KEY (id_pendaftaran) REFERENCES pendaftar (id_pendaftaran) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE pembayaran 
ADD CONSTRAINT fk_pembayaran_verified_by 
FOREIGN KEY (verified_by) REFERENCES users (id_user) ON DELETE SET NULL ON UPDATE CASCADE;

-- tahfidz_test foreign key
ALTER TABLE tahfidz_test 
ADD CONSTRAINT fk_tahfidz_pendaftar 
FOREIGN KEY (id_pendaftaran) REFERENCES pendaftar (id_pendaftaran) ON DELETE CASCADE ON UPDATE CASCADE;

-- wawancara foreign key
ALTER TABLE wawancara 
ADD CONSTRAINT fk_wawancara_pendaftar 
FOREIGN KEY (id_pendaftaran) REFERENCES pendaftar (id_pendaftaran) ON DELETE CASCADE ON UPDATE CASCADE;

-- daftar_ulang foreign keys
ALTER TABLE daftar_ulang 
ADD CONSTRAINT fk_daftar_ulang_pendaftar 
FOREIGN KEY (id_pendaftaran) REFERENCES pendaftar (id_pendaftaran) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE daftar_ulang 
ADD CONSTRAINT fk_daftar_ulang_processed_by 
FOREIGN KEY (processed_by) REFERENCES users (id_user) ON DELETE SET NULL ON UPDATE CASCADE;

-- audit_logs foreign key
ALTER TABLE audit_logs 
ADD CONSTRAINT fk_audit_user 
FOREIGN KEY (id_user) REFERENCES users (id_user) ON DELETE CASCADE ON UPDATE CASCADE;

-- notifikasi foreign key
ALTER TABLE notifikasi 
ADD CONSTRAINT fk_notifikasi_pendaftar 
FOREIGN KEY (id_pendaftaran) REFERENCES pendaftar (id_pendaftaran) ON DELETE SET NULL ON UPDATE CASCADE;

-- daya_tampung foreign key
ALTER TABLE daya_tampung 
ADD CONSTRAINT fk_daya_tampung_gelombang 
FOREIGN KEY (id_gelombang) REFERENCES gelombang (id_gelombang) ON DELETE CASCADE ON UPDATE CASCADE;

-- =====================================================
-- 5. PERFORMANCE INDEXES
-- =====================================================

-- Authentication & Security Indexes
CREATE INDEX idx_users_username_active ON users (username, is_active);
CREATE INDEX idx_users_role ON users (role);
CREATE INDEX idx_audit_user_time ON audit_logs (id_user, created_at DESC);
CREATE INDEX idx_audit_module_time ON audit_logs (module, created_at DESC);

-- Registration & Selection Indexes (CRITICAL)
CREATE INDEX idx_pendaftar_status_composite ON pendaftar (status_berkas, status_seleksi, status_pendaftaran);
CREATE INDEX idx_pendaftar_gelombang_jalur ON pendaftar (id_gelombang, id_jalur);
CREATE INDEX idx_pendaftar_ranking ON pendaftar (id_jalur, nilai_total DESC, nilai_seleksi_internal DESC);
CREATE INDEX idx_pendaftar_tanggal ON pendaftar (tanggal_daftar);
CREATE INDEX idx_pendaftar_nik_nama ON pendaftar (nik, nama_lengkap);

-- Composite ranking index (most important)
CREATE INDEX idx_pendaftar_ranking_composite ON pendaftar 
(id_gelombang, id_jalur, status_berkas, nilai_total DESC, nilai_seleksi_internal DESC);

-- Dashboard metrics composite
CREATE INDEX idx_pendaftar_dashboard_metrics ON pendaftar 
(id_gelombang, status_pendaftaran, status_berkas, status_seleksi, tanggal_daftar);

-- Document Management Indexes
CREATE INDEX idx_dokumen_pendaftar_status ON dokumen_pendaftar (id_pendaftaran, status_verifikasi);
CREATE INDEX idx_dokumen_jenis_status ON dokumen_pendaftar (jenis_dokumen, status_verifikasi);
CREATE INDEX idx_dokumen_verified ON dokumen_pendaftar (verified_by, verified_at);

-- Document workflow composite
CREATE INDEX idx_dokumen_workflow ON dokumen_pendaftar 
(jenis_dokumen, status_verifikasi, verified_by, created_at DESC);

-- Payment System Indexes
CREATE INDEX idx_pembayaran_status_date ON pembayaran (status_pembayaran, tanggal_pembayaran DESC);
CREATE INDEX idx_pembayaran_pendaftar ON pembayaran (id_pendaftaran, jenis_pembayaran);
CREATE INDEX idx_pembayaran_reference ON pembayaran (reference_id);

-- Payment verification composite
CREATE INDEX idx_pembayaran_verification ON pembayaran 
(status_pembayaran, verified_by, tanggal_pembayaran DESC);

-- Assessment System Indexes
CREATE INDEX idx_tahfidz_jadwal ON tahfidz_test (jadwal_test);
CREATE INDEX idx_tahfidz_nilai ON tahfidz_test (nilai_total DESC);
CREATE INDEX idx_wawancara_jadwal ON wawancara (jadwal_wawancara);
CREATE INDEX idx_wawancara_pewawancara ON wawancara (pewawancara, jadwal_wawancara);

-- Notification System Indexes
CREATE INDEX idx_notifikasi_status_created ON notifikasi (status_kirim, created_at DESC);
CREATE INDEX idx_notifikasi_pendaftar ON notifikasi (id_pendaftaran, kategori);
CREATE INDEX idx_notifikasi_tipe_tanggal ON notifikasi (tipe_notifikasi, tanggal_kirim);

-- Notification delivery composite
CREATE INDEX idx_notifikasi_delivery ON notifikasi 
(tipe_notifikasi, status_kirim, retry_count, created_at DESC);

-- Date-based indexes for reporting
CREATE INDEX idx_gelombang_tanggal ON gelombang (tanggal_buka, tanggal_tutup);
CREATE INDEX idx_jalur_active ON jalur_pendaftaran (is_active, urutan_prioritas);

-- =====================================================
-- 6. CHECK CONSTRAINTS
-- =====================================================

-- users constraints
ALTER TABLE users ADD CONSTRAINT chk_users_failed_attempts 
CHECK (failed_login_attempts >= 0 AND failed_login_attempts <= 10);

-- gelombang constraints
ALTER TABLE gelombang ADD CONSTRAINT chk_gelombang_tanggal 
CHECK (tanggal_tutup > tanggal_buka);

ALTER TABLE gelombang ADD CONSTRAINT chk_gelombang_kuota 
CHECK (kuota_gelombang > 0);

-- jalur_pendaftaran constraints
ALTER TABLE jalur_pendaftaran ADD CONSTRAINT chk_jalur_kuota 
CHECK (kuota_jalur > 0);

ALTER TABLE jalur_pendaftaran ADD CONSTRAINT chk_jalur_bobot 
CHECK (bobot_prioritas >= 1.0 AND bobot_prioritas <= 2.0);

-- pendaftar constraints
ALTER TABLE pendaftar ADD CONSTRAINT chk_pendaftar_nik 
CHECK (LENGTH(nik) = 16 AND nik REGEXP '^[0-9]+$');

ALTER TABLE pendaftar ADD CONSTRAINT chk_pendaftar_tanggal_lahir 
CHECK (tanggal_lahir < CURDATE());

ALTER TABLE pendaftar ADD CONSTRAINT chk_pendaftar_nilai 
CHECK (nilai_seleksi_internal IS NULL OR (nilai_seleksi_internal BETWEEN 0 AND 100));

ALTER TABLE pendaftar ADD CONSTRAINT chk_pendaftar_anak_ke 
CHECK (anak_ke IS NULL OR (anak_ke > 0 AND jumlah_saudara >= 0));

ALTER TABLE pendaftar ADD CONSTRAINT chk_pendaftar_fisik 
CHECK (berat_badan IS NULL OR (berat_badan > 0 AND tinggi_badan > 0));

-- dokumen_pendaftar constraints
ALTER TABLE dokumen_pendaftar ADD CONSTRAINT chk_dokumen_ukuran 
CHECK (ukuran_file > 0 AND ukuran_file <= 10485760);

-- pembayaran constraints
ALTER TABLE pembayaran ADD CONSTRAINT chk_pembayaran_jumlah 
CHECK (jumlah_tagihan >= 0 AND jumlah_dibayar >= 0);

ALTER TABLE pembayaran ADD CONSTRAINT chk_pembayaran_lebih_bayar 
CHECK (jumlah_dibayar <= jumlah_tagihan * 1.1);

-- tahfidz_test constraints
ALTER TABLE tahfidz_test ADD CONSTRAINT chk_tahfidz_nilai 
CHECK (nilai_kelancaran IS NULL OR (nilai_kelancaran BETWEEN 0 AND 100 AND nilai_tajwid BETWEEN 0 AND 100));

-- wawancara constraints
ALTER TABLE wawancara ADD CONSTRAINT chk_wawancara_nilai 
CHECK (aspek_kepribadian IS NULL OR aspek_kepribadian BETWEEN 0 AND 100);

-- daya_tampung constraints
ALTER TABLE daya_tampung ADD CONSTRAINT chk_daya_tampung_kuota 
CHECK (sisa_kuota <= kuota_total AND kuota_terisi <= kuota_total);

-- =====================================================
-- 7. TRIGGERS
-- =====================================================

-- Trigger 1: Auto-calculate nilai total pendaftar
DELIMITER $$
CREATE TRIGGER trg_pendaftar_calculate_nilai_total
BEFORE UPDATE ON pendaftar
FOR EACH ROW
BEGIN
    -- Weighted calculation: 40% seleksi internal + 30% tahfidz + 30% wawancara
    IF (NEW.nilai_seleksi_internal IS NOT NULL OR 
        NEW.nilai_tahfidz IS NOT NULL OR 
        NEW.nilai_wawancara IS NOT NULL) THEN
        SET NEW.nilai_total = (
            COALESCE(NEW.nilai_seleksi_internal, 0) * 0.4 +
            COALESCE(NEW.nilai_tahfidz, 0) * 0.3 + 
            COALESCE(NEW.nilai_wawancara, 0) * 0.3
        );
    END IF;
END$$
DELIMITER ;

-- Trigger 2: Update sisa kuota
DELIMITER $$
CREATE TRIGGER trg_pendaftar_update_kuota
AFTER UPDATE ON pendaftar
FOR EACH ROW
BEGIN
    IF OLD.status_seleksi != NEW.status_seleksi THEN
        UPDATE daya_tampung dt
        SET 
            dt.kuota_terisi = (
                SELECT COUNT(*) 
                FROM pendaftar p
                WHERE p.id_gelombang = NEW.id_gelombang 
                AND p.status_seleksi = 'DITERIMA'
            ),
            dt.updated_at = NOW()
        WHERE dt.id_gelombang = NEW.id_gelombang;
        
        UPDATE daya_tampung 
        SET sisa_kuota = kuota_total - kuota_terisi 
        WHERE id_gelombang = NEW.id_gelombang;
    END IF;
END$$
DELIMITER ;

-- Trigger 3: Generate nomor daftar
DELIMITER $$
CREATE TRIGGER trg_pendaftar_generate_nomor
BEFORE INSERT ON pendaftar
FOR EACH ROW
BEGIN
    DECLARE tahun VARCHAR(4);
    DECLARE gelombang_num VARCHAR(2);  
    DECLARE sequence_num INT;
    
    -- Get tahun from gelombang
    SELECT SUBSTRING(g.tahun_ajaran, 1, 4), LPAD(g.id_gelombang, 2, '0')
    INTO tahun, gelombang_num
    FROM gelombang g 
    WHERE g.id_gelombang = NEW.id_gelombang;
    
    -- Get next sequence number
    SELECT COALESCE(MAX(CAST(SUBSTRING(nomor_daftar, -4) AS UNSIGNED)), 0) + 1
    INTO sequence_num
    FROM pendaftar 
    WHERE id_gelombang = NEW.id_gelombang;
    
    -- Generate: PMB + tahun + gelombang + sequence (PMB2026010001)
    SET NEW.nomor_daftar = CONCAT('PMB', tahun, gelombang_num, LPAD(sequence_num, 4, '0'));
END$$
DELIMITER ;

-- Trigger 4: Auto-create notifications
DELIMITER $$
CREATE TRIGGER trg_pendaftar_notification
AFTER UPDATE ON pendaftar
FOR EACH ROW  
BEGIN
    -- Status berkas diverifikasi -> send notification
    IF OLD.status_berkas != NEW.status_berkas AND NEW.status_berkas = 'DIVERIFIKASI' THEN
        INSERT INTO notifikasi (
            id_pendaftaran, tipe_notifikasi, kategori, judul, pesan, email_tujuan, created_at
        ) VALUES (
            NEW.id_pendaftaran,
            'EMAIL',
            'PENDAFTARAN', 
            'Dokumen Berkas Telah Diverifikasi',
            CONCAT('Dokumen berkas Anda untuk nomor pendaftaran ', NEW.nomor_daftar, ' telah berhasil diverifikasi.'),
            NEW.email,
            NOW()
        );
    END IF;
    
    -- Status seleksi berubah -> send notification  
    IF OLD.status_seleksi != NEW.status_seleksi AND NEW.status_seleksi IN ('DITERIMA', 'TIDAK_DITERIMA') THEN
        INSERT INTO notifikasi (
            id_pendaftaran, tipe_notifikasi, kategori, judul, pesan, email_tujuan, created_at
        ) VALUES (
            NEW.id_pendaftaran,
            'EMAIL', 
            'PENGUMUMAN',
            'Pengumuman Hasil Seleksi',
            CONCAT('Hasil seleksi Anda: ', NEW.status_seleksi, '. Silakan cek dashboard untuk detail lengkap.'),
            NEW.email,
            NOW()
        );
    END IF;
END$$
DELIMITER ;

-- Trigger 5: Calculate tahfidz nilai total
DELIMITER $$
CREATE TRIGGER trg_tahfidz_calculate_total
BEFORE UPDATE ON tahfidz_test
FOR EACH ROW
BEGIN
    IF (NEW.nilai_kelancaran IS NOT NULL AND 
        NEW.nilai_tajwid IS NOT NULL AND 
        NEW.nilai_fashohah IS NOT NULL) THEN
        SET NEW.nilai_total = (NEW.nilai_kelancaran + NEW.nilai_tajwid + NEW.nilai_fashohah) / 3;
        
        -- Update nilai_tahfidz di pendaftar
        UPDATE pendaftar 
        SET nilai_tahfidz = NEW.nilai_total 
        WHERE id_pendaftaran = NEW.id_pendaftaran;
    END IF;
END$$
DELIMITER ;

-- Trigger 6: Calculate wawancara nilai total
DELIMITER $$
CREATE TRIGGER trg_wawancara_calculate_total
BEFORE UPDATE ON wawancara
FOR EACH ROW
BEGIN
    IF (NEW.aspek_kepribadian IS NOT NULL AND 
        NEW.aspek_komunikasi IS NOT NULL AND 
        NEW.aspek_motivasi IS NOT NULL AND 
        NEW.aspek_pengetahuan IS NOT NULL) THEN
        SET NEW.nilai_total = (NEW.aspek_kepribadian + NEW.aspek_komunikasi + NEW.aspek_motivasi + NEW.aspek_pengetahuan) / 4;
        
        -- Update nilai_wawancara di pendaftar
        UPDATE pendaftar 
        SET nilai_wawancara = NEW.nilai_total 
        WHERE id_pendaftaran = NEW.id_pendaftaran;
    END IF;
END$$
DELIMITER ;

-- =====================================================
-- 8. VIEWS
-- =====================================================

-- View 1: Dashboard Metrics
CREATE VIEW vw_dashboard_metrics AS
SELECT 
    g.id_gelombang,
    g.nama_gelombang,
    g.tahun_ajaran,
    COUNT(p.id_pendaftaran) as total_pendaftar,
    SUM(CASE WHEN p.status_pendaftaran = 'SUBMITTED' THEN 1 ELSE 0 END) as pendaftar_submitted,
    SUM(CASE WHEN p.status_berkas = 'DIVERIFIKASI' THEN 1 ELSE 0 END) as berkas_verified,
    SUM(CASE WHEN p.status_seleksi = 'DITERIMA' THEN 1 ELSE 0 END) as diterima,
    SUM(CASE WHEN p.status_seleksi = 'CADANGAN' THEN 1 ELSE 0 END) as cadangan,
    SUM(CASE WHEN p.status_pembayaran = 'VERIFIED' THEN 1 ELSE 0 END) as payment_verified,
    dt.kuota_total,
    dt.sisa_kuota,
    ROUND((SUM(CASE WHEN p.status_seleksi = 'DITERIMA' THEN 1 ELSE 0 END) / dt.kuota_total * 100), 2) as persentase_terisi
FROM gelombang g
LEFT JOIN pendaftar p ON g.id_gelombang = p.id_gelombang
LEFT JOIN daya_tampung dt ON g.id_gelombang = dt.id_gelombang
WHERE g.is_active = TRUE
GROUP BY g.id_gelombang, g.nama_gelombang, g.tahun_ajaran, dt.kuota_total, dt.sisa_kuota;

-- View 2: Pendaftar Complete Info
CREATE VIEW vw_pendaftar_complete AS
SELECT 
    p.id_pendaftaran,
    p.nomor_daftar,
    p.nik,
    p.nama_lengkap,
    p.tempat_lahir,
    p.tanggal_lahir,
    p.jenis_kelamin,
    p.agama,
    p.asal_sekolah,
    CONCAT(p.kelurahan, ', ', p.kecamatan, ', ', p.kabupaten, ', ', p.provinsi) as alamat_lengkap,
    p.no_telepon,
    p.email,
    g.nama_gelombang,
    g.tahun_ajaran,
    jp.nama_jalur,
    p.status_pendaftaran,
    p.status_berkas,
    p.status_tahfidz,
    p.status_wawancara,
    p.status_pembayaran,
    p.status_seleksi,
    p.nilai_seleksi_internal,
    p.nilai_tahfidz,
    p.nilai_wawancara,
    p.nilai_total,
    p.ranking_jalur,
    p.ranking_global,
    -- Document summary
    (SELECT COUNT(*) FROM dokumen_pendaftar dp WHERE dp.id_pendaftaran = p.id_pendaftaran AND dp.status_verifikasi = 'DIVERIFIKASI') as dokumen_verified,
    (SELECT COUNT(*) FROM dokumen_pendaftar dp WHERE dp.id_pendaftaran = p.id_pendaftaran AND dp.status_verifikasi = 'DITOLAK') as dokumen_rejected,
    -- Payment summary  
    (SELECT SUM(jumlah_tagihan) FROM pembayaran pb WHERE pb.id_pendaftaran = p.id_pendaftaran) as total_tagihan,
    (SELECT SUM(jumlah_dibayar) FROM pembayaran pb WHERE pb.id_pendaftaran = p.id_pendaftaran) as total_dibayar,
    p.tanggal_daftar,
    p.tanggal_submit,
    p.tanggal_verifikasi
FROM pendaftar p
INNER JOIN gelombang g ON p.id_gelombang = g.id_gelombang
INNER JOIN jalur_pendaftaran jp ON p.id_jalur = jp.id_jalur;

-- View 3: Document Status Summary
CREATE VIEW vw_document_status AS
SELECT 
    p.id_pendaftaran,
    p.nomor_daftar,
    p.nama_lengkap,
    SUM(CASE WHEN dp.jenis_dokumen = 'AKTA' AND dp.status_verifikasi = 'DIVERIFIKASI' THEN 1 ELSE 0 END) as akta_verified,
    SUM(CASE WHEN dp.jenis_dokumen = 'KK' AND dp.status_verifikasi = 'DIVERIFIKASI' THEN 1 ELSE 0 END) as kk_verified,
    SUM(CASE WHEN dp.jenis_dokumen = 'KTP_ORTU' AND dp.status_verifikasi = 'DIVERIFIKASI' THEN 1 ELSE 0 END) as ktp_ortu_verified,
    SUM(CASE WHEN dp.jenis_dokumen = 'FOTO' AND dp.status_verifikasi = 'DIVERIFIKASI' THEN 1 ELSE 0 END) as foto_verified,
    SUM(CASE WHEN dp.jenis_dokumen = 'IJAZAH' AND dp.status_verifikasi = 'DIVERIFIKASI' THEN 1 ELSE 0 END) as ijazah_verified,
    SUM(CASE WHEN dp.jenis_dokumen = 'SPJM' AND dp.status_verifikasi = 'DIVERIFIKASI' THEN 1 ELSE 0 END) as spjm_verified,
    COUNT(dp.id_dokumen) as total_dokumen,
    SUM(CASE WHEN dp.status_verifikasi = 'DIVERIFIKASI' THEN 1 ELSE 0 END) as dokumen_verified,
    SUM(CASE WHEN dp.status_verifikasi = 'DITOLAK' THEN 1 ELSE 0 END) as dokumen_rejected,
    CASE 
        WHEN SUM(CASE WHEN dp.status_verifikasi = 'DIVERIFIKASI' THEN 1 ELSE 0 END) = 6 THEN 'LENGKAP'
        WHEN SUM(CASE WHEN dp.status_verifikasi = 'DITOLAK' THEN 1 ELSE 0 END) > 0 THEN 'ADA_DITOLAK'
        WHEN COUNT(dp.id_dokumen) < 6 THEN 'BELUM_LENGKAP'
        ELSE 'PENDING'
    END as status_kelengkapan
FROM pendaftar p
LEFT JOIN dokumen_pendaftar dp ON p.id_pendaftaran = dp.id_pendaftaran
GROUP BY p.id_pendaftaran, p.nomor_daftar, p.nama_lengkap;

-- View 4: Ranking Per Jalur
CREATE VIEW vw_ranking_jalur AS
SELECT 
    p.id_pendaftaran,
    p.nomor_daftar,
    p.nama_lengkap,
    jp.nama_jalur,
    p.nilai_total,
    p.status_berkas,
    p.status_seleksi,
    ROW_NUMBER() OVER (
        PARTITION BY p.id_jalur 
        ORDER BY p.nilai_total DESC, p.nilai_seleksi_internal DESC, p.tanggal_daftar ASC
    ) as ranking_calculated,
    jp.kuota_jalur,
    CASE 
        WHEN ROW_NUMBER() OVER (
            PARTITION BY p.id_jalur 
            ORDER BY p.nilai_total DESC, p.nilai_seleksi_internal DESC, p.tanggal_daftar ASC
        ) <= jp.kuota_jalur THEN 'DITERIMA'
        WHEN ROW_NUMBER() OVER (
            PARTITION BY p.id_jalur 
            ORDER BY p.nilai_total DESC, p.nilai_seleksi_internal DESC, p.tanggal_daftar ASC  
        ) <= jp.kuota_jalur * 1.2 THEN 'CADANGAN'
        ELSE 'TIDAK_DITERIMA'
    END as status_seleksi_calculated
FROM pendaftar p
INNER JOIN jalur_pendaftaran jp ON p.id_jalur = jp.id_jalur
WHERE p.status_berkas = 'DIVERIFIKASI'
  AND p.nilai_total IS NOT NULL;

-- View 5: Selection Statistics
CREATE VIEW vw_selection_statistics AS
SELECT 
    jp.nama_jalur,
    jp.kuota_jalur,
    COUNT(p.id_pendaftaran) as total_pendaftar,
    COUNT(CASE WHEN p.status_berkas = 'DIVERIFIKASI' THEN 1 END) as berkas_verified,
    COUNT(CASE WHEN p.status_seleksi = 'DITERIMA' THEN 1 END) as diterima,
    COUNT(CASE WHEN p.status_seleksi = 'CADANGAN' THEN 1 END) as cadangan,
    COUNT(CASE WHEN p.status_seleksi = 'TIDAK_DITERIMA' THEN 1 END) as tidak_diterima,
    ROUND(AVG(CASE WHEN p.nilai_total IS NOT NULL THEN p.nilai_total END), 2) as nilai_rata_rata,
    MAX(p.nilai_total) as nilai_tertinggi,
    MIN(CASE WHEN p.nilai_total IS NOT NULL THEN p.nilai_total END) as nilai_terendah,
    ROUND((COUNT(CASE WHEN p.status_seleksi = 'DITERIMA' THEN 1 END) / jp.kuota_jalur * 100), 2) as persentase_terisi
FROM jalur_pendaftaran jp
LEFT JOIN pendaftar p ON jp.id_jalur = p.id_jalur
GROUP BY jp.id_jalur, jp.nama_jalur, jp.kuota_jalur;

-- =====================================================
-- 9. STORED PROCEDURES
-- =====================================================

-- Procedure 1: Jalankan Seleksi Otomatis
DELIMITER $$
CREATE PROCEDURE sp_jalankan_seleksi_otomatis(
    IN p_id_gelombang INT,
    OUT p_result_code INT,
    OUT p_result_message VARCHAR(500)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        GET DIAGNOSTICS CONDITION 1
            p_result_code = MYSQL_ERRNO,
            p_result_message = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    -- 1. Reset all status to TIDAK_DITERIMA
    UPDATE pendaftar 
    SET status_seleksi = 'TIDAK_DITERIMA', 
        ranking_jalur = NULL, 
        ranking_global = NULL
    WHERE id_gelombang = p_id_gelombang;
    
    -- 2. Calculate ranking per jalur
    UPDATE pendaftar p
    INNER JOIN (
        SELECT 
            id_pendaftaran,
            ROW_NUMBER() OVER (
                PARTITION BY id_jalur 
                ORDER BY nilai_total DESC, nilai_seleksi_internal DESC, tanggal_daftar ASC
            ) as new_ranking
        FROM pendaftar 
        WHERE id_gelombang = p_id_gelombang 
          AND status_berkas = 'DIVERIFIKASI'
          AND nilai_total IS NOT NULL
    ) ranked ON p.id_pendaftaran = ranked.id_pendaftaran
    SET p.ranking_jalur = ranked.new_ranking;
    
    -- 3. Update status based on ranking and kuota
    UPDATE pendaftar p
    INNER JOIN jalur_pendaftaran jp ON p.id_jalur = jp.id_jalur
    SET p.status_seleksi = CASE 
        WHEN p.ranking_jalur <= jp.kuota_jalur THEN 'DITERIMA'
        WHEN p.ranking_jalur <= jp.kuota_jalur * 1.2 THEN 'CADANGAN'  
        ELSE 'TIDAK_DITERIMA'
    END
    WHERE p.id_gelombang = p_id_gelombang 
      AND p.ranking_jalur IS NOT NULL;
    
    -- 4. Calculate global ranking
    UPDATE pendaftar p
    INNER JOIN (
        SELECT 
            id_pendaftaran,
            ROW_NUMBER() OVER (
                ORDER BY nilai_total DESC, nilai_seleksi_internal DESC, tanggal_daftar ASC
            ) as global_ranking
        FROM pendaftar 
        WHERE id_gelombang = p_id_gelombang 
          AND status_berkas = 'DIVERIFIKASI'
    ) global_ranked ON p.id_pendaftaran = global_ranked.id_pendaftaran
    SET p.ranking_global = global_ranked.global_ranking;
    
    COMMIT;
    
    SET p_result_code = 0;
    SET p_result_message = 'Seleksi otomatis berhasil dijalankan';
    
END$$
DELIMITER ;

-- Procedure 2: Send Mass Notification
DELIMITER $$  
CREATE PROCEDURE sp_send_mass_notification(
    IN p_id_gelombang INT,
    IN p_status_filter VARCHAR(50),
    IN p_tipe_notifikasi ENUM('EMAIL', 'SMS'),
    IN p_kategori VARCHAR(50),
    IN p_judul VARCHAR(200),
    IN p_pesan TEXT,
    OUT p_result_code INT,
    OUT p_result_message VARCHAR(500)
)
BEGIN
    DECLARE v_count INT DEFAULT 0;
    
    INSERT INTO notifikasi (
        id_pendaftaran, tipe_notifikasi, kategori, judul, pesan, 
        email_tujuan, no_hp_tujuan, created_at
    )
    SELECT 
        p.id_pendaftaran,
        p_tipe_notifikasi,
        p_kategori,
        p_judul,
        p_pesan,
        CASE WHEN p_tipe_notifikasi = 'EMAIL' THEN p.email ELSE NULL END,
        CASE WHEN p_tipe_notifikasi = 'SMS' THEN p.no_telepon ELSE NULL END,
        NOW()
    FROM pendaftar p
    WHERE p.id_gelombang = p_id_gelombang
      AND (p_status_filter = 'ALL' OR p.status_seleksi = p_status_filter)
      AND (
          (p_tipe_notifikasi = 'EMAIL' AND p.email IS NOT NULL) OR
          (p_tipe_notifikasi = 'SMS' AND p.no_telepon IS NOT NULL)
      );
    
    SELECT ROW_COUNT() INTO v_count;
    
    SET p_result_code = 0;
    SET p_result_message = CONCAT('Mass notification created: ', v_count, ' recipients');
    
END$$
DELIMITER ;

-- Procedure 3: Cleanup Old Data
DELIMITER $$
CREATE PROCEDURE sp_cleanup_old_data(
    IN p_retention_months INT,
    OUT p_result_code INT, 
    OUT p_result_message VARCHAR(500)
)
BEGIN
    DECLARE v_cutoff_date DATE;
    DECLARE v_deleted_logs INT DEFAULT 0;
    DECLARE v_deleted_notifications INT DEFAULT 0;
    
    SET v_cutoff_date = DATE_SUB(CURDATE(), INTERVAL p_retention_months MONTH);
    
    -- Delete old audit logs
    DELETE FROM audit_logs 
    WHERE created_at < v_cutoff_date;
    SET v_deleted_logs = ROW_COUNT();
    
    -- Delete old notifications
    DELETE FROM notifikasi 
    WHERE created_at < v_cutoff_date 
      AND status_kirim IN ('DELIVERED', 'FAILED', 'BOUNCED');
    SET v_deleted_notifications = ROW_COUNT();
    
    SET p_result_code = 0;
    SET p_result_message = CONCAT('Cleanup completed. Logs: ', v_deleted_logs, ', Notifications: ', v_deleted_notifications);
    
END$$
DELIMITER ;

-- =====================================================
-- 10. SAMPLE MASTER DATA
-- =====================================================

-- Insert sample users (admin accounts)
INSERT INTO users (username, email, password, role, is_active) VALUES
('superadmin', 'superadmin@alfadl.sch.id', '$2a$12$J3R1L8MqGXZv7K9w/mNhGe7JjZgJn6hC8mDk2A9QrE4P1sOvWqF8.', 'SUPER_ADMIN', TRUE),
('admin_pmb', 'admin.pmb@alfadl.sch.id', '$2a$12$L2K9MqGXZv7K9w/mNhGe7JjZgJn6hC8mDk2A9QrE4P1sOvWqF8K.', 'ADMIN', TRUE),
('operator_1', 'operator1@alfadl.sch.id', '$2a$12$M3L9MqGXZv7K9w/mNhGe7JjZgJn6hC8mDk2A9QrE4P1sOvWqF8L.', 'OPERATOR', TRUE);

-- Insert jalur pendaftaran
INSERT INTO jalur_pendaftaran (nama_jalur, deskripsi, kuota_jalur, bobot_prioritas, persyaratan_umum, is_active, urutan_prioritas) VALUES
('Domisili', 'Jalur khusus untuk siswa berdomisili di sekitar sekolah', 40, 1.00, 'Memiliki KK dengan alamat dalam radius 5km dari sekolah', TRUE, 1),
('Prestasi', 'Jalur untuk siswa berprestasi akademik dan non-akademik', 30, 1.20, 'Memiliki sertifikat prestasi tingkat minimal kabupaten', TRUE, 2),
('Afirmasi', 'Jalur untuk siswa dari keluarga kurang mampu', 20, 1.10, 'Memiliki surat keterangan tidak mampu dari kelurahan', TRUE, 3),
('Mutasi', 'Jalur untuk siswa pindahan dari sekolah lain', 10, 1.00, 'Surat pindah dari sekolah asal dan surat rekomendasi', TRUE, 4);

-- Insert gelombang pendaftaran
INSERT INTO gelombang (nama_gelombang, tahun_ajaran, tanggal_buka, tanggal_tutup, kuota_gelombang, biaya_pendaftaran, is_active) VALUES
('Gelombang 1', '2026-2027', '2026-01-15', '2026-02-15', 80, 150000.00, TRUE),
('Gelombang 2', '2026-2027', '2026-03-01', '2026-03-31', 20, 175000.00, TRUE);

-- Insert daya tampung
INSERT INTO daya_tampung (id_gelombang, tahun_ajaran, kuota_total, kuota_terisi, sisa_kuota, status) VALUES
(1, '2026-2027', 80, 0, 80, 'ACTIVE'),
(2, '2026-2027', 20, 0, 20, 'DRAFT');

-- Insert sistem konfigurasi
INSERT INTO sistem_config (config_key, config_value, config_type, deskripsi, is_active) VALUES
('app.name', 'SPMB SMPIT Al-Fadl', 'STRING', 'Nama aplikasi', TRUE),
('app.version', '1.0.0', 'STRING', 'Versi aplikasi', TRUE),
('registration.max_file_size', '10485760', 'INTEGER', 'Maksimal ukuran file upload (bytes)', TRUE),
('registration.allowed_file_types', '["image/jpeg","image/png","application/pdf"]', 'JSON', 'Tipe file yang diizinkan untuk upload', TRUE),
('payment.auto_expire_hours', '24', 'INTEGER', 'Otomatis expire pembayaran setelah X jam', TRUE),
('notification.email_enabled', 'true', 'BOOLEAN', 'Enable email notification', TRUE),
('notification.sms_enabled', 'false', 'BOOLEAN', 'Enable SMS notification', TRUE),
('selection.auto_ranking', 'true', 'BOOLEAN', 'Enable automatic ranking calculation', TRUE),
('selection.cadangan_percentage', '20', 'INTEGER', 'Persentase cadangan dari kuota jalur', TRUE),
('tahfidz.minimum_score', '70', 'INTEGER', 'Nilai minimal tahfidz untuk lulus', TRUE),
('wawancara.minimum_score', '65', 'INTEGER', 'Nilai minimal wawancara untuk lulus', TRUE),
('school.address', 'Jl. Raya Cibinong No. 123, Cibinong, Bogor', 'STRING', 'Alamat sekolah', TRUE),
('school.phone', '021-12345678', 'STRING', 'Nomor telepon sekolah', TRUE),
('school.email', 'info@alfadl.sch.id', 'STRING', 'Email sekolah', TRUE);

-- Sample pendaftar data (5 sample records)
INSERT INTO pendaftar (
    nomor_daftar, id_gelombang, id_jalur, nik, nama_lengkap, tempat_lahir, tanggal_lahir, 
    jenis_kelamin, agama, alamat_lengkap, kelurahan, kecamatan, kabupaten, provinsi, 
    no_telepon, email, asal_sekolah, nama_ayah, nama_ibu, pekerjaan_ayah, pekerjaan_ibu,
    nilai_seleksi_internal, status_pendaftaran, status_berkas
) VALUES
('PMB2026010001', 1, 1, '3201012345678901', 'Ahmad Rizki Pratama', 'Jakarta', '2012-05-15', 'L', 'Islam', 
 'Jl. Mawar No. 10 RT 02/03', 'Cibinong', 'Cibinong', 'Bogor', 'Jawa Barat',
 '081234567890', 'ahmad.rizki@email.com', 'SDN Cibinong 01', 'Budi Pratama', 'Siti Nurhaliza', 'PNS', 'Guru',
 85.50, 'SUBMITTED', 'PENDING'),
 
('PMB2026010002', 1, 2, '3201012345678902', 'Fatimah Azzahra', 'Bogor', '2012-03-22', 'P', 'Islam',
 'Jl. Melati No. 25 RT 01/02', 'Nanggewer', 'Cibinong', 'Bogor', 'Jawa Barat',
 '081234567891', 'fatimah.azzahra@email.com', 'SDN Nanggewer 02', 'Ali Rahman', 'Khadijah', 'Wiraswasta', 'Ibu Rumah Tangga',
 92.00, 'SUBMITTED', 'DIVERIFIKASI'),
 
('PMB2026010003', 1, 1, '3201012345678903', 'Muhammad Hafiz', 'Depok', '2012-07-08', 'L', 'Islam',
 'Jl. Anggrek No. 5 RT 03/01', 'Karadenan', 'Cibinong', 'Bogor', 'Jawa Barat', 
 '081234567892', 'hafiz.muhammad@email.com', 'SDN Karadenan 01', 'Hasan Basri', 'Aisyah', 'Pegawai Swasta', 'Guru',
 78.25, 'SUBMITTED', 'DIVERIFIKASI'),
 
('PMB2026010004', 1, 3, '3201012345678904', 'Zahra Aulia', 'Cibinong', '2012-11-30', 'P', 'Islam',
 'Jl. Dahlia No. 15 RT 02/04', 'Sukahati', 'Cibinong', 'Bogor', 'Jawa Barat',
 '081234567893', 'zahra.aulia@email.com', 'SDN Sukahati 01', 'Ahmad Yusuf', 'Maryam', 'Buruh', 'Penjaga Warung',
 72.75, 'SUBMITTED', 'DIVERIFIKASI'),
 
('PMB2026010005', 1, 2, '3201012345678905', 'Ibrahim Al-Farisi', 'Tangerang', '2012-01-18', 'L', 'Islam',
 'Jl. Kenanga No. 8 RT 01/03', 'Tengah', 'Cibinong', 'Bogor', 'Jawa Barat',
 '081234567894', 'ibrahim.alfarisi@email.com', 'SDN Tengah 02', 'Umar Faruq', 'Zainab', 'Guru', 'Perawat',
 88.50, 'SUBMITTED', 'DIVERIFIKASI');

-- Sample dokumen pendaftar
INSERT INTO dokumen_pendaftar (id_pendaftaran, jenis_dokumen, nama_file, path_file, ukuran_file, mime_type, status_verifikasi) VALUES
(1, 'AKTA', 'akta_ahmad_rizki.pdf', '/uploads/2026/01/akta_ahmad_rizki.pdf', 1024000, 'application/pdf', 'UPLOADED'),
(1, 'KK', 'kk_ahmad_rizki.pdf', '/uploads/2026/01/kk_ahmad_rizki.pdf', 856000, 'application/pdf', 'UPLOADED'),
(2, 'AKTA', 'akta_fatimah.pdf', '/uploads/2026/01/akta_fatimah.pdf', 1200000, 'application/pdf', 'DIVERIFIKASI'),
(2, 'KK', 'kk_fatimah.pdf', '/uploads/2026/01/kk_fatimah.pdf', 950000, 'application/pdf', 'DIVERIFIKASI'),
(2, 'FOTO', 'foto_fatimah.jpg', '/uploads/2026/01/foto_fatimah.jpg', 256000, 'image/jpeg', 'DIVERIFIKASI');

-- Sample pembayaran
INSERT INTO pembayaran (id_pendaftaran, kode_pembayaran, jenis_pembayaran, jumlah_tagihan, metode_pembayaran, status_pembayaran) VALUES
(1, 'PAY20260115001', 'PENDAFTARAN', 150000.00, 'BANK_TRANSFER', 'PENDING'),
(2, 'PAY20260115002', 'PENDAFTARAN', 150000.00, 'E_WALLET', 'PAID'),
(3, 'PAY20260115003', 'PENDAFTARAN', 150000.00, 'BANK_TRANSFER', 'PAID'),
(4, 'PAY20260115004', 'PENDAFTARAN', 150000.00, 'CASH', 'PAID'),
(5, 'PAY20260115005', 'PENDAFTARAN', 150000.00, 'VA', 'PAID');

-- Sample tahfidz test
INSERT INTO tahfidz_test (id_pendaftaran, jadwal_test, juz_hafalan, nilai_kelancaran, nilai_tajwid, nilai_fashohah, penguji, status_test) VALUES
(2, '2026-02-20 09:00:00', 'Juz 30', 85.00, 88.00, 82.00, 'Ustadz Ahmad', 'COMPLETED'),
(3, '2026-02-20 09:30:00', 'Juz 29-30', 75.00, 78.00, 80.00, 'Ustadz Ahmad', 'COMPLETED'),
(4, '2026-02-20 10:00:00', 'Juz 30', 70.00, 72.00, 75.00, 'Ustadz Ahmad', 'COMPLETED');

-- Sample wawancara
INSERT INTO wawancara (id_pendaftaran, jadwal_wawancara, pewawancara, aspek_kepribadian, aspek_komunikasi, aspek_motivasi, aspek_pengetahuan, status_wawancara) VALUES
(2, '2026-02-25 10:00:00', 'Bu Siti Nurjanah', 85.00, 80.00, 88.00, 82.00, 'COMPLETED'),
(3, '2026-02-25 10:30:00', 'Bu Siti Nurjanah', 78.00, 75.00, 80.00, 76.00, 'COMPLETED'),
(4, '2026-02-25 11:00:00', 'Bu Siti Nurjanah', 72.00, 70.00, 75.00, 68.00, 'COMPLETED');

-- Sample notifikasi
INSERT INTO notifikasi (id_pendaftaran, tipe_notifikasi, kategori, judul, pesan, email_tujuan, status_kirim) VALUES
(1, 'EMAIL', 'PENDAFTARAN', 'Pendaftaran Berhasil', 'Pendaftaran Anda telah berhasil. Nomor pendaftaran: PMB2026010001', 'ahmad.rizki@email.com', 'SENT'),
(2, 'EMAIL', 'PENDAFTARAN', 'Berkas Diverifikasi', 'Berkas dokumen Anda telah diverifikasi dan dinyatakan lengkap.', 'fatimah.azzahra@email.com', 'DELIVERED'),
(2, 'EMAIL', 'PENGUMUMAN', 'Hasil Seleksi', 'Selamat! Anda dinyatakan DITERIMA di SMPIT Al-Fadl.', 'fatimah.azzahra@email.com', 'DELIVERED');

-- Sample audit logs
INSERT INTO audit_logs (id_user, module, aktivitas, detail, created_at) VALUES
(2, 'PENDAFTAR', 'CREATE', 'Pendaftar baru PMB2026010001 telah terdaftar', '2026-01-15 10:00:00'),
(2, 'PENDAFTAR', 'UPDATE', 'Status berkas pendaftar PMB2026010002 diubah ke DIVERIFIKASI', '2026-01-16 14:30:00'),
(2, 'SELEKSI', 'EXECUTE', 'Proses seleksi otomatis dijalankan untuk gelombang 1', '2026-02-28 09:00:00');

-- =====================================================
-- 11. DATA VERIFICATION QUERIES
-- =====================================================

-- Verify table creation and constraints
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    CREATE_TIME,
    TABLE_COMMENT
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'spmb_alfadl' 
ORDER BY TABLE_NAME;

-- Verify foreign key constraints
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE CONSTRAINT_SCHEMA = 'spmb_alfadl' 
AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, COLUMN_NAME;

-- Verify indexes
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'spmb_alfadl'
AND INDEX_NAME != 'PRIMARY'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- Test dashboard metrics view
SELECT * FROM vw_dashboard_metrics;

-- Test pendaftar complete view (limit 5)
SELECT nomor_daftar, nama_lengkap, nama_gelombang, nama_jalur, status_seleksi 
FROM vw_pendaftar_complete 
LIMIT 5;

-- Test selection statistics view
SELECT * FROM vw_selection_statistics;

-- =====================================================
-- 12. FINAL NOTES & MAINTENANCE
-- =====================================================

/*
DATABASE SCHEMA DEPLOYMENT CHECKLIST:

✅ 1. Database & Tables Created (13 tables)
✅ 2. Primary Keys Defined (all tables)
✅ 3. Foreign Keys Established (12 relationships)
✅ 4. Unique Constraints Applied (11 business rules)
✅ 5. Performance Indexes Created (25+ indexes)
✅ 6. Check Constraints Added (15+ data validation rules)
✅ 7. Triggers Implemented (6 automation triggers)
✅ 8. Views Created (5 reporting views)
✅ 9. Stored Procedures Added (3 business procedures)
✅ 10. Sample Master Data Inserted
✅ 11. Verification Queries Provided

MAINTENANCE RECOMMENDATIONS:

1. BACKUP STRATEGY:
   - Daily backup: sp_cleanup_old_data(6) -- Keep 6 months
   - Weekly full backup of entire database
   - Monthly archive old gelombang data

2. PERFORMANCE MONITORING:
   - Monitor slow query log for queries > 1 second
   - Check index usage monthly
   - Monitor table growth and plan for partitioning

3. SECURITY:
   - Regular password policy enforcement
   - Audit log review monthly
   - Failed login attempt monitoring

4. DATA INTEGRITY:
   - Weekly constraint validation check
   - Monthly foreign key relationship verification
   - Quarterly data consistency audit

5. CAPACITY PLANNING:
   - Estimate 2GB per academic year
   - Plan storage expansion annually
   - Monitor connection pool usage

DATABASE READY FOR PRODUCTION DEPLOYMENT!
*/

-- End of DATABASE_SCHEMA.sql