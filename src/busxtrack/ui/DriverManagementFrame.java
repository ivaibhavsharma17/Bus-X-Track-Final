package busxtrack.ui;

import busxtrack.dao.DriverDAO;
import busxtrack.model.Driver;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class DriverManagementFrame extends JFrame {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField licenseField;
    private JTable driverTable;
    private DefaultTableModel tableModel;
    private DriverDAO driverDAO;
    private int selectedDriverId;

    public DriverManagementFrame() {
        driverDAO = new DriverDAO();
        selectedDriverId = 0;

        setTitle("BusXTrack - Manage Drivers");
        setSize(720, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
        loadDrivers();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("Manage Drivers", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameField = new JTextField();
        phoneField = new JTextField();
        licenseField = new JTextField();

        formPanel.add(new JLabel("Driver Name"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Phone Number"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("License Number"));
        formPanel.add(licenseField);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        formPanel.add(addButton);
        formPanel.add(updateButton);

        add(formPanel, BorderLayout.WEST);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Phone", "License"}, 0);
        driverTable = new JTable(tableModel);
        add(new JScrollPane(driverTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton backButton = new JButton("Back");

        bottomPanel.add(deleteButton);
        bottomPanel.add(clearButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDriver();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDriver();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteDriver();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AdminDashboardFrame().setVisible(true);
            }
        });

        driverTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });
    }

    private void addDriver() {
        if (!isFormValid()) {
            return;
        }

        Driver driver = new Driver(0, nameField.getText().trim(), phoneField.getText().trim(), licenseField.getText().trim());

        try {
            driverDAO.addDriver(driver);
            loadDrivers();
            clearForm();
            JOptionPane.showMessageDialog(this, "Driver added successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateDriver() {
        if (selectedDriverId == 0) {
            JOptionPane.showMessageDialog(this, "Please select a driver from the table.");
            return;
        }

        if (!isFormValid()) {
            return;
        }

        Driver driver = new Driver(selectedDriverId, nameField.getText().trim(), phoneField.getText().trim(), licenseField.getText().trim());

        try {
            driverDAO.updateDriver(driver);
            loadDrivers();
            clearForm();
            JOptionPane.showMessageDialog(this, "Driver updated successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteDriver() {
        if (selectedDriverId == 0) {
            JOptionPane.showMessageDialog(this, "Please select a driver from the table.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this, "Delete selected driver?");
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            driverDAO.deleteDriver(selectedDriverId);
            loadDrivers();
            clearForm();
            JOptionPane.showMessageDialog(this, "Driver deleted successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: This driver may be assigned to a bus.");
        }
    }

    private void loadDrivers() {
        try {
            tableModel.setRowCount(0);
            List<Driver> drivers = driverDAO.getAllDrivers();

            for (int i = 0; i < drivers.size(); i++) {
                Driver driver = drivers.get(i);
                tableModel.addRow(new Object[]{
                        driver.getDriverId(),
                        driver.getDriverName(),
                        driver.getPhoneNumber(),
                        driver.getLicenseNumber()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void fillFormFromTable() {
        int row = driverTable.getSelectedRow();
        if (row >= 0) {
            selectedDriverId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            nameField.setText(tableModel.getValueAt(row, 1).toString());
            phoneField.setText(tableModel.getValueAt(row, 2).toString());
            licenseField.setText(tableModel.getValueAt(row, 3).toString());
        }
    }

    private boolean isFormValid() {
        if (nameField.getText().trim().length() == 0
                || phoneField.getText().trim().length() == 0
                || licenseField.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        selectedDriverId = 0;
        nameField.setText("");
        phoneField.setText("");
        licenseField.setText("");
        driverTable.clearSelection();
    }
}
