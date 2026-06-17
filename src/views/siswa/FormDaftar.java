package views.siswa;

import config.DatabaseConfig;
import org.mindrot.jbcrypt.BCrypt;
import views.LandingPage; 
import views.components.CustomButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Wizard Pendaftaran Multi-Step Terintegrasi Sistem PMB SMPIT AL FADL.
 * Menggunakan pendekatan berbasis CardLayout dan penataan komponen presisi GridBagLayout.
 * * @author Rivaldi
 */
public class FormDaftar extends JFrame {

    // <-------------------- KONSTANTA WARNA & FONT -------------------->
    private final Color clrHijauBg     = new Color(240, 247, 244); 
    private final Color clrHijauTua    = new Color(45, 90, 65);    
    private final Color clrHijauMain   = new Color(46, 125, 50);   
    
    private static final Color HIJAU = new Color(22, 163, 74);
    private static final Color MERAH = new Color(220, 38, 38);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    
    // <-------------------- KOMPONEN UI WIZARD ENGINE -------------------->
    private CardLayout cardLayout;
    private JPanel mainCardPanel;
    private JPanel stepIndicatorPanel;
    private CustomButton btnNext;
    private CustomButton btnPrev;
    private CustomButton btnSubmit;
    private CustomButton btnBackToLanding;
    private int currentStep = 1;
    private final int MAX_STEPS = 8;

    // <-------------------- KOMPONEN DATA INPUT FORM -------------------->
    private JTextField txtNisn;
    private JTextField txtHp;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;
    private JTextField txtNik;
    private JTextField txtNama;
    private JTextField txtTempatLahir;
    private JTextField txtTglLahir;
    private JTextField txtEmail;
    private JComboBox<String> cmbJk;
    private JComboBox<String> cmbAgama;
    private ButtonGroup bgJalur;
    private String selectedJalur = "Domisili";
    private JPanel panelYatimOption;
    private JRadioButton rbAyahHidup;
    private JRadioButton rbAyahMeninggal;
    private JTextField txtAyah;
    private JTextField txtKerjaAyah;
    private JTextField txtHpAyah;
    private JTextField txtIbu;
    private JTextField txtKerjaIbu;
    private JTextField txtHpIbu;
    private JComboBox<String> cmbPenghasilan;
    private JTextField txtProvinsi;
    private JTextField txtKabupaten;
    private JTextField txtKecamatan;
    private JTextField txtKelurahan;
    private JTextField txtKodePos;
    private JTextField txtRt;
    private JTextField txtRw;
    private JTextArea txtDetailAlamat;
    private JTextField txtNamaSekolah;
    private JTextField txtNpsn;
    private JTextField txtThnLulus;
    private JTextField txtAlamatSekolah;
    private Map<String, File> uploadedFiles = new HashMap<>();
    private JPanel panelDokumenDinamis;
    private JTextArea txtReview;
    private JCheckBox chkBenar;
    private JCheckBox chkSetuju;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari FormDaftar dan mendaftarkan pengunci tipe data.
     */
    public FormDaftar() {
        setTitle("Pendaftaran Siswa Baru - SMPIT AL FADL");
        setSize(850, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(clrHijauBg);
        setContentPane(rootPanel);

        JPanel headerTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 20));
        headerTitle.setBackground(clrHijauBg);
        JLabel title = new JLabel("Formulir Pendaftaran Siswa Baru");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(clrHijauTua);
        headerTitle.add(title);
        rootPanel.add(headerTitle, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(0, 30, 20, 30));

        stepIndicatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        stepIndicatorPanel.setBackground(Color.WHITE);
        stepIndicatorPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(220, 230, 225)));
        updateStepIndicator();
        centerWrapper.add(stepIndicatorPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBackground(Color.WHITE);
        mainCardPanel.setBorder(new LineBorder(new Color(220, 230, 225), 1));

        mainCardPanel.add(createStep1Akun(), "STEP1");
        mainCardPanel.add(createStep2Biodata(), "STEP2");
        mainCardPanel.add(createStep3Jalur(), "STEP3");
        mainCardPanel.add(createStep4Ortu(), "STEP4");
        mainCardPanel.add(createStep5Alamat(), "STEP5");
        mainCardPanel.add(createStep6Sekolah(), "STEP6");
        mainCardPanel.add(createStep7Dokumen(), "STEP7");
        mainCardPanel.add(createStep8Review(), "STEP8");

        centerWrapper.add(mainCardPanel, BorderLayout.CENTER);
        rootPanel.add(centerWrapper, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 30, 20, 30));

        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLeft.setOpaque(false);

        btnPrev = new CustomButton("KEMBALI", Color.WHITE, clrHijauTua);
        btnPrev.setForeground(Color.BLACK);
        btnPrev.setCustomBorder(clrHijauTua);
        btnPrev.setVisible(false);
        btnPrev.addActionListener(e -> prevStep());

        btnBackToLanding = new CustomButton("KEMBALI KE UTAMA", Color.WHITE, new Color(220, 38, 38));
        btnBackToLanding.setForeground(Color.BLACK);
        btnBackToLanding.setCustomBorder(new Color(220, 38, 38));
        btnBackToLanding.setVisible(true); 
        btnBackToLanding.addActionListener(e -> {
            int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin membatalkan pendaftaran dan kembali ke halaman utama?", 
                "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            if (konfirmasi == JOptionPane.YES_OPTION) {
                this.dispose(); 
                new views.LandingPage().setVisible(true); 
            }
        });

        pnlLeft.add(btnPrev);
        pnlLeft.add(btnBackToLanding);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setOpaque(false);

        btnNext = new CustomButton("SELANJUTNYA", clrHijauTua, Color.WHITE);
        btnNext.setForeground(Color.WHITE);
        btnNext.addActionListener(e -> nextStep());

        btnSubmit = new CustomButton("SUBMIT PENDAFTARAN", clrHijauMain, Color.WHITE);
        btnSubmit.setForeground(Color.BLACK);
        btnSubmit.setVisible(false);
        btnSubmit.setEnabled(false);
        btnSubmit.addActionListener(e -> simpanDataKeDatabase());

        pnlRight.add(btnNext); 
        pnlRight.add(btnSubmit);
        
        footer.add(pnlLeft, BorderLayout.WEST); 
        footer.add(pnlRight, BorderLayout.EAST);
        rootPanel.add(footer, BorderLayout.SOUTH);
        
        views.components.InputValidator.batasiJumlahKarakter(txtNisn, 10);
        views.components.InputValidator.kunciHanyaAngka(txtHp);
        views.components.InputValidator.batasiJumlahKarakter(txtNik, 16);
        views.components.InputValidator.kunciHanyaAngka(txtHpAyah);
        views.components.InputValidator.kunciHanyaAngka(txtHpIbu);
        views.components.InputValidator.kunciHanyaAngka(txtKodePos);
        views.components.InputValidator.kunciHanyaAngka(txtRt);
        views.components.InputValidator.kunciHanyaAngka(txtRw);
        views.components.InputValidator.kunciHanyaAngka(txtNpsn);
        views.components.InputValidator.kunciHanyaAngka(txtThnLulus);
    }

    // <-------------------- WIZARD STEP INDICATOR -------------------->
    private void updateStepIndicator() {
        stepIndicatorPanel.removeAll();
        String[] titles = {"1. Kredensial", "2. Biodata", "3. Jalur", "4. Ortu", "5. Alamat", "6. Sekolah", "7. Berkas", "8. Selesai"};
        for (int i = 1; i <= MAX_STEPS; i++) {
            JLabel l = new JLabel(titles[i-1]);
            l.setFont(new Font("Segoe UI", i == currentStep ? Font.BOLD : Font.PLAIN, 13));
            l.setForeground(i == currentStep ? clrHijauTua : Color.GRAY);
            stepIndicatorPanel.add(l);
        }
        stepIndicatorPanel.revalidate(); stepIndicatorPanel.repaint();
    }

    // <-------------------- STEP BUILDER PANELS -------------------->
    private JPanel createStep1Akun() {
        JPanel p = new JPanel(null); p.setBackground(Color.WHITE);
        JLabel inst = new JLabel("Langkah 1: Buat kredensial untuk memantau kelulusan nanti.");
        inst.setFont(new Font("Segoe UI", Font.ITALIC, 13)); inst.setBounds(30, 20, 500, 20); p.add(inst);

        addLabel(p, "NISN (10 Digit Angka):", 30, 60); txtNisn = addInput(p, 30, 85, 300);
        addLabel(p, "Nomor WhatsApp Aktif:", 375, 60); txtHp = addInput(p, 375, 85, 300);
        addLabel(p, "Buat Kata Sandi (Min. 8 Karakter):", 30, 140);
        txtPassword = new JPasswordField(); txtPassword.setBounds(30, 165, 300, 35);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 220, 220)), new EmptyBorder(0, 10, 0, 10))); p.add(txtPassword);
        addLabel(p, "Konfirmasi Kata Sandi:", 375, 140);
        txtConfirm = new JPasswordField(); txtConfirm.setBounds(375, 165, 300, 35);
        txtConfirm.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 220, 220)), new EmptyBorder(0, 10, 0, 10))); p.add(txtConfirm);
        return p;
    }

    private JPanel createStep2Biodata() {
        JPanel p = new JPanel(null); p.setBackground(Color.WHITE);
        addLabel(p, "NIK Siswa (16 Digit KTP):", 30, 30); txtNik = addInput(p, 30, 55, 300);
        addLabel(p, "Nama Lengkap (Sesuai Akta):", 375, 30); txtNama = addInput(p, 375, 55, 300);
        addLabel(p, "Tempat Lahir:", 30, 110); txtTempatLahir = addInput(p, 30, 135, 300);
        addLabel(p, "Tanggal Lahir (YYYY-MM-DD):", 375, 110); txtTglLahir = addInput(p, 375, 135, 300);
        addLabel(p, "Jenis Kelamin:", 30, 190); cmbJk = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"}); cmbJk.setBounds(30, 215, 300, 35); p.add(cmbJk);
        addLabel(p, "Agama:", 375, 190); cmbAgama = new JComboBox<>(new String[]{"Islam", "Lainnya"}); cmbAgama.setBounds(375, 215, 300, 35); p.add(cmbAgama);
        addLabel(p, "Alamat Email (Opsional):", 30, 270); txtEmail = addInput(p, 30, 295, 300);
        return p;
    }

    private JPanel createStep3Jalur() {
        JPanel p = new JPanel(null); p.setBackground(Color.WHITE);
        addLabel(p, "Pilih Jalur Pendaftaran yang Sesuai:", 30, 30);
        bgJalur = new ButtonGroup();
        String[] jalurs = {"Domisili", "Prestasi", "Afirmasi", "Mutasi", "Yatim"};
        int y = 60;
        for (String j : jalurs) {
            JRadioButton rb = new JRadioButton(j);
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 14)); rb.setBackground(Color.WHITE);
            rb.setBounds(30, y, 150, 30);
            if (j.equals("Domisili")) rb.setSelected(true);
            rb.addActionListener(e -> {
                selectedJalur = j;
                panelYatimOption.setVisible(j.equals("Yatim"));
                buildDokumenDinamis();
            });
            bgJalur.add(rb); p.add(rb); y += 40;
        }
        
        panelYatimOption = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelYatimOption.setBackground(Color.WHITE); panelYatimOption.setBounds(30, y, 400, 40);
        panelYatimOption.setVisible(false);
        panelYatimOption.add(new JLabel("Status Ayah: "));
        rbAyahHidup = new JRadioButton("Masih Hidup", true); rbAyahHidup.setBackground(Color.WHITE);
        rbAyahMeninggal = new JRadioButton("Meninggal Dunia"); rbAyahMeninggal.setBackground(Color.WHITE);
        ButtonGroup bgYatim = new ButtonGroup(); bgYatim.add(rbAyahHidup); bgYatim.add(rbAyahMeninggal);
        rbAyahMeninggal.addActionListener(e -> buildDokumenDinamis());
        rbAyahHidup.addActionListener(e -> buildDokumenDinamis());
        panelYatimOption.add(rbAyahHidup); panelYatimOption.add(rbAyahMeninggal);
        p.add(panelYatimOption);
        return p;
    }

    private JPanel createStep4Ortu() {
        JPanel p = new JPanel(null); p.setBackground(Color.WHITE);
        addLabel(p, "Nama Lengkap Ayah:", 30, 30); txtAyah = addInput(p, 30, 55, 300);
        addLabel(p, "Pekerjaan Ayah:", 375, 30); txtKerjaAyah = addInput(p, 375, 55, 300);
        addLabel(p, "No HP Ayah:", 30, 110); txtHpAyah = addInput(p, 30, 135, 300);
        addLabel(p, "Nama Lengkap Ibu:", 375, 110); txtIbu = addInput(p, 375, 135, 300);
        addLabel(p, "Pekerjaan Ibu:", 30, 190); txtKerjaIbu = addInput(p, 30, 215, 300);
        addLabel(p, "No HP Ibu:", 375, 190); txtHpIbu = addInput(p, 375, 215, 300);
        addLabel(p, "Total Penghasilan Ortu per Bulan:", 30, 270);
        cmbPenghasilan = new JComboBox<>(new String[]{"< 1 juta", "1 - 3 juta", "3 - 5 juta", "5 - 10 juta", "> 10 juta"});
        cmbPenghasilan.setBounds(30, 295, 300, 35); p.add(cmbPenghasilan);
        return p;
    }

    private JPanel createStep5Alamat() {
        JPanel p = new JPanel(null); p.setBackground(Color.WHITE);
        
        addLabel(p, "Provinsi:", 30, 30); txtProvinsi = addInput(p, 30, 55, 300);
        addLabel(p, "Kabupaten / Kota:", 375, 30); txtKabupaten = addInput(p, 375, 55, 300);
        
        addLabel(p, "Kecamatan:", 30, 110); txtKecamatan = addInput(p, 30, 135, 300);
        addLabel(p, "Kelurahan / Desa:", 375, 110); txtKelurahan = addInput(p, 375, 135, 300);
        
        addLabel(p, "Kode Pos:", 30, 190); 
        txtKodePos = addInput(p, 30, 215, 120);
        
        addLabel(p, "RT:", 170, 190); 
        txtRt = addInput(p, 170, 215, 70);     
        
        addLabel(p, "RW:", 260, 190); 
        txtRw = addInput(p, 260, 215, 70);     
        
        addLabel(p, "Nama Jalan / Nomor Rumah:", 30, 270);
        txtDetailAlamat = new JTextArea(); txtDetailAlamat.setBorder(new LineBorder(new Color(220, 220, 220)));
        txtDetailAlamat.setFont(FONT_BODY);
        JScrollPane sc = new JScrollPane(txtDetailAlamat); sc.setBounds(30, 295, 645, 60); p.add(sc);
        
        return p;
    }

    private JPanel createStep6Sekolah() {
        JPanel p = new JPanel(null); p.setBackground(Color.WHITE);
        addLabel(p, "Nama Sekolah Asal (SD/MI):", 30, 30); txtNamaSekolah = addInput(p, 30, 55, 300);
        addLabel(p, "NPSN Sekolah:", 375, 30); txtNpsn = addInput(p, 375, 55, 300);
        addLabel(p, "Tahun Lulus:", 30, 110); txtThnLulus = addInput(p, 30, 135, 300);
        addLabel(p, "Alamat Sekolah Asal:", 30, 190); txtAlamatSekolah = addInput(p, 30, 215, 645);
        return p;
    }

    private JPanel createStep7Dokumen() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(Color.WHITE);
        JLabel lblTop = new JLabel("  Unggah Dokumen Wajib (PDF/JPG/PNG - Max 5MB)");
        lblTop.setFont(FONT_HEADER); lblTop.setForeground(clrHijauTua); lblTop.setPreferredSize(new Dimension(0, 40));
        p.add(lblTop, BorderLayout.NORTH);
        
        panelDokumenDinamis = new JPanel();
        panelDokumenDinamis.setLayout(new BoxLayout(panelDokumenDinamis, BoxLayout.Y_AXIS));
        panelDokumenDinamis.setBackground(Color.WHITE);
        p.add(new JScrollPane(panelDokumenDinamis), BorderLayout.CENTER);
        buildDokumenDinamis();
        return p;
    }

    private void buildDokumenDinamis() {
        panelDokumenDinamis.removeAll();
        addUploadRow("Kartu Keluarga"); addUploadRow("Akta Kelahiran");
        addUploadRow("Pas Foto"); addUploadRow("Ijazah / SKL");
        addUploadRow("Rapor Semester 5"); addUploadRow("Rapor Semester 6");

        if (selectedJalur.equals("Prestasi")) { addUploadRow("Sertifikat Prestasi"); } 
        else if (selectedJalur.equals("Afirmasi")) { addUploadRow("KIP / PKH"); addUploadRow("SKTM"); } 
        else if (selectedJalur.equals("Mutasi")) { addUploadRow("Surat Mutasi Orang Tua"); } 
        else if (selectedJalur.equals("Yatim")) {
            if (rbAyahMeninggal.isSelected()) addUploadRow("Surat Kematian Ayah");
            addUploadRow("SKTM (Jalur Yatim)"); addUploadRow("KIP / PKH (Opsional)");
        }
        panelDokumenDinamis.revalidate(); panelDokumenDinamis.repaint();
    }

    private void addUploadRow(String docName) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE); row.setMaximumSize(new Dimension(800, 45));
        row.setBorder(new MatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

        JLabel lblName = new JLabel("  " + docName); lblName.setFont(FONT_BODY);
        JLabel lblStatus = new JLabel(uploadedFiles.containsKey(docName) ? "Terlampir" : "Belum Diunggah");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(uploadedFiles.containsKey(docName) ? HIJAU : MERAH);

        JButton btnUp = new JButton("Pilih File..."); btnUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUp.setForeground(Color.BLACK);
        btnUp.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileFilter(new FileNameExtensionFilter("Image & PDF", "jpg", "jpeg", "png", "pdf"));
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = jfc.getSelectedFile();
                if (f.length() > 5 * 1024 * 1024) { JOptionPane.showMessageDialog(this, "Ukuran maksimal 5MB!"); return; }
                uploadedFiles.put(docName, f);
                lblStatus.setText(f.getName()); lblStatus.setForeground(HIJAU);
            }
        });
        row.add(lblName, BorderLayout.WEST); row.add(lblStatus, BorderLayout.CENTER); row.add(btnUp, BorderLayout.EAST);
        panelDokumenDinamis.add(row);
    }

    private JPanel createStep8Review() {
        JPanel p = new JPanel(new BorderLayout(10, 10)); p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        txtReview = new JTextArea(); txtReview.setEditable(false); txtReview.setFont(new Font("Monospaced", Font.PLAIN, 13));
        p.add(new JScrollPane(txtReview), BorderLayout.CENTER);

        JPanel pnlCheck = new JPanel(new GridLayout(2, 1)); pnlCheck.setOpaque(false);
        chkBenar = new JCheckBox("Saya menyatakan seluruh data dan dokumen yang dilampirkan adalah benar dan asli.");
        chkBenar.setFont(FONT_BODY); chkBenar.setBackground(Color.WHITE);
        chkSetuju = new JCheckBox("Saya bersedia didiskualifikasi jika di kemudian hari ditemukan kecurangan data.");
        chkSetuju.setFont(FONT_BODY); chkSetuju.setBackground(Color.WHITE);
        pnlCheck.add(chkBenar); pnlCheck.add(chkSetuju);
        chkBenar.addActionListener(e -> btnSubmit.setEnabled(chkBenar.isSelected() && chkSetuju.isSelected()));
        chkSetuju.addActionListener(e -> btnSubmit.setEnabled(chkBenar.isSelected() && chkSetuju.isSelected()));
        p.add(pnlCheck, BorderLayout.SOUTH);
        return p;
    }

    // <-------------------- WIZARD NAVIGATION LOGIC -------------------->
    private void nextStep() {
        if (!validateCurrentStep()) return;
        if (currentStep < MAX_STEPS) {
            currentStep++;
            cardLayout.show(mainCardPanel, "STEP" + currentStep);
            btnPrev.setVisible(true);
            if (currentStep == MAX_STEPS) {
                btnNext.setVisible(false); btnSubmit.setVisible(true); generateReviewText();
            }
            updateStepIndicator();
        }
    }

    private void prevStep() {
        if (currentStep > 1) {
            currentStep--;
            cardLayout.show(mainCardPanel, "STEP" + currentStep);
            btnNext.setVisible(true); btnSubmit.setVisible(false);
            if (currentStep == 1) btnPrev.setVisible(false);
            updateStepIndicator();
        }
    }

    // <-------------------- VALIDASI FORM WIZARD -------------------->
    private boolean validateCurrentStep() {
        try {
            if (currentStep == 1) {
                if (txtNisn.getText().length() != 10) { showError("Format NISN harus tepat 10 digit!"); return false; }
                if (txtHp.getText().isEmpty()) { showError("Nomor WhatsApp wajib diisi!"); return false; }
                if (txtPassword.getPassword().length < 8) { showError("Password minimal 8 karakter!"); return false; }
                String p1 = new String(txtPassword.getPassword()); String p2 = new String(txtConfirm.getPassword());
                if (!p1.equals(p2)) { showError("Password konfirmasi tidak cocok!"); return false; }
                if (isExistsInDb("tbl_biodata_siswa", "nisn", txtNisn.getText())) { showError("NISN sudah terdaftar!"); return false; }
                if (isExistsInDb("tbl_users", "no_hp", txtHp.getText())) { showError("Nomor WA sudah terdaftar!"); return false; }
            } else if (currentStep == 2) {
                String nikSiswa = txtNik.getText().trim();
                if (nikSiswa.length() != 16) { 
                    showError("Format NIK harus tepat 16 digit!\nDigit Anda saat ini: " + nikSiswa.length() + " digit."); 
                    txtNik.requestFocus(); 
                    return false; 
                }
                if (txtNama.getText().isEmpty()) { showError("Nama Lengkap wajib diisi!"); return false; }
                if (isExistsInDb("tbl_biodata_siswa", "nik", txtNik.getText())) { showError("NIK sudah terdaftar!"); return false; }
            }
        } catch (Exception e) { showError("Database Error: " + e.getLocalizedMessage()); return false; }
        return true;
    }

    private void generateReviewText() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RINGKASAN FORMULIR PENDAFTARAN ===\n\n");
        
        // Membaca teks langsung dari field Kredensial Langkah 1
        String nomorWhatsAppAktif = txtHp.getText().trim();
        String nomorNisnAktif = txtNisn.getText().trim();
        
        sb.append("[AKUN LOGIN]\n");
        sb.append("NISN    : ").append(nomorNisnAktif.isEmpty() ? "-" : nomorNisnAktif).append("\n");
        sb.append("WhatsApp: ").append(nomorWhatsAppAktif.isEmpty() ? "-" : nomorWhatsAppAktif).append("\n\n");
        
        // Membaca teks langsung dari field Biodata Langkah 2
        String nomorNikAktif = txtNik.getText().trim();
        String namaSiswaAktif = txtNama.getText().trim();
        
        sb.append("[IDENTITAS DIRI]\n");
        sb.append("NIK       : ").append(nomorNikAktif.isEmpty() ? "-" : nomorNikAktif).append("\n");
        sb.append("Nama Siswa: ").append(namaSiswaAktif.isEmpty() ? "-" : namaSiswaAktif).append("\n\n");
        
        sb.append("[JALUR PENERIMAAN]\n");
        sb.append("Pilihan Jalur: ").append(selectedJalur).append("\n\n");
        
        sb.append("[STATUS DOKUMEN]\n");
        if (uploadedFiles.isEmpty()) {
            sb.append("Belum ada dokumen yang dilampirkan.\n");
        } else {
            for (String k : uploadedFiles.keySet()) { 
                sb.append("- ").append(k).append(" : ").append(uploadedFiles.get(k).getName()).append("\n"); 
            }
        }
        
        txtReview.setText(sb.toString());
    }

    private boolean isExistsInDb(String table, String col, String val) throws SQLException {
        try (Connection c = DatabaseConfig.getKoneksi();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM " + table + " WHERE " + col + " = ? LIMIT 1")) {
            ps.setString(1, val); try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Validasi", JOptionPane.ERROR_MESSAGE); }

    private void addLabel(JPanel panel, String text, int x, int y) {
        JLabel label = new JLabel(text); label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(clrHijauTua); label.setBounds(x, y, 250, 20); panel.add(label);
    }

    private JTextField addInput(JPanel panel, int x, int y, int width) {
        JTextField field = new JTextField(); field.setBounds(x, y, width, 35);
        field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 220, 220)), new EmptyBorder(0, 10, 0, 10)));
        panel.add(field); return field;
    }

    // <-------------------- DATABASE ACTIONS -------------------->
    /**
     * Menyimpan seluruh berkas formulir dan file biner pendaftar secara transaksional aman (Atomic Commit).
     */
    private void simpanDataKeDatabase() {
        String nomorDaftar = "REG-" + System.currentTimeMillis(); 
        String hashedPass = BCrypt.hashpw(new String(txtPassword.getPassword()), BCrypt.gensalt());

        String inputEmail = txtEmail.getText().trim();
        if (inputEmail.isEmpty()) {
            inputEmail = nomorDaftar.toLowerCase() + "@siswa.lokal";
        }

        try (Connection conn = DatabaseConfig.getKoneksi()) {
            conn.setAutoCommit(false); 

            try {
                int idUser = 0;
                String sqlUser = "INSERT INTO tbl_users (username, password_hash, nama_lengkap, email, no_hp, role, status) VALUES (?, ?, ?, ?, ?, 'CALON_SISWA', 'AKTIF')";
                try (PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, nomorDaftar); ps.setString(2, hashedPass); ps.setString(3, txtNama.getText());
                    ps.setString(4, inputEmail); 
                    ps.setString(5, txtHp.getText()); ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) idUser = rs.getInt(1); }
                }

                int idSiswa = 0;
                String sqlSiswa = 
                        "INSERT INTO tbl_siswa (nomor_pendaftaran, id_tahun, id_gelombang, id_jalur, status_pendaftaran) " +
                        "VALUES (?, " +
                        "        (SELECT id_tahun FROM tbl_tahun_ajaran WHERE status_aktif = 1 LIMIT 1), " +
                        "        (SELECT id_gelombang FROM tbl_gelombang WHERE UPPER(status) IN ('AKTIF', 'BUKA') LIMIT 1), " +
                        "        (SELECT id_jalur FROM tbl_jalur WHERE nama_jalur = ? LIMIT 1), " +
                        "        'Pending')";
                try (PreparedStatement ps = conn.prepareStatement(sqlSiswa, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, nomorDaftar); 
                    ps.setString(2, selectedJalur); 
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) idSiswa = rs.getInt(1); }
                }

                String sqlBio = "INSERT INTO tbl_biodata_siswa (id_siswa, nik, nisn, nama_lengkap, tempat_lahir, tanggal_lahir, jenis_kelamin, agama, email, nomor_hp) VALUES (?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlBio)) {
                    ps.setInt(1, idSiswa); ps.setString(2, txtNik.getText()); ps.setString(3, txtNisn.getText()); ps.setString(4, txtNama.getText()); ps.setString(5, txtTempatLahir.getText()); ps.setString(6, txtTglLahir.getText()); ps.setString(7, cmbJk.getSelectedItem().toString()); ps.setString(8, cmbAgama.getSelectedItem().toString()); 
                    ps.setString(9, inputEmail); 
                    ps.setString(10, txtHp.getText()); ps.executeUpdate();
                }

                String sqlOrtu = "INSERT INTO tbl_orang_tua (id_siswa, nama_ayah, pekerjaan_ayah, hp_ayah, nama_ibu, pekerjaan_ibu, hp_ibu, penghasilan_ayah) VALUES (?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlOrtu)) {
                    ps.setInt(1, idSiswa); ps.setString(2, txtAyah.getText()); ps.setString(3, txtKerjaAyah.getText()); ps.setString(4, txtHpAyah.getText()); ps.setString(5, txtIbu.getText()); ps.setString(6, txtKerjaIbu.getText()); ps.setString(7, txtHpIbu.getText()); ps.setString(8, cmbPenghasilan.getSelectedItem().toString()); ps.executeUpdate();
                }

                String sqlAlamat = "INSERT INTO tbl_alamat (id_siswa, provinsi, kabupaten, kecamatan, kelurahan, kode_pos, rt, rw, alamat_lengkap) VALUES (?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlAlamat)) {
                    ps.setInt(1, idSiswa); 
                    ps.setString(2, txtProvinsi.getText().trim()); 
                    ps.setString(3, txtKabupaten.getText().trim()); 
                    ps.setString(4, txtKecamatan.getText().trim()); 
                    ps.setString(5, txtKelurahan.getText().trim()); 
                    ps.setString(6, txtKodePos.getText().trim());
                    
                    ps.setString(7, txtRt.getText().trim()); 
                    ps.setString(8, txtRw.getText().trim()); 
                    
                    ps.setString(9, txtDetailAlamat.getText().trim()); 
                    ps.executeUpdate();
                }

                String sqlSekolah = "INSERT INTO tbl_sekolah_asal (id_siswa, nama_sekolah, npsn, alamat, tahun_lulus) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlSekolah)) {
                    ps.setInt(1, idSiswa); ps.setString(2, txtNamaSekolah.getText()); ps.setString(3, txtNpsn.getText()); ps.setString(4, txtAlamatSekolah.getText()); ps.setString(5, txtThnLulus.getText()); ps.executeUpdate();
                }

                File upDir = new File("uploads"); if (!upDir.exists()) upDir.mkdir();
                String sqlBerkas = "INSERT INTO tbl_berkas (id_siswa, jenis_berkas, nama_file_asli, nama_file_server, ukuran_file, status) VALUES (?,?,?,?,?, 'MENUNGGU_VERIFIKASI')";
                try (PreparedStatement ps = conn.prepareStatement(sqlBerkas)) {
                    for (Map.Entry<String, File> entry : uploadedFiles.entrySet()) {
                        File src = entry.getValue(); String namaServer = "FILE_" + System.currentTimeMillis() + "_" + src.getName();
                        copyFileFast(src, new File(upDir, namaServer)); 
                        ps.setInt(1, idSiswa); ps.setString(2, entry.getKey()); ps.setString(3, src.getName()); ps.setString(4, namaServer); ps.setLong(5, src.length()); ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO tbl_seleksi (id_siswa, status_kelulusan) VALUES (?, 'PROSES')")) { ps.setInt(1, idSiswa); ps.executeUpdate(); }
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO tbl_notifikasi (id_user, judul, pesan) VALUES (?, 'Selamat Datang', 'Pendaftaran Anda berhasil direkam.')")) { ps.setInt(1, idUser); ps.executeUpdate(); }

                conn.commit(); 
                
                JOptionPane.showMessageDialog(this, "PENDAFTARAN BERHASIL!\n\nNomor Pendaftaran: " + nomorDaftar + "\nNISN: " + txtNisn.getText() + "\nWhatsApp: " + txtHp.getText() + "\n\nAnda dapat LOGIN menggunakan salah satu dari tiga data di atas bersama password Anda.", "Pendaftaran Sukses", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); new LandingPage().setVisible(true);

            } catch (Exception ex) {
                conn.rollback(); showError("Gagal Mendaftar! " + ex.getLocalizedMessage());
            } finally { conn.setAutoCommit(true); }
        } catch (Exception e) { showError("Database Error: " + e.getLocalizedMessage()); }
    }

    private void copyFileFast(File src, File dest) throws Exception {
        try (FileChannel s = new FileInputStream(src).getChannel(); FileChannel d = new FileOutputStream(dest).getChannel()) { d.transferFrom(s, 0, s.size()); }
    }
}
