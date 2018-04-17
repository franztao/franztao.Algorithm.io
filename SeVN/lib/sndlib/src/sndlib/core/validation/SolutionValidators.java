/*
 * $Id: SolutionValidators.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.validation;

import java.util.Set;

import sndlib.core.problem.Problem;
import sndlib.core.problem.SolvedProblem;

import com.atesio.utils.message.Messages;

/**
 * This class provides the common method to check a given 
 * {@link sndlib.core.problem.SolvedProblem} for feasibility.<br/>
 * <br/> 
 * 
 * It aggregates all solution validators which are currently implemented.
 * 
 * @see sndlib.core.problem.SolvedProblem
 * @see sndlib.core.solution.Solution
 * 
 * @author Roman Klaehne
 */
public class SolutionValidators implements SolutionValidator {

    /**
     * This validator checks whether the installed link capacity modules are 
     * admissible in a given solution.<br/>
     * <br/>
     * 
     * The admissibility of the installed capacity modules thereby depends on
     * the link capacity model.<br/>
     * <br/>
     * 
     * For a link which possesses a pre-installed capacity module this 
     * validator also checks whether there is a corresponding 
     * <tt>ModuleConfiguration</tt> in that link's configuration.
     * 
     * @see sndlib.core.model.LinkCapacityModel
     */
    public static final SolutionValidator LINK_CAPACITIES = new LinkCapacityValidator();

    /**
     * This validator checks whether for each operating state the link flows 
     * in a given solution do not exceed the installed link capacities, with 
     * respect to the link and survivability model.
     * 
     * @see sndlib.core.model.LinkModel
     * @see sndlib.core.problem.LinkFlow
     */
    public static final SolutionValidator LINK_FLOW = new LinkFlowValidator();

    /**
     * This validator checks whether for each operating state the demand 
     * routings in a given solution satisfy the demand value (the flow path
     * values sum-up to at least the demand value).
     * 
     * @see sndlib.core.model.DemandModel
     * @see sndlib.core.problem.DemandFlow
     */
    public static final SolutionValidator DEMAND_FLOW = new DemandFlowValidator();

    /**
     * This validator checks whether for each operating state the routing 
     * paths are valid in terms of existence, admissibility and hop-limit 
     * restriction.<br/><br/>
     * 
     * In more detail:<br/><br/>
     * 
     * It is checked whether the demand routing paths are valid with respect 
     * to the link and demand model.<br/>
     * If the hop-limit model is set to <tt>INDIVIDUAL_HOP_LIMITS</tt> then
     * the routing paths must not violate the hop-limit restrictions.<br/> 
     * If the admissible path model is set to <tt>EXPLICIT_LIST</tt> then the 
     * routing paths used to route a demand must be contained in that demand's
     * admissible path list. 
     *
     * @see sndlib.core.model.HopLimitModel
     * @see sndlib.core.model.AdmissiblePathModel
     * @see sndlib.core.model.LinkModel
     * @see sndlib.core.model.DemandModel
     */
    public static final SolutionValidator ADMISSIBLE_ROUTING_PATH = new RoutingPathValidator();

    /**
     * This validator checks whether for each operating state the demand 
     * routings of a given solution are admissible with respect to the 
     * routing model.<br/><br/>
     * 
     * If the routing model is <tt>INTEGER</tt> then all flow path values must
     * be integral.<br/>
     * If the routing model is <tt>SINGLE_PATH</tt> then there must be exactly 
     * one flow path per demand routing.<br/>
     * If the routing model is <tt>CONTINOUS</tt> then there can be an abritrary
     * number of flow paths with non-negative flow value per demand routing.
     * 
     * @see sndlib.core.model.RoutingModel
     */
    public static final SolutionValidator ADMISSIBLE_ROUTING = new RoutingValidator();

    /**
     * This validator checks whether the demand routings in case of a failure 
     * (when the network is at failure state) are feasible with respect to the
     * used survivability mechanism.<br/><br/>
     * 
     * If no survivability mechanism is used, this validator has nothing to check.
     * Otherwise for each single link failure and each demand there must be a 
     * routing not containing the corresponding failure link.<br/><br/>
     * 
     * In case the 1+1 dedicated path protection mechansim 
     * ({@link sndlib.core.model.SurvivabilityModel#ONE_PLUS_ONE_PROTECTION}) 
     * is used it is also checked whether there is only one protection (and one 
     * working) path per demand.<br/><br/>
     * 
     * If the shared path protection mechanism 
     * ({@link sndlib.core.model.SurvivabilityModel#SHARED_PATH_PROTECTION})
     * is used it is also checked whether path flows not affected by a single link
     * failure (not containing the failure link) are also contained in the 
     * corresponding failure routing.
     * 
     * @see sndlib.core.model.SurvivabilityModel
     * @see sndlib.core.problem.OperatingState
     */
    public static final SolutionValidator SURVIVABILITY = new SurvivabilityValidator();

    /**
     * This validator is the singleton instance of this class and aggregates 
     * all validators currently implemented.
     */
    public static final SolutionValidator ALL = new SolutionValidators();

    /**
     * Constructs a new instance of this validator.
     */
    private SolutionValidators() {

    }

    /**
     * Performs the validation of the given solved problem against the 
     * feasibility conditions imposed by all the validators currently 
     * implemented.
     * 
     * @param solvedProblem the solved problem to validate
     * 
     * @return the messages indicating the feasiblity errors
     */
    public static Messages validateAll(SolvedProblem solvedProblem) {

        Messages errors = new Messages();

        ALL.validate(solvedProblem, errors);

        return errors;
    }

    /**
     * Applies the specified validators to the given solved problem.
     * 
     * @param solvedProblem the solved problem to validate
     * @param validators the validator implementations
     * 
     * @return the messages indicating the feasiblity errors
     */
    public static Messages validateAll(SolvedProblem solvedProblem,
        Set<SolutionValidator> validators) {

        Messages errors = new Messages();

        for(SolutionValidator validation : validators) {
            validation.validate(solvedProblem, errors);
        }

        return errors;
    }

    /**
     * Performs the validation of the given solved problem against the 
     * feasibility conditions imposed by all the validators currently 
     * implemented.<br/>
     * This method first calls {@link ProblemValidators#validateAll(Problem)}
     * and returns immediately if problem errors are found.
     * 
     * @param solvedProblem the solved problem to validate
     * @param errors the message container into those the errors are put
     */
    public void validate(SolvedProblem solvedProblem, Messages errors) {

        Messages problemErrors = ProblemValidators.validateAll(solvedProblem);
        if(problemErrors.size() > 0) {
            errors.add(problemErrors);
            return;
        }

        LINK_CAPACITIES.validate(solvedProblem, errors);
        LINK_FLOW.validate(solvedProblem, errors);
        ADMISSIBLE_ROUTING.validate(solvedProblem, errors);
        ADMISSIBLE_ROUTING_PATH.validate(solvedProblem, errors);
        DEMAND_FLOW.validate(solvedProblem, errors);
        SURVIVABILITY.validate(solvedProblem, errors);
    }
}
