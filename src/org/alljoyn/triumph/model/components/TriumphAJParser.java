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

package org.alljoyn.triumph.model.components;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.ifaces.Introspectable;
import org.alljoyn.triumph.MainApplication;
import org.alljoyn.triumph.TriumphException;
import org.alljoyn.triumph.controller.session.Session;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Introspection Parser that is able to parse into 
 * object oriented model of Alljoyn components.
 *  
 * @author Michael Hotan mhotan@quicinc.com
 */
public class TriumphAJParser {

    private static final Logger LOG = Logger.getLogger(TriumphAJParser.class.getSimpleName());
    
    /**
     * Must have a session to 
     */
    private final Session mSession;

    private final static String DOC_TYPE = "<!DOCTYPE node PUBLIC \"-" +
            "//freedesktop//DTD D-BUS Object Introspection 1.0//EN\"\n" +
            "\"http://www.freedesktop.org/standards/dbus/1.0/introspect.dtd\">";

    /**
     * Creates a parser with a session manager that is able to access the bus.
     * @param manager Initialized 
     */
    public TriumphAJParser(Session session) {
        if (session == null) 
            throw new IllegalArgumentException("Null Session manager for parser.");
        mSession = session;
    }

    /**
     * Given an Alljoyn Service attempt to retrieve all the objects that are accessible.
     * This returns a complete AllJoyn Service and all the internal objects.
     * 
     * @param service AllJoyn service to construct
     * @param sessionId Session ID associated 
     * @return The complete service with all the internal objects
     * @throws TriumphException Unable to get introspection
     */
    public boolean parseIntrospectData(EndPoint service) throws TriumphException {
        try {
            short sessionId = (short) mSession.getSessionId();
            // Retrieve all the names of the objects that currently exists within that
            // service.  This allows us to check if we need to create a session.
            Set<AJObject> currentObjects = new HashSet<AJObject>(service.getObjects());
            Set<String> currObjStrs = new HashSet<String>(currentObjects.size());
            for (AJObject obj: currentObjects) 
                currObjStrs.add(obj.getName());

            // Parse the root object path first and recursively complete the set
            List<AJObject> objects = parse(service.getName(), "/", sessionId, currObjStrs);
            service.addAll(objects);
            return true;
        } catch (TriumphException e) {
            LOG.warning("Unable to parse instrospection for " + service);
            return false;
        }


    }

    /**
     * Given a wellknown name of a service and the root path
     * of the parent highest on the tree, Find all the children.
     * 
     * All the children that have names in the ignore set will
     * not be introspected.
     * 
     * @param service Well known name of the service to introspect
     * @param objectPath The object path to find object under.
     * @param sessionPortNum Session port number to use to establish a connection to service
     * @param ignoreSet Set of names to ignore if come across.
     * @return List of all children objects that didnt have names in the ignore set.
     * @throws TriumphException Unable to get introspection
     */
    private List<AJObject> parse(
            String service, String objectPath, 
            short sessionPortNum, Set<String> ignoreSet) throws TriumphException {

        // A null ignore set is as good as having an empty one.
        ignoreSet = ignoreSet == null ? new HashSet<String>(): ignoreSet;

        try {
            // Get the introspect data of the bus mamager
            ProxyBusObject proxy = mSession.getProxy(objectPath);
            Introspectable i = proxy.getInterface(Introspectable.class);
            String intr = i.Introspect();

            // A builder that will be used to parse a String.
            // After removing the header the builder parses the document into nodes.
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(
                    intr.replace(DOC_TYPE, ""))));

            // Get the 
            doc.getDocumentElement().normalize();
            Node node = doc.getDocumentElement();

            // Register the node's interfaces on the bus the bus manager uses.
            Status status = mSession.registerInterface(node);
            if (status == Status.BUS_IFACE_ALREADY_EXISTS) {
                MainApplication.getLogger().info("Repeated Interface found while parsing service: " + service + " object path: " + objectPath);
            } else if (status != Status.OK) {
                MainApplication.getLogger().warning("Unable create Interfaces found while parsing service: " + service + " object path: " + objectPath);
            }
            // if this node is an object then add it to the list.
            List<AJObject> objects = new ArrayList<AJObject>();
            if (AJObject.isObject(node))
                objects.add(new AJObject(node, objectPath));

            // iterate through all the sub objects and add 
            // child objects to the list.
            List<String> subObjects = getSubObjects(objectPath, node);

            // Parse each descendant object and add it to
            for (String childName: subObjects) {
                if (ignoreSet.contains(childName)) continue;	
                List<AJObject> childObjects = parse(service, childName, sessionPortNum, ignoreSet);
                objects.addAll(childObjects);
            }
            return objects; 
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("ParserConfigurationException: " + e);
        } catch (SAXException e) {
            throw new RuntimeException("SAXException: " + e);
        } catch (IOException e) {
            throw new RuntimeException("IOException: " + e);
        } catch (BusException e) {
            throw new TriumphException(e);
        }
    }

    /**
     * Given a node with its full name find the immediate children names
     * of that parent.  This will find a one level deep / one generation
     *  list of children.  
     * 
     * @param parentName Object path of parent name. (IE. Parent: /org/alljoyn Child: /org/alljoyn/triumph)
     * @param node Node of the parent
     * @return A list containing all the full names of the children
     */
    private static List<String> getSubObjects(String parentName, Node node) {

        // iterate through all the child nodes
        // If the child is an object then add it to the list to return.
        NodeList children = node.getChildNodes();
        int length = children.getLength();

        // Maintain a list of sub objects.
        List<String> subObjs = new ArrayList<String>(length);

        for (int i = 0; i < length; ++i) {
            Node child = children.item(i); // Get the child

            // If this node is not an object.
            // IE could be an interface or something else.
            if (!AJObject.LABEL.equals(child.getNodeName()))
                continue;

            //Attempt to find the name attribute in the parse
            String name = findName(child);
            if (name == null) {
                MainApplication.getLogger().warning(
                        "Null child name returned for object");
                continue;
            } 

            // Apply the name suffix to the parent full name
            // This is Unix style naming.
            if (parentName.endsWith("/")) 
                name = parentName + name;
            else 
                name = parentName + "/" + name;
            subObjs.add(name);
        }

        return subObjs;
    }

    /**
     * Given a distinct node, attempts to find the name 
     * correspoding to this node
     * @param node node to find name in.
     * @return name on success, null on failure to find name
     */
    public static String findName(Node node) {
        String name = "";

        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) 
            return name;

        Node nameNode = attributes.getNamedItem("name");
        if (nameNode != null)
            name = nameNode.getNodeValue();
        return name;
    }
}
