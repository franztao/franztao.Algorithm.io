/*
 * $Id: FlowPath.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import com.atesio.utils.ArgChecker;

/**
 * This class represents a single flow path of a {@link DemandRouting}
 * of a specific {@link Solution}.<br/><br/>
 * 
 * A <tt>FlowPath</tt> consists of a flow value and a path on which the 
 * flow is routed.<br/><br/>
 *
 * For constructing a new <tt>FlowPath</tt> a parent <tt>Solution</tt> and a 
 * parent <tt>DemandRouting</tt> is needed.
 * 
 * @see DemandRouting
 * @see Solution#newFlowPath(DemandRouting, double, List)
 * @see Solution
 * @see sndlib.core.network.Demand
 * 
 * @author Roman Klaehne
 */
final public class FlowPath {

    /**
     * The flow path value.
     */
    private double _flowPathValue;

    /**
     * The ID's of the links forming the path.
     */
    private List<String> _linkIds;

    /**
     * Constructs a new <tt>FlowPath</tt> with the specified flow value and
     * the list of link ID's forming the path.
     * 
     * @param flowPathValue the flow value
     * @param linkIds the ID's of the links forming the path
     */
    FlowPath(double flowPathValue, List<String> linkIds) {

        _flowPathValue = flowPathValue;
        _linkIds = new ArrayList<String>(linkIds);
    }

    /**
     * Returns the flow value of this flow path.
     * 
     * @return the flow value
     */
    public double flowPathValue() {

        return _flowPathValue;
    }

    /**
     * Returns the length of the path respectively the number of links
     * forming the path.
     * 
     * @return the length of the path
     */
    public int linkIdCount() {

        return _linkIds.size();
    }

    /**
     * Returns a <tt>List</tt> view of the ID's of the links forming the path.
     * <br/><br/>
     * 
     * The returned list is unmodifiable. That means that each attempt to 
     * modify the collection will cause an 
     * <tt>UnsupportedOperationException</tt>.
     * 
     * @return an unmodifiable list view of the ID's of the links forming
     * the path
     */
    public List<String> linkIds() {

        return Collections.unmodifiableList(_linkIds);
    }

    /**
     * Returns <tt>true</tt> if this flow path traverses the specified link.
     * 
     * @param linkId the ID of the link
     * 
     * @return <tt>true</tt> if and only if this flow path traverses the 
     * specified link; <tt>false</tt> otherwise
     */
    public boolean containsLink(String linkId) {
        
        ArgChecker.checkNotNull(linkId, "link ID");
        
        for(String lid : _linkIds) {
            if(lid.equals(linkId)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns a textual representation of this flow path.
     * 
     * @return a textual representation
     */
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append("flowPath [\n");
        result.append(" flowPathValue = " + _flowPathValue + "\n");
        result.append(" linkIds       = [");

        for(String linkId : _linkIds) {
            result.append(" " + linkId);
        }

        result.append(" ]\n");
        result.append("]");
        return result.toString();
    }
}

