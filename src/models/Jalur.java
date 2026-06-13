package models;

import java.io.Serializable;

public class Jalur implements Serializable {

    private int idJalur;
    private String namaJalur;
    private int kuotaPersen;
    private String status;

    public Jalur() {
    }

    public Jalur(int idJalur, String namaJalur, int kuotaPersen, String status) {
        this.idJalur = idJalur;
        this.namaJalur = namaJalur;
        this.kuotaPersen = kuotaPersen;
        this.status = status;
    }

    public int getIdJalur() {
        return idJalur;
    }

    public void setIdJalur(int idJalur) {
        this.idJalur = idJalur;
    }

    public String getNamaJalur() {
        return namaJalur;
    }

    public void setNamaJalur(String namaJalur) {
        this.namaJalur = namaJalur;
    }

    public int getKuotaPersen() {
        return kuotaPersen;
    }

    public void setKuotaPersen(int kuotaPersen) {
        this.kuotaPersen = kuotaPersen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return namaJalur != null ? namaJalur : "";
    }
}
