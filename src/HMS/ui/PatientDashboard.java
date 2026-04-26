package HMS.ui;

import HMS.model.*;
import HMS.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PatientDashboard extends JFrame {
    private final DoctorManager doctorManager;
    private final AppointmentManager appointmentManager;
    private final BedManager bedManager;
    // Phase 3 managers — used in Step 5 UI tabs
    private final PrescriptionManager prescriptionManager;
    private final BillingManager billingManager;
    private final Runnable onLogout;

    private Patient patient;
    private final User currentUser;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private DefaultTableModel appointmentTableModel;

    public PatientDashboard(User user, PatientManager patientManager, DoctorManager doctorManager,
                            AppointmentManager appointmentManager, BedManager bedManager,
                            PrescriptionManager prescriptionManager, BillingManager billingManager,
                            Runnable onLogout) {
        this.doctorManager = doctorManager;
        this.appointmentManager = appointmentManager;
        this.bedManager = bedManager;
        this.prescriptionManager = prescriptionManager;
        this.billingManager = billingManager;
        this.onLogout = onLogout;

        this.currentUser = user;
        this.patient = patientManager.findPatient(user.getLinkedId());

        initFrame();
        initComponents();
    }

    private void initFrame() {
        setTitle("HospiCare - Patient Dashboard");
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
        JButton btnBed = createNavButton("My Bed Status", "BED");
        JButton btnPrescriptions = createNavButton("My Prescriptions", "PRESCRIPTIONS");
        JButton btnBills = createNavButton("My Bills", "BILLS");
        JButton btnSettings = createNavButton("Settings", "SETTINGS");

        navPanel.add(btnProfile);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnAppointments);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnBed);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnPrescriptions);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnBills);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnSettings);

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
        mainContentPanel.add(createBedPanel(), "BED");
        mainContentPanel.add(createPrescriptionsPanel(), "PRESCRIPTIONS");
        mainContentPanel.add(createBillsPanel(), "BILLS");
        mainContentPanel.add(createSettingsPanel(), "SETTINGS");

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

        if (patient != null) {
            details.add(createDetailRow("Hospital ID:", patient.getId()));
            details.add(createDetailRow("Name:", patient.getName()));
            details.add(createDetailRow("Age:", String.valueOf(patient.getAge())));
            details.add(createDetailRow("Disease:", patient.getDisease()));
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

        JLabel lblTitle = new JLabel("My Appointments");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Appt ID", "Doctor ID", "Doctor Name", "Date", "Status"};
        appointmentTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(appointmentTableModel);
        UIUtils.styleTable(table);

        refreshAppointments();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233)));
        panel.add(scroll, BorderLayout.CENTER);

        // Add Book Appointment Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UIUtils.MAIN_BG);
        JButton btnBook = new JButton("Book New Appointment");
        UIUtils.styleButton(btnBook, true);
        btnBook.addActionListener(e -> showBookingDialog());
        bottomPanel.add(btnBook);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshAppointments() {
        appointmentTableModel.setRowCount(0);
        if (patient != null) {
            List<Appointment> myAppts = appointmentManager.getAppointments().stream()
                .filter(a -> a.getPatientId().equals(patient.getId()))
                .collect(Collectors.toList());

            for (Appointment a : myAppts) {
                Doctor d = doctorManager.findDoctor(a.getDoctorId());
                String docName = (d != null) ? d.getName() : "Unknown";
                appointmentTableModel.addRow(new Object[]{a.getId(), a.getDoctorId(), docName, a.getDate(), a.getStatus()});
            }
        }
    }

    private void showBookingDialog() {
        JDialog dialog = new JDialog(this, "Book Appointment", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        JTextField txtProblem = new JTextField();
        txtProblem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel lblSuggested = new JLabel("No doctor selected.");
        lblSuggested.setForeground(Color.GRAY);
        
        JTextField txtDate = new JTextField(HMS.utils.DateUtil.todayFormatted());
        txtDate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JComboBox<String> comboDoctors = new JComboBox<>();
        comboDoctors.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comboDoctors.setVisible(false); // only show if suggestion fails
        
        for (Doctor d : doctorManager.getDoctors()) {
            comboDoctors.addItem(d.getId() + " - " + d.getName() + " (" + d.getSpecialization() + ")");
        }

        JButton btnSuggest = new JButton("Suggest Doctor");
        UIUtils.styleButton(btnSuggest, false);
        
        // Suggestion Logic
        final Doctor[] selectedDoctor = {null};

        btnSuggest.addActionListener(e -> {
            String problem = txtProblem.getText().trim();
            if (problem.isEmpty()) return;

            Doctor suggested = HMS.utils.DoctorDirectory.suggestDoctor(problem, doctorManager.getDoctors());
            if (suggested != null) {
                selectedDoctor[0] = suggested;
                lblSuggested.setText("Suggested: " + suggested.getName() + " (" + suggested.getSpecialization() + ")");
                lblSuggested.setForeground(new Color(46, 204, 113)); // Green
                comboDoctors.setVisible(false);
            } else {
                lblSuggested.setText("No exact match. Please select from the list below.");
                lblSuggested.setForeground(Color.RED);
                comboDoctors.setVisible(true);
                selectedDoctor[0] = null;
            }
        });

        form.add(new JLabel("Describe your problem (e.g. fever, headache):"));
        form.add(Box.createRigidArea(new Dimension(0, 5)));
        form.add(txtProblem);
        form.add(Box.createRigidArea(new Dimension(0, 5)));
        form.add(btnSuggest);
        form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(lblSuggested);
        form.add(Box.createRigidArea(new Dimension(0, 5)));
        form.add(comboDoctors);
        form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(new JLabel("Preferred Date (yyyy-MM-dd):"));
        form.add(Box.createRigidArea(new Dimension(0, 5)));
        form.add(txtDate);

        JButton btnConfirm = new JButton("Confirm Booking");
        UIUtils.styleButton(btnConfirm, true);
        btnConfirm.addActionListener(e -> {
            Doctor targetDoc = selectedDoctor[0];
            if (targetDoc == null && comboDoctors.isVisible()) {
                int idx = comboDoctors.getSelectedIndex();
                if (idx >= 0) {
                    targetDoc = doctorManager.getDoctors().get(idx);
                }
            }

            if (targetDoc == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a doctor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String date = txtDate.getText().trim();
            try {
                java.time.LocalDate requestedDate = HMS.utils.DateUtil.parse(date); // validate
                if (requestedDate.isBefore(HMS.utils.DateUtil.today())) {
                    JOptionPane.showMessageDialog(dialog, "Cannot book appointments in the past.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            appointmentManager.scheduleAppointment(patient.getId(), targetDoc.getId(), date);
            JOptionPane.showMessageDialog(dialog, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAppointments();
            dialog.dispose();
        });

        JPanel bottom = new JPanel();
        bottom.add(btnConfirm);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createBedPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("My Bed Status");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        Bed myBed = null;
        if (patient != null) {
            for (Bed b : bedManager.getBeds()) {
                if (b.isOccupied() && b.getPatientId().equals(patient.getId())) {
                    myBed = b;
                    break;
                }
            }
        }

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UIUtils.MAIN_BG);

        if (myBed != null) {
            JPanel details = new JPanel();
            details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
            details.setBackground(Color.WHITE);
            details.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(223, 230, 233)),
                    new EmptyBorder(20, 20, 20, 20)
            ));

            details.add(createDetailRow("Bed ID:", myBed.getId()));
            details.add(createDetailRow("Ward Type:", myBed.getWardType()));
            details.add(createDetailRow("Admit Date:", HMS.utils.DateUtil.display(myBed.getAdmitDate())));
            details.add(createDetailRow("Discharge Date:", HMS.utils.DateUtil.display(myBed.getDischargeDate())));

            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
            wrapper.setBackground(UIUtils.MAIN_BG);
            wrapper.add(details);
            centerPanel.add(wrapper);
        } else {
            // Patient does not have a bed, show availability and booking option
            JPanel infoPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            infoPanel.setBackground(UIUtils.MAIN_BG);
            infoPanel.setMaximumSize(new Dimension(600, 200));

            int gen = 0, icu = 0, priv = 0, semi = 0;
            for (Bed b : bedManager.getBeds()) {
                if (!b.isOccupied()) {
                    switch (b.getWardType()) {
                        case "General": gen++; break;
                        case "ICU": icu++; break;
                        case "Private": priv++; break;
                        case "Semi-Private": semi++; break;
                    }
                }
            }

            infoPanel.add(createAvailabilityCard("General Ward", gen, new Color(52, 152, 219)));
            infoPanel.add(createAvailabilityCard("ICU", icu, new Color(231, 76, 60)));
            infoPanel.add(createAvailabilityCard("Private", priv, new Color(155, 89, 182)));
            infoPanel.add(createAvailabilityCard("Semi-Private", semi, new Color(241, 196, 15)));

            centerPanel.add(infoPanel);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

            JButton btnBookBed = new JButton("Book a Bed");
            UIUtils.styleButton(btnBookBed, true);
            btnBookBed.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnBookBed.addActionListener(e -> showBedBookingDialog());
            
            centerPanel.add(btnBookBed);
        }

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAvailabilityCard(String wardType, int availableCount, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(223, 230, 233)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel(wardType, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel count = new JLabel(availableCount + " available", SwingConstants.CENTER);
        count.setFont(new Font("Segoe UI", Font.BOLD, 18));
        count.setForeground(availableCount > 0 ? accentColor : Color.GRAY);

        card.add(title, BorderLayout.NORTH);
        card.add(count, BorderLayout.CENTER);
        return card;
    }

    private void showBedBookingDialog() {
        JDialog dialog = new JDialog(this, "Book a Bed", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        JTextField txtPatientId = new JTextField(patient != null ? patient.getId() : "");
        txtPatientId.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtPatientId.setEditable(false); // Prefilled and locked

        String[] wardTypes = {"General", "ICU", "Private", "Semi-Private"};
        JComboBox<String> comboWard = new JComboBox<>(wardTypes);
        comboWard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        form.add(new JLabel("Patient ID:"));
        form.add(Box.createRigidArea(new Dimension(0, 5)));
        form.add(txtPatientId);
        form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(new JLabel("Select Ward Type:"));
        form.add(Box.createRigidArea(new Dimension(0, 5)));
        form.add(comboWard);

        JButton btnConfirm = new JButton("Confirm Booking");
        UIUtils.styleButton(btnConfirm, true);
        btnConfirm.addActionListener(e -> {
            String selectedWard = (String) comboWard.getSelectedItem();
            
            // Find first available bed in selected ward
            Bed availableBed = null;
            for (Bed b : bedManager.getBeds()) {
                if (b.getWardType().equals(selectedWard) && !b.isOccupied()) {
                    availableBed = b;
                    break;
                }
            }

            if (availableBed == null) {
                JOptionPane.showMessageDialog(dialog, "No available beds in the " + selectedWard + " ward.", "Unavailable", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Calculate stay days
            int stayDays = 0;
            switch (selectedWard) {
                case "General": stayDays = 7; break;
                case "ICU": stayDays = 3; break;
                case "Private": stayDays = 10; break;
                case "Semi-Private": stayDays = 5; break;
            }

            String admitDate = HMS.utils.DateUtil.todayFormatted();
            String dischargeDate = HMS.utils.DateUtil.addDays(admitDate, stayDays);

            availableBed.book(patient.getId(), admitDate, dischargeDate);
            bedManager.updateBedInDB(availableBed);

            JOptionPane.showMessageDialog(dialog, "Bed booked successfully!\nBed ID: " + availableBed.getId() + "\nExpected Discharge: " + HMS.utils.DateUtil.display(dischargeDate), "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            
            // Refresh Bed Panel
            cardLayout.show(mainContentPanel, "PROFILE"); // temporarily switch
            mainContentPanel.remove(2); // Remove old bed panel
            mainContentPanel.add(createBedPanel(), "BED", 2); // Re-add updated bed panel
            cardLayout.show(mainContentPanel, "BED");
        });

        JPanel bottom = new JPanel();
        bottom.add(btnConfirm);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    private JPanel createPrescriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("My Prescriptions");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Pres. ID", "Doctor ID", "Medication", "Dosage", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIUtils.styleTable(table);

        if (patient != null) {
            prescriptionManager.getPrescriptionsByPatient(patient.getId()).forEach(p ->
                    model.addRow(new Object[]{
                            p.getId(), p.getDoctorId(), p.getMedication(), p.getDosage(),
                            HMS.utils.DateUtil.display(p.getDate())}));
        }

        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"—", "No prescriptions on record.", "", "", ""});
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBillsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("My Bills");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Bill ID", "Description", "Amount (₹)", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIUtils.styleTable(table);

        // Colour-code UNPAID rows
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object val, boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, val, selected, focused, row, col);
                String status = (String) t.getModel().getValueAt(row, 4);
                if (!selected) {
                    setBackground("UNPAID".equals(status) ? new Color(255, 243, 230) : new Color(232, 255, 243));
                }
                return this;
            }
        });

        if (patient != null) {
            billingManager.getBillsByPatient(patient.getId()).forEach(b ->
                    model.addRow(new Object[]{
                            b.getId(), b.getDescription(),
                            String.format("%.2f", b.getAmount()),
                            HMS.utils.DateUtil.display(b.getDate()), b.getStatus()}));
        }

        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"—", "No bills on record.", "", "", ""});
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233)));
        panel.add(scroll, BorderLayout.CENTER);

        JLabel note = new JLabel("  Note: Payments are processed at the billing counter.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        note.setForeground(Color.GRAY);
        panel.add(note, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("Account Settings");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(223, 230, 233)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblUser = new JLabel("New Username:");
        JTextField txtUser = new JTextField(currentUser.getUsername());
        
        JLabel lblPass = new JLabel("New Password (8-12 chars):");
        JPasswordField txtPass = new JPasswordField();
        
        JButton btnSave = new JButton("Update Credentials");
        UIUtils.styleButton(btnSave, true);

        form.add(lblUser);
        form.add(txtUser);
        form.add(lblPass);
        form.add(txtPass);
        form.add(new JLabel(""));
        form.add(btnSave);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT));
        center.setBackground(UIUtils.MAIN_BG);
        center.add(form);
        panel.add(center, BorderLayout.CENTER);

        btnSave.addActionListener(e -> {
            String newUser = txtUser.getText().trim();
            String newPass = new String(txtPass.getPassword()).trim();

            if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                HMS.utils.AuthService auth = new HMS.utils.AuthService();
                auth.updateCredentials(currentUser, newUser, newPass);
                JOptionPane.showMessageDialog(panel, "Credentials updated successfully.\nUsername: " + newUser, "Success", JOptionPane.INFORMATION_MESSAGE);
                txtPass.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }
}
