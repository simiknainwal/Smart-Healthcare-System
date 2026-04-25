package HMS.ui;

import HMS.model.Doctor;
import HMS.service.DoctorManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorPanel extends JPanel {
    private final DoctorManager doctorManager;
    private JTable table;
    private DefaultTableModel tableModel;

    public DoctorPanel(DoctorManager doctorManager) {
        this.doctorManager = doctorManager;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.MAIN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // Title Label
        JLabel titleLabel = new JLabel("Doctor Management");
        titleLabel.setFont(UIUtils.TITLE_FONT);
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Age", "Specialization"};
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

        JButton btnAdd = new JButton("Add Doctor");
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
        List<Doctor> doctors = doctorManager.getDoctors();
        for (Doctor d : doctors) {
            tableModel.addRow(new Object[]{d.getId(), d.getName(), d.getAge(), d.getSpecialization()});
        }
    }

    private void showAddDialog() {
        JTextField txtName = new JTextField(15);
        JTextField txtAge = new JTextField(15);
        JTextField txtSpec = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Age:"));
        panel.add(txtAge);
        panel.add(new JLabel("Specialization:"));
        panel.add(txtSpec);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Doctor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = txtName.getText().trim();
                int age = Integer.parseInt(txtAge.getText().trim());
                String spec = txtSpec.getText().trim();

                Doctor d = Doctor.create(name, age, spec);
                doctorManager.addDoctor(d);
                refreshData();
                JOptionPane.showMessageDialog(this, "Doctor added successfully!");
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
            JOptionPane.showMessageDialog(this, "Please select a doctor to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Doctor d = doctorManager.findDoctor(id);
        if (d == null) return;

        JTextField txtName = new JTextField(d.getName(), 15);
        JTextField txtAge = new JTextField(String.valueOf(d.getAge()), 15);
        JTextField txtSpec = new JTextField(d.getSpecialization(), 15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Age:"));
        panel.add(txtAge);
        panel.add(new JLabel("Specialization:"));
        panel.add(txtSpec);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Doctor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = txtName.getText().trim();
                int age = Integer.parseInt(txtAge.getText().trim());
                String spec = txtSpec.getText().trim();

                d.setName(name);
                d.setAge(age);
                d.setSpecialization(spec);

                doctorManager.updateDoctorInDB(d);
                refreshData();
                JOptionPane.showMessageDialog(this, "Doctor updated successfully!");
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
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete doctor " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            doctorManager.removeDoctor(id);
            refreshData();
        }
    }
}
