package models;

import java.sql.Timestamp;

/**
 * Auditlog Model
 * SPMB SMPIT AL FADL
 *
 * Representasi tabel audit_logs
 * untuk mencatat semua aktivitas user
 */
public class Auditlog {

    private int idLog;
    private int idUser;
    private String username;
    private String role;
    private String aksi;
    private String rincian;
    private Timestamp waktuKejadian;

    // ======================================================
    // CONSTRUCTOR
    // ======================================================
    public Auditlog() {
    }

    public Auditlog(int idLog, int idUser, String username, String role,
                    String aksi, String rincian, Timestamp waktuKejadian) {
        this.idLog = idLog;
        this.idUser = idUser;
        this.username = username;
        this.role = role;
        this.aksi = aksi;
        this.rincian = rincian;
        this.waktuKejadian = waktuKejadian;
    }

    // ======================================================
    // GETTER & SETTER
    // ======================================================

    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAksi() {
        return aksi;
    }

    public void setAksi(String aksi) {
        this.aksi = aksi;
    }

    public String getRincian() {
        return rincian;
    }

    public void setRincian(String rincian) {
        this.rincian = rincian;
    }

    public Timestamp getWaktuKejadian() {
        return waktuKejadian;
    }

    public void setWaktuKejadian(Timestamp waktuKejadian) {
        this.waktuKejadian = waktuKejadian;
    }

    // ======================================================
    // DEBUG PRINT
    // ======================================================
    @Override
    public String toString() {
        return "Auditlog{" +
                "idLog=" + idLog +
                ", idUser=" + idUser +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", aksi='" + aksi + '\'' +
                ", rincian='" + rincian + '\'' +
                ", waktuKejadian=" + waktuKejadian +
                '}';
    }
}