package views.admin;

import config.DatabaseConfig;
import controllers.SeleksiOtomatisController;
import views.components.MetricCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Engine seleksi & kelulusan dengan fitur klik ganda Form Input Komponen Nilai Realistis.
 * Terintegrasi penuh secara transaksional dengan tabel tbl_seleksi dan tbl_siswa.
 * * @author Rivaldi
 */
public class PengaturanKuotaPanel extends JPanel {

    // <-------------------- CONTROLLER -------------------->
    private final SeleksiOtomatisController seleksiController;

    // <-------------------- KOMPONEN UI: METRIK -------------------->
    private final MetricCard cardTotalPendaftar;
    private final MetricCard cardDiterima;
    private final MetricCard cardCadangan;
    private final MetricCard cardSisaKuota;

    // <-------------------- KOMPONEN UI: FORM & TABEL -------------------->
    private DefaultTableModel modelSeleksi;
    private JTable tblSeleksi;
    private JComboBox<String> cmbStatusFilter;
    private JSpinner spinPassingGrade;
    private JButton btnReload;
    private JButton btnJalankanSeleksi;
    private JButton btnSetDiterima;
    private JButton btnSetCadangan;
    private JButton btnSetDitolak;

    // <-------------------- KOMPONEN UI: DETAIL LABEL -------------------->
    private JLabel lblDetailNama;
    private JLabel lblDetailTotalNilai;
    private JLabel lblDetailRanking;
    private JLabel lblDetailStatus;
    private JLabel lblDetailSisaKuota;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari PengaturanKuotaPanel dan menginisialisasi nilai awal metrik.
     */
    public PengaturanKuotaPanel() {
        seleksiController = new SeleksiOtomatisController();

        cardTotalPendaftar = new MetricCard("Total Pendaftar", "Memuat...", new Color(37, 99, 235));
        cardDiterima = new MetricCard("Total Diterima", "Memuat...", new Color(16, 185, 129));
        cardCadangan = new MetricCard("Cadangan", "Memuat...", new Color(245, 158, 11));
        cardSisaKuota = new MetricCard("Sisa Kuota", "Memuat...", new Color(168, 85, 247));

        initPanelLayout();
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    /**
     * Mengonfigurasi tata letak visual utama pembentuk struktur panel dashboard.
     */
    private void initPanelLayout() {
        setLayout(new BorderLayout(18, 18));
        setBackground(new Color(244, 246, 249));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        muatSeluruhData();
    }

    /**
     * Membangun komponen panel header bilah atas beserta tombol sinkronisasi penyegaran.
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel lblJudul = new JLabel("ENGINE SELEKSI & KELULUSAN");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblJudul.setForeground(new Color(20, 23, 29));

        JLabel lblSubjudul = new JLabel("Ringkasan metrik, pratinjau hasil, dan eksekusi mesin seleksi otomatis untuk admin.");
        lblSubjudul.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubjudul.setForeground(new Color(82, 94, 110));

        titleBox.add(lblJudul);
        titleBox.add(Box.createRigidArea(new Dimension(0, 8)));
        titleBox.add(lblSubjudul);

        btnReload = new JButton("Segarkan Data");
        btnReload.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReload.setBackground(new Color(37, 99, 235));
        btnReload.setForeground(Color.BLACK);
        btnReload.setFocusPainted(false);
        btnReload.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        
        btnReload.addActionListener((ActionEvent e) -> {
            muatSeluruhData(); 
            JOptionPane.showMessageDialog(this, 
                "Tampilan berhasil disegarkan! Urutan ranking telah disesuaikan dengan nilai terbaru.", 
                "Sistem Disegarkan", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        header.add(titleBox, BorderLayout.WEST);
        header.add(btnReload, BorderLayout.EAST);
        return header;
    }

    /**
     * Menyatukan baris metrik data analitik dan split pane panel tengah.
     */
    private JPanel createMainContentPanel() {
        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setOpaque(false);

        content.add(createMetricRow(), BorderLayout.NORTH);
        content.add(createCenterSplitPane(), BorderLayout.CENTER);
        return content;
    }

    private JPanel createMetricRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.add(cardTotalPendaftar);
        row.add(cardDiterima);
        row.add(cardCadangan);
        row.add(cardSisaKuota);
        return row;
    }

    private JSplitPane createCenterSplitPane() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createTablePanel(), createDetailPanel());
        split.setResizeWeight(0.65);
        split.setOpaque(false);
        split.setBorder(null);
        return split;
    }

    /**
     * Membangun sub-panel tabel visualisasi data seleksi siswa beserta filternya.
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel label = new JLabel("Hasil Seleksi Saat Ini");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(20, 23, 29));
        panel.add(label, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatusFilter = new JComboBox<>(new String[]{"SEMUA", "DITERIMA", "CADANGAN", "TIDAK_DITERIMA", "PROSES"});
        cmbStatusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatusFilter.addActionListener((ActionEvent e) -> muatTabelSeleksi());

        toolbar.add(lblFilter);
        toolbar.add(cmbStatusFilter);
        panel.add(toolbar, BorderLayout.SOUTH);

        modelSeleksi = new DefaultTableModel(new String[]{"#", "ID Seleksi", "ID Siswa", "Nama", "Total Nilai", "Ranking", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSeleksi = new JTable(modelSeleksi);
        tblSeleksi.setRowHeight(35);
        tblSeleksi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblSeleksi.getTableHeader().setReorderingAllowed(false);
        
        tblSeleksi.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                
                if (c != 3) { 
                    setHorizontalAlignment(JLabel.CENTER);
                } else {
                    setHorizontalAlignment(JLabel.LEFT); 
                }
                
                if (!isS) {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    comp.setForeground(new Color(15, 23, 42));
                    
                    if (c == 6 && v != null) {
                        String statusStr = v.toString();
                        if (statusStr.equals("DITERIMA")) { 
                            comp.setBackground(new Color(220, 252, 231)); 
                            comp.setForeground(new Color(21, 128, 61)); 
                        } else if (statusStr.equals("CADANGAN")) { 
                            comp.setBackground(new Color(254, 243, 199)); 
                            comp.setForeground(new Color(180, 83, 9)); 
                        } else if (statusStr.equals("TIDAK_DITERIMA")) { 
                            comp.setBackground(new Color(254, 226, 226)); 
                            comp.setForeground(new Color(185, 28, 28)); 
                        }
                    }
                } else {
                    comp.setBackground(new Color(219, 234, 254)); 
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        });

        tblSeleksi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    muatDetailSeleksi();
                }
            }
        });

        tblSeleksi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblSeleksi.getSelectedRow() != -1) {
                    int selectedRow = tblSeleksi.getSelectedRow();
                    int idSeleksi = Integer.parseInt(modelSeleksi.getValueAt(selectedRow, 1).toString());
                    String namaSiswa = modelSeleksi.getValueAt(selectedRow, 3).toString();
                    bukaDialogFormPenilaianManual(idSeleksi, namaSiswa);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblSeleksi);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Membangun struktur panel detail tinjauan ringkasan serta tombol keputusan manual.
     */
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel label = new JLabel("Ringkasan Seleksi dan Kontrol Manual");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(20, 23, 29));
        panel.add(label, BorderLayout.NORTH);

        JPanel detail = new JPanel();
        detail.setOpaque(false);
        detail.setLayout(new GridLayout(5, 2, 8, 8));

        detail.add(createSmallLabel("Nama Calon:"));
        lblDetailNama = createValueLabel();
        detail.add(lblDetailNama);

        detail.add(createSmallLabel("Total Nilai:"));
        lblDetailTotalNilai = createValueLabel();
        detail.add(lblDetailTotalNilai);

        detail.add(createSmallLabel("Ranking:"));
        lblDetailRanking = createValueLabel();
        detail.add(lblDetailRanking);

        detail.add(createSmallLabel("Status Sekarang:"));
        lblDetailStatus = createValueLabel();
        detail.add(lblDetailStatus);

        detail.add(createSmallLabel("Sisa Kuota Total:"));
        lblDetailSisaKuota = createValueLabel();
        detail.add(lblDetailSisaKuota);

        panel.add(detail, BorderLayout.CENTER);

        JPanel actionGroup = new JPanel(new GridLayout(4, 1, 10, 10));
        actionGroup.setOpaque(false);

        btnSetDiterima = createActionButton("Tetapkan DITERIMA", new Color(16, 185, 129));
        btnSetCadangan = createActionButton("Tetapkan CADANGAN", new Color(245, 158, 11));
        btnSetDitolak = createActionButton("Tetapkan TIDAK_DITERIMA", new Color(239, 68, 68));
        btnJalankanSeleksi = createActionButton("JALANKAN SELEKSI OTOMATIS", new Color(79, 70, 229));

        btnSetDiterima.addActionListener((ActionEvent e) -> updateSelectedStatus("DITERIMA"));
        btnSetCadangan.addActionListener((ActionEvent e) -> updateSelectedStatus("CADANGAN"));
        btnSetDitolak.addActionListener((ActionEvent e) -> updateSelectedStatus("TIDAK_DITERIMA"));
        btnJalankanSeleksi.addActionListener((ActionEvent e) -> prosesEksekusiSeleksiMassal());

        actionGroup.add(btnSetDiterima);
        actionGroup.add(btnSetCadangan);
        actionGroup.add(btnSetDitolak);
        actionGroup.add(btnJalankanSeleksi);

        panel.add(actionGroup, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout(10, 10));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(16, 16, 16, 16)));

        JPanel footerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        footerLeft.setOpaque(false);
        JLabel lblPassingGrade = new JLabel("Passing Grade Saat Ini:");
        lblPassingGrade.setFont(new Font("Segoe UI", Font.BOLD, 13));
        spinPassingGrade = new JSpinner(new SpinnerNumberModel(75, 0, 400, 1));
        spinPassingGrade.setFont(new Font("Segoe UI", Font.BOLD, 14));
        footerLeft.add(lblPassingGrade);
        footerLeft.add(spinPassingGrade);

        JPanel footerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footerRight.setOpaque(false);
        JLabel lblHint = new JLabel("Gunakan tombol eksekusi untuk memperbarui status kelulusan berdasarkan kuota dan nilai.");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHint.setForeground(new Color(82, 94, 110));
        footerRight.add(lblHint);

        footer.add(footerLeft, BorderLayout.WEST);
        footer.add(footerRight, BorderLayout.CENTER);
        return footer;
    }

    private JLabel createSmallLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(82, 94, 110));
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(20, 23, 29));
        return label;
    }

    private JButton createActionButton(String title, Color background) {
        JButton button = new JButton(title);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(background);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // <-------------------- LOAD DATA METHODS -------------------->
    private void muatSeluruhData() {
        muatStatistik();
        muatTabelSeleksi();
        resetDetailPanel();
    }

    private void muatStatistik() {
        cardTotalPendaftar.setText(String.valueOf(seleksiController.getTotalPendaftar()));
        cardDiterima.setText(String.valueOf(seleksiController.getTotalDiterima()));
        cardCadangan.setText(String.valueOf(seleksiController.getTotalCadangan()));
        cardSisaKuota.setText(String.valueOf(seleksiController.getSisaKuota()));
    }

    private void muatTabelSeleksi() {
        String filter = String.valueOf(cmbStatusFilter.getSelectedItem());
        seleksiController.populateSeleksiTable(modelSeleksi, filter);
    }

    private void muatDetailSeleksi() {
        int row = tblSeleksi.getSelectedRow();
        if (row < 0) {
            resetDetailPanel();
            return;
        }

        lblDetailNama.setText(modelSeleksi.getValueAt(row, 3).toString());
        lblDetailTotalNilai.setText(modelSeleksi.getValueAt(row, 4).toString());
        lblDetailRanking.setText(modelSeleksi.getValueAt(row, 5).toString());
        lblDetailStatus.setText(modelSeleksi.getValueAt(row, 6).toString());
        lblDetailSisaKuota.setText(String.valueOf(seleksiController.getSisaKuota()));
    }

    private void resetDetailPanel() {
        lblDetailNama.setText("-");
        lblDetailTotalNilai.setText("-");
        lblDetailRanking.setText("-");
        lblDetailStatus.setText("-");
        lblDetailSisaKuota.setText(String.valueOf(seleksiController.getSisaKuota()));
    }

    // <-------------------- PROCESS LOGIC ACTIONS -------------------->
    private void updateSelectedStatus(String newStatus) {
        int row = tblSeleksi.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris seleksi terlebih dahulu untuk memperbarui status.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idSeleksi = Integer.parseInt(modelSeleksi.getValueAt(row, 1).toString());
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Anda akan mengubah status seleksi untuk baris terpilih menjadi: " + newStatus + ".\n"
                        + "Lanjutkan?", "Konfirmasi Update Status", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        boolean sukses = seleksiController.updateStatusSeleksi(idSeleksi, newStatus);
        if (sukses) {
            JOptionPane.showMessageDialog(this,
                    "Status seleksi berhasil diperbarui menjadi " + newStatus + ".", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            muatSeluruhData();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal memperbarui status seleksi.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prosesEksekusiSeleksiMassal() {
        int passingGradeTerpilih = (Integer) spinPassingGrade.getValue();

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Apakah Anda benar-benar yakin ingin menjalankan prosedur seleksi massal?\n"
                        + "Acuan kelulusan saat ini adalah Nilai >= " + passingGradeTerpilih + ".\n\n"
                        + "Tindakan ini akan memperbarui seluruh status kelulusan siswa di database secara permanen.",
                "Konfirmasi Eksekusi Engine", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        // 1. Jalankan kalkulasi background engine ke database MySQL via Controller
        boolean sukses = seleksiController.eksekusiSeleksiOtomatis(passingGradeTerpilih);
        
        if (sukses) {
            // 2. 🎯 FIX UTAMA: Panggil paksa tiga fungsi sinkronisasi visual agar JTable, ranking, 
            // serta kartu metrik (Total Diterima, Sisa Kuota) langsung berubah detik itu juga tanpa perlu klik tombol refresh lagi!
            muatSeluruhData();
            
            JOptionPane.showMessageDialog(this,
                    "<html><body><b style='color:#16a34a;'>Engine Seleksi Otomatis Sukses Dijalankan!</b><br>"
                            + "Seluruh pendaftar dengan Nilai Rata-Rata >= <b>" + passingGradeTerpilih + "</b> otomatis berstatus <font color='green'><b>DITERIMA</b></font>.<br>"
                            + "Sisa kuota dan pemeringkatan ranking baru telah diperbarui secara realtime.</body></html>",
                    "Kalkulasi Selesai", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Terjadi kendala saat memproses seleksi otomatis.", "Kegagalan Prosedur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // <-------------------- SUB FORM MANUAL POPUP DIALOG -------------------->
    private void bukaDialogFormPenilaianManual(int idSel, String namaSiswa) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parentWindow, "Input Komponen Nilai — " + namaSiswa, Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(380, 320);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(12, 12));
        ((JPanel) dlg.getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        dlg.getContentPane().setBackground(Color.WHITE);

        JPanel pnlFormGrid = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlFormGrid.setOpaque(false);

        // Mengubah inisialisasi awal ke tipe data int (sesuai struktur kolom INT database)
        int akad = 0, tahf = 0, wawa = 0, domi = 0;
        String queryGetNilai = "SELECT nilai_akademik, nilai_tahfidz, nilai_wawancara, nilai_domisili FROM tbl_seleksi WHERE id_seleksi = ?";
        
        try (Connection conn = DatabaseConfig.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(queryGetNilai)) {
            ps.setInt(1, idSel);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    akad = rs.getInt("nilai_akademik");
                    tahf = rs.getInt("nilai_tahfidz");
                    wawa = rs.getInt("nilai_wawancara");
                    domi = rs.getInt("nilai_domisili");
                }
            }
        } catch (SQLException ex) {
            // Kronologi log kegagalan internal sistem diredam aman
        }

        // Mengubah SpinnerNumberModel menggunakan format Integer (Nilai awal, Min, Max, Step)
        JSpinner sAkad = new JSpinner(new SpinnerNumberModel(akad, 0, 100, 1));
        JSpinner sTahf = new JSpinner(new SpinnerNumberModel(tahf, 0, 100, 1));
        JSpinner sWawa = new JSpinner(new SpinnerNumberModel(wawa, 0, 100, 1));
        JSpinner sDomi = new JSpinner(new SpinnerNumberModel(domi, 0, 100, 1));

        pnlFormGrid.add(new JLabel("Nilai Tes Akademik:")); pnlFormGrid.add(sAkad);
        pnlFormGrid.add(new JLabel("Nilai Hafalan Tahfidz:")); pnlFormGrid.add(sTahf);
        pnlFormGrid.add(new JLabel("Nilai Wawancara Internal:")); pnlFormGrid.add(sWawa);
        pnlFormGrid.add(new JLabel("Nilai Poin Domisili:")); pnlFormGrid.add(sDomi);

        JButton btnSimpanNilai = new JButton("Simpan Komponen Nilai");
        btnSimpanNilai.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSimpanNilai.setBackground(new Color(22, 163, 74));
        btnSimpanNilai.setForeground(Color.WHITE); // 🌟 SEKALIAN MEMPERBAIKI KOTRAST: Teks diubah ke putih agar estetik
        btnSimpanNilai.setPreferredSize(new Dimension(0, 38));
        btnSimpanNilai.setFocusPainted(false);
        btnSimpanNilai.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSimpanNilai.addActionListener(e -> {
            String sqlUpdate = "UPDATE tbl_seleksi SET nilai_akademik = ?, nilai_tahfidz = ?, nilai_wawancara = ?, "
                             + "nilai_domisili = ? WHERE id_seleksi = ?";
            
            try (Connection conn = DatabaseConfig.getKoneksi();
                 PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                
                // Ambil nilai dari JSpinner murni sebagai Integer murni tanpa memicu ClassCastException
                int vAkad = (Integer) sAkad.getValue();
                int vTahf = (Integer) sTahf.getValue();
                int vWawa = (Integer) sWawa.getValue();
                int vDomi = (Integer) sDomi.getValue();

                ps.setInt(1, vAkad);
                ps.setInt(2, vTahf);
                ps.setInt(3, vWawa);
                ps.setInt(4, vDomi);
                ps.setInt(5, idSel);
                ps.executeUpdate();

                // Picu mesin seleksi otomatis bawaan controller agar status passing grade ikut terhitung ulang
                int currentPassingGrade = (Integer) spinPassingGrade.getValue();
                seleksiController.eksekusiSeleksiOtomatis(currentPassingGrade);

                JOptionPane.showMessageDialog(dlg, "Komponen nilai berhasil diperbarui & ranking disinkronkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                
                // Panggil refresh total data agar JTable dan Metric Cards langsung terupdate detik itu juga
                muatSeluruhData(); 
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "Gagal memperbarui nilai: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.add(pnlFormGrid, BorderLayout.CENTER);
        dlg.add(btnSimpanNilai, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}