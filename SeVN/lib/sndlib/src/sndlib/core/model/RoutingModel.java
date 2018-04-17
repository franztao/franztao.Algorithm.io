/*
 * $Id: RoutingModel.java 99 2006-07-04 14:05:16Z bzforlow $
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
 * This enumeration declares the supported routing models.
 * 
 * @author Roman Klaehne
 */
public enum RoutingModel {

    /**
     * Any fraction of a demand can be routed on any admissible path for 
     * the demand.<br/>
     * A bifurcated routing is allowed. 
     */
    CONTINUOUS('C'),

    /**
     * Integer multiples of the routing unit of a demand can be routed on 
     * any admissible path for the demand.<br/>
     * A bifurcated routing is allowed. 
     */
    INTEGER('I'),

    /**
     * Each demand must be completely routed over a single admissible path.
     */
    SINGLE_PATH('S'),

    /**
     * This model is not yet supported but is planned for 
     * version 2.0 of SNDlib.
     */
    OSPF_SINGLE_PATH('O'),

    /**
     * This model is not yet supported but is planned for 
     * version 2.0 of SNDlib.
     */
    OSPF_EQUAL_COST_MULTI_PATH('E');

    /**
     * Tests whether there is an routing model with the specified name, 
     * ignoring case considerations.
     * 
     * @param name the name of the routing model to look for
     * 
     * @return <tt>true</tt> if and only if there is an routing model with the
     * specified name; <tt>false</tt> otherwise
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
     * Returns the routing model with the specified name, ignoring case 
     * considerations.
     * 
     * @param name the name of the routing model to look for
     * 
     * @return the routing model with the specified name; <tt>null</tt> if
     * such a routing model does not exist
     */
    public static RoutingModel valueOfIgnoreCase(String name) {

        return RoutingModel.valueOf(name.toUpperCase());
    }
    
    /**
     * Returns the routing model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the routing model to look for
     * 
     * @return the routing model with the specified name; <tt>null</tt> if
     * such a routing model does not exist
     */
    public static RoutingModel valueOfShortCut(char shortCut) {

        for(RoutingModel model : values()) {
            if(model._shortCut == shortCut) {
                return model;
            }
        }
        return null;
    }

    /**
     * Returns the shortcut of this routing model.
     * 
     * @return the shortcut of this routing model
     */
    public char getShortCut() {

        return _shortCut;
    }

    /**
     * The shortcut of this routing model.
     */
    private char _shortCut;

    /**
     * Constructs a new routing model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the routing model
     */
    private RoutingModel(char shortCut) {

        _shortCut = shortCut;
    }
}

