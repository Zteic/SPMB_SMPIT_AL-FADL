package models;

import java.sql.Timestamp;

/**
 * Pendaftar Model (LEGACY + MODERN COMPATIBLE)
 * SPMB SMPIT AL FADL
 *
 * Digunakan untuk:
 * - tabel pendaftar (legacy)
 * - mapping view laporan
 * - sinkronisasi ke tbl_siswa
 */
public class Pendaftar {

    private int idPendaftaran;

    private Integer idUser;

    private String nomorDaftar;
    private String nik;
    private String namaLengkap;

    private String tempatLahir;
    private String tanggalLahir; // tetap String untuk kompatibilitas form input

    private String jenisKelamin;
    private String agama;

    private String asalSekolah;

    private String kelurahan;
    private String kecamatan;
    private String kabupaten;
    private String provinsi;
    private String rt;
    private String rw;
    private String kodePos;

    private String statusPendaftaran;
    private String statusBerkas;
    private String statusSeleksi;

    private int idJalur;
    private int nilaiSeleksiInternal;

    private Timestamp tanggalDaftar;
    // CONSTRUCTOR
    public Pendaftar() {}

    public Pendaftar(int idPendaftaran,
                     String nomorDaftar,
                     String nik,
                     String namaLengkap,
                     String statusPendaftaran) {

        this.idPendaftaran = idPendaftaran;
        this.nomorDaftar = nomorDaftar;
        this.nik = nik;
        this.namaLengkap = namaLengkap;
        this.statusPendaftaran = statusPendaftaran;
    }
    // GETTER & SETTER

    public int getIdPendaftaran() {
        return idPendaftaran;
    }

    public void setIdPendaftaran(int idPendaftaran) {
        this.idPendaftaran = idPendaftaran;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getNomorDaftar() {
        return nomorDaftar;
    }

    public void setNomorDaftar(String nomorDaftar) {
        this.nomorDaftar = nomorDaftar;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
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

    public String getAsalSekolah() {
        return asalSekolah;
    }

    public void setAsalSekolah(String asalSekolah) {
        this.asalSekolah = asalSekolah;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(String kelurahan) {
        this.kelurahan = kelurahan;
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

    public String getStatusPendaftaran() {
        return statusPendaftaran;
    }

    public void setStatusPendaftaran(String statusPendaftaran) {
        this.statusPendaftaran = statusPendaftaran;
    }

    public String getStatusBerkas() {
        return statusBerkas;
    }

    public void setStatusBerkas(String statusBerkas) {
        this.statusBerkas = statusBerkas;
    }

    public String getStatusSeleksi() {
        return statusSeleksi;
    }

    public void setStatusSeleksi(String statusSeleksi) {
        this.statusSeleksi = statusSeleksi;
    }

    public int getIdJalur() {
        return idJalur;
    }

    public void setIdJalur(int idJalur) {
        this.idJalur = idJalur;
    }

    public int getNilaiSeleksiInternal() {
        return nilaiSeleksiInternal;
    }

    public void setNilaiSeleksiInternal(int nilaiSeleksiInternal) {
        this.nilaiSeleksiInternal = nilaiSeleksiInternal;
    }

    public Timestamp getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void setTanggalDaftar(Timestamp tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar;
    }
    
    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public String getRw() {
        return rw;
    }

    public void setRw(String rw) {
        this.rw = rw;
    }

    public String getKodePos() {
        return kodePos;
    }

    public void setKodePos(String kodePos) {
        this.kodePos = kodePos;
    }
}
