package busxtrack.dao;

import busxtrack.db.DBConnection;
import busxtrack.model.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {
    public boolean addDriver(Driver driver) throws SQLException {
        String sql = "INSERT INTO drivers (driver_name, phone_number, license_number) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, driver.getDriverName());
            statement.setString(2, driver.getPhoneNumber());
            statement.setString(3, driver.getLicenseNumber());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateDriver(Driver driver) throws SQLException {
        String sql = "UPDATE drivers SET driver_name = ?, phone_number = ?, license_number = ? WHERE driver_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, driver.getDriverName());
            statement.setString(2, driver.getPhoneNumber());
            statement.setString(3, driver.getLicenseNumber());
            statement.setInt(4, driver.getDriverId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteDriver(int driverId) throws SQLException {
        String sql = "DELETE FROM drivers WHERE driver_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, driverId);
            return statement.executeUpdate() > 0;
        }
    }

    public Driver getDriverById(int driverId) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE driver_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, driverId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return readDriver(resultSet);
                }
            }
        }

        return null;
    }

    public List<Driver> getAllDrivers() throws SQLException {
        List<Driver> drivers = new ArrayList<Driver>();
        String sql = "SELECT * FROM drivers ORDER BY driver_name";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                drivers.add(readDriver(resultSet));
            }
        }

        return drivers;
    }

    private Driver readDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setDriverId(resultSet.getInt("driver_id"));
        driver.setDriverName(resultSet.getString("driver_name"));
        driver.setPhoneNumber(resultSet.getString("phone_number"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }
}
