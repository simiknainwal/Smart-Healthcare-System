package HMS.ui;

import HMS.model.Patient;
import HMS.service.PatientManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientPanel extends JPanel {
    private final PatientManager patientManager;
    private JTable table;
    private DefaultTableModel tableModel;

    public PatientPanel(PatientManager patientManager) {
        this.patientManager = patientManager;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.MAIN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // Title Label
        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(UIUtils.TITLE_FONT);
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Age", "Disease"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // uneditable
            }
        };
        table = new JTable(tableModel);
        UIUtils.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233)));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(UIUtils.MAIN_BG);

        JButton btnAdd = new JButton("Add Patient");
        UIUtils.styleButton(btnAdd, true);
        btnAdd.addActionListener(e -> showAddDialog());

        JButton btnUpdate = new JButton("Update Selected");
        UIUtils.styleButton(btnUpdate, false);
        btnUpdate.addActionListener(e -> showUpdateDialog());

        JButton btnDelete = new JButton("Delete Selected");
        UIUtils.styleDangerButton(btnDelete);
        btnDelete.addActionListener(e -> deleteSelected());

        JButton btnRefresh = new JButton("Refresh");
        UIUtils.styleButton(btnRefresh, false);
        btnRefresh.addActionListener(e -> refreshData());

        controlPanel.add(btnRefresh);
        controlPanel.add(btnAdd);
        controlPanel.add(btnUpdate);
        controlPanel.add(btnDelete);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        List<Patient> patients = patientManager.getPatients();
        for (Patient p : patients) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getAge(), p.getDisease()});
        }
    }

    private void showAddDialog() {
        JTextField txtName = new JTextField(15);
        JTextField txtAge = new JTextField(15);
        JTextField txtDisease = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Age:"));
        panel.add(txtAge);
        panel.add(new JLabel("Disease:"));
        panel.add(txtDisease);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Patient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = txtName.getText().trim();
                int age = Integer.parseInt(txtAge.getText().trim());
                String disease = txtDisease.getText().trim();

                Patient p = Patient.create(name, age, disease);
                patientManager.addPatient(p);
                refreshData();
                JOptionPane.showMessageDialog(this, "Patient added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid age format!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Patient p = patientManager.findPatient(id);
        if (p == null) return;

        JTextField txtName = new JTextField(p.getName(), 15);
        JTextField txtAge = new JTextField(String.valueOf(p.getAge()), 15);
        JTextField txtDisease = new JTextField(p.getDisease(), 15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Age:"));
        panel.add(txtAge);
        panel.add(new JLabel("Disease:"));
        panel.add(txtDisease);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Patient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = txtName.getText().trim();
                int age = Integer.parseInt(txtAge.getText().trim());
                String disease = txtDisease.getText().trim();

                p.setName(name);
                p.setAge(age);
                p.setDisease(disease);

                patientManager.updatePatientInDB(p);
                refreshData();
                JOptionPane.showMessageDialog(this, "Patient updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid age format!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete patient " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            patientManager.removePatient(id);
            refreshData();
        }
    }
}
