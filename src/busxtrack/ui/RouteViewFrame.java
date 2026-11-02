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
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class RouteViewFrame extends JFrame {
    private JComboBox<ComboItem> routeComboBox;
    private JTable stopTable;
    private DefaultTableModel tableModel;
    private RouteDAO routeDAO;
    private StopDAO stopDAO;

    public RouteViewFrame() {
        routeDAO = new RouteDAO();
        stopDAO = new StopDAO();

        setTitle("BusXTrack - View Routes");
        setSize(700, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
        loadRoutes();
        loadStopsForSelectedRoute();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("View Route Stops", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout());
        routeComboBox = new JComboBox<ComboItem>();
        JButton viewButton = new JButton("View");
        JButton backButton = new JButton("Back");

        topPanel.add(new JLabel("Route"));
        topPanel.add(routeComboBox);
        topPanel.add(viewButton);
        topPanel.add(backButton);
        add(topPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new String[]{"Stop Order", "Stop Name", "Minutes From Start"}, 0);
        stopTable = new JTable(tableModel);
        add(new JScrollPane(stopTable), BorderLayout.CENTER);

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadStopsForSelectedRoute();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new StudentHomeFrame().setVisible(true);
            }
        });
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

    private void loadStopsForSelectedRoute() {
        ComboItem selectedRoute = (ComboItem) routeComboBox.getSelectedItem();
        if (selectedRoute == null) {
            return;
        }

        try {
            tableModel.setRowCount(0);
            List<Stop> stops = stopDAO.getStopsByRouteId(selectedRoute.getId());

            for (int i = 0; i < stops.size(); i++) {
                Stop stop = stops.get(i);
                tableModel.addRow(new Object[]{
                        stop.getStopOrder(),
                        stop.getStopName(),
                        stop.getMinutesFromStart()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
