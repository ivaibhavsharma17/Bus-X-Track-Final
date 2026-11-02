package busxtrack.ui;

import busxtrack.dao.BusDAO;
import busxtrack.dao.RouteDAO;
import busxtrack.dao.TimetableDAO;
import busxtrack.model.Bus;
import busxtrack.model.Route;
import busxtrack.model.Timetable;

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
import java.sql.Time;
import java.util.List;

public class TimetableManagementFrame extends JFrame {
    private JComboBox<ComboItem> busComboBox;
    private JComboBox<ComboItem> routeComboBox;
    private JTextField departureField;
    private JComboBox<String> tripTypeComboBox;
    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private TimetableDAO timetableDAO;
    private BusDAO busDAO;
    private RouteDAO routeDAO;
    private int selectedTimetableId;

    public TimetableManagementFrame() {
        timetableDAO = new TimetableDAO();
        busDAO = new BusDAO();
        routeDAO = new RouteDAO();
        selectedTimetableId = 0;

        setTitle("BusXTrack - Manage Timetable");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
        loadBuses();
        loadRoutes();
        loadTimetable();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("Manage Timetable", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        busComboBox = new JComboBox<ComboItem>();
        routeComboBox = new JComboBox<ComboItem>();
        departureField = new JTextField();
        tripTypeComboBox = new JComboBox<String>(new String[]{"MORNING", "EVENING"});

        formPanel.add(new JLabel("Bus"));
        formPanel.add(busComboBox);
        formPanel.add(new JLabel("Route"));
        formPanel.add(routeComboBox);
        formPanel.add(new JLabel("Departure Time (HH:mm)"));
        formPanel.add(departureField);
        formPanel.add(new JLabel("Trip Type"));
        formPanel.add(tripTypeComboBox);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        formPanel.add(addButton);
        formPanel.add(updateButton);

        add(formPanel, BorderLayout.WEST);

        tableModel = new DefaultTableModel(new String[]{"ID", "Bus", "Route", "Departure", "Trip Type"}, 0);
        timetableTable = new JTable(tableModel);
        add(new JScrollPane(timetableTable), BorderLayout.CENTER);

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
                addTimetable();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTimetable();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteTimetable();
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

        timetableTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });
    }

    private void addTimetable() {
        Timetable timetable = readTimetableFromForm(0);
        if (timetable == null) {
            return;
        }

        try {
            timetableDAO.addTimetable(timetable);
            loadTimetable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Timetable added successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateTimetable() {
        if (selectedTimetableId == 0) {
            JOptionPane.showMessageDialog(this, "Please select a timetable record.");
            return;
        }

        Timetable timetable = readTimetableFromForm(selectedTimetableId);
        if (timetable == null) {
            return;
        }

        try {
            timetableDAO.updateTimetable(timetable);
            loadTimetable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Timetable updated successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteTimetable() {
        if (selectedTimetableId == 0) {
            JOptionPane.showMessageDialog(this, "Please select a timetable record.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this, "Delete selected timetable record?");
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            timetableDAO.deleteTimetable(selectedTimetableId);
            loadTimetable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Timetable deleted successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private Timetable readTimetableFromForm(int timetableId) {
        ComboItem selectedBus = (ComboItem) busComboBox.getSelectedItem();
        ComboItem selectedRoute = (ComboItem) routeComboBox.getSelectedItem();

        if (selectedBus == null || selectedRoute == null || departureField.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return null;
        }

        try {
            String timeText = departureField.getText().trim();
            if (timeText.length() == 5) {
                timeText = timeText + ":00";
            }

            Time departureTime = Time.valueOf(timeText);
            return new Timetable(timetableId, selectedBus.getId(), selectedRoute.getId(), departureTime,
                    tripTypeComboBox.getSelectedItem().toString());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Enter time in HH:mm format, for example 08:30.");
            return null;
        }
    }

    private void loadBuses() {
        try {
            busComboBox.removeAllItems();
            List<Bus> buses = busDAO.getAllBuses();

            for (int i = 0; i < buses.size(); i++) {
                Bus bus = buses.get(i);
                busComboBox.addItem(new ComboItem(bus.getBusId(), bus.getBusNumber()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
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

    private void loadTimetable() {
        try {
            tableModel.setRowCount(0);
            List<String[]> rows = timetableDAO.getTimetableTableData();

            for (int i = 0; i < rows.size(); i++) {
                tableModel.addRow(rows.get(i));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void fillFormFromTable() {
        int row = timetableTable.getSelectedRow();
        if (row >= 0) {
            selectedTimetableId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

            try {
                Timetable timetable = timetableDAO.getTimetableById(selectedTimetableId);
                if (timetable != null) {
                    selectComboItem(busComboBox, timetable.getBusId());
                    selectComboItem(routeComboBox, timetable.getRouteId());
                    departureField.setText(timetable.getDepartureTime().toString().substring(0, 5));
                    tripTypeComboBox.setSelectedItem(timetable.getTripType());
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
        selectedTimetableId = 0;
        departureField.setText("");
        timetableTable.clearSelection();
        if (busComboBox.getItemCount() > 0) {
            busComboBox.setSelectedIndex(0);
        }
        if (routeComboBox.getItemCount() > 0) {
            routeComboBox.setSelectedIndex(0);
        }
        tripTypeComboBox.setSelectedIndex(0);
    }
}
