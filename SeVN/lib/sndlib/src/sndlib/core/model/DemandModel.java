/*
 * $Id: DemandModel.java 99 2006-07-04 14:05:16Z bzforlow $
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
 * This enumeration declares the supported demand models.
 * 
 * @author Roman Klaehne
 */
public enum DemandModel {

    /**
     * Each demand is routed without direction between its end-nodes.<br/>
     * This case corresponds to a triangular traffic matrix.
     */
    UNDIRECTED('U'),

    /**
     * Each demand is routed from source to target.<br/>
     * This case corresponds to an unsymmetric traffic matrix. 
     */
    DIRECTED('D');

    /**
     * Tests whether there is a demand model with the specified name, 
     * ignoring case considerations.
     * 
     * @param name the name of the demand model to look for
     * 
     * @return <tt>true</tt> if and only if there is an demand model with the
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
     * Returns the demand model with the specified name, ignoring case 
     * considerations.
     * 
     * @param name the name of the demand model to look for
     * 
     * @return the demand model with the specified name; <tt>null</tt> if
     * such a demand model does not exist
     */
    public static DemandModel valueOfIgnoreCase(String name) {

        return DemandModel.valueOf(name.toUpperCase());
    }

    /**
     * Returns the demand model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the demand model to look for
     * 
     * @return the demand model with the specified name; <tt>null</tt> if
     * such a demand model does not exist
     */
    public static DemandModel valueOfShortCut(char shortCut) {

        for(DemandModel model : values()) {
            if(model._shortCut == shortCut) {
                return model;
            }
        }
        return null;
    }

    /**
     * Returns the shortcut of this demand model.
     * 
     * @return the shortcut of this demand model
     */
    public char getShortCut() {

        return _shortCut;
    }

    /**
     * The shortcut of this demand model.
     */
    private char _shortCut;

    /**
     * Constructs a new demand model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the demand model
     */
    private DemandModel(char shortCut) {

        _shortCut = shortCut;
    }
}

