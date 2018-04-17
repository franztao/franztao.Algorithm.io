/*
 * $Id: Link.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

import com.atesio.utils.ArgChecker;
import com.atesio.utils.StringFormatUtils;

/**
 * This class represents a link between two nodes in a 
 * {@link Network}.
 * <br/><br/>
 * 
 * Besides its ID a link has several properties, namely a setup cost, 
 * routing cost, and a pre-installed capacity as well as a number of 
 * additional capacities (modules) which can be installed on this link.
 * <br/><br/>
 * 
 * For constructing a new <tt>Link</tt> a parent <tt>Network</tt> is needed.   
 * 
 * @see Network
 * @see Network#newLink(String, Node, Node)
 * 
 * @author Roman Klaehne
 */
final public class Link {

    /**
     * The ID of this link.
     */
    private String _id;

    /**
     * The first (source) node of this link.
     */
    private Node _first;

    /**
     * The second (target) node of this link.
     */
    private Node _second;

    /**
     * The pre-installed capacity of this link, 
     */
    private double _preCapacity = 0.0;

    /**
     * The cost incurred by the pre-installed capacity.
     */
    private double _preCost = 0.0;

    /**
     * The cost incurred by any unit of flow (in terms of routing unit 1) 
     * using the link.
     */
    private double _routingCost = 0.0;

    /**
     * The cost incurred if this link is included in the 
     * final solution topology.
     */
    private double _setupCost = 0.0;

    /**
     * A set of capacity modules which can be installed on this link
     * in addition to the preinstalled capacity.
     */
    private LinkedHashMap<Double, CapacityModule> _modules;

    /**
     * Constructs a new link with the specified ID between the 
     * specified nodes.
     * 
     * @param id the ID of the link
     * @param firstNode the first (source) node of the link
     * @param secondNode the second (target) node of the link
     */
    Link(String id, Node firstNode, Node secondNode) {

        _id = id;
        _first = firstNode;
        _second = secondNode;
        _modules = new LinkedHashMap<Double, CapacityModule>();
    }

    /**
     * Returns the ID of this link.
     * 
     * @return the ID of this link
     */
    public String getId() {

        return _id;
    }

    /**
     * Returns the first (source) node of this link.
     * 
     * @return the first node of this link
     */
    public Node getFirstNode() {

        return _first;
    }

    /**
     * Returns the second (target) node of this link.
     * 
     * @return the second node of this link
     */
    public Node getSecondNode() {

        return _second;
    }

    /**
     * Returns the pre-installed capacity of this link.
     * 
     * @return the pre-installed capacity
     */
    public double getPreCapacity() {

        return _preCapacity;
    }

    /**
     * Sets the pre-installed capacity of this link.
     * It can be used for expansion planning problems or for allocation 
     * problems. 
     * A link with positive pre-installed capacity must be included in every
     * solution with at least that capacity. 
     * 
     * @param preCapacity the pre-installed capacity of this link
     *
     * @throws IllegalArgumentException if the specified capacity 
     * is lower than zero
     */
    public void setPreCapacity(double preCapacity) {

        ArgChecker.checkNotLowerThanZero(preCapacity, "pre-installed capacity");
        _preCapacity = preCapacity;
    }

    /**
     * Returns the number of additional capacity modules installable 
     * on this link.
     * 
     * @return the number of additional modules
     */
    public int moduleCount() {

        return _modules.size();
    }

    /**
     * Tests whether this link contains a additional module with the 
     * specified capacity.
     * 
     * @param capacity the capacity to look for
     * 
     * @return <tt>true</tt> if and only if this link contains a additional 
     * module with the specified capacity; <tt>false</tt> otherwise
     */
    public boolean hasModule(double capacity) {

        return _modules.containsKey(capacity);
    }

    /**
     * Returns the module with the specified capacity.
     * 
     * @param capacity the capacity of the module
     *
     * @return the module with the specified capacity; <tt>null</tt> if this
     * link does not contain such a additional capacity module 
     */
    public CapacityModule getModule(double capacity) {

        return _modules.get(capacity);
    }

    /**
     * Returns a <tt>Collection</tt> view of the additional capacity modules 
     * installable on this link.<br/><br/>
     * The returned collection is unmodifiable. That means that each attempt to 
     * modify the collection will cause an <tt>UnsupportedOperationException</tt>.
     *  
     * @return an unmodifiable collection view of the additional capacity modules 
     * of this link.
     */
    public Collection<CapacityModule> modules() {

        return Collections.unmodifiableCollection(_modules.values());
    }

    /**
     * Sets the cost incurred by the pre-installed capacity of this link.
     * 
     * @param preCost the cost incurred by the pre-installed capacity
     * 
     * @throws IllegalArgumentException if the specified pre-installed capacity cost
     * is lower than zero
     */
    public void setPreCost(double preCost) {

        ArgChecker.checkNotLowerThanZero(preCost, "pre-installed capacity cost");
        _preCost = preCost;
    }

    /**
     * Returns the pre-installed capacity cost of this link.
     * 
     * @return the pre-installed capacity cost of this link.
     */
    public double getPreCost() {

        return _preCost;
    }

    /**
     * Specifies a new capacity module which can be installed on this link.
     * 
     * @param module a new capacity module for this link
     * 
     * @throws IllegalStateException if this link already contains a module
     * with the capacity of the given one
     */
    public void addModule(CapacityModule module) {

        ArgChecker.checkNotNull(module, "module");

        if(hasModule(module.getCapacity())) {
            throw new IllegalStateException(
                "this link already contains a module with capacity "
                    + module.getCapacity());
        }

        _modules.put(module.getCapacity(), module);
    }

    /**
     * Removes the given capacity module from this link.
     * 
     * @param module the capacity module to remove
     * 
     * @return <tt>true</tt> if and only if the given module 
     * was removed from this link; <tt>false</tt> otherwise
     */
    public boolean removeModule(CapacityModule module) {

        ArgChecker.checkNotNull(module, "module");

        CapacityModule m = _modules.get(module.getCapacity());
        if(m == module) {
            _modules.remove(module.getCapacity());
            return true;
        }

        return false;
    }

    /**
     * Returns the setup cost of this link.
     * 
     * @return the setup cost of this link
     */
    public double getSetupCost() {

        return _setupCost;
    }

    /**
     * Sets the setup cost of this link. The setup cost is incurred if 
     * this link is included in the final solution topology. In particular,
     * this is the case if the link has preinstalled capacity.
     * 
     * @param setupCost the setup cost
     * 
     * @throws IllegalArgumentException if the specified setup cost 
     * is lower than zero.
     */
    public void setSetupCost(double setupCost) {

        ArgChecker.checkNotLowerThanZero(setupCost, "setup cost");
        _setupCost = setupCost;
    }

    /**
     * Returns the routing cost of this link.
     * 
     * @return the routing cost of this link
     */
    public double getRoutingCost() {

        return _routingCost;
    }

    /**
     * Sets the routing cost of this link. The routing cost is incurred by 
     * any unit of flow (in terms of routing unit 1) using the link.
     * 
     * @param routingCost the routing cost of this link 
     * 
     * @throws IllegalArgumentException if the specified routing cost 
     * is lower than zero 
     */
    public void setRoutingCost(double routingCost) {

        ArgChecker.checkNotLowerThanZero(routingCost, "routing cost");
        _routingCost = routingCost;
    }

    /**
     * Returns a textual representation of this link.
     * 
     * @return a textual representation of this link
     */
    public String toString() {

        StringBuilder result = new StringBuilder();
        result.append("link [\n");
        result.append(" id                       = " + getId() + "\n");
        result.append(" source                   = " + _first.getId() + "\n");
        result.append(" target                   = " + _second.getId() + "\n");
        result.append(" preInstalledCapacity     = " + _preCapacity + "\n");
        result.append(" preInstalledCapacityCost = " + _preCost + "\n");
        result.append(" unitRoutingCost          = " + _routingCost + "\n");
        result.append(" setupCost                = " + _setupCost + "\n\n");

        result.append(" additionalModules [\n");

        for(CapacityModule mod : _modules.values()) {
            result.append(StringFormatUtils.indentByLine(mod.toString(), 2) + "\n");
        }

        result.append(" ]\n");
        result.append("]");

        return result.toString();
    }
}
