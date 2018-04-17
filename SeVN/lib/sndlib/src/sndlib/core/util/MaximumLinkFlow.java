/*
 * $Id$
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

import sndlib.core.network.Link;
import sndlib.core.problem.OperatingState;

/**
 * This is a simple class containing the maximum flows on a specific link, 
 * together with the states in which the flow values are assumed.
 * 
 * @see SolutionUtils#calculateMaximumLinkFlow(Link, SolvedProblem) 
 * 
 * @author Roman Klaehne
 */
public class MaximumLinkFlow {

    /**
     * The maximum positive flow on the link.
     */
    double maxPositiveFlow = 0.0;

    /**
     * The maximum negative flow on the link.
     */
    double maxNegativeFlow = 0.0;

    /**
     * The maximum total (positive + negative) flow on the link.
     */
    double maxTotalFlow = 0.0;

    /**
     * The state in which the maximum positive flow is assumed.
     */
    OperatingState maxPositiveFlowAssumedState = null;

    /**
     * The state in which the maximum negative flow is assumed.
     */
    OperatingState maxNegativeFlowAssumedState = null;

    /**
     * The state in which the maximum total flow is assumed.
     */
    OperatingState maxTotalFlowAssumedState = null;

    /**
     * The link on which the flow is routed.
     */
    private Link link;

    /**
     * Constructs a new statistic object for the specified link.
     * 
     * @param link the link
     */
    MaximumLinkFlow(Link link) {

        this.link = link;
    }

    /**
     * Returns the link on which the flow is routed.
     * 
     * @return the link on which the flow is routed
     */
    public Link getLink() {

        return link;
    }

    /**
     * Returns the maximum negative flow on the link.
     * 
     * @return the maximum negative flow on the link
     */
    public double getMaxNegativeFlow() {

        return maxNegativeFlow;
    }

    /**
     * Returns the state in which the maximum negative flow is assumed.
     * 
     * @return the state in which the maximum negative flow is assumed
     */
    public OperatingState getMaxNegativeFlowAssumedState() {

        return maxNegativeFlowAssumedState;
    }

    /**
     * Returns the maximum positive flow on the link.
     * 
     * @return the maximum positive flow on the link
     */
    public double getMaxPositiveFlow() {

        return maxPositiveFlow;
    }

    /**
     * Returns the state in which the maximum positive flow is assumed.
     * 
     * @return the state in which the maximum positive flow is assumed
     */
    public OperatingState getMaxPositiveFlowAssumedState() {

        return maxPositiveFlowAssumedState;
    }

    /**
     * Returns the maximum total flow on the link.
     * 
     * @return the maximum total flow on the link
     */
    public double getMaxTotalFlow() {

        return maxTotalFlow;
    }

    /**
     * Returns the state in which the maximum total flow is assumed.
     * 
     * @return the state in which the maximum total flow is assumed
     */
    public OperatingState getMaxTotalFlowAssumedState() {

        return maxTotalFlowAssumedState;
    }
}
