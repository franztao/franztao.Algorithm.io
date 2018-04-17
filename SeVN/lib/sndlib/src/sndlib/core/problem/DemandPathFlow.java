/*
 * $Id: DemandPathFlow.java 124 2006-07-06 20:03:51Z roman.klaehne $
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

import java.util.List;

import sndlib.core.network.Demand;

/**
 * A <tt>DemandPathFlow</tt> represents the flow on a single path incurred by 
 * a certain demand w.r.t. a specific solution.<br/><br/>
 * 
 * It provides a positive and a negative flow component depending on the
 * direction into which the flow runs.
 * If the link model of the network design problem is DIRECTED, then there is
 * only a positive flow. If it is UNDIRECTED, no direction is taken into 
 * account. For a BIDIRECTED link model the flow in each direction has to be 
 * considered separately.  
 * 
 * @see sndlib.core.network.Demand
 * @see SolvedProblem
 * 
 * @author Roman Klaehne
 */
public class DemandPathFlow extends Flow {
    
    /**
     * The demand incurring the flow on the path.
     */
    private Demand _demand;
    
    /**
     * The path on which the flow runs.
     */
    private List<String> _path;
    
    
    /**
     * Constructs a new flow object for the given demand and path.
     * 
     * @param demand the demand incurring the flow on the path
     * @param path the path on which the flow runs
     */
    DemandPathFlow(Demand demand, List<String> path) {
        
        _demand = demand;
        _path = path;
    }

    /**
     * Returns the demand between whose source and target node this 
     * flow is routed.
     * 
     * @return the demand between whose source and target node the 
     * flow is routed
     */
    public Demand getDemand() {

        return _demand;
    }

    /**
     * Returns the ID of the demand between whose source and target node this 
     * flow is routed.
     * 
     * @return the ID of the demand between whose source and target 
     * node the flow is routed
     */
    public String getDemandId() {

        return _demand.getId();
    }
    
    /**
     * Returns the path on which this flow runs.
     * 
     * @return the path on which this flow runs
     */
    public List<String> getPath() {
        
        return _path;
    }
}
