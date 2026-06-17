package views.siswa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * WizardPendaftaranPanel - Panel Wizard Navigasi Multi-Step Pendaftaran Siswa Mandiri.
 * Menyinkronkan sub-panel Formulir, Berkas, dan Pengumuman menggunakan CardLayout.
 * * @author Rivaldi
 */
public class WizardPendaftaranPanel extends JPanel {

    // <-------------------- KOMPONEN FORM -------------------->
    private JPanel panelKontainerUtama;
    private CardLayout tataLetakKartu;
    private JPanel panelFormulir;
    private KelolaBerkasPanel panelBerkas;
    private PengumumanPanel panelPengumuman;
    private JButton btnLangkah1;
    private JButton btnLangkah2;
    private JButton btnLangkah3;

    // <-------------------- SESSION & STATE -------------------->
    private String nomorPendaftaranSiswa;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari WizardPendaftaranPanel dan menginisialisasi nilai awal.
     * * @param nomorPendaftaran nomor registrasi pendaftaran siswa
     */
    public WizardPendaftaranPanel(String nomorPendaftaran) {
        this.nomorPendaftaranSiswa = nomorPendaftaran;

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initWizard();
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    /**
     * Membangun komponen wizard dan menghubungkan sub-panel halaman ke CardLayout.
     */
    private void initWizard() {
        tataLetakKartu = new CardLayout();
        panelKontainerUtama = new JPanel(tataLetakKartu);
        panelKontainerUtama.setBackground(Color.WHITE);

        panelFormulir = new JPanel(); 
        panelFormulir.setBackground(java.awt.Color.WHITE);
        panelFormulir.add(new javax.swing.JLabel("Formulir sudah dipindahkan ke halaman pendaftaran publik."));
        
        panelBerkas = new KelolaBerkasPanel();
        panelPengumuman = new PengumumanPanel(); 

        panelKontainerUtama.add(panelFormulir, "FORM");
        panelKontainerUtama.add(panelBerkas, "BERKAS");
        panelKontainerUtama.add(panelPengumuman, "PENGUMUMAN");

        add(panelKontainerUtama, BorderLayout.CENTER);
        add(buildNav(), BorderLayout.NORTH);

        showStep("FORM", btnLangkah1);
    }

    // <-------------------- EVENT BUTTON -------------------->
    /**
     * Membangun menu bilah tombol navigasi bagian atas dengan listener realtime.
     */
    private JPanel buildNav() {
        JPanel nav = new JPanel(new GridLayout(1, 3, 8, 0));
        nav.setBackground(Color.WHITE);

        btnLangkah1 = createBtn("1. FORMULIR");
        btnLangkah2 = createBtn("2. BERKAS");
        btnLangkah3 = createBtn("3. PENGUMUMAN");

        btnLangkah1.addActionListener(e -> showStep("FORM", btnLangkah1));

        btnLangkah2.addActionListener((ActionEvent e) -> {
            panelBerkas.muatStatusBerkasSiswa();
            showStep("BERKAS", btnLangkah2);
        });

        btnLangkah3.addActionListener((ActionEvent e) -> {
            panelPengumuman.loadDataSeleksi(); 
            showStep("PENGUMUMAN", btnLangkah3);
        });

        nav.add(btnLangkah1);
        nav.add(btnLangkah2);
        nav.add(btnLangkah3);

        return nav;
    }

    // <-------------------- HELPER METHOD -------------------->
    private JButton createBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(245, 246, 250));
        btn.setForeground(Color.BLACK);
        return btn;
    }

    // <-------------------- UTILITY -------------------->
    /**
     * Mengatur status halaman aktif dan memperbarui warna lencana tombol navigasi.
     * * @param card id nama kartu halaman target routing
     * @param active instansiasi komponen tombol yang sedang dipilih
     */
    private void showStep(String card, JButton active) {
        tataLetakKartu.show(panelKontainerUtama, card);

        JButton[] all = {btnLangkah1, btnLangkah2, btnLangkah3};

        for (JButton b : all) {
            b.setBackground(new Color(245, 246, 250));
            b.setForeground(Color.BLACK);
        }

        active.setBackground(new Color(41, 128, 185));
        active.setForeground(Color.BLACK);
    }
}
