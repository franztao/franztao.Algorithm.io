/*
 * $Id: DemandValueValidator.java 442 2008-01-23 14:53:36Z roman.klaehne $
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
import sndlib.core.network.Demand;
import sndlib.core.problem.Problem;
import sndlib.core.util.DoubleComparator;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * In case of an <tt>INTEGER</tt> routing model this validator checks whether
 * the values of all demands in a given network are integral. 
 * 
 * @see sndlib.core.model.RoutingModel
 * @see sndlib.core.network.Demand
 * 
 * @author Roman Klaehne
 */
class DemandValueValidator implements ProblemValidator {

    private static class ErrorKeys {

        final static MessageKey INVALID_DEMAND_VALUE = new SimpleMessageKey(
            "validator.problem.error.invalidDemandValue");
    }

    /**
     * Constructs a new instance of this validator.
     */
    DemandValueValidator() {

        /* nothing to initialize */
    }

    /**
     * In case of an <tt>INTEGER</tt> routing model this method checks
     * whether the values of all demands in the given problem's network
     * are integral.<br/>
     * If the routing model is not <tt>INTEGER</tt> this method has no
     * effect.
     * 
     * @param problem the problem to validate
     * @param errors the message container into those the errors are put
     */
    public void validate(Problem problem, Messages errors) {

        RoutingModel routingModel = problem.getRoutingModel();

        if(routingModel == RoutingModel.INTEGER) {

            for(Demand demand : problem.getNetwork().demands()) {

                double demandValue = demand.getDemandValue();

                if(!DoubleComparator.getInstance().isInteger(demandValue)) {
                    errors.add(SNDlibMessages.error(ErrorKeys.INVALID_DEMAND_VALUE,
                        demandValue, demand.getId(), routingModel.name()));
                }
            }
        }
    }
}
