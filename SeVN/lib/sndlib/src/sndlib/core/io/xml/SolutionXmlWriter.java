/*
 * $Id: SolutionXmlWriter.java 429 2008-01-23 14:44:07Z roman.klaehne $
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
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import sndlib.core.io.SNDlibWriteException;
import sndlib.core.io.xml.castorgen.solution.DemandRoutingEl;
import sndlib.core.io.xml.castorgen.solution.DemandRoutingElItem;
import sndlib.core.io.xml.castorgen.solution.DemandRoutingsEl;
import sndlib.core.io.xml.castorgen.solution.DemandRoutingsElItem;
import sndlib.core.io.xml.castorgen.solution.FlowPathEl;
import sndlib.core.io.xml.castorgen.solution.InstalledModuleEl;
import sndlib.core.io.xml.castorgen.solution.LinkConfigurationEl;
import sndlib.core.io.xml.castorgen.solution.LinkConfigurationElItem;
import sndlib.core.io.xml.castorgen.solution.LinkConfigurationsEl;
import sndlib.core.io.xml.castorgen.solution.LinkConfigurationsElItem;
import sndlib.core.io.xml.castorgen.solution.RoutingPathEl;
import sndlib.core.io.xml.castorgen.solution.RoutingPathElItem;
import sndlib.core.io.xml.castorgen.solution.SolutionEl;
import sndlib.core.problem.OperatingState;
import sndlib.core.solution.DemandRouting;
import sndlib.core.solution.FlowPath;
import sndlib.core.solution.LinkConfiguration;
import sndlib.core.solution.ModuleConfiguration;
import sndlib.core.solution.Solution;

import com.atesio.utils.xml.XmlException;
import com.atesio.utils.xml.XmlFacade;

/**
 * This class provides the method to write a {@link sndlib.core.solution.Solution} 
 * to the XML format of SNDlib.<br/>
 * <br/>
 * 
 * The SNDlib XML format of a solution file is described on the 
 * <a target="_blank" 
 * href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html">
 * SNDlib webpage</a><br/><br/>
 * 
 * This class is considered as an internal helper of this package.
 * 
 * @see SNDlibXmlWriter
 * @see sndlib.core.solution.Solution
 * 
 * @author Roman Klaehne
 */
class SolutionXmlWriter {

    /**
     * Writes the given solution to the specified target writer.
     * 
     * @param solution the solution to write
     * @param target the target to which the solution is written
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibWriteException if an error concerning the IO-format
     * occured
     */
    static void writeSolution(Solution solution, Writer target) throws IOException,
        SNDlibWriteException {

        SolutionEl solutionEl = solution2Xsd(solution);

        try {
            XmlFacade.marshal(solutionEl, target);
        }
        catch (XmlException xmlx) {
            throw new SNDlibWriteException("error marshalling solution to xml: "
                + xmlx.getMessage(), xmlx);
        }
    }

    /**
     * Transforms the given <tt>Solution</tt> into its corresponding 
     * XML-element.
     * 
     * @param solution the solution
     * 
     * @return the XML-element corresponding to the specified solution
     */
    private static SolutionEl solution2Xsd(Solution solution) {

        SolutionEl solutionEl = new SolutionEl();

        LinkConfigurationsEl linkConfsEl = createLinkConfsEl(solution);
        solutionEl.setLinkConfigurationsEl(linkConfsEl);

        for(Map.Entry<OperatingState, Collection<DemandRouting>> routingsInState : solution.allRoutings().entrySet()) {
            DemandRoutingsEl demandRoutingsEl = createDemandRoutingsEl(
                routingsInState.getKey(), routingsInState.getValue());
            solutionEl.addDemandRoutingsEl(demandRoutingsEl);
        }

        return solutionEl;
    }

    /**
     * Transforms the <tt>LinkConfiguration</tt>s of the given solution into 
     * their corresponding XML-element.
     * 
     * @param solution the solution
     * 
     * @return the XML-element corresponding to the link configurations of 
     * the specified solution
     */
    private static LinkConfigurationsEl createLinkConfsEl(Solution solution) {

        LinkConfigurationsEl linkConfsEl = new LinkConfigurationsEl();

        for(LinkConfiguration linkConf : solution.linkConfigs()) {

            LinkConfigurationEl linkConfEl = new LinkConfigurationEl();

            linkConfEl.setLinkId(linkConf.getLinkId());

            for(ModuleConfiguration moduleConf : linkConf.moduleConfigs()) {

                InstalledModuleEl instModuleEl = new InstalledModuleEl();

                instModuleEl.setCapacityEl(moduleConf.getModuleCapacity());
                instModuleEl.setInstallCountEl(moduleConf.getInstallCount());

                LinkConfigurationElItem linkConfElItem = new LinkConfigurationElItem();
                linkConfElItem.setInstalledModuleEl(instModuleEl);
                linkConfEl.addLinkConfigurationElItem(linkConfElItem);
            }

            LinkConfigurationsElItem linkConfsItem = new LinkConfigurationsElItem();
            linkConfsItem.setLinkConfigurationEl(linkConfEl);
            linkConfsEl.addLinkConfigurationsElItem(linkConfsItem);
        }

        return linkConfsEl;
    }

    /**
     * Transforms the state-dependent <tt>DemandRouting</tt>s of the given 
     * solution into their corresponding XML-element.
     * 
     * @param solution the solution
     * 
     * @return the XML-element corresponding to the demand routings of 
     * the specified solution
     */
    private static DemandRoutingsEl createDemandRoutingsEl(OperatingState state,
        Collection<DemandRouting> routingsInState) {

        DemandRoutingsEl demandRoutingsEl = new DemandRoutingsEl();
        demandRoutingsEl.setState(state.getName());

        for(DemandRouting routing : routingsInState) {

            DemandRoutingEl demandRoutingEl = new DemandRoutingEl();

            demandRoutingEl.setDemandId(routing.getDemandId());

            for(FlowPath flowPath : routing.flowPaths()) {

                FlowPathEl flowPathEl = new FlowPathEl();
                flowPathEl.setFlowPathValueEl(flowPath.flowPathValue());

                RoutingPathEl routingPathEl = new RoutingPathEl();

                for(String linkId : flowPath.linkIds()) {

                    RoutingPathElItem pathElItem = new RoutingPathElItem();
                    pathElItem.setLinkIdEl(linkId);

                    routingPathEl.addRoutingPathElItem(pathElItem);
                }
                flowPathEl.setRoutingPathEl(routingPathEl);

                DemandRoutingElItem demandRoutingElItem = new DemandRoutingElItem();
                demandRoutingElItem.setFlowPathEl(flowPathEl);
                demandRoutingEl.addDemandRoutingElItem(demandRoutingElItem);
            }
            DemandRoutingsElItem demandRoutingsElItem = new DemandRoutingsElItem();
            demandRoutingsElItem.setDemandRoutingEl(demandRoutingEl);
            demandRoutingsEl.addDemandRoutingsElItem(demandRoutingsElItem);
        }
        return demandRoutingsEl;
    }
}
