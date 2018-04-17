/*
 * $Id: DemandFlowValidator.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import java.util.HashSet;
import java.util.Set;

import sndlib.core.model.SurvivabilityModel;
import sndlib.core.network.Demand;
import sndlib.core.problem.DemandFlow;
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
 * in a solution satisfy the demand value (the flow path values sum-up to at 
 * least the demand value).
 * 
 * @see sndlib.core.model.DemandModel
 * @see sndlib.core.problem.DemandFlow
 * @see sndlib.core.network.Demand
 * 
 * @author Roman Klaehne
 */
class DemandFlowValidator implements SolutionValidator {

    private static class ErrorKeys {

        final static MessageKey FLOW_NOT_SATISFY_DEMAND_VALUE = new SimpleMessageKey(
            "validator.solution.error.flowNotSatisfyDemandValue");
    }

    /**
     * Constructs a new instance of this validator.
     */
    DemandFlowValidator() {

        /* nothing to initialize */
    }

    /**
     * This validator checks whether for each operating state the demand 
     * routings in the given solution satisfy the demand value (the flow 
     * path values sum-up to at least the demand value).
     * 
     * @param solvedProblem the solved problem to validate
     * @param errors the message container into those the errors are put
     */
    public void validate(SolvedProblem solvedProblem, Messages errors) {

        for(Demand demand : solvedProblem.getNetwork().demands()) {
            DemandRouting nosDemandRouting = solvedProblem.getRouting(demand,
                OperatingState.NOS);
            DemandFlow nosDemandFlow = solvedProblem.getDemandFlow(demand,
                OperatingState.NOS);

            checkDemandFlow(demand, nosDemandFlow, OperatingState.NOS.getName(),
                errors);

            if(solvedProblem.getSurvivabilityModel() != SurvivabilityModel.NO_SURVIVABILITY) {

                Set<String> linksInNOSRouting = new HashSet<String>();
                for(FlowPath flowPath : nosDemandRouting.flowPaths()) {
                    linksInNOSRouting.addAll(flowPath.linkIds());
                }

                for(String failureLink : linksInNOSRouting) {
                    checkDemandFlow(demand, solvedProblem.getDemandFlow(demand,
                        OperatingState.singleLinkFailureState(failureLink)),
                        failureLink, errors);
                }
            }
        }
    }

    private void checkDemandFlow(Demand demand, DemandFlow demandFlow, String state,
        Messages errors) {

        double totalFlow = demandFlow.getTotalFlow();
        double demandValue = demand.getDemandValue();

        if(DoubleComparator.getInstance().less(totalFlow, demandValue)) {
            errors.add(SNDlibMessages.error(ErrorKeys.FLOW_NOT_SATISFY_DEMAND_VALUE,
                totalFlow, demand.getId(), state, demandValue));
        }
    }

}
