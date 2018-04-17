/*
 * $Id: ModelValidator.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import sndlib.core.model.DemandModel;
import sndlib.core.model.LinkModel;
import sndlib.core.model.ModelProperty;
import sndlib.core.model.NodeModel;
import sndlib.core.model.RoutingModel;
import sndlib.core.model.SurvivabilityModel;
import sndlib.core.problem.Problem;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This validator checks whether the model combination in a network design
 * {@link Problem} is supported in SNDlib.<br/><br/>
 * 
 * Actually a model combination ({@link sndlib.core.model.Model}) must satisfy 
 * the following conditions:
 * 
 * <ul>
 *  <li><tt>SurvivabilityModel == ONE_PLUS_ONE_PROTECTIONS => 
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
 * 
 * @author Roman Klaehne
 */
class ModelValidator implements ProblemValidator {

    static class ErrorKeys {

        final static MessageKey INVALID_MODEL_COMBINATION = new SimpleMessageKey(
            "validator.problem.error.invalidModelPropertyCombination");

        final static MessageKey MODEL_NOT_YET_SUPPORTED = new SimpleMessageKey(
            "validator.problem.error.modelNotYetSupported");
    }

    /**
     * Constructs a new instance of this validator.
     */
    ModelValidator() {

        /* nothing to initialize */
    }

    /**
     * This method checks whether the model combination in the given problem 
     * is supported in SNDlib.<br/><br/>
     * 
     * Actually a model combination ({@link sndlib.core.model.Model}) must satisfy 
     * the following conditions:
     * 
     * <ul>
     *  <li><tt>SurvivabilityModel == ONE_PLUS_ONE_PROTECTIONS => 
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
     * @param problem the problem 
     * @param errors the message container into those the errors are put
     * 
     * @see sndlib.core.model.Model
     */
    public void validate(Problem problem, Messages errors) {

        SurvivabilityModel sm = problem.getSurvivabilityModel();
        RoutingModel rm = problem.getRoutingModel();

        if(sm == SurvivabilityModel.ONE_PLUS_ONE_PROTECTION
            && rm != RoutingModel.SINGLE_PATH) {
            errors.add(SNDlibMessages.error(ErrorKeys.INVALID_MODEL_COMBINATION,
                ModelProperty.SURVIVABILITY_MODEL.name(),
                ModelProperty.ROUTING_MODEL.name(), sm.name(), rm.name()));
        }

        if(rm == RoutingModel.OSPF_EQUAL_COST_MULTI_PATH
            || rm == RoutingModel.OSPF_SINGLE_PATH) {
            errors.add(SNDlibMessages.error(ErrorKeys.MODEL_NOT_YET_SUPPORTED,
                ModelProperty.ROUTING_MODEL.name(), rm.name()));
        }

        NodeModel nm = problem.getNodeModel();
        if(nm == NodeModel.NODE_HARDWARE) {
            errors.add(SNDlibMessages.error(ErrorKeys.MODEL_NOT_YET_SUPPORTED,
                ModelProperty.NODE_MODEL.name(), nm.name()));
        }

        LinkModel lm = problem.getLinkModel();
        DemandModel dm = problem.getDemandModel();
        if((lm == LinkModel.UNDIRECTED && dm != DemandModel.UNDIRECTED)
            || ((lm == LinkModel.DIRECTED || lm == LinkModel.BIDIRECTED) && dm != DemandModel.DIRECTED)) {
            errors.add(SNDlibMessages.error(ErrorKeys.INVALID_MODEL_COMBINATION,
                ModelProperty.DEMAND_MODEL.name(), ModelProperty.LINK_MODEL.name(),
                dm.name(), lm.name()));
        }
    }
}
