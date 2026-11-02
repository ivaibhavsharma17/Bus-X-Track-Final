package busxtrack.ui;

import busxtrack.dao.BusDAO;
import busxtrack.dao.RouteDAO;
import busxtrack.dao.StopDAO;
import busxtrack.dao.TimetableDAO;
import busxtrack.model.Bus;
import busxtrack.model.Route;
import busxtrack.model.Stop;
import busxtrack.model.Timetable;
import busxtrack.service.ETAService;

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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class SearchStopFrame extends JFrame {
    private JTextField stopNameField;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    private StopDAO stopDAO;
    private BusDAO busDAO;
    private RouteDAO routeDAO;
    private TimetableDAO timetableDAO;
    private ETAService etaService;

    public SearchStopFrame() {
        stopDAO = new StopDAO();
        busDAO = new BusDAO();
        routeDAO = new RouteDAO();
        timetableDAO = new TimetableDAO();
        etaService = new ETAService();

        setTitle("BusXTrack - Search by Stop");
        setSize(900, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("Search by Stop", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout());
        stopNameField = new JTextField(18);
        JButton searchButton = new JButton("Search");
        JButton backButton = new JButton("Back");

        topPanel.add(new JLabel("Stop Name"));
        topPanel.add(stopNameField);
        topPanel.add(searchButton);
        topPanel.add(backButton);
        add(topPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new String[]{"Stop", "Route", "Bus", "Trip", "Departure", "Expected", "ETA", "Status"}, 0);
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchStop();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new StudentHomeFrame().setVisible(true);
            }
        });
    }

    private void searchStop() {
        String stopName = stopNameField.getText().trim();

        if (stopName.length() == 0) {
            JOptionPane.showMessageDialog(this, "Please enter stop name.");
            return;
        }

        try {
            tableModel.setRowCount(0);
            List<Stop> stops = stopDAO.searchStopsByName(stopName);

            if (stops.size() == 0) {
                JOptionPane.showMessageDialog(this, "No matching stop found.");
                return;
            }

            for (int i = 0; i < stops.size(); i++) {
                Stop stop = stops.get(i);
                Route route = routeDAO.getRouteById(stop.getRouteId());
                List<Bus> buses = busDAO.getBusesByRouteId(stop.getRouteId());
                List<Stop> routeStops = stopDAO.getStopsByRouteId(stop.getRouteId());
                int lastStopMinutes = etaService.getLastStopMinutes(routeStops);

                for (int j = 0; j < buses.size(); j++) {
                    Bus bus = buses.get(j);
                    List<Timetable> timetables = timetableDAO.getTimetablesByBusId(bus.getBusId());

                    for (int k = 0; k < timetables.size(); k++) {
                        Timetable timetable = timetables.get(k);
                        tableModel.addRow(new Object[]{
                                stop.getStopName(),
                                route.getRouteName(),
                                bus.getBusNumber(),
                                timetable.getTripType(),
                                timetable.getDepartureTime().toString().substring(0, 5),
                                etaService.getExpectedTime(timetable.getDepartureTime(), stop.getMinutesFromStart()),
                                etaService.getEtaText(timetable.getDepartureTime(), stop.getMinutesFromStart()),
                                etaService.getStatus(timetable.getDepartureTime(), lastStopMinutes)
                        });
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
