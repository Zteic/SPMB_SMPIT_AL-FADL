package models;

import java.io.Serializable;

public class TahunAjaran implements Serializable {

    private int idTahun;
    private String tahunAjaran;
    private int statusAktif;

    public TahunAjaran() {
    }

    public TahunAjaran(int idTahun, String tahunAjaran, int statusAktif) {
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

    public int getStatusAktif() {
        return statusAktif;
    }

    public void setStatusAktif(int statusAktif) {
        this.statusAktif = statusAktif;
    }

    @Override
    public String toString() {
        return tahunAjaran != null ? tahunAjaran : "";
    }
}
