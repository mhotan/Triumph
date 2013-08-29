package org.alljoyn.triumph.view;

import javafx.scene.layout.BorderPane;

import org.alljoyn.triumph.model.components.arguments.Argument;
import org.alljoyn.triumph.util.loaders.ViewLoader;

public class LoadableArgumentView extends BorderPane {

    public LoadableArgumentView(Argument<?> arg) {
        ViewLoader.loadView("LoadableArgumentView.fxml", this);
        
        
    }
    
}
