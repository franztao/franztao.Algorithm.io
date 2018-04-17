/*
 * $Id: SolutionXmlParser.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

package sndlib.core.io.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import sndlib.core.io.SNDlibParseException;
import sndlib.core.io.xml.castorgen.solution.DemandRoutingEl;
import sndlib.core.io.xml.castorgen.solution.DemandRoutingsEl;
import sndlib.core.io.xml.castorgen.solution.FlowPathEl;
import sndlib.core.io.xml.castorgen.solution.InstalledModuleEl;
import sndlib.core.io.xml.castorgen.solution.LinkConfigurationEl;
import sndlib.core.io.xml.castorgen.solution.LinkConfigurationsEl;
import sndlib.core.io.xml.castorgen.solution.RoutingPathEl;
import sndlib.core.io.xml.castorgen.solution.SolutionEl;
import sndlib.core.problem.OperatingState;
import sndlib.core.solution.DemandRouting;
import sndlib.core.solution.LinkConfiguration;
import sndlib.core.solution.ModuleConfiguration;
import sndlib.core.solution.Solution;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;
import com.atesio.utils.xml.XmlException;
import com.atesio.utils.xml.XmlFacade;
import com.atesio.utils.xml.XmlValidationException;

/**
 * This class provides the method to a parse a solution file written in the 
 * XML format of SNDlib to produce a {@link sndlib.core.solution.Solution} 
 * instance.
 * <br/><br/>
 * 
 * The SNDlib XML format of a solution file is described on the 
 * <a target="_blank" 
 * href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html">
 * SNDlib webpage</a><br/><br/>
 * 
 * This class is considered as an internal helper of this package.
 * 
 * @see SNDlibXmlParser
 * @see sndlib.core.solution.Solution
 * 
 * @author Roman Klaehne
 */
class SolutionXmlParser {

    /**
     * Lists the errors which may be produced by this parser.
     */
    private static class ErrorKeys {

        final static MessageKey DUPLICATE_LINK_CONF = new SimpleMessageKey(
            "parser.xml.solution.error.duplicateLinkConf");

        final static MessageKey DUPLICATE_MODULE_CONF = new SimpleMessageKey(
            "parser.xml.solution.error.duplicateModuleConf");

        final static MessageKey DUPLICATE_ROUTING = new SimpleMessageKey(
            "parser.xml.solution.error.duplicateRouting");

        final static MessageKey LINK_ID_IN_ROUTING_PATH_NOT_FOUND = new SimpleMessageKey(
            "parser.xml.solution.error.linkIdInRoutingPathNotFound");

        final static MessageKey NO_FLOW_PATH_IN_ROUTING = new SimpleMessageKey(
            "parser.xml.solution.error.noFlowPathInRouting");
    }

    /**
     * Parses the given source in order to produce a <tt>Solution</tt>.
     * 
     * @param source the source to parse the solution from
     * 
     * @return the parsed <tt>Solution</tt>
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibParseException if an error concerning the syntax or
     * consistency occured
     */
    static Solution parseSolution(Reader source) throws IOException,
        SNDlibParseException {

        SolutionEl solutionEl = null;

        try {
            solutionEl = XmlFacade.unmarshal(source, SolutionEl.class,
                SNDlibXmlValidator.SOLUTION_SCHEMA);
        }
        catch (XmlException xmlx) {
            throw new SNDlibParseException("error parsing solution xml: "
                + xmlx.getMessage(), xmlx);
        }
        catch (XmlValidationException xmlvx) {
            throw new SNDlibParseException(xmlvx.getErrors());
        }

        Messages errors = new Messages();
        Solution solution = xsd2Solution(solutionEl, errors);

        if(errors.size() > 0) {
            throw new SNDlibParseException(errors);
        }

        return solution;
    }

    /**
     * Transforms the given XML-element into a <tt>Solution</tt> object.<br/>
     * <br/>
     * 
     * Errors occured while transforming (e.g. regarding the consistency) are
     * put to the given error container.
     * 
     * @param solutionEl the XML-element
     * @param errors the error container
     * 
     * @return a <tt>Solution</tt> instance
     */
    private static Solution xsd2Solution(SolutionEl solutionEl, Messages errors) {

        Solution solution = new Solution();

        LinkConfigurationsEl linkConfsEl = solutionEl.getLinkConfigurationsEl();
        readLinkConfigurations(linkConfsEl, solution, errors);

        for(int i = 0; i < solutionEl.getDemandRoutingsElCount(); ++i) {
            readDemandRoutings(solutionEl.getDemandRoutingsEl(i), solution, errors);
        }

        return solution;
    }

    /**
     * Transforms the given XML-element into a set of 
     * <tt>LinkConfiguration</tt>s and puts them into the given solution.
     * <br/><br/>
     * 
     * Errors occured while transforming (e.g. regarding the consistency) are
     * put to the given error container.
     * 
     * @param linkConfsEl the XML-element containing the link configuration 
     * data
     * @param solution the solution
     * @param errors the error container
     */
    private static void readLinkConfigurations(LinkConfigurationsEl linkConfsEl,
        Solution solution, Messages errors) {

        for(int i = 0; i < linkConfsEl.getLinkConfigurationsElItemCount(); ++i) {
            LinkConfigurationEl linkConfEl = linkConfsEl.getLinkConfigurationsElItem(
                i).getLinkConfigurationEl();

            String linkId = linkConfEl.getLinkId();
            if(solution.hasLinkConfig(linkId)) {
                errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_LINK_CONF, linkId));
                continue;
            }

            LinkConfiguration linkConf = solution.newLinkConfig(linkId);
            for(int j = 0; j < linkConfEl.getLinkConfigurationElItemCount(); ++j) {
                InstalledModuleEl instModule = linkConfEl.getLinkConfigurationElItem(
                    j).getInstalledModuleEl();

                double moduleCap = instModule.getCapacityEl();
                double installCount = instModule.getInstallCountEl();
                if(linkConf.hasModuleConfig(moduleCap)) {
                    errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_MODULE_CONF,
                        moduleCap, linkId));
                    continue;
                }
                linkConf.addModuleConfig(new ModuleConfiguration(moduleCap,
                    installCount));
            }
        }
    }

    /**
     * Transforms the given XML-element into a set of <tt>DemandRouting</tt>s 
     * and puts them into the given solution.<br/>
     * <br/>
     * 
     * Errors occured while transforming (e.g. regarding the consistency) are
     * put to the given error container.
     * 
     * @param demandRoutingsEl the XML-element containing the routings data
     * @param solution the solution
     * @param errors the error container
     */
    private static void readDemandRoutings(DemandRoutingsEl demandRoutingsEl,
        Solution solution, Messages errors) {

        String stateName = demandRoutingsEl.getState();
        OperatingState state = null;
        if(stateName == null) {
            state = OperatingState.NOS;
        }
        else {
            state = OperatingState.getInstance(stateName);
        }

        for(int i = 0; i < demandRoutingsEl.getDemandRoutingsElItemCount(); ++i) {
            DemandRoutingEl demandRouting = demandRoutingsEl.getDemandRoutingsElItem(
                i).getDemandRoutingEl();

            String demandId = demandRouting.getDemandId();

            DemandRouting existingRouting = solution.getRouting(demandId, state);
            if(existingRouting != null && existingRouting.getState().equals(state)) {
                errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_ROUTING, demandId));
                continue;
            }
            DemandRouting routing = solution.newRouting(demandId, state);

            nextFlowPath: for(int j = 0; j < demandRouting.getDemandRoutingElItemCount(); ++j) {
                FlowPathEl flowPathEl = demandRouting.getDemandRoutingElItem(j).getFlowPathEl();

                double flowPathValue = flowPathEl.getFlowPathValueEl();
                RoutingPathEl path = flowPathEl.getRoutingPathEl();

                List<String> linkIds = new LinkedList<String>();
                for(int k = 0; k < path.getRoutingPathElItemCount(); ++k) {
                    String linkId = path.getRoutingPathElItem(k).getLinkIdEl();

                    if(!solution.hasLinkConfig(linkId)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.LINK_ID_IN_ROUTING_PATH_NOT_FOUND, linkId,
                            demandId));
                        continue nextFlowPath;
                    }
                    linkIds.add(linkId);
                }
                solution.newFlowPath(routing, flowPathValue, linkIds);
            }
            if(routing.flowPathCount() == 0) {
                errors.add(SNDlibMessages.error(ErrorKeys.NO_FLOW_PATH_IN_ROUTING,
                    demandId));
            }
        }
    }
}
