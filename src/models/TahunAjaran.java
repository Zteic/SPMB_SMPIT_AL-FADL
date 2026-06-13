package models;

import java.io.Serializable;

/**
 * @author Rivaldi
 * File: TahunAjaran.java
 * Fungsi: Model data Tahun Ajaran (Tipe data status_aktif disinkronkan dengan INT Database)
 */
public class TahunAjaran implements Serializable {

    private int idTahun;
    private String tahunAjaran;
    private int statusAktif; // 🎯 REVISI: Diubah dari String menjadi int agar klop dengan database

    public TahunAjaran() {
    }

    public TahunAjaran(int idTahun, String tahunAjaran, int statusAktif) { // 🎯 REVISI: Parameter memakai int
        this.idTahun = idTahun;
        this.tahunAjaran = tahunAjaran;
        this.statusAktif = statusAktif;
    }

    public int getIdTahun() {
        return idTahun;
    }

    public void setIdTahun(int idTahun) {
        this.idTahun = idTahun;
    }

    public String getTahunAjaran() {
        return tahunAjaran;
    }

    public void setTahunAjaran(String tahunAjaran) {
        this.tahunAjaran = tahunAjaran;
    }

    // 🎯 REVISI: Getter mengembalikan tipe data int
    public int getStatusAktif() {
        return statusAktif;
    }

    // 🎯 REVISI: Setter menerima parameter bertipe int
    public void setStatusAktif(int statusAktif) {
        this.statusAktif = statusAktif;
    }

    @Override
    public String toString() {
        return tahunAjaran != null ? tahunAjaran : "";
    }
}