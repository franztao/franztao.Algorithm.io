/*
 * $Id: SolutionUtils.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.util;

import static com.atesio.utils.ArgChecker.checkNotNull;

import java.util.List;
import java.util.Set;

import sndlib.core.model.LinkModel;
import sndlib.core.network.CapacityModule;
import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Node;
import sndlib.core.problem.DemandLinkFlow;
import sndlib.core.problem.DemandPathFlow;
import sndlib.core.problem.LinkFlow;
import sndlib.core.problem.OperatingState;
import sndlib.core.problem.SolvedProblem;
import sndlib.core.solution.DemandRouting;
import sndlib.core.solution.FlowPath;
import sndlib.core.solution.LinkConfiguration;
import sndlib.core.solution.ModuleConfiguration;

/**
 * This class provides some commonly used utility methods on a 
 * {@link sndlib.core.solution.Solution} (resp. a {@link SolvedProblem}).
 * 
 * @author Roman Klaehne
 */
public class SolutionUtils {

    /**
     * Determines the in-degree of the given node with respect to the 
     * given solution topology.<br/>
     * <br/>
     * 
     * If the given problem's <tt>LinkModel</tt> is <tt>DIRECTED</tt> then the 
     * in-degree of a node is the number of its incident links of which that 
     * node is the target.
     * <br/><br/>
     * 
     * If the <tt>LinkModel</tt> is not <tt>DIRECTED</tt> then there is 
     * no definite target node. Thus the in-degree of a node is simply the 
     * number of its incident links.
     * 
     * @param node the node to calculate the in-degree for
     * @param solvedProblem the solved problem providing the solution topology
     * 
     * @return the in-degree of the given node with respect to the given 
     * solution topology.
     */
    public static int getNodeInDegree(Node node, SolvedProblem solvedProblem) {

        checkNotNull(node, "node");
        checkNotNull(solvedProblem, "solved problem");

        int nodeInDegree = 0;

        for(Link link : NetworkUtils.getIncidentLinks(node,
            solvedProblem.getNetwork())) {

            LinkConfiguration linkConf = solvedProblem.getLinkConf(link);

            if(linkConf != null
                && SolutionUtils.getTotalInstalledCapacity(linkConf) > 0) {

                if(solvedProblem.getLinkModel() != LinkModel.DIRECTED
                    || link.getSecondNode() == node) {
                    nodeInDegree++;
                }
            }
        }
        return nodeInDegree;
    }

    /**
     * Determines the out-degree of the given node with respect to the 
     * given solution topology.<br/>
     * <br/>
     * 
     * If the given problem's <tt>LinkModel</tt> is <tt>DIRECTED</tt> then the 
     * out-degree of a node is the number of its incident links of which that 
     * node is the source.
     * <br/><br/>
     * 
     * If the <tt>LinkModel</tt> is not <tt>DIRECTED</tt> then there is 
     * no definite target node. Thus the out-degree of a node is simply the 
     * number of its incident links.
     * 
     * @param node the node to calculate the out-degree for
     * @param solvedProblem the solved problem providing the solution topology
     * 
     * @return the out-degree of the given node with respect to the given 
     * solution topology.
     */
    public static int getNodeOutDegree(Node node, SolvedProblem solvedProblem) {

        checkNotNull(node, "node");
        checkNotNull(solvedProblem, "solved problem");

        int nodeOutDegree = 0;

        for(Link link : NetworkUtils.getIncidentLinks(node,
            solvedProblem.getNetwork())) {

            LinkConfiguration linkConf = solvedProblem.getLinkConf(link);

            if(linkConf != null
                && SolutionUtils.getTotalInstalledCapacity(linkConf) > 0) {

                if(solvedProblem.getLinkModel() != LinkModel.DIRECTED
                    || link.getFirstNode() == node) {
                    nodeOutDegree++;
                }
            }
        }
        return nodeOutDegree;
    }

    /**
     * Determines the degree of the given node with respect to the given 
     * solution topology.<br/>
     * <br/>
     * 
     * The degree of a given node is simply the number of its incident links.
     * 
     * @param node the node to calculate the degree for
     * @param solvedProblem the solved problem providing the solution topology
     * 
     * @return the degree of the given node with respect to the given solution
     * topology
     */
    public static int getNodeDegree(Node node, SolvedProblem solvedProblem) {

        checkNotNull(node, "node");
        checkNotNull(solvedProblem, "solved problem");

        int nodeDegree = 0;

        for(Link link : NetworkUtils.getIncidentLinks(node,
            solvedProblem.getNetwork())) {

            LinkConfiguration linkConf = solvedProblem.getLinkConf(link);

            if(linkConf != null
                && SolutionUtils.getTotalInstalledCapacity(linkConf) > 0) {
                nodeDegree++;
            }
        }
        return nodeDegree;
    }

    /**
     * Returns the total amount of capacity installed on the given link
     * with respect to the given solution topology.
     * 
     * @param link the link
     * @param solvedProblem the solved problem providing the solution 
     * topology
     * 
     * @return the total amount of capacity installed on the given link
     */
    public static double getTotalInstalledCapacity(Link link,
        SolvedProblem solvedProblem) {

        checkNotNull(link, "link");
        checkNotNull(solvedProblem, "solved problem");

        LinkConfiguration linkConf = solvedProblem.getLinkConf(link);
        if(linkConf == null) {
            return 0;
        }

        return getTotalInstalledCapacity(linkConf);
    }

    /**
     * Returns the total amount of capacity installed on the link 
     * corresponding to the given link configuration.
     * 
     * @param linkConf the link configuration providing the capacities
     * installed on the link 
     * 
     * @return the total amount of capacity installed on the link 
     * corresponding to the given link configuration
     */
    public static double getTotalInstalledCapacity(LinkConfiguration linkConf) {

        checkNotNull(linkConf, "link configuration");

        double cap = 0;

        for(ModuleConfiguration modConf : linkConf.moduleConfigs()) {
            cap += (modConf.getModuleCapacity() * modConf.getInstallCount());
        }
        return cap;
    }

    /**
     * Returns the total cost of the capacities installed on the given link
     * with respect to the given link configuration. The costs comprises the 
     * pre-installed capacity as well as the additionally installed capacities.
     * 
     * @param link the link 
     * @param linkConf the link configuration providing the capacities
     * installed on the link 
     * 
     * @return the total cost of the capacity installed on the given link 
     */
    public static double getTotalInstalledCapacityCost(Link link,
        LinkConfiguration linkConf) {

        return getAdditionalModuleCapacityCost(link, linkConf) + link.getPreCost();
    }

    /**
     * Returns the cost of the capacities additionally installed on the given 
     * link with respect to the given link configuration. The pre-installed 
     * capacity cost is not considered.
     * 
     * @param link the link 
     * @param linkConf the link configuration providing the capacities
     * installed on the link 
     * 
     * @return the cost of the capacities additionally installed on the given 
     * link 
     */
    public static double getAdditionalModuleCapacityCost(Link link,
        LinkConfiguration linkConf) {

        double addModuleCost = 0.0;

        double preCapacity = link.getPreCapacity();
        for(ModuleConfiguration moduleConf : linkConf.moduleConfigs()) {
            double installCount = moduleConf.getInstallCount();
            double moduleCapacity = moduleConf.getModuleCapacity();

            if(moduleCapacity == preCapacity) {
                installCount--;
            }

            if(installCount > 0) {
                CapacityModule module = link.getModule(moduleConf.getModuleCapacity());
                if(module != null) {
                    addModuleCost += installCount * module.getCost();
                }
            }
        }

        return addModuleCost;
    }

    /**
     * Constructs a set containing all pairwise different paths of the 
     * specified demand routing.<br/><br/>
     * 
     * The paths are compared with respect to the given link model. If the 
     * link model is <tt>UNDIRECTED</tt> the direction into which the path
     * is traversed is not taken into account.<br/><br/>
     * 
     * If the given routing is <tt>null</tt> an empty path set is returned.
     * 
     * @param routing the demand routing
     * @param linkModel the link model
     * 
     * @return a set containing all pairwise different paths of the specified 
     * demand routing
     */
    public static PathSet toPathSet(DemandRouting routing, LinkModel linkModel) {

        PathSet pathSet = new PathSet(linkModel);

        if(routing != null) {
            for(FlowPath fp : routing.flowPaths()) {
                pathSet.add(fp.linkIds());
            }
        }
        return pathSet;
    }

    /**
     * Returns a set containing all pairwise different paths which are 
     * additional with respect to the normal operating state (NOS) and used
     * to route the specified demand when the network is in any failure state.
     * 
     * @param demand the demand
     * @param solvedProblem the solved problem
     * 
     * @return a set containing all pairwise different backup paths of the
     * specified demand
     */
    public static PathSet findBackupPathsOfDemand(Demand demand,
        SolvedProblem solvedProblem) {

        PathSet allBackupPaths = new PathSet(solvedProblem.getLinkModel());

        LinkModel linkModel = solvedProblem.getLinkModel();

        Set<OperatingState> states = solvedProblem.operatingStates();
        states.remove(OperatingState.NOS);

        for(OperatingState state : states) {
            DemandRouting backupRouting = solvedProblem.getRouting(demand, state);
            PathSet backupPaths = toPathSet(backupRouting, linkModel);

            for(List<String> backupPath : backupPaths) {
                DemandPathFlow nosPathFlow = solvedProblem.getDemandPathFlow(demand,
                    backupPath, OperatingState.NOS);
                DemandPathFlow backupPathFlow = solvedProblem.getDemandPathFlow(
                    demand, backupPath, state);
                if(DoubleComparator.getInstance().less(nosPathFlow.getTotalFlow(),
                    backupPathFlow.getTotalFlow())) {
                    allBackupPaths.add(backupPath);
                }
            }
        }
        return allBackupPaths;
    }

    /**
     * Returns the maximum flows on the specified link, together with the 
     * states in which the flow values are assumed. The calcuation is 
     * performed with respect to the survivability model.<br/><br/>
     * 
     * If a dedicated protection mechanism is used the maximum flow on the 
     * given link <tt>e</tt>, for example, is calculated as follows:<br/><br/>
     * 
     * &nbsp;&nbsp;<tt>sum_{d \in Demands} max_{s \in States} f(e, d, s)</tt>
     * where <tt>f(e, d, s)</tt> denotes the flow on link <tt>e</tt> incurred
     * by demand <tt>d</tt> in state <tt>s</tt>.<br/><br/>
     * 
     * For a shared protection mechanism the calculation looks as follows:
     * <br/><br/>
     *
     * &nbsp;&nbsp;<tt>max_{s \in States} sum_{d \in Demands} f(e, d, s)</tt>
     * 
     * @param link the link
     * @param solvedProblem the solved problem
     * 
     * @return the maximum flow on the specified link
     */
    public static MaximumLinkFlow calculateMaximumLinkFlow(Link link,
        SolvedProblem solvedProblem) {

        MaximumLinkFlow maxFlow = new MaximumLinkFlow(link);

        if(solvedProblem.getSurvivabilityModel().isDedicatedProtection()) {

            for(Demand demand : solvedProblem.demands()) {
                double maxPosFlowByDemand = 0.0;
                double maxNegFlowByDemand = 0.0;
                double maxTotalFlowByDemand = 0.0;

                for(OperatingState state : solvedProblem.operatingStates()) {
                    DemandLinkFlow demandLinkFlow = solvedProblem.getDemandLinkFlow(
                        demand, link, state);
                    maxPosFlowByDemand = Math.max(maxPosFlowByDemand,
                        demandLinkFlow.getPositiveFlow());
                    maxNegFlowByDemand = Math.max(maxNegFlowByDemand,
                        demandLinkFlow.getNegativeFlow());
                    maxTotalFlowByDemand = Math.max(maxTotalFlowByDemand,
                        demandLinkFlow.getTotalFlow());
                }

                maxFlow.maxPositiveFlow += maxPosFlowByDemand;
                maxFlow.maxNegativeFlow += maxNegFlowByDemand;
                maxFlow.maxTotalFlow += maxTotalFlowByDemand;
            }
            maxFlow.maxPositiveFlowAssumedState = OperatingState.NOS;
            maxFlow.maxNegativeFlowAssumedState = OperatingState.NOS;
            maxFlow.maxTotalFlowAssumedState = OperatingState.NOS;
        }
        else {
            for(OperatingState state : solvedProblem.operatingStates()) {
                LinkFlow linkFlow = solvedProblem.getLinkFlow(link, state);

                if(maxFlow.maxPositiveFlow < linkFlow.getPositiveFlow()) {
                    maxFlow.maxPositiveFlow = linkFlow.getPositiveFlow();
                    maxFlow.maxPositiveFlowAssumedState = state;
                }
                if(maxFlow.maxNegativeFlow < linkFlow.getNegativeFlow()) {
                    maxFlow.maxNegativeFlow = linkFlow.getNegativeFlow();
                    maxFlow.maxNegativeFlowAssumedState = state;
                }
                if(maxFlow.maxTotalFlow < linkFlow.getTotalFlow()) {
                    maxFlow.maxTotalFlow = linkFlow.getTotalFlow();
                    maxFlow.maxTotalFlowAssumedState = state;
                }
            }
        }
        return maxFlow;
    }

    private SolutionUtils() {

        /* not instantiable */
    }
}
