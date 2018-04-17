/*
 * $Id: DemandFlow.java 99 2006-07-04 14:05:16Z bzforlow $
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

import sndlib.core.network.Demand;

/**
 * A <tt>DemandFlow</tt> is used to represent the flow between the source
 * and the target node of a single demand in a network with respect to a 
 * specific solution.<br/><br/>
 * The demand flow is defined as the sum of the flow values of all paths 
 * used to route the demand.
 * The positive flow is routed from the source to the target of a
 * <tt>Demand</tt>, the negative flow in the opposite direction.
 * <br/><br/>
 * 
 * @see sndlib.core.network.Demand
 * @see SolvedProblem
 * 
 * @author Roman Klaehne
 */
public class DemandFlow extends Flow {

    /**
     * The demand between whose source and target node this flow is routed.
     */
    private Demand _demand;

    /**
     * Constructs a new flow object for the given demand.
     * 
     * @param demand the demand between whose source and target node the 
     * flow is routed
     */
    DemandFlow(Demand demand) {

        super();
        _demand = demand;
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
}

