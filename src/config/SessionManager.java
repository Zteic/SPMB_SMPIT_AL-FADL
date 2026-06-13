package config;

import models.User;

/**
 * =========================================================
 * SESSION MANAGER
 * SPMB SMPIT AL FADL
 * =========================================================
 *
 * Fungsi:
 * - Menyimpan user aktif selama aplikasi berjalan
 * - Menyediakan helper role checking
 * - Menyediakan akses cepat data user
 * - Mendukung Audit Log
 * - Mendukung RBAC (Role Based Access Control)
 *
 * Author : Rivaldi
 * Version: Production Ready
 * =========================================================
 */
public final class SessionManager {

    private static User currentUser;

    /**
     * Constructor private
     * Mencegah instansiasi object
     */
    private SessionManager() {
    }

    // =====================================================
    // SESSION SETTER
    // =====================================================

    public static synchronized void setCurrentUser(User user) {

        currentUser = user;

        if (user != null) {

            System.out.println(
                    "[SESSION] Login berhasil : "
                    + user.getUsername()
                    + " | "
                    + user.getRole()
            );
        }
    }

    // =====================================================
    // SESSION GETTER
    // =====================================================

    public static synchronized User getCurrentUser() {
        return currentUser;
    }

    public static synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    // =====================================================
    // USER INFORMATION
    // =====================================================

    public static synchronized int getUserId() {

        if (currentUser == null) {
            return 0;
        }

        return currentUser.getIdUser();
    }

    public static synchronized String getUsername() {

        if (currentUser == null) {
            return "";
        }

        return currentUser.getUsername();
    }

    public static synchronized String getNamaLengkap() {

        if (currentUser == null) {
            return "";
        }

        return currentUser.getNamaLengkap();
    }

    public static synchronized String getRole() {

        if (currentUser == null) {
            return "";
        }

        return currentUser.getRole();
    }

    public static synchronized String getStatus() {

        if (currentUser == null) {
            return "";
        }

        return currentUser.getStatus();
    }

    public static synchronized String getEmail() {

        if (currentUser == null) {
            return "";
        }

        return currentUser.getEmail();
    }

    public static synchronized String getNoHp() {

        if (currentUser == null) {
            return "";
        }

        return currentUser.getNoHp();
    }

    // =====================================================
    // ROLE CHECKER
    // =====================================================

    public static synchronized boolean isAdmin() {

        return currentUser != null
                && "ADMIN".equalsIgnoreCase(
                        currentUser.getRole()
                );
    }

    public static synchronized boolean isVerifikator() {

        return currentUser != null
                && "VERIFIKATOR".equalsIgnoreCase(
                        currentUser.getRole()
                );
    }

    public static synchronized boolean isCalonSiswa() {

        return currentUser != null
                && "CALON_SISWA".equalsIgnoreCase(
                        currentUser.getRole()
                );
    }

    // =====================================================
    // ROLE VALIDATION
    // =====================================================

    public static synchronized boolean hasRole(
            String role
    ) {

        if (currentUser == null) {
            return false;
        }

        return role.equalsIgnoreCase(
                currentUser.getRole()
        );
    }

    public static synchronized boolean hasAnyRole(
            String... roles
    ) {

        if (currentUser == null) {
            return false;
        }

        String currentRole =
                currentUser.getRole();

        for (String role : roles) {

            if (role.equalsIgnoreCase(
                    currentRole
            )) {
                return true;
            }
        }

        return false;
    }

    // =====================================================
    // SESSION CLEAR
    // =====================================================

    public static synchronized void clearSession() {

        if (currentUser != null) {

            System.out.println(
                    "[SESSION] Logout : "
                    + currentUser.getUsername()
            );
        }

        currentUser = null;
    }

    // =====================================================
    // DEBUG
    // =====================================================

    public static synchronized void printSession() {

        System.out.println(
                "===================================="
        );

        if (currentUser == null) {

            System.out.println(
                    "[SESSION] Tidak ada user login."
            );

            System.out.println(
                    "===================================="
            );

            return;
        }

        System.out.println(
                "ID USER      : "
                + currentUser.getIdUser()
        );

        System.out.println(
                "USERNAME     : "
                + currentUser.getUsername()
        );

        System.out.println(
                "NAMA         : "
                + currentUser.getNamaLengkap()
        );

        System.out.println(
                "ROLE         : "
                + currentUser.getRole()
        );

        System.out.println(
                "STATUS       : "
                + currentUser.getStatus()
        );

        System.out.println(
                "===================================="
        );
    }

    // =====================================================
    // SESSION SUMMARY
    // =====================================================

    public static synchronized String getSessionInfo() {

        if (currentUser == null) {
            return "Tidak ada session aktif";
        }

        return currentUser.getNamaLengkap()
                + " ("
                + currentUser.getRole()
                + ")";
    }
}