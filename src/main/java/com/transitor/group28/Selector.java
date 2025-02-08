package com.transitor.group28;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Selector {

    // Method to find the best path time
    public PathTime findBestPathTime(PostalCode source, PostalCode destination, double searchRadius) throws SQLException {
        List<PathTime> pathTimes = getPossibleRoutes(source, destination, searchRadius);

        if (pathTimes == null || pathTimes.isEmpty()) {
            return null; // Or throw an IllegalArgumentException("PathTimes list cannot be null or empty");
        }

        PathTime bestPathTime = pathTimes.get(0);
        for (PathTime pathTime : pathTimes) {
            if (pathTime.getTotalTime() < bestPathTime.getTotalTime()) {
                bestPathTime = pathTime;
            }
        }

        return bestPathTime;
    }

    // Method to get possible routes
    public List<PathTime> getPossibleRoutes(PostalCode source, PostalCode destination, double searchRadius) throws SQLException {
        List<PathTime> paths = new ArrayList<>();
        DatabaseConnection database = DatabaseConnection.getInstance();
        double sourceLat = source.getLat();
        double sourceLon = source.getLon();
        double destinationLat = destination.getLat();
        double destinationLon = destination.getLon();

        try (Connection connection = database.getConnection()) {
            String query = "WITH SourceClosestStops AS (\n" +
                    "    SELECT\n" +
                    "        stop_id AS source_stop_id,\n" +
                    "        trip_id,\n" +
                    "        lat AS source_lat,\n" +
                    "        lon AS source_lon,\n" +
                    "        stop_time AS source_stop_time,\n" +
                    "        (6371000 * acos(cos(radians(?)) * cos(radians(lat)) * cos(radians(lon) - radians(?)) + sin(radians(?)) * sin(radians(lat)))) AS source_distance\n" +
                    "    FROM\n" +
                    "        maastricht_bus_stop\n" +
                    "    HAVING\n" +
                    "        source_distance <= ?\n" +
                    "    ORDER BY\n" +
                    "        source_distance\n" +
                    "),\n" +
                    "DestinationClosestStops AS (\n" +
                    "    SELECT\n" +
                    "        stop_id AS destination_stop_id,\n" +
                    "        trip_id,\n" +
                    "        lat AS destination_lat,\n" +
                    "        lon AS destination_lon,\n" +
                    "        stop_time AS destination_stop_time,\n" +
                    "        (6371000 * acos(cos(radians(?)) * cos(radians(lat)) * cos(radians(lon) - radians(?)) + sin(radians(?)) * sin(radians(lat)))) AS destination_distance\n" +
                    "    FROM\n" +
                    "        maastricht_bus_stop\n" +
                    "    HAVING\n" +
                    "        destination_distance <= ?\n" +
                    "    ORDER BY\n" +
                    "        destination_distance\n" +
                    ")\n" +
                    "SELECT\n" +
                    "    s.trip_id AS common_trip_id,\n" +
                    "    s.source_stop_id,\n" +
                    "    s.source_lat,\n" +
                    "    s.source_lon,\n" +
                    "    s.source_stop_time,\n" +
                    "    d.destination_stop_id,\n" +
                    "    d.destination_lat,\n" +
                    "    d.destination_lon,\n" +
                    "    d.destination_stop_time\n" +
                    "FROM\n" +
                    "    SourceClosestStops s\n" +
                    "INNER JOIN\n" +
                    "    DestinationClosestStops d\n" +
                    "ON\n" +
                    "    s.trip_id = d.trip_id\n" +
                    "WHERE\n" +
                    "    s.source_stop_id != d.destination_stop_id;";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setDouble(1, sourceLat);
                statement.setDouble(2, sourceLon);
                statement.setDouble(3, sourceLat);
                statement.setDouble(4, searchRadius);
                statement.setDouble(5, destinationLat);
                statement.setDouble(6, destinationLon);
                statement.setDouble(7, destinationLat);
                statement.setDouble(8, searchRadius);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String sourceStopId = resultSet.getString("source_stop_id");
                        double sourceLatA = resultSet.getDouble("source_lat");
                        double sourceLonA = resultSet.getDouble("source_lon");
                        Time sourceStopTime = resultSet.getTime("source_stop_time");

                        String destinationStopId = resultSet.getString("destination_stop_id");
                        double destinationLatB = resultSet.getDouble("destination_lat");
                        double destinationLonB = resultSet.getDouble("destination_lon");
                        Time destinationStopTime = resultSet.getTime("destination_stop_time");

                        BusStop sourceStop = new BusStop(sourceStopId, sourceLatA, sourceLonA, sourceStopTime);
                        BusStop destinationStop = new BusStop(destinationStopId, destinationLatB, destinationLonB, destinationStopTime);

                        paths.add(new PathTime(source, destination, sourceStop, destinationStop));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to database: " + e.getMessage());
        }

        return paths;
    }
}
