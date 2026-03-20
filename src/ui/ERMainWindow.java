package ui;

import exception.*;
import model.*;
import service.*;
import thread.ERMonitorThread;
import util.InputValidator;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;

import static ui.ERTheme.*;
import static ui.ERComponents.*;

public class ERMainWindow extends JFrame {

    private final TriageService svc;
    private final FileService   fs;

    private JLabel hdrWaiting, hdrCritical, hdrTotal, clockLbl;
    private JTextArea logArea;
    private JPanel content;
    private CardLayout cards;
    private DefaultTableModel queueModel;
    private final JLabel[] dash = new JLabel[6];
    private DefaultTableModel histModel;

    private JComboBox<String> typeCombo;
    private JPanel formHolder;
    private CardLayout formCards;
    private JTextField gName, gComplaint, tName, tInjury, cName;
    private JFormattedTextField gAge, gPain, gTemp, gRR, tAge, tGCS, cAge, cHR, cBP;
    private JComboBox<String> gBlood, tBlood, cBlood;
    private JCheckBox tConscious, tBleeding, cChest, cSTEMI;
    private JTextField lookupField;
    private JPanel lookupResult;

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String[] BLOODS = {"","A+","A-","B+","B-","AB+","AB-","O+","O-"};

    public ERMainWindow(TriageService svc, FileService fs) {
        this.svc = svc; this.fs = fs;
        buildWindow();
        startMonitor();
        log("System started.", "system");
    }

    private void buildWindow() {
        setTitle("ER-Triage — Emergency Room Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
        new Timer(1000, e -> clockLbl.setText(java.time.LocalTime.now().format(TF))).start();
        new Timer(5000, e -> refreshQueue()).start();
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(0x0D, 0x18, 0x25));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        h.setPreferredSize(new Dimension(0, 60));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        left.setOpaque(false);
        JLabel icon = new JLabel("ER");
        icon.setFont(new Font("Consolas", Font.BOLD, 22));
        icon.setForeground(RED);
        left.add(icon);
        JPanel tb = new JPanel();
        tb.setLayout(new BoxLayout(tb, BoxLayout.Y_AXIS));
        tb.setOpaque(false);
        JLabel t1 = new JLabel("ER-TRIAGE");
        t1.setFont(new Font("Consolas", Font.BOLD, 17));
        t1.setForeground(ACCENT);
        JLabel t2 = new JLabel("EMERGENCY ROOM MANAGEMENT SYSTEM");
        t2.setFont(new Font("Consolas", Font.PLAIN, 9));
        t2.setForeground(DIM);
        tb.add(t1); tb.add(t2);
        left.add(tb);

        JPanel centre = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        centre.setOpaque(false);
        hdrWaiting  = pill(centre, "WAITING",     "0", ACCENT);
        hdrCritical = pill(centre, "CRITICAL",    "0", RED);
        hdrTotal    = pill(centre, "TOTAL TODAY", "0", TEXT);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        clockLbl = new JLabel("--:--:--");
        clockLbl.setFont(new Font("Consolas", Font.PLAIN, 13));
        clockLbl.setForeground(DIM);
        clockLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        right.add(clockLbl);

        h.add(left,   BorderLayout.WEST);
        h.add(centre, BorderLayout.CENTER);
        h.add(right,  BorderLayout.EAST);
        return h;
    }

    private JLabel pill(JPanel p, String label, String val, Color vc) {
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        pill.setBackground(BG_CARD);
        pill.setBorder(BorderFactory.createCompoundBorder(line(BORDER, 1), BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        JLabel k = new JLabel(label); k.setFont(new Font("Consolas", Font.PLAIN, 10)); k.setForeground(DIM);
        JLabel v = new JLabel(val);   v.setFont(new Font("Consolas", Font.BOLD,  13)); v.setForeground(vc);
        pill.add(k); pill.add(v); p.add(pill);
        return v;
    }

    // ── Body ──────────────────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_DARK);
        body.add(buildSidebar(), BorderLayout.WEST);
        body.add(buildContent(), BorderLayout.CENTER);
        body.add(buildLog(),     BorderLayout.EAST);
        return body;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(BG_PANEL);
        s.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));
        s.setPreferredSize(new Dimension(200, 0));
        s.add(Box.createVerticalStrut(12));
        s.add(sideLabel("NAVIGATION"));
        s.add(navBtn("Admit Patient", "admit", true,  s));
        s.add(navBtn("Waiting Queue", "queue", false, s));
        s.add(navBtn("ER Dashboard",  "dash",  false, s));
        s.add(navBtn("Lookup Patient","lookup",false, s));
        s.add(Box.createVerticalStrut(16));
        s.add(sideLabel("TRIAGE LEVELS"));
        Object[][] levels = {
            {"ESI-1","Resuscitation",RED},  {"ESI-2","Emergent",ORANGE},
            {"ESI-3","Urgent",BLUE},        {"ESI-4","Less Urgent",GREEN},
            {"ESI-5","Non-Urgent",GRAY}
        };
        for (Object[] l : levels) s.add(legendRow((String)l[0], (String)l[1], (Color)l[2]));
        s.add(Box.createVerticalGlue());
        return s;
    }

    private JLabel sideLabel(String t) {
        JLabel l = new JLabel("  " + t);
        l.setFont(new Font("Consolas", Font.BOLD, 9));
        l.setForeground(DIM);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 0));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return l;
    }

    private JToggleButton navBtn(String text, String card, boolean sel, JPanel sidebar) {
        JToggleButton b = new JToggleButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(new Color(0, 0xD4, 0xFF, 20));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(ACCENT);
                    g2.fillRect(0, 0, 3, getHeight());
                } else if (getModel().isRollover()) {
                    g2.setColor(BG_CARD);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", sel ? Font.BOLD : Font.PLAIN, 13));
        b.setForeground(sel ? ACCENT : TEXT);
        b.setBackground(BG_PANEL);
        b.setBorderPainted(false); b.setContentAreaFilled(false); b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        b.setSelected(sel);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> {
            cards.show(content, card);
            for (Component c : sidebar.getComponents()) {
                if (c instanceof JToggleButton) {
                    JToggleButton tb = (JToggleButton) c;
                    boolean s2 = tb == b;
                    tb.setSelected(s2);
                    tb.setForeground(s2 ? ACCENT : TEXT);
                    tb.setFont(new Font("Segoe UI", s2 ? Font.BOLD : Font.PLAIN, 13));
                }
            }
            if (card.equals("queue")) refreshQueue();
            if (card.equals("dash"))  refreshDash();
        });
        return b;
    }

    private JPanel legendRow(String esi, String name, Color c) {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        r.setOpaque(false);
        r.setAlignmentX(LEFT_ALIGNMENT);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel dot = new JLabel("*"); dot.setFont(new Font("Segoe UI", Font.BOLD, 10)); dot.setForeground(c);
        JLabel e   = new JLabel(esi); e.setFont(new Font("Consolas", Font.BOLD, 10)); e.setForeground(c);
        JLabel nm  = new JLabel(name); nm.setFont(F_SMALL); nm.setForeground(DIM);
        r.add(dot); r.add(e); r.add(nm);
        return r;
    }

    // ── Content area ──────────────────────────────────────────────────────────
    private JPanel buildContent() {
        cards = new CardLayout();
        content = new JPanel(cards);
        content.setBackground(BG_DARK);
        content.add(buildAdmit(),  "admit");
        content.add(buildQueue(),  "queue");
        content.add(buildDash(),   "dash");
        content.add(buildLookup(), "lookup");
        return content;
    }

    // ── Admit Panel ───────────────────────────────────────────────────────────
    private JPanel buildAdmit() {
        JPanel out = new JPanel(new BorderLayout(0, 12));
        out.setBackground(BG_DARK);
        out.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        top.setOpaque(false);
        top.add(lblTitle("Admit New Patient"));
        top.add(Box.createHorizontalStrut(20));
        top.add(lbl("Type:"));
        typeCombo = combo(new String[]{"General", "Trauma", "Cardiac"});
        typeCombo.setPreferredSize(new Dimension(140, 34));
        top.add(typeCombo);

        formCards = new CardLayout();
        formHolder = new JPanel(formCards);
        formHolder.setBackground(BG_DARK);
        formHolder.add(generalForm(), "General");
        formHolder.add(traumaForm(),  "Trauma");
        formHolder.add(cardiacForm(), "Cardiac");
        typeCombo.addActionListener(e -> formCards.show(formHolder, (String) typeCombo.getSelectedItem()));

        out.add(top,        BorderLayout.NORTH);
        out.add(formHolder, BorderLayout.CENTER);
        return out;
    }

    private JPanel generalForm() {
        gName = field(); gAge = numField(); gBlood = combo(BLOODS);
        gComplaint = field(); gPain = numField(); gTemp = numField(); gRR = numField();
        return form(ACCENT, new Object[][]{
            {"Full Name", gName}, {"Age", gAge}, {"Blood Type", gBlood}, {"Complaint", gComplaint},
            {"Pain (0-10)", gPain}, {"Temp (C)", gTemp}, {"Resp Rate/min", gRR}
        }, this::admitGeneral);
    }

    private JPanel traumaForm() {
        tName = field(); tAge = numField(); tBlood = combo(BLOODS);
        tInjury = field(); tGCS = numField();
        tConscious = check("Patient is conscious"); tConscious.setSelected(true);
        tBleeding  = check("Internal bleeding suspected");
        return form(ORANGE, new Object[][]{
            {"Full Name", tName}, {"Age", tAge}, {"Blood Type", tBlood}, {"Injury Type", tInjury},
            {"GCS (3-15)", tGCS}, {"", tConscious}, {"", tBleeding}
        }, this::admitTrauma);
    }

    private JPanel cardiacForm() {
        cName = field(); cAge = numField(); cBlood = combo(BLOODS);
        cHR = numField(); cBP = numField();
        cChest = check("Chest pain"); cSTEMI = check("ST-Elevation (STEMI)");
        return form(RED, new Object[][]{
            {"Full Name", cName}, {"Age", cAge}, {"Blood Type", cBlood},
            {"Heart Rate (bpm)", cHR}, {"Systolic BP", cBP}, {"", cChest}, {"", cSTEMI}
        }, this::admitCardiac);
    }

    private JPanel form(Color accent, Object[][] rows, Runnable onAdmit) {
        JPanel card = accentCard(accent);
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(BG_CARD);
        body.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.add(body, BorderLayout.CENTER);

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 8, 6, 8);
        int col = 0, row = 0;
        for (Object[] r : rows) {
            String lbl = (String) r[0];
            JComponent fld = (JComponent) r[1];
            JPanel cell = new JPanel(new BorderLayout(0, 4));
            cell.setOpaque(false);
            if (!lbl.isEmpty()) cell.add(lblDim(lbl), BorderLayout.NORTH);
            fld.setPreferredSize(new Dimension(220, 34));
            cell.add(fld, BorderLayout.CENTER);
            g.gridx = col; g.gridy = row; g.gridwidth = 1; g.weightx = 0.5;
            body.add(cell, g);
            col++;
            if (col > 1) { col = 0; row++; }
        }
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);
        JButton clr = btnGhost("Clear");
        JButton adm = btnPrimary("Admit & Triage");
        clr.addActionListener(e -> clearForm());
        adm.addActionListener(e -> onAdmit.run());
        btns.add(clr); btns.add(adm);
        g.gridx = 0; g.gridy = row + 1; g.gridwidth = 2; g.insets = new Insets(16, 8, 0, 8);
        body.add(btns, g);
        return card;
    }

    // ── Queue Panel ───────────────────────────────────────────────────────────
    private JPanel buildQueue() {
        JPanel out = new JPanel(new BorderLayout(0, 12));
        out.setBackground(BG_DARK);
        out.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblTitle("Waiting Queue"), BorderLayout.WEST);
        JButton next = btnDanger("Call Next Patient");
        next.addActionListener(e -> callNext());
        top.add(next, BorderLayout.EAST);
        out.add(top, BorderLayout.NORTH);

        queueModel = new DefaultTableModel(new String[]{"#","ID","Name","Type","Triage","Waiting","Action"}, 0) {
            public boolean isCellEditable(int r, int c) { return c == 6; }
        };
        JTable t = new JTable(queueModel);
        styleTable(t);
        t.getColumn("Action").setCellRenderer(new BtnRenderer());
        t.getColumn("Action").setCellEditor(new BtnEditor());
        t.getColumnModel().getColumn(0).setMaxWidth(40);
        t.getColumnModel().getColumn(1).setMaxWidth(90);
        t.getColumnModel().getColumn(3).setMaxWidth(80);
        t.getColumnModel().getColumn(5).setMaxWidth(90);
        t.getColumnModel().getColumn(6).setMaxWidth(110);
        out.add(scroll(t), BorderLayout.CENTER);
        return out;
    }

    // ── Dashboard Panel ───────────────────────────────────────────────────────
    private JPanel buildDash() {
        JPanel out = new JPanel(new BorderLayout(0, 16));
        out.setBackground(BG_DARK);
        out.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        out.add(lblTitle("ER Dashboard"), BorderLayout.NORTH);

        JPanel statCards = new JPanel(new GridLayout(1, 5, 12, 0));
        statCards.setOpaque(false);
        Color[]  cls = {RED, ORANGE, BLUE, GREEN, GRAY};
        String[] nms = {"Resuscitation","Emergent","Urgent","Less Urgent","Non-Urgent"};
        for (int i = 1; i <= 5; i++) {
            JPanel c = accentCard(cls[i-1]);
            JPanel in = new JPanel(new GridBagLayout());
            in.setBackground(BG_CARD);
            in.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));
            c.add(in, BorderLayout.CENTER);
            dash[i] = new JLabel("0");
            dash[i].setFont(new Font("Consolas", Font.BOLD, 36));
            dash[i].setForeground(cls[i-1]);
            dash[i].setHorizontalAlignment(SwingConstants.CENTER);
            JLabel nm = new JLabel("ESI-" + i + " " + nms[i-1]);
            nm.setFont(new Font("Consolas", Font.PLAIN, 9));
            nm.setForeground(DIM);
            nm.setHorizontalAlignment(SwingConstants.CENTER);
            GridBagConstraints gc = new GridBagConstraints();
            gc.gridx = 0; gc.gridy = 0; in.add(dash[i], gc);
            gc.gridy = 1; gc.insets = new Insets(4, 0, 0, 0); in.add(nm, gc);
            statCards.add(c);
        }
        out.add(statCards, BorderLayout.NORTH);

        histModel = new DefaultTableModel(
            new String[]{"ID","Name","Age","Type","Triage","Status","Arrived"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable ht = new JTable(histModel);
        styleTable(ht);
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setOpaque(false);
        wrap.add(lblTitle("All Patients - History"), BorderLayout.NORTH);
        wrap.add(scroll(ht), BorderLayout.CENTER);
        out.add(wrap, BorderLayout.CENTER);
        return out;
    }

    // ── Lookup Panel ──────────────────────────────────────────────────────────
    private JPanel buildLookup() {
        JPanel out = new JPanel(new BorderLayout(0, 16));
        out.setBackground(BG_DARK);
        out.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        out.add(lblTitle("Patient Lookup"), BorderLayout.NORTH);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setOpaque(false);
        lookupField = field();
        lookupField.setPreferredSize(new Dimension(240, 34));
        JButton go = btnPrimary("Search");
        go.addActionListener(e -> doLookup());
        lookupField.addActionListener(e -> doLookup());
        bar.add(lblDim("Patient ID")); bar.add(Box.createHorizontalStrut(6));
        bar.add(lookupField); bar.add(go);

        lookupResult = new JPanel(new BorderLayout());
        lookupResult.setOpaque(false);
        out.add(bar,          BorderLayout.CENTER);
        out.add(lookupResult, BorderLayout.SOUTH);
        return out;
    }

    // ── Log Panel ─────────────────────────────────────────────────────────────
    private JPanel buildLog() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER));
        p.setPreferredSize(new Dimension(280, 0));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        hdr.setBackground(BG_CARD);
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        JLabel dot = new JLabel("*"); dot.setForeground(GREEN); dot.setFont(new Font("Consolas", Font.BOLD, 14));
        new Timer(800, e -> dot.setVisible(!dot.isVisible())).start();
        JLabel title = new JLabel("ACTIVITY LOG");
        title.setFont(new Font("Consolas", Font.BOLD, 10));
        title.setForeground(ACCENT);
        hdr.add(dot); hdr.add(title);

        logArea = logArea();
        JScrollPane sp = scroll(logArea);
        sp.setBorder(BorderFactory.createEmptyBorder());
        p.add(hdr, BorderLayout.NORTH);
        p.add(sp,  BorderLayout.CENTER);
        return p;
    }

    // ── Admit Logic ───────────────────────────────────────────────────────────
    private void admitGeneral() {
        try {
            GeneralPatient p = new GeneralPatient(
                InputValidator.validateNonEmpty(gName.getText().trim(), "name"),
                InputValidator.validateAge(parseInt(gAge, "age")),
                validateBlood(gBlood),
                InputValidator.validateNonEmpty(gComplaint.getText().trim(), "complaint"),
                InputValidator.validatePainScale(parseInt(gPain, "pain")),
                InputValidator.validateTemperature(parseDouble(gTemp, "temp")),
                parseInt(gRR, "respiratoryRate"));
            admit(p);
        } catch (Exception e) { err(e.getMessage()); }
    }

    private void admitTrauma() {
        try {
            TraumaPatient p = new TraumaPatient(
                InputValidator.validateNonEmpty(tName.getText().trim(), "name"),
                InputValidator.validateAge(parseInt(tAge, "age")),
                validateBlood(tBlood),
                InputValidator.validateNonEmpty(tInjury.getText().trim(), "injury"),
                InputValidator.validateGCS(parseInt(tGCS, "GCS")),
                tConscious.isSelected(), tBleeding.isSelected());
            admit(p);
        } catch (Exception e) { err(e.getMessage()); }
    }

    private void admitCardiac() {
        try {
            CardiacPatient p = new CardiacPatient(
                InputValidator.validateNonEmpty(cName.getText().trim(), "name"),
                InputValidator.validateAge(parseInt(cAge, "age")),
                validateBlood(cBlood),
                InputValidator.validateHeartRate(parseInt(cHR, "heartRate")),
                parseInt(cBP, "systolicBP"),
                cChest.isSelected(), cSTEMI.isSelected());
            admit(p);
        } catch (Exception e) { err(e.getMessage()); }
    }

    private void admit(Patient p) throws DuplicatePatientException {
        svc.admitPatient(p);
        fs.logActivity("Admitted: " + p.getPatientId() + " - " + p.getName());
        updateStats(); refreshQueue(); clearForm();
        log("Admitted: " + p.getName() + " [" + p.getPatientId() + "] ESI-" + p.getTriageLevel().getPriority(), "admit");
        info("Admitted: " + p.getName() + " - " + p.getTriageLevel().name());
    }

    // ── Queue Actions ─────────────────────────────────────────────────────────
    private void callNext() {
        try {
            Patient p = svc.callNextPatient();
            fs.logActivity("Called: " + p.getPatientId() + " - " + p.getName());
            updateStats(); refreshQueue();
            log("Called: " + p.getName() + " [" + p.getPatientId() + "]", "call");
            showDetail(p);
        } catch (PatientNotFoundException e) { info("No patients waiting."); }
    }

    public void discharge(String id) {
        try {
            svc.dischargePatient(id);
            fs.logActivity("Discharged: " + id);
            updateStats(); refreshQueue();
            log("Discharged: " + id, "discharge");
        } catch (PatientNotFoundException e) { err(e.getMessage()); }
    }

    // ── Lookup ────────────────────────────────────────────────────────────────
    private void doLookup() {
        String id = lookupField.getText().trim().toUpperCase();
        lookupResult.removeAll();
        try {
            lookupResult.add(patientCard(svc.findPatient(id)), BorderLayout.NORTH);
        } catch (PatientNotFoundException e) {
            JLabel l = lbl("No patient found: " + id);
            l.setForeground(RED);
            l.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
            lookupResult.add(l, BorderLayout.NORTH);
        }
        lookupResult.revalidate(); lookupResult.repaint();
    }

    private JPanel patientCard(Patient p) {
        JPanel card = accentCard(level(p.getTriageLevel()));
        JPanel body = new JPanel(new GridLayout(0, 2, 12, 8));
        body.setBackground(BG_CARD);
        body.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.add(body, BorderLayout.CENTER);
        dr(body, "PATIENT ID", p.getPatientId());
        dr(body, "NAME",       p.getName());
        dr(body, "AGE/BLOOD",  p.getAge() + " / " + p.getBloodType());
        dr(body, "TRIAGE",     p.getTriageLevel().name() + " - " + p.getTriageLevel().getDescription(), level(p.getTriageLevel()));
        dr(body, "STATUS",     p.getStatus().name());
        dr(body, "ARRIVED",    p.getArrivalTime().format(TF));
        dr(body, "SUMMARY",    p.getClinicalSummary(), DIM);
        if (p.getStatus() == Patient.PatientStatus.WAITING) {
            JPanel br = new JPanel(new FlowLayout(FlowLayout.LEFT));
            br.setBackground(BG_CARD);
            JButton d = btnDanger("Discharge");
            d.addActionListener(e -> { discharge(p.getPatientId()); doLookup(); });
            br.add(d);
            card.add(br, BorderLayout.SOUTH);
        }
        return card;
    }

    private void dr(JPanel p, String k, String v)           { p.add(lblDim(k)); JLabel l = lbl(v); l.setFont(F_MONO_B); p.add(l); }
    private void dr(JPanel p, String k, String v, Color c)  { p.add(lblDim(k)); JLabel l = lbl(v); l.setForeground(c); l.setFont(F_SMALL); p.add(l); }

    // ── Refresh ───────────────────────────────────────────────────────────────
    private void refreshQueue() {
        SwingUtilities.invokeLater(() -> {
            queueModel.setRowCount(0);
            int rank = 1;
            for (Patient p : svc.getWaitingPatients()) {
                long s = (System.currentTimeMillis() - java.sql.Timestamp.valueOf(p.getArrivalTime()).getTime()) / 1000;
                queueModel.addRow(new Object[]{
                    rank++, p.getPatientId(), p.getName(),
                    p.getClass().getSimpleName().replace("Patient",""),
                    "ESI-" + p.getTriageLevel().getPriority() + " " + p.getTriageLevel().name(),
                    s < 60 ? s + "s" : s/60 + "m " + s%60 + "s",
                    p.getPatientId()
                });
            }
            updateStats();
        });
    }

    private void refreshDash() {
        SwingUtilities.invokeLater(() -> {
            java.util.Map<TriageLevel,Long> stats = svc.getTriageLevelStats();
            for (int i = 1; i <= 5; i++)
                dash[i].setText(String.valueOf(stats.getOrDefault(TriageLevel.values()[i-1], 0L)));
            if (histModel != null) {
                histModel.setRowCount(0);
                for (Patient p : svc.getAllPatients())
                    histModel.addRow(new Object[]{
                        p.getPatientId(), p.getName(), p.getAge(),
                        p.getClass().getSimpleName().replace("Patient",""),
                        "ESI-" + p.getTriageLevel().getPriority(),
                        p.getStatus().name(), p.getArrivalTime().format(TF)
                    });
            }
        });
    }

    private void updateStats() {
        hdrWaiting.setText(String.valueOf(svc.getWaitingCount()));
        long cr = svc.getWaitingPatients().stream().filter(p -> p.getTriageLevel() == TriageLevel.RESUSCITATION).count();
        hdrCritical.setText(String.valueOf(cr));
        hdrTotal.setText(String.valueOf(svc.getAllPatients().size()));
    }

    // ── Monitor Thread ────────────────────────────────────────────────────────
    private void startMonitor() {
        ERMonitorThread m = new ERMonitorThread(svc, fs);
        Thread t = new Thread(m, "ER-Monitor");
        t.setDaemon(true); t.start();
        new Timer(5000, e -> {
            long now = System.currentTimeMillis();
            for (Patient p : svc.getWaitingPatients()) {
                long w = (now - java.sql.Timestamp.valueOf(p.getArrivalTime()).getTime()) / 1000;
                if (p.getTriageLevel() == TriageLevel.RESUSCITATION && w > 120)
                    log("CRITICAL: " + p.getName() + " [" + p.getPatientId() + "] " + w + "s!", "alert");
                else if (p.getTriageLevel() == TriageLevel.EMERGENT && w > 300)
                    log("URGENT: " + p.getName() + " [" + p.getPatientId() + "] " + w + "s", "alert");
            }
            refreshQueue();
        }).start();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void showDetail(Patient p) {
        JDialog d = new JDialog(this, "Patient Called for Treatment", true);
        d.setSize(500, 400); d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(BG_DARK);
        d.add(patientCard(p));
        d.setVisible(true);
    }

    public void log(String msg, String type) {
        SwingUtilities.invokeLater(() -> {
            String t   = java.time.LocalTime.now().format(TF);
            String pfx = type.equals("admit") ? "[ADMIT]" : type.equals("call") ? "[CALL]" :
                         type.equals("discharge") ? "[DISC]" : type.equals("alert") ? "[ALERT]" : "[SYS]";
            logArea.append("[" + t + "] " + pfx + " " + msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void err(String m)  { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
    private void info(String m) { JOptionPane.showMessageDialog(this, m, "Info",  JOptionPane.INFORMATION_MESSAGE); }

    private int parseInt(JTextField f, String n) {
        try { return Integer.parseInt(f.getText().trim()); }
        catch (NumberFormatException e) { throw new InvalidTriageDataException(n, "Must be a whole number."); }
    }

    private double parseDouble(JTextField f, String n) {
        try { return Double.parseDouble(f.getText().trim()); }
        catch (NumberFormatException e) { throw new InvalidTriageDataException(n, "Must be a decimal."); }
    }

    private String validateBlood(JComboBox<String> c) {
        String v = (String) c.getSelectedItem();
        if (v == null || v.isBlank()) throw new InvalidTriageDataException("bloodType", "Please select a blood type.");
        return v;
    }

    private void clearForm() {
        String type = (String) typeCombo.getSelectedItem();
        if ("General".equals(type)) {
            gName.setText(""); gComplaint.setText(""); gAge.setText(""); gPain.setText(""); gTemp.setText(""); gRR.setText(""); gBlood.setSelectedIndex(0);
        } else if ("Trauma".equals(type)) {
            tName.setText(""); tInjury.setText(""); tAge.setText(""); tGCS.setText(""); tBlood.setSelectedIndex(0); tConscious.setSelected(true); tBleeding.setSelected(false);
        } else {
            cName.setText(""); cAge.setText(""); cHR.setText(""); cBP.setText(""); cBlood.setSelectedIndex(0); cChest.setSelected(false); cSTEMI.setSelected(false);
        }
    }

    // ── Table button renderer / editor ────────────────────────────────────────
    class BtnRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            JButton b = btnDanger("Discharge"); b.setOpaque(true); return b;
        }
    }

    class BtnEditor extends DefaultCellEditor {
        private JButton btn;
        private String  pid;
        BtnEditor() {
            super(new JCheckBox());
            btn = btnDanger("Discharge");
            btn.addActionListener(e -> fireEditingStopped());
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) { pid = (String) v; return btn; }
        public Object getCellEditorValue() { discharge(pid); return pid; }
    }
}
