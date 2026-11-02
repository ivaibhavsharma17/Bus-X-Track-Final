package busxtrack.dao;

import busxtrack.db.DBConnection;
import busxtrack.model.Timetable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {
    public boolean addTimetable(Timetable timetable) throws SQLException {
        String sql = "INSERT INTO timetable (bus_id, route_id, departure_time, trip_type) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, timetable.getBusId());
            statement.setInt(2, timetable.getRouteId());
            statement.setTime(3, timetable.getDepartureTime());
            statement.setString(4, timetable.getTripType());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateTimetable(Timetable timetable) throws SQLException {
        String sql = "UPDATE timetable SET bus_id = ?, route_id = ?, departure_time = ?, trip_type = ? WHERE timetable_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, timetable.getBusId());
            statement.setInt(2, timetable.getRouteId());
            statement.setTime(3, timetable.getDepartureTime());
            statement.setString(4, timetable.getTripType());
            statement.setInt(5, timetable.getTimetableId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteTimetable(int timetableId) throws SQLException {
        String sql = "DELETE FROM timetable WHERE timetable_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, timetableId);
            return statement.executeUpdate() > 0;
        }
    }

    public Timetable getTimetableById(int timetableId) throws SQLException {
        String sql = "SELECT * FROM timetable WHERE timetable_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, timetableId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return readTimetable(resultSet);
                }
            }
        }

        return null;
    }

    public List<Timetable> getTimetablesByBusId(int busId) throws SQLException {
        List<Timetable> timetables = new ArrayList<Timetable>();
        String sql = "SELECT * FROM timetable WHERE bus_id = ? ORDER BY departure_time";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, busId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    timetables.add(readTimetable(resultSet));
                }
            }
        }

        return timetables;
    }

    public List<String[]> getTimetableTableData() throws SQLException {
        List<String[]> rows = new ArrayList<String[]>();
        String sql = "SELECT t.timetable_id, b.bus_number, r.route_name, t.departure_time, t.trip_type "
                + "FROM timetable t "
                + "JOIN buses b ON t.bus_id = b.bus_id "
                + "JOIN routes r ON t.route_id = r.route_id "
                + "ORDER BY t.departure_time, b.bus_number";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String[] row = new String[5];
                row[0] = String.valueOf(resultSet.getInt("timetable_id"));
                row[1] = resultSet.getString("bus_number");
                row[2] = resultSet.getString("route_name");
                row[3] = resultSet.getTime("departure_time").toString();
                row[4] = resultSet.getString("trip_type");
                rows.add(row);
            }
        }

        return rows;
    }

    private Timetable readTimetable(ResultSet resultSet) throws SQLException {
        Timetable timetable = new Timetable();
        timetable.setTimetableId(resultSet.getInt("timetable_id"));
        timetable.setBusId(resultSet.getInt("bus_id"));
        timetable.setRouteId(resultSet.getInt("route_id"));
        timetable.setDepartureTime(resultSet.getTime("departure_time"));
        timetable.setTripType(resultSet.getString("trip_type"));
        return timetable;
    }
}
