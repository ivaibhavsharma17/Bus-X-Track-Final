package busxtrack.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboardFrame extends JFrame {
    public AdminDashboardFrame() {
        setTitle("BusXTrack - Admin Dashboard");
        setSize(460, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(22f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JButton busButton = new JButton("Manage Buses");
        JButton driverButton = new JButton("Manage Drivers");
        JButton routeButton = new JButton("Manage Routes");
        JButton stopButton = new JButton("Manage Stops");
        JButton timetableButton = new JButton("Manage Timetable");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(busButton);
        buttonPanel.add(driverButton);
        buttonPanel.add(routeButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(timetableButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);

        busButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new BusManagementFrame().setVisible(true);
            }
        });

        driverButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new DriverManagementFrame().setVisible(true);
            }
        });

        routeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RouteManagementFrame().setVisible(true);
            }
        });

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new StopManagementFrame().setVisible(true);
            }
        });

        timetableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new TimetableManagementFrame().setVisible(true);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
    }
}
