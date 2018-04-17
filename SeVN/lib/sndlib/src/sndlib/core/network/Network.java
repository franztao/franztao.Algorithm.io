/*
 * $Id: Network.java 631 2011-04-27 08:16:04Z bzfraack $
 *
 * Copyright (c) 2005-2006 by Konrad-Zuse-Zentrum fuer Informationstechnik Berlin. 
 * (http://www.zib.de)  
 * 
 * Licensed under the ZIB ACADEMIC LICENSE; you may not use this file except 
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.zib.de/Optimization/Software/ziblicense.html
 *
 * as well as in the file LICENSE.txt, contained in the SNDlib distribution 
 * package.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sndlib.core.network;

import static com.atesio.utils.ArgChecker.checkNotEmpty;
import static com.atesio.utils.ArgChecker.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import sndlib.core.util.NetworkUtils;

import com.atesio.utils.StringFormatUtils;

/**
 * This class is the root element of a network definition tree.<br/><br/>
 * 
 * It provides the topology of the network (the nodes and the connections 
 * between them) as well as the traffic demands.
 * 
 * More precisely, a <tt>Network</tt> consists of {@link Node}s, {@link Link}s, 
 * {@link Demand}s and {@link AdmissiblePath}s. The <tt>Network</tt> object 
 * acts as a factory for constructing these elements.<br/><br/>
 * 
 * A <tt>Network</tt> object maintains several invariants:
 * 
 * <ul>
 *  <li>
 *     For all links <i>e</i> in the network: 
 *     The source and target node of <i>e</i> are also contained in the 
 *     network.<br/>
 *  </li>
 *  <li>
 *     For all demands <i>d</i> in the network: 
 *     The source and target node of <i>d</i> are also contained in the 
 *     network.<br/>
 *  </li>
 *  <li>
 *     If a node is removed from the network then all of its incident links 
 *     and demands will be also removed.<br/>
 *  </li>
 *  <li>
 *     For all admissible paths <i>p</i> of all demands in the network: 
 *     All links of <i>p</i> are also contained in the network.<br/>
 *  </li>
 *  <li>
 *     If a link is removed from the network then that link is also removed
 *     from all admissible paths of all demands in the network.<br/>
 *     If an admissible path is empty after removing a link then that path
 *     will be removed from its parent demand.
 *  </li>
 * </ul> 
 *
 * @see NetworkUtils 
 * @see Node 
 * @see Link 
 * @see Demand 
 * @see AdmissiblePath 
 * @see CapacityModule
 * 
 * @author Roman Klaehne
 */
final public class Network {

    /**
     * The name of this network.
     */
    private String _name;

    /**
     * The demands of this network keyed by ID.
     */
    private LinkedHashMap<String, Demand> _demands;

    /**
     * The links of this network keyed by ID.
     */
    private LinkedHashMap<String, Link> _links;

    /**
     * The nodes of this network keyed by ID.
     */
    private LinkedHashMap<String, Node> _nodes;

    /**
     * The type of the node coordinates in this network.
     */
    private NodeCoordinatesType _nodeCoordType;
    
    /**
     * The meta data of this network.
     */
    private Meta _meta;

    /**
     * Constructs a new network object without defining a name.
     */
    public Network() {

        this("noName");
    }

    /**
     * Constructs a new network object with the specified name.
     * 
     * @param name the name for this network.
     * 
     * @throws IllegalArgumentException if the given name is <tt>null</tt> or empty.
     */
    public Network(String name) {

        setName(name);

        _nodes   = new LinkedHashMap<String, Node>();
        _links   = new LinkedHashMap<String, Link>();
        _demands = new LinkedHashMap<String, Demand>();
        _meta    = null;

        _nodeCoordType = NodeCoordinatesType.PIXEL;
    }

    /**
     * Sets the name of this network.
     * 
     * @param name the new name for this network
     * 
     * @throws IllegalArgumentException if the given name is <tt>null</tt> or empty.
     */
    public void setName(String name) {

        checkNotEmpty(name, "name");
        _name = name;
    }
    
    /**
     * Sets the meta information for this network
     * 
     * @param meta the meta information
     * 
     * @throws IllegalArgumentException if the given meta information is <tt>null</tt>.
     */
    public void setMeta(Meta meta) {

        checkNotNull(meta, "meta");
        _meta = meta;
    }

    /**
     * Sets the type of the node coordinates in this network.<br/>
     * The specified type is inherited by all nodes currently in this network 
     * and by those which will be added subsequently.
     * 
     * @param nodeCoordType the coordinates type
     * 
     * @throws IllegalArgumentException if the specified coordinates type
     * is {@link NodeCoordinatesType#GEOGRAPHICAL} and there is a node in 
     * this network whose coordinates cannot be interpreted as a geographical 
     * longitude and latitude
     */
    public void setNodeCoordinatesType(NodeCoordinatesType nodeCoordType) {

        checkNotNull(nodeCoordType, "node coordinates type");

        for(Node node : _nodes.values()) {
            Node.checkCoordinates(nodeCoordType, node.getXCoordinate(),
                node.getYCoordinate());
        }

        for(Node node : _nodes.values()) {
            node.setCoordinateType(nodeCoordType);
        }
        _nodeCoordType = nodeCoordType;
    }

    /**
     * Returns the type of the node coordinates in this network.
     * 
     * @return the type of the node coordinates in this network
     */
    public NodeCoordinatesType getNodeCoordinatesType() {

        return _nodeCoordType;
    }

    /**
     * Tests whether this network contains the given demand instance.
     * 
     * @param demand the demand
     * 
     * @return <tt>true</tt> if and only if the given demand belongs to 
     * this network; <tt>false</tt> otherwise.
     */
    public boolean containsDemand(Demand demand) {

        checkNotNull(demand, "demand");

        return _demands.get(demand.getId()) == demand;
    }

    /**
     * Tests whether this network contains the given link instance.
     * 
     * @param link the link
     * 
     * @return <tt>true</tt> if and only if the given link belongs to 
     * this network; <tt>false</tt> otherwise.
     */
    public boolean containsLink(Link link) {

        checkNotNull(link, "link");

        return _links.get(link.getId()) == link;
    }

    /**
     * Tests whether this network contains the given node instance.
     * 
     * @param node the node
     * 
     * @return <tt>true</tt> if and only if the given node belongs to 
     * this network; <tt>false</tt> otherwise.
     */
    public boolean containsNode(Node node) {

        checkNotNull(node, "node");

        return _nodes.get(node.getId()) == node;
    }

    /**
     * Returns the number of demands contained in this network.
     * 
     * @return the number of demands
     */
    public int demandCount() {

        return _demands.size();
    }

    /**
     * Returns a <tt>Collection</tt> view of the demands in this network.<br/>
     * <br/>
     * The returned collection is unmodifiable. That means that each attempt to 
     * modify the collection will cause an <tt>UnsupportedOperationException</tt>.
     *  
     * 
     * @return an unmodifiable collection view of the demands in this network.
     */
    public Collection<Demand> demands() {

        return Collections.unmodifiableCollection(_demands.values());
    }

    /**
     * This method is an internal helper for removing a demand from this network.
     * 
     * @param demand the demand to remove.
     */
    private void doRemoveDemand(Demand demand) {

        _demands.remove(demand.getId());
    }

    /**
     * This method is an internal helper for removing a link from this network.
     * 
     * @param link the link to remove.
     */
    private void doRemoveLink(Link link) {

        for(Demand demand : _demands.values()) {
            List<AdmissiblePath> pathsToRemove = new LinkedList<AdmissiblePath>();

            for(AdmissiblePath path : demand.admissiblePaths()) {
                path.removeLink(link);
                if(path.linkCount() == 0) {
                    pathsToRemove.add(path);
                }
            }
            for(AdmissiblePath pathToRemove : pathsToRemove) {
                demand.removeAdmissiblePath(pathToRemove);
            }
        }

        _links.remove(link.getId());
    }

    /**
     * This method is an internal helper for removing a node from this network.
     * 
     * @param node the node to remove.
     */
    private void doRemoveNode(Node node) {

        List<Link> linksToRemove = new LinkedList<Link>();
        for(Link link : _links.values()) {
            if(link.getFirstNode() == node || link.getSecondNode() == node) {
                linksToRemove.add(link);
            }
        }
        for(Link link : linksToRemove) {
            doRemoveLink(link);
        }

        List<Demand> demandsToRemove = new LinkedList<Demand>();
        for(Demand demand : _demands.values()) {
            if(demand.getFirstNode() == node || demand.getSecondNode() == node) {
                demandsToRemove.add(demand);
            }
        }
        for(Demand demand : demandsToRemove) {
            doRemoveDemand(demand);
        }

        _nodes.remove(node.getId());
    }

    /**
     * Returns the <tt>Demand</tt> with the specified ID.
     * 
     * @return the <tt>Demand</tt> with the specified ID; 
     * <tt>null</tt> if such a demand does not exist in this network.
     */
    public Demand getDemand(String demandId) {

        checkNotNull(demandId, "demand id");

        return _demands.get(demandId);
    }

    /**
     * Returns the <tt>Link</tt> with the specified ID.
     * 
     * @return the <tt>Link</tt> with the specified ID; 
     * <tt>null</tt> if such a link does not exist in this network.
     */
    public Link getLink(String linkId) {

        checkNotNull(linkId, "link id");

        return _links.get(linkId);
    }

    /**
     * Returns the <tt>Node</tt> with the specified ID.
     * 
     * @return the <tt>Node</tt> with the specified ID; 
     * <tt>null</tt> if such a node does not exist in this network.
     */
    public Node getNode(String nodeId) {

        checkNotNull(nodeId, "node id");

        return _nodes.get(nodeId);
    }

    /**
     * Returns the name of this network.
     * 
     * @return the name of this network.
     */
    public String getName() {

        return _name;
    }
    
    /**
     * Returns the meta information of this network.
     * 
     * @return the meta information this network.
     */
    public Meta getMeta() {

        return _meta;
    }

    /**
     * Tests whether this network contains a <tt>Demand</tt> with the specified ID.
     * 
     * @return <tt>true</tt> if and only if this network contains a demand 
     * with the specified ID; <tt>false</tt> otherwise.
     */
    public boolean hasDemand(String demandId) {

        checkNotNull(demandId, "demand id");

        return _demands.containsKey(demandId);
    }

    /**
     * Tests whether this network contains a <tt>Link</tt> with the specified ID.
     * 
     * @return <tt>true</tt> if and only if this network contains a link 
     * with the specified ID; <tt>false</tt> otherwise.
     */
    public boolean hasLink(String linkId) {

        checkNotNull(linkId, "link id");

        return _links.containsKey(linkId);
    }

    /**
     * Tests whether this network contains a <tt>Node</tt> with the specified ID.
     * 
     * @return <tt>true</tt> if and only if this network contains a node 
     * with the specified ID; <tt>false</tt> otherwise.
     */
    public boolean hasNode(String nodeId) {

        checkNotNull(nodeId, "node id");

        return _nodes.containsKey(nodeId);
    }

    /**
     * Returns the number of links contained in this network.
     * 
     * @return the number of links
     */
    public int linkCount() {

        return _links.size();
    }

    /**
     * Returns a <tt>Collection</tt> view of the links in this network.<br/>
     * <br/>
     * The returned collection is unmodifiable. That means that each attempt to 
     * modify the collection will cause an <tt>UnsupportedOperationException</tt>.
     * 
     * @return an unmodifiable collection view of the links in this network.
     */
    public Collection<Link> links() {

        return Collections.unmodifiableCollection(_links.values());
    }

    /**
     * Constructs a new <tt>Demand</tt> in this network.<br/>
     * <br/>
     * 
     * @param demandId the ID of the demand
     * @param first the first (source) node of the demand
     * @param second the second (target) node of the demand
     * 
     * @return a new created demand
     * 
     * @throws IllegalStateException if any of the given nodes do not belong
     * to this network; or if a demand with the given ID already exists in this 
     * network. 
     */
    public Demand newDemand(String demandId, Node first, Node second) {

        checkNotNull(demandId, "demand id");
        checkNotNull(first, "first node");
        checkNotNull(second, "second node");

        if(!containsNode(first)) {
            throw new IllegalStateException("the given node with ID "
                + first.getId() + " does not belong to this network");
        }
        if(!containsNode(second)) {
            throw new IllegalStateException("the given node with ID "
                + second.getId() + " does not belong to this network");
        }

        if(_demands.containsKey(demandId)) {
            throw new IllegalStateException(
                "this network already contains a demand with id " + demandId);
        }

        Demand demand = new Demand(demandId, first, second);

        _demands.put(demandId, demand);

        return demand;
    }

    /**
     * Creates a new admissible path for the given demand.
     * <br/><br/>
     * 
     * The path is built from the given list of links.
     * 
     * @param pathId the ID of the path
     * @param links the links of the path
     * @param demand the demand to create the admissible path for
     * 
     * @return the new created path
     * 
     * @throws IllegalStateException if given demand already
     * has a path with the specified ID
     * @throws IllegalStateException if the given demand or one of the 
     * given links does not belong to this network
     * @throws IllegalArgumentException if the given list of links is empty 
     */
    public AdmissiblePath newAdmissiblePath(String pathId, List<Link> links,
        Demand demand) {

        checkNotNull(pathId, "path id");
        checkNotNull(links, "link list");
        checkNotNull(demand, "demand");

        if(!containsDemand(demand)) {
            throw new IllegalStateException("the given demand with ID "
                + demand.getId() + " does not belong to this network");
        }

        if(links.size() == 0) {
            throw new IllegalArgumentException(
                "the given list of links must not be empty");
        }

        for(Link link : links) {
            checkNotNull(link, "links in link list");

            if(!containsLink(link)) {
                throw new IllegalStateException("the given link with ID "
                    + link.getId() + " does not belong to this network");
            }
        }

        if(demand.hasAdmissiblePath(pathId)) {
            throw new IllegalStateException("demand " + demand.getId()
                + " already contains a path with id " + pathId);
        }

        AdmissiblePath path = new AdmissiblePath(pathId, links);
        demand.addAdmissiblePath(path);

        return path;
    }

    /**
     * Constructs a new <tt>Link</tt> in this network.<br/>
     * <br/>
     * 
     * @param linkId the ID of the link
     * @param first the first (source) node of the link
     * @param second the second (target) node of the link
     * 
     * @return a new created link
     * 
     * @throws IllegalStateException if any of the given nodes do not belong
     * to this network; or if a link with the given ID already exists in this 
     * network. 
     */
    public Link newLink(String linkId, Node first, Node second) {

        checkNotNull(linkId, "link id");
        checkNotNull(first, "first node");
        checkNotNull(second, "second node");

        if(!containsNode(first)) {
            throw new IllegalStateException("the given node with ID "
                + first.getId() + " does not belong to this network");
        }
        if(!containsNode(second)) {
            throw new IllegalStateException("the given node with ID "
                + second.getId() + " does not belong to this network");
        }

        if(_links.containsKey(linkId)) {
            throw new IllegalStateException(
                "this network already contains a link with id " + linkId);
        }

        Link link = new Link(linkId, first, second);

        _links.put(linkId, link);

        return link;
    }

    /**
     * Constructs a new <tt>Node</tt> in this network.<br/>
     * <br/>
     * 
     * @param nodeId the ID of the node

     * @return a new created node
     * 
     * @throws IllegalArgumentException if a node with the given ID already exists 
     * in this network. 
     */
    public Node newNode(String nodeId) {

        return newNode(nodeId, 0.0, 0.0);
    }

    /**
     * Constructs a new <tt>Node</tt> in this network.<br/>
     * <br/>
     * 
     * @param nodeId the ID of the node
     * @param xCoord the x-coordinate (longitude) of the node
     * @param yCoord the y-coordinate (latitude) of the node
     * 
     * @return a new created node
     * 
     * @throws IllegalStateException if a node with the given ID already exists 
     * in this network. 
     */
    public Node newNode(String nodeId, double xCoord, double yCoord) {

        checkNotNull(nodeId, "node id");

        if(_nodes.containsKey(nodeId)) {
            throw new IllegalStateException(
                "this network already contains a node with id " + nodeId);
        }

        Node node = new Node(nodeId, _nodeCoordType, xCoord, yCoord);

        _nodes.put(nodeId, node);

        return node;
    }

    /**
     * Returns the number of nodes contained in this network.
     * 
     * @return the number of nodes
     */
    public int nodeCount() {

        return _nodes.size();
    }

    /**
     * Returns a <tt>Collection</tt> view of the nodes in this network.<br/>
     * <br/>
     * The returned collection is unmodifiable. That means that each attempt to 
     * modify the collection will cause an <tt>UnsupportedOperationException</tt>.
     * 
     * @return an unmodifiable collection view of the nodes in this network.
     */
    public Collection<Node> nodes() {

        return Collections.unmodifiableCollection(_nodes.values());
    }

    /**
     * Removes the given <tt>Demand</tt> from this network.
     * 
     * @param demand the demand to remove
     * 
     * @return <tt>true</tt> if and only if the given demand was removed; 
     * <tt>false</tt> otherwise.
     */
    public boolean removeDemand(Demand demand) {

        if(!containsDemand(demand)) {
            return false;
        }

        doRemoveDemand(demand);

        return true;
    }

    /**
     * Removes the given <tt>Link</tt> from this network.<br/><br/>
     * 
     * The given link will be removed automatically from all admissible
     * paths of all demands in this network. If an admissible path remains
     * empty after removing the link it is also removed from its parent
     * demand.
     * 
     * @param link the link to remove
     * 
     * @return <tt>true</tt> if and only if the given link was removed; 
     * <tt>false</tt> otherwise.
     */
    public boolean removeLink(Link link) {

        if(!containsLink(link)) {
            return false;
        }

        doRemoveLink(link);

        return true;
    }

    /**
     * Removes the given <tt>Node</tt> from this network.<br/><br/>
     * 
     * All links and demands which are incident to the given node will
     * be removed automatically.
     * 
     * @param node the node to remove
     * 
     * @return <tt>true</tt> if and only if the given node was removed; 
     * <tt>false</tt> otherwise.
     */
    public boolean removeNode(Node node) {

        if(!containsNode(node)) {
            return false;
        }

        doRemoveNode(node);

        return true;
    }

    /**
     * Returns a textual representation of this network.
     *
     * @return a textual representation of this network.
     */
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append("network [\n\n");
        result.append(" nodes [\n");
        for(Node node : _nodes.values()) {
            result.append(StringFormatUtils.indentByLine(node.toString(), 2) + "\n");
        }
        result.append(" ]\n\n");

        result.append(" links [\n");
        for(Link link : _links.values()) {
            result.append(StringFormatUtils.indentByLine(link.toString(), 2) + "\n");
        }
        result.append(" ]\n\n");

        result.append(" demands [\n");
        for(Demand demand : _demands.values()) {
            result.append(StringFormatUtils.indentByLine(demand.toString(), 2)
                + "\n");
        }
        result.append(" ]\n");

        result.append("]");

        return result.toString();
    }
}
