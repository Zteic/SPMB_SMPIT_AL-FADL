package models;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Pembayaran SPMB SMPIT AL FADL
 * Model transaksi keuangan siswa
 */
public class Pembayaran {

    private int idPembayaran;
    private int idSiswa;

    private String nomorInvoice;
    private BigDecimal nominal;

    private String metode; // CASH / TRANSFER / VA / QRIS
    private String buktiBayar;

    private String status; // PENDING / LUNAS / DITOLAK / VERIFIKASI

    private Timestamp tanggalBayar;
    private Timestamp tanggalVerifikasi;

    // =====================================
    // CONSTRUCTOR
    // =====================================
    public Pembayaran() {}

    public Pembayaran(int idSiswa, String nomorInvoice, BigDecimal nominal, String status) {
        this.idSiswa = idSiswa;
        this.nomorInvoice = nomorInvoice;
        this.nominal = nominal;
        this.status = status;
    }

    // =====================================
    // GETTER & SETTER
    // =====================================

    public int getIdPembayaran() {
        return idPembayaran;
    }

    public void setIdPembayaran(int idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    public int getIdSiswa() {
        return idSiswa;
    }

    public void setIdSiswa(int idSiswa) {
        this.idSiswa = idSiswa;
    }

    public String getNomorInvoice() {
        return nomorInvoice;
    }

    public void setNomorInvoice(String nomorInvoice) {
        this.nomorInvoice = nomorInvoice;
    }

    public BigDecimal getNominal() {
        return nominal;
    }

    public void setNominal(BigDecimal nominal) {
        this.nominal = nominal;
    }

    public String getMetode() {
        return metode;
    }

    public void setMetode(String metode) {
        this.metode = metode;
    }

    public String getBuktiBayar() {
        return buktiBayar;
    }

    public void setBuktiBayar(String buktiBayar) {
        this.buktiBayar = buktiBayar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTanggalBayar() {
        return tanggalBayar;
    }

    public void setTanggalBayar(Timestamp tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }

    public Timestamp getTanggalVerifikasi() {
        return tanggalVerifikasi;
    }

    public void setTanggalVerifikasi(Timestamp tanggalVerifikasi) {
        this.tanggalVerifikasi = tanggalVerifikasi;
    }
}