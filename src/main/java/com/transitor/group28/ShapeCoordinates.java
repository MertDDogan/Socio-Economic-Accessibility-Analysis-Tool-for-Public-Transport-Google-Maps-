package com.transitor.group28;

import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.CoordinateLine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class ShapeCoordinates {
    double stopALat;
    double stopALon;
    double stopBLat;
    double stopBLon;

    private DatabaseConnection db = DatabaseConnection.getInstance();

    public ShapeCoordinates() throws SQLException {
    }

    public CoordinateLine getCoordinatesFromShapes(PathTime bestPathTime) throws SQLException {
        stopALat = bestPathTime.getStopA().getLat();
        stopALon = bestPathTime.getStopA().getLon();
        stopBLat = bestPathTime.getStopB().getLat();
        stopBLon = bestPathTime.getStopB().getLon();

        // SQL query to execute all necessary steps in one go
        String query = "WITH SourceShape AS (\n" +
                "    SELECT DISTINCT shape_id\n" +
                "    FROM shapes_maastricht_master\n" +
                "    WHERE TRUNCATE(shape_pt_lat, 3) = TRUNCATE(?, 3)\n" +
                "      AND TRUNCATE(shape_pt_lon, 3) = TRUNCATE(?, 3)\n" +
                "),\n" +
                "DestShape AS (\n" +
                "    SELECT DISTINCT shape_id\n" +
                "    FROM shapes_maastricht_master\n" +
                "    WHERE TRUNCATE(shape_pt_lat, 3) = TRUNCATE(?, 3)\n" +
                "      AND TRUNCATE(shape_pt_lon, 3) = TRUNCATE(?, 3)\n" +
                "),\n" +
                "CommonShapes AS (\n" +
                "    SELECT DISTINCT s.shape_id\n" +
                "    FROM SourceShape s\n" +
                "    INNER JOIN DestShape d ON s.shape_id = d.shape_id\n" +
                "    LIMIT 1\n" +
                "),\n" +
                "ShapeCoordinates AS (\n" +
                "    SELECT sc.shape_id, sc.shape_pt_lat_t, sc.shape_pt_lon_t, sc.shape_pt_sequence\n" +
                "    FROM shapes_maastricht_master sc\n" +
                "    INNER JOIN CommonShapes cs ON sc.shape_id = cs.shape_id\n" +
                ")\n" +
                "SELECT DISTINCT sc.shape_pt_lat_t, sc.shape_pt_lon_t, sc.shape_pt_sequence\n" +
                "FROM ShapeCoordinates sc\n" +
                "JOIN (\n" +
                "    SELECT MIN(sc.shape_pt_sequence) AS min_sequence, MAX(sc.shape_pt_sequence) AS max_sequence\n" +
                "    FROM ShapeCoordinates sc\n" +
                "    JOIN SourceShape ss ON sc.shape_id = ss.shape_id\n" +
                "    JOIN DestShape ds ON sc.shape_id = ds.shape_id\n" +
                "    GROUP BY sc.shape_id\n" +
                ") seq ON sc.shape_pt_sequence > seq.min_sequence AND sc.shape_pt_sequence < seq.max_sequence\n" +
                "ORDER BY sc.shape_pt_sequence;";

        // Create the prepared statement and set the parameters
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, stopALat);
            statement.setDouble(2, stopALon);
            statement.setDouble(3, stopBLat);
            statement.setDouble(4, stopBLon);

            // Execute the query and return the ResultSet
            ResultSet resultSet = statement.executeQuery();
            //System.out.println("Result set size: "+getResultSetSize(resultSet));
            //exit(0);


            return loadCoordinateLine(resultSet);
        }
    }

    private CoordinateLine loadCoordinateLine(ResultSet resultSet) {
        List<ShapeCoordinate> unfilteredCoordinates = new ArrayList<>();
        List<Coordinate> filteredCoordinates = new ArrayList<>();
        try {
            //* Write data from ResultSet
            while (resultSet.next()) {
                try {
                    double latitude = resultSet.getDouble("shape_pt_lat_t");
                    double longitude = resultSet.getDouble("shape_pt_lon_t");
                    int sequenceNumber = resultSet.getInt("shape_pt_sequence");
                    unfilteredCoordinates.add(new ShapeCoordinate(new Coordinate(latitude, longitude),sequenceNumber ));
                } catch (SQLException e){
                    throw new IllegalArgumentException("Error loading the data on the coordinate line", e);
                }
            }

            // find closest shape to starting coordinate
            Coordinate startingCoordinate = new Coordinate(stopALat, stopALon);
            Coordinate endingCoordinate = new Coordinate(stopBLat, stopBLon);

            int startShapeSeq = findClosestCoordinate(startingCoordinate, unfilteredCoordinates).sequence();
            int endShapeSep = findClosestCoordinate(endingCoordinate, unfilteredCoordinates).sequence();

            for(ShapeCoordinate shapeCoordinate: unfilteredCoordinates){
                int sequence = shapeCoordinate.sequence();
                if(startShapeSeq < endShapeSep){
                    if(sequence >= startShapeSeq && sequence <= endShapeSep){
                        filteredCoordinates.add(shapeCoordinate.coordinate());
                    }
                } else {
                    if(sequence <= startShapeSeq && sequence >= endShapeSep){
                        filteredCoordinates.add(shapeCoordinate.coordinate());
                    }
                }

            }

            return new CoordinateLine(filteredCoordinates);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to make coordinate line:", e);
        }
    }

    ShapeCoordinate findClosestCoordinate(Coordinate target, List<ShapeCoordinate> ShapeCoordinates) {
        if (ShapeCoordinates == null || ShapeCoordinates.isEmpty()) {
            throw new IllegalArgumentException("Coordinate list is null or empty");
        }

        ShapeCoordinate closestCoordinate = null;
        double minDistance = Double.MAX_VALUE;

        for (ShapeCoordinate coordinate : ShapeCoordinates) {
            if(coordinate.sequence() == 472) {
                System.out.println("aaaaaaaaaaaaaaaaaaa");
            }

            double distance = distanceTo(target, coordinate.coordinate());
            if (distance < minDistance) {
                minDistance = distance;
                closestCoordinate = coordinate;
            }

        }

        return closestCoordinate;
    }

    public double distanceTo(Coordinate coordinates1, Coordinate coordinates2) {
        final double R = 6371.0;

        double latDistance = Math
                .toRadians(coordinates2.getLatitude() - coordinates1.getLatitude());
        double lonDistance = Math
                .toRadians(coordinates2.getLongitude() - coordinates1.getLongitude());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(coordinates1.getLatitude()))
                * Math.cos(Math.toRadians(coordinates2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }


}
