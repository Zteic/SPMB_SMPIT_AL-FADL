package models;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Rivaldi
 * File: User.java
 * Fungsi: Model utama user untuk autentikasi & role system (SPMB Core)
 */
public class User implements Serializable {

    // =========================
    // PRIMARY KEY
    // =========================
    private int idUser;

    // =========================
    // AUTH DATA
    // =========================
    private String username;
    private String passwordHash;
    private String role;
    private String status;

    // =========================
    // PROFILE DATA
    // =========================
    private String namaLengkap;
    private String email;
    private String noHp;

    // =========================
    // SYSTEM TRACKING
    // =========================
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // =========================
    // CONSTRUCTOR
    // =========================
    public User() {
        // default constructor
    }

    public User(int idUser, String username, String passwordHash, String namaLengkap, String role, String status) {
        this.idUser = idUser;
        this.username = username;
        this.passwordHash = passwordHash;
        this.namaLengkap = namaLengkap;
        this.role = role;
        this.status = status;
    }

    // FULL CONSTRUCTOR (UNTUK DAO / REPORT / MIGRATION)
    public User(int idUser, String username, String passwordHash, String namaLengkap,
                String email, String noHp, String role, String status,
                Timestamp createdAt, Timestamp updatedAt) {

        this.idUser = idUser;
        this.username = username;
        this.passwordHash = passwordHash;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.noHp = noHp;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // =========================
    // GETTER SETTER
    // =========================
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}