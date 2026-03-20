package ui;

import model.TriageLevel;
import java.awt.*;

public final class ERTheme {
    private ERTheme() {}

    public static final Color
        BG_DARK  = new Color(0x0A,0x0E,0x14), BG_PANEL = new Color(0x0F,0x15,0x20),
        BG_CARD  = new Color(0x14,0x1C,0x2B), BORDER   = new Color(0x1E,0x2D,0x45),
        ACCENT   = new Color(0x00,0xD4,0xFF), ACCENT2  = new Color(0x00,0x99,0xCC),
        TEXT     = new Color(0xC8,0xD8,0xE8), DIM      = new Color(0x4A,0x60,0x70),
        RED      = new Color(0xFF,0x3B,0x3B), ORANGE   = new Color(0xFF,0x8C,0x00),
        BLUE     = new Color(0x3B,0x8B,0xFF), GREEN    = new Color(0x00,0xC8,0x51),
        GRAY     = new Color(0x88,0x99,0xAA);

    public static final Font
        F_TITLE  = new Font("Segoe UI", Font.BOLD,  20),
        F_HEADER = new Font("Segoe UI", Font.BOLD,  13),
        F_BODY   = new Font("Segoe UI", Font.PLAIN, 12),
        F_SMALL  = new Font("Segoe UI", Font.PLAIN, 11),
        F_MONO   = new Font("Consolas", Font.PLAIN, 11),
        F_MONO_B = new Font("Consolas", Font.BOLD,  12);

    public static Color level(TriageLevel t) {
        int p = t.getPriority();
        if (p == 1) return RED;
        if (p == 2) return ORANGE;
        if (p == 3) return BLUE;
        if (p == 4) return GREEN;
        return GRAY;
    }
}
