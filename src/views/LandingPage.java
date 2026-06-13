package views;

import views.components.CustomButton;
import views.components.ModernCard;
import views.siswa.info.FAQ;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import pmb.LoginFrame;

/**
 * @author Rivaldi
 * Role: Senior Java Swing Developer / Software Architect
 * Project: Integrated Landing Page Overhaul - SPMB SMPIT AL FADL (2026 Modern Standard)
 */
public class LandingPage extends JFrame {

    class BackgroundPanel extends JPanel {
        private Image background;

        public BackgroundPanel(String fileName) {
            setLayout(null); 
            setOpaque(true);
            
            // Mengambil gambar background dari path yang diberikan
            URL imgURL = getClass().getResource(fileName);
            
            if (imgURL == null) {
                setBackground(new Color(240, 247, 244)); 
                System.out.println("Gambar tidak ditemukan di: " + fileName);
            } else {
                this.background = new ImageIcon(imgURL).getImage();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (background != null) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public LandingPage() {
        setTitle("PPDB - SMPIT AL FADL");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Palet Warna Kustom Desain Modern 2026
        Color clrDarkSlate  = new Color(15, 23, 42);    // #0F172A
        Color clrBorderGray = new Color(209, 213, 219); // #D1D5DB
        Color clrLightGray  = new Color(243, 244, 246); // #F3F4F6
        Color clrBlueMain   = new Color(13, 71, 161);   // #0D47A1
        Color clrBlueHover  = new Color(21, 101, 192);  // #1565C0
        Color clrBlueLight  = new Color(227, 242, 253); // #E3F2FD
        Color clrGreenMain  = new Color(46, 125, 50);   // #2E7D32
        Color clrGreenHover = new Color(56, 142, 60);   // #388E3C
        Color clrPutihTransparan = new Color(255, 255, 255, 210); // Nilai 210 memberikan efek tembus pandang yang pas
        Color clrPutihHover      = new Color(243, 244, 246, 240);

        // Path mengarah ke folder src/resources/ (Sesuai gambar background_sekolah2)
        BackgroundPanel bgPanel = new BackgroundPanel("/resources/background_sekolah2.png");
        setContentPane(bgPanel);

        // --- NAVBAR ---
        JPanel navbar = new JPanel(null);
        navbar.setBounds(0, 0, 1000, 80);
        navbar.setOpaque(false); 
        bgPanel.add(navbar);

        // REVISI: Tombol FAQ menggunakan CustomButton (Kanan Atas)
        CustomButton btnFAQ = new CustomButton("FAQ", clrPutihTransparan, clrDarkSlate);
        btnFAQ.setBounds(700, 25, 110, 42); 
        btnFAQ.setRadius(15);
        btnFAQ.setHoverColor(clrPutihHover);
        btnFAQ.setCustomBorder(clrBorderGray);
        btnFAQ.addActionListener(e -> {
            new FAQ().setVisible(true); 
        });
        navbar.add(btnFAQ);

        // REVISI: Tombol LOGIN menggunakan CustomButton (Kanan Atas)
        CustomButton btnLoginNav = new CustomButton("LOGIN", clrBlueMain, Color.WHITE);
        btnLoginNav.setBounds(830, 25, 130, 42);
        btnLoginNav.setRadius(15);
        btnLoginNav.setHoverColor(clrBlueHover);
        btnLoginNav.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        navbar.add(btnLoginNav);

        // --- MIDDLE CTA AREA ---

        // REVISI: Tombol DAFTAR SEKARANG menggunakan CustomButton (CTA Utama Tengah)
        CustomButton btnDaftar = new CustomButton("Daftar Sekarang", clrGreenMain, Color.WHITE);
        btnDaftar.setBounds(230, 300, 260, 55); 
        btnDaftar.setRadius(20);
        btnDaftar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnDaftar.setHoverColor(clrGreenHover);
        btnDaftar.addActionListener(e -> {
            views.siswa.FormDaftar formBaru = new views.siswa.FormDaftar();
            formBaru.setVisible(true);
            this.dispose();
        });
        bgPanel.add(btnDaftar);

        // REVISI: Tombol PANDUAN PENDAFTARAN menggunakan CustomButton (Tengah)
        CustomButton btnPanduan = new CustomButton("Panduan Pendaftaran", clrPutihTransparan, clrBlueMain);
        btnPanduan.setBounds(510, 300, 260, 55);
        btnPanduan.setRadius(20);
        btnPanduan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPanduan.setHoverColor(clrBlueLight);
        btnPanduan.setCustomBorder(clrBlueMain);
        btnPanduan.addActionListener(e -> {
             new FAQ().setVisible(true); 
        });
        bgPanel.add(btnPanduan);

        // --- BOTTOM MENU BUTTONS ---
        setupBottomMenuCardLayout(bgPanel);
    }

    private void setupBottomMenuCardLayout(JPanel bgPanel) {
    String[] menuNames = {"Syarat Pendaftaran", "Info Pendaftaran", "Fasilitas", "Program Unggulan", "Program Sekolah"};
    String[] unicodeIcons = {"📄", "ℹ", "🏫", "⭐", "📢"};
    
    String[] fileNames = {
        "syarat_pmb.png", 
        "InfoPendaftaran.png", 
        "fasilitas_sekolah.png", 
        "program_unggulan.png", 
        "program_sekolah.png"
    };
    
    // Putih transparan dengan alpha 200 (sangat stabil untuk teks di atasnya)
    Color clrCardTransparan = new Color(255, 255, 255, 200); 
    
    int xStart = 40; 
    for (int i = 0; i < menuNames.length; i++) {
        // Panggil ModernCard yang baru diperbaiki di atas
        ModernCard cardBtn = new ModernCard(20, clrCardTransparan);
        cardBtn.setCardContent(unicodeIcons[i], menuNames[i]);
        cardBtn.setBounds(xStart + (i * 188), 530, 180, 95);
        
        final int index = i;
        cardBtn.addActionListener(e -> tampilkanGambar(fileNames[index], menuNames[index]));
        
        bgPanel.add(cardBtn);
    }
}

    private void tampilkanGambar(String fileName, String title) {
        URL imgURL = getClass().getResource("/resources/" + fileName);
        
        if (imgURL == null) {
            JOptionPane.showMessageDialog(this, "File gambar " + fileName + " tidak ditemukan di folder resources!");
            return;
        }

        // 1. Buat JDialog penampil dengan ukuran yang ideal
        JDialog viewer = new JDialog(this, title, true);
        viewer.setSize(850, 650); // Sedikit disesuaikan ukurannya agar lega
        viewer.setLocationRelativeTo(this);
        viewer.setLayout(new BorderLayout());

        // 2. Ambil gambar asli dan lakukan scaling secara halus (SMOOTH)
        ImageIcon originalIcon = new ImageIcon(imgURL);
        Image originalImage = originalIcon.getImage();
        
        // Mengurangi ukuran target dengan padding agar tidak menempel ketat ke border window
        int targetWidth = 820;
        int targetHeight = 580;
        
        // Proses scaling gambar otomatis mengikuti area window
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // 3. Masukkan gambar yang sudah di-scale ke JLabel
        JLabel lblFoto = new JLabel(scaledIcon);
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblFoto.setVerticalAlignment(SwingConstants.CENTER);
        
        // Berikan background putih tipis di sekitar gambar agar estetik
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setBackground(Color.WHITE);
        imageContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        imageContainer.add(lblFoto, BorderLayout.CENTER);
        
        // 4. Masukkan langsung ke viewer (Kita hilangkan JScrollPane agar scrollbar jelek itu hilang)
        viewer.add(imageContainer, BorderLayout.CENTER);
        viewer.setResizable(false); // Kunci ukuran jendela agar scaling tetap konsisten
        viewer.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        EventQueue.invokeLater(() -> {
            new LandingPage().setVisible(true);
        });
    }
}