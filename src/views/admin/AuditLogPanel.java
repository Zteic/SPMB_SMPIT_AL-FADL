package views.admin;

import controllers.AdminController;
import models.Auditlog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Visualisasi riwayat audit trail premium menggunakan Username & Role kustom.
 * Menghubungkan log operasional pengguna ke AdminController.
 * * @author Rivaldi
 */
public class AuditLogPanel extends JPanel {

    // <-------------------- CONTROLLER -------------------->
    private final AdminController adminController;

    // <-------------------- KOMPONEN FORM -------------------->
    private DefaultTableModel modelLog;
    private JTable tblLog;

    // <-------------------- CONSTRUCTOR -------------------->
    /**
     * Membuat instance baru dari AuditLogPanel dan memuat data awal.
     */
    public AuditLogPanel() {
        adminController = new AdminController();
        initUI();
        muatDataLogKeTabel();
    }

    // <-------------------- INISIALISASI KOMPONEN -------------------->
    /**
     * Merender dan mengonfigurasi komponen utama antarmuka log audit.
     */
    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // <-------------------- HEADER PANEL -------------------->
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitleText = new JPanel(new GridLayout(2, 1, 4, 4));
        pnlTitleText.setOpaque(false);
        
        JLabel title = new JLabel("Audit Trail Logging");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(15, 23, 42));

        JLabel subtitle = new JLabel("Rekaman jejak aktivitas sistem berkala untuk keperluan pengawasan keamanan dan audit data.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(100, 116, 139));
        
        pnlTitleText.add(title);
        pnlTitleText.add(subtitle);

        JButton btnRefresh = new JButton("Segarkan Log");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(71, 85, 105));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setPreferredSize(new Dimension(130, 35));
        btnRefresh.addActionListener(e -> muatDataLogKeTabel());

        pnlHeader.add(pnlTitleText, BorderLayout.WEST);
        pnlHeader.add(btnRefresh, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // <-------------------- DATA GRID TABEL -------------------->
        String[] columns = {"ID", "Waktu Aktivitas", "Nama Pengguna (Operator)", "Hak Akses / Role", "Aksi Sistem", "Rincian Tindakan"};
        modelLog = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblLog = new JTable(modelLog);
        tblLog.setRowHeight(36);
        tblLog.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblLog.setShowGrid(false);
        tblLog.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader headerTable = tblLog.getTableHeader();
        headerTable.setReorderingAllowed(false);
        headerTable.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerTable.setBackground(new Color(235, 243, 250));
        headerTable.setForeground(new Color(51, 65, 85));
        headerTable.setPreferredSize(new Dimension(0, 36));

        tblLog.getColumnModel().getColumn(0).setMinWidth(0);
        tblLog.getColumnModel().getColumn(0).setMaxWidth(0);
        tblLog.getColumnModel().getColumn(0).setWidth(0); 
        tblLog.getColumnModel().getColumn(1).setPreferredWidth(160); 
        tblLog.getColumnModel().getColumn(2).setPreferredWidth(150); 
        tblLog.getColumnModel().getColumn(3).setPreferredWidth(120); 
        tblLog.getColumnModel().getColumn(4).setPreferredWidth(120); 
        tblLog.getColumnModel().getColumn(5).setPreferredWidth(360); 

        tblLog.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                if (!isS) {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    comp.setForeground(new Color(15, 23, 42));
                    
                    if (c == 4 && v != null) {
                        String aksi = v.toString();
                        if (aksi.contains("LOGIN")) { comp.setForeground(new Color(22, 163, 74)); }
                        else if (aksi.contains("LOGOUT")) { comp.setForeground(new Color(100, 116, 139)); }
                        else if (aksi.contains("HAPUS") || aksi.contains("DELETE")) { comp.setForeground(new Color(220, 38, 38)); }
                        else if (aksi.contains("UPLOAD") || aksi.contains("UPDATE") || aksi.contains("SIMPAN")) { comp.setForeground(new Color(37, 99, 235)); }
                    }
                } else {
                    comp.setBackground(new Color(219, 234, 254));
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblLog);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    // <-------------------- DATABASE LOAD DATA -------------------->
    /**
     * Memanggil fungsi penarik log dari AdminController bawaan untuk dimuat ke tabel.
     */
    public void muatDataLogKeTabel() {
        modelLog.setRowCount(0);
        List<Auditlog> logs = adminController.fetchAllAuditLogs(); 
        for (Auditlog log : logs) {
            modelLog.addRow(new Object[]{
                    log.getIdLog(),
                    log.getWaktuKejadian(), 
                    log.getUsername(),      
                    log.getRole(),          
                    log.getAksi(),
                    log.getRincian()
            });
        }
    }
}