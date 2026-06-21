# 🏫 Sistem Penerimaan Murid Baru (SPMB) - SMPIT AL FADL

Aplikasi Desktop Pendaftaran dan Seleksi Murid Baru Berbasis **Java Swing (Desktop)** dengan Arsitektur **Model-View-Controller (MVC)** dan integrasi **MySQLpusat**. Aplikasi ini dibangun menggunakan **Apache Ant Compiler** di lingkungan NetBeans IDE[cite: 1, 3].

---

## 🚀 Fitur Utama (Core Features)

*   **Engine Seleksi Otomatis:** Sistem penyaringan massal pendaftar baru secara *real-time* berbasis *Passing Grade* (ambang batas nilai) yang dinamis, terintegrasi langsung dengan sisa kuota daya tampung[cite: 1, 2].
*   **Arsitektur MVC Tangguh:** Pemisahan logika bisnis (*Controller*), representasi data (*Model*), dan antarmuka visual (*View*) untuk memastikan kode mudah dirawat (*maintainable*)[cite: 1].
*   **Dashboard Analytics:** Ringkasan metrik data pendaftar, metrik berkas, dan visualisasi grafik interaktif untuk manajemen admin[cite: 1, 2].
*   **Audit Trail Logging:** Pencatatan otomatis setiap aktivitas login, logout, dan manipulasi data sistem demi keamanan dan transparansi data[cite: 1, 2].
*   **Manajemen Berkas & Pembayaran:** Verifikasi dokumen persyaratan calon siswa serta status administrasi keuangan pendaftaran[cite: 1, 2].

---

## 🛠️ Spesifikasi Teknologi & Dependensi

| Komponen / Library | Versi | Fungsi |
| :--- | :--- | :--- |
| **Java Platform** | JDK 1.8 (Java 8) | Bahasa pemrograman utama & runtime[cite: 1] |
| **DBMS** | MySQL 8.0.28 (XAMPP) | Penyimpanan basis data transaksional[cite: 1, 5] |
| **Build Tool** | Apache Ant | Otomasi kompilasi dan *build artifacts*[cite: 1] |
| **MySQL Connector/J** | 8.0.28 | JDBC Driver konektivitas database[cite: 1] |
| **JFreeChart** | 1.5.3 | Pembuatan grafik analytics pada dashboard[cite: 1] |
| **jBCrypt** | 0.4 | Keamanan enkripsi password pengguna[cite: 2, 4] |

---

## 📦 Cara Instalasi & Menjalankan Proyek (Deployment)

### 1. Persiapan Database (XAMPP)
1. Aktifkan modul **Apache** dan **MySQL** pada XAMPP Control Panel.
2. Buka `http://localhost/phpmyadmin` di browser kamu.
3. Buat database baru bernama `spmb_alfadl`[cite: 1].
4. Pilih database tersebut, masuk ke tab **Import**, lalu pilih file skema yang ada di: `/database/spmb_alfadl.sql`. Klik **Go/Import**.

### 2. Membuka Proyek di NetBeans
1. Buka NetBeans IDE.
2. Pilih menu **File** -> **Open Project**.
3. Arahkan ke folder hasil kloning dari repository ini (`SPMB_SMPIT-ALFADL`).
4. Klik kanan pada nama proyek di panel sebelah kiri, lalu pilih **Clean and Build** (`Shift + F11`)[cite: 1].
5. Tekan tombol **Run** (`F6`) untuk menjalankan aplikasi[cite: 1].

### 3. Login Account Admin
1. Username - superadmin
2. Password - admin123

---

## 👨‍💻 Kontributor & Hak Cipta

*   **Rivaldi** - *Lead Developer & Database Architect* - SMPIT AL FADL[cite: 1, 3]

*Proyek ini dikembangkan secara mandiri untuk memenuhi tugas akhir/skripsi skema Penerimaan Peserta Didik Baru (PPDB) SMPIT AL FADL.*
