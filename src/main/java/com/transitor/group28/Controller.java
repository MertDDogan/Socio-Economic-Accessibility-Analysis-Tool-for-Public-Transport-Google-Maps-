package com.transitor.group28;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapLabelEvent;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private static final Coordinate coordMaastrichtCenter = new Coordinate(50.85193578549532, 5.694435130814521);
    private static final int ZOOM_DEFAULT = 14;
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("\\d{4}[A-Z]{2}");

    @FXML
    private MapView mapView;

    @FXML
    private TextField postalCodeTextField;

    @FXML
    private TextField secondPostalCodeTextField;

    @FXML
    private Button buttonZoom;

    @FXML
    private Button buttonCenterMap;

    @FXML
    private Slider sliderZoom;

    @FXML
    private Label labelCenter;

    @FXML
    private Label labelExtent;

    @FXML
    private Label labelZoom;

    @FXML
    private Label labelEvent;

    @FXML
    private Label travelTimeLabel; // Link this to your FXML

    private final Map<String, PostalCode> postalCodeMap = new HashMap<>();
    private Marker firstMarker;
    private Marker secondMarker;
    private CoordinateLine busLine;
    private ShapeCoordinates shapeCoordinates;
    private Selector selector = new Selector();

    public void initMapAndControls(Projection projection) {
        logger.trace("Initializing map and controls");

        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                mapView.setZoom(ZOOM_DEFAULT);
                mapView.setCenter(coordMaastrichtCenter);
                loadPostalCodes();
                afterMapIsInitialized();
            }
        });

        initializeControls();
        setupEventHandlers();

        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(false)
                .build());
        logger.debug("Map initialization finished");
    }

    @FXML
    private void handleDisplayFirstPostalCode() {
        displayPostalCode(postalCodeTextField.getText().toUpperCase().trim(), true);
    }

    @FXML
    private void handleDisplaySecondPostalCode() {
        displayPostalCode(secondPostalCodeTextField.getText().toUpperCase().trim(), false);
    }

    private void displayPostalCode(String postalCode, boolean isFirst) {
        if (!isValidPostalCode(postalCode)) {
            showAlert("Invalid Postal Code", "The entered postal code " + postalCode + " is not in the valid Dutch format (e.g., 1234AB).");
            return;
        }

        logger.debug("Entered postal code: {}", postalCode);

        //PostalCode postalCodeData = postalCodeMap.get(postalCode);

        PostalCodeRetriever postalCodeRetriever = new PostalCodeRetriever();
        PostalCode postalCodeData = postalCodeRetriever.getDataZipCode(postalCode);

        if (postalCodeData != null) {
            Coordinate coordinate = new Coordinate(postalCodeData.getLat(), postalCodeData.getLon());
            Marker marker = Marker.createProvided(isFirst ? Marker.Provided.RED : Marker.Provided.BLUE)
                    .setPosition(coordinate)
                    .setVisible(true);

            if (!isFirst) {
                marker.setCssClass("dark-blue-marker");
            }

            if (isFirst) {
                if (firstMarker != null) {
                    mapView.removeMarker(firstMarker);
                }
                firstMarker = marker;
            } else {
                if (secondMarker != null) {
                    mapView.removeMarker(secondMarker);
                }
                secondMarker = marker;
            }

            mapView.addMarker(marker);
            mapView.setCenter(coordinate);
        } else {
            logger.debug("Postal code not found: {}", postalCode);
            showAlert("Postal Code Not Found", "The entered postal code " + postalCode + " does not exist in the data.");
        }
    }

    private void loadPostalCodes() {
        String csvFile = "/MassZipLatlon.csv";
        String csvSplitBy = ",";

        try (InputStream is = getClass().getResourceAsStream(csvFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            if (is == null) {
                logger.error("CSV file not found: {}", csvFile);
                showAlert("Error", "CSV file not found: " + csvFile);
                return;
            }
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
                if (data.length >= 3) {
                    String code = data[0];
                    double latitude = Double.parseDouble(data[1]);
                    double longitude = Double.parseDouble(data[2]);
                    postalCodeMap.put(code, new PostalCode(latitude, longitude));
                    logger.debug("Loaded postal code: {} with coordinates: {}, {}", code, latitude, longitude);
                }
            }
            logger.debug("Postal codes loaded successfully.");
        } catch (IOException e) {
            logger.error("Failed to load postal codes from the CSV file.", e);
            showAlert("Error", "Failed to load postal codes from the CSV file.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidPostalCode(String postalCode) {
        return POSTAL_CODE_PATTERN.matcher(postalCode).matches();
    }

    private void setupEventHandlers() {
        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume();
            final Coordinate newPosition = event.getCoordinate().normalize();
            labelEvent.setText("Event: map clicked at: " + newPosition);
        });

        mapView.addEventHandler(MapViewEvent.MAP_EXTENT, event -> {
            event.consume();
            mapView.setExtent(event.getExtent());
        });

        mapView.addEventHandler(MapViewEvent.MAP_BOUNDING_EXTENT, event -> {
            event.consume();
            labelExtent.setText(event.getExtent().toString());
        });

        mapView.addEventHandler(MapViewEvent.MAP_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: map right clicked at: " + event.getCoordinate());
        });

        mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: marker clicked: " + event.getMarker().getId());
        });

        mapView.addEventHandler(MarkerEvent.MARKER_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: marker right clicked: " + event.getMarker().getId());
        });

        mapView.addEventHandler(MapLabelEvent.MAPLABEL_CLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: label clicked: " + event.getMapLabel().getText());
        });

        mapView.addEventHandler(MapLabelEvent.MAPLABEL_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: label right clicked: " + event.getMapLabel().getText());
        });

        mapView.addEventHandler(MapViewEvent.MAP_POINTER_MOVED, event -> {
            logger.debug("Pointer moved to {}", event.getCoordinate());
        });

        logger.trace("Map event handlers initialized");
    }

    private void initializeControls() {
        if (buttonCenterMap != null) {
            buttonCenterMap.setOnAction(event -> mapView.setCenter(coordMaastrichtCenter));
        }
        if (buttonZoom != null) {
            buttonZoom.setOnAction(event -> mapView.setZoom(ZOOM_DEFAULT));
        }
        if (sliderZoom != null) {
            sliderZoom.valueProperty().bindBidirectional(mapView.zoomProperty());
        }
        if (labelCenter != null) {
            labelCenter.textProperty().bind(Bindings.format("center: %s", mapView.centerProperty()));
        }
        if (labelZoom != null) {
            labelZoom.textProperty().bind(Bindings.format("zoom: %.0f", mapView.zoomProperty()));
        }
    }

    @FXML
    private void handleZoomIn() {
        mapView.setZoom(mapView.getZoom() + 1);
    }

    @FXML
    private void handleZoomOut() {
        mapView.setZoom(mapView.getZoom() - 1);
    }

    @FXML
    private void handleResetView() {
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(coordMaastrichtCenter);
    }

    @FXML
    private void handleClear() {
        if (firstMarker != null) {
            mapView.removeMarker(firstMarker);
            firstMarker = null;
        }
        if (secondMarker != null) {
            mapView.removeMarker(secondMarker);
            secondMarker = null;
        }

        postalCodeTextField.clear();
        secondPostalCodeTextField.clear();

        if (busLine != null) {
            mapView.removeCoordinateLine(busLine);
            busLine = null;
        }

        if (labelCenter != null) {
            labelCenter.textProperty().unbind();
            labelCenter.setText("");
        }
        if (labelExtent != null) {
            labelExtent.textProperty().unbind();
            labelExtent.setText("");
        }
        if (labelZoom != null) {
            labelZoom.textProperty().unbind();
            labelZoom.setText("");
        }
        if (labelEvent != null) {
            labelEvent.setText("");
        }
        if (travelTimeLabel != null) {
            travelTimeLabel.setText("");
        }

        logger.debug("Map and text fields cleared");
    }

    private void afterMapIsInitialized() {
        logger.trace("Map initialized");
        logger.debug("Setting center and enabling controls...");
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(coordMaastrichtCenter);
    }

    @FXML
    private void handleFindRoute() {
        try {
            String postalCode1 = postalCodeTextField.getText().toUpperCase().trim();
            String postalCode2 = secondPostalCodeTextField.getText().toUpperCase().trim();

            if (postalCode1.isEmpty() || postalCode2.isEmpty()) {
                showAlert("Input Error", "Please enter both postal codes.");
                return;
            }

            if (!isValidPostalCode(postalCode1)) {
                showAlert("Invalid Postal Code", "The entered postal code " + postalCode1 + " is not in the valid Dutch format (e.g., 1234AB).");
                return;
            }
            if (!isValidPostalCode(postalCode2)) {
                showAlert("Invalid Postal Code", "The entered postal code " + postalCode2 + " is not in the valid Dutch format (e.g., 1234AB).");
                return;
            }

            PostalCodeRetriever postalCodeRetriever = new PostalCodeRetriever();

            PostalCode source = postalCodeRetriever.getDataZipCode(postalCode1);
            PostalCode destination = postalCodeRetriever.getDataZipCode(postalCode2);


            PathTime bestPathTime = selector.findBestPathTime(source, destination, 1000);

            if (bestPathTime == null) {
                showAlert("Route Not Found", "No route found between the specified postal codes.");
                return;
            }

            System.out.println("Source stop lat: " + bestPathTime.getStopA().getLat());
            System.out.println("Source stop lon: " + bestPathTime.getStopA().getLon());
            System.out.println("Dest stop lat: " + bestPathTime.getStopB().getLat());
            System.out.println("Dest stop lon: " + bestPathTime.getStopB().getLat());

            busLine = shapeCoordinates.getCoordinatesFromShapes(bestPathTime)
                    .setColor(Color.MAGENTA)
                    .setWidth(7);

            if (busLine != null) {
                mapView.removeCoordinateLine(busLine);
            }

            mapView.addCoordinateLine(busLine);
            busLine.setVisible(true);

            int travelTime = (int) bestPathTime.getTotalTime();
            travelTimeLabel.setText("Travel Time: " + travelTime + " minutes");

        } catch (SQLException e) {
            logger.error("SQL Exception while finding route", e);
            showAlert("Database Error", "An error occurred while accessing the database.");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            showAlert("Unexpected Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    public void setShapeCoordinates(ShapeCoordinates shapeCoordinates){
        this.shapeCoordinates = shapeCoordinates;
    }
}





