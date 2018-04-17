/*
 * $Id: FixedChargeModel.java 99 2006-07-04 14:05:16Z bzforlow $
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
 * This enumeration declares the supported fixed-charge models.
 * 
 * @author Roman Klaehne
 */
public enum FixedChargeModel {

    /**
     * Take the fixed-charge cost specification of the links into account.
     */
    YES('Y'),

    /**
     * Ignore the fixed-charge cost specification of the links,
     * i.e., assume fixed charge cost 0 for all links.
     */
    NO('N');

    /**
     * Tests whether there is an fixed-charge model with the specified name, 
     * ignoring case considerations.
     * 
     * @param name the name of the fixed-charge model to look for
     * 
     * @return <tt>true</tt> if and only if there is an fixed-charge model 
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
     * Returns the fixed-charge model with the specified name, ignoring case 
     * considerations.
     * 
     * @param name the name of the fixed-charge model to look for
     * 
     * @return the fixed-charge model with the specified name; <tt>null</tt> if
     * such a fixed-charge model does not exist
     */
    public static FixedChargeModel valueOfIgnoreCase(String name) {

        return FixedChargeModel.valueOf(name.toUpperCase());
    }

    /**
     * Returns the fixed-charge model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the fixed-charge model to look for
     * 
     * @return the fixed-charge model with the specified name; <tt>null</tt> if
     * such a fixed-charge model does not exist
     */
    public static FixedChargeModel valueOfShortCut(char shortCut) {

        for(FixedChargeModel model : values()) {
            if(model._shortCut == shortCut) {
                return model;
            }
        }
        return null;
    }

    /**
     * Returns the shortcut of this fixed-charge model.
     * 
     * @return the shortcut of this fixed-charge model
     */
    public char getShortCut() {

        return _shortCut;
    }

    /**
     * The shortcut of this fixed-charge model.
     */
    private char _shortCut;

    /**
     * Constructs a new fixed-charge model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the fixed-charge model
     */
    private FixedChargeModel(char shortCut) {

        _shortCut = shortCut;
    }
}

