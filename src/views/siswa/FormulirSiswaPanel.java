package views.siswa;

import config.DatabaseConfig;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Pemrosesan Formulir Mandiri Calon Siswa (Anti Duplicate Insert Engine).
 * Menangani pembaruan data biodata, orang tua, dan alamat secara aman.
 * * @author Rivaldi
 */
public class FormulirSiswaPanel extends JPanel {

    // <-------------------- SESSION & STATE -------------------->
    private final int idSiswa;

    // <-------------------- KOMPONEN FORM -------------------->
    private JTextField txtNama;
    private JTextField txtNik;
    private JTextField txtNisn;
    private JTextField txtEmail;
    private JTextField txtHp;
    private JTextField txtAgama;
    private JTextField txtAyah;
    private JTextField txtIbu;
    private JTextField txtHpAyah;
    private JTextField txtProv;
    private JTextField txtKab;
    private JTextField txtKec;
    private JTextField txtKel;
    private JButton btnSave;
    private JButton btnLihat;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari FormulirSiswaPanel dan memuat data awal.
     * * @param idSiswa id unik dari siswa pendaftar
     */
    public FormulirSiswaPanel(int idSiswa) {
        this.idSiswa = idSiswa;
        initUI();
        muatDataFormulirDariDatabase();
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    /**
     * Merender dan mengonfigurasi tata letak komponen utama antarmuka formulir siswa.
     */
    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // <-------------------- BARIS HEADER CONTROL -------------------->
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel title = new JLabel("Isian Formulir Biodata & Registrasi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(title, BorderLayout.WEST);

        btnLihat = new JButton("Lihat Data Formulir Terinput");
        btnLihat.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLihat.setForeground(Color.BLACK);
        btnLihat.addActionListener(e -> aksiPopUpLihatDataFormulir());
        pnlHeader.add(btnLihat, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // <-------------------- INPUT GRID SCROLL CONTAINER -------------------->
        JPanel pnlGrid = new JPanel(new GridLayout(0, 2, 16, 16));
        pnlGrid.setBackground(Color.WHITE);
        pnlGrid.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        pnlGrid.add(new JLabel("Nama Lengkap Siswa:")); txtNama = new JTextField(); pnlGrid.add(txtNama);
        
        pnlGrid.add(new JLabel("NIK Core (Read Only):")); txtNik = new JTextField(); txtNik.setEditable(false); txtNik.setBackground(new Color(241, 245, 249)); pnlGrid.add(txtNik);
        pnlGrid.add(new JLabel("NISN Nomor Induk (Read Only):")); txtNisn = new JTextField(); txtNisn.setEditable(false); txtNisn.setBackground(new Color(241, 245, 249)); pnlGrid.add(txtNisn);
        
        pnlGrid.add(new JLabel("Email Korespondensi :")); txtEmail = new JTextField(); pnlGrid.add(txtEmail);
        pnlGrid.add(new JLabel("Nomor HP Aktif Siswa :")); txtHp = new JTextField(); pnlGrid.add(txtHp);
        pnlGrid.add(new JLabel("Agama :")); txtAgama = new JTextField(); pnlGrid.add(txtAgama);

        pnlGrid.add(new JLabel("Nama Ayah Kandung :")); txtAyah = new JTextField(); pnlGrid.add(txtAyah);
        pnlGrid.add(new JLabel("No HP Kontak Ayah :")); txtHpAyah = new JTextField(); pnlGrid.add(txtHpAyah);
        pnlGrid.add(new JLabel("Nama Ibu Kandung  :")); txtIbu = new JTextField(); pnlGrid.add(txtIbu);

        pnlGrid.add(new JLabel("Provinsi Domisili :")); txtProv = new JTextField(); pnlGrid.add(txtProv);
        pnlGrid.add(new JLabel("Kabupaten/Kota    :")); txtKab = new JTextField(); pnlGrid.add(txtKab);
        pnlGrid.add(new JLabel("Kecamatan Tempat Tinggal :")); txtKec = new JTextField(); pnlGrid.add(txtKec);
        pnlGrid.add(new JLabel("Desa / Kelurahan :")); txtKel = new JTextField(); pnlGrid.add(txtKel);

        JScrollPane scroll = new JScrollPane(pnlGrid);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // <-------------------- SUBMIT CONTROL -------------------->
        btnSave = new JButton("Simpan Pembaruan Formulir (UPDATE)");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(52, 152, 219));
        btnSave.setForeground(Color.BLACK);
        btnSave.setPreferredSize(new Dimension(0, 42));
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> eksekusiUpdateStrictDatabase());
        add(btnSave, BorderLayout.SOUTH);
    }

    // <-------------------- DATABASE QUERY -------------------->
    /**
     * Menarik isian rekaman formulir pendaftaran siswa dari server database MySQL.
     */
    private void muatDataFormulirDariDatabase() {
        try (Connection conn = DatabaseConfig.getKoneksi()) {
            String sqlBio = "SELECT nama_lengkap, nik, nisn, email, nomor_hp, agama FROM biodata_siswa WHERE id_pendaftaran = (SELECT id_pendaftaran FROM tbl_siswa WHERE id_siswa = ? LIMIT 1) LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sqlBio)) {
                ps.setInt(1, idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        txtNama.setText(rs.getString("nama_lengkap"));
                        txtNik.setText(rs.getString("nik"));
                        txtNisn.setText(rs.getString("nisn"));
                        txtEmail.setText(rs.getString("email"));
                        txtHp.setText(rs.getString("nomor_hp"));
                        txtAgama.setText(rs.getString("agama"));
                    }
                }
            }
            
            String sqlOrtu = "SELECT nama_ayah, hp_ayah, nama_ibu FROM tbl_orang_tua WHERE id_siswa = ? LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sqlOrtu)) {
                ps.setInt(1, idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        txtAyah.setText(rs.getString("nama_ayah"));
                        txtHpAyah.setText(rs.getString("hp_ayah"));
                        txtIbu.setText(rs.getString("nama_ibu"));
                    }
                }
            }
            
            String sqlAlamat = "SELECT provinsi, kabupaten, kecamatan, kelurahan FROM tbl_alamat WHERE id_siswa = ? LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sqlAlamat)) {
                ps.setInt(1, idSiswa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        txtProv.setText(rs.getString("provinsi"));
                        txtKab.setText(rs.getString("kabupaten"));
                        txtKec.setText(rs.getString("kecamatan"));
                        txtKel.setText(rs.getString("kelurahan"));
                    }
                }
            }
        } catch (SQLException e) {
            // Kronologi log kegagalan internal sistem diredam aman
        }
    }

    // <-------------------- PROSES UPDATE DATA -------------------->
    /**
     * Mengeksekusi rangkaian query pembaruan massal di database secara transaksional aman.
     */
    private void eksekusiUpdateStrictDatabase() {
        try (Connection conn = DatabaseConfig.getKoneksi()) {
            conn.setAutoCommit(false); 

            String sql1 = "UPDATE biodata_siswa SET nama_lengkap = ?, email = ?, nomor_hp = ?, agama = ? WHERE id_pendaftaran = (SELECT id_pendaftaran FROM tbl_siswa WHERE id_siswa = ? LIMIT 1)";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setString(1, txtNama.getText().trim());
                ps.setString(2, txtEmail.getText().trim());
                ps.setString(3, txtHp.getText().trim());
                ps.setString(4, txtAgama.getText().trim());
                ps.setInt(5, idSiswa);
                ps.executeUpdate();
            }

            String sql2 = "UPDATE tbl_orang_tua SET nama_ayah = ?, hp_ayah = ?, nama_ibu = ? WHERE id_siswa = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setString(1, txtAyah.getText().trim());
                ps.setString(2, txtHpAyah.getText().trim());
                ps.setString(3, txtIbu.getText().trim());
                ps.setInt(4, idSiswa);
                ps.executeUpdate();
            }

            String sql3 = "UPDATE tbl_alamat SET provinsi = ?, kabupaten = ?, kecamatan = ?, kelurahan = ? WHERE id_siswa = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql3)) {
                ps.setString(1, txtProv.getText().trim());
                ps.setString(2, txtKab.getText().trim());
                ps.setString(3, txtKec.getText().trim());
                ps.setString(4, txtKel.getText().trim());
                ps.setInt(5, idSiswa);
                ps.executeUpdate();
            }

            conn.commit(); 
            JOptionPane.showMessageDialog(this, "Seluruh isian data formulir pendaftaran berhasil di-update ke server MySQL!", "Update Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memproses update formulir pendaftaran: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // <-------------------- HELPER METHOD -------------------->
    private void aksiPopUpLihatDataFormulir() {
        String summary = "=== RINGKASAN DATA FORMULIR INPUT ===\n\n"
                       + "Nama Lengkap : " + txtNama.getText() + "\n"
                       + "NIK Pendaftar: " + txtNik.getText() + "\n"
                       + "NISN Asal    : " + txtNisn.getText() + "\n"
                       + "Email Kontak : " + txtEmail.getText() + "\n"
                       + "No Handphone : " + txtHp.getText() + "\n"
                       + "Nama Ayah    : " + txtAyah.getText() + "\n"
                       + "Domisili     : " + txtKel.getText() + ", " + txtKec.getText() + ", " + txtKab.getText();
        
        JTextArea area = new JTextArea(summary);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Review Isian Berkas Fisik", JOptionPane.INFORMATION_MESSAGE);
    }
}