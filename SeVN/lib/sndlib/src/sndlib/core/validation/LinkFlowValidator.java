/*
 * $Id: LinkFlowValidator.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import sndlib.core.network.Link;
import sndlib.core.problem.SolvedProblem;
import sndlib.core.util.DoubleComparator;
import sndlib.core.util.MaximumLinkFlow;
import sndlib.core.util.SNDlibMessages;
import sndlib.core.util.SolutionUtils;

import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This validator checks whether the link flows do not exceed the installed
 * link capacities with respect to a specific link and survivability model.
 * 
 * @see sndlib.core.model.LinkModel
 * @see sndlib.core.network.Link
 * 
 * @author Roman Klaehne
 */
class LinkFlowValidator implements SolutionValidator {

    private static class ErrorKeys {

        final static MessageKey DIRECTED_LINK_CAPACITY_EXCEEDED = new SimpleMessageKey(
            "validator.solution.error.directedLinkCapacityExceeded");

        final static MessageKey UNDIRECTED_LINK_CAPACITY_EXCEEDED = new SimpleMessageKey(
            "validator.solution.error.undirectedLinkCapacityExceeded");

        final static MessageKey BIDIRECTED_POS_LINK_CAPACITY_EXCEEDED = new SimpleMessageKey(
            "validator.solution.error.bidirectedPosLinkCapacityExceeded");

        final static MessageKey BIDIRECTED_NEG_LINK_CAPACITY_EXCEEDED = new SimpleMessageKey(
            "validator.solution.error.bidirectedNegLinkCapacityExceeded");
    }

    /**
     * Constructs a new instance of this validator.
     */
    LinkFlowValidator() {

        /* nothing to initialize */
    }

    /**
     * This method checks whether for each operating state the link flows in 
     * the given solution do not exceed the installed link capacities with 
     * respect to a specific link and survivability model.
     * 
     * @param solvedProblem the solved problem to validate
     * @param errors the message container into those the errors are put
     * 
     * @see sndlib.core.model.LinkModel
     */
    public void validate(SolvedProblem solvedProblem, Messages errors) {

        switch (solvedProblem.getLinkModel()) {

        case DIRECTED:
            validateDirectedCase(solvedProblem, errors);
            break;

        case UNDIRECTED:
            validateUndirectedCase(solvedProblem, errors);
            break;

        case BIDIRECTED:
            validateBidirectedCase(solvedProblem, errors);
            break;
        }
    }

    /**
     * Performs the validation in case of a directed link model.
     * 
     * @param solvedProblem the solved problem to validate
     * @param errors the message container into those the errors are put
     */
    private static void validateDirectedCase(SolvedProblem solvedProblem,
        Messages errors) {

        for(Link link : solvedProblem.getNetwork().links()) {

            double installedCapacity = SolutionUtils.getTotalInstalledCapacity(link,
                solvedProblem);

            MaximumLinkFlow maxLinkFlow = SolutionUtils.calculateMaximumLinkFlow(
                link, solvedProblem);

            if(DoubleComparator.getInstance().less(installedCapacity,
                maxLinkFlow.getMaxPositiveFlow())) {
                errors.add(SNDlibMessages.error(
                    ErrorKeys.DIRECTED_LINK_CAPACITY_EXCEEDED,
                    maxLinkFlow.getMaxPositiveFlow(), link.getId(),
                    maxLinkFlow.getMaxPositiveFlowAssumedState(), installedCapacity));
            }
        }
    }

    /**
     * fPerforms the validation in case of an undirected link model.
     * 
     * @param solvedProblem the solved problem to validate
     * @param errors the message container into those the errors are put
     */
    private static void validateUndirectedCase(SolvedProblem solvedProblem,
        Messages errors) {

        for(Link link : solvedProblem.getNetwork().links()) {

            double installedCapacity = SolutionUtils.getTotalInstalledCapacity(link,
                solvedProblem);

            MaximumLinkFlow maxLinkFlow = SolutionUtils.calculateMaximumLinkFlow(
                link, solvedProblem);

            if(DoubleComparator.getInstance().less(installedCapacity,
                maxLinkFlow.getMaxTotalFlow())) {
                errors.add(SNDlibMessages.error(
                    ErrorKeys.UNDIRECTED_LINK_CAPACITY_EXCEEDED,
                    maxLinkFlow.getMaxTotalFlow(), link.getId(),
                    maxLinkFlow.getMaxTotalFlowAssumedState(), installedCapacity));
            }
        }
    }

    /**
     * Performs the validation in case of a bidirected link model.
     * 
     * @param solvedProblem the solved problem to validate
     * @param errors the message container into those the errors are put
     */
    private static void validateBidirectedCase(SolvedProblem solvedProblem,
        Messages errors) {

        for(Link link : solvedProblem.getNetwork().links()) {

            double installedCapacity = SolutionUtils.getTotalInstalledCapacity(link,
                solvedProblem);

            MaximumLinkFlow maxLinkFlow = SolutionUtils.calculateMaximumLinkFlow(
                link, solvedProblem);

            if(DoubleComparator.getInstance().less(installedCapacity,
                maxLinkFlow.getMaxPositiveFlow())) {
                errors.add(SNDlibMessages.error(
                    ErrorKeys.BIDIRECTED_POS_LINK_CAPACITY_EXCEEDED,
                    maxLinkFlow.getMaxPositiveFlow(), link.getId(),
                    maxLinkFlow.getMaxPositiveFlowAssumedState(), installedCapacity));
            }
            if(DoubleComparator.getInstance().less(installedCapacity,
                maxLinkFlow.getMaxNegativeFlow())) {
                errors.add(SNDlibMessages.error(
                    ErrorKeys.BIDIRECTED_NEG_LINK_CAPACITY_EXCEEDED,
                    maxLinkFlow.getMaxNegativeFlow(), link.getId(),
                    maxLinkFlow.getMaxNegativeFlowAssumedState(), installedCapacity));
            }
        }
    }

}
