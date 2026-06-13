package views.admin;

import controllers.BiodataSiswaController;
import models.BiodataSiswa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Dialog Form Pop-up Perubahan dan Sinkronisasi Biodata Lengkap Siswa.
 * Kepatuhan Kompilasi: NetBeans 8.2 / JDK 8 Linier Engine.
 * @author Rivaldi
 */
public class EditDataSiswaDialog extends JDialog {

    // <-------------------- CONTROLLER -------------------->
    private final BiodataSiswaController controller = new BiodataSiswaController();
    
    // <-------------------- SESSION & STATE -------------------->
    private final String nomorPendaftaran;
    private final Runnable onSaved;

    // <-------------------- KOMPONEN FORM: DATA SISWA -------------------->
    private JTextField fNik;
    private JTextField fNisn;
    private JTextField fKk; 
    private JTextField fNama;
    private JTextField fEmail;
    private JTextField fHp;
    private JTextField fTempat;
    private JTextField fTanggal;
    private JComboBox<String> fJenis;
    private JTextField fAgama;
    private JTextField fSekolah;

    // <-------------------- KOMPONEN FORM: ALAMAT -------------------->
    private JTextArea fAlamat;
    private JTextField fDesa; 
    private JTextField fKec;
    private JTextField fKab;
    private JTextField fProv;

    // <-------------------- KOMPONEN FORM: ORANG TUA -------------------->
    private JTextField fNamaAyah;
    private JTextField fPekerjaanAyah;
    private JTextField fNamaIbu;
    private JTextField fPekerjaanIbu;

    // <-------------------- CONSTRUCTOR -------------------->
    public EditDataSiswaDialog(Window parent, String nomorPendaftaran, Runnable onSaved) {
        super(parent, "Edit Data Siswa - " + nomorPendaftaran, ModalityType.APPLICATION_MODAL);
        this.nomorPendaftaran = nomorPendaftaran;
        this.onSaved = onSaved;
        initUI();
        loadData();
    }

    // <-------------------- INISIALISASI KOMPONEN UI -------------------->
    private void initUI() {
        setPreferredSize(new Dimension(900, 650));
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.addTab("Data Siswa", createDataSiswaPanel());
        tabs.addTab("Data Alamat", createDataAlamatPanel());
        tabs.addTab("Data Orang Tua", createDataOrangTuaPanel());

        add(tabs, BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
    }

    private JComponent createDataSiswaPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.3;

        fNik = new JTextField();
        fNisn = new JTextField();
        fKk = new JTextField();
        fNama = new JTextField();
        fEmail = new JTextField();
        fHp = new JTextField();
        fTempat = new JTextField();
        fTanggal = new JTextField();
        fJenis = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        fJenis.setBackground(Color.WHITE);
        fAgama = new JTextField();
        fSekolah = new JTextField();

        fNik.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fNisn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fKk.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fHp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fTempat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fJenis.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fAgama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fSekolah.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        addLabelField(panel, c, 0, "NIK Siswa *", fNik);
        addLabelField(panel, c, 1, "NISN *", fNisn);
        addLabelField(panel, c, 2, "No. Kartu Keluarga (KK)", fKk);
        addLabelField(panel, c, 3, "Nama Lengkap Calon Siswa *", fNama);
        addLabelField(panel, c, 4, "Alamat E-mail", fEmail);
        addLabelField(panel, c, 5, "Nomor HP / WhatsApp *", fHp);
        addLabelField(panel, c, 6, "Tempat Lahir", fTempat);
        addLabelField(panel, c, 7, "Tanggal Lahir (YYYY-MM-DD) *", fTanggal);
        addLabelField(panel, c, 8, "Jenis Kelamin", fJenis);
        addLabelField(panel, c, 9, "Agama", fAgama);
        addLabelField(panel, c, 10, "Nama Sekolah Asal *", fSekolah);

        return new JScrollPane(panel);
    }

    private JComponent createDataAlamatPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        fAlamat = new JTextArea(5, 40);
        fAlamat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fAlamat.setLineWrap(true);
        fAlamat.setWrapStyleWord(true);
        JScrollPane scrollAlamat = new JScrollPane(fAlamat);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1.0;
        JLabel lblAlamatTitle = new JLabel("Alamat Rumah Domisili Lengkap (RT/RW / No. Rumah)");
        lblAlamatTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAlamatTitle.setForeground(new Color(64, 74, 86));
        panel.add(lblAlamatTitle, c);

        c.gridy = 1;
        c.weighty = 0.1;
        panel.add(scrollAlamat, c);

        c.gridwidth = 1;
        c.weighty = 0;
        fDesa = new JTextField();
        fKec = new JTextField();
        fKab = new JTextField();
        fProv = new JTextField();

        fDesa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fKec.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fKab.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fProv.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        addLabelField(panel, c, 2, "Kelurahan / Desa", fDesa);
        addLabelField(panel, c, 3, "Kecamatan", fKec);
        addLabelField(panel, c, 4, "Kabupaten / Kota", fKab);
        addLabelField(panel, c, 5, "Provinsi Wilayah", fProv);

        return new JScrollPane(panel);
    }

    private JComponent createDataOrangTuaPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        fNamaAyah = new JTextField();
        fPekerjaanAyah = new JTextField();
        fNamaIbu = new JTextField();
        fPekerjaanIbu = new JTextField();

        fNamaAyah.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fPekerjaanAyah.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fNamaIbu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fPekerjaanIbu.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        addLabelField(panel, c, 0, "Nama Lengkap Ayah Kandung", fNamaAyah);
        addLabelField(panel, c, 1, "Pekerjaan Utama Ayah", fPekerjaanAyah);
        addLabelField(panel, c, 2, "Nama Lengkap Ibu Kandung", fNamaIbu);
        addLabelField(panel, c, 3, "Pekerjaan Utama Ibu", fPekerjaanIbu);

        return new JScrollPane(panel);
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 16));
        
        JButton btnSave = new JButton("SIMPAN DATA PERUBAHAN");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFocusPainted(false);

        JButton btnCancel = new JButton("BATALKAN REVISI");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setBackground(new Color(231, 76, 60));
        btnCancel.setForeground(Color.BLACK);
        btnCancel.setFocusPainted(false);

        btnSave.addActionListener(this::handleSave);
        btnCancel.addActionListener(e -> dispose());

        footer.add(btnSave);
        footer.add(btnCancel);
        return footer;
    }

    private void addLabelField(JPanel panel, GridBagConstraints c, int row, String labelText, JComponent field) {
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 1;
        c.weightx = 0.3;
        JLabel lblInput = new JLabel(labelText);
        lblInput.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblInput.setForeground(new Color(64, 74, 86));
        panel.add(lblInput, c);

        c.gridx = 1;
        c.weightx = 0.7;
        panel.add(field, c);
    }

    // <-------------------- SYNC DATABASE ENGINE -------------------->
    private void loadData() {
        BiodataSiswa siswa = controller.getByNomorPendaftaran(nomorPendaftaran);
        if (siswa == null) {
            JOptionPane.showMessageDialog(this, "Data profil siswa gagal dimuat dari engine database.", "Error Sinkronisasi", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        fNik.setText(nullToEmpty(siswa.getNik()));
        fNisn.setText(nullToEmpty(siswa.getNisn()));
        fKk.setText(nullToEmpty(siswa.getNomorKk())); 
        fNama.setText(nullToEmpty(siswa.getNamaLengkap()));
        fEmail.setText(nullToEmpty(siswa.getEmail()));
        fHp.setText(nullToEmpty(siswa.getNomorHp()));
        fTempat.setText(nullToEmpty(siswa.getTempatLahir()));
        fTanggal.setText(siswa.getTanggalLahir() != null ? new SimpleDateFormat("yyyy-MM-dd").format(siswa.getTanggalLahir()) : "");
        
        String jenis = siswa.getJenisKelamin();
        if (jenis != null) {
            if (jenis.equalsIgnoreCase("L") || jenis.equalsIgnoreCase("Laki-laki")) {
                fJenis.setSelectedItem("Laki-laki");
            } else if (jenis.equalsIgnoreCase("P") || jenis.equalsIgnoreCase("Perempuan")) {
                fJenis.setSelectedItem("Perempuan");
            } else {
                fJenis.setSelectedItem(jenis);
            }
        }
        fAgama.setText(nullToEmpty(siswa.getAgama()));
        fSekolah.setText(nullToEmpty(siswa.getSekolahAsal()));

        fAlamat.setText(nullToEmpty(siswa.getAlamatLengkap()));
        fDesa.setText(nullToEmpty(siswa.getDesa())); // 🎯 FIXED SYNC: Memakai getter getDes() asli model pmb
        fKec.setText(nullToEmpty(siswa.getKecamatan()));
        fKab.setText(nullToEmpty(siswa.getKabupaten()));
        fProv.setText(nullToEmpty(siswa.getProvinsi()));

        fNamaAyah.setText(nullToEmpty(siswa.getNamaAyah()));
        fPekerjaanAyah.setText(nullToEmpty(siswa.getPekerjaanAyah()));
        fNamaIbu.setText(nullToEmpty(siswa.getNamaIbu()));
        fPekerjaanIbu.setText(nullToEmpty(siswa.getPekerjaanIbu()));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeJenisKelamin(String value) {
        if (value == null) return "Laki-laki";
        return (value.equalsIgnoreCase("Laki-laki") || value.equalsIgnoreCase("L")) ? "Laki-laki" : "Perempuan";
    }

    // <-------------------- ACTION SUBMIT DATA VALIDASI -------------------->
    private void handleSave(ActionEvent event) {
        if (fNama.getText().trim().isEmpty() || fSekolah.getText().trim().isEmpty() || fHp.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap, No HP, dan Sekolah Asal wajib diisi!", "Validasi Formulir", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!fNik.getText().trim().isEmpty() && !Pattern.matches("\\d{16}", fNik.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Format NIK salah, nomor identitas wajib 16 digit angka murni.", "Validasi Kriteria", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!fNisn.getText().trim().isEmpty() && !Pattern.matches("\\d{10}", fNisn.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Format NISN salah, nomor induk nasional wajib 10 digit angka.", "Validasi Kriteria", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!fEmail.getText().trim().isEmpty() && !Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$", fEmail.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Format alamat email tidak valid (Contoh: budi@gmail.com).", "Validasi Kriteria", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!fHp.getText().trim().isEmpty() && !Pattern.matches("\\d{10,15}", fHp.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Nomor Handphone pendaftar wajib berjumlah antara 10 s.d 15 digit.", "Validasi Kriteria", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BiodataSiswa siswa = controller.getByNomorPendaftaran(nomorPendaftaran);
        if (siswa == null) {
            JOptionPane.showMessageDialog(this, "Sistem gagal mengunci token ID pendaftaran siswa.", "Error Session", JOptionPane.ERROR_MESSAGE);
            return;
        }

        siswa.setNik(fNik.getText().trim());
        siswa.setNisn(fNisn.getText().trim());
        siswa.setNomorKk(fKk.getText().trim()); 
        siswa.setNamaLengkap(fNama.getText().trim());
        siswa.setEmail(fEmail.getText().trim());
        siswa.setNomorHp(fHp.getText().trim());
        siswa.setTempatLahir(fTempat.getText().trim());
        siswa.setJenisKelamin(normalizeJenisKelamin(fJenis.getSelectedItem() != null ? fJenis.getSelectedItem().toString() : "Laki-laki"));
        siswa.setAgama(fAgama.getText().trim());
        siswa.setSekolahAsal(fSekolah.getText().trim());

        if (fTanggal.getText().trim().isEmpty()) {
            siswa.setTanggalLahir(null);
        } else {
            try {
                Date parsed = new SimpleDateFormat("yyyy-MM-dd").parse(fTanggal.getText().trim());
                siswa.setTanggalLahir(parsed);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Format penulisan kalender lahir salah. Wajib menggunakan format: YYYY-MM-DD", "Validasi Kalender", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        siswa.setAlamatLengkap(fAlamat.getText().trim());
        siswa.setDesa(fDesa.getText().trim()); // 🎯 FIXED SYNC: Memakai setter setDesa() asli model pmb
        siswa.setKecamatan(fKec.getText().trim());
        siswa.setKabupaten(fKab.getText().trim());
        siswa.setProvinsi(fProv.getText().trim());

        siswa.setNamaAyah(fNamaAyah.getText().trim());
        siswa.setPekerjaanAyah(fPekerjaanAyah.getText().trim());
        siswa.setNamaIbu(fNamaIbu.getText().trim());
        siswa.setPekerjaanIbu(fPekerjaanIbu.getText().trim());

        boolean saved = controller.updateDataSiswa(siswa);
        if (saved) {
            JOptionPane.showMessageDialog(this, "Sinkronisasi Berhasil! Seluruh data transaksi pendaftar diperbarui ke server.", "Sukses Operasional", JOptionPane.INFORMATION_MESSAGE);
            if (onSaved != null) {
                onSaved.run(); 
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "MySQL Engine menolak perubahan data. Silakan periksa log koneksi server JDBC.", "Gagal Eksekusi", JOptionPane.ERROR_MESSAGE);
        }
    }
}