package com.transitor.group28;

import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.CoordinateLine;
import javafx.geometry.Pos;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShapeCoordinatesTest {

    @Test
    void testCoordinates6211BA_6224AB() throws SQLException {
        PostalCode source = new PostalCode(50.85466667, 5.692548967); //6211BA
        PostalCode destination = new PostalCode(50.85339012, 5.70921457); //6224AB
        Selector selector = new Selector();
        PathTime pathTime = selector.findBestPathTime(source, destination, 1000);

        ShapeCoordinates shapeCoordinates = new ShapeCoordinates();
        CoordinateLine coordinateLine = shapeCoordinates.getCoordinatesFromShapes(pathTime);

        assertNotNull(coordinateLine);
    }

    @Test
    void testCoordinates6211BA_6212AB() throws SQLException {
        PostalCode source = new PostalCode(50.85466667, 5.692548967); //6211BA
        PostalCode destination = new PostalCode(50.84157854,5.689361853); //6224AB
        Selector selector = new Selector();
        PathTime pathTime = selector.findBestPathTime(source, destination, 1000);

        ShapeCoordinates shapeCoordinates = new ShapeCoordinates();
        CoordinateLine coordinateLine = shapeCoordinates.getCoordinatesFromShapes(pathTime);

        assertNotNull(coordinateLine);
    }

    @Test
    void testCoordinates6218HP_6226BN() throws SQLException {
        PostalCode source = new PostalCode(50.86632617,5.666071843); //6218HP
        PostalCode destination = new PostalCode(50.84833288,5.72464426); //6226BN
        Selector selector = new Selector();
        PathTime pathTime = selector.findBestPathTime(source, destination, 1000);

        ShapeCoordinates shapeCoordinates = new ShapeCoordinates();
        CoordinateLine coordinateLine = shapeCoordinates.getCoordinatesFromShapes(pathTime);

        assertNotNull(coordinateLine);
    }
    @Test
    public void testDistanceTo() throws SQLException {
        Coordinate coord1 = new Coordinate(50.0, 5.0);
        Coordinate coord2 = new Coordinate(51.0, 6.0);
        ShapeCoordinates shapeCoordinates = new ShapeCoordinates();
        double distance = shapeCoordinates.distanceTo(coord1, coord2);
        assertEquals(131.0, distance, 1.0); // Approximate distance in kilometers
    }

    @Test
    public void testFindClosestCoordinate() throws SQLException {
        List<ShapeCoordinate> shapeCoordinatesList = new ArrayList<>();
        shapeCoordinatesList.add(new ShapeCoordinate(new Coordinate(50.1, 5.1), 1));
        shapeCoordinatesList.add(new ShapeCoordinate(new Coordinate(51.0, 6.0), 2));

        ShapeCoordinates shapeCoordinates = new ShapeCoordinates();
        ShapeCoordinate closest = shapeCoordinates.findClosestCoordinate(new Coordinate(50.0, 5.0), shapeCoordinatesList);
        assertEquals(1, closest.sequence());
    }



}