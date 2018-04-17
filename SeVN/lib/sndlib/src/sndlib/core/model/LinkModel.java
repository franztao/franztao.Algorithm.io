/*
 * $Id: LinkModel.java 99 2006-07-04 14:05:16Z bzforlow $
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
 * This enumeration declares the supported link models.
 * 
 * @author Roman Klaehne
 */
public enum LinkModel {

    /**
     * Links are bidirectional, that is, the installed routing capacity can
     * be used in both directions to route communication demands.<br/> 
     * The maximum of the flows in both directions must not exceed the 
     * installed capacity. 
     */
    BIDIRECTED('B'),

    /**
     * Links are undirected, that is, the installed routing capacity can be 
     * used in both directions to route communication demands.<br/>
     * The sum of the flows in both directions must not exceed the 
     * installed capacity. 
     */
    UNDIRECTED('U'),

    /**
     * Links are directed, that is, the installed routing capacity can be 
     * used in the direction from the source to the target node.<br/>
     * The flow in this direction must not exceed the installed capacity. 
     */
    DIRECTED('D');

    /**
     * Tests whether there is an link model with the specified name, 
     * ignoring case considerations.
     * 
     * @param name the name of the link model to look for
     * 
     * @return <tt>true</tt> if and only if there is an link model with the
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
     * Returns the link model with the specified name, ignoring case 
     * considerations.
     * 
     * @param name the name of the link model to look for
     * 
     * @return the link model with the specified name; <tt>null</tt> if
     * such a link model does not exist
     */
    public static LinkModel valueOfIgnoreCase(String name) {

        return LinkModel.valueOf(name.toUpperCase());
    }

    /**
     * Returns the link model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the link model to look for
     * 
     * @return the link model with the specified name; <tt>null</tt> if
     * such a link model does not exist
     */
    public static LinkModel valueOfShortCut(char shortCut) {

        for(LinkModel model : values()) {
            if(model._shortCut == shortCut) {
                return model;
            }
        }
        return null;
    }

    /**
     * Returns the shortcut of this link model.
     * 
     * @return the shortcut of this link model
     */
    public char getShortCut() {

        return _shortCut;
    }

    /**
     * The shortcut of this link model.
     */
    private char _shortCut;

    /**
     * Constructs a new link model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the link model
     */
    private LinkModel(char shortCut) {

        _shortCut = shortCut;
    }
}

