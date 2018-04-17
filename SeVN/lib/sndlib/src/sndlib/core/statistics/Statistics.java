/*
 * $Id: Statistics.java 99 2006-07-04 14:05:16Z bzforlow $
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

package sndlib.core.statistics;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

/**
 * This is the super class of {@link ProblemStatistics} and 
 * {@link SolutionStatistics}. It encapsulates the common methods which are 
 * provided by both sub classes.<br/>
 * This abstraction is not a design issue of this package but is regarded as 
 * a technical implementation detail.
 * 
 * @author Roman Klaehne
 */
abstract class Statistics<ValueKey extends StatisticValueKey<?>> implements
    Iterable<ValueKey> {

    /**
     * The entry map of this statistics.
     */
    private Map<ValueKey, Double> _entries;

    /**
     * Constructs a new empty statistics object.
     */
    Statistics() {

        _entries = new IdentityHashMap<ValueKey, Double>();
    }

    /**
     * Tests whether this statistics contains the statistic value for the 
     * specified key.
     * 
     * @param key the key identifying a statistic value
     * 
     * @return <tt>true</tt> if and only if this statistics contains the 
     * value for the specified key; <tt>false</tt> otherwise
     */
    public boolean containsValue(ValueKey key) {

        return _entries.containsKey(key);
    }

    /**
     * Adds the given value to this statistics.
     * 
     * @param key the key identifying the value 
     * @param value the calculated value 
     */
    void addValue(ValueKey key, double value) {

        _entries.put(key, value);
    }

    /**
     * Returns the statistic value for the specified key.
     * 
     * @param key the key identifying a statistic value
     * 
     * @return the statistic value
     * 
     * @throws NoSuchElementException if this statistics does not contain
     * a statistic value for the specified key
     */
    public double getValue(ValueKey key) {

        if(!_entries.containsKey(key)) {
            throw new NoSuchElementException("key " + key
                + " not found in this statistics");
        }

        return _entries.get(key);
    }

    /**
     * Returns a simple textual representation of this statistics.
     * 
     * @return a textual representation of this statistics
     */
    public String toString() {

        StringBuffer result = new StringBuffer();

        for(Map.Entry<ValueKey, Double> value : _entries.entrySet()) {
            result.append(value.getKey().getName()).append(" = ");
            result.append(value.getValue()).append("\n");
        }

        return result.toString();
    }

    /**
     * Returns an iterator over the value keys in this statistics.
     * 
     * @return an iterator over the value keys in this statistics
     */
    public Iterator<ValueKey> iterator() {

        /* iterate over keys sorted by name */
        Set<ValueKey> s = new TreeSet<ValueKey>(
            new Comparator<StatisticValueKey<?>>() {

                public int compare(StatisticValueKey<?> o1, StatisticValueKey<?> o2) {

                    return o1.getName().compareTo(o2.getName());
                }
            });

        s.addAll(_entries.keySet());
        return s.iterator();
    }
}

