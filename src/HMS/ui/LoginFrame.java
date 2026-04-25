package HMS.ui;

import HMS.model.User;
import HMS.utils.AuthService;
import HMS.HospitalApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final HospitalApp app;

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame(HospitalApp app) {
        this.app = app;
        this.authService = new AuthService();
        
        initFrame();
        initComponents();
    }

    private void initFrame() {
        setTitle("HospiCare - Login");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtils.MAIN_BG);
    }

    private void initComponents() {
        // --- Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UIUtils.SIDEBAR_BG);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));
        headerPanel.setLayout(new GridBagLayout());
        
        JLabel lblBrand = new JLabel("HOSPICARE");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblBrand.setForeground(Color.WHITE);
        headerPanel.add(lblBrand);

        add(headerPanel, BorderLayout.NORTH);

        // --- Center Form ---
        JPanel formPanel = new JPanel();
        formPanel.setBackground(UIUtils.MAIN_BG);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel lblTitle = new JLabel("Welcome Back");
        lblTitle.setFont(UIUtils.TITLE_FONT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Please login to continue");
        lblSubtitle.setFont(UIUtils.MAIN_FONT);
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUsername.setFont(UIUtils.MAIN_FONT);
        
        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setFont(UIUtils.MAIN_FONT);

        JButton btnLogin = new JButton("Login");
        UIUtils.styleButton(btnLogin, true);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> attemptLogin());

        // Layout the form
        formPanel.add(lblTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(lblSubtitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        formPanel.add(createLabeledField("Username", txtUsername));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createLabeledField("Password", txtPassword));
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(btnLogin);

        add(formPanel, BorderLayout.CENTER);

        // --- Footer ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(UIUtils.MAIN_BG);
        footerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblNoAccount = new JLabel("Don't have an account? ");
        lblNoAccount.setFont(UIUtils.MAIN_FONT);
        
        JButton btnSignup = new JButton("Sign Up");
        btnSignup.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSignup.setForeground(UIUtils.ACCENT_COLOR);
        btnSignup.setBorderPainted(false);
        btnSignup.setContentAreaFilled(false);
        btnSignup.setFocusPainted(false);
        btnSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSignup.addActionListener(e -> openSignup());

        footerPanel.add(lblNoAccount);
        footerPanel.add(btnSignup);
        
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIUtils.MAIN_BG);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(UIUtils.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
        
        return panel;
    }

    private void attemptLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = authService.login(username, password);

        if (user != null) {
            // Success! Delegate to app to open correct dashboard
            this.dispose(); // Close login window
            app.handleLoginSuccess(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText(""); // Clear password field
        }
    }

    private void openSignup() {
        this.dispose(); // Close login window
        new SignupFrame(app).setVisible(true);
    }
}
