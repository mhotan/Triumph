package org.alljoyn.triumph.view.propview;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import org.alljoyn.triumph.model.components.Interface;
import org.alljoyn.triumph.model.components.AJObject;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.util.loaders.ViewLoader;

/**
 * Title view for the property.
 */
public class PropertyTitleView extends BorderPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label mAnnotationTitleLabel;

    @FXML
    private ListView<?> mAnnotationsList;

    @FXML
    private VBox mDetailLabelPane;

    @FXML
    private Label mInterfaceLabel;

    @FXML
    private VBox mMainLabelPane;

    @FXML
    private Label mMemberName;

    @FXML
    private Label mObjectPathLabel;

    @FXML
    private Label mWellKnownNameLabel;

    /**
     * Title View for the property to show.
     * 
     * @param prop The Property to show. 
     */
    public PropertyTitleView(Property prop) {
        ViewLoader.loadView("MemberTitleView.fxml", this);

        // Bind layout properties to their visibility properties
        mAnnotationTitleLabel.managedProperty().bind(mAnnotationTitleLabel.visibleProperty());
        mAnnotationsList.managedProperty().bind(mAnnotationsList.visibleProperty());
        mAnnotationsList.setVisible(false);
        
        // Set the annotation label to the read write access.
        StringBuffer rwAccess = new StringBuffer();
        if (prop.hasReadAccess()) {
            rwAccess.append("Read");
        }
        if (prop.hasWriteAccess()) {
            if (rwAccess.length() > 0) {
                rwAccess.append(" & ");
            }
            rwAccess.append("Write");
        }
        if (rwAccess.length() > 0)
            mAnnotationTitleLabel.setText(rwAccess.toString());
        else 
            mAnnotationTitleLabel.setVisible(false);
        
        // Set the name of the property
        mMemberName.setText(prop.getName());
    
        Interface iface = prop.getInterface();
        AJObject object = iface.getObject();
        EndPoint service = object.getOwner();
        
        mWellKnownNameLabel.setText(service.getName());
        mInterfaceLabel.setText(iface.getName());
        mObjectPathLabel.setText(object.getName());
    }

    @FXML
    void initialize() {
        assert mAnnotationTitleLabel != null : "fx:id=\"mAnnotationTitleLabel\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mAnnotationsList != null : "fx:id=\"mAnnotationsList\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mDetailLabelPane != null : "fx:id=\"mDetailLabelPane\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mInterfaceLabel != null : "fx:id=\"mInterfaceLabel\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mMainLabelPane != null : "fx:id=\"mMainLabelPane\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mMemberName != null : "fx:id=\"mMemberName\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mObjectPathLabel != null : "fx:id=\"mObjectPathLabel\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mWellKnownNameLabel != null : "fx:id=\"mWellKnownNameLabel\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
    }

}
