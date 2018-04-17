/*
 * $Id: DemandRouting.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sndlib.core.problem.OperatingState;

import com.atesio.utils.ArgChecker;
import com.atesio.utils.StringFormatUtils;

/**
 * This class represents a routing for a single traffic 
 * {@link sndlib.core.network.Demand} belonging to a specific 
 * {@link Solution}.<br/><br/>
 * 
 * A demand routing specifies the operating state in which it is valid and a 
 * set of <tt>FlowPath</tt>s defining the whole routing. Each of the flow 
 * paths has a flow value and a path on which the flow is routed.<br/><br/>
 * 
 * For constructing a new <tt>DemandRouting</tt> a parent 
 * <tt>Solution</tt> is needed.
 * 
 * @see Solution
 * @see Solution#newRouting(String, OperatingState)
 * @see FlowPath
 * @see sndlib.core.network.Demand
 * @see OperatingState
 * 
 * @author Roman Klaehne
 */
final public class DemandRouting {

    /**
     * The ID of the corresponding network demand.
     */
    private String _demandId;

    /**
     * The network operating state in which this demand routing is used.
     */
    private OperatingState _state;

    /**
     * The flow paths constituting this demand routing.
     */
    private List<FlowPath> _flowPaths;

    /**
     * For the specified demand constructs a new <tt>DemandRouting</tt> which 
     * is used when the network is in the given state.
     * 
     * @param demandId the ID of the corresponding demand
     * @param state the operating state
     */
    DemandRouting(String demandId, OperatingState state) {

        _demandId = demandId;
        _state = state;
        _flowPaths = new ArrayList<FlowPath>();
    }

    /**
     * Returns the ID of the corresponding network demand.
     * 
     * @return the ID of the corresponding network demand.
     */
    public String getDemandId() {

        return _demandId;
    }

    /**
     * Returns the operating state in which this demand routing is used.
     *  
     * @return the operating state in which this demand routing is used
     */
    public OperatingState getState() {

        return _state;
    }

    /**
     * Adds the given flow path to this demand routing.
     * 
     * @param flowPath the flow path to add
     */
    void addFlowPath(FlowPath flowPath) {

        _flowPaths.add(flowPath);
    }

    /**
     * Returns the number of flow paths in this demand routing.
     * <br/>
     * A flow path count greater than one indicates that this demand routing
     * is a bifurcated routing.
     * 
     * @return the number of flow paths in this demand routing
     */
    public int flowPathCount() {

        return _flowPaths.size();
    }

    /**
     * Returns a <tt>List</tt> view of the flow paths in this demand routing.
     * <br/><br/>
     * 
     * The returned list is unmodifiable. That means that each attempt 
     * to modify the list will cause an 
     * <tt>UnsupportedOperationException</tt>.
     * 
     * @return an unmodifiable list view of the flow paths in this demand 
     * routing.
     */
    public List<FlowPath> flowPaths() {

        return Collections.unmodifiableList(_flowPaths);
    }

    /**
     * Tests whether one of the flow paths in this demand routing traverses 
     * the specified link.
     * 
     * @param linkId the ID of the link
     * 
     * @return <tt>true</tt> if and only if one of the flow paths in this 
     * demand routing traverses the specified link; <tt>false</tt> otherwise
     */
    public boolean containsLink(String linkId) {

        ArgChecker.checkNotNull(linkId, "link ID");

        for(FlowPath flowPath : _flowPaths) {
            if(flowPath.containsLink(linkId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a textual representation of this demand routing.
     * 
     * @return a textual representation
     */
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append("routing [\n");
        result.append(" demandId   = " + _demandId + "\n\n");
        result.append(" flowPaths [\n");

        for(FlowPath flowPath : _flowPaths) {
            result.append(StringFormatUtils.indentByLine(flowPath.toString(), 2)
                + "\n");
        }

        result.append(" ]\n");
        result.append("]");
        return result.toString();
    }
}
