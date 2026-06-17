package models;

import java.io.Serializable;

public class Gelombang implements Serializable {

    private int idGelombang;
    private String namaGelombang;
    private String tanggalMulai;
    private String tanggalSelesai;
    private double biayaPendaftaran;
    private String status;

    public Gelombang() {
    }

    public Gelombang(int idGelombang, String namaGelombang, String tanggalMulai, String tanggalSelesai, double biayaPendaftaran, String status) {
        this.idGelombang = idGelombang;
        this.namaGelombang = namaGelombang;
        this.tanggalMulai = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.biayaPendaftaran = biayaPendaftaran;
        this.status = status;
    }

    public int getIdGelombang() {
        return idGelombang;
    }

    public void setIdGelombang(int idGelombang) {
        this.idGelombang = idGelombang;
    }

    public String getNamaGelombang() {
        return namaGelombang;
    }

    public void setNamaGelombang(String namaGelombang) {
        this.namaGelombang = namaGelombang;
    }

    public String getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(String tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public String getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setTanggalSelesai(String tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }

    public double getBiayaPendaftaran() {
        return biayaPendaftaran;
    }

    public void setBiayaPendaftaran(double biayaPendaftaran) {
        this.biayaPendaftaran = biayaPendaftaran;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

