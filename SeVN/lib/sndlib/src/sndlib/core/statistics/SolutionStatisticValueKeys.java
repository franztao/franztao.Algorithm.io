/*
 * $Id: SolutionStatisticValueKeys.java 215 2006-07-11 10:14:25Z roman.klaehne $
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

package sndlib.core.statistics;

import java.util.List;
import java.util.Set;

import sndlib.core.model.FixedChargeModel;
import sndlib.core.model.LinkModel;
import sndlib.core.model.SurvivabilityModel;
import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Node;
import sndlib.core.problem.DemandLinkFlow;
import sndlib.core.problem.LinkFlow;
import sndlib.core.problem.OperatingState;
import sndlib.core.problem.SolvedProblem;
import sndlib.core.solution.DemandRouting;
import sndlib.core.solution.FlowPath;
import sndlib.core.solution.LinkConfiguration;
import sndlib.core.util.MaximumLinkFlow;
import sndlib.core.util.PathSet;
import sndlib.core.util.SolutionUtils;

/**
 * This enumeration implements all currently defined statistical values 
 * of a solution of a network design problem.<br/>
 * <br/>
 * 
 * For a given solved problem a single statistical value can be calculated by 
 * invoking the {@link #calculateValue(SolvedProblem)} method of the 
 * corresponding enumeration constant. To compute, e.g., the total link 
 * capacity cost of a given solution, write this line: 
 *
 * <p><tt>&nbsp;&nbsp;
 *  SolutionStatisticValueKeys.LINK_TOTAL_CAPACITY_COST.calculateValue(solvedProblem)
 * </tt></p><br/>
 * 
 * To calculate the entire statistics of a given solved problem make use of 
 * the static factory methods of {@link SolutionStatistics}, like:
 * 
 * <p><tt>&nbsp;&nbsp;
 *  SolutionStatistics.newStatistics(solvedProblem)
 * </tt></p>
 * 
 * @see SolutionStatistics
 * 
 * @author Roman Klaehne
 */
public enum SolutionStatisticValueKeys implements SolutionStatisticValueKey {

    /* -----------------------------------------------------------------------
     *      General statistics
     * -----------------------------------------------------------------------
     */

    /**
     * Calculates the total pre-installed capacity cost of all links 
     * contained in the solution topology.
     */
    LINK_PRE_INSTALLED_CAPACITY_COST {

        public double calculateValue(SolvedProblem solvedProblem) {

            double preCost = 0.0;

            for(LinkConfiguration linkConf : solvedProblem.linkConfigs()) {
                if(SolutionUtils.getTotalInstalledCapacity(linkConf) > 0) {
                    preCost += solvedProblem.getLink(linkConf).getPreCost();
                }
            }
            return preCost;
        }
    },

    /**
     * Calculates the total cost of the additional modules installed on all
     * links contained in the solution topology.<br/><br/>
     * 
     * The costs of the pre-installed link capacities are not considered.
     */
    LINK_ADDITIONAL_MODULE_CAPACITY_COST {

        public double calculateValue(SolvedProblem solvedProblem) {

            double addModuleCost = 0.0;

            for(LinkConfiguration linkConf : solvedProblem.linkConfigs()) {
                Link link = solvedProblem.getLink(linkConf);
                addModuleCost += SolutionUtils.getAdditionalModuleCapacityCost(link,
                    linkConf);
            }
            return addModuleCost;
        }
    },

    /**
     * Calculates the total cost of all link capacities.<br/><br/>
     * 
     * The cost is the sum of the pre-installed and the additional capacity 
     * costs.
     * 
     * @see #LINK_PRE_INSTALLED_CAPACITY_COST
     * @see #LINK_ADDITIONAL_MODULE_CAPACITY_COST
     */
    LINK_TOTAL_CAPACITY_COST {

        public double calculateValue(SolvedProblem solvedProblem) {

            return LINK_PRE_INSTALLED_CAPACITY_COST.calculateValue(solvedProblem)
                + LINK_ADDITIONAL_MODULE_CAPACITY_COST.calculateValue(solvedProblem);
        }
    },

    /**
     * Calculates the sum of the setup costs imposed on all link contained 
     * in the solution topology.
     */
    LINK_FIXED_CHARGE_COST {

        public double calculateValue(SolvedProblem solvedProblem) {

            if(solvedProblem.getFixedChargeModel() == FixedChargeModel.NO) {
                return 0;
            }

            double fixedChargeCost = 0.0;

            for(LinkConfiguration linkConf : solvedProblem.linkConfigs()) {
                if(SolutionUtils.getTotalInstalledCapacity(linkConf) > 0) {
                    fixedChargeCost += solvedProblem.getLink(linkConf).getSetupCost();
                }
            }
            return fixedChargeCost;
        }
    },

    /**
     * Calculates the total link cost.<br/><br/>
     * 
     * The total link cost covers the capacity cost, the fixed-charge cost
     * and the routing cost.
     * 
     * @see #LINK_TOTAL_CAPACITY_COST
     * @see #LINK_FIXED_CHARGE_COST
     * @see #LINK_TOTAL_ROUTING_COST
     */
    LINK_TOTAL_COST {

        public double calculateValue(SolvedProblem solvedProblem) {

            return LINK_TOTAL_CAPACITY_COST.calculateValue(solvedProblem)
                + LINK_FIXED_CHARGE_COST.calculateValue(solvedProblem)
                + LINK_TOTAL_ROUTING_COST.calculateValue(solvedProblem);
        }
    },

    /**
     * Calculates the average usage of the link capacities when the network
     * is in the normal operating state (NOS).<br/><br/>
     * 
     * The capacity usage of a link is the ratio between the flow on the 
     * link and the capacity installed:<br/><br/>
     * 
     * &nbsp;&nbsp;<tt>0 <= u = f / c <= 1 </tt> where <tt>f</tt> denotes the
     * link flow and <tt>c</tt> the capacity installed on that link. 
     */
    LINK_NOS_AVG_CAPACITY_USAGE {

        public double calculateValue(SolvedProblem solvedProblem) {

            double usageSum = 0.0;
            int linkCount = 0;

            LinkModel lm = solvedProblem.getLinkModel();

            for(LinkConfiguration linkConf : solvedProblem.linkConfigs()) {
                double capacity = SolutionUtils.getTotalInstalledCapacity(linkConf);
                if(capacity > 0) {
                    linkCount++;
                    Link link = solvedProblem.getLink(linkConf);

                    LinkFlow lf = solvedProblem.getLinkFlow(link, OperatingState.NOS);
                    double flow = (lm == LinkModel.BIDIRECTED) ? Math.max(
                        lf.getPositiveFlow(), lf.getNegativeFlow())
                        : lf.getTotalFlow();
                    usageSum += flow / capacity;
                }
            }
            return (linkCount > 0) ? usageSum / linkCount : 1.0;
        }
    },

    /**
     * Calculates the minimum usage of the link capacities when the network
     * is in the normal operating state (NOS).<br/><br/>
     * 
     * The capacity usage of a link is the ratio between the flow on the 
     * link and the capacity installed:<br/><br/>
     * 
     * &nbsp;&nbsp;<tt>0 <= u = f / c <= 1 </tt> where <tt>f</tt> denotes the
     * link flow and <tt>c</tt> the capacity installed on that link. 
     */
    LINK_NOS_MIN_CAPACITY_USAGE {

        public double calculateValue(SolvedProblem solvedProblem) {

            double minUsage = 1.0;

            LinkModel lm = solvedProblem.getLinkModel();

            for(LinkConfiguration linkConf : solvedProblem.linkConfigs()) {
                double capacity = SolutionUtils.getTotalInstalledCapacity(linkConf);
                if(capacity > 0) {
                    Link link = solvedProblem.getLink(linkConf);

                    LinkFlow lf = solvedProblem.getLinkFlow(link, OperatingState.NOS);
                    double flow = (lm == LinkModel.BIDIRECTED) ? Math.max(
                        lf.getPositiveFlow(), lf.getNegativeFlow())
                        : lf.getTotalFlow();
                    minUsage = Math.min(minUsage, flow / capacity);
                }
            }
            return minUsage;
        }
    },

    /**
     * Calculates the maximum usage of the link capacities when the network
     * is in the normal operating state (NOS).<br/><br/>
     * 
     * The capacity usage of a link is the ratio between the flow on the 
     * link and the capacity installed:<br/><br/>
     * 
     * &nbsp;&nbsp;<tt>0 <= u = f / c <= 1 </tt> where <tt>f</tt> denotes the
     * link flow and <tt>c</tt> the capacity installed on that link. 
     */
    LINK_NOS_MAX_CAPACITY_USAGE {

        public double calculateValue(SolvedProblem solvedProblem) {

            double maxUsage = 0.0;

            LinkModel lm = solvedProblem.getLinkModel();

            for(LinkConfiguration linkConf : solvedProblem.linkConfigs()) {
                double capacity = SolutionUtils.getTotalInstalledCapacity(linkConf);
                if(capacity > 0) {
                    Link link = solvedProblem.getLink(linkConf);

                    LinkFlow lf = solvedProblem.getLinkFlow(link, OperatingState.NOS);
                    double flow = (lm == LinkModel.BIDIRECTED) ? Math.max(
                        lf.getPositiveFlow(), lf.getNegativeFlow())
                        : lf.getTotalFlow();

                    maxUsage = Math.max(maxUsage, flow / capacity);
                }
            }
            return maxUsage;
        }
    },

    /**
     * Calculates the maximum usage of the link capacities when the network
     * is in any operating state.
     */
    LINK_ALL_STATES_MAX_CAPACITY_USAGE {

        public double calculateValue(SolvedProblem solvedProblem) {

            double maxUsage = 0.0;

            LinkModel lm = solvedProblem.getLinkModel();

            for(LinkConfiguration linkConf : solvedProblem.linkConfigs()) {
                double capacity = SolutionUtils.getTotalInstalledCapacity(linkConf);
                if(capacity > 0) {
                    Link link = solvedProblem.getLink(linkConf);
                    MaximumLinkFlow maxFlow = SolutionUtils.calculateMaximumLinkFlow(
                        link, solvedProblem);

                    double maxLinkFlow = (lm == LinkModel.BIDIRECTED) ? Math.max(
                        maxFlow.getMaxPositiveFlow(), maxFlow.getMaxNegativeFlow())
                        : maxFlow.getMaxTotalFlow();

                    maxUsage = Math.max(maxUsage, maxLinkFlow / capacity);
                }
            }
            return maxUsage;
        }
    },

    /**
     * Calculates the minimum usage of the link capacities when the network
     * is in any operating state.
     */
    LINK_ALL_STATES_MIN_CAPACITY_USAGE {

        public double calculateValue(SolvedProblem solvedProblem) {

            double minUsage = 1.0;

            LinkModel lm = solvedProblem.getLinkModel();

            for(LinkConfiguration linkConf : solvedProblem.linkConfigs()) {
                double capacity = SolutionUtils.getTotalInstalledCapacity(linkConf);
                if(capacity > 0) {
                    Link link = solvedProblem.getLink(linkConf);
                    MaximumLinkFlow maxFlow = SolutionUtils.calculateMaximumLinkFlow(
                        link, solvedProblem);

                    double flowValue = (lm == LinkModel.BIDIRECTED) ? Math.max(
                        maxFlow.getMaxPositiveFlow(), maxFlow.getMaxNegativeFlow())
                        : maxFlow.getMaxTotalFlow();

                    minUsage = Math.min(minUsage, flowValue / capacity);
                }
            }
            return minUsage;
        }
    },

    /**
     * Calculates the average usage of the link capacities when the network
     * is in any operating state.
     */
    LINK_ALL_STATES_AVG_CAPACITY_USAGE {

        public double calculateValue(SolvedProblem solvedProblem) {

            double usageSum = 0.0;
            int linkCount = 0;

            LinkModel lm = solvedProblem.getLinkModel();

            for(LinkConfiguration linkConf : solvedProblem.linkConfigs()) {
                double capacity = SolutionUtils.getTotalInstalledCapacity(linkConf);
                if(capacity > 0) {
                    linkCount++;
                    Link link = solvedProblem.getLink(linkConf);
                    MaximumLinkFlow maxFlow = SolutionUtils.calculateMaximumLinkFlow(
                        link, solvedProblem);

                    double flowValue = (lm == LinkModel.BIDIRECTED) ? Math.max(
                        maxFlow.getMaxPositiveFlow(), maxFlow.getMaxNegativeFlow())
                        : maxFlow.getMaxTotalFlow();

                    usageSum += flowValue / capacity;
                }
            }
            return (linkCount > 0) ? usageSum / linkCount : 1.0;
        }
    },

    /**
     * Calculates the number of links in the solution topology on which any
     * capacity is installed.
     */
    LINKS_TOTAL_INSTALLED_LINKS {

        public double calculateValue(SolvedProblem solvedProblem) {

            int count = 0;

            for(LinkConfiguration linkConf : solvedProblem.linkConfigs()) {
                if(SolutionUtils.getTotalInstalledCapacity(linkConf) > 0) {
                    count++;
                }
            }
            return count;
        }
    },

    /**
     * Calculates the sum of the capacities installed on all links contained 
     * in the solution topology.
     */
    LINKS_TOTAL_INSTALLED_CAPACITIES {

        public double calculateValue(SolvedProblem solvedProblem) {

            double totalInstalledCapacities = 0;

            for(LinkConfiguration linkConf : solvedProblem.getSolution().linkConfigs()) {
                totalInstalledCapacities += SolutionUtils.getTotalInstalledCapacity(linkConf);
            }
            return totalInstalledCapacities;
        }
    },

    /**
     * Calculates the minimum degree over all nodes contained in the solution
     * topology.<br/><br/>
     * 
     * The degree of a node is defined as the number of its incident links.
     */
    NODE_MIN_DEGREE {

        public double calculateValue(SolvedProblem solvedProblem) {

            int minDegree = Integer.MAX_VALUE;

            for(Node node : solvedProblem.nodes()) {
                minDegree = Math.min(minDegree, SolutionUtils.getNodeDegree(node,
                    solvedProblem));
            }
            return (solvedProblem.nodeCount() > 0) ? minDegree : 0;
        }
    },

    /**
     * Calculates the maximum degree over all nodes contained in the solution
     * topology.<br/><br/>
     * 
     * The degree of a node is defined as the number of its incident links.
     */
    NODE_MAX_DEGREE {

        public double calculateValue(SolvedProblem solvedProblem) {

            int maxDegree = 0;

            for(Node node : solvedProblem.nodes()) {
                maxDegree = Math.max(maxDegree, SolutionUtils.getNodeDegree(node,
                    solvedProblem));
            }
            return maxDegree;
        }
    },

    /**
     * Calculates the average degree over all nodes contained in the solution
     * topology.<br/>
     * <br/>
     * 
     * The degree of a node is defined as the number of its incident links.
     */
    NODE_AVG_DEGREE {

        public double calculateValue(SolvedProblem solvedProblem) {

            int degreeSum = 0;

            for(Node node : solvedProblem.nodes()) {
                degreeSum += SolutionUtils.getNodeDegree(node, solvedProblem);
            }

            int nodeCount = solvedProblem.nodeCount();
            return (nodeCount > 0) ? (double) degreeSum / (double) nodeCount : 0;
        }
    },

    /**
     * Calculates the minimum in-degree over all nodes contained in the 
     * solution topology.<br/><br/>
     * 
     * For a directed link model the in-degree of a node is defined as the 
     * number of its incident links of which that node is the target. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_MIN_IN_DEGREE {

        public double calculateValue(SolvedProblem solvedProblem) {

            int minInDegree = Integer.MAX_VALUE;

            for(Node node : solvedProblem.nodes()) {
                minInDegree = Math.min(minInDegree, SolutionUtils.getNodeInDegree(
                    node, solvedProblem));
            }
            return (solvedProblem.nodeCount() > 0) ? minInDegree : 0;
        }
    },

    /**
     * Calculates the maximum in-degree over all nodes contained in the 
     * solution topology.<br/><br/>
     * 
     * For a directed link model the in-degree of a node is defined as the 
     * number of its incident links of which that node is the target. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_MAX_IN_DEGREE {

        public double calculateValue(SolvedProblem solvedProblem) {

            int maxInDegree = 0;

            for(Node node : solvedProblem.nodes()) {
                maxInDegree = Math.max(maxInDegree, SolutionUtils.getNodeInDegree(
                    node, solvedProblem));
            }
            return maxInDegree;
        }
    },

    /**
     * Calculates the average in-degree over all nodes contained in the 
     * solution topology.<br/><br/>
     * 
     * For a directed link model the in-degree of a node is defined as the 
     * number of its incident links of which that node is the target. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_AVG_IN_DEGREE {

        public double calculateValue(SolvedProblem solvedProblem) {

            int inDegreeSum = 0;

            for(Node node : solvedProblem.nodes()) {
                inDegreeSum += SolutionUtils.getNodeInDegree(node, solvedProblem);
            }

            int nodeCount = solvedProblem.nodeCount();
            return (nodeCount > 0) ? (double) inDegreeSum / (double) nodeCount : 0;
        }
    },

    /**
     * Calculates the minimum out-degree over all nodes in the solution
     * topology.<br/>
     * <br/>
     * 
     * For a directed link model the out-degree of a node is defined as the 
     * number of its incident links of which that node is the source. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_MIN_OUT_DEGREE {

        public double calculateValue(SolvedProblem solvedProblem) {

            int minOutDegree = Integer.MAX_VALUE;

            for(Node node : solvedProblem.nodes()) {
                minOutDegree = Math.min(minOutDegree,
                    SolutionUtils.getNodeOutDegree(node, solvedProblem));
            }
            return (solvedProblem.nodeCount() > 0) ? minOutDegree : 0;
        }
    },

    /**
     * Calculates the maximum out-degree over all nodes in the solution
     * topology.<br/>
     * <br/>
     * 
     * For a directed link model the out-degree of a node is defined as the 
     * number of its incident links of which that node is the source. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_MAX_OUT_DEGREE {

        public double calculateValue(SolvedProblem solvedProblem) {

            int maxOutDegree = 0;

            for(Node node : solvedProblem.nodes()) {
                maxOutDegree = Math.max(maxOutDegree,
                    SolutionUtils.getNodeOutDegree(node, solvedProblem));
            }
            return maxOutDegree;
        }
    },

    /**
     * Calculates the average out-degree over all nodes in the solution
     * topology.<br/>
     * <br/>
     * 
     * For a directed link model the out-degree of a node is defined as the 
     * number of its incident links of which that node is the source. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_AVG_OUT_DEGREE {

        public double calculateValue(SolvedProblem solvedProblem) {

            int outDegreeSum = 0;

            for(Node node : solvedProblem.nodes()) {
                outDegreeSum += SolutionUtils.getNodeOutDegree(node, solvedProblem);
            }

            int nodeCount = solvedProblem.nodeCount();
            return (nodeCount > 0) ? (double) outDegreeSum / (double) nodeCount : 0;
        }
    },

    /* -----------------------------------------------------------------------
     *      Routing statistics
     * -----------------------------------------------------------------------
     */

    /**
     * Calculates the average length over all paths used to route all demands 
     * when the network is in the normal operating state (NOS). 
     */
    ROUTING_PATH_NOS_AVG_LENGTH {

        public double calculateValue(SolvedProblem solvedProblem) {

            int sumPaths = 0;
            int sumPathLength = 0;
            for(DemandRouting routing : solvedProblem.routings(OperatingState.NOS)) {
                PathSet pathSet = SolutionUtils.toPathSet(routing,
                    solvedProblem.getLinkModel());

                sumPaths += pathSet.size();
                for(List<String> path : pathSet) {
                    sumPathLength += path.size();
                }
            }

            return (sumPaths > 0) ? sumPathLength / (double) sumPaths : 0;
        }
    },

    /**
     * Calculates the minimum length over all paths used to route all demands 
     * when the network is in the normal operating state (NOS).
     */
    ROUTING_PATH_NOS_MIN_LENGTH {

        public double calculateValue(SolvedProblem solvedProblem) {

            int minPathLength = Integer.MAX_VALUE;
            for(DemandRouting routing : solvedProblem.routings(OperatingState.NOS)) {
                for(FlowPath flowPath : routing.flowPaths()) {
                    minPathLength = Math.min(minPathLength, flowPath.linkIdCount());
                }
            }

            return (minPathLength != Integer.MAX_VALUE) ? minPathLength : 0;
        }
    },

    /**
     * Calculates the maximum length over all paths used to route all demands 
     * when the network is in the normal operating state (NOS).
     */
    ROUTING_PATH_NOS_MAX_LENGTH {

        public double calculateValue(SolvedProblem solvedProblem) {

            int maxPathLength = 0;
            for(DemandRouting routing : solvedProblem.routings(OperatingState.NOS)) {
                for(FlowPath flowPath : routing.flowPaths()) {
                    maxPathLength = Math.max(maxPathLength, flowPath.linkIdCount());
                }
            }

            return maxPathLength;
        }
    },

    /**
     * Calculates the total number of paths which are used to route all demands 
     * when the network is in the normal operating state (NOS).
     */
    ROUTING_PATHS_NOS_TOTAL_NUMBER {

        public double calculateValue(SolvedProblem solvedProblem) {

            LinkModel linkModel = solvedProblem.getLinkModel();
            int totalNumberOfNOSPaths = 0;

            for(DemandRouting routing : solvedProblem.routings(OperatingState.NOS)) {
                totalNumberOfNOSPaths += SolutionUtils.toPathSet(routing, linkModel).size();
            }

            return totalNumberOfNOSPaths;
        }
    },

    /**
     * Calculates the average number of paths per demand used when the network
     * is in the normal operating state (NOS).
     */
    ROUTING_PATHS_NOS_AVG_NUMBER_PER_DEMAND {

        public double calculateValue(SolvedProblem solvedProblem) {

            int demandCount = solvedProblem.demandCount();
            return (demandCount > 0)
                ? ROUTING_PATHS_NOS_TOTAL_NUMBER.calculateValue(solvedProblem)
                    / (double) demandCount : 0;
        }
    },

    /**
     * Calculates the minimum number of paths per demand used when the network
     * is in the normal operating state (NOS).
     */
    ROUTING_PATHS_NOS_MIN_NUMBER_PER_DEMAND {

        public double calculateValue(SolvedProblem solvedProblem) {

            int minPaths = Integer.MAX_VALUE;
            for(DemandRouting routing : solvedProblem.routings(OperatingState.NOS)) {
                PathSet pathSet = SolutionUtils.toPathSet(routing,
                    solvedProblem.getLinkModel());
                minPaths = Math.min(minPaths, pathSet.size());
            }

            return (minPaths < Integer.MAX_VALUE) ? minPaths : 0;
        }
    },

    /**
     * Calculates the maximum number of paths per demand used when the network
     * is in the normal operating state (NOS).
     */
    ROUTING_PATHS_NOS_MAX_NUMBER_PER_DEMAND {

        public double calculateValue(SolvedProblem solvedProblem) {

            int maxPaths = 0;
            for(DemandRouting routing : solvedProblem.routings(OperatingState.NOS)) {
                PathSet pathSet = SolutionUtils.toPathSet(routing,
                    solvedProblem.getLinkModel());
                maxPaths = Math.max(maxPaths, pathSet.size());
            }

            return maxPaths;
        }
    },

    /**
     * Calculates the total number of paths which are used to route all 
     * demands when the network is in a failure operating state.
     */
    ROUTING_PATHS_BACKUP_TOTAL_NUMBER {

        public double calculateValue(SolvedProblem solvedProblem) {

            int totalNumberOfBackupPaths = 0;

            for(Demand demand : solvedProblem.demands()) {
                totalNumberOfBackupPaths += SolutionUtils.findBackupPathsOfDemand(
                    demand, solvedProblem).size();
            }

            return totalNumberOfBackupPaths;
        }
    },

    /**
     * Calculates the minimum length over all paths used to route all demands 
     * when the network is in any failure operating state.
     */
    ROUTING_PATH_BACKUP_MIN_LENGTH {

        public double calculateValue(SolvedProblem solvedProblem) {

            int minPathLength = Integer.MAX_VALUE;
            for(Demand demand : solvedProblem.demands()) {
                for(List<String> backupPath : SolutionUtils.findBackupPathsOfDemand(
                    demand, solvedProblem)) {
                    minPathLength = Math.min(minPathLength, backupPath.size());
                }
            }

            return (minPathLength != Integer.MAX_VALUE) ? minPathLength : 0;
        }
    },

    /**
     * Calculates the maximum length over all paths used to route all demands 
     * when the network is in any failure operating state.
     */
    ROUTING_PATH_BACKUP_MAX_LENGTH {

        public double calculateValue(SolvedProblem solvedProblem) {

            int maxPathLength = 0;

            for(Demand demand : solvedProblem.demands()) {
                for(List<String> backupPath : SolutionUtils.findBackupPathsOfDemand(
                    demand, solvedProblem)) {
                    maxPathLength = Math.max(maxPathLength, backupPath.size());
                }
            }

            return maxPathLength;
        }
    },

    /**
     * Calculates the average length over all paths used to route all demands 
     * when the network is in any failure operating state.
     */
    ROUTING_PATH_BACKUP_AVG_LENGTH {

        public double calculateValue(SolvedProblem solvedProblem) {

            int sumLengthOfBackupPaths = 0;
            int numberOfBackupPaths = 0;

            for(Demand demand : solvedProblem.demands()) {
                PathSet backupPaths = SolutionUtils.findBackupPathsOfDemand(demand,
                    solvedProblem);
                for(List<String> backupPath : backupPaths) {
                    sumLengthOfBackupPaths += backupPath.size();
                }
                numberOfBackupPaths += backupPaths.size();
            }

            return (numberOfBackupPaths > 0) ? sumLengthOfBackupPaths
                / (double) numberOfBackupPaths : 0;
        }
    },

    /**
     * Calculates the minimum number of paths per demand which are used when 
     * the network is in any failure operating state.
     */
    ROUTING_PATH_BACKUP_MIN_NUMBER_PER_DEMAND {

        public double calculateValue(SolvedProblem solvedProblem) {

            int minNumberOfBackupPaths = Integer.MAX_VALUE;

            for(Demand demand : solvedProblem.demands()) {
                minNumberOfBackupPaths = Math.min(
                    minNumberOfBackupPaths,
                    SolutionUtils.findBackupPathsOfDemand(demand, solvedProblem).size());

            }
            return (minNumberOfBackupPaths == Integer.MAX_VALUE) ? 0
                : minNumberOfBackupPaths;
        }
    },

    /**
     * Calculates the maximum number of paths per demand which are used when 
     * the network is in any failure operating state.
     */
    ROUTING_PATH_BACKUP_MAX_NUMBER_PER_DEMAND {

        public double calculateValue(SolvedProblem solvedProblem) {

            int maxNumberOfBackupPaths = 0;

            for(Demand demand : solvedProblem.demands()) {
                maxNumberOfBackupPaths = Math.max(
                    maxNumberOfBackupPaths,
                    SolutionUtils.findBackupPathsOfDemand(demand, solvedProblem).size());

            }
            return maxNumberOfBackupPaths;
        }
    },

    /**
     * Calculates the average number of paths per demand which are used when 
     * the network is in any failure operating state.
     */
    ROUTING_PATH_BACKUP_AVG_NUMBER_PER_DEMAND {

        public double calculateValue(SolvedProblem solvedProblem) {

            return ROUTING_PATHS_BACKUP_TOTAL_NUMBER.calculateValue(solvedProblem)
                / (double) solvedProblem.demandCount();
        }
    },

    /**
     * Calculates the total flow in the network when it is in the normal 
     * operating state (NOS).<br/><br/>
     */
    TOTAL_NOS_FLOW {

        public double calculateValue(SolvedProblem solvedProblem) {

            double totalNosFlow = 0;

            LinkModel lm = solvedProblem.getLinkModel();

            for(Link link : solvedProblem.links()) {
                LinkFlow linkFlow = solvedProblem.getLinkFlow(link,
                    OperatingState.NOS);

                totalNosFlow += (lm == LinkModel.BIDIRECTED) ? Math.max(
                    linkFlow.getPositiveFlow(), linkFlow.getNegativeFlow())
                    : linkFlow.getTotalFlow();
            }
            return totalNosFlow;
        }
    },

    /**
     * Calculates the total routing cost in the network considering all 
     * operating states.<br/><br/>
     * 
     * The calculation is done using the following formula:<br/>
     * <br/>
     * 
     * &nbsp;&nbsp;<tt>\sum_{e \in E} routing_cost(e) *
     *      \max_{s \in S} \sum_{d \in D} flow(d,e,s)</tt> 
     */
    LINK_TOTAL_ROUTING_COST {

        public double calculateValue(SolvedProblem solvedProblem) {

            double routingCost = 0.0;

            LinkModel lm = solvedProblem.getLinkModel();

            for(Link link : solvedProblem.links()) {

                MaximumLinkFlow maxLinkFlow = SolutionUtils.calculateMaximumLinkFlow(
                    link, solvedProblem);

                double maxLinkFlowValue = (lm == LinkModel.BIDIRECTED) ? Math.max(
                    maxLinkFlow.getMaxPositiveFlow(),
                    maxLinkFlow.getMaxNegativeFlow())
                    : maxLinkFlow.getMaxTotalFlow();

                routingCost += maxLinkFlowValue * link.getRoutingCost();
            }
            return routingCost;
        }
    },

    /**
     * Calculates the maximal flow in the network which is additional with 
     * respect to the flow in the normal operating state (NOS).<br/><br/> 
     * 
     * The calculation is done using the following formula:<br/><br/>
     *  
     * &nbsp;&nbsp;<tt>\sum{e \in E} \max_{s \in Failstates} 
     *    \sum_{d \in D} \max{ 0, flow(e,d,s) - flow(e,d,NOS) }
     *
     * for shared protection and unrestricted flow reconfiguration, and
     *
     * &nbsp;&nbsp;<tt>\sum{e \in E} \sum_{d \in D} 
     *    \max_{s \in Failstates} \max{ 0, flow(e,d,s) - flow(e,d,NOS) }
     * 
     * for dedicated 1+1 protection.
     * </tt> 
     */
    MAXIMUM_ADDITIONAL_BACKUP_FLOW {

        public double calculateValue(SolvedProblem solvedProblem) {

            SurvivabilityModel sm = solvedProblem.getSurvivabilityModel();

            if(sm == SurvivabilityModel.NO_SURVIVABILITY)
                return 0.0;

            double maxAdditionalBackupFlow = 0.0;
            for(Link link : solvedProblem.links()) {

                maxAdditionalBackupFlow += (sm.isDedicatedProtection())
                    ? getDedicatedMaxLinkFlow(solvedProblem, link)
                    : getSharedMaxLinkFlow(solvedProblem, link);
            }
            return maxAdditionalBackupFlow;
        }

        // dedicated protection
        private double getDedicatedMaxLinkFlow(SolvedProblem solvedProblem, Link link) {

            LinkModel lm = solvedProblem.getLinkModel();
            Set<OperatingState> failureStates = solvedProblem.operatingStates();
            failureStates.remove(OperatingState.NOS);

            double maxLinkFlow = 0.0;
            for(Demand demand : solvedProblem.demands()) {
                double maxDemandLinkFlow = 0.0;

                for(OperatingState state : failureStates) {
                    double posLinkFlowInState = 0.0;
                    double negLinkFlowInState = 0.0;
                    double totalLinkFlowInState = 0.0;

                    DemandLinkFlow backupDemandFlow = solvedProblem.getDemandLinkFlow(
                        demand, link, state);
                    DemandLinkFlow nosDemandFlow = solvedProblem.getDemandLinkFlow(
                        demand, link, OperatingState.NOS);

                    posLinkFlowInState += Math.max(0,
                        backupDemandFlow.getPositiveFlow()
                            - nosDemandFlow.getPositiveFlow());
                    negLinkFlowInState += Math.max(0,
                        backupDemandFlow.getNegativeFlow()
                            - nosDemandFlow.getNegativeFlow());
                    totalLinkFlowInState += Math.max(0,
                        backupDemandFlow.getTotalFlow()
                            - nosDemandFlow.getTotalFlow());

                    double linkFlowInState = (lm == LinkModel.BIDIRECTED)
                        ? Math.max(posLinkFlowInState, negLinkFlowInState)
                        : totalLinkFlowInState;

                    maxDemandLinkFlow = Math.max(maxDemandLinkFlow, linkFlowInState);
                }
                maxLinkFlow += maxDemandLinkFlow;
            }
            return maxLinkFlow;
        }

        // shared protection
        private double getSharedMaxLinkFlow(SolvedProblem solvedProblem, Link link) {

            LinkModel lm = solvedProblem.getLinkModel();
            Set<OperatingState> failureStates = solvedProblem.operatingStates();
            failureStates.remove(OperatingState.NOS);

            double maxLinkFlow = 0.0;
            for(OperatingState state : failureStates) {
                double posLinkFlowInState = 0.0;
                double negLinkFlowInState = 0.0;
                double totalLinkFlowInState = 0.0;

                for(Demand demand : solvedProblem.demands()) {
                    DemandLinkFlow backupDemandFlow = solvedProblem.getDemandLinkFlow(
                        demand, link, state);
                    DemandLinkFlow nosDemandFlow = solvedProblem.getDemandLinkFlow(
                        demand, link, OperatingState.NOS);

                    posLinkFlowInState += Math.max(0,
                        backupDemandFlow.getPositiveFlow()
                            - nosDemandFlow.getPositiveFlow());
                    negLinkFlowInState += Math.max(0,
                        backupDemandFlow.getNegativeFlow()
                            - nosDemandFlow.getNegativeFlow());
                    totalLinkFlowInState += Math.max(0,
                        backupDemandFlow.getTotalFlow()
                            - nosDemandFlow.getTotalFlow());
                }

                double linkFlowInState = (lm == LinkModel.BIDIRECTED) ? Math.max(
                    posLinkFlowInState, negLinkFlowInState) : totalLinkFlowInState;

                maxLinkFlow = Math.max(maxLinkFlow, linkFlowInState);
            }
            return maxLinkFlow;
        }
    };

    abstract public double calculateValue(SolvedProblem solvedProblem);

    /**
     * Returns the name of this enumeration constant.<br/>
     * <br/>
     * 
     * This method is equivalent to <tt>name()</tt>.
     * 
     * @return the name of this enumeration constant
     */
    public String getName() {

        return name();
    }
}
