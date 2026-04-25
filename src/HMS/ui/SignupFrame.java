package HMS.ui;

import HMS.HospitalApp;
import HMS.utils.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SignupFrame extends JFrame {

    private final HospitalApp app;
    private final AuthService authService;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JComboBox<String> comboRole;
    private JTextField txtLinkedId;

    public SignupFrame(HospitalApp app) {
        this.app = app;
        this.authService = new AuthService();

        initFrame();
        initComponents();
    }

    private void initFrame() {
        setTitle("HospiCare - Sign Up");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtils.MAIN_BG);
    }

    private void initComponents() {
        // --- Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UIUtils.SIDEBAR_BG);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setLayout(new GridBagLayout());

        JLabel lblBrand = new JLabel("Create Account");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblBrand.setForeground(Color.WHITE);
        headerPanel.add(lblBrand);

        add(headerPanel, BorderLayout.NORTH);

        // --- Center Form ---
        JPanel formPanel = new JPanel();
        formPanel.setBackground(UIUtils.MAIN_BG);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        txtUsername = new JTextField();
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        String[] roles = { "PATIENT", "DOCTOR" };
        comboRole = new JComboBox<>(roles);
        comboRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comboRole.setBackground(Color.WHITE);

        txtLinkedId = new JTextField();
        txtLinkedId.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtLinkedId.setToolTipText("e.g. PAT001 for Patient or DOC001 for Doctor");

        formPanel.add(createLabeledField("Username", txtUsername));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createLabeledField("Password (8-12 characters)", txtPassword));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createLabeledField("Confirm Password", txtConfirmPassword));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createLabeledField("Role", comboRole));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createLabeledField("Hospital ID (e.g. PAT001, DOC001)", txtLinkedId));

        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton btnSubmit = new JButton("Sign Up");
        UIUtils.styleButton(btnSubmit, true);
        btnSubmit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSubmit.addActionListener(e -> attemptSignup());

        formPanel.add(btnSubmit);

        add(formPanel, BorderLayout.CENTER);

        // --- Footer ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(UIUtils.MAIN_BG);
        footerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblHasAccount = new JLabel("Already have an account? ");
        lblHasAccount.setFont(UIUtils.MAIN_FONT);

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(UIUtils.ACCENT_COLOR);
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> backToLogin());

        footerPanel.add(lblHasAccount);
        footerPanel.add(btnLogin);

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

    private void attemptSignup() {
        String username = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());
        String role = (String) comboRole.getSelectedItem();
        String linkedId = txtLinkedId.getText().trim();

        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            authService.signup(username, pass, role, linkedId);

            JOptionPane.showMessageDialog(this, "Account created successfully! Please login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            backToLogin();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Signup Failed", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void backToLogin() {
        this.dispose();
        new LoginFrame(app).setVisible(true);
    }
}
