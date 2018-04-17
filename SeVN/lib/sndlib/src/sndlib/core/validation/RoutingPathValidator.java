/*
 * $Id: RoutingPathValidator.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import sndlib.core.model.AdmissiblePathModel;
import sndlib.core.model.DemandModel;
import sndlib.core.model.HopLimitModel;
import sndlib.core.network.AdmissiblePath;
import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.problem.OperatingState;
import sndlib.core.problem.RoutingPath;
import sndlib.core.problem.SolvedProblem;
import sndlib.core.solution.DemandRouting;
import sndlib.core.solution.FlowPath;
import sndlib.core.util.NetworkUtils;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This validator checks whether for each operating state the routing paths 
 * are valid in terms of existance, admissibility and hop-limit restriction.
 * <br/><br/>
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
 * 
 * @author Roman Klaehne
 */
class RoutingPathValidator implements SolutionValidator {

    private static class ErrorKeys {

        final static MessageKey PATH_VIOLATES_MAX_PATH_LENGTH = new SimpleMessageKey(
            "validator.solution.error.routingPathViolatesMaxPathLength");

        final static MessageKey PATH_NOT_ADMISSIBLE = new SimpleMessageKey(
            "validator.solution.error.routingPathNotAdmissible");

        final static MessageKey INVALID_ROUTING_PATH = new SimpleMessageKey(
            "validator.solution.error.invalidRoutingPath");
    }

    /**
     * Constructs a new instance of this validator.
     */
    RoutingPathValidator() {

        /* nothing to initialize */
    }

    /**
     * This method checks whether for each operating state the routing paths 
     * are valid in terms of existance, admissibility and hop-limit 
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
     * @param solvedProblem the solved problem to validate
     * @param errors the message container into those the errors are put
     * 
     * @see sndlib.core.model.HopLimitModel
     * @see sndlib.core.model.AdmissiblePathModel
     * @see sndlib.core.model.LinkModel
     * @see sndlib.core.model.DemandModel
     */
    public void validate(SolvedProblem solvedProblem, Messages errors) {

        Network network = solvedProblem.getNetwork();
        for(OperatingState state : solvedProblem.operatingStates()) {
            for(DemandRouting routing : solvedProblem.routings(state)) {

                String demandId = routing.getDemandId();

                Demand demand = network.getDemand(demandId);
                if(demand == null) {
                    continue;
                }

                /* check hop limits; max path lengths */
                if(solvedProblem.getHopLimitModel() == HopLimitModel.INDIVIDUAL_HOP_LIMITS) {

                    int maxPathLength = demand.getMaxPathLength();

                    if(demand.hasMaxPathLength()) {

                        for(FlowPath flowPath : routing.flowPaths()) {

                            int linkCount = flowPath.linkIdCount();

                            if(linkCount > maxPathLength) {
                                errors.add(SNDlibMessages.error(
                                    ErrorKeys.PATH_VIOLATES_MAX_PATH_LENGTH,
                                    flowPathToString(flowPath), linkCount, demandId,
                                    state.getName(), maxPathLength));
                            }
                        }
                    }
                }

                /* check whether routing paths are indeed source-target paths */
                for(FlowPath flowPath : routing.flowPaths()) {

                    RoutingPath routingPath = solvedProblem.getRoutingPath(flowPath);
                    if(routingPath == null) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.INVALID_ROUTING_PATH,
                            flowPathToString(flowPath), demandId, state.getName()));
                    }
                }

                /* check whether paths are admissible */
                if(solvedProblem.getAdmissiblePathModel() == AdmissiblePathModel.EXPLICIT_LIST) {
                    Collection<AdmissiblePath> admissiblePaths = demand.admissiblePaths();

                    if(admissiblePaths.size() > 0) {
                        for(FlowPath flowPath : routing.flowPaths()) {

                            List<Link> path = NetworkUtils.getLinksByIds(
                                flowPath.linkIds(), solvedProblem.getNetwork());

                            if(path == null) {
                                continue;
                            }

                            if(!containsPath(path, admissiblePaths)) {
                                if(solvedProblem.getDemandModel() == DemandModel.DIRECTED) {
                                    errors.add(SNDlibMessages.error(
                                        ErrorKeys.PATH_NOT_ADMISSIBLE,
                                        flowPathToString(flowPath), demandId,
                                        state.getName()));
                                }
                                else {
                                    Collections.reverse(path);
                                    if(!containsPath(path, admissiblePaths)) {
                                        errors.add(SNDlibMessages.error(
                                            ErrorKeys.PATH_NOT_ADMISSIBLE,
                                            flowPathToString(flowPath), demandId,
                                            state.getName()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Tests whether the specified path is contained in the given collection
     * of admissible paths.
     * 
     * @param path the path to search for
     * @param admissiblePaths the admissible paths
     * 
     * @return <tt>true</tt> if and only if the specified path is contained
     * in the given collection of admissible paths; <tt>false</tt> otherwise
     */
    private static boolean containsPath(List<Link> path,
        Collection<AdmissiblePath> admissiblePaths) {

        for(AdmissiblePath admissiblePath : admissiblePaths) {
            if(admissiblePath.links().equals(path)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a textual representation of the given flow path which can be
     * used in error messages.
     * 
     * @param flowPath the flow path
     * 
     * @return a simple textual representation of the given flow path
     */
    private static String flowPathToString(FlowPath flowPath) {

        StringBuilder builder = new StringBuilder();
        for(String linkId : flowPath.linkIds()) {
            builder.append(linkId + " ");
        }

        return builder.substring(0, builder.length() - 1);
    }
}
