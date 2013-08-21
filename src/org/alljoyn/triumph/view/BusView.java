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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.alljoyn.triumph.MainApplication;
import org.alljoyn.triumph.controller.AllJoynComponentTreeClickHandler;
import org.alljoyn.triumph.controller.AllJoynComponentTreeClickHandler.OnClickListener;
import org.alljoyn.triumph.model.components.AllJoynComponent;
import org.alljoyn.triumph.model.components.AllJoynService;
import org.alljoyn.triumph.model.components.Member;
import org.alljoyn.triumph.model.components.Method;
import org.alljoyn.triumph.model.components.Property;
import org.alljoyn.triumph.model.components.Signal;
import org.alljoyn.triumph.util.loaders.ViewLoader;
import org.alljoyn.triumph.view.propview.PropertyView;

/**
 * A view that presents all the service defined by 
 * 
 * @author mhotan@quicinc.com, Michael Hotan
 */
public class BusView extends BorderPane implements OnClickListener {

    /*
     * Representation invariant
     * 
     * For an AllJoynService name s
     * If the service is known to 
     */

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label mError;

    @FXML
    private ScrollPane mScrollPane;

    @FXML
    private TextField mFilterInput;

    @FXML
    private TreeView<AllJoynComponent> mServiceTree;

    @FXML
    private VBox mServicesPane;

    @FXML
    private Label mTitle;

    /**
     * A cache to hold the state of old views.  
     */
    private final Map<Member, MemberView> mMemberViewCache;
    private final Map<Property, PropertyView> mPropertyViewCache;

    /**
     * This is a reference tracker that is used to check quickly for 
     * 
     */
    private final Map<String, TreeItem<AllJoynComponent>> mServiceMap;

    /**
     * This is the underlying list of TreeItems that are viewable to the user.
     * This data structure manages the redrawing of the list of services.
     */
    private final ObservableList<TreeItem<AllJoynComponent>> mShownServices;

    /**
     * Set of services that are not shown to the user.
     * Services are filtered into a sub set for a set of services that
     * match a certain Filter parameter.  This set is a collection of services 
     * that are currently not shown because they do not meet those
     * requirements.
     */
    private final Set<TreeItem<AllJoynComponent>> mUnshownServices;

    /**
     * A Comparator that helps sort the Tree item by alphabetical order.
     */
    private final Comparator<TreeItem<AllJoynComponent>> mComparator;

    private String mFilter;

    /**
     * Constructs a 
     */
    public BusView() {
        ViewLoader.loadView("BusView.fxml", this);
        initialize();

        // Map of Member and MemberViews
        mMemberViewCache = new HashMap<Member, MemberView>();
        mPropertyViewCache = new HashMap<Property, PropertyView>();
        mServiceMap = new HashMap<String, TreeItem<AllJoynComponent>>();
        mUnshownServices = new HashSet<TreeItem<AllJoynComponent>>();

        // Set the Root of the tree
        TreeItem<AllJoynComponent> root = new TreeItem<AllJoynComponent>(new org.alljoyn.triumph.model.components.Label(""));
        mServiceTree.setRoot(root);
        root.setExpanded(true); // Expand the root of the children services

        // Keep a reference to the list of all the tree items.
        mShownServices = root.getChildren();

        // Comparator for adding services.
        mComparator = new Comparator<TreeItem<AllJoynComponent>>() {

            @Override
            public int compare(TreeItem<AllJoynComponent> o1,
                    TreeItem<AllJoynComponent> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        };

        // Accept everything
        mFilter = "";
        mFilterInput.setText(mFilter);

        // Register the handler clicks on the tree.
        new AllJoynComponentTreeClickHandler(mServiceTree).addClickListener(this);

        // Handle the drawing of the Object Attributes
        mServiceTree.setCellFactory(new Callback<TreeView<AllJoynComponent>, TreeCell<AllJoynComponent>>() {

            @Override
            public TreeCell<AllJoynComponent> call(TreeView<AllJoynComponent> param) {
                return new AllJoynComponentTreeCell();
            }
        });

        hideError();
    }

    /**
     * Given the state of all the known services. Adjust the view 
     * respectively.
     * 
     * @param services a list of all the existing services
     */
    public void updateState(List<AllJoynService> services) {
        if (services == null) return;

        // Extract all the names of the service.
        List<String> currServiceNames = new ArrayList<String>(services.size());
        for (int i = 0; i < services.size(); ++i) {
            AllJoynService service = services.get(i);
            currServiceNames.add(service.getName());
        }

        // All The name of in this list must exist in this view.
        for (int i = 0; i < currServiceNames.size(); ++i) {
            String name = currServiceNames.get(i);

            // skip the name if we already have it.
            if (mServiceMap.containsKey(name)) continue;

            addService(services.get(i));
        }

        // If the current list does not have one of our names
        for (String name: mServiceMap.keySet()) {

            // If the list has the name then we need to keep the service
            if (currServiceNames.contains(name)) continue;

            // If the current list doesn't have the name we should remove the 
            // service associated with this name.
            removeService(name);
        }
    }

    /**
     * The result
     * @param result
     */
    public void showResult(Object result) {
        // TODO Auto-generated method stub

    }

    /**
     * Notifies all sub views that the a property is desired to be shown.
     * @param property Property to show.
     */
    public void showProperty(Property property) {
        // Check the cache for the property View we need
        PropertyView view = mPropertyViewCache.get(property);
        if (view == null) {
            view = new PropertyView(property);
            mPropertyViewCache.put(property, view);
        }
        showView(view);
    }

    public void showSignal(Signal signal) {
        showMember(signal);
    }

    public void showMethod(Method method) {
        showMember(method);
    }

    /**
     * Shows the member of 
     * 
     * @param toShow Member to show.
     */
    protected void showMember(Member toShow) {
        // Check if we have the Member already.
        MemberView view = mMemberViewCache.get(toShow);
        if (view == null) { // We didnt have the view already.
            view = MemberViewFactory.produceView(toShow);
            mMemberViewCache.put(toShow, view);
        }

        showView(view);
    }

    /**
     * SHows the node in the component pane of this application.
     * 
     * @param toShow View to show in the component pane
     */
    private void showView(Node toShow) {
        // Check if the member view is already present in the
        // component pane.
        ObservableList<Node> memberViews = mScrollPane.getChildrenUnmodifiable();
        if (memberViews.contains(toShow)) return;

        // If the view is different
        // remove the old member view and add the new one.
        HBox.setHgrow(toShow, Priority.ALWAYS);
        mScrollPane.setContent(toShow);
    }


    /**
     * Add the service to this view.
     * 
     * @param service Service to add to the list of known services
     */
    private void addService(AllJoynService service) {
        if (service == null) {
            MainApplication.getLogger().warning("BusView.addService Service is null");
            return;
        }

        // Do a quick check to 
        if (mServiceMap.containsKey(service.getName()))
            return;
        // Get the tree view of the service
        TreeItem<AllJoynComponent> item = service.toTree();

        // Check if passes filter requirement
        if (!isFiltered(service.getName())) {
            mUnshownServices.add(item);
            return;
        }

        // track the service in the map
        mServiceMap.put(service.getName(), item);
        // Add the service so that it shows
        mShownServices.add(item);
        // Sort the remaining services to be shown
        sort(mShownServices);
    }

    /**
     * Attempt to remove the service with the name inputted 
     * 
     * @param serviceName Name of service to remove
     * @return true if service was removed, false otherwise.
     */
    private boolean removeService(String serviceName) {
        // Do a quick check to make sure that we still have
        // the service.  If we do not then we are done
        // This prevents the unnecesary access to the list.
        if (!mServiceMap.containsKey(serviceName))
            return false;

        // 
        TreeItem<AllJoynComponent> item = mServiceMap.get(serviceName);
        mServiceMap.remove(serviceName);

        // Remove the service 
        return mShownServices.remove(item) || mUnshownServices.remove(item);
    }

    /**
     * Sorts the current list of Services in alphabetical order.
     * @param listToSort List to sort.
     */
    private void sort(List<TreeItem<AllJoynComponent>> listToSort) {
        Collections.sort(listToSort, mComparator);
    }

    @FXML
    void filterServices(ActionEvent event) {
        // called when request ed
        mFilter = mFilterInput.getText();

        List<TreeItem<AllJoynComponent>> tempList = new ArrayList<TreeItem<AllJoynComponent>>(
                mShownServices.size() + mUnshownServices.size());
        // Add all the service both show and unshown.  Then filter as we go.
        tempList.addAll(mShownServices);
        tempList.addAll(mUnshownServices);

        // Clear the old list
        mShownServices.clear();
        mUnshownServices.clear();

        sort(tempList); // Sort all the services.

        for (TreeItem<AllJoynComponent> item : tempList) {
            String name = item.getValue().getName();
            // If it is filtered show it.
            if (isFiltered(name)) {
                // If the name meets the requirement then add to view
                mShownServices.add(item);
            } else {
                // If not hide from the view and store for later.
                mUnshownServices.add(item);
            }
        }
    }

    /**
     * Checks if the service name meets filter requirements.
     * 
     * @param serviceName Service name to filter
     * @return Whether the service name is meets the filter requirement
     */
    private boolean isFiltered(String serviceName) {
        if (serviceName == null)
            return false;
        return serviceName.startsWith(mFilter);
    }

    @FXML
    void initialize() {
        assert mError != null : "fx:id=\"mError\" was not injected: check your FXML file 'BusView.fxml'.";
        assert mFilterInput != null : "fx:id=\"mFilterInput\" was not injected: check your FXML file 'BusView.fxml'.";
        assert mScrollPane != null : "fx:id=\"mScrollPane\" was not injected: check your FXML file 'BusView.fxml'.";
        assert mServiceTree != null : "fx:id=\"mServiceTree\" was not injected: check your FXML file 'BusView.fxml'.";
        assert mServicesPane != null : "fx:id=\"mServicesPane\" was not injected: check your FXML file 'BusView.fxml'.";
        assert mTitle != null : "fx:id=\"mTitle\" was not injected: check your FXML file 'BusView.fxml'.";
    }

    @Override
    public String toString() {
        return mServiceMap.keySet().toArray().toString();
    }

    public void hideError() {
        mServicesPane.getChildren().remove(mError);
    }

    public void showError(String message) {
        mError.setText(message);
        if (!mServicesPane.getChildren().contains(mError)) {
            mServicesPane.getChildren().add(mError);
        }
    }

    @Override
    public void onClick() {
        hideError();
    }
}
