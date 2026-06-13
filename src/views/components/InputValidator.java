package views.components;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Helper Validator Dunia Nyata - SPMB SMPIT AL FADL
 * Mengunci input keyboard agar murni hanya menerima angka tanpa membuat UI freeze.
 */
public class InputValidator {

    /**
     * Memaksa JTextField JComboBox/Text murni hanya menerima angka digital 0-9
     */
    public static void kunciHanyaAngka(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if (string.matches("[0-9]*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if (text.matches("[0-9]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    /**
     * Membatasi jumlah karakter maksimal (Misal: 10 untuk NISN, 16 untuk NIK)
     */
    public static void batasiJumlahKarakter(JTextField field, int maxLen) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLen = fb.getDocument().getLength();
                int nextLen = currentLen - length + (text != null ? text.length() : 0);
                if (nextLen <= maxLen && (text == null || text.matches("[0-9]*"))) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}