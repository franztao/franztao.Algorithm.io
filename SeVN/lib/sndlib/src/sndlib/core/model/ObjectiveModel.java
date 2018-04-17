/*
 * $Id: ObjectiveModel.java 99 2006-07-04 14:05:16Z bzforlow $
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
 * This enumeration declares the supported objective models.
 * 
 * @author Roman Klaehne
 */
public enum ObjectiveModel {

    /**
     * Minimize total network cost, which is the sum of link capacity cost, 
     * fixed-charge cost and routing cost.
     */
    MINIMIZE_TOTAL_COST('T');

    /**
     * Tests whether there is an objective model with the specified name, 
     * ignoring case considerations.
     * 
     * @param name the name of the objective model to look for
     * 
     * @return <tt>true</tt> if and only if there is an objective model with the
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
     * Returns the objective model with the specified name, ignoring case 
     * considerations.
     * 
     * @param name the name of the objective model to look for
     * 
     * @return the objective model with the specified name; <tt>null</tt> if
     * such a objective model does not exist
     */
    public static ObjectiveModel valueOfIgnoreCase(String name) {

        return ObjectiveModel.valueOf(name.toUpperCase());
    }

    /**
     * Returns the objective model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the objective model to look for
     * 
     * @return the objective model with the specified name; <tt>null</tt> if
     * such a objective model does not exist
     */
    public static ObjectiveModel valueOfShortCut(char shortCut) {

        for(ObjectiveModel model : values()) {
            if(model._shortCut == shortCut) {
                return model;
            }
        }
        return null;
    }

    /**
     * Returns the shortcut of this objective model.
     * 
     * @return the shortcut of this objective model
     */
    public char getShortCut() {

        return _shortCut;
    }

    /**
     * The shortcut of this objective model.
     */
    private char _shortCut;

    /**
     * Constructs a new objective model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the objective model
     */
    private ObjectiveModel(char shortCut) {

        _shortCut = shortCut;
    }
}

