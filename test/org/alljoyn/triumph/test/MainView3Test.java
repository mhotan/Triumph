package org.alljoyn.triumph.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.alljoyn.triumph.view.ServicesView;

public class MainView3Test extends Application {

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane bPane = new BorderPane();
        bPane.setTop(new ServicesView());
        stage.setScene(new Scene(bPane));
        stage.show();
    }

    
}
