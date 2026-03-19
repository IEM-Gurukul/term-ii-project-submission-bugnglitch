

import service.*;
import ui.ERMainWindow;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext","true");
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}

        Color bg = new Color(0x0F,0x15,0x20), fg = new Color(0xC8,0xD8,0xE8);
        UIManager.put("Panel.background",     bg); UIManager.put("OptionPane.background",  bg);
        UIManager.put("Label.foreground",     fg); UIManager.put("Button.background",      bg);
        UIManager.put("ComboBox.background",  bg); UIManager.put("ComboBox.foreground",    fg);
        UIManager.put("TextField.background", bg); UIManager.put("TextField.foreground",   fg);
        UIManager.put("Table.background",     bg); UIManager.put("Table.foreground",       fg);
        UIManager.put("ScrollPane.background",bg); UIManager.put("Viewport.background",    bg);
        UIManager.put("TableHeader.background",new Color(0x14,0x1C,0x2B));
        UIManager.put("OptionPane.messageForeground", fg);

        TriageService svc = new TriageService();
        FileService   fs  = new FileService();
        SwingUtilities.invokeLater(() -> new ERMainWindow(svc, fs).setVisible(true));
    }
}
