package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import org.alljoyn.triumph.util.loaders.ViewLoader;

public class MainBorderView extends BorderPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Pane mBottomPane;

    @FXML
    private Pane mCenterPane;

    @FXML
    private Pane mTopPane;
    
    public MainBorderView() {
        ViewLoader.loadView("MainView3.fxml", this);
        
        setRectangle(mBottomPane, Color.RED);
        setRectangle(mCenterPane, Color.GREEN);
        setRectangle(mTopPane, Color.BLUE);
    }

    public void setTopPane(Pane arg) {
        mTopPane.getChildren().clear();
        arg.prefWidthProperty().bind(mTopPane.widthProperty());
        mTopPane.getChildren().add(arg);
    }
    
    public void setCenterPane(Pane arg) {
        mCenterPane.getChildren().clear();
        setBindings(arg, mCenterPane);
        mCenterPane.getChildren().add(arg);
    }
    
    public void setBottomPane(Pane arg) {
        mBottomPane.getChildren().clear();
        setBindings(arg, mBottomPane);
        mBottomPane.getChildren().add(arg);
    }
    
    private static void setBindings(Pane arg, Pane container) {
        arg.prefWidthProperty().bind(container.prefWidthProperty());
        arg.prefHeightProperty().bind(container.prefHeightProperty());
        arg.minWidthProperty().bind(container.minWidthProperty());
        arg.minHeightProperty().bind(container.minHeightProperty());
        arg.maxWidthProperty().bind(container.maxWidthProperty());
        arg.maxHeightProperty().bind(container.maxHeightProperty());
    }
    
    private void setRectangle(Pane container, Paint fill) {
        Rectangle rect = new Rectangle(100, 100);
        rect.widthProperty().bind(container.widthProperty());
        rect.heightProperty().bind(container.heightProperty());
        rect.setFill(fill);
        container.getChildren().add(rect);
    }
}
