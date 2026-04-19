package HMS.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UIUtils {

    // Colors
    public static final Color SIDEBAR_BG = new Color(44, 62, 80);
    public static final Color SIDEBAR_HOVER = new Color(52, 73, 94);
    public static final Color SIDEBAR_TEXT = new Color(236, 240, 241);
    public static final Color MAIN_BG = new Color(245, 246, 250);
    public static final Color ACCENT_COLOR = new Color(52, 152, 219);
    public static final Color ACCENT_HOVER = new Color(41, 128, 185);
    public static final Color DANGER_COLOR = new Color(231, 76, 60);
    public static final Color TEXT_PRIMARY = new Color(45, 52, 54);
    
    // Fonts
    public static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public static void styleButton(JButton btn, boolean isPrimary) {
        btn.setFont(MAIN_FONT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        if (isPrimary) {
            btn.setBackground(ACCENT_COLOR);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(223, 230, 233));
            btn.setForeground(TEXT_PRIMARY);
        }
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleDangerButton(JButton btn) {
        btn.setFont(MAIN_FONT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setBackground(DANGER_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleSidebarButton(JButton btn) {
        btn.setFont(HEADER_FONT);
        btn.setForeground(SIDEBAR_TEXT);
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(15, 20, 15, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_BG);
            }
        });
    }

    public static void styleTable(JTable table) {
        table.setFont(MAIN_FONT);
        table.setRowHeight(30);
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(new Color(223, 230, 233));
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(new Color(189, 195, 199));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
    }
}
