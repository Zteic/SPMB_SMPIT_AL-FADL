package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * @author Rivaldi
 *
 * Database Configuration
 * SPMB SMPIT AL FADL
 *
 * JDBC MySQL Connection Manager
 */
public class DatabaseConfig {

    private static final String URL =
            "jdbc:mysql://localhost:3306/spmb_alfadl"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=Asia/Jakarta";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection koneksi;

    private static boolean errorShown = false;

    private DatabaseConfig() {
    }

    /**
     * Mengambil koneksi database aktif
     */
    public static Connection getKoneksi() {

        try {
            loadDriver();
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            errorShown = false;
            System.out.println("[DATABASE] Connected -> spmb_alfadl");
            return conn;
        } catch (SQLException ex) {
            System.err.println("[DATABASE ERROR] " + ex.getMessage());
            showConnectionError(ex.getMessage());
            return null;
        }
    }

    /**
     * Load Driver JDBC
     */
    private static void loadDriver() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException ex) {

            try {

                Class.forName("com.mysql.jdbc.Driver");

            } catch (ClassNotFoundException e) {

                JOptionPane.showMessageDialog(
                        null,
                        "MySQL JDBC Driver tidak ditemukan.\n"
                        + "Pastikan mysql-connector sudah "
                        + "ditambahkan ke Libraries.",
                        "Driver Error",
                        JOptionPane.ERROR_MESSAGE
                );

                System.err.println(
                        "[CRITICAL] JDBC Driver not found."
                );
            }
        }
    }

    /**
     * Menampilkan popup error satu kali
     */
    private static void showConnectionError(String errorMessage) {

        if (errorShown) {
            return;
        }

        errorShown = true;

        JOptionPane.showMessageDialog(
                null,
                "Gagal terhubung ke database MySQL.\n\n"
                + "Periksa:\n"
                + "1. XAMPP MySQL sudah START\n"
                + "2. Database spmb_alfadl tersedia\n"
                + "3. Port MySQL benar\n\n"
                + "Detail:\n"
                + errorMessage,
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Test koneksi database
     */
    public static boolean testConnection() {

        try (Connection conn = getKoneksi()) {
            return conn != null && !conn.isClosed();
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Status koneksi aktif
     */
    public static boolean isConnected() {

        return testConnection();
    }

    /**
     * Menutup koneksi database
     */
    public static void closeKoneksi() {

        try {

            if (koneksi != null
                    && !koneksi.isClosed()) {

                koneksi.close();

                System.out.println(
                        "[DATABASE] Connection closed."
                );
            }

        } catch (SQLException ex) {

            System.err.println(
                    "[DATABASE ERROR] "
                    + ex.getMessage()
            );
        }
    }
}