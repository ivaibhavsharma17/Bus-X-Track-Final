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
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class BusDetailsFrame extends JFrame {
    private String busNumber;
    private JTextArea detailsArea;

    private BusDAO busDAO;
    private DriverDAO driverDAO;
    private RouteDAO routeDAO;
    private StopDAO stopDAO;
    private TimetableDAO timetableDAO;
    private ETAService etaService;

    public BusDetailsFrame(String busNumber) {
        this.busNumber = busNumber;
        busDAO = new BusDAO();
        driverDAO = new DriverDAO();
        routeDAO = new RouteDAO();
        stopDAO = new StopDAO();
        timetableDAO = new TimetableDAO();
        etaService = new ETAService();

        setTitle("BusXTrack - Bus Details");
        setSize(720, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
        loadDetails();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("Bus Details", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
        add(titleLabel, BorderLayout.NORTH);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back");
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SearchBusFrame().setVisible(true);
            }
        });
    }

    private void loadDetails() {
        try {
            Bus bus = busDAO.getBusByNumber(busNumber);
            if (bus == null) {
                detailsArea.setText("Bus not found.");
                return;
            }

            Route route = routeDAO.getRouteById(bus.getRouteId());
            Driver driver = driverDAO.getDriverById(bus.getDriverId());
            List<Stop> stops = stopDAO.getStopsByRouteId(bus.getRouteId());
            List<Timetable> timetables = timetableDAO.getTimetablesByBusId(bus.getBusId());
            int lastStopMinutes = etaService.getLastStopMinutes(stops);

            StringBuilder details = new StringBuilder();
            details.append("Bus Number: ").append(bus.getBusNumber()).append("\n");
            details.append("Capacity: ").append(bus.getCapacity()).append("\n\n");
            details.append("Route: ").append(route.getRouteName()).append("\n");
            details.append("From: ").append(route.getSource()).append("\n");
            details.append("To: ").append(route.getDestination()).append("\n\n");
            details.append("Driver: ").append(driver.getDriverName()).append("\n");
            details.append("Phone: ").append(driver.getPhoneNumber()).append("\n");
            details.append("License: ").append(driver.getLicenseNumber()).append("\n\n");

            details.append("Timetable and Status\n");
            details.append("--------------------\n");
            for (int i = 0; i < timetables.size(); i++) {
                Timetable timetable = timetables.get(i);
                Stop nextStop = etaService.findNextStop(timetable.getDepartureTime(), stops);
                details.append(timetable.getTripType()).append(" trip at ")
                        .append(timetable.getDepartureTime().toString().substring(0, 5))
                        .append(" - ")
                        .append(etaService.getStatus(timetable.getDepartureTime(), lastStopMinutes))
                        .append("\n");

                if (nextStop != null) {
                    details.append("Next stop: ").append(nextStop.getStopName()).append("\n");
                    details.append("ETA: ").append(etaService.getEtaText(timetable.getDepartureTime(), nextStop.getMinutesFromStart())).append("\n");
                } else {
                    details.append("Next stop: Trip completed\n");
                }
                details.append("\n");
            }

            details.append("Route Stops\n");
            details.append("-----------\n");
            for (int i = 0; i < stops.size(); i++) {
                Stop stop = stops.get(i);
                details.append(stop.getStopOrder()).append(". ")
                        .append(stop.getStopName())
                        .append(" - ")
                        .append(stop.getMinutesFromStart())
                        .append(" minutes from start\n");
            }

            detailsArea.setText(details.toString());
            detailsArea.setCaretPosition(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
