package pmb;

import javax.swing.UIManager;
import java.awt.EventQueue;
import views.LandingPage;

/**
 * @author Rivaldi
 * Class Utama (Main Entry Point)
 *
 * Flow Aplikasi:
 * PMB -> LandingPage -> LoginFrame -> Main System
 */
public class PMB {

    public static void main(String[] args) {

        // 1. Set Look and Feel agar mengikuti OS (Windows/Mac/Linux)
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            System.err.println("[WARN] Gagal mengatur Look and Feel: " + e.getMessage());
        }

        // 2. Jalankan aplikasi di Event Dispatch Thread (EDT)
        EventQueue.invokeLater(() -> {
            try {
                // LandingPage adalah halaman pertama aplikasi
                LandingPage landingPage = new LandingPage();
                landingPage.setVisible(true);

            } catch (Exception e) {
                System.err.println("[ERROR] Gagal membuka LandingPage:");
                e.printStackTrace();
            }
        });
    }
}