/*
 * $Id: Demand.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import static com.atesio.utils.ArgChecker.checkNotNull;
import static com.atesio.utils.ArgChecker.checkNotLowerOrEqualToZero;
import static com.atesio.utils.ArgChecker.checkNotLowerThanZero;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.atesio.utils.StringFormatUtils;

/**
 * This class represents a single traffic demand between two nodes in a 
 * {@link Network}.
 * <br/><br/>
 * 
 * Besides its ID a demand has several properties, such as the demand value, 
 * the routing unit and the hop limit. Also a set of admissible paths can 
 * be specified for each demand, which is used if the demand model is
 * EXPLICIT_PATHS.
 * <br/><br/>
 * 
 * For constructing a new <tt>Demand</tt> a parent <tt>Network</tt> is needed.   
 * 
 * @see Network
 * @see Network#newDemand(String, Node, Node)
 * 
 * @author Roman Klaehne
 */
final public class Demand {

    /**
     * The ID of this demand.
     */
    private String _id;

    /**
     * The first (source) node of this demand.
     */
    private Node _first;

    /**
     * The second (target) node of this demand.
     */
    private Node _second;

    /**
     * The value (or traffic rate) of this demand.
     */
    private double _demandValue;

    /**
     * The individual routing unit of this demand.
     */
    private int _routingUnit;

    /**
     * The individual hop limit of this demand.
     */
    private int _maxPathLength;

    /**
     * The admissible paths for this demand.
     */
    private Map<String, AdmissiblePath> _admissiblePaths;

    /**
     * Constructs a new <tt>Demand</tt> with the specified ID between the 
     * specified nodes.
     * 
     * @param id the ID of the demand
     * @param firstNode the first (source) node of the demand
     * @param secondNode the second (target) node of the demand
     */
    Demand(String id, Node firstNode, Node secondNode) {

        _id = id;
        _first = firstNode;
        _second = secondNode;
        _admissiblePaths = new LinkedHashMap<String, AdmissiblePath>();
        _maxPathLength = 0;
        _routingUnit = 1;
    }

    /**
     * Specifies a new admissible path for this demand.
     * 
     * @param path the admissible path
     */
    void addAdmissiblePath(AdmissiblePath path) {

        _admissiblePaths.put(path.getId(), path);
    }

    /**
     * Returns the ID of this demand.
     * 
     * @return the ID of this demand
     */
    public String getId() {

        return _id;
    }

    /**
     * Sets maximum number of links (hop limit) used on a path to route this demand.
     * It is interpreted for a specific demand such that the routing paths 
     * must not consist of more than the given <tt>maxPathLength</tt> 
     * links during normal operation. An argument of zero indicates that there 
     * is no hop limit restriction for this demand.
     * 
     * @param maxPathLength the maximum path length; or zero for no restriction
     * 
     * @throws IllegalArgumentException if the given maximum path length is
     * lower than zero.
     */
    public void setMaxPathLength(int maxPathLength) {

        checkNotLowerThanZero(maxPathLength, "max path length");

        _maxPathLength = maxPathLength;
    }

    /**
     * Returns <tt>true</tt> if and only if this demand has a positive 
     * maximum path length restriction. This method is equivalent to 
     * <tt>getMaxPathLength() > 0</tt>.
     * 
     * @return <tt>true</tt> if this demand has a positive 
     * maximum path length restriction; <tt>false</tt> otherwise
     */
    public boolean hasMaxPathLength() {

        return _maxPathLength > 0;
    }

    /**
     * Returns the maximum path length used for this demand.
     * If this method returns zero then there is no restriction for the 
     * maximum path length.
     * 
     * @return the maximum path length restriction for this demand;
     * zero if there is no restriction
     */
    public int getMaxPathLength() {

        return _maxPathLength;
    }

    /**
     * Returns the first (source) node of this demand.
     * 
     * @return the first (source) node of this demand
     */
    public Node getFirstNode() {

        return _first;
    }

    /**
     * Returns the second (target) node of this demand.
     * 
     * @return the second (target) node of this demand
     */
    public Node getSecondNode() {

        return _second;
    }

    /**
     * Returns the value (traffic rate) of this demand.
     * 
     * @return the value of this demand
     */
    public double getDemandValue() {

        return _demandValue;
    }

    /**
     * Sets the value (traffic rate) of this demand.
     * It is interpreted for a specific demand such that at least 
     * <tt>routingUnit</tt> times <tt>demandValue</tt> many units must be 
     * routed through the network during normal operation (where all network 
     * components are active).
     * 
     * @param value the demand value
     * 
     * @throws IllegalArgumentException if the given demand value
     * is lower than or equal to zero.
     */
    public void setDemandValue(double value) {

        checkNotLowerOrEqualToZero(value, "demand value");

        _demandValue = value;
    }

    /**
     * Returns the individual routing unit of this demand.
     * 
     * @return the routing unit of this demand
     */
    public int getRoutingUnit() {

        return _routingUnit;
    }

    /**
     * Sets the individual routing unit of this demand.
     * With an integer routing model every routing path used for this demand 
     * must carry an integer multiple of the routing unit. 
     * Independent of the routing model, the total traffic rate of this 
     * demand is <tt>routingUnit</tt> times <tt>demandValue</tt>.  
     * 
     * @param unit the routing unit
     * 
     * @throws IllegalArgumentException if the given routing unit
     * is lower than or equal to zero
     */
    public void setRoutingUnit(int unit) {

        checkNotLowerOrEqualToZero(unit, "routing unit");

        _routingUnit = unit;
    }

    /**
     * Tests whether this demand has an admissible path with the specified ID.
     * 
     * @param pathId the ID of the path to look for
     * 
     * @return <tt>true</tt> if and only if this demand has an admissible 
     * path with the specified ID; <tt>false</tt> otherwise
     */
    public boolean hasAdmissiblePath(String pathId) {

        checkNotNull(pathId, "path id");

        return _admissiblePaths.containsKey(pathId);
    }

    /**
     * Removes the given admissible path from this demand.
     * 
     * @param path the path to remove
     * 
     * @return <tt>true</tt> if and only if the path was removed
     * from this demand; <tt>false</tt> otherwise
     */
    public boolean removeAdmissiblePath(AdmissiblePath path) {

        checkNotNull(path, "path");

        AdmissiblePath p = _admissiblePaths.get(path.getId());

        if(p == path) {
            _admissiblePaths.remove(path.getId());
            return true;
        }

        return false;
    }

    /**
     * Returns the number of admissible paths of this demand.
     * 
     * @return the number of admissible paths
     */
    public int admissiblePathCount() {

        return _admissiblePaths.size();
    }

    /**
     * Returns a <tt>Collection</tt> view of the admissible paths of this
     * demand. The returned collection is not modifiable, i.e., any attempt 
     * to modify the collection will cause an 
     * <tt>UnsupportedOperationException</tt>.
     *  
     * @return an unmodifiable collection view of the admissible paths
     * of this demand
     */
    public Collection<AdmissiblePath> admissiblePaths() {

        return Collections.unmodifiableCollection(_admissiblePaths.values());
    }

    /**
     * Returns the admissible path with the specified ID.
     * 
     * @param pathId the ID of the path
     * 
     * @return the admissible path with the specified ID; <tt>null</tt> if such
     * a path does not exist
     */
    public AdmissiblePath getAdmissiblePath(String pathId) {

        return _admissiblePaths.get(pathId);
    }

    /**
     * Returns a textual representation of this demand.
     * 
     * @return a textual representation of this demand
     */
    public String toString() {

        StringBuilder result = new StringBuilder();
        result.append("demand [\n");
        result.append(" id            = " + getId() + "\n");
        result.append(" source        = " + _first.getId() + "\n");
        result.append(" target        = " + _second.getId() + "\n");
        result.append(" routingUnit   = " + _routingUnit + "\n");
        result.append(" demandValue   = " + _demandValue + "\n");

        String maxPathLength = (_maxPathLength == 0) ? "UNLIMITED" : _maxPathLength
            + "";

        result.append(" maxPathLength = " + maxPathLength + "\n\n");
        result.append(" admissible paths [\n");

        for(AdmissiblePath path : _admissiblePaths.values()) {
            result.append(StringFormatUtils.indentByLine(path.toString(), 2) + "\n");
        }

        result.append(" ]\n");
        result.append("]");

        return result.toString();
    }
}
