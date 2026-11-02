package busxtrack.ui;

import busxtrack.dao.BusDAO;
import busxtrack.dao.DriverDAO;
import busxtrack.dao.RouteDAO;
import busxtrack.dao.StopDAO;
import busxtrack.dao.TimetableDAO;
import busxtrack.model.Bus;
import busxtrack.model.Driver;
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

public class SearchBusFrame extends JFrame {
    private JTextField busNumberField;
    private JLabel summaryLabel;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private String searchedBusNumber;

    private BusDAO busDAO;
    private DriverDAO driverDAO;
    private RouteDAO routeDAO;
    private StopDAO stopDAO;
    private TimetableDAO timetableDAO;
    private ETAService etaService;

    public SearchBusFrame() {
        busDAO = new BusDAO();
        driverDAO = new DriverDAO();
        routeDAO = new RouteDAO();
        stopDAO = new StopDAO();
        timetableDAO = new TimetableDAO();
        etaService = new ETAService();
        searchedBusNumber = "";

        setTitle("BusXTrack - Search Bus");
        setSize(850, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("Search Bus", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout());
        busNumberField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        JButton detailsButton = new JButton("View Details");
        JButton backButton = new JButton("Back");

        topPanel.add(new JLabel("Bus Number"));
        topPanel.add(busNumberField);
        topPanel.add(searchButton);
        topPanel.add(detailsButton);
        topPanel.add(backButton);
        add(topPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        summaryLabel = new JLabel("Enter a bus number to search.");
        summaryLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        centerPanel.add(summaryLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Trip", "Departure", "Status", "Next Stop", "ETA"}, 0);
        resultTable = new JTable(tableModel);
        centerPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchBus();
            }
        });

        detailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDetails();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new StudentHomeFrame().setVisible(true);
            }
        });
    }

    private void searchBus() {
        String busNumber = busNumberField.getText().trim();

        if (busNumber.length() == 0) {
            JOptionPane.showMessageDialog(this, "Please enter bus number.");
            return;
        }

        try {
            tableModel.setRowCount(0);
            Bus bus = busDAO.getBusByNumber(busNumber);

            if (bus == null) {
                summaryLabel.setText("No bus found for number: " + busNumber);
                searchedBusNumber = "";
                return;
            }

            searchedBusNumber = bus.getBusNumber();
            Route route = routeDAO.getRouteById(bus.getRouteId());
            Driver driver = driverDAO.getDriverById(bus.getDriverId());
            List<Stop> stops = stopDAO.getStopsByRouteId(bus.getRouteId());
            List<Timetable> timetables = timetableDAO.getTimetablesByBusId(bus.getBusId());

            summaryLabel.setText("Bus " + bus.getBusNumber()
                    + " | Route: " + route.getRouteName()
                    + " | Driver: " + driver.getDriverName()
                    + " | Phone: " + driver.getPhoneNumber());

            int lastStopMinutes = etaService.getLastStopMinutes(stops);

            for (int i = 0; i < timetables.size(); i++) {
                Timetable timetable = timetables.get(i);
                Stop nextStop = etaService.findNextStop(timetable.getDepartureTime(), stops);
                String nextStopName = "Trip completed";
                String etaText = "Trip completed";

                if (nextStop != null) {
                    nextStopName = nextStop.getStopName();
                    etaText = etaService.getEtaText(timetable.getDepartureTime(), nextStop.getMinutesFromStart());
                }

                tableModel.addRow(new Object[]{
                        timetable.getTripType(),
                        timetable.getDepartureTime().toString().substring(0, 5),
                        etaService.getStatus(timetable.getDepartureTime(), lastStopMinutes),
                        nextStopName,
                        etaText
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void openDetails() {
        if (searchedBusNumber.length() == 0) {
            JOptionPane.showMessageDialog(this, "Please search a bus first.");
            return;
        }

        dispose();
        new BusDetailsFrame(searchedBusNumber).setVisible(true);
    }
}
