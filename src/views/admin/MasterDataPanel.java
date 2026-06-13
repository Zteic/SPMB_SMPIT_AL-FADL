package views.admin;

import controllers.MasterDataController;
import models.Gelombang;
import models.Jalur;
import models.Kuota;
import models.TahunAjaran;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Pusat Konfigurasi Data Master - Modern Enterprise View
 * Mendukung Realtime Dynamic Topbar UI Sync & Unified Upsert Action Button.
 * @author Rivaldi
 */
public class MasterDataPanel extends JPanel {

    private MasterDataController controller;

    private DefaultTableModel tahunModel;
    private DefaultTableModel gelombangModel;
    private DefaultTableModel jalurModel;
    private DefaultTableModel kuotaModel;

    private JTable tblTahun;
    private JTable tblGelombang;
    private JTable tblJalur;
    private JTable tblKuota;

    private JTextField txtTahunAjaran;
    private JComboBox<String> cmbStatusTahun;
    private int selectedTahunId = -1;

    private JTextField txtNamaGelombang;
    private JTextField txtTanggalMulai;
    private JTextField txtTanggalSelesai;
    private JTextField txtBiayaGelombang;
    private JComboBox<String> cmbStatusGelombang;
    private int selectedGelombangId = -1;

    private JTextField txtNamaJalur;
    private JTextField txtKuotaPersen;
    private JComboBox<String> cmbStatusJalur;
    private int selectedJalurId = -1;

    private JComboBox<TahunAjaran> cmbKuotaTahun;
    private JComboBox<Jalur> cmbKuotaJalur;
    private JTextField txtTotalKuota;
    private JTextField txtKuotaTerisi;
    private JTextField txtSisaKuota;
    private int selectedKuotaId = -1;

    public MasterDataPanel() {
        controller = new MasterDataController();
        initUI();
        loadAllData();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // <-------------------- HEADER ATAS -------------------->
        JPanel pnlHeader = new JPanel(new BorderLayout(10, 10));
        pnlHeader.setOpaque(false);

        JPanel pnlTitleText = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitleText.setOpaque(false);
        JLabel title = new JLabel("Pusat Konfigurasi Data Master");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(15, 23, 42));
        JLabel subtitle = new JLabel("Kelola parameter operasional institusi: Tahun akademik, gelombang registrasi, jalur pendaftaran, dan pagu kuota.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(100, 116, 139));
        pnlTitleText.add(title);
        pnlTitleText.add(subtitle);

        JButton btnRefreshAll = new JButton("Refresh") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btnRefreshAll.setPreferredSize(new Dimension(100, 32));
        btnRefreshAll.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefreshAll.setBackground(Color.WHITE);
        btnRefreshAll.setForeground(Color.BLACK);
        btnRefreshAll.setOpaque(false);
        btnRefreshAll.setContentAreaFilled(false);
        btnRefreshAll.setBorderPainted(false);
        btnRefreshAll.setFocusPainted(false);
        btnRefreshAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefreshAll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btnRefreshAll.addActionListener(e -> loadAllData());

        JPanel pnlButtonWrapper = new JPanel(new GridBagLayout());
        pnlButtonWrapper.setOpaque(false);
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.gridx = 0;
        gbcBtn.gridy = 0;
        gbcBtn.anchor = GridBagConstraints.CENTER;
        pnlButtonWrapper.add(btnRefreshAll, gbcBtn);

        pnlHeader.add(pnlTitleText, BorderLayout.CENTER);
        add(pnlHeader, BorderLayout.NORTH);

        // <-------------------- MAIN TABBED PANE PANEL -------------------->
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabPane.setBackground(Color.WHITE);

        tabPane.addTab("Tahun Ajaran", createTahunTab());
        tabPane.addTab("Gelombang Registrasi", createGelombangTab());
        tabPane.addTab("Jalur Pendaftaran", createJalurTab());
        tabPane.addTab("Alokasi Kuota Siswa", createKuotaTab());
        add(tabPane, BorderLayout.CENTER);
    }

    // <-------------------- PANEL GENERATORS WITH UNIFIED UPSERT -------------------->
    private JPanel createTahunTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);

        JPanel pnlForm = createFormContainer();
        GridBagConstraints gbc = createGbcBase();

        gbc.gridy = 0; pnlForm.add(createInputLabel("Tahun Ajaran (Contoh: 2026-2027)"), gbc);
        txtTahunAjaran = createStyledTextField();
        gbc.gridy = 1; pnlForm.add(txtTahunAjaran, gbc);

        gbc.gridy = 2; pnlForm.add(createInputLabel("Status Operasional"), gbc);
        cmbStatusTahun = createStyledComboBox(new String[]{"AKTIF", "NONAKTIF"});
        gbc.gridy = 3; pnlForm.add(cmbStatusTahun, gbc);

        // 🎯 FIXED TOMBOL: Menggunakan tombol SIMPAN terpadu (Upsert) untuk Tahun Ajaran
        gbc.gridy = 4; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.SOUTH;
        pnlForm.add(createActionButtonsRow(e -> handleUpsertTahunAjaran(), e -> deleteTahunAjaran()), gbc);

        JPanel pnlTable = createTableContainer();
        String[] columns = {"ID", "Tahun Ajaran", "Status Operasional"};
        tahunModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTahun = createStyledTable(tahunModel, 2);
        tblTahun.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = tblTahun.getSelectedRow();
                if (row >= 0) {
                    selectedTahunId = Integer.parseInt(tahunModel.getValueAt(row, 0).toString());
                    txtTahunAjaran.setText(tahunModel.getValueAt(row, 1).toString());
                    cmbStatusTahun.setSelectedItem(tahunModel.getValueAt(row, 2).toString());
                }
            }
        });
        pnlTable.add(new JScrollPane(tblTahun), BorderLayout.CENTER);

        panel.add(pnlForm, BorderLayout.WEST);
        panel.add(pnlTable, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGelombangTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);

        JPanel pnlForm = createFormContainer();
        GridBagConstraints gbc = createGbcBase();

        gbc.gridy = 0; pnlForm.add(createInputLabel("Nama Gelombang"), gbc);
        txtNamaGelombang = createStyledTextField(); gbc.gridy = 1; pnlForm.add(txtNamaGelombang, gbc);

        gbc.gridy = 2; pnlForm.add(createInputLabel("Tanggal Mulai (YYYY-MM-DD)"), gbc);
        txtTanggalMulai = createStyledTextField(); gbc.gridy = 3; pnlForm.add(txtTanggalMulai, gbc);

        gbc.gridy = 4; pnlForm.add(createInputLabel("Tanggal Selesai (YYYY-MM-DD)"), gbc);
        txtTanggalSelesai = createStyledTextField(); gbc.gridy = 5; pnlForm.add(txtTanggalSelesai, gbc);

        gbc.gridy = 6; pnlForm.add(createInputLabel("Biaya Registrasi Formula (IDR)"), gbc);
        txtBiayaGelombang = createStyledTextField(); gbc.gridy = 7; pnlForm.add(txtBiayaGelombang, gbc);

        gbc.gridy = 8; pnlForm.add(createInputLabel("Status Gelombang"), gbc);
        cmbStatusGelombang = createStyledComboBox(new String[]{"AKTIF", "NON_AKTIF", "BUKA", "TUTUP"});
        gbc.gridy = 9; pnlForm.add(cmbStatusGelombang, gbc);

        // 🎯 FIXED TOMBOL: Menggunakan tombol SIMPAN terpadu (Upsert) untuk Gelombang
        gbc.gridy = 10; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.SOUTH;
        pnlForm.add(createActionButtonsRow(e -> handleUpsertGelombang(), e -> deleteGelombang()), gbc);

        JPanel pnlTable = createTableContainer();
        String[] columns = {"ID", "Nama Gelombang", "Tanggal Mulai", "Tanggal Selesai", "Biaya Pendaftaran", "Status"};
        gelombangModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblGelombang = createStyledTable(gelombangModel, 5);
        tblGelombang.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = tblGelombang.getSelectedRow();
                if (row >= 0) {
                    selectedGelombangId = Integer.parseInt(gelombangModel.getValueAt(row, 0).toString());
                    txtNamaGelombang.setText(gelombangModel.getValueAt(row, 1).toString());
                    txtTanggalMulai.setText(gelombangModel.getValueAt(row, 2).toString());
                    txtTanggalSelesai.setText(gelombangModel.getValueAt(row, 3).toString());
                    txtBiayaGelombang.setText(gelombangModel.getValueAt(row, 4).toString());
                    cmbStatusGelombang.setSelectedItem(gelombangModel.getValueAt(row, 5).toString());
                }
            }
        });
        pnlTable.add(new JScrollPane(tblGelombang), BorderLayout.CENTER);

        panel.add(pnlForm, BorderLayout.WEST);
        panel.add(pnlTable, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createJalurTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);

        JPanel pnlForm = createFormContainer();
        GridBagConstraints gbc = createGbcBase();

        gbc.gridy = 0; pnlForm.add(createInputLabel("Nama Jalur"), gbc);
        txtNamaJalur = createStyledTextField(); gbc.gridy = 1; pnlForm.add(txtNamaJalur, gbc);

        gbc.gridy = 2; pnlForm.add(createInputLabel("Persentase Kuota Pagu (%)"), gbc);
        txtKuotaPersen = createStyledTextField(); gbc.gridy = 3; pnlForm.add(txtKuotaPersen, gbc);

        gbc.gridy = 4; pnlForm.add(createInputLabel("Status Jalur"), gbc);
        cmbStatusJalur = createStyledComboBox(new String[]{"AKTIF", "TIDAK AKTIF"});
        gbc.gridy = 5; pnlForm.add(cmbStatusJalur, gbc);

        // 🎯 FIXED TOMBOL: Menggunakan tombol SIMPAN terpadu (Upsert) untuk Jalur
        gbc.gridy = 6; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.SOUTH;
        pnlForm.add(createActionButtonsRow(e -> handleUpsertJalur(), e -> deleteJalur()), gbc);

        JPanel pnlTable = createTableContainer();
        String[] columns = {"ID", "Nama Jalur Masuk", "Alokasi Persen", "Status Registrasi"};
        jalurModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblJalur = createStyledTable(jalurModel, 3);
        tblJalur.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = tblJalur.getSelectedRow();
                if (row >= 0) {
                    selectedJalurId = Integer.parseInt(jalurModel.getValueAt(row, 0).toString());
                    txtNamaJalur.setText(jalurModel.getValueAt(row, 1).toString());
                    txtKuotaPersen.setText(jalurModel.getValueAt(row, 2).toString());
                    cmbStatusJalur.setSelectedItem(jalurModel.getValueAt(row, 3).toString());
                }
            }
        });
        pnlTable.add(new JScrollPane(tblJalur), BorderLayout.CENTER);

        panel.add(pnlForm, BorderLayout.WEST);
        panel.add(pnlTable, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createKuotaTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);

        JPanel pnlForm = createFormContainer();
        GridBagConstraints gbc = createGbcBase();

        gbc.gridy = 0; pnlForm.add(createInputLabel("Tahun Ajaran"), gbc);
        cmbKuotaTahun = new JComboBox<>(); cmbKuotaTahun.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbKuotaTahun.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 1; pnlForm.add(cmbKuotaTahun, gbc);

        gbc.gridy = 2; pnlForm.add(createInputLabel("Jalur Seleksi"), gbc);
        cmbKuotaJalur = new JComboBox<>(); cmbKuotaJalur.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbKuotaJalur.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 3; pnlForm.add(cmbKuotaJalur, gbc);

        gbc.gridy = 4; pnlForm.add(createInputLabel("Total Batas Kuota (Siswa)"), gbc);
        txtTotalKuota = createStyledTextField(); gbc.gridy = 5; pnlForm.add(txtTotalKuota, gbc);

        gbc.gridy = 6; pnlForm.add(createInputLabel("Kuota Terisi Sementara"), gbc);
        txtKuotaTerisi = createStyledTextField(); gbc.gridy = 7; pnlForm.add(txtKuotaTerisi, gbc);

        gbc.gridy = 8; pnlForm.add(createInputLabel("Sisa Kuota Kamar"), gbc);
        txtSisaKuota = createStyledTextField(); txtSisaKuota.setEditable(false);
        txtSisaKuota.setBackground(new Color(241, 245, 249));
        gbc.gridy = 9; pnlForm.add(txtSisaKuota, gbc);

        // 🎯 FIXED TOMBOL: Menggunakan tombol SIMPAN terpadu (Upsert) untuk Kuota
        gbc.gridy = 10; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.SOUTH;
        pnlForm.add(createActionButtonsRow(e -> handleUpsertKuota(), e -> deleteKuota()), gbc);

        JPanel pnlTable = createTableContainer();
        String[] columns = {"ID", "Tahun", "Jalur Seleksi", "Daya Tampung", "Terisi", "Sisa Kursi"};
        kuotaModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKuota = createStyledTable(kuotaModel, -1);
        tblKuota.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = tblKuota.getSelectedRow();
                if (row >= 0) {
                    selectedKuotaId = Integer.parseInt(kuotaModel.getValueAt(row, 0).toString());
                    selectComboBoxItem(cmbKuotaTahun, kuotaModel.getValueAt(row, 1).toString());
                    selectComboBoxItem(cmbKuotaJalur, kuotaModel.getValueAt(row, 2).toString());
                    txtTotalKuota.setText(kuotaModel.getValueAt(row, 3).toString());
                    txtKuotaTerisi.setText(kuotaModel.getValueAt(row, 4).toString());
                    txtSisaKuota.setText(kuotaModel.getValueAt(row, 5).toString());
                }
            }
        });
        pnlTable.add(new JScrollPane(tblKuota), BorderLayout.CENTER);

        panel.add(pnlForm, BorderLayout.WEST);
        panel.add(pnlTable, BorderLayout.CENTER);
        return panel;
    }

    // <-------------------- UTILITY LAYOUT ENGINE -------------------->
    private JPanel createFormContainer() {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        p.setPreferredSize(new Dimension(360, 0)); 
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(18, 16, 18, 16)
        ));
        return p;
    }

    private GridBagConstraints createGbcBase() {
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.weightx = 1.0;
        g.insets = new Insets(4, 0, 4, 0);
        return g;
    }

    private JPanel createTableContainer() {
        return new JPanel(new BorderLayout());
    }

    private JLabel createInputLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(new Color(100, 116, 139)); return l;
    }

    private JTextField createStyledTextField() {
        JTextField t = new JTextField(); t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setPreferredSize(new Dimension(0, 35));
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        return t;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setPreferredSize(new Dimension(0, 35));
        return cb;
    }

    // 🎯 REVISI TOTAL COMPONENT: Menghilangkan tombol "Ubah", menyisakan 3 tombol utama (SIMPAN, HAPUS, RESET)
    private JPanel createActionButtonsRow(java.awt.event.ActionListener upsertAction, java.awt.event.ActionListener delAction) {
        JPanel row = new JPanel(new GridLayout(1, 3, 8, 0)); 
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 38));

        JButton bSimpan = createFormActionButton("SIMPAN", new Color(34, 197, 94), upsertAction);
        JButton bHapus = createFormActionButton("Hapus", new Color(239, 68, 68), delAction);
        JButton bReset = createFormActionButton("Reset", new Color(148, 163, 184), e -> loadAllData());
        
        row.add(bSimpan); row.add(bHapus); row.add(bReset);
        return row;
    }

    private JButton createFormActionButton(String labelText, Color bg, java.awt.event.ActionListener act) {
        JButton btn = new JButton(labelText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(act);
        return btn;
    }

    private JTable createStyledTable(DefaultTableModel m, final int statusColIdx) {
        JTable t = new JTable(m); t.setRowHeight(38);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12)); t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0,0));
        t.setSelectionBackground(new Color(241, 245, 249));
        t.setSelectionForeground(new Color(15, 23, 42));

        JTableHeader th = t.getTableHeader(); th.setReorderingAllowed(false);
        th.setFont(new Font("Segoe UI", Font.BOLD, 12)); th.setBackground(new Color(236, 240, 241));
        th.setForeground(new Color(51, 65, 85)); th.setPreferredSize(new Dimension(0, 34));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, v, isS, hasF, r, c);
                if (!isS) {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    comp.setForeground(new Color(15, 23, 42));
                    
                    if (c == statusColIdx && v != null) {
                        String statusStr = v.toString();
                        if (statusStr.equals("AKTIF") || statusStr.equals("BUKA")) {
                            comp.setBackground(new Color(220, 252, 231)); comp.setForeground(new Color(21, 128, 61));
                        } else {
                            comp.setBackground(new Color(241, 245, 249)); comp.setForeground(new Color(100, 116, 139));
                        }
                    }
                } else {
                    comp.setBackground(new Color(219, 234, 254)); comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        });

        t.getColumnModel().getColumn(0).setMinWidth(0);
        t.getColumnModel().getColumn(0).setMaxWidth(0);
        t.getColumnModel().getColumn(0).setWidth(0);
        return t;
    }

    // <-------------------- CORE ENGINE OPERATIONS (SINKRON REALTIME) -------------------->
    private void triggerRealtimeTopbarSync() {
        // 🎯 LOGIKA EMIT SINKRONISASI: Memicu Frame Utama menyegarkan label TA saat data master berubah
        if (javax.swing.SwingUtilities.getWindowAncestor(this) instanceof views.admin.AdminMainFrame) {
            ((views.admin.AdminMainFrame) javax.swing.SwingUtilities.getWindowAncestor(this)).sinkronisasiUIPeriodeAktif();
        }
    }

    private void loadAllData() {
        loadTahunAjaran();
        loadGelombang();
        loadJalur();
        loadKuota();
        loadKuotaCombos();
    }

    private void loadTahunAjaran() {
        tahunModel.setRowCount(0); selectedTahunId = -1;
        txtTahunAjaran.setText(""); cmbStatusTahun.setSelectedIndex(0);
        List<TahunAjaran> years = controller.fetchTahunAjaran();
        for (TahunAjaran year : years) {
            String statusTeksLabel = (year.getStatusAktif() == 1) ? "AKTIF" : "NONAKTIF";
            tahunModel.addRow(new Object[]{year.getIdTahun(), year.getTahunAjaran(), statusTeksLabel});
        }
    }

    private void loadGelombang() {
        gelombangModel.setRowCount(0); selectedGelombangId = -1;
        txtNamaGelombang.setText(""); txtTanggalMulai.setText(""); txtTanggalSelesai.setText(""); txtBiayaGelombang.setText("");
        cmbStatusGelombang.setSelectedIndex(0);
        List<Gelombang> list = controller.fetchGelombang();
        for (Gelombang item : list) {
            gelombangModel.addRow(new Object[]{
                    item.getIdGelombang(), item.getNamaGelombang(), item.getTanggalMulai(),
                    item.getTanggalSelesai(), String.format("%.2f", item.getBiayaPendaftaran()), item.getStatus()
            });
        }
    }

    private void loadJalur() {
        jalurModel.setRowCount(0); selectedJalurId = -1;
        txtNamaJalur.setText(""); txtKuotaPersen.setText(""); cmbStatusJalur.setSelectedIndex(0);
        List<Jalur> list = controller.fetchJalur();
        for (Jalur item : list) {
            jalurModel.addRow(new Object[]{item.getIdJalur(), item.getNamaJalur(), item.getKuotaPersen(), item.getStatus()});
        }
    }

    private void loadKuota() {
        kuotaModel.setRowCount(0); selectedKuotaId = -1;
        txtTotalKuota.setText(""); txtKuotaTerisi.setText(""); txtSisaKuota.setText("");
        List<Kuota> list = controller.fetchKuota();
        for (Kuota item : list) {
            kuotaModel.addRow(new Object[]{
                    item.getIdKuota(), item.getNamaTahunAjaran(), item.getNamaJalur(),
                    item.getTotalKuota(), item.getKuotaTerisi(), item.getSisaKuota()
            });
        }
    }

    private void loadKuotaCombos() {
        List<TahunAjaran> years = controller.fetchTahunAjaran();
        DefaultComboBoxModel<TahunAjaran> yearModel = new DefaultComboBoxModel<>();
        for (TahunAjaran year : years) { yearModel.addElement(year); }
        cmbKuotaTahun.setModel(yearModel);

        List<Jalur> jalurs = controller.fetchJalur();
        DefaultComboBoxModel<Jalur> jalurModelCombo = new DefaultComboBoxModel<>();
        for (Jalur jalur : jalurs) { jalurModelCombo.addElement(jalur); }
        cmbKuotaJalur.setModel(jalurModelCombo);
    }

    // 🎯 LOGIKA GABUNGAN (UPSERT): Handler Tombol SIMPAN untuk Tahun Ajaran
    private void handleUpsertTahunAjaran() {
        String tahun = txtTahunAjaran.getText().trim();
        if (tahun.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Tahun ajaran tidak boleh kosong.", "Validasi", JOptionPane.WARNING_MESSAGE); 
            return; 
        }
        
        int statusAngkaInt = cmbStatusTahun.getSelectedItem().toString().equalsIgnoreCase("AKTIF") ? 1 : 0;
        boolean isSuccess;

        if (selectedTahunId >= 0) {
            // Jalankan Aksi UBAH (UPDATE) jika baris tabel sedang dipilih kursor
            TahunAjaran item = new TahunAjaran(selectedTahunId, tahun, statusAngkaInt);
            isSuccess = controller.updateTahunAjaran(item);
        } else {
            // Jalankan Aksi SIMPAN BARU (INSERT) jika form dalam kondisi kosong bersih
            TahunAjaran item = new TahunAjaran();
            item.setTahunAjaran(tahun);
            item.setStatusAktif(statusAngkaInt);
            isSuccess = controller.createTahunAjaran(item);
        }

        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "Konfigurasi tahun ajaran berhasil disimpan ke database.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            triggerRealtimeTopbarSync(); // Memicu UI Topbar Induk menyegarkan teks TA
            loadAllData();
        } else { 
            JOptionPane.showMessageDialog(this, "Gagal memproses transaksi tahun ajaran.", "Error MySQL", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void deleteTahunAjaran() {
        if (selectedTahunId < 0) { JOptionPane.showMessageDialog(this, "Pilih tahun ajaran terlebih dahulu.", "Validasi", JOptionPane.WARNING_MESSAGE); return; }
        int opsi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus tahun ajaran ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (opsi != JOptionPane.YES_OPTION) return;
        if (controller.deleteTahunAjaran(selectedTahunId)) {
            JOptionPane.showMessageDialog(this, "Tahun ajaran berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            triggerRealtimeTopbarSync(); // Memicu UI Topbar menyegarkan teks jika data aktif terhapus
            loadAllData();
        } else { JOptionPane.showMessageDialog(this, "Gagal menghapus tahun ajaran.", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    // 🎯 LOGIKA GABUNGAN (UPSERT): Handler Tombol SIMPAN untuk Gelombang
    private void handleUpsertGelombang() {
        String nama = txtNamaGelombang.getText().trim();
        String mulai = txtTanggalMulai.getText().trim();
        String selesai = txtTanggalSelesai.getText().trim();
        String biayaText = txtBiayaGelombang.getText().trim();
        if (nama.isEmpty() || mulai.isEmpty() || selesai.isEmpty() || biayaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field gelombang harus diisi.", "Validasi", JOptionPane.WARNING_MESSAGE); return;
        }
        double biaya;
        try { biaya = Double.parseDouble(biayaText); } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Biaya pendaftaran harus angka.", "Validasi", JOptionPane.WARNING_MESSAGE); return;
        }

        boolean isSuccess;
        if (selectedGelombangId >= 0) {
            Gelombang item = new Gelombang(selectedGelombangId, nama, mulai, selesai, biaya, cmbStatusGelombang.getSelectedItem().toString());
            isSuccess = controller.updateGelombang(item);
        } else {
            Gelombang item = new Gelombang();
            item.setNamaGelombang(nama); item.setTanggalMulai(mulai); item.setTanggalSelesai(selesai); item.setBiayaPendaftaran(biaya);
            item.setStatus(cmbStatusGelombang.getSelectedItem().toString());
            isSuccess = controller.createGelombang(item);
        }

        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "Data gelombang registrasi berhasil diproses.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadAllData();
        } else { JOptionPane.showMessageDialog(this, "Gagal memproses data gelombang.", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void deleteGelombang() {
        if (selectedGelombangId < 0) { JOptionPane.showMessageDialog(this, "Pilih gelombang terlebih dahulu.", "Validasi", JOptionPane.WARNING_MESSAGE); return; }
        int opsi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus gelombang ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (opsi != JOptionPane.YES_OPTION) return;
        if (controller.deleteGelombang(selectedGelombangId)) {
            JOptionPane.showMessageDialog(this, "Gelombang berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadAllData();
        } else { JOptionPane.showMessageDialog(this, "Gagal menghapus gelombang.", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    // 🎯 LOGIKA GABUNGAN (UPSERT): Handler Tombol SIMPAN untuk Jalur
    private void handleUpsertJalur() {
        String nama = txtNamaJalur.getText().trim();
        String persenText = txtKuotaPersen.getText().trim();
        if (nama.isEmpty() || persenText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama jalur dan kuota persen harus diisi.", "Validasi", JOptionPane.WARNING_MESSAGE); return;
        }
        int persen;
        try { persen = Integer.parseInt(persenText); } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Kuota persen harus angka.", "Validasi", JOptionPane.WARNING_MESSAGE); return;
        }

        boolean isSuccess;
        if (selectedJalurId >= 0) {
            Jalur item = new Jalur(selectedJalurId, nama, persen, cmbStatusJalur.getSelectedItem().toString());
            isSuccess = controller.updateJalur(item);
        } else {
            Jalur item = new Jalur();
            item.setNamaJalur(nama); item.setKuotaPersen(persen);
            item.setStatus(cmbStatusJalur.getSelectedItem().toString());
            isSuccess = controller.createJalur(item);
        }

        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "Parameter jalur pendaftaran berhasil diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadAllData();
        } else { JOptionPane.showMessageDialog(this, "Gagal memproses data jalur masuk.", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void deleteJalur() {
        if (selectedJalurId < 0) { JOptionPane.showMessageDialog(this, "Pilih jalur terlebih dahulu.", "Validasi", JOptionPane.WARNING_MESSAGE); return; }
        int opsi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus jalur ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (opsi != JOptionPane.YES_OPTION) return;
        if (controller.deleteJalur(selectedJalurId)) {
            JOptionPane.showMessageDialog(this, "Jalur berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadAllData();
        } else { JOptionPane.showMessageDialog(this, "Gagal menghapus jalur.", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    // 🎯 LOGIKA GABUNGAN (UPSERT): Handler Tombol SIMPAN untuk Pagu Kuota
    private void handleUpsertKuota() {
        TahunAjaran tahun = (TahunAjaran) cmbKuotaTahun.getSelectedItem();
        Jalur jalur = (Jalur) cmbKuotaJalur.getSelectedItem();
        if (tahun == null || jalur == null) {
            JOptionPane.showMessageDialog(this, "Pilih tahun dan jalur terlebih dahulu.", "Validasi", JOptionPane.WARNING_MESSAGE); return;
        }
        String totalText = txtTotalKuota.getText().trim();
        String terisiText = txtKuotaTerisi.getText().trim();
        if (totalText.isEmpty() || terisiText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Total dan terisi harus diisi.", "Validasi", JOptionPane.WARNING_MESSAGE); return;
        }
        int total, terisi;
        try { total = Integer.parseInt(totalText); terisi = Integer.parseInt(terisiText); } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Total kuota dan terisi harus angka.", "Validasi", JOptionPane.WARNING_MESSAGE); return;
        }
        int sisa = controller.calculateSisa(total, terisi);

        boolean isSuccess;
        if (selectedKuotaId >= 0) {
            Kuota item = new Kuota(selectedKuotaId, tahun.getIdTahun(), jalur.getIdJalur(), total, terisi, sisa);
            isSuccess = controller.updateKuota(item);
        } else {
            Kuota item = new Kuota();
            item.setIdTahun(tahun.getIdTahun()); item.setIdJalur(jalur.getIdJalur());
            item.setTotalKuota(total); item.setKuotaTerisi(terisi); item.setSisaKuota(sisa);
            isSuccess = controller.createKuota(item);
        }

        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "Alokasi batas daya tampung siswa berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            txtSisaKuota.setText(String.valueOf(sisa)); 
            loadAllData();
        } else { JOptionPane.showMessageDialog(this, "Gagal memproses entri pagu kuota.", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void deleteKuota() {
        if (selectedKuotaId < 0) { JOptionPane.showMessageDialog(this, "Pilih data kuota terlebih dahulu.", "Validasi", JOptionPane.WARNING_MESSAGE); return; }
        int opsi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus entri kuota ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (opsi != JOptionPane.YES_OPTION) return;
        if (controller.deleteKuota(selectedKuotaId)) {
            JOptionPane.showMessageDialog(this, "Kuota berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadAllData();
        } else { JOptionPane.showMessageDialog(this, "Gagal menghapus kuota.", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private <T> void selectComboBoxItem(JComboBox<T> combo, String text) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            T item = combo.getItemAt(i);
            if (item != null && item.toString().equals(text)) { combo.setSelectedIndex(i); return; }
        }
    }
}