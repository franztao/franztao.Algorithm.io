/*
 * $Id: HopLimitModel.java 99 2006-07-04 14:05:16Z bzforlow $
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
 * This enumeration declares the supported hop-limit models.
 * 
 * @author Roman Klaehne
 */
public enum HopLimitModel {

    /**
     * Use only those admissible paths satisfying the hop limit specified
     * for each demand. The hop limit applies regardless of whether the 
     * admissible paths are given by an explicit list or implicitly.
     */
    INDIVIDUAL_HOP_LIMITS('Y'),

    /**
     * Ignore the hop limit specification of the demands, i.e.,
     * routing paths are not length-restricted.
     */
    IGNORE_HOP_LIMITS('N');

    /**
     * Tests whether there is an hop-limit model with the specified name, 
     * ignoring case considerations.
     * 
     * @param name the name of the hop-limit model to look for
     * 
     * @return <tt>true</tt> if and only if there is an hop-limit model with the
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
     * Returns the hop-limit model with the specified name, ignoring case 
     * considerations.
     * 
     * @param name the name of the hop-limit model to look for
     * 
     * @return the hop-limit model with the specified name; <tt>null</tt> if
     * such a hop-limit model does not exist
     */
    public static HopLimitModel valueOfIgnoreCase(String name) {

        return HopLimitModel.valueOf(name.toUpperCase());
    }

    /**
     * Returns the hop-limit model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the hop-limit model to look for
     * 
     * @return the hop-limit model with the specified name; <tt>null</tt> if
     * such a hop-limit model does not exist
     */
    public static HopLimitModel valueOfShortCut(char shortCut) {

        for(HopLimitModel model : values()) {
            if(model._shortCut == shortCut) {
                return model;
            }
        }
        return null;
    }

    /**
     * Returns the shortcut of this hop-limit model.
     * 
     * @return the shortcut of this hop-limit model
     */
    public char getShortCut() {

        return _shortCut;
    }

    /**
     * The shortcut of this hop-limit model.
     */
    private char _shortCut;

    /**
     * Constructs a new hop-limit model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the hop-limit model
     */
    private HopLimitModel(char shortCut) {

        _shortCut = shortCut;
    }
}

