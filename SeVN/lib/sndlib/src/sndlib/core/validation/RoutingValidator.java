/*
 * $Id: RoutingValidator.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import sndlib.core.model.RoutingModel;
import sndlib.core.network.Network;
import sndlib.core.problem.OperatingState;
import sndlib.core.problem.SolvedProblem;
import sndlib.core.solution.DemandRouting;
import sndlib.core.solution.FlowPath;
import sndlib.core.util.DoubleComparator;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This validator checks whether for each operating state the demand routings
 * of a specific solutionare admissible with respect to the routing model.
 * <br/><br/>
 * 
 * If the routing model is <tt>INTEGER</tt> then all flow path values must
 * be integral.<br/>
 * If the routing model is <tt>SINGLE_PATH</tt> then there must be exactly 
 * one flow path per demand routing.<br/>
 * If the routing model is <tt>CONTINOUS</tt> then there can be an abritrary
 * number of flow paths with non-negative flow value per demand routing.
 *
 * @see sndlib.core.model.RoutingModel
 * 
 * @author Roman Klaehne
 */
class RoutingValidator implements SolutionValidator {

    private static class ErrorKeys {

        final static MessageKey FLOW_VALUE_LOWER_THAN_ZERO = new SimpleMessageKey(
            "validator.solution.error.routingFlowValueLowerThanZero");

        final static MessageKey FLOW_VALUE_NOT_AN_INTEGER = new SimpleMessageKey(
            "validator.solution.error.routingFlowValueNotAnInteger");

        final static MessageKey MORE_THAN_ONE_ROUTING_PATH = new SimpleMessageKey(
            "validator.solution.error.moreThanOneRoutingPath");
    }

    /**
     * Constructs a new instance of this validator.
     */
    RoutingValidator() {

        /* nothing to initialize */
    }

    /**
     * This method checks whether for each operating state the demand routings
     * of the given solution are admissible with respect to the routing model.
     * <br/><br/>
     * 
     * If the routing model is <tt>INTEGER</tt> then all flow path values must
     * be integral.<br/>
     * If the routing model is <tt>SINGLE_PATH</tt> then there must be exactly 
     * one flow path per demand routing.<br/>
     * If the routing model is <tt>CONTINOUS</tt> then there can be an abritrary
     * number of flow paths with non-negative flow value per demand routing.
     * 
     * @param solvedProblem the solved problem to validate
     * @param errors the message container into those the errors are put
     * 
     * @see sndlib.core.model.RoutingModel
     */
    public void validate(SolvedProblem solvedProblem, Messages errors) {

        RoutingModel routingModel = solvedProblem.getRoutingModel();

        Validator validator = Validator.getValidatorFor(routingModel);

        Network network = solvedProblem.getNetwork();

        for(OperatingState state : solvedProblem.operatingStates()) {
            for(DemandRouting routing : solvedProblem.routings(state)) {

                if(network.hasDemand(routing.getDemandId())) {
                    validator.validate(routing, errors);
                }
            }
        }
    }

    /**
     * This is an internal helper class which abstracts the validation from
     * a specific routing model.
     * 
     * @author Roman Klaehne
     */
    private static abstract class Validator {

        /**
         * Performs the validation in case of a continous routing model.
         */
        static final Validator CONTINOUS_ROUTINGS = new Validator() {

            void validate(DemandRouting routing, Messages errors) {

                for(FlowPath flowPath : routing.flowPaths()) {

                    double flowPathValue = flowPath.flowPathValue();

                    if(DoubleComparator.getInstance().less(flowPathValue, 0.0)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.FLOW_VALUE_LOWER_THAN_ZERO, flowPathValue,
                            flowPath, routing.getDemandId(), routing.getState()));
                    }
                }
            }
        };

        /**
         * Performs the validation in case of a integer routing model.
         */
        static final Validator INTEGER_ROUTINGS = new Validator() {

            void validate(DemandRouting routing, Messages errors) {

                for(FlowPath flowPath : routing.flowPaths()) {

                    double flowPathValue = flowPath.flowPathValue();

                    if(!DoubleComparator.getInstance().isInteger(flowPathValue)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.FLOW_VALUE_NOT_AN_INTEGER, flowPathValue,
                            flowPath, routing.getDemandId(), routing.getState()));
                    }
                    if(DoubleComparator.getInstance().less(flowPathValue, 0.0)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.FLOW_VALUE_LOWER_THAN_ZERO, flowPathValue,
                            flowPath, routing.getDemandId(), routing.getState()));
                    }
                }
            }
        };

        /**
         * Performs the validation in case of a single-path routing model.
         */
        static final Validator SINGLE_PATH_ROUTINGS = new Validator() {

            void validate(DemandRouting routing, Messages errors) {

                int pathCount = routing.flowPathCount();

                if(pathCount != 1) {
                    errors.add(SNDlibMessages.error(
                        ErrorKeys.MORE_THAN_ONE_ROUTING_PATH, pathCount,
                        routing.getDemandId(), routing.getState()));
                }
                else {
                    FlowPath singleFlowPath = routing.flowPaths().get(0);
                    double flowPathValue = singleFlowPath.flowPathValue();

                    if(DoubleComparator.getInstance().less(flowPathValue, 0.0)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.FLOW_VALUE_LOWER_THAN_ZERO, flowPathValue,
                            singleFlowPath, routing.getDemandId(),
                            routing.getState()));
                    }
                }
            }
        };

        /**
         * Returns the appropriate validator for the specified routing model.
         * 
         * @param routingModel the routing Model
         * 
         * @return the appropriate validator for the specified routing model
         */
        static Validator getValidatorFor(RoutingModel routingModel) {

            Validator validator = null;

            switch (routingModel) {

            case CONTINUOUS:
                validator = CONTINOUS_ROUTINGS;
                break;

            case INTEGER:
                validator = INTEGER_ROUTINGS;
                break;

            case SINGLE_PATH:
                validator = SINGLE_PATH_ROUTINGS;
                break;

            default:
                throw new UnsupportedOperationException(routingModel
                    + " is not supported till now");
            }

            return validator;
        }

        abstract void validate(DemandRouting routing, Messages errors);
    }
}
