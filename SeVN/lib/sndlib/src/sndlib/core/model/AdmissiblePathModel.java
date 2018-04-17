/*
 * $Id: AdmissiblePathModel.java 99 2006-07-04 14:05:16Z bzforlow $
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
 * This enumeration declares the supported admissible path models.
 * 
 * @author Roman Klaehne
 */
public enum AdmissiblePathModel {

    /**
     * For each demand, all paths satisfying the hop limits can be used. 
     * Any explicit list of admissible paths is ignored. 
     */
    ALL_PATHS('A'),

    /**
     * For each demand, an explicit list of admissible paths must be specified. 
     * Only these paths are admissible (provided that they satisfy the 
     * hop limits).
     */
    EXPLICIT_LIST('E');

    /**
     * Tests whether there is an admissible path model with the specified name, 
     * ignoring case considerations.
     * 
     * @param name the name of the admissible path model to look for
     * 
     * @return <tt>true</tt> if and only if there is an admissible path model 
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
     * Returns the admissible path model with the specified name, ignoring case 
     * considerations.
     * 
     * @param name the name of the admissible path model to look for
     * 
     * @return the admissible path model with the specified name; <tt>null</tt> if
     * such a admissible path model does not exist
     */
    public static AdmissiblePathModel valueOfIgnoreCase(String name) {

        return AdmissiblePathModel.valueOf(name.toUpperCase());
    }

    /**
     * Returns the admissible path model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the admissible path model to look for
     * 
     * @return the admissible path model with the specified name; <tt>null</tt> if
     * such a admissible path model does not exist
     */
    public static AdmissiblePathModel valueOfShortCut(char shortCut) {

        for(AdmissiblePathModel model : values()) {
            if(model._shortCut == shortCut) {
                return model;
            }
        }
        return null;
    }

    /**
     * Returns the shortcut of this admissible path model.
     * 
     * @return the shortcut of this admissible path model
     */
    public char getShortCut() {

        return _shortCut;
    }

    /**
     * The shortcut of this admissible path model.
     */
    private char _shortCut;

    /**
     * Constructs a new admissible path model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the admissible path model
     */
    private AdmissiblePathModel(char shortCut) {

        _shortCut = shortCut;
    }
}

