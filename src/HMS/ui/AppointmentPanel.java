package HMS.ui;

import HMS.model.Appointment;
import HMS.service.AppointmentManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AppointmentPanel extends JPanel {
    private final AppointmentManager appointmentManager;
    private JTable table;
    private DefaultTableModel tableModel;

    public AppointmentPanel(AppointmentManager appointmentManager) {
        this.appointmentManager = appointmentManager;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.MAIN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // Title Label
        JLabel titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(UIUtils.TITLE_FONT);
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Patient ID", "Doctor ID", "Date", "Status"};
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

        JButton btnAdd = new JButton("Schedule Appointment");
        UIUtils.styleButton(btnAdd, true);
        btnAdd.addActionListener(e -> showAddDialog());

        JButton btnUpdateStatus = new JButton("Update Status");
        UIUtils.styleButton(btnUpdateStatus, false);
        btnUpdateStatus.addActionListener(e -> showUpdateStatusDialog());

        JButton btnRefresh = new JButton("Refresh");
        UIUtils.styleButton(btnRefresh, false);
        btnRefresh.addActionListener(e -> refreshData());

        controlPanel.add(btnRefresh);
        controlPanel.add(btnAdd);
        controlPanel.add(btnUpdateStatus);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        List<Appointment> appointments = appointmentManager.getAppointments();
        for (Appointment a : appointments) {
            tableModel.addRow(new Object[]{a.getId(), a.getPatientId(), a.getDoctorId(), a.getDate(), a.getStatus()});
        }
    }

    private void showAddDialog() {
        JTextField txtPatientId = new JTextField(15);
        JTextField txtDoctorId = new JTextField(15);
        JTextField txtDate = new JTextField(15);
        
        // Let's inform the user about the expected date format if possible
        txtDate.setToolTipText("Format: DD-MM-YYYY");

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Patient ID:"));
        panel.add(txtPatientId);
        panel.add(new JLabel("Doctor ID:"));
        panel.add(txtDoctorId);
        panel.add(new JLabel("Date (DD-MM-YYYY):"));
        panel.add(txtDate);

        int result = JOptionPane.showConfirmDialog(this, panel, "Schedule Appointment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String pId = txtPatientId.getText().trim();
                String dId = txtDoctorId.getText().trim();
                String date = txtDate.getText().trim();

                appointmentManager.scheduleAppointment(pId, dId, date);
                refreshData();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateStatusDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Find appointment
        Appointment target = null;
        for (Appointment a : appointmentManager.getAppointments()) {
            if (a.getId().equals(id)) {
                target = a;
                break;
            }
        }
        
        if (target == null) return;

        String[] statuses = {"Scheduled", "Done", "Cancelled"};
        JComboBox<String> comboStatus = new JComboBox<>(statuses);
        comboStatus.setSelectedItem(target.getStatus());

        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.add(new JLabel("Select Status:"));
        panel.add(comboStatus);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Status", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String newStatus = (String) comboStatus.getSelectedItem();
            target.setStatus(newStatus);
            appointmentManager.updateAppointmentInDB(target);
            refreshData();
            JOptionPane.showMessageDialog(this, "Status updated successfully!");
        }
    }
}
