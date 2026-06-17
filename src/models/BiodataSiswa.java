package models;

import java.io.Serializable;
import java.util.Date;

/**
 * Biodata Siswa (SPMB SMPIT AL FADL)
 */
public class BiodataSiswa implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idBiodata;
    private int idSiswa;
    private String nomorPendaftaran;

    private String nik;
    private String nisn;
    private String nomorKk;
    private String namaLengkap;
    private String email;
    private String nomorHp;
    private String tempatLahir;
    private Date tanggalLahir;
    private String jenisKelamin;
    private String agama;
    private String sekolahAsal;

    private String alamatLengkap;
    private String desa;
    private String kecamatan;
    private String kabupaten;
    private String provinsi;

    private String namaAyah;
    private String pekerjaanAyah;
    private String namaIbu;
    private String pekerjaanIbu;

    private String namaPanggilan;
    private String kewarganegaraan;
    private int anakKe;
    private int jumlahSaudara;
    private int tinggiBadan;
    private int beratBadan;
    private String golonganDarah;
    private String hobi;
    private String citaCita;
    private String statusData; // VALID / INCOMPLETE / VERIFIED

    public BiodataSiswa() {}

    public BiodataSiswa(int idSiswa, String nomorPendaftaran, String nik, String nisn, String nomorKk,
                        String namaLengkap, String email, String nomorHp, String tempatLahir, Date tanggalLahir,
                        String jenisKelamin, String agama, String sekolahAsal) {
        this.idSiswa = idSiswa;
        this.nomorPendaftaran = nomorPendaftaran;
        this.nik = nik;
        this.nisn = nisn;
        this.nomorKk = nomorKk;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.nomorHp = nomorHp;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
        this.jenisKelamin = jenisKelamin;
        this.agama = agama;
        this.sekolahAsal = sekolahAsal;
    }

    public int getIdBiodata() {
        return idBiodata;
    }

    public void setIdBiodata(int idBiodata) {
        this.idBiodata = idBiodata;
    }

    public int getIdSiswa() {
        return idSiswa;
    }

    public void setIdSiswa(int idSiswa) {
        this.idSiswa = idSiswa;
    }

    public String getNomorPendaftaran() {
        return nomorPendaftaran;
    }

    public void setNomorPendaftaran(String nomorPendaftaran) {
        this.nomorPendaftaran = nomorPendaftaran;
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

    public String getNomorKk() {
        return nomorKk;
    }

    public void setNomorKk(String nomorKk) {
        this.nomorKk = nomorKk;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomorHp() {
        return nomorHp;
    }

    public void setNomorHp(String nomorHp) {
        this.nomorHp = nomorHp;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public void setTempatLahir(String tempatLahir) {
        this.tempatLahir = tempatLahir;
    }

    public Date getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(Date tanggalLahir) {
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

    public String getAlamatLengkap() {
        return alamatLengkap;
    }

    public void setAlamatLengkap(String alamatLengkap) {
        this.alamatLengkap = alamatLengkap;
    }

    public String getDesa() {
        return desa;
    }

    public void setDesa(String desa) {
        this.desa = desa;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getKabupaten() {
        return kabupaten;
    }

    public void setKabupaten(String kabupaten) {
        this.kabupaten = kabupaten;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getNamaAyah() {
        return namaAyah;
    }

    public void setNamaAyah(String namaAyah) {
        this.namaAyah = namaAyah;
    }

    public String getPekerjaanAyah() {
        return pekerjaanAyah;
    }

    public void setPekerjaanAyah(String pekerjaanAyah) {
        this.pekerjaanAyah = pekerjaanAyah;
    }

    public String getNamaIbu() {
        return namaIbu;
    }

    public void setNamaIbu(String namaIbu) {
        this.namaIbu = namaIbu;
    }

    public String getPekerjaanIbu() {
        return pekerjaanIbu;
    }

    public void setPekerjaanIbu(String pekerjaanIbu) {
        this.pekerjaanIbu = pekerjaanIbu;
    }

    public String getNamaPanggilan() {
        return namaPanggilan;
    }

    public void setNamaPanggilan(String namaPanggilan) {
        this.namaPanggilan = namaPanggilan;
    }

    public String getKewarganegaraan() {
        return kewarganegaraan;
    }

    public void setKewarganegaraan(String kewarganegaraan) {
        this.kewarganegaraan = kewarganegaraan;
    }

    public int getAnakKe() {
        return anakKe;
    }

    public void setAnakKe(int anakKe) {
        this.anakKe = anakKe;
    }

    public int getJumlahSaudara() {
        return jumlahSaudara;
    }

    public void setJumlahSaudara(int jumlahSaudara) {
        this.jumlahSaudara = jumlahSaudara;
    }

    public int getTinggiBadan() {
        return tinggiBadan;
    }

    public void setTinggiBadan(int tinggiBadan) {
        this.tinggiBadan = tinggiBadan;
    }

    public int getBeratBadan() {
        return beratBadan;
    }

    public void setBeratBadan(int beratBadan) {
        this.beratBadan = beratBadan;
    }

    public String getGolonganDarah() {
        return golonganDarah;
    }

    public void setGolonganDarah(String golonganDarah) {
        this.golonganDarah = golonganDarah;
    }

    public String getHobi() {
        return hobi;
    }

    public void setHobi(String hobi) {
        this.hobi = hobi;
    }

    public String getCitaCita() {
        return citaCita;
    }

    public void setCitaCita(String citaCita) {
        this.citaCita = citaCita;
    }

    public String getStatusData() {
        return statusData;
    }

    public void setStatusData(String statusData) {
        this.statusData = statusData;
    }
}

