package busxtrack.ui;

import busxtrack.dao.RouteDAO;
import busxtrack.dao.StopDAO;
import busxtrack.model.Route;
import busxtrack.model.Stop;

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

public class StopManagementFrame extends JFrame {
    private JComboBox<ComboItem> routeComboBox;
    private JTextField stopNameField;
    private JTextField stopOrderField;
    private JTextField minutesField;
    private JTable stopTable;
    private DefaultTableModel tableModel;
    private StopDAO stopDAO;
    private RouteDAO routeDAO;
    private int selectedStopId;

    public StopManagementFrame() {
        stopDAO = new StopDAO();
        routeDAO = new RouteDAO();
        selectedStopId = 0;

        setTitle("BusXTrack - Manage Stops");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
        loadRoutes();
        loadStops();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("Manage Stops", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        routeComboBox = new JComboBox<ComboItem>();
        stopNameField = new JTextField();
        stopOrderField = new JTextField();
        minutesField = new JTextField();

        formPanel.add(new JLabel("Route"));
        formPanel.add(routeComboBox);
        formPanel.add(new JLabel("Stop Name"));
        formPanel.add(stopNameField);
        formPanel.add(new JLabel("Stop Order"));
        formPanel.add(stopOrderField);
        formPanel.add(new JLabel("Minutes From Start"));
        formPanel.add(minutesField);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        formPanel.add(addButton);
        formPanel.add(updateButton);

        add(formPanel, BorderLayout.WEST);

        tableModel = new DefaultTableModel(new String[]{"ID", "Route", "Stop", "Order", "Minutes"}, 0);
        stopTable = new JTable(tableModel);
        add(new JScrollPane(stopTable), BorderLayout.CENTER);

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
                addStop();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateStop();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteStop();
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

        stopTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });
    }

    private void addStop() {
        Stop stop = readStopFromForm(0);
        if (stop == null) {
            return;
        }

        try {
            stopDAO.addStop(stop);
            loadStops();
            clearForm();
            JOptionPane.showMessageDialog(this, "Stop added successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateStop() {
        if (selectedStopId == 0) {
            JOptionPane.showMessageDialog(this, "Please select a stop from the table.");
            return;
        }

        Stop stop = readStopFromForm(selectedStopId);
        if (stop == null) {
            return;
        }

        try {
            stopDAO.updateStop(stop);
            loadStops();
            clearForm();
            JOptionPane.showMessageDialog(this, "Stop updated successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteStop() {
        if (selectedStopId == 0) {
            JOptionPane.showMessageDialog(this, "Please select a stop from the table.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this, "Delete selected stop?");
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            stopDAO.deleteStop(selectedStopId);
            loadStops();
            clearForm();
            JOptionPane.showMessageDialog(this, "Stop deleted successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private Stop readStopFromForm(int stopId) {
        ComboItem selectedRoute = (ComboItem) routeComboBox.getSelectedItem();

        if (selectedRoute == null
                || stopNameField.getText().trim().length() == 0
                || stopOrderField.getText().trim().length() == 0
                || minutesField.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return null;
        }

        try {
            int stopOrder = Integer.parseInt(stopOrderField.getText().trim());
            int minutes = Integer.parseInt(minutesField.getText().trim());
            return new Stop(stopId, selectedRoute.getId(), stopNameField.getText().trim(), stopOrder, minutes);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stop order and minutes must be numbers.");
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

    private void loadStops() {
        try {
            tableModel.setRowCount(0);
            List<String[]> rows = stopDAO.getStopTableData();

            for (int i = 0; i < rows.size(); i++) {
                tableModel.addRow(rows.get(i));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void fillFormFromTable() {
        int row = stopTable.getSelectedRow();
        if (row >= 0) {
            selectedStopId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

            try {
                Stop stop = stopDAO.getStopById(selectedStopId);
                if (stop != null) {
                    selectComboItem(routeComboBox, stop.getRouteId());
                    stopNameField.setText(stop.getStopName());
                    stopOrderField.setText(String.valueOf(stop.getStopOrder()));
                    minutesField.setText(String.valueOf(stop.getMinutesFromStart()));
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
        selectedStopId = 0;
        stopNameField.setText("");
        stopOrderField.setText("");
        minutesField.setText("");
        stopTable.clearSelection();
        if (routeComboBox.getItemCount() > 0) {
            routeComboBox.setSelectedIndex(0);
        }
    }
}
