/*
 * $Id: RoutingLink.java 99 2006-07-04 14:05:16Z bzforlow $
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

import sndlib.core.network.Link;
import sndlib.core.network.Node;

/**
 * A <tt>RoutingLink</tt> is a component of a {@link RoutingPath} and wraps
 * a single network link together with the direction of the corresponding 
 * flow. 
 * 
 * @author Roman Klaehne
 */
public class RoutingLink {

    /**
     * The corresponding network link.
     */
    private Link _link;

    /**
     * The source node of the flow.
     */
    private Node _source;

    /**
     * The target node of the flow.
     */
    private Node _target;

    /**
     * Constructs a new routing link.
     * 
     * @param link the corresponding network link
     * @param source the source node of the flow
     * @param target the target node of the flow
     */
    RoutingLink(Link link, Node source, Node target) {

        _link = link;
        _source = source;
        _target = target;
    }

    /**
     * Returns the corresponding network link.
     * 
     * @return the corresponding network link
     */
    public Link getLink() {

        return _link;
    }

    /**
     * Returns the source node of the corresponding flow. The returned 
     * node is either of the two nodes which are incident to the network link 
     * corresponding to this routing link. 
     * 
     * @return the source node of the corresponding flow path
     */
    public Node getSource() {

        return _source;
    }

    /**
     * Returns the target node of the corresponding flow. The returned 
     * node is either of the two nodes which are incident to the network link 
     * corresponding to this routing link. 
     * 
     * @return the target node of the corresponding flow path
     */
    public Node getTarget() {

        return _target;
    }

    /**
     * Returns <tt>true</tt> if the link is traversed in positive direction.
     * A link is traversed in positive direction if the corresponding link's 
     * first node is the source of the flow.
     * 
     * @return <tt>true</tt> if the link is traversed in positive direction;
     * <tt>false</tt> otherwise
     */
    public boolean isPositiveDirection() {

        return _link.getFirstNode() == _source;
    }
}

