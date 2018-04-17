/*
 * $Id: Flow.java 163 2006-07-08 16:19:05Z roman.klaehne $
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

/**
 * This abstract class is used to represent a flow value between two nodes 
 * in a network.<br/><br/>
 * 
 * A <tt>Flow</tt> object provides a positive and a negative flow
 * component depending on the direction into which the flow runs.  
 * The positive flow is routed from the source to the target of a
 * <tt>Demand</tt>, the negative flow in the opposite direction.
 * 
 * @author Roman Klaehne
 */
abstract class Flow {

    /**
     * The positive direction flow.
     */
    private double _positiveFlow;

    /**
     * The negative direction flow.
     */
    private double _negativeFlow;

    /**
     * Constructs a new flow object. The positive and negative flow
     * will be initialized with zero.
     */
    protected Flow() {

        _positiveFlow = 0.0;
        _negativeFlow = 0.0;
    }

    /**
     * Returns the flow value which runs in positive direction.
     * 
     * @return the positive direction flow
     */
    public double getPositiveFlow() {

        return _positiveFlow;
    }

    /**
     * Returns the flow value which runs in negative direction.
     * 
     * @return the negative direction flow
     */
    public double getNegativeFlow() {

        return _negativeFlow;
    }

    /**
     * Returns the sum of both, the positive and negative direction flow.
     * 
     * @return the sum of positive and negative direction flow
     */
    public double getTotalFlow() {

        return _positiveFlow + _negativeFlow;
    }

    /**
     * Increases the positive direction flow by the given amount.
     * 
     * @param amount the amount by which the positive flow is increased
     */
    void increasePosistiveFlow(double amount) {

        _positiveFlow += amount;
    }

    /**
     * Increases the negative direction flow by the given amount.
     * 
     * @param amount the amount by which the negative flow is increased
     */
    void increaseNegativeFlow(double amount) {

        _negativeFlow += amount;
    }

    /**
     * Decreases the positive direction flow by the given amount.
     * 
     * @param amount the amount by which the positive flow is decreased
     */
    void decreasePosistiveFlow(double amount) {

        _positiveFlow -= amount;
    }

    /**
     * Decreases the negative direction flow by the given amount.
     * 
     * @param amount the amount by which the negative flow is decreased
     */
    void decreaseNegativeFlow(double amount) {

        _negativeFlow -= amount;
    }

    /**
     * Returns a texutal representation of this flow object.
     * 
     * @return a textual representation of this flow object
     */
    public String toString() {

        return "posFlow = " + _positiveFlow + "; negFlow = " + _negativeFlow;
    }
}