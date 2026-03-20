package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import static ui.ERTheme.*;;

public final class ERComponents {
    private ERComponents() {}

    // ── Panels ────────────────────────────────────────────────────────────────
    public static JPanel dark()  { return bg(new JPanel(), BG_PANEL); }
    public static JPanel card()  { JPanel p = bg(new JPanel(), BG_CARD); p.setBorder(line(BORDER,1)); return p; }

    public static JPanel accentCard(Color c) {
        JPanel out = bg(new JPanel(new BorderLayout()), BG_CARD);
        out.setBorder(line(c,1));
        JPanel stripe = bg(new JPanel(), c);
        stripe.setPreferredSize(new Dimension(0,3));
        out.add(stripe, BorderLayout.NORTH);
        return out;
    }

    private static JPanel bg(JPanel p, Color c) { p.setBackground(c); p.setForeground(TEXT); return p; }

    // ── Labels ────────────────────────────────────────────────────────────────
    public static JLabel lbl(String t)              { return fl(new JLabel(t), TEXT,   F_BODY); }
    public static JLabel lblDim(String t)           { return fl(new JLabel(t.toUpperCase()), DIM, new Font("Consolas",Font.PLAIN,10)); }
    public static JLabel lblTitle(String t)         { return fl(new JLabel(t), ACCENT, F_TITLE); }
    private static JLabel fl(JLabel l, Color c, Font f) { l.setForeground(c); l.setFont(f); return l; }

    // ── Inputs ────────────────────────────────────────────────────────────────
    public static JTextField field() {
        JTextField f = new JTextField();
        styleInput(f); f.setFont(F_MONO); f.setCaretColor(ACCENT);
        focusBorder(f);
        return f;
    }

    public static JFormattedTextField numField() {
        JFormattedTextField f = new JFormattedTextField();
        styleInput(f); f.setFont(F_MONO); f.setCaretColor(ACCENT);
        focusBorder(f);
        return f;
    }

    private static void styleInput(JTextField f) {
        f.setBackground(BG_PANEL); f.setForeground(TEXT);
        f.setBorder(inputBorder(BORDER));
    }

    private static void focusBorder(JTextField f) {
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.setBorder(inputBorder(ACCENT2)); }
            public void focusLost (FocusEvent e)  { f.setBorder(inputBorder(BORDER));  }
        });
    }

    private static Border inputBorder(Color c) {
        return BorderFactory.createCompoundBorder(line(c,1), BorderFactory.createEmptyBorder(6,10,6,10));
    }

    public static <T> JComboBox<T> combo(T[] items) {
        JComboBox<T> c = new JComboBox<>(items);
        c.setBackground(BG_PANEL); c.setForeground(TEXT); c.setFont(F_MONO);
        c.setBorder(line(BORDER,1));
        c.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean f) {
                super.getListCellRendererComponent(l,v,i,sel,f);
                setBackground(sel ? BORDER : BG_PANEL); setForeground(TEXT); setFont(F_MONO);
                setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
                return this;
            }
        });
        return c;
    }

    public static JCheckBox check(String t) {
        JCheckBox c = new JCheckBox(t); c.setBackground(BG_CARD); c.setForeground(TEXT); c.setFont(F_BODY); c.setFocusPainted(false); return c;
    }

    // ── Buttons ───────────────────────────────────────────────────────────────
    public static JButton btnPrimary(String t) { return btn(t, ACCENT,  BG_DARK,  null); }
    public static JButton btnDanger (String t) { return btn(t, RED,     RED,      RED);  }
    public static JButton btnGhost  (String t) { return btn(t, TEXT,    null,     BORDER); }

    private static JButton btn(String text, Color fg, Color bg, Color border) {
        JButton b = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (bg != null) {
                    Color fill = bg == RED
                        ? new Color(bg.getRed(),bg.getGreen(),bg.getBlue(), getModel().isRollover()?60:30)
                        : (getModel().isPressed() ? ACCENT2 : getModel().isRollover() ? new Color(0,187,224) : bg);
                    g2.setColor(fill); g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
                }
                if (border != null) {
                    g2.setColor(border); g2.setStroke(new BasicStroke(1));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,6,6);
                }
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setForeground(fg); b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setFocusPainted(false); b.setBorderPainted(false); b.setContentAreaFilled(false); b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(b.getPreferredSize().width+24, 34));
        return b;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    public static void styleTable(JTable t) {
        t.setBackground(BG_PANEL); t.setForeground(TEXT); t.setFont(F_BODY);
        t.setGridColor(BORDER); t.setRowHeight(36);
        t.setSelectionBackground(new Color(0x1E,0x2D,0x45)); t.setSelectionForeground(ACCENT);
        t.setShowHorizontalLines(true); t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0,0)); t.setFillsViewportHeight(true);
        JTableHeader h = t.getTableHeader();
        h.setBackground(BG_CARD); h.setForeground(DIM);
        h.setFont(new Font("Consolas",Font.BOLD,10));
        h.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
        h.setReorderingAllowed(false);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object v, boolean sel, boolean f, int r, int c) {
                super.getTableCellRendererComponent(tbl,v,sel,f,r,c);
                setBackground(sel ? new Color(0x1E,0x2D,0x45) : r%2==0 ? BG_PANEL : BG_CARD);
                setForeground(sel ? ACCENT : TEXT);
                setBorder(BorderFactory.createEmptyBorder(0,12,0,12));
                return this;
            }
        });
    }

    // ── Scroll / TextArea ─────────────────────────────────────────────────────
    public static JScrollPane scroll(Component v) {
        JScrollPane sp = new JScrollPane(v);
        sp.setBackground(BG_PANEL); sp.getViewport().setBackground(BG_PANEL);
        sp.setBorder(line(BORDER,1));
        sp.getVerticalScrollBar().setUI(darkSB()); sp.getHorizontalScrollBar().setUI(darkSB());
        return sp;
    }

    public static JTextArea logArea() {
        JTextArea a = new JTextArea(); a.setBackground(BG_CARD);
        a.setForeground(new Color(0x7A,0xAA,0xBB)); a.setFont(F_MONO);
        a.setEditable(false); a.setLineWrap(true); a.setWrapStyleWord(true);
        a.setBorder(BorderFactory.createEmptyBorder(8,10,8,10)); return a;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    public static Border line(Color c, int w) { return BorderFactory.createLineBorder(c, w); }

    private static BasicScrollBarUI darkSB() {
        return new BasicScrollBarUI() {
            protected void configureScrollBarColors() { thumbColor = BORDER; trackColor = BG_PANEL; }
            protected JButton createDecreaseButton(int o) { return ghost(); }
            protected JButton createIncreaseButton(int o) { return ghost(); }
            private JButton ghost() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x2E,0x45,0x60));
                g2.fillRoundRect(r.x+2,r.y+2,r.width-4,r.height-4,4,4); g2.dispose();
            }
            protected void paintTrack(Graphics g, JComponent c, Rectangle r) { g.setColor(BG_PANEL); g.fillRect(r.x,r.y,r.width,r.height); }
        };
    }
}
