/*
 * $Id: SNDlibNativeWriter.java 631 2011-04-27 08:16:04Z bzfraack $
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

package sndlib.core.io.sndnative;

import static com.atesio.utils.StringFormatUtils.NEW_LINE;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.ADMISSIBLE_PATHS;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.DEMANDS;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.DEMAND_DESC_LINE;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.LINKS;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.LINK_DESC_LINE;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.META_DESC_LINE;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.NODES;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.NODE_DESC_LINE;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.PATHS_DESC_LINE;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.PATH_UNLIMITED;
import static sndlib.core.io.sndnative.SolutionNativeSyntax.DEMAND_ROUTINGS_DESC_LINE;
import static sndlib.core.io.sndnative.SolutionNativeSyntax.LINK_CONFIGURATIONS;
import static sndlib.core.io.sndnative.SolutionNativeSyntax.LINK_CONF_DESC_LINE;
import static sndlib.core.io.sndnative.SolutionNativeSyntax.ROUTINGS;
import static sndlib.core.util.DoubleFormat.formatLong;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import sndlib.core.io.SNDlibIOFormat;
import sndlib.core.io.SNDlibWriter;
import sndlib.core.io.sndnative.SNDlibNativeHeader.FileType;
import sndlib.core.model.Model;
import sndlib.core.model.ModelProperty;
import sndlib.core.network.AdmissiblePath;
import sndlib.core.network.CapacityModule;
import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.OperatingState;
import sndlib.core.solution.DemandRouting;
import sndlib.core.solution.FlowPath;
import sndlib.core.solution.LinkConfiguration;
import sndlib.core.solution.ModuleConfiguration;
import sndlib.core.solution.Solution;

/**
 * This {@link SNDlibWriter} implementation handles the SNDlib native format.
 * <br/><br/>
 * 
 * Instances of this class should not be created directly but obtained by 
 * calling the static factory method of {@link sndlib.core.io.SNDlibIOFactory}.
 * <br/><br/>
 * 
 * The SNDlib native format is described on the <a target="_blank" 
 * href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html">
 * SNDlib webpage</a>
 * 
 * @see sndlib.core.io.SNDlibIOFactory
 * @see SNDlibIOFormat#NATIVE
 * 
 * @author Roman Klaehne
 */
public class SNDlibNativeWriter implements SNDlibWriter {

    /**
     * The amount of spaces nested elements are intended.
     */
    private static final int INTEND = 2;

    /**
     * @inheritDoc
     */
    public void writeNetwork(Network network, Writer target) throws IOException {

        SNDlibNativeHeader.write(FileType.network.name(), "1.0", target);
        target.write("# network " + network.getName() + NEW_LINE + NEW_LINE);

        if(network.getMeta() != null) {
            
        	target.write("# META SECTION" + NEW_LINE + "#" + NEW_LINE);
        	target.write("# " + META_DESC_LINE  + NEW_LINE + NEW_LINE);
        	target.write("META" + " (" + NEW_LINE);
        	if(network.getMeta().getGranularity() !=  null)
        		target.write("  granularity "+ " = " + network.getMeta().getGranularity() + NEW_LINE);
        	if(network.getMeta().getTime() !=  null)
        		target.write("  time "+ " = " + network.getMeta().getTime()  +NEW_LINE);
        	if(network.getMeta().getUnit() !=  null)
        		target.write("  unit "+ " = " +network.getMeta().getUnit()  + NEW_LINE);
        	if(network.getMeta().getOrigin() !=  null)
        		target.write("  origin " + " = " + network.getMeta().getOrigin() + NEW_LINE);
        	target.write(")" + NEW_LINE + NEW_LINE);
        }
        	
        	
        target.write("# NODE SECTION" + NEW_LINE + "#" + NEW_LINE);
        target.write("# " + NODE_DESC_LINE + NEW_LINE + NEW_LINE);

        target.write(NODES + " (" + NEW_LINE);
        for(Node node : network.nodes()) {
            intend(target).write(node.getId() + " ( ");

            target.write(formatLong(node.getXCoordinate()) + " ");
            target.write(formatLong(node.getYCoordinate()) + " )" + NEW_LINE);
        }
        target.write(")" + NEW_LINE + NEW_LINE);

        target.write("# LINK SECTION" + NEW_LINE + "#" + NEW_LINE);
        target.write("# " + LINK_DESC_LINE + NEW_LINE + NEW_LINE);

        target.write(LINKS + " (" + NEW_LINE);
        for(Link link : network.links()) {
            intend(target).write(link.getId() + " ( ");

            target.write(link.getFirstNode().getId() + " ");
            target.write(link.getSecondNode().getId() + " ) ");

            target.write(formatLong(link.getPreCapacity()) + " ");
            target.write(formatLong(link.getPreCost()) + " ");
            target.write(formatLong(link.getRoutingCost()) + " ");
            target.write(formatLong(link.getSetupCost()) + " ( ");

            for(CapacityModule module : link.modules()) {
                target.write(formatLong(module.getCapacity()) + " ");
                target.write(formatLong(module.getCost()) + " ");
            }
            target.write(")" + NEW_LINE);
        }
        target.write(")" + NEW_LINE + NEW_LINE);

        target.write("# DEMAND SECTION" + NEW_LINE + "#" + NEW_LINE);
        target.write("# " + DEMAND_DESC_LINE + NEW_LINE + NEW_LINE);

        target.write(DEMANDS + " (" + NEW_LINE);
        for(Demand demand : network.demands()) {
            intend(target).write(demand.getId() + " ( ");

            target.write(demand.getFirstNode().getId() + " ");
            target.write(demand.getSecondNode().getId() + " ) ");

            target.write(demand.getRoutingUnit() + " ");
            target.write(formatLong(demand.getDemandValue()) + " ");

            if(!demand.hasMaxPathLength()) {
                target.write(PATH_UNLIMITED);
            }
            else {
                target.write(demand.getMaxPathLength() + "");
            }
            target.write(NEW_LINE);
        }
        target.write(")" + NEW_LINE + NEW_LINE);

        target.write("# ADMISSIBLE PATHS SECTION" + NEW_LINE + "#" + NEW_LINE);
        target.write("# " + PATHS_DESC_LINE + NEW_LINE + NEW_LINE);

        target.write(ADMISSIBLE_PATHS + " ( " + NEW_LINE);
        for(Demand demand : network.demands()) {

            if(demand.admissiblePathCount() > 0) {
                intend(target).write(demand.getId() + " (" + NEW_LINE);

                for(AdmissiblePath path : demand.admissiblePaths()) {
                    intend(target, 2).write(path.getId() + " (");

                    for(Link link : path.links()) {
                        target.write(" " + link.getId());
                    }
                    target.write(" )" + NEW_LINE);
                }
                intend(target).write(")" + NEW_LINE);
            }
        }
        target.write(")");
    }

    /**
     * @inheritDoc
     */
    public void writeSolution(Solution solution, Writer target) throws IOException {

        SNDlibNativeHeader.write(FileType.solution.name(), "1.0", target);
        target.write("# solution " + solution.getName() + NEW_LINE + NEW_LINE);

        target.write("# LINK CONFIGURATIONS" + NEW_LINE + "#" + NEW_LINE);
        target.write("# " + LINK_CONF_DESC_LINE + NEW_LINE + NEW_LINE);
        target.write(LINK_CONFIGURATIONS + " (" + NEW_LINE);
        for(LinkConfiguration linkConf : solution.linkConfigs()) {
            intend(target).write(linkConf.getLinkId() + " (");

            for(ModuleConfiguration moduleConf : linkConf.moduleConfigs()) {
                target.write(" " + formatLong(moduleConf.getModuleCapacity()) + " "
                    + formatLong(moduleConf.getInstallCount()));
            }
            target.write(" )" + NEW_LINE);
        }
        target.write(")" + NEW_LINE + NEW_LINE);

        target.write("# ROUTING" + NEW_LINE + "#" + NEW_LINE);
        target.write("# " + DEMAND_ROUTINGS_DESC_LINE + NEW_LINE + NEW_LINE);
        for(Map.Entry<OperatingState, Collection<DemandRouting>> routingsInState : solution.allRoutings().entrySet()) {
            target.write(ROUTINGS + " " + routingsInState.getKey().getName() + " ( "
                + NEW_LINE);
            for(DemandRouting routing : routingsInState.getValue()) {
                intend(target).write(routing.getDemandId() + " (" + NEW_LINE);

                for(FlowPath flowPath : routing.flowPaths()) {
                    intend(target, 2).write(
                        formatLong(flowPath.flowPathValue()) + " (");

                    for(String linkId : flowPath.linkIds()) {
                        target.write(" " + linkId);
                    }
                    target.write(" )" + NEW_LINE);
                }
                intend(target).write(")" + NEW_LINE);
            }
            target.write(")" + NEW_LINE + NEW_LINE);
        }
    }

    /**
     * @inheritDoc
     */
    public void writeModel(Model model, Writer target) throws IOException {

        SNDlibNativeHeader.write(FileType.model.name(), "1.0", target);
        target.write("# model " + model.getName() + NEW_LINE + NEW_LINE);

        target.write(ModelProperty.LINK_MODEL + " = " + model.getLinkModel().name()
            + NEW_LINE);

        target.write(ModelProperty.NODE_MODEL + " = " + model.getNodeModel().name()
            + NEW_LINE);

        target.write(ModelProperty.DEMAND_MODEL + " = "
            + model.getDemandModel().name() + NEW_LINE);

        target.write(ModelProperty.LINK_CAPACITY_MODEL + " = "
            + model.getLinkCapacityModel().name() + NEW_LINE);

        target.write(ModelProperty.ROUTING_MODEL + " = "
            + model.getRoutingModel().name() + NEW_LINE);

        target.write(ModelProperty.ADMISSIBLE_PATH_MODEL + " = "
            + model.getAdmissiblePathModel().name() + NEW_LINE);

        target.write(ModelProperty.HOP_LIMIT_MODEL + " = "
            + model.getHopLimitModel().name() + NEW_LINE);

        target.write(ModelProperty.SURVIVABILITY_MODEL + " = "
            + model.getSurvivabilityModel().name() + NEW_LINE);

        target.write(ModelProperty.FIXED_CHARGE_MODEL + " = "
            + model.getFixedChargeModel().name() + NEW_LINE);

        target.write(ModelProperty.OBJECTIVE_MODEL + " = "
            + model.getObjectiveModel().name());
    }

    /**
     * Intends the given writer about the amount of spaces specified by
     * {@link #INTEND}.
     * 
     * @param writer the writer
     * 
     * @throws IOException if an IO error occured
     */
    private static Writer intend(Writer writer) throws IOException {

        return intend(writer, 1);
    }

    /**
     * Intends the given writer about <tt>depth</tt> times the amount of 
     * spaces specified by {@link #INTEND}.
     * 
     * @param writer the writer
     * @param depth the depth of the element being intented
     * 
     * @throws IOException if an IO error occured
     */
    private static Writer intend(Writer writer, int depth) throws IOException {

        int intend = depth * INTEND;

        for(int i = 0; i < intend; ++i) {
            writer.write(" ");
        }
        return writer;
    }

    /**
     * Returns {@link SNDlibIOFormat#NATIVE}.
     * 
     * @return {@link SNDlibIOFormat#NATIVE}
     */
    public String getFormat() {

        return SNDlibIOFormat.NATIVE;
    }
}
