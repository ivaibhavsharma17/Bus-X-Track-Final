package busxtrack.dao;

import busxtrack.db.DBConnection;
import busxtrack.model.Bus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BusDAO {
    public boolean addBus(Bus bus) throws SQLException {
        String sql = "INSERT INTO buses (bus_number, route_id, driver_id, capacity) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, bus.getBusNumber());
            statement.setInt(2, bus.getRouteId());
            statement.setInt(3, bus.getDriverId());
            statement.setInt(4, bus.getCapacity());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateBus(Bus bus) throws SQLException {
        String sql = "UPDATE buses SET bus_number = ?, route_id = ?, driver_id = ?, capacity = ? WHERE bus_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, bus.getBusNumber());
            statement.setInt(2, bus.getRouteId());
            statement.setInt(3, bus.getDriverId());
            statement.setInt(4, bus.getCapacity());
            statement.setInt(5, bus.getBusId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteBus(int busId) throws SQLException {
        String sql = "DELETE FROM buses WHERE bus_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, busId);
            return statement.executeUpdate() > 0;
        }
    }

    public Bus getBusById(int busId) throws SQLException {
        String sql = "SELECT * FROM buses WHERE bus_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, busId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return readBus(resultSet);
                }
            }
        }

        return null;
    }

    public Bus getBusByNumber(String busNumber) throws SQLException {
        String sql = "SELECT * FROM buses WHERE bus_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, busNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return readBus(resultSet);
                }
            }
        }

        return null;
    }

    public List<Bus> getAllBuses() throws SQLException {
        List<Bus> buses = new ArrayList<Bus>();
        String sql = "SELECT * FROM buses ORDER BY bus_number";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                buses.add(readBus(resultSet));
            }
        }

        return buses;
    }

    public List<Bus> getBusesByRouteId(int routeId) throws SQLException {
        List<Bus> buses = new ArrayList<Bus>();
        String sql = "SELECT * FROM buses WHERE route_id = ? ORDER BY bus_number";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, routeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    buses.add(readBus(resultSet));
                }
            }
        }

        return buses;
    }

    public List<String[]> getBusTableData() throws SQLException {
        List<String[]> rows = new ArrayList<String[]>();
        String sql = "SELECT b.bus_id, b.bus_number, r.route_name, d.driver_name, b.capacity "
                + "FROM buses b "
                + "JOIN routes r ON b.route_id = r.route_id "
                + "JOIN drivers d ON b.driver_id = d.driver_id "
                + "ORDER BY b.bus_number";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String[] row = new String[5];
                row[0] = String.valueOf(resultSet.getInt("bus_id"));
                row[1] = resultSet.getString("bus_number");
                row[2] = resultSet.getString("route_name");
                row[3] = resultSet.getString("driver_name");
                row[4] = String.valueOf(resultSet.getInt("capacity"));
                rows.add(row);
            }
        }

        return rows;
    }

    private Bus readBus(ResultSet resultSet) throws SQLException {
        Bus bus = new Bus();
        bus.setBusId(resultSet.getInt("bus_id"));
        bus.setBusNumber(resultSet.getString("bus_number"));
        bus.setRouteId(resultSet.getInt("route_id"));
        bus.setDriverId(resultSet.getInt("driver_id"));
        bus.setCapacity(resultSet.getInt("capacity"));
        return bus;
    }
}
