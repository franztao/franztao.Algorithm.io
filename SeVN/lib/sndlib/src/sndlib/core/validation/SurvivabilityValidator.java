/*
 * $Id: SurvivabilityValidator.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import java.util.List;

import sndlib.core.network.Demand;
import sndlib.core.problem.DemandPathFlow;
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
 * 
 * @author Roman Klaehne
 */
class SurvivabilityValidator implements SolutionValidator {

    static class ErrorKeys {

        final static MessageKey NO_ROUTING_IN_FAILURE_STATE = new SimpleMessageKey(
            "validator.solution.error.noRoutingInFailureState");

        final static MessageKey PROTECTION_ROUTING_CONTAINS_FAILURE_LINK = new SimpleMessageKey(
            "validator.solution.error.protectionRoutingContainsFailureLink");

        final static MessageKey MORE_THAN_ONE_PROTECTION_PATH = new SimpleMessageKey(
            "validator.solution.error.moreThanOneProtectionPath");

        final static MessageKey ILLEGAL_FLOW_REROUTING = new SimpleMessageKey(
            "validator.solution.error.illegalFlowRerouting");
    }

    /** 
     * This method checks whether the demand routings specified in the given 
     * solution in case of a failure (when the network is at failure state) 
     * are feasible with respect to the used survivability mechanism.<br/><br/>
     * 
     * If no survivability mechanism is used, this method has nothing to check.
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
     * @param solvedProblem the solved problem 
     * @param errors the message container into those the errors are put
     * 
     * @see sndlib.core.model.SurvivabilityModel
     * @see sndlib.core.problem.OperatingState
     */
    public void validate(SolvedProblem solvedProblem, Messages errors) {

        switch (solvedProblem.getSurvivabilityModel()) {
        case ONE_PLUS_ONE_PROTECTION:
            validateOneAndOneProtection(solvedProblem, errors);
            break;
        case SHARED_PATH_PROTECTION:
            validateSharedPathProtection(solvedProblem, errors);
            break;
        case UNRESTRICTED_FLOW_RECONFIGURATION:
            validateUnrestrictedFlowReconfiguration(solvedProblem, errors);
            break;
        }
    }

    /**
     * Validates the given solution in case the shared path protection 
     * mechanism is used.
     *
     * @param solveProblem the solved problem 
     * @param errors the message container into those the errors are put
     * 
     * @see sndlib.core.model.SurvivabilityModel#SHARED_PATH_PROTECTION
     */
    private void validateSharedPathProtection(SolvedProblem solvedProblem,
        Messages errors) {

        for(Demand demand : solvedProblem.demands()) {

            DemandRouting nosRouting = solvedProblem.getRouting(demand,
                OperatingState.NOS);
            if(nosRouting == null || nosRouting.flowPathCount() == 0) {
                continue;
            }

            for(FlowPath fp : nosRouting.flowPaths()) {
                for(String failureLinkId : fp.linkIds()) {
                    OperatingState failureState = OperatingState.singleLinkFailureState(failureLinkId);
                    DemandRouting failureRouting = solvedProblem.getRouting(demand,
                        failureState);

                    if(failureRouting.getState() == OperatingState.NOS) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.NO_ROUTING_IN_FAILURE_STATE, demand.getId(),
                            failureLinkId));
                        continue;
                    }

                    for(FlowPath nosFlowPath : nosRouting.flowPaths()) {
                        if(!nosFlowPath.containsLink(failureLinkId)) {
                            List<String> nosPath = nosFlowPath.linkIds();

                            DemandPathFlow failurePathFlow = solvedProblem.getDemandPathFlow(
                                demand, nosPath, failureState);
                            DemandPathFlow nosPathFlow = solvedProblem.getDemandPathFlow(
                                demand, nosPath, OperatingState.NOS);

                            if(DoubleComparator.getInstance().less(failurePathFlow.getTotalFlow(),
                                nosPathFlow.getTotalFlow())) {
                                errors.add(SNDlibMessages.error(
                                    ErrorKeys.ILLEGAL_FLOW_REROUTING,
                                    flowPathToString(nosFlowPath), demand.getId(),
                                    failureLinkId));
                            }
                        }
                    }

                    for(FlowPath protectionPath : failureRouting.flowPaths()) {
                        if(protectionPath.containsLink(failureLinkId)) {
                            errors.add(SNDlibMessages.error(
                                ErrorKeys.PROTECTION_ROUTING_CONTAINS_FAILURE_LINK,
                                flowPathToString(protectionPath), demand.getId(),
                                failureLinkId));
                        }
                    }
                }
            }
        }
    }

    /**
     * Validates the given solution in case the unrestricted flow 
     * reconfiguration protection mechanism is used.
     *
     * @param solveProblem the solved problem 
     * @param errors the message container into those the errors are put
     * 
     * @see sndlib.core.model.SurvivabilityModel#UNRESTRICTED_FLOW_RECONFIGURATION
     */
    private void validateUnrestrictedFlowReconfiguration(
        SolvedProblem solvedProblem, Messages errors) {

        for(Demand demand : solvedProblem.demands()) {

            DemandRouting nosRouting = solvedProblem.getRouting(demand,
                OperatingState.NOS);
            if(nosRouting == null || nosRouting.flowPathCount() == 0) {
                continue;
            }

            for(FlowPath fp : nosRouting.flowPaths()) {
                for(String failureLinkId : fp.linkIds()) {
                    DemandRouting failureRouting = solvedProblem.getRouting(demand,
                        OperatingState.singleLinkFailureState(failureLinkId));

                    if(failureRouting.getState() == OperatingState.NOS) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.NO_ROUTING_IN_FAILURE_STATE, demand.getId(),
                            failureLinkId));
                        continue;
                    }

                    for(FlowPath protectionPath : failureRouting.flowPaths()) {
                        if(protectionPath.containsLink(failureLinkId)) {
                            errors.add(SNDlibMessages.error(
                                ErrorKeys.PROTECTION_ROUTING_CONTAINS_FAILURE_LINK,
                                flowPathToString(protectionPath), demand.getId(),
                                failureLinkId));
                        }
                    }

                }
            }

        }
    }

    /**
     * Validates the given solution in case the 1+1 dedicated path protection
     * mechanism is used.
     *
     * @param solveProblem the solved problem 
     * @param errors the message container into those the errors are put
     * 
     * @see sndlib.core.model.SurvivabilityModel#ONE_PLUS_ONE_PROTECTION
     */
    private void validateOneAndOneProtection(SolvedProblem solvedProblem,
        Messages errors) {

        for(Demand demand : solvedProblem.demands()) {

            DemandRouting nosRouting = solvedProblem.getRouting(demand,
                OperatingState.NOS);
            if(nosRouting == null || nosRouting.flowPathCount() == 0) {
                continue;
            }

            FlowPath nosPath = nosRouting.flowPaths().get(0);
            FlowPath protectionPath = null;

            for(String failureLinkId : nosPath.linkIds()) {
                DemandRouting failureRouting = solvedProblem.getRouting(demand,
                    OperatingState.singleLinkFailureState(failureLinkId));

                if(failureRouting.getState() == OperatingState.NOS
                    || failureRouting.flowPathCount() == 0) {
                    errors.add(SNDlibMessages.error(
                        ErrorKeys.NO_ROUTING_IN_FAILURE_STATE, demand.getId(),
                        failureLinkId));
                    continue;
                }

                FlowPath pp = failureRouting.flowPaths().get(0);
                if(protectionPath == null) {
                    protectionPath = pp;

                    if(protectionPath.containsLink(failureLinkId)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.PROTECTION_ROUTING_CONTAINS_FAILURE_LINK,
                            flowPathToString(protectionPath), demand.getId(),
                            failureLinkId));
                    }
                }
                else if(!equals(protectionPath, pp)) {
                    errors.add(SNDlibMessages.error(
                        ErrorKeys.MORE_THAN_ONE_PROTECTION_PATH, demand.getId(),
                        flowPathToString(protectionPath), flowPathToString(pp)));
                }
            }
        }
    }

    /**
     * Tests whether the given two flow paths are equal, that means whether 
     * they have the same flow path value and traverses the same sequence of
     * links in order.
     * 
     * @param first one flow path
     * @param second another flow path
     * 
     * @return <tt>true</tt> if and only if the given two flow paths are 
     * equal; <tt>false</tt> otherwise
     */
    private boolean equals(FlowPath first, FlowPath second) {

        if(first.flowPathValue() != second.flowPathValue()) {
            return false;
        }
        return first.linkIds().equals(second.linkIds());
    }

    /**
     * Returns a textual representation of the given flow path for the use
     * in error messages.
     * 
     * @param flowPath the flow path
     * 
     * @return a simple textual representation of the given flow path
     */
    static String flowPathToString(FlowPath flowPath) {

        StringBuilder builder = new StringBuilder();
        for(String linkId : flowPath.linkIds()) {
            builder.append(linkId + " ");
        }

        return builder.substring(0, builder.length() - 1);
    }
}
