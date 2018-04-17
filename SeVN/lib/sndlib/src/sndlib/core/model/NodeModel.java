/*
 * $Id: NodeModel.java 99 2006-07-04 14:05:16Z bzforlow $
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
 * This enumeration declares the supported node models.
 * 
 * @author Roman Klaehne
 */
public enum NodeModel {

    /**
     * No hardware like multiplexers, cross-connects, line-cards,
     * etc. is considered.
     */
    NO_NODE_HARDWARE('N'),

    /**
     * Hardware like multiplexers, cross-connects, line-cards, ports,
     * etc. is considered. This model is not yet supported but is planned for 
     * version 2.0 of SNDlib.
     */
    NODE_HARDWARE('Y');

    /**
     * Tests whether there is a node model with the specified name, ignoring
     * case considerations.
     * 
     * @param name the name of the node model to look for
     * 
     * @return <tt>true</tt> if and only if there is a node model with the
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
     * Returns the node model with the specified name, ignoring case 
     * considerations.
     * 
     * @param name the name of the node model to look for
     * 
     * @return the node model with the specified name; <tt>null</tt> if
     * such a node model does not exist
     */
    public static NodeModel valueOfIgnoreCase(String name) {

        return NodeModel.valueOf(name.toUpperCase());
    }

    /**
     * Returns the node model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the node model to look for
     * 
     * @return the node model with the specified name; <tt>null</tt> if
     * such a node model does not exist
     */
    public static NodeModel valueOfShortCut(char shortCut) {

        for(NodeModel model : values()) {
            if(model._shortCut == shortCut) {
                return model;
            }
        }
        return null;
    }

    /**
     * Returns the shortcut of this node model.
     * 
     * @return the shortcut of this node model
     */
    public char getShortCut() {

        return _shortCut;
    }

    /**
     * The shortcut of this node model.
     */
    private char _shortCut;

    /**
     * Constructs a new node model with the specified shortcut.
     * 
     * @param shortCut the shortcut of the node model
     */
    private NodeModel(char shortCut) {

        _shortCut = shortCut;
    }
}

