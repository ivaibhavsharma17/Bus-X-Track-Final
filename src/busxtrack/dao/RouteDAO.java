package busxtrack.dao;

import busxtrack.db.DBConnection;
import busxtrack.model.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RouteDAO {
    public boolean addRoute(Route route) throws SQLException {
        String sql = "INSERT INTO routes (route_name, source, destination) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, route.getRouteName());
            statement.setString(2, route.getSource());
            statement.setString(3, route.getDestination());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateRoute(Route route) throws SQLException {
        String sql = "UPDATE routes SET route_name = ?, source = ?, destination = ? WHERE route_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, route.getRouteName());
            statement.setString(2, route.getSource());
            statement.setString(3, route.getDestination());
            statement.setInt(4, route.getRouteId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteRoute(int routeId) throws SQLException {
        String sql = "DELETE FROM routes WHERE route_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, routeId);
            return statement.executeUpdate() > 0;
        }
    }

    public Route getRouteById(int routeId) throws SQLException {
        String sql = "SELECT * FROM routes WHERE route_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, routeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return readRoute(resultSet);
                }
            }
        }

        return null;
    }

    public List<Route> getAllRoutes() throws SQLException {
        List<Route> routes = new ArrayList<Route>();
        String sql = "SELECT * FROM routes ORDER BY route_name";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                routes.add(readRoute(resultSet));
            }
        }

        return routes;
    }

    private Route readRoute(ResultSet resultSet) throws SQLException {
        Route route = new Route();
        route.setRouteId(resultSet.getInt("route_id"));
        route.setRouteName(resultSet.getString("route_name"));
        route.setSource(resultSet.getString("source"));
        route.setDestination(resultSet.getString("destination"));
        return route;
    }
}
