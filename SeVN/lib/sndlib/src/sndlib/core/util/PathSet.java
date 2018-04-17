/*
 * $Id: PathSet.java 332 2007-03-29 15:55:29Z roman.klaehne $
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
package sndlib.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import sndlib.core.model.LinkModel;

/**
 * This class represents a set containing pairwise different paths. A path is
 * recognized as a list of link ID's referencing the network links which that 
 * path traverses.<br/><br/>
 * 
 * Whether two paths are equal depends on the link model. If it is 
 * <tt>UNDIRECTED</tt> the direction of the paths is of no importance.
 * Otherwise also the direction is taken into account and the two paths are 
 * equals if they traverse the same sequence of links.
 * 
 * @author Roman Klaehne
 */
public class PathSet implements Set<List<String>> {

    /**
     * The backed set of paths.
     */
    private Set<List<String>> _pathSet;

    /**
     * Compares the paths with respect to a specific link model.
     */
    public static class PathComparator implements Comparator<List<String>> {

        private LinkModel _linkModel;

        /**
         * Constructs a new path comparator. The paths will be compared w.r.t.
         * the specified link model.
         * 
         * @param linkModel the link model
         */
        public PathComparator(LinkModel linkModel) {

            _linkModel = linkModel;
        }

        /**
         * Compares the given two paths.<br/><br/>
         * 
         * Whether two paths are equal depends on the link model. If it is 
         * <tt>UNDIRECTED</tt> the direction of the paths is of no importance.
         * Otherwise also the direction is taken into account and the two 
         * paths are equals if they traverse the same sequence of links.<br/>
         * <br/>
         * 
         * @param p1 one path
         * @param p2 another path
         * 
         * @return <tt>0</tt> if the two paths are equal; a negative or a 
         * positive value otherwise, but in a consistent manner to satisfy
         * the contract of {@link Comparator#compare(Object, Object)}.
         */
        public int compare(List<String> p1, List<String> p2) {

            if(p1.equals(p2)) {
                return 0;
            }

            if(_linkModel == LinkModel.UNDIRECTED) {
                p1 = new ArrayList<String>(p1);
                Collections.reverse(p1);
                if(p1.equals(p2)) {
                    return 0;
                }
            }

            if(p1.size() != p2.size()) {
                return p1.size() - p2.size();
            }

            int pathLength = p1.size();
            for(int i = 0; i < pathLength - 1; ++i) {
                int compareResult = p1.get(i).compareTo(p2.get(i));
                if(compareResult != 0) {
                    return compareResult;
                }
            }
            return p1.get(pathLength - 1).compareTo(p2.get(pathLength - 1));
        }
    }

    /**
     * Constructs a new path set. The paths in the set are compared with 
     * respect to the specified link model.<br/>
     * If the link model is <tt>UNDIRECTED</tt> the direction of the paths 
     * is of no importance.<br/>
     * Otherwise also the direction is taken into account and the two paths 
     * are equals if they traverse the same sequence of links.
     * 
     * @param linkModel the link model
     */
    public PathSet(LinkModel linkModel) {

        _pathSet = new TreeSet<List<String>>(new PathComparator(linkModel));
    }

    /**
     * @inheritDoc
     */
    public int size() {

        return _pathSet.size();
    }

    /**
     * @inheritDoc
     */
    public boolean isEmpty() {

        return _pathSet.isEmpty();
    }

    /**
     * @inheritDoc
     */

    public boolean contains(Object o) {

        return _pathSet.contains(o);
    }

    /**
     * @inheritDoc
     */

    public Iterator<List<String>> iterator() {

        return _pathSet.iterator();
    }

    /**
     * @inheritDoc
     */

    public Object[] toArray() {

        return _pathSet.toArray();
    }

    /**
     * @inheritDoc
     */

    public <T> T[] toArray(T[] a) {

        return _pathSet.toArray(a);
    }

    /**
     * @inheritDoc
     */
    public boolean add(List<String> o) {

        return _pathSet.add(o);
    }

    /**
     * @inheritDoc
     */
    public boolean remove(Object o) {

        return _pathSet.remove(o);
    }

    /**
     * @inheritDoc
     */
    public boolean containsAll(Collection<?> c) {

        return _pathSet.containsAll(c);
    }

    /**
     * @inheritDoc
     */
    public boolean addAll(Collection<? extends List<String>> c) {

        return _pathSet.addAll(c);
    }

    /**
     * @inheritDoc
     */
    public boolean retainAll(Collection<?> c) {

        return _pathSet.retainAll(c);
    }

    /**
     * @inheritDoc
     */
    public boolean removeAll(Collection<?> c) {

        return _pathSet.removeAll(c);
    }

    /**
     * @inheritDoc
     */
    public void clear() {

        _pathSet.clear();
    }
}
