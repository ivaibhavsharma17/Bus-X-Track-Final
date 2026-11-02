package busxtrack.ui;

import busxtrack.dao.BusDAO;
import busxtrack.dao.DriverDAO;
import busxtrack.dao.RouteDAO;
import busxtrack.model.Bus;
import busxtrack.model.Driver;
import busxtrack.model.Route;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class BusManagementFrame extends JFrame {
    private JTextField busNumberField;
    private JComboBox<ComboItem> routeComboBox;
    private JComboBox<ComboItem> driverComboBox;
    private JTextField capacityField;
    private JTable busTable;
    private DefaultTableModel tableModel;
    private BusDAO busDAO;
    private RouteDAO routeDAO;
    private DriverDAO driverDAO;
    private int selectedBusId;

    public BusManagementFrame() {
        busDAO = new BusDAO();
        routeDAO = new RouteDAO();
        driverDAO = new DriverDAO();
        selectedBusId = 0;

        setTitle("BusXTrack - Manage Buses");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
        loadRoutes();
        loadDrivers();
        loadBuses();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("Manage Buses", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        busNumberField = new JTextField();
        routeComboBox = new JComboBox<ComboItem>();
        driverComboBox = new JComboBox<ComboItem>();
        capacityField = new JTextField();

        formPanel.add(new JLabel("Bus Number"));
        formPanel.add(busNumberField);
        formPanel.add(new JLabel("Route"));
        formPanel.add(routeComboBox);
        formPanel.add(new JLabel("Driver"));
        formPanel.add(driverComboBox);
        formPanel.add(new JLabel("Capacity"));
        formPanel.add(capacityField);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        formPanel.add(addButton);
        formPanel.add(updateButton);

        add(formPanel, BorderLayout.WEST);

        tableModel = new DefaultTableModel(new String[]{"ID", "Bus Number", "Route", "Driver", "Capacity"}, 0);
        busTable = new JTable(tableModel);
        add(new JScrollPane(busTable), BorderLayout.CENTER);

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
                addBus();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateBus();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteBus();
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

        busTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });
    }

    private void addBus() {
        Bus bus = readBusFromForm(0);
        if (bus == null) {
            return;
        }

        try {
            busDAO.addBus(bus);
            loadBuses();
            clearForm();
            JOptionPane.showMessageDialog(this, "Bus added successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateBus() {
        if (selectedBusId == 0) {
            JOptionPane.showMessageDialog(this, "Please select a bus from the table.");
            return;
        }

        Bus bus = readBusFromForm(selectedBusId);
        if (bus == null) {
            return;
        }

        try {
            busDAO.updateBus(bus);
            loadBuses();
            clearForm();
            JOptionPane.showMessageDialog(this, "Bus updated successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteBus() {
        if (selectedBusId == 0) {
            JOptionPane.showMessageDialog(this, "Please select a bus from the table.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this, "Delete selected bus?");
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            busDAO.deleteBus(selectedBusId);
            loadBuses();
            clearForm();
            JOptionPane.showMessageDialog(this, "Bus deleted successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private Bus readBusFromForm(int busId) {
        ComboItem selectedRoute = (ComboItem) routeComboBox.getSelectedItem();
        ComboItem selectedDriver = (ComboItem) driverComboBox.getSelectedItem();

        if (selectedRoute == null
                || selectedDriver == null
                || busNumberField.getText().trim().length() == 0
                || capacityField.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return null;
        }

        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            return new Bus(busId, busNumberField.getText().trim(), selectedRoute.getId(), selectedDriver.getId(), capacity);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be a number.");
            return null;
        }
    }

    private void loadRoutes() {
        try {
            routeComboBox.removeAllItems();
            List<Route> routes = routeDAO.getAllRoutes();

            for (int i = 0; i < routes.size(); i++) {
                Route route = routes.get(i);
                routeComboBox.addItem(new ComboItem(route.getRouteId(), route.getRouteName()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadDrivers() {
        try {
            driverComboBox.removeAllItems();
            List<Driver> drivers = driverDAO.getAllDrivers();

            for (int i = 0; i < drivers.size(); i++) {
                Driver driver = drivers.get(i);
                driverComboBox.addItem(new ComboItem(driver.getDriverId(), driver.getDriverName()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadBuses() {
        try {
            tableModel.setRowCount(0);
            List<String[]> rows = busDAO.getBusTableData();

            for (int i = 0; i < rows.size(); i++) {
                tableModel.addRow(rows.get(i));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void fillFormFromTable() {
        int row = busTable.getSelectedRow();
        if (row >= 0) {
            selectedBusId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

            try {
                Bus bus = busDAO.getBusById(selectedBusId);
                if (bus != null) {
                    busNumberField.setText(bus.getBusNumber());
                    selectComboItem(routeComboBox, bus.getRouteId());
                    selectComboItem(driverComboBox, bus.getDriverId());
                    capacityField.setText(String.valueOf(bus.getCapacity()));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void selectComboItem(JComboBox<ComboItem> comboBox, int id) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboItem item = comboBox.getItemAt(i);
            if (item.getId() == id) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void clearForm() {
        selectedBusId = 0;
        busNumberField.setText("");
        capacityField.setText("");
        busTable.clearSelection();
        if (routeComboBox.getItemCount() > 0) {
            routeComboBox.setSelectedIndex(0);
        }
        if (driverComboBox.getItemCount() > 0) {
            driverComboBox.setSelectedIndex(0);
        }
    }
}
