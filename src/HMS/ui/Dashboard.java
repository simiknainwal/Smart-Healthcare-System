package HMS.ui;

import HMS.service.AppointmentManager;
import HMS.service.BedManager;
import HMS.service.BillingManager;
import HMS.service.DoctorManager;
import HMS.service.PatientManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Dashboard extends JFrame {

    private final PatientManager patientManager;
    private final DoctorManager doctorManager;
    private final AppointmentManager appointmentManager;
    private final BedManager bedManager;
    // Phase 3 — used for Billing panel in Step 5
    private final BillingManager billingManager;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private final Runnable onLogout;

    public Dashboard(PatientManager patientManager, DoctorManager doctorManager,
                     AppointmentManager appointmentManager, BedManager bedManager,
                     BillingManager billingManager, Runnable onLogout) {
        this.patientManager = patientManager;
        this.doctorManager = doctorManager;
        this.appointmentManager = appointmentManager;
        this.bedManager = bedManager;
        this.billingManager = billingManager;
        this.onLogout = onLogout;

        initFrame();
        initComponents();
    }

    private void initFrame() {
        setTitle("HospiCare Admin Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Add WindowAdapter to satisfy Event Handling syllabus requirement
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        Dashboard.this,
                        "Are you sure you want to exit HospiCare?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
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

        JButton btnOverview = createNavButton("Overview", "WELCOME");
        JButton btnPatients = createNavButton("Patients", "PATIENTS");
        JButton btnDoctors = createNavButton("Doctors", "DOCTORS");
        JButton btnAppointments = createNavButton("Appointments", "APPOINTMENTS");
        JButton btnBeds = createNavButton("Beds", "BEDS");
        JButton btnBilling = createNavButton("Billing", "BILLING");
        JButton btnAccounts = createNavButton("Accounts", "ACCOUNTS");

        navPanel.add(btnOverview);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnPatients);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnDoctors);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnAppointments);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnBeds);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnBilling);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnAccounts);

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
        
        JLabel lblClock = new JLabel();
        lblClock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblClock.setForeground(Color.LIGHT_GRAY);
        lblClock.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblClock.setBorder(new EmptyBorder(0, 0, 10, 0));
        HMS.utils.ClockThread clock = new HMS.utils.ClockThread(lblClock);
        clock.start();
        
        footerPanel.add(lblClock);
        footerPanel.add(btnLogout);
        sidebar.add(footerPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);

        // --- Main Content Area (CardLayout) ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UIUtils.MAIN_BG);

        // Add panels to CardLayout
        mainContentPanel.add(new PatientPanel(patientManager), "PATIENTS");
        mainContentPanel.add(new DoctorPanel(doctorManager), "DOCTORS");
        mainContentPanel.add(new AppointmentPanel(appointmentManager), "APPOINTMENTS");
        mainContentPanel.add(new BedPanel(bedManager), "BEDS");
        mainContentPanel.add(createBillingPanel(), "BILLING");
        mainContentPanel.add(createAccountsPanel(), "ACCOUNTS");

        // Welcome Panel (Overview Dashboard)
        JPanel welcomePanel = new JPanel(new BorderLayout(20, 20));
        welcomePanel.setBackground(UIUtils.MAIN_BG);
        welcomePanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel lblWelcome = new JLabel("Dashboard Overview", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(UIUtils.TEXT_PRIMARY);
        welcomePanel.add(lblWelcome, BorderLayout.NORTH);
        
        // Metrics Grid
        JPanel metricsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        metricsPanel.setBackground(UIUtils.MAIN_BG);

        metricsPanel.add(createMetricCard("Total Patients", String.valueOf(patientManager.getPatients().size()), new Color(52, 152, 219)));
        metricsPanel.add(createMetricCard("Total Doctors", String.valueOf(doctorManager.getDoctors().size()), new Color(46, 204, 113)));
        
        long scheduled = appointmentManager.getAppointments().stream()
                .filter(a -> a.getStatus().equalsIgnoreCase("Scheduled"))
                .count();
        metricsPanel.add(createMetricCard("Scheduled Appointments", String.valueOf(scheduled), new Color(155, 89, 182)));
        
        long availableBeds = bedManager.getBeds().stream()
                .filter(b -> !b.isOccupied())
                .count();
        metricsPanel.add(createMetricCard("Available Beds", availableBeds + " / " + bedManager.getBeds().size(), new Color(241, 196, 15)));

        welcomePanel.add(metricsPanel, BorderLayout.CENTER);
        mainContentPanel.add(welcomePanel, "WELCOME");

        add(mainContentPanel, BorderLayout.CENTER);

        // Show welcome screen initially
        cardLayout.show(mainContentPanel, "WELCOME");
    }

    private JPanel createMetricCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(223, 230, 233), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel valLabel = new JLabel(value, SwingConstants.CENTER);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valLabel.setForeground(accentColor);
        card.add(valLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(UIUtils.TITLE_FONT);
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JButton createNavButton(String title, String cardName) {
        JButton btn = new JButton(title);
        UIUtils.styleSidebarButton(btn);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.addActionListener(e -> cardLayout.show(mainContentPanel, cardName));
        return btn;
    }

    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Billing Overview");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterBar.setBackground(UIUtils.MAIN_BG);
        String[] filters = {"ALL", "UNPAID", "PAID"};
        JComboBox<String> comboFilter = new JComboBox<>(filters);
        comboFilter.setFont(UIUtils.MAIN_FONT);
        filterBar.add(new JLabel("Show:"));
        filterBar.add(comboFilter);

        // Table
        String[] cols = {"Bill ID", "Patient ID", "Description", "Amount (₹)", "Date", "Status"};
        javax.swing.table.DefaultTableModel billModel = new javax.swing.table.DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable billTable = new JTable(billModel);
        UIUtils.styleTable(billTable);

        // Colour-code rows
        billTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object val, boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, val, selected, focused, row, col);
                String status = (String) t.getModel().getValueAt(row, 5);
                if (!selected) {
                    setBackground("UNPAID".equals(status) ? new Color(255, 243, 230) : new Color(232, 255, 243));
                }
                return this;
            }
        });

        // Populate method
        Runnable populate = () -> {
            billModel.setRowCount(0);
            String filter = (String) comboFilter.getSelectedItem();
            billingManager.getAll().stream()
                    .filter(b -> "ALL".equals(filter) || b.getStatus().equals(filter))
                    .forEach(b -> billModel.addRow(new Object[]{
                            b.getId(), b.getPatientId(), b.getDescription(),
                            String.format("%.2f", b.getAmount()),
                            HMS.utils.DateUtil.display(b.getDate()), b.getStatus()}));
        };
        populate.run();
        comboFilter.addActionListener(e -> populate.run());

        JScrollPane scroll = new JScrollPane(billTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233)));

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setBackground(UIUtils.MAIN_BG);

        JButton btnPaid = new JButton("Mark as Paid");
        UIUtils.styleButton(btnPaid, true);
        btnPaid.setBackground(new Color(46, 204, 113));
        btnPaid.addActionListener(e -> {
            int row = billTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "Select a bill first.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String billId = (String) billModel.getValueAt(row, 0);
            boolean changed = billingManager.markAsPaid(billId);
            if (changed) {
                populate.run();
                JOptionPane.showMessageDialog(panel, "Bill " + billId + " marked as PAID.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, "Bill is already PAID.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnRefresh = new JButton("Refresh");
        UIUtils.styleButton(btnRefresh, false);
        btnRefresh.addActionListener(e -> populate.run());

        bottom.add(btnRefresh);
        bottom.add(btnPaid);

        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(UIUtils.MAIN_BG);
        north.add(lblTitle, BorderLayout.NORTH);
        north.add(filterBar, BorderLayout.SOUTH);
        panel.add(north, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Manage User Accounts");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Username", "Role", "Linked ID", "Created At"};
        javax.swing.table.DefaultTableModel userModel = new javax.swing.table.DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable userTable = new JTable(userModel);
        UIUtils.styleTable(userTable);

        HMS.db.UserDAO userDAO = new HMS.db.UserDAO();

        Runnable refreshUsers = () -> {
            userModel.setRowCount(0);
            userDAO.getAll().forEach(u -> userModel.addRow(new Object[]{
                    u.getUsername(), u.getRole(), u.getLinkedId(), u.getCreatedAt()
            }));
        };
        refreshUsers.run();

        JScrollPane scroll = new JScrollPane(userTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233)));

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setBackground(UIUtils.MAIN_BG);

        JButton btnDelete = new JButton("Delete Account");
        UIUtils.styleDangerButton(btnDelete);
        btnDelete.setEnabled(false); // Default disabled

        userTable.getSelectionModel().addListSelectionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                String role = (String) userModel.getValueAt(row, 1);
                btnDelete.setEnabled(!"ADMIN".equals(role));
            } else {
                btnDelete.setEnabled(false);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row == -1) return;
            
            String username = (String) userModel.getValueAt(row, 0);
            String role = (String) userModel.getValueAt(row, 1);
            
            if ("ADMIN".equals(role)) {
                JOptionPane.showMessageDialog(panel, "Cannot delete admin accounts.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(panel, 
                    "Are you sure you want to delete user account: " + username + "?\n" +
                    "(This does not delete their patient/doctor profile)", 
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                userDAO.delete(username);
                refreshUsers.run();
                JOptionPane.showMessageDialog(panel, "Account deleted successfully.");
            }
        });

        JButton btnRefresh = new JButton("Refresh");
        UIUtils.styleButton(btnRefresh, false);
        btnRefresh.addActionListener(e -> refreshUsers.run());

        bottom.add(btnRefresh);
        bottom.add(btnDelete);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }
}
