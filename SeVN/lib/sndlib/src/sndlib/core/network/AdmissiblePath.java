/*
 * $Id: AdmissiblePath.java 99 2006-07-04 14:05:16Z bzforlow $
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a single admissible path for a {@link Demand}.
 * <br/><br/>
 * 
 * In SNDlib a path is formulated by a sequence of {@link Link}s.
 * <br/><br/>
 * 
 * For constructing a new <tt>AdmissiblePath</tt> a parent <tt>Network</tt> 
 * and a parent <tt>Demand</tt> is needed.
 * <br/><br/>
 * 
 * An <tt>AdmissiblePath</tt> maintains the following invariants:
 * 
 * <ul>
 *  <li>
 *   all links in the path are contained in the parent network of 
 *   its parent demand
 *  </li>
 *  <li>
 *   if a link is removed from the parent network of the path's parent demand,
 *   then all occurences of that link will be also removed from the path.<br/>
 *   If the path is empty after removing a link then that path will be removed 
 *   from its parent demand.
 *  </li> 
 * </ul>
 * 
 * Whether or not an <tt>AdmissiblePath</tt> is really a path, depends among
 * others on the network {@link sndlib.core.model.Model}, and is not checked 
 * by this class itself.
 * 
 * @see Demand
 * @see Network#newAdmissiblePath(String, List, Demand)
 * 
 * @author Roman Klaehne
 */
final public class AdmissiblePath {

    /**
     * The list of links in this path.
     */
    private List<Link> _linkList;

    /**
     * The ID of the path.
     */
    private String _id;

    /**
     * Constructs a new admissible path from the given list of links.
     * 
     * @param pathId the ID of the path
     * @param links the sequence of links
     */
    AdmissiblePath(String pathId, List<Link> links) {

        _id = pathId;
        _linkList = new LinkedList<Link>(links);
    }

    /**
     * Removes all occurences of the given link from this path.
     * 
     * @param link the link to remove
     * 
     * @return <tt>true</tt> if and only if at least one occurence 
     * of the given link was removed; <tt>false</tt> otherwise
     */
    boolean removeLink(Link link) {

        List<Link> remainingLinks = new ArrayList<Link>();

        for(Link l : _linkList) {
            if(l != link) {
                remainingLinks.add(l);
            }
        }
        boolean removedAtLeastOne = _linkList.size() != remainingLinks.size();

        _linkList = remainingLinks;

        return removedAtLeastOne;
    }

    /**
     * Returns the number of links in this path.
     * 
     * @return the number of links
     */
    public int linkCount() {

        return _linkList.size();
    }

    /**
     * Returns a <tt>Collection</tt> view of the links in this path.
     * <br/><br/>
     * 
     * The returned collection is unmodifiable. That means that each attempt to 
     * modify the collection will cause an <tt>UnsupportedOperationException</tt>.
     *  
     * @return an unmodifiable collection view of the links in this path
     */
    public List<Link> links() {

        return Collections.unmodifiableList(_linkList);
    }

    /**
     * Returns the link at the specified position in the path.
     * 
     * @param index the index of the link
     * 
     * @return the link at the specified index
     * 
     * @throws IndexOutOfBoundsException if the specified index is out
     * of bounds
     */
    public Link linkAt(int index) {

        return _linkList.get(index);
    }

    /**
     * Returns the ID of this path.
     * 
     * @return the ID of this path
     */
    public String getId() {

        return _id;
    }

    /**
     * Returns a textual representation of this admissible path.
     * 
     * @return a textual representation
     */
    public String toString() {

        StringBuilder result = new StringBuilder();
        result.append("path [\n");
        result.append(" id    = " + getId() + "\n");
        result.append(" links = [");

        for(Link link : _linkList) {
            result.append(" " + link.getId());
        }

        result.append(" ]\n");
        result.append("]");

        return result.toString();
    }
}
