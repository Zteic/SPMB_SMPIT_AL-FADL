package models;

import java.io.Serializable;

public class Kuota implements Serializable {

    private int idKuota;
    private int idTahun;
    private int idJalur;
    private int totalKuota;
    private int kuotaTerisi;
    private int sisaKuota;
    private String namaTahunAjaran;
    private String namaJalur;

    public Kuota() {
    }

    public Kuota(int idKuota, int idTahun, int idJalur, int totalKuota, int kuotaTerisi, int sisaKuota) {
        this.idKuota = idKuota;
        this.idTahun = idTahun;
        this.idJalur = idJalur;
        this.totalKuota = totalKuota;
        this.kuotaTerisi = kuotaTerisi;
        this.sisaKuota = sisaKuota;
    }

    public int getIdKuota() {
        return idKuota;
    }

    public void setIdKuota(int idKuota) {
        this.idKuota = idKuota;
    }

    public int getIdTahun() {
        return idTahun;
    }

    public void setIdTahun(int idTahun) {
        this.idTahun = idTahun;
    }

    public int getIdJalur() {
        return idJalur;
    }

    public void setIdJalur(int idJalur) {
        this.idJalur = idJalur;
    }

    public int getTotalKuota() {
        return totalKuota;
    }

    public void setTotalKuota(int totalKuota) {
        this.totalKuota = totalKuota;
    }

    public int getKuotaTerisi() {
        return kuotaTerisi;
    }

    public void setKuotaTerisi(int kuotaTerisi) {
        this.kuotaTerisi = kuotaTerisi;
    }

    public int getSisaKuota() {
        return sisaKuota;
    }

    public void setSisaKuota(int sisaKuota) {
        this.sisaKuota = sisaKuota;
    }

    public String getNamaTahunAjaran() {
        return namaTahunAjaran;
    }

    public void setNamaTahunAjaran(String namaTahunAjaran) {
        this.namaTahunAjaran = namaTahunAjaran;
    }

    public String getNamaJalur() {
        return namaJalur;
    }

    public void setNamaJalur(String namaJalur) {
        this.namaJalur = namaJalur;
    }
}

