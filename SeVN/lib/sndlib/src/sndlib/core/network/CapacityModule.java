/*
 * $Id: CapacityModule.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import com.atesio.utils.ArgChecker;

/**
 * This class represents a capacity module which can be installed on a link
 * in addition to its pre-installed capacity.
 * <br/><br/>
 * 
 * A <tt>CapacityModule</tt> consists of a capacity together with the 
 * cost incurred per unit of the module.
 * <br/><br/>
 * 
 * @see Link
 * @see Link#addModule(CapacityModule)
 * 
 * @author Roman Klaehne
 */
final public class CapacityModule {

    /**
     * The capacity of this module.
     */
    private double _capacity;

    /**
     * The cost of this module.
     */
    private double _cost;

    /**
     * Constructs a new capacity module with the specified capacity and cost.
     * 
     * @param capacity the capacity of the module
     * @param cost the cost of the module
     */
    public CapacityModule(double capacity, double cost) {

        ArgChecker.checkNotLowerThanZero(capacity, "module capacity");
        ArgChecker.checkNotLowerThanZero(cost, "module capacity cost");

        _capacity = capacity;
        _cost = cost;
    }

    /**
     * Returns the capacity of this module.
     * 
     * @return the capacity of this module
     */
    public double getCapacity() {

        return _capacity;
    }

    /**
     * Returns the cost of this module.
     * 
     * @return the cost of this module
     */
    public double getCost() {

        return _cost;
    }

    /**
     * Returns a textual representation of this module.
     * 
     * @return a textual representation
     */
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append("module [\n");
        result.append(" capacity = " + _capacity + "\n");
        result.append(" cost     = " + _cost + "\n");
        result.append("]");

        return result.toString();
    }
}

