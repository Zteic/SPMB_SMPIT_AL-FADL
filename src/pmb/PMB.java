package pmb;

import java.awt.EventQueue;
import javax.swing.UIManager;
import views.LandingPage;

public class PMB {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            System.err.println("[WARN] Gagal mengatur Look and Feel: " + e.getMessage());
        }

        EventQueue.invokeLater(() -> {
            try {
                LandingPage landingPage = new LandingPage();
                landingPage.setVisible(true);
            } catch (Exception e) {
                System.err.println("[ERROR] Gagal membuka LandingPage:");
                e.printStackTrace();
            }
        });
    }
}
