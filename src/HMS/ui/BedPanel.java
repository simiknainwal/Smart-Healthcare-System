package HMS.ui;

import HMS.model.Bed;
import HMS.service.BedManager;
import HMS.utils.CounterManager;
import HMS.utils.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BedPanel extends JPanel {
    private final BedManager bedManager;
    private JTable table;
    private DefaultTableModel tableModel;

    public BedPanel(BedManager bedManager) {
        this.bedManager = bedManager;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.MAIN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // Title Label
        JLabel titleLabel = new JLabel("Bed Management");
        titleLabel.setFont(UIUtils.TITLE_FONT);
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Bed ID", "Ward Type", "Status", "Patient ID", "Admit Date", "Discharge Date"};
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

        JButton btnAddBed = new JButton("Add Bed");
        UIUtils.styleButton(btnAddBed, false);
        btnAddBed.addActionListener(e -> addBed());

        JButton btnBook = new JButton("Book Selected Bed");
        UIUtils.styleButton(btnBook, true);
        btnBook.addActionListener(e -> bookBed());

        JButton btnDischarge = new JButton("Discharge Selected");
        UIUtils.styleDangerButton(btnDischarge);
        btnDischarge.addActionListener(e -> dischargeBed());

        JButton btnRefresh = new JButton("Refresh");
        UIUtils.styleButton(btnRefresh, false);
        btnRefresh.addActionListener(e -> refreshData());

        controlPanel.add(btnRefresh);
        controlPanel.add(btnAddBed);
        controlPanel.add(btnBook);
        controlPanel.add(btnDischarge);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        List<Bed> beds = bedManager.getBeds();
        for (Bed b : beds) {
            String status = b.isOccupied() ? "OCCUPIED" : "AVAILABLE";
            tableModel.addRow(new Object[]{
                    b.getId(), b.getWardType(), status, 
                    b.getPatientId(), b.getAdmitDate(), b.getDischargeDate()
            });
        }
    }

    private void addBed() {
        String[] wards = {"General", "ICU", "Private", "Semi-Private"};
        JComboBox<String> comboWard = new JComboBox<>(wards);

        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.add(new JLabel("Select Ward:"));
        panel.add(comboWard);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Bed", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String ward = (String) comboWard.getSelectedItem();
            String id = CounterManager.getNextBedId();
            Bed b = new Bed(id, ward);
            bedManager.getBeds().add(b);
            bedManager.insertBedInDB(b);
            refreshData();
            JOptionPane.showMessageDialog(this, "Bed added successfully!");
        }
    }

    private void bookBed() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bed.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Bed target = null;
        for (Bed b : bedManager.getBeds()) {
            if (b.getId().equals(id)) {
                target = b;
                break;
            }
        }

        if (target == null) return;
        
        if (target.isOccupied()) {
            JOptionPane.showMessageDialog(this, "This bed is already occupied!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField txtPatientId = new JTextField(15);
        JTextField txtAdmitDate = new JTextField(DateUtil.todayFormatted(), 15);
        JTextField txtStayDays = new JTextField("7", 15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Patient ID:"));
        panel.add(txtPatientId);
        panel.add(new JLabel("Admit Date:"));
        panel.add(txtAdmitDate);
        panel.add(new JLabel("Stay Duration (Days):"));
        panel.add(txtStayDays);

        int result = JOptionPane.showConfirmDialog(this, panel, "Book Bed", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String patientId = txtPatientId.getText().trim();
                String admitDate = txtAdmitDate.getText().trim();
                int days = Integer.parseInt(txtStayDays.getText().trim());
                String dischargeDate = DateUtil.addDays(admitDate, days);

                target.book(patientId, admitDate, dischargeDate);
                bedManager.updateBedInDB(target);
                refreshData();
                JOptionPane.showMessageDialog(this, "Bed booked successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error formatting dates or duration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dischargeBed() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bed.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Bed target = null;
        for (Bed b : bedManager.getBeds()) {
            if (b.getId().equals(id)) {
                target = b;
                break;
            }
        }

        if (target == null) return;
        
        if (!target.isOccupied()) {
            JOptionPane.showMessageDialog(this, "This bed is already available.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to discharge patient from bed " + id + "?", "Confirm Discharge", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            target.discharge();
            bedManager.updateBedInDB(target);
            refreshData();
        }
    }
}
