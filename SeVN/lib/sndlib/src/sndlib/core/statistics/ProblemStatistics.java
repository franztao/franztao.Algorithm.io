/*
 * $Id: ProblemStatistics.java 99 2006-07-04 14:05:16Z bzforlow $
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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import sndlib.core.problem.Problem;

/**
 * This class provides the common methods to calculate statistics of a 
 * given network design {@link sndlib.core.problem.Problem}.<br/>
 * <br/>
 * An instance of this class obtained by one of the static factory methods 
 * acts as a container for the calculated statistic values and caches them 
 * for future requests. It provides methods to iterate over all values or to 
 * query a single specific value.  
 * 
 * @see ProblemStatisticValueKeys
 * 
 * @author Roman Klaehne
 */
public class ProblemStatistics extends Statistics<ProblemStatisticValueKey> {

    /**
     * Generates the statistics of the given problem, considering the given
     * set of statistic keys.
     * 
     * @param problem the problem
     * @param keys identify the statistic values to calculate
     * 
     * @return a new statistics object containing the calculated values 
     */
    public static ProblemStatistics newStatistics(Problem problem,
        Set<ProblemStatisticValueKey> keys) {

        ProblemStatistics stats = new ProblemStatistics();

        for(ProblemStatisticValueKey statValue : keys) {
            stats.addValue(statValue, statValue.calculateValue(problem));
        }

        return stats;
    }

    /**
     * Generates the statistics of the given problem, considering all the 
     * values listed in {@link ProblemStatisticValueKeys}.
     * 
     * @param problem the problem
     * 
     * @return a new statistics object containing the calculated values 
     */
    public static ProblemStatistics newStatistics(Problem problem) {

        return newStatistics(problem, new HashSet<ProblemStatisticValueKey>(
            EnumSet.allOf(ProblemStatisticValueKeys.class)));
    }
    
    /**
     * Constructs a new instance of this class.
     */
    private ProblemStatistics() {
        
        /* nothing to initialize */
    }
}

