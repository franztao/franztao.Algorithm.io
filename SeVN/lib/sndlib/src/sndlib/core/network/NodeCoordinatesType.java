/*
 * $Id: NodeCoordinatesType.java 99 2006-07-04 14:05:16Z bzforlow $
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

package sndlib.core.network;

/**
 * Lists the types of node coordinates.
 * 
 * @author Roman Klaehne
 */
public enum NodeCoordinatesType {

    /**
     * Identifies geographical coordinates.<br/>
     * <br/>
     * 
     * If a node has geographical coordinates then its x-coordinate is 
     * interpreted as the geographical longitude in the range (-180, 180] 
     * and its y-coordinate is interpreted as the geographical latitude in 
     * the range [-90, 90].
     */
    GEOGRAPHICAL,

    /**
     * Identifies pixel coordinates.<br/>
     * <br/>
     * 
     * This type does not impose any restriction on the node coordinates.
     */
    PIXEL;
}

