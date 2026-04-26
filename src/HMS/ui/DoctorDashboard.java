package HMS.ui;

import HMS.model.*;
import HMS.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorDashboard extends JFrame {

    private final AppointmentManager appointmentManager;
    private final PatientManager patientManager;
    // Phase 3 managers — used in Step 4 UI tabs
    private final PrescriptionManager prescriptionManager;
    private final BillingManager billingManager;
    private final Runnable onLogout;

    private Doctor doctor;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private DefaultTableModel appointmentTableModel;
    private JTable appointmentTable;

    public DoctorDashboard(User user, DoctorManager doctorManager,
                           AppointmentManager appointmentManager, PatientManager patientManager,
                           PrescriptionManager prescriptionManager, BillingManager billingManager,
                           Runnable onLogout) {
        this.appointmentManager = appointmentManager;
        this.patientManager = patientManager;
        this.prescriptionManager = prescriptionManager;
        this.billingManager = billingManager;
        this.onLogout = onLogout;

        this.doctor = doctorManager.findDoctor(user.getLinkedId());

        initFrame();
        initComponents();
    }

    private void initFrame() {
        setTitle("HospiCare - Doctor Dashboard");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        // --- Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBackground(UIUtils.SIDEBAR_BG);
        sidebar.setLayout(new BorderLayout());

        // Brand Area
        JLabel brandLabel = new JLabel("HOSPICARE", SwingConstants.CENTER);
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setBorder(new EmptyBorder(30, 10, 30, 10));
        sidebar.add(brandLabel, BorderLayout.NORTH);

        // Navigation Buttons
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(UIUtils.SIDEBAR_BG);
        navPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton btnProfile = createNavButton("My Profile", "PROFILE");
        JButton btnAppointments = createNavButton("My Appointments", "APPOINTMENTS");
        JButton btnPrescribe = createNavButton("Prescribe", "PRESCRIBE");
        JButton btnHistory = createNavButton("Patient History", "HISTORY");
        JButton btnLookup = createNavButton("Patient Lookup", "LOOKUP");

        navPanel.add(btnProfile);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnAppointments);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnPrescribe);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnHistory);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnLookup);

        sidebar.add(navPanel, BorderLayout.CENTER);

        // Footer Area
        JButton btnLogout = new JButton("Logout");
        UIUtils.styleSidebarButton(btnLogout);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnLogout.addActionListener(e -> {
            this.dispose();
            if (onLogout != null) onLogout.run();
        });

        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(UIUtils.SIDEBAR_BG);
        footerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        footerPanel.add(btnLogout);
        sidebar.add(footerPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);

        // --- Main Content Area (CardLayout) ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UIUtils.MAIN_BG);

        // Build Panels
        mainContentPanel.add(createProfilePanel(), "PROFILE");
        mainContentPanel.add(createAppointmentsPanel(), "APPOINTMENTS");
        mainContentPanel.add(createPrescribePanel(), "PRESCRIBE");
        mainContentPanel.add(createHistoryPanel(), "HISTORY");
        mainContentPanel.add(createLookupPanel(), "LOOKUP");

        add(mainContentPanel, BorderLayout.CENTER);

        // Show default
        cardLayout.show(mainContentPanel, "PROFILE");
    }

    private JButton createNavButton(String title, String cardName) {
        JButton btn = new JButton(title);
        UIUtils.styleSidebarButton(btn);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.addActionListener(e -> cardLayout.show(mainContentPanel, cardName));
        return btn;
    }

    // ==================== PANELS ====================

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("My Profile");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBackground(Color.WHITE);
        details.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(223, 230, 233)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        if (doctor != null) {
            details.add(createDetailRow("Doctor ID:", doctor.getId()));
            details.add(createDetailRow("Name:", doctor.getName()));
            details.add(createDetailRow("Age:", String.valueOf(doctor.getAge())));
            details.add(createDetailRow("Specialization:", doctor.getSpecialization()));
        } else {
            details.add(new JLabel("Profile not found. Please contact administration."));
        }

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.setBackground(UIUtils.MAIN_BG);
        wrapper.add(details);
        
        panel.add(wrapper, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setBackground(Color.WHITE);
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLabel.setPreferredSize(new Dimension(140, 25));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(UIUtils.MAIN_FONT);
        
        row.add(lblLabel);
        row.add(lblValue);
        return row;
    }

    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("My Scheduled Appointments");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Appt ID", "Patient ID", "Date", "Status"};
        appointmentTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        appointmentTable = new JTable(appointmentTableModel);
        UIUtils.styleTable(appointmentTable);

        refreshAppointments();

        JScrollPane scroll = new JScrollPane(appointmentTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233)));
        panel.add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setBackground(UIUtils.MAIN_BG);
        
        JButton btnDone = new JButton("Mark as Done");
        UIUtils.styleButton(btnDone, true);
        btnDone.setBackground(new Color(46, 204, 113)); // Green
        btnDone.addActionListener(e -> updateSelectedAppointmentStatus("Done"));

        JButton btnCancel = new JButton("Cancel Appointment");
        UIUtils.styleDangerButton(btnCancel);
        btnCancel.addActionListener(e -> updateSelectedAppointmentStatus("Cancelled"));

        bottomPanel.add(btnDone);
        bottomPanel.add(btnCancel);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshAppointments() {
        appointmentTableModel.setRowCount(0);
        if (doctor != null) {
            List<Appointment> myAppts = appointmentManager.getAppointments().stream()
                .filter(a -> a.getDoctorId().equals(doctor.getId()) && a.getStatus().equalsIgnoreCase("Scheduled"))
                .collect(Collectors.toList());

            for (Appointment a : myAppts) {
                appointmentTableModel.addRow(new Object[]{a.getId(), a.getPatientId(), a.getDate(), a.getStatus()});
            }
        }
    }

    private void updateSelectedAppointmentStatus(String newStatus) {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = (String) appointmentTableModel.getValueAt(row, 0);
        Appointment target = null;
        for (Appointment a : appointmentManager.getAppointments()) {
            if (a.getId().equals(apptId)) {
                target = a;
                break;
            }
        }

        if (target != null) {
            target.setStatus(newStatus);
            appointmentManager.updateAppointmentInDB(target);
            JOptionPane.showMessageDialog(this, "Appointment marked as " + newStatus + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAppointments();
        }
    }

    private JPanel createPrescribePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("Write a Prescription");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        // ---- Form ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(223, 230, 233)),
                new EmptyBorder(25, 25, 25, 25)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtPatientId  = new JTextField(20);
        JTextField txtMedication = new JTextField(20);
        JTextField txtDosage     = new JTextField(20);
        JTextField txtFee        = new JTextField("500", 20); // default consultation fee

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(boldLabel("Patient ID:"), gbc);
        gbc.gridx = 1;                  form.add(txtPatientId, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(boldLabel("Medication:"), gbc);
        gbc.gridx = 1;                  form.add(txtMedication, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(boldLabel("Dosage / Instructions:"), gbc);
        gbc.gridx = 1;                  form.add(txtDosage, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(boldLabel("Consultation Fee (₹):"), gbc);
        gbc.gridx = 1;                  form.add(txtFee, gbc);

        // ---- History table below form ----
        JLabel lblHistory = new JLabel("My Recent Prescriptions");
        lblHistory.setFont(UIUtils.HEADER_FONT);
        lblHistory.setForeground(UIUtils.TEXT_PRIMARY);

        String[] cols = {"Pres. ID", "Patient ID", "Medication", "Dosage", "Date"};
        DefaultTableModel prescTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable prescTable = new JTable(prescTableModel);
        UIUtils.styleTable(prescTable);
        JScrollPane scroll = new JScrollPane(prescTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233)));
        scroll.setPreferredSize(new Dimension(0, 200));

        // Populate history
        if (doctor != null) {
            prescriptionManager.getPrescriptionsByDoctor(doctor.getId()).forEach(p ->
                prescTableModel.addRow(new Object[]{
                        p.getId(), p.getPatientId(), p.getMedication(), p.getDosage(), p.getDate()}));
        }

        // ---- Submit button ----
        JButton btnSubmit = new JButton("Issue Prescription + Bill");
        UIUtils.styleButton(btnSubmit, true);
        btnSubmit.addActionListener(e -> {
            String patId    = txtPatientId.getText().trim();
            String med      = txtMedication.getText().trim();
            String dosage   = txtDosage.getText().trim();
            String feeStr   = txtFee.getText().trim();

            if (patId.isEmpty() || med.isEmpty() || dosage.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Patient ID, medication and dosage are required.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (patientManager.findPatient(patId) == null) {
                JOptionPane.showMessageDialog(this, "Patient ID '" + patId + "' not found.",
                        "Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double fee = 500.0;
            try { fee = Double.parseDouble(feeStr); } catch (NumberFormatException ignored) {}

            Prescription p = prescriptionManager.addPrescription(
                    patId, doctor.getId(), med, dosage);
            billingManager.generateBill(
                    patId, fee, "Consultation + Prescription (" + p.getId() + ")");

            // Refresh history table
            prescTableModel.addRow(new Object[]{
                    p.getId(), p.getPatientId(), p.getMedication(), p.getDosage(), p.getDate()});

            txtPatientId.setText("");
            txtMedication.setText("");
            txtDosage.setText("");
            txtFee.setText("500");

            JOptionPane.showMessageDialog(this,
                    "Prescription " + p.getId() + " issued and bill generated!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(UIUtils.MAIN_BG);
        south.add(btnSubmit);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(UIUtils.MAIN_BG);
        center.add(form, BorderLayout.NORTH);
        center.add(lblHistory, BorderLayout.CENTER);
        center.add(scroll, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 15));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("Patient History");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setBackground(UIUtils.MAIN_BG);
        JTextField txtId = new JTextField(15);
        txtId.setFont(UIUtils.MAIN_FONT);
        JButton btnSearch = new JButton("View History");
        UIUtils.styleButton(btnSearch, true);
        searchBar.add(new JLabel("Patient ID:"));
        searchBar.add(txtId);
        searchBar.add(btnSearch);

        JPanel northPanel = new JPanel(new BorderLayout(0, 10));
        northPanel.setBackground(UIUtils.MAIN_BG);
        northPanel.add(lblTitle, BorderLayout.NORTH);
        northPanel.add(searchBar, BorderLayout.SOUTH);
        panel.add(northPanel, BorderLayout.NORTH);

        // Results area with 3 tables
        JPanel resultArea = new JPanel();
        resultArea.setLayout(new BoxLayout(resultArea, BoxLayout.Y_AXIS));
        resultArea.setBackground(UIUtils.MAIN_BG);
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(null);
        panel.add(resultScroll, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> {
            resultArea.removeAll();
            String id = txtId.getText().trim();
            Patient p = patientManager.findPatient(id);

            if (p == null) {
                JLabel err = new JLabel("Patient '" + id + "' not found.");
                err.setForeground(Color.RED);
                resultArea.add(err);
                resultArea.revalidate(); resultArea.repaint();
                return;
            }

            // Patient info card
            resultArea.add(sectionLabel("Patient: " + p.getName() + " (" + p.getId() + ") — " + p.getDisease()));
            resultArea.add(Box.createRigidArea(new Dimension(0, 10)));

            // Appointments
            resultArea.add(sectionLabel("Appointments"));
            String[] aCols = {"Appt ID", "Doctor ID", "Date", "Status"};
            DefaultTableModel aModel = new DefaultTableModel(aCols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
            appointmentManager.getAppointments().stream()
                    .filter(a -> a.getPatientId().equals(p.getId()))
                    .forEach(a -> aModel.addRow(new Object[]{a.getId(), a.getDoctorId(), a.getDate(), a.getStatus()}));
            resultArea.add(styledTable(aModel));
            resultArea.add(Box.createRigidArea(new Dimension(0, 15)));

            // Prescriptions
            resultArea.add(sectionLabel("Prescriptions"));
            String[] pCols = {"Pres. ID", "Doctor ID", "Medication", "Dosage", "Date"};
            DefaultTableModel pModel = new DefaultTableModel(pCols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
            prescriptionManager.getPrescriptionsByPatient(p.getId())
                    .forEach(pr -> pModel.addRow(new Object[]{pr.getId(), pr.getDoctorId(), pr.getMedication(), pr.getDosage(), pr.getDate()}));
            resultArea.add(styledTable(pModel));

            resultArea.revalidate();
            resultArea.repaint();
        });

        return panel;
    }

    // ==================== HELPERS ====================

    private JLabel boldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return lbl;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIUtils.HEADER_FONT);
        lbl.setForeground(UIUtils.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JScrollPane styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        UIUtils.styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233)));
        // Use Short.MAX_VALUE (32767) not Integer.MAX_VALUE — avoids OOM in Windows L&F skin painter
        sp.setPreferredSize(new Dimension(Short.MAX_VALUE, 130));
        sp.setMaximumSize(new Dimension(Short.MAX_VALUE, 130));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sp;
    }

    private JPanel createLookupPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("Patient Lookup");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setBackground(UIUtils.MAIN_BG);
        
        JTextField txtSearch = new JTextField(15);
        txtSearch.setFont(UIUtils.MAIN_FONT);
        JButton btnSearch = new JButton("Search by ID");
        UIUtils.styleButton(btnSearch, true);

        searchBar.add(new JLabel("Patient ID: "));
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);

        JPanel resultArea = new JPanel();
        resultArea.setLayout(new BoxLayout(resultArea, BoxLayout.Y_AXIS));
        resultArea.setBackground(UIUtils.MAIN_BG);

        btnSearch.addActionListener(e -> {
            resultArea.removeAll();
            String id = txtSearch.getText().trim();
            Patient p = patientManager.findPatient(id);
            
            if (p != null) {
                JPanel details = new JPanel();
                details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
                details.setBackground(Color.WHITE);
                details.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(223, 230, 233)),
                        new EmptyBorder(20, 20, 20, 20)
                ));
                details.add(createDetailRow("ID:", p.getId()));
                details.add(createDetailRow("Name:", p.getName()));
                details.add(createDetailRow("Age:", String.valueOf(p.getAge())));
                details.add(createDetailRow("Disease:", p.getDisease()));

                JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
                wrapper.setBackground(UIUtils.MAIN_BG);
                wrapper.add(details);
                resultArea.add(wrapper);
            } else {
                JLabel lblError = new JLabel("Patient not found!");
                lblError.setForeground(Color.RED);
                resultArea.add(lblError);
            }
            resultArea.revalidate();
            resultArea.repaint();
        });

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(resultArea, BorderLayout.CENTER);

        return panel;
    }
}

