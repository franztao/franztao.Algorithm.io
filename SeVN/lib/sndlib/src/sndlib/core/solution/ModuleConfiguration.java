/*
 * $Id: ModuleConfiguration.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import com.atesio.utils.ArgChecker;

/**
 * This class represents the configuration of a single capacity module of
 * a single link in a specific {@link Solution}.
 * <br/><br/>
 * 
 * A <tt>ModuleConfiguration</tt> corresponds to a 
 * {@link sndlib.core.network.CapacityModule}, identified by its capacity.
 * 
 * It only consists of the information how many times the corresponding 
 * <tt>CapacityModule</tt> is installed on the link.
 * 
 * @see LinkConfiguration
 * @see sndlib.core.network.CapacityModule
 * @see Solution
 * 
 * @author Roman Klaehne
 */
final public class ModuleConfiguration {

    /**
     * The capacity of the corresponding module.
     */
    private double _moduleCapacity;

    /**
     * The install count of the module.
     */
    private double _installCount;

    /**
     * Constructs a new configuration for a module with the specified
     * capacity.
     * 
     * @param moduleCapacity the capacity of the module
     * @param installCount the install count of the module
     * 
     * @throws IllegalArgumentExcpeption if the module capacity or the 
     * install count is lower than zero
     */
    public ModuleConfiguration(double moduleCapacity, double installCount) {

        ArgChecker.checkNotLowerThanZero(moduleCapacity, "module capacity");
        ArgChecker.checkNotLowerThanZero(installCount, "module install count");

        _moduleCapacity = moduleCapacity;
        _installCount = installCount;
    }

    /**
     * Returns the capacity of the module.
     * 
     * @return the capacity of the module
     */
    public double getModuleCapacity() {

        return _moduleCapacity;
    }

    /**
     * Returns the install count of the module.
     * 
     * @return the install count of the module
     */
    public double getInstallCount() {

        return _installCount;
    }

    /**
     * Returns a textual representation of this module configuration.
     * 
     * @return a textual representation
     */
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append("moduleConfiguration [\n");
        result.append(" moduleCapacity = " + _moduleCapacity + "\n");
        result.append(" installCount   = " + _installCount + "\n");
        result.append("]");

        return result.toString();
    }
}

