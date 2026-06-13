package models;

import java.io.Serializable;

/**
 * @author Rivaldi (Revised by AI Assistant)
 * File: Siswa.java
 * Fungsi: Model utama representasi data siswa (CORE ENTITY MODERN)
 */
public class Siswa implements Serializable {

    // =========================
    // IDENTITAS DATABASE
    // =========================
    private int idSiswa;
    private int idUser;

    // =========================
    // DATA IDENTITAS DASAR
    // =========================
    private String nik;
    private String nisn;
    private String namaLengkap;
    private String tempatLahir;
    private String tanggalLahir;
    private String jenisKelamin;
    private String agama;

    // =========================
    // DATA SEKOLAH
    // =========================
    private String sekolahAsal;
    private String idJalur;

    // =========================
    // DATA ALAMAT
    // =========================
    private String provinsi;
    private String kotaKabupaten;
    private String kecamatan;
    private String kelurahan;
    private String asalDaerah;

    // =========================
    // DATA ADMINISTRASI
    // =========================
    private String nomorPendaftaran;
    private String statusPendaftaran;

    // =========================
    // CONSTRUCTOR
    // =========================
    public Siswa() {
    }

    public Siswa(int idSiswa, String nik, String namaLengkap) {
        this.idSiswa = idSiswa;
        this.nik = nik;
        this.namaLengkap = namaLengkap;
    }

    // =========================
    // GETTER & SETTER
    // =========================
    public int getIdSiswa() {
        return idSiswa;
    }

    public void setIdSiswa(int idSiswa) {
        this.idSiswa = idSiswa;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNisn() {
        return nisn;
    }

    public void setNisn(String nisn) {
        this.nisn = nisn;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public void setTempatLahir(String tempatLahir) {
        this.tempatLahir = tempatLahir;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getAgama() {
        return agama;
    }

    public void setAgama(String agama) {
        this.agama = agama;
    }

    public String getSekolahAsal() {
        return sekolahAsal;
    }

    public void setSekolahAsal(String sekolahAsal) {
        this.sekolahAsal = sekolahAsal;
    }

    public String getIdJalur() {
        return idJalur;
    }

    public void setIdJalur(String idJalur) {
        this.idJalur = idJalur;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKotaKabupaten() {
        return kotaKabupaten;
    }

    public void setKotaKabupaten(String kotaKabupaten) {
        this.kotaKabupaten = kotaKabupaten;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(String kelurahan) {
        this.kelurahan = kelurahan;
    }

    public String getAsalDaerah() {
        return asalDaerah;
    }

    public void setAsalDaerah(String asalDaerah) {
        this.asalDaerah = asalDaerah;
    }

    public String getNomorPendaftaran() {
        return nomorPendaftaran;
    }

    public void setNomorPendaftaran(String nomorPendaftaran) {
        this.nomorPendaftaran = nomorPendaftaran;
    }

    public String getStatusPendaftaran() {
        return statusPendaftaran;
    }

    public void setStatusPendaftaran(String statusPendaftaran) {
        this.statusPendaftaran = statusPendaftaran;
    }
}