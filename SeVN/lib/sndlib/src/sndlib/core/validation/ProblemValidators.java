/*
 * $Id: ProblemValidators.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import com.atesio.utils.message.Messages;

/**
 * This class provides the common method to check a given 
 * {@link sndlib.core.problem.Problem} for feasibility.<br/>
 * <br/> 
 * 
 * It aggregates all problem validators which are currently implemented.
 * 
 * @see sndlib.core.problem.Problem
 * 
 * @author Roman Klaehne
 */
public class ProblemValidators implements ProblemValidator {

    /**
     * This validator checks whether the model combination in a network design
     * problem is supported in SNDlib.<br/><br/>
     * 
     * Actually a valid model combination ({@link sndlib.core.model.Model}) 
     * must satisfy the following conditions:
     * 
     * <ul>
     *  <li><tt>SurvivabilityModel == ONE_PLUS_ONE_PROTECTION => 
     *  RoutingModel == SINGLE_PATH</tt></li>
     *  <li><tt>LinkModel == UNDIRECTED <=> DemandModel == UNDIRECTED</tt></li>
     *  <li><tt>LinkModel != UNDIRECTED <=> Demand == DIRECTED</tt></li>
     * </ul>
     * <br/>
     * 
     * The following models are not yet supported in SNDlib:
     * 
     * <ul>
     *  <li><tt>RoutingModel OSPF_EQUAL_COST_MULTI_PATH</tt></li>
     *  <li><tt>RoutingModel OSPF_SINGLE_PATH</tt></li>
     *  <li><tt>NodeModel NODE_HARDWARE</tt></li>
     * </ul>
     * 
     * @see sndlib.core.model.Model
     */
    public static final ProblemValidator MODEL = new ModelValidator();

    /**
     * If the admissible path model of a network design problem is set to 
     * <tt>EXPLICIT_LIST</tt> this validator checks whether the admissible 
     * paths of all demands in a given network are feasible.
     * An admissible path is feasible if it connects the end nodes of
     * the demand. With an undirected demand model, the path may be
     * defined from target to source of the demand, otherwise it must be
     * directed from source to target. 
     * <br/><br/>
     *
     * The direction into which the links can be traversed is determined by 
     * the link model.
     * <br/><br/>  
     * 
     * If the admissible path model is not <tt>EXPLICIT_LIST</tt> this 
     * validator has no effect.
     * 
     * @see sndlib.core.model.AdmissiblePathModel
     * @see sndlib.core.network.AdmissiblePath
     */
    public static final ProblemValidator ADMISSIBLE_PATHS_FEASIBILITY = new ExplicitPathsFeasibilityValidator();

    /**
     * If the admissible path model is set to <tt>EXPLICIT_LIST</tt> this 
     * validator checks at least one admissible path is specified for each 
     * demand in the network.<br/>
     * <br/>
     * 
     * If the admissible path model is not <tt>EXPLICIT_LIST</tt> this 
     * validator has no effect.
     * 
     * @see sndlib.core.model.AdmissiblePathModel
     * @see sndlib.core.network.AdmissiblePath
     */
    public static final ProblemValidator ADMISSIBLE_PATHS_EXISTENCE = new ExplicitPathsExistanceValidator();

    /**
     * In case of an <tt>INTEGER</tt> routing model this validator checks
     * whether the values of all demands in a given network are integral.
     * <br/><br/>
     * 
     * If the routing model is not <tt>INTEGER</tt> this validator has no
     * effect.
     * 
     * @see sndlib.core.model.RoutingModel
     * @see sndlib.core.network.Demand
     */
    public static final ProblemValidator DEMAND_VALUES = new DemandValueValidator();

    /**
     * This validator is the only instance of this class and aggregates 
     * all validators currently implemented.
     */
    public static final ProblemValidator ALL = new ProblemValidators();

    /**
     * Constructs a new intance of this validator.
     */
    private ProblemValidators() {

    }

    /**
     * Performs the validation of the given problem against the feasibility
     * conditions imposed by all the validators currently implemented.
     * 
     * @param problem the problem to validate
     * 
     * @return the messages indicating the feasiblity errors
     */
    public static Messages validateAll(Problem problem) {

        Messages errors = new Messages();

        ALL.validate(problem, errors);

        return errors;
    }

    /**
     * Applies the specified validators to the given problem.
     * 
     * @param problem the problem to validate
     * @param validators the validator implementations
     * 
     * @return the messages indicating the feasiblity errors
     */
    public static Messages validateAll(Problem problem,
        Set<ProblemValidator> validators) {

        Messages errors = new Messages();

        for(ProblemValidator validation : validators) {
            validation.validate(problem, errors);
        }

        return errors;
    }

    /**
     * Performs the validation of the given problem against the feasibility
     * conditions imposed by all the validators currently implemented.
     * 
     * @param problem the problem to validate
     * @param errors the message container into which the errors are put
     */
    public void validate(Problem problem, Messages errors) {

        MODEL.validate(problem, errors);
        if(errors.size() > 0) {
            return;
        }

        ADMISSIBLE_PATHS_EXISTENCE.validate(problem, errors);
        ADMISSIBLE_PATHS_FEASIBILITY.validate(problem, errors);
        DEMAND_VALUES.validate(problem, errors);
    }
}
