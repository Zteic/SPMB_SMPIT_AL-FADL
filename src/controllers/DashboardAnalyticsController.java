package controllers;

import dao.DashboardDAO;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DashboardAnalyticsController {

    private final DashboardDAO dao;

    public DashboardAnalyticsController() {
        this.dao = new DashboardDAO();
    }

    public int getTotalPendaftar() {
        try {
            return dao.getStatMetrics().getOrDefault("total", 0);
        } catch (Exception e) {
            System.err.println("[CTRL] Error getTotalPendaftar: " + e.getMessage());
            return 0;
        }
    }

    public int getPendaftarHariIni() {
        try {
            return dao.getStatMetrics().getOrDefault("hariIni", 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getMenungguVerifikasi() {
        try {
            return dao.getStatMetrics().getOrDefault("pending", 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getPendaftarDitolak() {
        try {
            return dao.getStatMetrics().getOrDefault("ditolak", 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getLolosVerifikasi() {
        try {
            return dao.getStatMetrics().getOrDefault("verified", 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getSisaKuota() {
        try {
            return dao.getStatMetrics().getOrDefault("kuota", 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public Map<String, Integer> getAllMetrics() {
        try {
            return dao.getStatMetrics();
        } catch (Exception e) {
            System.err.println("[CTRL] Error getAllMetrics: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public Map<String, Integer> getJalurComposition() {
        try {
            return dao.getJalurDistribution();
        } catch (Exception e) {
            System.err.println("[CTRL] Error getJalurComposition: " + e.getMessage());
            return new LinkedHashMap<>();
        }
    }

    public LinkedHashMap<String, int[]> getDailyRegistrationTrend(int days) {
        try {
            return dao.getDailyTrend(days);
        } catch (Exception e) {
            System.err.println("[CTRL] Error getDailyRegistrationTrend: " + e.getMessage());
            return new LinkedHashMap<>();
        }
    }

    public List<Object[]> getLatestPendaftar() {
        return getLatestPendaftar(10);
    }

    public List<Object[]> getLatestPendaftar(int limit) {
        try {
            return dao.getLatestPendaftar(limit);
        } catch (Exception e) {
            System.err.println("[CTRL] Error getLatestPendaftar: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Object[]> getNotifikasiTerbaru(int limit) {
        try {
            return dao.getNotifikasiTerbaru(limit);
        } catch (Exception e) {
            System.err.println("[CTRL] Error getNotifikasiTerbaru: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<String, String> getInfoAkademik() {
        try {
            return dao.getInfoAkademik();
        } catch (Exception e) {
            Map<String, String> fallback = new HashMap<>();
            fallback.put("tahun_ajaran", "-");
            fallback.put("gelombang", "-");
            fallback.put("jalur", "-");
            return fallback;
        }
    }

    public Map<String, Integer> getMonitoringBerkas() {
        try {
            return dao.getMonitoringBerkas();
        } catch (Exception e) {
            Map<String, Integer> fallback = new HashMap<>();
            fallback.put("pending", 0);
            fallback.put("verified", 0);
            fallback.put("ditolak", 0);
            return fallback;
        }
    }

    public Map<String, Integer> getMonitoringSeleksi() {
        try {
            return dao.getMonitoringSeleksi();
        } catch (Exception e) {
            Map<String, Integer> fallback = new HashMap<>();
            fallback.put("belum", 0);
            fallback.put("lulus", 0);
            fallback.put("tidak_lulus", 0);
            fallback.put("cadangan", 0);
            return fallback;
        }
    }

    public List<Object[]> getRecentAuditLogs() {
        return getRecentAuditLogs(5);
    }

    public List<Object[]> getRecentAuditLogs(int limit) {
        try {
            return dao.getRecentAuditLogs(limit);
        } catch (Exception e) {
            System.err.println("[CTRL] Error getRecentAuditLogs: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public int getUnreadNotificationCount() {
        try {
            return dao.getUnreadNotificationCount();
        } catch (Exception e) {
            return 0;
        }
    }

    public String formatRelativeTime(Timestamp createdAt) {
        if (createdAt == null) {
            return "-";
        }
        
        long diffMs = System.currentTimeMillis() - createdAt.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs);
        long hours = TimeUnit.MILLISECONDS.toHours(diffMs);

        if (minutes < 1) {
            return "Baru saja";
        }
        if (minutes < 60) {
            return minutes + " menit lalu";
        }
        if (hours < 24) {
            return hours + " jam lalu";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(
            "dd MMM yyyy HH:mm", 
            new Locale("id", "ID")
        );
        return sdf.format(createdAt);
    }
}

