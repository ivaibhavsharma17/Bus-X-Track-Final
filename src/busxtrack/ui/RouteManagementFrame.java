package busxtrack.ui;

import busxtrack.dao.RouteDAO;
import busxtrack.model.Route;

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

public class RouteManagementFrame extends JFrame {
    private JTextField routeNameField;
    private JTextField sourceField;
    private JTextField destinationField;
    private JTable routeTable;
    private DefaultTableModel tableModel;
    private RouteDAO routeDAO;
    private int selectedRouteId;

    public RouteManagementFrame() {
        routeDAO = new RouteDAO();
        selectedRouteId = 0;

        setTitle("BusXTrack - Manage Routes");
        setSize(720, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
        loadRoutes();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("Manage Routes", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        routeNameField = new JTextField();
        sourceField = new JTextField();
        destinationField = new JTextField();

        formPanel.add(new JLabel("Route Name"));
        formPanel.add(routeNameField);
        formPanel.add(new JLabel("Source"));
        formPanel.add(sourceField);
        formPanel.add(new JLabel("Destination"));
        formPanel.add(destinationField);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        formPanel.add(addButton);
        formPanel.add(updateButton);

        add(formPanel, BorderLayout.WEST);

        tableModel = new DefaultTableModel(new String[]{"ID", "Route Name", "Source", "Destination"}, 0);
        routeTable = new JTable(tableModel);
        add(new JScrollPane(routeTable), BorderLayout.CENTER);

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
                addRoute();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateRoute();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRoute();
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

        routeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });
    }

    private void addRoute() {
        if (!isFormValid()) {
            return;
        }

        Route route = new Route(0, routeNameField.getText().trim(), sourceField.getText().trim(), destinationField.getText().trim());

        try {
            routeDAO.addRoute(route);
            loadRoutes();
            clearForm();
            JOptionPane.showMessageDialog(this, "Route added successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateRoute() {
        if (selectedRouteId == 0) {
            JOptionPane.showMessageDialog(this, "Please select a route from the table.");
            return;
        }

        if (!isFormValid()) {
            return;
        }

        Route route = new Route(selectedRouteId, routeNameField.getText().trim(), sourceField.getText().trim(), destinationField.getText().trim());

        try {
            routeDAO.updateRoute(route);
            loadRoutes();
            clearForm();
            JOptionPane.showMessageDialog(this, "Route updated successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteRoute() {
        if (selectedRouteId == 0) {
            JOptionPane.showMessageDialog(this, "Please select a route from the table.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this, "Delete selected route?");
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            routeDAO.deleteRoute(selectedRouteId);
            loadRoutes();
            clearForm();
            JOptionPane.showMessageDialog(this, "Route deleted successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: This route may be assigned to buses or timetable records.");
        }
    }

    private void loadRoutes() {
        try {
            tableModel.setRowCount(0);
            List<Route> routes = routeDAO.getAllRoutes();

            for (int i = 0; i < routes.size(); i++) {
                Route route = routes.get(i);
                tableModel.addRow(new Object[]{
                        route.getRouteId(),
                        route.getRouteName(),
                        route.getSource(),
                        route.getDestination()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void fillFormFromTable() {
        int row = routeTable.getSelectedRow();
        if (row >= 0) {
            selectedRouteId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            routeNameField.setText(tableModel.getValueAt(row, 1).toString());
            sourceField.setText(tableModel.getValueAt(row, 2).toString());
            destinationField.setText(tableModel.getValueAt(row, 3).toString());
        }
    }

    private boolean isFormValid() {
        if (routeNameField.getText().trim().length() == 0
                || sourceField.getText().trim().length() == 0
                || destinationField.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        selectedRouteId = 0;
        routeNameField.setText("");
        sourceField.setText("");
        destinationField.setText("");
        routeTable.clearSelection();
    }
}
