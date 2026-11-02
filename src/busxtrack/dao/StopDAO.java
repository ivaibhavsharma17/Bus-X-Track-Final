package busxtrack.dao;

import busxtrack.db.DBConnection;
import busxtrack.model.Stop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StopDAO {
    public boolean addStop(Stop stop) throws SQLException {
        String sql = "INSERT INTO stops (route_id, stop_name, stop_order, minutes_from_start) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, stop.getRouteId());
            statement.setString(2, stop.getStopName());
            statement.setInt(3, stop.getStopOrder());
            statement.setInt(4, stop.getMinutesFromStart());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateStop(Stop stop) throws SQLException {
        String sql = "UPDATE stops SET route_id = ?, stop_name = ?, stop_order = ?, minutes_from_start = ? WHERE stop_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, stop.getRouteId());
            statement.setString(2, stop.getStopName());
            statement.setInt(3, stop.getStopOrder());
            statement.setInt(4, stop.getMinutesFromStart());
            statement.setInt(5, stop.getStopId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteStop(int stopId) throws SQLException {
        String sql = "DELETE FROM stops WHERE stop_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, stopId);
            return statement.executeUpdate() > 0;
        }
    }

    public Stop getStopById(int stopId) throws SQLException {
        String sql = "SELECT * FROM stops WHERE stop_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, stopId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return readStop(resultSet);
                }
            }
        }

        return null;
    }

    public List<Stop> getStopsByRouteId(int routeId) throws SQLException {
        List<Stop> stops = new ArrayList<Stop>();
        String sql = "SELECT * FROM stops WHERE route_id = ? ORDER BY stop_order";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, routeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    stops.add(readStop(resultSet));
                }
            }
        }

        return stops;
    }

    public List<Stop> searchStopsByName(String stopName) throws SQLException {
        List<Stop> stops = new ArrayList<Stop>();
        String sql = "SELECT * FROM stops WHERE stop_name LIKE ? ORDER BY stop_name";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + stopName + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    stops.add(readStop(resultSet));
                }
            }
        }

        return stops;
    }

    public List<String[]> getStopTableData() throws SQLException {
        List<String[]> rows = new ArrayList<String[]>();
        String sql = "SELECT s.stop_id, r.route_name, s.stop_name, s.stop_order, s.minutes_from_start "
                + "FROM stops s JOIN routes r ON s.route_id = r.route_id "
                + "ORDER BY r.route_name, s.stop_order";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String[] row = new String[5];
                row[0] = String.valueOf(resultSet.getInt("stop_id"));
                row[1] = resultSet.getString("route_name");
                row[2] = resultSet.getString("stop_name");
                row[3] = String.valueOf(resultSet.getInt("stop_order"));
                row[4] = String.valueOf(resultSet.getInt("minutes_from_start"));
                rows.add(row);
            }
        }

        return rows;
    }

    private Stop readStop(ResultSet resultSet) throws SQLException {
        Stop stop = new Stop();
        stop.setStopId(resultSet.getInt("stop_id"));
        stop.setRouteId(resultSet.getInt("route_id"));
        stop.setStopName(resultSet.getString("stop_name"));
        stop.setStopOrder(resultSet.getInt("stop_order"));
        stop.setMinutesFromStart(resultSet.getInt("minutes_from_start"));
        return stop;
    }
}
