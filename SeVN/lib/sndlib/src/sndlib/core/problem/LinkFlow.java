/*
 * $Id: LinkFlow.java 99 2006-07-04 14:05:16Z bzforlow $
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

/**
 * A <tt>LinkFlow</tt> is used to represent the flow on a single link in a 
 * network with respect to a specific solution.<br/><br/>
 * 
 * It provides a positive and a negative flow component depending on the
 * direction into which the flow runs.
 * If the link model of the network design problem is DIRECTED, then there is 
 * only a positive flow. If it is UNDIRECTED, no direction is taken into 
 * account. For a BIDIRECTED link model the flow in each direction has to be 
 * considered separately.  
 * 
 * @see sndlib.core.network.Link
 * @see SolvedProblem
 * 
 * @author Roman Klaehne
 */
public class LinkFlow extends Flow {

    /**
     * The link on which this flow runs.
     */
    private Link _link;

    /**
     * Constructs a new flow object for the given link.
     * 
     * @param link the link on which the flow runs.
     */
    LinkFlow(Link link){

        super();
        _link = link;
    }

    /**
     * Returns the link on which this flow runs.
     * 
     * @return the link on which this flow runs
     */
    public Link getLink() {
        
        return _link;
    }
    
    /**
     * Returns the ID of the link on which this flow runs.
     * 
     * @return the ID of the link on which this flow runs
     */
    public String getLinkId() {
        
        return _link.getId();
    }
}

