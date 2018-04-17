/*
 * $Id: RoutingPath.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.problem;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sndlib.core.model.DemandModel;
import sndlib.core.model.LinkModel;
import sndlib.core.network.Link;
import sndlib.core.network.Node;

import com.atesio.utils.ArgChecker;

/**
 * This class represents a routing path in a specific solution of a network
 * design problem.<br/><br/>
 * 
 * A <tt>RoutingPath</tt> is simply a sequence of {@link RoutingLink}s, 
 * where every <tt>RoutingLink</tt>, beside its referenced network 
 * {@link sndlib.core.network.Link}, also comprises the direction into which 
 * the link is traversed.
 * A common use of this class is to check the existence of given path in a 
 * network.
 * 
 * @see SolvedProblem
 * 
 * @author Roman Klaehne
 */
public class RoutingPath {

    /**
     * Constructs a routing path from the specified source to the specified
     * target node. The path is built from the given list of links. The links
     * are assumed to be undirected.
     * 
     * @param links the links on the path
     * @param source the source node of the path
     * @param target the target node of the path
     * 
     * @return a routing path from <tt>source</tt> to <tt>target</tt> 
     * consisting of the given links; <tt>null</tt> if there is no undirected 
     * path from <tt>source</tt> to <tt>target</tt> 
     */
    public static RoutingPath newUndirectedSTPath(List<Link> links, Node source,
        Node target) {

        checkArguments(links, source, target);

        LinkedList<RoutingLink> routingLinks = new LinkedList<RoutingLink>();

        Iterator<Link> linkIt = links.iterator();

        Link firstLink = linkIt.next();
        Node current = null;
        if(firstLink.getFirstNode() == source) {
            current = firstLink.getSecondNode();
        }
        else if(firstLink.getSecondNode() == source) {
            current = firstLink.getFirstNode();
        }
        else {
            return null;
        }

        routingLinks.add(new RoutingLink(firstLink, source, current));

        while (linkIt.hasNext()) {
            Link linkI = linkIt.next();

            Node currentBefore = current;
            if(linkI.getFirstNode() == current) {
                current = linkI.getSecondNode();
            }
            else if(linkI.getSecondNode() == current) {
                current = linkI.getFirstNode();
            }
            else {
                return null;
            }
            routingLinks.add(new RoutingLink(linkI, currentBefore, current));
        }

        if(current != target) {
            return null;
        }

        return new RoutingPath(routingLinks);
    }

    /**
     * Constructs a routing path from the specified source to the specified
     * target node. The path is built from the given list of links. The links
     * are assumed to be directed.
     * 
     * @param links the links on the path
     * @param source the source node of the path
     * @param target the target node of the path
     * 
     * @return a routing path from <tt>source</tt> to <tt>target</tt> 
     * consisting of the given links; <tt>null</tt> if there is no directed 
     * path from <tt>source</tt> to <tt>target</tt> 
     */
    public static RoutingPath newDirectedSTPath(List<Link> links, Node source,
        Node target) {

        checkArguments(links, source, target);

        LinkedList<RoutingLink> routingLinks = new LinkedList<RoutingLink>();

        Iterator<Link> linkIt = links.iterator();

        Link firstLink = linkIt.next();
        if(firstLink.getFirstNode() != source) {
            return null;
        }

        Node current = firstLink.getSecondNode();

        routingLinks.add(new RoutingLink(firstLink, source, current));
        while (linkIt.hasNext()) {
            Link linkI = linkIt.next();
            if(linkI.getFirstNode() != current) {
                return null;
            }

            routingLinks.add(new RoutingLink(linkI, current, linkI.getSecondNode()));
            current = linkI.getSecondNode();
        }

        if(current != target) {
            return null;
        }

        return new RoutingPath(routingLinks);
    }

    /**
     * Constructs a routing path between the specified nodes, disregarding
     * which of them is the source and which is the target node.
     * The path is built from the given list of links. The links are assumed
     * to be undirected.
     * 
     * @param links the links on the path
     * @param one the one end of the path
     * @param other the other end of the path
     * 
     * @return a routing path consisting of the given links between the
     * specified nodes; <tt>null</tt> if there is no undirected 
     * path between the specified nodes 
     */
    public static RoutingPath newUndirectedSTOrTSPath(List<Link> links, Node one,
        Node other) {

        checkArguments(links, one, other);

        Node source = null;
        Node target = null;

        Link firstLink = links.get(0);

        if(firstLink.getFirstNode() == one || firstLink.getSecondNode() == one) {
            source = one;
            target = other;
        }
        else if(firstLink.getFirstNode() == other
            || firstLink.getSecondNode() == other) {
            source = other;
            target = one;
        }
        else {
            return null;
        }

        return newUndirectedSTPath(links, source, target);
    }

    /**
     * Constructs a routing path between the specified nodes, disregarding
     * which of them is the source and which is the target node.
     * The path is built from the given list of links. The links are assumed 
     * to be directed.
     * 
     * @param links the links on the path
     * @param one the one end of the path
     * @param another the other end of the path
     * 
     * @return a routing path consisting of the given links between the
     * specified nodes; <tt>null</tt> if there is no directed 
     * path between the specified nodes
     */
    public static RoutingPath newDirectedSTOrTSPath(List<Link> links, Node one,
        Node another) {

        checkArguments(links, one, another);

        Node source = null;
        Node target = null;

        Link firstLink = links.get(0);

        if(firstLink.getFirstNode() == one) {
            source = one;
            target = another;
        }
        else if(firstLink.getFirstNode() == another) {
            source = another;
            target = one;
        }
        else {
            return null;
        }

        return newDirectedSTPath(links, source, target);
    }

    /**
     * Helps to check the common arguments for validity.
     * 
     * @param links list check for <tt>null</tt> and emptiness
     * @param one to check for <tt>null</tt>
     * @param another to check for <tt>null</tt>
     */
    private static void checkArguments(List<Link> links, Node one, Node another) {

        ArgChecker.checkNotNull(one, "first node");
        ArgChecker.checkNotNull(another, "second node");
        ArgChecker.checkNotNull(links, "links");

        if(links.size() == 0) {
            throw new IllegalArgumentException("empty link list given");
        }
    }

    /**
     * This inner class abstracts the four static methods of 
     * <tt>RoutingPath</tt>.<br/>
     * A specific builder can be acquired using the static method
     * {@link RoutingPath#getBuilderFor(LinkModel, DemandModel)}.
     *
     * @author Roman Klaehne
     */
    abstract public static class Builder {

        /**
         * Constructs a new routing path between the specified nodes, 
         * disregarding which of them is the source and which is the 
         * target node. The path is built from the given list of links.
         * 
         * @param links the links on the path
         * @param one the one end of the path
         * @param another the other end of the path
         * 
         * @return a routing path consisting of the given links between the
         * specified nodes; <tt>null</tt> if there is no path between 
         * <tt>source</tt> and <tt>target</tt> 
         */
        abstract public RoutingPath newPath(List<Link> links, Node one, Node another);

        /**
         * This builder is equivalent to
         * {@link RoutingPath#newUndirectedSTPath(List, Node, Node)}.
         */
        static final Builder ST_UNDIRECTED = new Builder() {

            public RoutingPath newPath(List<Link> links, Node one, Node other) {

                return RoutingPath.newUndirectedSTPath(links, one, other);
            }

        };

        /**
         * This builder is equivalent to
         * {@link RoutingPath#newUndirectedSTOrTSPath(List, Node, Node)}.
         */
        static final Builder ST_TS_UNDIRECTED = new Builder() {

            public RoutingPath newPath(List<Link> links, Node one, Node other) {

                return RoutingPath.newUndirectedSTOrTSPath(links, one, other);
            }

        };

        /**
         * This builder is equivalent to
         * {@link RoutingPath#newDirectedSTPath(List, Node, Node)}.
         */
        static final Builder ST_DIRECTED = new Builder() {

            public RoutingPath newPath(List<Link> links, Node one, Node other) {

                return RoutingPath.newDirectedSTPath(links, one, other);
            }

        };

        /**
         * This builder is equivalent to 
         * {@link RoutingPath#newDirectedSTOrTSPath(List, Node, Node)}.
         */
        static final Builder ST_TS_DIRECTED = new Builder() {

            public RoutingPath newPath(List<Link> links, Node one, Node other) {

                return RoutingPath.newDirectedSTOrTSPath(links, one, other);
            }

        };
    }

    /**
     * Returns the appropriate builder for the specified link and demand model.
     * <br/><br/>
     * If the link model is <tt>DIRECTED</tt> then the links are assumed to be 
     * directed. Otherwise it is assumed that they can be traversed in both
     * directions.
     * If the demand model is <tt>DIRECTED</tt> then there must be a definite
     * source and target node. Otherwise the path is assumed to be valid in
     * both directions. 
     * 
     * @param linkModel the link model
     * @param demandModel the demand model
     * 
     * @return the appropriate builder for the given link and demand model
     */
    public static Builder getBuilderFor(LinkModel linkModel, DemandModel demandModel) {

        Builder builder = null;

        switch (demandModel) {

        case UNDIRECTED:

            switch (linkModel) {

            case DIRECTED:
                builder = Builder.ST_TS_DIRECTED;
                break;

            case UNDIRECTED: /* or */
            case BIDIRECTED:
                builder = Builder.ST_TS_UNDIRECTED;
                break;
            }
            break;

        case DIRECTED:

            switch (linkModel) {

            case DIRECTED:
                builder = Builder.ST_DIRECTED;
                break;

            case BIDIRECTED: /* or */
            case UNDIRECTED:
                builder = Builder.ST_UNDIRECTED;
                break;
            }
            break;
        }

        ArgChecker.assertNotNull(builder, "path builder");

        return builder;
    }

    /**
     * The links in the path.
     */
    private LinkedList<RoutingLink> _routingLinks;

    /**
     * Constructs a new routing path consisting of the given links.
     * 
     * @param routingLinks the links in the path
     */
    private RoutingPath(LinkedList<RoutingLink> routingLinks) {

        _routingLinks = routingLinks;
    }

    /**
     * Returns a <tt>List</tt> view of the routing links this routing 
     * path.<br/>
     * <br/>
     * The returned list is unmodifiable. That means that each attempt to 
     * modify the list will cause an <tt>UnsupportedOperationException</tt>.
     * 
     * @return an unmodifiable list view of the routing links in this routing
     * path 
     */
    public List<RoutingLink> routingLinks() {

        return Collections.unmodifiableList(_routingLinks);
    }

    /**
     * Returns the last routing link in this routing path.
     * 
     * @return the last routing link
     */
    public RoutingLink getLast() {

        return _routingLinks.getLast();
    }

    /**
     * Returns the first routing link in this routing path.
     * 
     * @return the first routing link
     */
    public RoutingLink getFirst() {

        return _routingLinks.getFirst();
    }
}

