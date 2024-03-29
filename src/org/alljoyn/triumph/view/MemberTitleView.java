/******************************************************************************
 * Copyright 2013, Qualcomm Innovation Center, Inc.
 *
 *    All rights reserved.
 *    This file is licensed under the 3-clause BSD license in the NOTICE.txt
 *    file for this project. A copy of the 3-clause BSD license is found at:
 *
 *        http://opensource.org/licenses/BSD-3-Clause.
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the license is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the license for the specific language governing permissions and
 *    limitations under the license.
 ******************************************************************************/

package org.alljoyn.triumph.view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import org.alljoyn.triumph.model.components.AJObject;
import org.alljoyn.triumph.model.components.Annotation;
import org.alljoyn.triumph.model.components.EndPoint;
import org.alljoyn.triumph.model.components.Interface;
import org.alljoyn.triumph.model.components.Member;
import org.alljoyn.triumph.util.loaders.ViewLoader;

/**
 * Creates title view for this Member to be invoked.
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class MemberTitleView extends VBox {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label mAnnotationTitleLabel;

    @FXML
    private ListView<Annotation> mAnnotationsList;

    @FXML
    private Label mInterfaceLabel;

    @FXML
    private Label mMemberName;

    @FXML
    private Label mObjectPathLabel;

    @FXML
    private Label mWellKnownNameLabel;

    public MemberTitleView(Member member) {
        // Load the associated 
        ViewLoader.loadView(this);

        mMemberName.setText(member.getName());

        // Preset the object path and the well known name
        mObjectPathLabel.setText("Unknown Object");
        
        mWellKnownNameLabel.setText("Unknown Advertised Name");

        // Add annotations if they exists
        // Other wise remove annotations.
        List<Annotation> annotations = member.getAnnotations();
        if (annotations.isEmpty()) {
            getChildren().remove(mAnnotationTitleLabel);
            getChildren().remove(mAnnotationsList);
        } else {
            ObservableList<Annotation> anns = FXCollections.observableArrayList(annotations);
            mAnnotationsList.setItems(anns);
        }

        Interface iface = member.getInterface();
        mInterfaceLabel.setText(iface.getName());

        AJObject object = iface.getObject();
        if (object == null) 
            return;

        // set the object path name
        mObjectPathLabel.setText(object.getName());

        EndPoint service = object.getOwner();
        if (service == null)
            return;

        mWellKnownNameLabel.setText(service.getName());
    }

    // Check for the ability 
    @FXML
    void initialize() {
        assert mAnnotationTitleLabel != null : "fx:id=\"mAnnotationTitleLabel\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mAnnotationsList != null : "fx:id=\"mAnnotationsLabel\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mInterfaceLabel != null : "fx:id=\"mInterfaceLabel\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mMemberName != null : "fx:id=\"mMemberName\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mObjectPathLabel != null : "fx:id=\"mObjectPathLabel\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
        assert mWellKnownNameLabel != null : "fx:id=\"mWellKnownNameLabel\" was not injected: check your FXML file 'MemberTitleView.fxml'.";
    }


}
