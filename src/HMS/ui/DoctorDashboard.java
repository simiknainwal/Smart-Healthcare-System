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

    private final User currentUser;
    private final DoctorManager doctorManager;
    private final AppointmentManager appointmentManager;
    private final PatientManager patientManager;
    // Phase 3 managers — used in Step 4 UI tabs
    private final PrescriptionManager prescriptionManager;
    private final BillingManager billingManager;
    private final ReportManager reportManager;
    private final Runnable onLogout;

    private Doctor doctor;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private DefaultTableModel appointmentTableModel;
    private JTable appointmentTable;

    public DoctorDashboard(User user, DoctorManager doctorManager,
                           AppointmentManager appointmentManager, PatientManager patientManager,
                           PrescriptionManager prescriptionManager, BillingManager billingManager,
                           ReportManager reportManager, Runnable onLogout) {
        this.currentUser = user;
        this.doctorManager = doctorManager;
        this.appointmentManager = appointmentManager;
        this.patientManager = patientManager;
        this.prescriptionManager = prescriptionManager;
        this.billingManager = billingManager;
        this.reportManager = reportManager;
        this.onLogout = onLogout;

        this.doctor = doctorManager.findDoctor(currentUser.getLinkedId());

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
        JButton btnLookup = createNavButton("Patient Lookup", "LOOKUP");

        navPanel.add(btnProfile);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnAppointments);
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
        lblLabel.setPreferredSize(new Dimension(120, 25));
        
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
