package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import org.alljoyn.triumph.util.loaders.ViewLoader;

/**
 * This is a very general Generic border view.
 * <br>
 * 
 * @author Michael Hotan, mhotan@quicinc.com
 */
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

        // The end point views
        mTopPane.managedProperty().bind(mTopPane.visibleProperty());
        mCenterPane.managedProperty().bind(mCenterPane.visibleProperty());
        mBottomPane.managedProperty().bind(mBottomPane.visibleProperty());
    }

    /**
     * Sets the top pane
     * 
     * @param inner Inner pane to place inside.
     */
    public void setTopPane(Pane inner) {
        setPane(inner, mTopPane);
    }

    /**
     * Sets the Center Pane
     * 
     * @param inner Inner plane to place in the center.
     */
    public void setCenterPane(Pane inner) {
        setPane(inner, mCenterPane);
    }

    /**
     * Sets Bottom pane
     * 
     * @param inner Inner pane to place at the bottom
     */
    public void setBottomPane(Pane inner) {
        setPane(inner, mBottomPane);
    }
    
    /**
     * Places inner pane exclusively inside the container
     * ensuring the inner pane consumes the entire space of the containing pane.
     * 
     * @param innerPane Inner pane to place in container
     * @param container Container to place inner pane inside
     */
    private static void setPane(Pane innerPane, Pane container) {
        ObservableList<Node> children = container.getChildren();
        if (children.contains(innerPane)) return;
        children.clear();
        if (innerPane != null) {
            setBindings(innerPane, container);
            container.getChildren().add(innerPane);
        }
        container.setVisible(innerPane != null);
    }

    private static void setBindings(Pane arg, Pane container) {
        arg.prefWidthProperty().bind(container.widthProperty());
        arg.prefHeightProperty().bind(container.heightProperty());
    }
}
