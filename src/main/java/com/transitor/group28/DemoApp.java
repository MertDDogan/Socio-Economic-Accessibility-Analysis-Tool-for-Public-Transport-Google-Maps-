
package com.transitor.group28;

import com.sothawo.mapjfx.Projection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DemoApp extends Application {

    /** Logger for the class */
    private static final Logger logger = LoggerFactory.getLogger(DemoApp.class);

    public static void main(String[] args) {
        logger.trace("begin main");
        launch(args);
        logger.trace("end main");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("starting DemoApp");
        String fxmlFile = "/fxml/DemoApp.fxml";
        logger.debug("loading fxml file {}", fxmlFile);
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent rootNode = fxmlLoader.load(getClass().getResourceAsStream(fxmlFile));
        logger.trace("stage loaded");

        final Controller controller = fxmlLoader.getController();
        final Projection projection = getParameters().getUnnamed().contains("wgs84")
                ? Projection.WGS_84 : Projection.WEB_MERCATOR;
        controller.initMapAndControls(projection);
        controller.setShapeCoordinates(new ShapeCoordinates());

        Scene scene = new Scene(rootNode);
        logger.trace("scene created");

        primaryStage.setTitle("Transitor Group 28");
        primaryStage.setScene(scene);
        logger.trace("showing scene");
        primaryStage.show();

        logger.debug("application start method finished.");
    }
}
