package org.alljoyn.triumph.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.alljoyn.triumph.view.MainBorderView;
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
        MainBorderView view = new MainBorderView();
        view.setTopPane(new ServicesView());
        stage.setScene(new Scene(view));
        stage.show();
    }

    
}
