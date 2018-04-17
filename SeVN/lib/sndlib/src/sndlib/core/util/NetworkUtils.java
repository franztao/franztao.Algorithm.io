/*
 * $Id: NetworkUtils.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.util;

import static com.atesio.utils.ArgChecker.checkNotNull;

import java.util.LinkedList;
import java.util.List;

import sndlib.core.model.LinkModel;
import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.Problem;

/**
 * This class provides some commonly used utility methods on a {@link Network} 
 * (or rather a {@link Problem}).
 * 
 * @author Roman Klaehne
 */
public class NetworkUtils {

    /**
     * Given a list of link ID's this method returns the corresponding 
     * <tt>Link</tt>s in the specified network<br/>
     * <br/>
     * 
     * If some link cannot be found in the network then this method
     * returns <tt>null</tt>. If all links are found then the returned list 
     * will have the same order as the given one.
     * 
     * @param linkIds the list of link ID's
     * @param network the network in which the links are to be looked for
     * 
     * @return a <tt>List</tt> of <tt>Link</tt>s in the same order as the
     * given <tt>List</tt> of link ID's; <tt>null</tt> if some link cannot 
     * be found
     */
    public static List<Link> getLinksByIds(List<String> linkIds, Network network) {

        checkNotNull(linkIds, "link ID list");
        checkNotNull(network, "network");

        List<Link> links = new LinkedList<Link>();
        for(String linkId : linkIds) {

            Link link = network.getLink(linkId);
            if(link == null) {
                return null;
            }
            links.add(link);
        }
        return links;
    }

    /**
     * Calculates the maximal demand value over all demands contained in the 
     * given network.
     * 
     * @param network the network 
     * 
     * @return the maximal demand value
     */
    public static double getMaxDemandValue(Network network) {

        checkNotNull(network, "network");

        double maxValue = 0.0;
        for(Demand demand : network.demands()) {
            maxValue = Math.max(maxValue, demand.getDemandValue());
        }
        return maxValue;
    }

    /**
     * Determines all incident links of the given node.
     * <br/><br/>
     * 
     * A link is incident to a node if that node is either the link's source 
     * or the link's target node.<br/>
     * (<tt>link l incident to node n <=> l.source = n or l.target = n</tt>)
     * <br/><br/>
     * 
     * If no link is incident to the given node then this method returns an 
     * empty list.
     * 
     * @param node the node 
     * @param network the network enclosing the node
     * 
     * @return a <tt>List</tt> of the incident <tt>Link</tt>s
     */
    public static List<Link> getIncidentLinks(Node node, Network network) {

        checkNotNull(node, "node");
        checkNotNull(network, "network");

        List<Link> incidentLinks = new LinkedList<Link>();

        for(Link link : network.links()) {
            if(link.getFirstNode() == node || link.getSecondNode() == node) {
                incidentLinks.add(link);
            }
        }
        return incidentLinks;
    }

    /**
     * Determines the in-degree of the given node.
     * <br/><br/>
     * 
     * If the given problem's <tt>LinkModel</tt> is <tt>DIRECTED</tt> then the 
     * in-degree of a node is the number of its incident links of which that 
     * node is the target.
     * <br/><br/>
     * 
     * If the <tt>LinkModel</tt> is not <tt>DIRECTED</tt> then there is no 
     * definite target node. Thus the in-degree of a node is simply the 
     * number of its incident links.
     * 
     * @param node the node to calculate the in-degree for
     * @param problem the problem around the node
     * 
     * @return the in-degree of the given node
     */
    public static int getNodeInDegree(Node node, Problem problem) {

        checkNotNull(node, "node");
        checkNotNull(problem, "problem");

        int nodeInDegree = 0;

        List<Link> incidentLinks = getIncidentLinks(node, problem.getNetwork());
        if(problem.getLinkModel() != LinkModel.DIRECTED) {
            nodeInDegree = incidentLinks.size();
        }
        else {
            for(Link link : incidentLinks) {
                if(link.getSecondNode() == node) {
                    nodeInDegree++;
                }
            }
        }

        return nodeInDegree;
    }

    /**
     * Determines the out-degree of the given node.
     * <br/><br/>
     * 
     * If the given problem's <tt>LinkModel</tt> is <tt>DIRECTED</tt> then the 
     * out-degree of a node is the number of its incident links of which that 
     * node is the source.
     * <br/><br/>
     * 
     * If the <tt>LinkModel</tt> is not <tt>DIRECTED</tt> then there is no 
     * definite source node. Thus the out-degree of a node is simply the 
     * number of its incident links.
     * 
     * @param node the node to calculate the out-degree for
     * @param problem the problem around the node
     * 
     * @return the out-degree of the given node
     */
    public static int getNodeOutDegree(Node node, Problem problem) {

        checkNotNull(node, "node");
        checkNotNull(problem, "problem");

        int nodeOutDegree = 0;

        List<Link> incidentLinks = getIncidentLinks(node, problem.getNetwork());
        if(problem.getLinkModel() != LinkModel.DIRECTED) {
            nodeOutDegree = incidentLinks.size();
        }
        else {
            for(Link link : incidentLinks) {
                if(link.getFirstNode() == node) {
                    nodeOutDegree++;
                }
            }
        }

        return nodeOutDegree;
    }

    /**
     * Determines the degree of the given node.
     * <br/><br/>
     * 
     * The degree of a given node is simply the number of its incident links.
     * Therefore this method is the same as <tt>getIncidentLinks().size()</tt>
     * 
     * @param node the node to calculate the degree for
     * @param network the network of the node
     * 
     * @return the degree of the given node
     */
    public static int getNodeDegree(Node node, Network network) {

        return getIncidentLinks(node, network).size();
    }

    /**
     * Tests whether the given two links are parallel.
     * <br/><br/>
     * 
     * Because no specific network model is took into account, parallel
     * means either really parallel or anti-parallel (for the directed case).
     * 
     * @param l1 the first link
     * @param l2 the second link
     * 
     * @return <tt>true</tt> if and only if the given two links are 
     * (anti-) parallel; <tt>false</tt> otherwise 
     */
    public static boolean areParallel(Link l1, Link l2) {

        checkNotNull(l1, "first link");
        checkNotNull(l2, "second link");

        Node n11 = l1.getFirstNode();
        Node n12 = l1.getSecondNode();

        Node n21 = l2.getFirstNode();
        Node n22 = l2.getSecondNode();

        return ((n11 == n21 && n12 == n22) || (n11 == n22 && n12 == n21));
    }

    /**
     * Tests whether the given two demands are parallel.
     * <br/><br/>
     * 
     * Because no specific network model is took into account, parallel
     * means either really parallel or anti-parallel (for the directed case).
     * 
     * @param d1 the demand link
     * @param d2 the demand link
     * 
     * @return <tt>true</tt> if and only if the given two demands are 
     * (anti-) parallel; <tt>false</tt> otherwise 
     */
    public static boolean areParallel(Demand d1, Demand d2) {

        checkNotNull(d1, "first demand");
        checkNotNull(d2, "second demand");

        Node n11 = d1.getFirstNode();
        Node n12 = d1.getSecondNode();

        Node n21 = d2.getFirstNode();
        Node n22 = d2.getSecondNode();

        return ((n11 == n21 && n12 == n22) || (n11 == n22 && n12 == n21));
    }

    /**
     * The earth radius in km.
     */
    private static final double EARTH_RADIUS = 6378.137;

    /**
     * Calculates the real-world-distance of the given two nodes in 
     * kilometer.<br/>
     * <br/>
     * 
     * This method assumes that the coordinates of the given nodes are 
     * geographical. If not, no warranty can be given on the result.<br/>
     * 
     * @param n1 the source node
     * @param n2 the target node
     * 
     * @return the distance of the given two nodes in kilometer; 
     */
    public static double calculateDistanceInKms(Node n1, Node n2) {

        checkNotNull(n1, "first node");
        checkNotNull(n2, "second node");

        double longitude1 = n1.getXCoordinate() / 180.0 * Math.PI;
        double longitude2 = n2.getXCoordinate() / 180.0 * Math.PI;
        double latitude1 = n1.getYCoordinate() / 180.0 * Math.PI;
        double latitude2 = n2.getYCoordinate() / 180.0 * Math.PI;

        return Math.acos(Math.sin(latitude1) * Math.sin(latitude2)
            + Math.cos(latitude1) * Math.cos(latitude2)
            * Math.cos(longitude2 - longitude1))
            * EARTH_RADIUS;
    }

    private NetworkUtils() {

        /* not instantiable */
    }
}
