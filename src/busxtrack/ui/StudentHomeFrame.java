package busxtrack.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentHomeFrame extends JFrame {
    public StudentHomeFrame() {
        setTitle("BusXTrack - Student");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("Student Home", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(22f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 60, 25, 60));

        JButton searchBusButton = new JButton("Search Bus");
        JButton searchStopButton = new JButton("Search by Stop");
        JButton routeButton = new JButton("View Routes");
        JButton backButton = new JButton("Back to Login");

        buttonPanel.add(searchBusButton);
        buttonPanel.add(searchStopButton);
        buttonPanel.add(routeButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.CENTER);

        searchBusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SearchBusFrame().setVisible(true);
            }
        });

        searchStopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SearchStopFrame().setVisible(true);
            }
        });

        routeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RouteViewFrame().setVisible(true);
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
    }
}
