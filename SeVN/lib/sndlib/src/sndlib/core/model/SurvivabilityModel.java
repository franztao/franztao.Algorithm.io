/*
 * $Id: SurvivabilityModel.java 206 2006-07-10 21:48:15Z roman.klaehne $
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
package sndlib.core.model;

/**
 * This enumeration declares the supported survivability models.
 * 
 * @author Roman Klaehne
 */
public enum SurvivabilityModel {

    /**
     * The routing of every demand must satisfy the conditions of 1+1 
     * dedicated path protection. That is, each demand must be routed on a 
     * single working path in the normal operating state, and a single 
     * link-disjoint backup path must be provided to protect the demand 
     * against single link failures.
     */
    ONE_PLUS_ONE_PROTECTION('P') {

        /**
         * @return <tt>false</tt>
         */
        public boolean isSharedProtection() {

            return false;
        }

        /**
         * @return <tt>true</tt>
         */
        public boolean isDedicatedProtection() {

            return true;
        }
    },

    /**
     * For each demand, a working path routing for the non-failure state
     * and an end-to-end backup path for each single link failure must
     * be provided. In each failure state, all non-affected working
     * paths must be maintained.  The backup capacity is shared among
     * the different demands as well as between the network states,
     * i.e., the backup path may depend on the specific link failure.
     * The capacity of failing working paths is released and can be
     * reused in failure states.
     */
    SHARED_PATH_PROTECTION('S') {

        /**
         * @return <tt>true</tt>
         */
        public boolean isSharedProtection() {

            return true;
        }

        /**
         * @return <tt>false</tt>
         */
        public boolean isDedicatedProtection() {

            return false;
        }

    },

    /**
     * For each demand, routings must be provided for the non-failure 
     * state as well as for all single link failure states. The 
     * routings of a demand in the different states are completely
     * independent of each other.
     */
    UNRESTRICTED_FLOW_RECONFIGURATION('U') {

        /**
         * @return <tt>true</tt>
         */
        public boolean isSharedProtection() {

            return true;
        }

        /**
         * @return <tt>false</tt>
         */
        public boolean isDedicatedProtection() {

            return false;
        }

    },

    /**
     * No survivability needs to be ensured in the routing.
     */
    NO_SURVIVABILITY('N') {

        /**
         * @return <tt>false</tt>
         */
        public boolean isSharedProtection() {

            return false;
        }

        /**
         * @return <tt>false</tt>
         */
        public boolean isDedicatedProtection() {

            return false;
        }
    };

    /**
     * Tests whether there is a survivability model with the specified name, 
     * ignoring case considerations.
     * 
     * @param name the name of the survivability model to look for
     * 
     * @return <tt>true</tt> if and only if there is an survivability model 
     * with the specified name; <tt>false</tt> otherwise
     */
    public static boolean hasEnumIgnoreCase(String name) {

        try {
            valueOfIgnoreCase(name);
        }
        catch (IllegalArgumentException iax) {
            return false;
        }
        return true;
    }

    /**
     * Returns the survivability model with the specified name, ignoring case 
     * considerations.
     * 
     * @param name the name of the suivivability model to look for
     * 
     * @return the survivability model with the specified name; <tt>null</tt> if
     * such a survivability model does not exist
     */
    public static SurvivabilityModel valueOfIgnoreCase(String name) {

        return SurvivabilityModel.valueOf(name.toUpperCase());
    }

    /**
     * Returns the survivability model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the survivability model to look for
     * 
     * @return the survivability model with the specified name; <tt>null</tt> if
     * such a survivability model does not exist
     */
    public static SurvivabilityModel valueOfShortCut(char shortCut) {

        for(SurvivabilityModel model : values()) {
            if(model._shortCut == shortCut) {
                return model;
            }
        }
        return null;
    }

    /**
     * Returns the shortcut of this survivability model.
     * 
     * @return the shortcut of this survivability model
     */
    public char getShortCut() {

        return _shortCut;
    }

    /**
     * Returns <tt>true</tt> if this survivability mechansim is a shared 
     * protection mechansim.
     * 
     * @return <tt>true</tt> if this survivability mechansim is a shared
     * protection mechansim
     */
    abstract public boolean isSharedProtection();

    /**
     * Returns <tt>true</tt> if this survivability mechansim is a dedicated
     * protection mechansim.
     * 
     * @return <tt>true</tt> if this survivability mechansim is a dedicated
     * protection mechansim
     */
    abstract public boolean isDedicatedProtection();

    /**
     * The shortcut of this survivability model.
     */
    private char _shortCut;

    /**
     * Constructs a new survivability model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the survivability model
     */
    private SurvivabilityModel(char shortCut) {

        _shortCut = shortCut;
    }
}
