/*
 * $Id: SolutionStatistics.java 99 2006-07-04 14:05:16Z bzforlow $
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

import sndlib.core.problem.SolvedProblem;

/**
 * This class provides the common methods to calculate statistics of a 
 * given {@link sndlib.core.solution.Solution} of a network design problem
 * (respectively a {@link sndlib.core.problem.SolvedProblem}).<br/>
 * <br/>
 * An instance of this class obtained by one of the static factory methods 
 * acts as a container for the calculated statistic values and caches them 
 * for future requests. It provides methods to iterate over all values or to 
 * query a single specific value.  
 * 
 * @see SolutionStatisticValueKeys
 * 
 * @author Roman Klaehne
 */

public class SolutionStatistics extends Statistics<SolutionStatisticValueKey> {

    /**
     * Generates the statistics of the given solved problem, considering the 
     * given set of statistic keys.
     * 
     * @param solvedProblem the solved problem
     * @param keys identify the statistic values to calculate
     * 
     * @return a new statistics object containing the calculated values
     */
    public static SolutionStatistics newStatistics(SolvedProblem solvedProblem,
        Set<SolutionStatisticValueKey> keys) {

        SolutionStatistics stats = new SolutionStatistics();

        for(SolutionStatisticValueKey statValue : keys) {
            stats.addValue(statValue, statValue.calculateValue(solvedProblem));
        }

        return stats;
    }

    /**
     * Generates the statistics of the given solved problem, considering all 
     * the values listed in {@link SolutionStatisticValueKeys}.
     * 
     * @param solvedProblem the solved problem
     * 
     * @return a new statistics object containing the calculated values 
     */
    public static SolutionStatistics newStatistics(SolvedProblem solvedProblem) {

        return newStatistics(solvedProblem, new HashSet<SolutionStatisticValueKey>(
            EnumSet.allOf(SolutionStatisticValueKeys.class)));
    }

    /**
     * Constructs a new instance of this class.
     */
    private SolutionStatistics() {

        /* nothing to initialize */
    }
}

