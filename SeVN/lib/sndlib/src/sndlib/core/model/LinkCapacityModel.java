/*
 * $Id: LinkCapacityModel.java 99 2006-07-04 14:05:16Z bzforlow $
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
 * This enumeration declares the supported link capacity models.
 * 
 * @author Roman Klaehne
 */
public enum LinkCapacityModel {

    /**
     * Any non-negative (fractional or integer) capacity can be installed 
     * on the links. 
     */
    LINEAR_LINK_CAPACITIES('L'),

    /**
     * All non-negative integer combinations of the base capacities 
     * specified for the link are admissible. 
     */
    MODULAR_LINK_CAPACITIES('M'),

    /**
     * An explicit list of all admissible capacities on a link is provided. 
     * At most one of them may be installed, and only once.
     */
    EXPLICIT_LINK_CAPACITIES('E');

    /**
     * Tests whether there is an link capacity model with the specified name, 
     * ignoring case considerations.
     * 
     * @param name the name of the link capacity model to look for
     * 
     * @return <tt>true</tt> if and only if there is an link capacity model with the
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
     * Returns the link capacity model with the specified name, ignoring case 
     * considerations.
     * 
     * @param name the name of the link capacity model to look for
     * 
     * @return the link capacity model with the specified name; <tt>null</tt> if
     * such a link capacity model does not exist
     */
    public static LinkCapacityModel valueOfIgnoreCase(String name) {

        return LinkCapacityModel.valueOf(name.toUpperCase());
    }

    /**
     * Returns the link capacity model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the link capacity model to look for
     * 
     * @return the link capacity model with the specified name; <tt>null</tt> if
     * such a link capacity model does not exist
     */
    public static LinkCapacityModel valueOfShortCut(char shortCut) {

        for(LinkCapacityModel model : values()) {
            if(model._shortCut == shortCut) {
                return model;
            }
        }
        return null;
    }

    /**
     * Returns the shortcut of this link capacity model.
     * 
     * @return the shortcut of this link capacity model
     */
    public char getShortCut() {

        return _shortCut;
    }

    /**
     * The shortcut of this link capacity model.
     */
    private char _shortCut;

    /**
     * Constructs a new link capacity model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the link capacity model
     */
    private LinkCapacityModel(char shortCut) {

        _shortCut = shortCut;
    }
}

