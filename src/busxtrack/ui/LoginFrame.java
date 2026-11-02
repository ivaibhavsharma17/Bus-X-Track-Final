package busxtrack.ui;

import busxtrack.dao.AdminDAO;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AdminDAO adminDAO;

    public LoginFrame() {
        adminDAO = new AdminDAO();

        setTitle("BusXTrack - Login");
        setSize(420, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createComponents();
    }

    private void createComponents() {
        JLabel titleLabel = new JLabel("BusXTrack", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(24f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        JButton loginButton = new JButton("Admin Login");
        JButton studentButton = new JButton("Continue as Student");

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username"), gbc);

        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(loginButton, gbc);

        gbc.gridy = 3;
        formPanel.add(studentButton, gbc);

        add(formPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginAdmin();
            }
        });

        studentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new StudentHomeFrame().setVisible(true);
            }
        });
    }

    private void loginAdmin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.length() == 0 || password.length() == 0) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        try {
            if (adminDAO.validateLogin(username, password)) {
                dispose();
                new AdminDashboardFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login details.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}
