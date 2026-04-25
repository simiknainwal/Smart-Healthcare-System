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

        JButton btnOverview = createNavButton("Overview", "WELCOME");
        JButton btnPatients = createNavButton("Patients", "PATIENTS");
        JButton btnDoctors = createNavButton("Doctors", "DOCTORS");
        JButton btnAppointments = createNavButton("Appointments", "APPOINTMENTS");
        JButton btnBeds = createNavButton("Beds", "BEDS");

        navPanel.add(btnOverview);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnPatients);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnDoctors);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnAppointments);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnBeds);

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

        // Add panels to CardLayout
        mainContentPanel.add(new PatientPanel(patientManager), "PATIENTS");
        mainContentPanel.add(new DoctorPanel(doctorManager), "DOCTORS");
        mainContentPanel.add(new AppointmentPanel(appointmentManager), "APPOINTMENTS");
        mainContentPanel.add(new BedPanel(bedManager), "BEDS");

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
}
