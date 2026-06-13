package models;

/**
 * File: Berkas.java
 * Fungsi: Representasi dokumen siswa (SPMB)
 */
public class Berkas {

    private String idSiswa;

    private String aktaKelahiran;
    private String ktpOrtu;
    private String fotoAnak;
    private String spjm;

    private String statusVerifikasi; // PENDING / LENGKAP / TIDAK_LENGKAP

    public Berkas() {}

    public String getIdSiswa() {
        return idSiswa;
    }

    public void setIdSiswa(String idSiswa) {
        this.idSiswa = idSiswa;
    }

    public String getAktaKelahiran() {
        return aktaKelahiran;
    }

    public void setAktaKelahiran(String aktaKelahiran) {
        this.aktaKelahiran = aktaKelahiran;
    }

    public String getKtpOrtu() {
        return ktpOrtu;
    }

    public void setKtpOrtu(String ktpOrtu) {
        this.ktpOrtu = ktpOrtu;
    }

    public String getFotoAnak() {
        return fotoAnak;
    }

    public void setFotoAnak(String fotoAnak) {
        this.fotoAnak = fotoAnak;
    }

    public String getSpjm() {
        return spjm;
    }

    public void setSpjm(String spjm) {
        this.spjm = spjm;
    }

    public String getStatusVerifikasi() {
        return statusVerifikasi;
    }

    public void setStatusVerifikasi(String statusVerifikasi) {
        this.statusVerifikasi = statusVerifikasi;
    }
}