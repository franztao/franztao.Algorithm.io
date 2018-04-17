/*
 * $Id: ProblemStatisticValueKeys.java 99 2006-07-04 14:05:16Z bzforlow $
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

import java.util.LinkedList;
import java.util.List;

import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Node;
import sndlib.core.network.AdmissiblePath;
import sndlib.core.problem.Problem;
import sndlib.core.util.NetworkUtils;

/**
 * This enumeration implements all currently defined statistical values 
 * of a network design {@link sndlib.core.problem.Problem}.<br/>
 * <br/>
 * 
 * For a given problem a single statistical value can be calculated by 
 * invoking the {@link #calculateValue(Problem)} method of the corresponding
 * enumeration constant. To compute, e.g., the average node out-degree of a 
 * given problem, write this line: 
 *
 * <p><tt>&nbsp;&nbsp;
 *  ProblemStatisticValueKeys.NODE_AVG_OUT_DEGREE.calculateValue(problem)
 * </tt></p><br/>
 * 
 * To calculate the entire statistics of a given problem make use of the 
 * static factory methods of {@link ProblemStatistics}, like
 * 
 * <p><tt>&nbsp;&nbsp;
 *  ProblemStatistics.newStatistics(problem)
 * </tt></p>
 * 
 * @see ProblemStatistics
 * 
 * @author Roman Klaehne
 */
public enum ProblemStatisticValueKeys implements ProblemStatisticValueKey {

    /**
     * Calculates the node count.
     */
    NODE_COUNT {

        public double calculateValue(Problem problem) {

            return problem.nodeCount();
        }
    },

    /**
     * Calculates the minimum degree over all nodes.<br/>
     * <br/>
     * 
     * The degree of a node is defined as the number of its incident links.
     */
    NODE_MIN_DEGREE {

        public double calculateValue(Problem problem) {

            if(problem.nodeCount() == 0) {
                return 0;
            }

            int minDegree = Integer.MAX_VALUE;
            for(Node node : problem.getNetwork().nodes()) {
                minDegree = Math.min(minDegree, NetworkUtils.getNodeDegree(node,
                    problem.getNetwork()));
            }

            return minDegree;
        }
    },

    /**
     * Calculates the maximum degree over all nodes.<br/>
     * <br/>
     *  
     * The degree of a node is defined as the number of its incident links.
     */
    NODE_MAX_DEGREE {

        public double calculateValue(Problem problem) {

            int maxDegree = 0;
            for(Node node : problem.getNetwork().nodes()) {
                maxDegree = Math.max(maxDegree, NetworkUtils.getNodeDegree(node,
                    problem.getNetwork()));
            }

            return maxDegree;
        }
    },

    /**
     * Calculates the average degree over all nodes.<br/>
     * <br/>
     * 
     * The degree of a node is defined as the number of its incident links.
     */
    NODE_AVG_DEGREE {

        public double calculateValue(Problem problem) {

            int nodeCount = problem.nodeCount();
            if(nodeCount == 0) {
                return 0;
            }

            int sumDegree = 0;
            for(Node node : problem.getNetwork().nodes()) {
                sumDegree += NetworkUtils.getNodeDegree(node, problem.getNetwork());
            }

            return (double) sumDegree / (double) nodeCount;
        }
    },

    /**
     * Calculates the minimum in-degree over all nodes.<br/>
     * <br/>
     * 
     * For a directed link model the in-degree of a node is defined as the 
     * number of its incident links of which that node is the target. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_MIN_IN_DEGREE {

        public double calculateValue(Problem problem) {

            if(problem.nodeCount() == 0) {
                return 0;
            }

            int minInDegree = Integer.MAX_VALUE;
            for(Node node : problem.getNetwork().nodes()) {
                minInDegree = Math.min(minInDegree, NetworkUtils.getNodeInDegree(
                    node, problem));
            }

            return minInDegree;
        }
    },

    /**
     * Calculates the maximum in-degree over all nodes.<br/>
     * <br/>
     * 
     * For a directed link model the in-degree of a node is defined as the 
     * number of its incident links of which that node is the target. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_MAX_IN_DEGREE {

        public double calculateValue(Problem problem) {

            int maxInDegree = 0;
            for(Node node : problem.getNetwork().nodes()) {
                maxInDegree = Math.max(maxInDegree, NetworkUtils.getNodeInDegree(
                    node, problem));
            }

            return maxInDegree;
        }
    },

    /**
     * Calculates the average in-degree over all nodes.<br/>
     * <br/>
     * 
     * For a directed link model the in-degree of a node is defined as the 
     * number of its incident links of which that node is the target. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_AVG_IN_DEGREE {

        public double calculateValue(Problem problem) {

            int nodeCount = problem.nodeCount();
            if(nodeCount == 0) {
                return 0;
            }

            int sumInDegree = 0;
            for(Node node : problem.getNetwork().nodes()) {
                sumInDegree += NetworkUtils.getNodeInDegree(node, problem);
            }

            return (double) sumInDegree / (double) nodeCount;
        }
    },

    /**
     * Calculates the minimum out-degree over all nodes.<br/>
     * <br/>
     * 
     * For a directed link model the out-degree of a node is defined as the 
     * number of its incident links of which that node is the source. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_MIN_OUT_DEGREE {

        public double calculateValue(Problem problem) {

            if(problem.nodeCount() == 0) {
                return 0;
            }

            int minOutDegree = Integer.MAX_VALUE;
            for(Node node : problem.getNetwork().nodes()) {
                minOutDegree = Math.min(minOutDegree, NetworkUtils.getNodeOutDegree(
                    node, problem));
            }

            return minOutDegree;
        }
    },

    /**
     * Calculates the maximum out-degree over all nodes.<br/>
     * <br/>
     * 
     * For a directed link model the out-degree of a node is defined as the 
     * number of its incident links of which that node is the source. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_MAX_OUT_DEGREE {

        public double calculateValue(Problem problem) {

            int maxOutDegree = 0;
            for(Node node : problem.getNetwork().nodes()) {
                maxOutDegree = Math.max(maxOutDegree, NetworkUtils.getNodeOutDegree(
                    node, problem));
            }

            return maxOutDegree;
        }
    },

    /**
     * Calculates the average out-degree over all nodes.<br/>
     * <br/>
     * 
     * For a directed link model the out-degree of a node is defined as the 
     * number of its incident links of which that node is the source. 
     * Otherwise there is no definite target node and thus the in-degree is
     * simply the number of its incident links.
     */
    NODE_AVG_OUT_DEGREE {

        public double calculateValue(Problem problem) {

            int nodeCount = problem.nodeCount();
            if(nodeCount == 0) {
                return 0;
            }

            int sumOutDegree = 0;
            for(Node node : problem.getNetwork().nodes()) {
                sumOutDegree += NetworkUtils.getNodeOutDegree(node, problem);
            }

            return (double) sumOutDegree / (double) nodeCount;
        }
    },

    /**
     * Calculates the link count.
     */
    LINK_COUNT {

        public double calculateValue(Problem problem) {

            return problem.linkCount();
        }
    },

    /**
     * Calculates the link density.<br/>
     * <br/>
     * 
     * The link density is the ratio between the
     * number of non-parallel links in the network and the number of links in 
     * the corresponding complete network (which is 
     * <tt>(n * (n - 1)) / 2</tt>, where <tt>n</tt> denotes the number of 
     * nodes). 
     */
    LINK_DENSITY {

        public double calculateValue(Problem problem) {

            List<Link> linksWithoutParallels = new LinkedList<Link>();

            toAdd: for(Link linkToAdd : problem.getNetwork().links()) {

                for(Link link : linksWithoutParallels) {
                    if(NetworkUtils.areParallel(linkToAdd, link)) {
                        continue toAdd;
                    }
                }
                linksWithoutParallels.add(linkToAdd);
            }

            int nodeCount = problem.nodeCount();
            int maxLinkCount = (nodeCount * (nodeCount - 1)) / 2;

            return (double) linksWithoutParallels.size() / (double) maxLinkCount;
        }
    },

    /**
     * Calculates the sum of the pre-installed capacities of all links. 
     */
    LINKS_TOTAL_PREINSTALLED_CAPACITY {

        public double calculateValue(Problem problem) {

            double sumPreInstalledCapacity = 0.0;

            for(Link link : problem.getNetwork().links()) {
                sumPreInstalledCapacity += link.getPreCapacity();
            }

            return sumPreInstalledCapacity;
        }
    },

    /**
     * Calculates the sum of the pre-installed capacity costs of all links. 
     */
    LINKS_TOTAL_PREINSTALLED_CAPACITY_COST {

        public double calculateValue(Problem problem) {

            double sumPreInstalledCapacityCost = 0.0;

            for(Link link : problem.getNetwork().links()) {
                sumPreInstalledCapacityCost += link.getPreCost();
            }

            return sumPreInstalledCapacityCost;
        }
    },

    /**
     * Calculates the average pre-installed capacity over all links.
     */
    LINK_AVG_PREINSTALLED_CAPACITY {

        public double calculateValue(Problem problem) {

            int linkCount = problem.linkCount();
            return (linkCount > 0)
                ? LINKS_TOTAL_PREINSTALLED_CAPACITY.calculateValue(problem)
                    / (double) linkCount : 0;
        }
    },

    /**
     * Calculates the minimum pre-installed capacity over all links.
     */
    LINK_MIN_PREINSTALLED_CAPACITY {

        public double calculateValue(Problem problem) {

            double minPreInstalledCapacity = Integer.MAX_VALUE;

            for(Link link : problem.getNetwork().links()) {
                minPreInstalledCapacity = Math.min(minPreInstalledCapacity,
                    link.getPreCapacity());
            }
            return (problem.linkCount() > 0) ? minPreInstalledCapacity : 0;
        }
    },

    /**
     * Calculates the maximum pre-installed capacity over all links.
     */
    LINK_MAX_PREINSTALLED_CAPACITY {

        public double calculateValue(Problem problem) {

            double maxPreInstalledCapacity = 0;

            for(Link link : problem.getNetwork().links()) {
                maxPreInstalledCapacity = Math.max(maxPreInstalledCapacity,
                    link.getPreCapacity());
            }
            return maxPreInstalledCapacity;
        }
    },

    /**
     * Calculates the demand count.
     */
    DEMAND_COUNT {

        public double calculateValue(Problem problem) {

            return problem.demandCount();
        }
    },

    /**
     * Calculates the demand density.<br/>
     * <br/>
     * 
     * The demand density is the ratio between the
     * number of non-parallel demands and the maximal possible number of 
     * demands in the network. (which is 
     * <tt>(n * (n - 1)) / 2</tt>, where <tt>n</tt> denotes the number of 
     * nodes). 
     */
    DEMAND_DENSITY {

        public double calculateValue(Problem problem) {

            List<Demand> demandsWithoutParallels = new LinkedList<Demand>();

            toAdd: for(Demand demandToAdd : problem.getNetwork().demands()) {

                for(Demand demand : demandsWithoutParallels) {
                    if(NetworkUtils.areParallel(demandToAdd, demand)) {
                        continue toAdd;
                    }
                }
                demandsWithoutParallels.add(demandToAdd);
            }

            int nodeCount = problem.nodeCount();
            int maxDemandCount = (nodeCount * (nodeCount - 1)) / 2;

            return (double) demandsWithoutParallels.size() / (double) maxDemandCount;
        }
    },

    /**
     * Calculates the sum of all demand values with respect to a routing unit
     * of one.<br/>
     * <br/>
     * 
     * If a demand provides a routing unit different from one then its demand
     * value is multiplied by its routing unit.
     */
    DEMAND_SUM_DEMAND_VALUES_WRT_UNIT_1 {

        public double calculateValue(Problem problem) {

            double sumDemandValues = 0.0;

            for(Demand demand : problem.getNetwork().demands()) {
                sumDemandValues += demand.getDemandValue() * demand.getRoutingUnit();
            }
            return sumDemandValues;
        }
    },

    /**
     * Calculates the sum of all demand values, disregarding the demand 
     * specific routing units.
     */
    DEMAND_SUM_DEMAND_VALUES {

        public double calculateValue(Problem problem) {

            double sumDemandValues = 0.0;

            for(Demand demand : problem.getNetwork().demands()) {
                sumDemandValues += demand.getDemandValue();
            }
            return sumDemandValues;
        }
    },

    /**
     * Calculates the average hop-limit over all demands.<br/>
     * <br/>
     * 
     * If on no demand a hop-limit is imposed then this statistic value is 
     * evaluated to -1.
     */
    DEMAND_AVG_HOP_LIMIT {

        public double calculateValue(Problem problem) {

            int sumHopLimits = 0;
            boolean atLeastOneHopLimit = false;

            for(Demand demand : problem.getNetwork().demands()) {
                if(demand.hasMaxPathLength()) {
                    sumHopLimits += demand.getMaxPathLength();
                    atLeastOneHopLimit = true;
                }
            }

            return (atLeastOneHopLimit) ? (double) sumHopLimits
                / (double) problem.demandCount() : -1;
        }
    },

    /**
     * Calculates the minimum hop-limit over all demands.<br/>
     * <br/>
     * 
     * If on no demand a hop-limit is imposed then this statistic value is 
     * evaluated to -1.
     */
    DEMAND_MIN_HOP_LIMIT {

        public double calculateValue(Problem problem) {

            int minHopLimit = Integer.MAX_VALUE;
            boolean atLeastOneHopLimit = false;

            for(Demand demand : problem.getNetwork().demands()) {
                if(demand.hasMaxPathLength()) {
                    minHopLimit = Math.min(minHopLimit, demand.getMaxPathLength());
                    atLeastOneHopLimit = true;
                }
            }

            return (atLeastOneHopLimit) ? minHopLimit : -1;
        }
    },

    /**
     * Calculates the maximum hop-limit over all demands.<br/>
     * <br/>
     * 
     * If on no demand a hop-limit is imposed then this statistic value is 
     * evaluated to -1.
     */
    DEMAND_MAX_HOP_LIMIT {

        public double calculateValue(Problem problem) {

            int maxHopLimit = 0;
            boolean atLeastOneHopLimit = false;

            for(Demand demand : problem.getNetwork().demands()) {
                if(demand.hasMaxPathLength()) {
                    maxHopLimit = Math.max(maxHopLimit, demand.getMaxPathLength());
                    atLeastOneHopLimit = true;
                }
            }

            return (atLeastOneHopLimit) ? maxHopLimit : -1;
        }
    },

    /**
     * Calculates the average number of admissible paths over all demands.
     */
    DEMAND_AVG_NUMBER_EXPL_PATHS {

        public double calculateValue(Problem problem) {

            int demandCount = problem.demandCount();
            if(demandCount == 0) {
                return 0;
            }

            int sumAdmissiblePaths = 0;
            for(Demand demand : problem.getNetwork().demands()) {
                sumAdmissiblePaths += demand.admissiblePathCount();
            }

            return (double) sumAdmissiblePaths / (double) demandCount;
        }
    },

    /**
     * Calculates the minimum number of admissible paths over all demands.
     */
    DEMAND_MIN_NUMBER_EXPL_PATHS {

        public double calculateValue(Problem problem) {

            if(problem.demandCount() == 0) {
                return 0;
            }

            int minAdmissiblePaths = Integer.MAX_VALUE;
            for(Demand demand : problem.getNetwork().demands()) {
                minAdmissiblePaths = Math.min(minAdmissiblePaths,
                    demand.admissiblePathCount());
            }

            return minAdmissiblePaths;
        }
    },

    /**
     * Calculates the maximum number of admissible paths over all demands.
     */
    DEMAND_MAX_NUMBER_EXPL_PATHS {

        public double calculateValue(Problem problem) {

            int maxAdmissiblePaths = 0;

            for(Demand demand : problem.getNetwork().demands()) {
                maxAdmissiblePaths = Math.max(maxAdmissiblePaths,
                    demand.admissiblePathCount());
            }

            return maxAdmissiblePaths;
        }
    },

    /**
     * Calculates the total number of the admissible paths of all demands. 
     */
    DEMAND_TOTAL_NUMBER_EXPL_PATHS {

        public double calculateValue(Problem problem) {

            int sumAdmissiblePaths = 0;

            for(Demand demand : problem.getNetwork().demands()) {
                sumAdmissiblePaths += demand.admissiblePathCount();
            }

            return sumAdmissiblePaths;
        }
    },

    /**
     * Calculates the average (hop-) length of the admissible paths
     * over all demands.<br/>
     * <br/>
     * 
     * If no demand specifies any admissible paths then this statistic value is
     * evaluated to zero.
     */
    DEMAND_EXPL_PATH_AVG_LENGTH {

        public double calculateValue(Problem problem) {

            double sumAdmissiblePaths = DEMAND_TOTAL_NUMBER_EXPL_PATHS.calculateValue(problem);
            if(sumAdmissiblePaths == 0) {
                return 0;
            }

            int sumAdmissiblePathLength = 0;
            for(Demand demand : problem.getNetwork().demands()) {
                for(AdmissiblePath path : demand.admissiblePaths()) {
                    sumAdmissiblePathLength += path.linkCount();
                }
            }

            return (double) sumAdmissiblePathLength / (double) sumAdmissiblePaths;
        }
    },

    /**
     * Calculates the minimum (hop-) length of the admissible paths
     * over all demands.<br/>
     * <br/>
     * 
     * If no demand specifies any admissible paths then this statistic value is
     * evaluated to zero.
     */
    DEMAND_EXPL_PATH_MIN_LENGTH {

        public double calculateValue(Problem problem) {

            int minAdmissiblePathLength = Integer.MAX_VALUE;
            boolean atLeastOneAdmissiblePath = false;

            for(Demand demand : problem.getNetwork().demands()) {
                for(AdmissiblePath path : demand.admissiblePaths()) {
                    minAdmissiblePathLength = Math.min(minAdmissiblePathLength,
                        path.linkCount());
                    atLeastOneAdmissiblePath = true;
                }
            }

            return (atLeastOneAdmissiblePath) ? minAdmissiblePathLength : 0;
        }
    },

    /**
     * Calculates the maximum (hop-) length of the admissible paths
     * over all demands.<br/>
     * <br/>
     * 
     * If no demand specifies any admissible paths then this statistic value is
     * evaluated to zero.
     */
    DEMAND_EXPL_PATH_MAX_LENGTH {

        public double calculateValue(Problem problem) {

            int maxAdmissiblePaths = 0;

            for(Demand demand : problem.getNetwork().demands()) {
                maxAdmissiblePaths = Math.max(maxAdmissiblePaths,
                    demand.admissiblePathCount());
            }

            return maxAdmissiblePaths;
        }
    },

    /**
     * Calculates the sum of the lengths of all demand's admissible paths, 
     * where the length of a path is the number of links it contains 
     * (hop-length).
     * 
     * If no demand specifies any admissible paths then this statistic value is
     * evaluated to zero.
     */
    DEMAND_EXPL_PATHS_TOTAL_HOP_LENGTH {

        public double calculateValue(Problem problem) {

            int sumAdmissiblePathHops = 0;

            for(Demand demand : problem.getNetwork().demands()) {
                for(AdmissiblePath path : demand.admissiblePaths()) {
                    sumAdmissiblePathHops += path.linkCount();
                }
            }
            return sumAdmissiblePathHops;
        }
    },

    /**
     * Calculates the sum of the lengths of all demand's admissible paths, 
     * where the length of a path is calculated in kilometer.<br/>
     * <br/>
     * 
     * To get a meaningful result it is assumed that the nodes in the network
     * have coordinates which can be interpreted in a geographical manner.<br/>
     * <br/>
     * 
     * If no demand specifies any admissible paths then this statistic value is
     * evaluated to zero.
     */
    DEMAND_EXPL_PATHS_TOTAL_KMS_LENGTH {

        public double calculateValue(Problem problem) {

            int sumAdmissiblePathKms = 0;

            for(Demand demand : problem.getNetwork().demands()) {
                for(AdmissiblePath path : demand.admissiblePaths()) {
                    for(Link link : path.links()) {
                        sumAdmissiblePathKms += NetworkUtils.calculateDistanceInKms(
                            link.getFirstNode(), link.getSecondNode());
                    }
                }
            }
            return sumAdmissiblePathKms;
        }
    };

    abstract public double calculateValue(Problem problem);

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

