/*
 * $Id: SolvedProblem.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.problem;

import static com.atesio.utils.ArgChecker.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import sndlib.core.model.Model;
import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.solution.DemandRouting;
import sndlib.core.solution.FlowPath;
import sndlib.core.solution.LinkConfiguration;
import sndlib.core.solution.ModuleConfiguration;
import sndlib.core.solution.Solution;
import sndlib.core.util.NetworkUtils;
import sndlib.core.util.PathSet;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.StringFormatUtils;
import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This class connects a specific {@link Solution} to a network design 
 * {@link sndlib.core.problem.Problem}.<br/><br/>
 * 
 * It provides methods to check the consistency of the solution with
 * the problem and to compute the routing paths and flow values derived
 * from that solution.
 * 
 * @see Solution
 * @see sndlib.core.problem.Problem
 * @see RoutingPath
 * @see DemandFlow
 * @see LinkFlow
 * 
 * @author Roman Klaehne
 */
public class SolvedProblem extends Problem {

    /**
     * The underlying solution of this solved problem.
     */
    private Solution _solution;

    /**
     * Caches the demand flows in this solved problem.
     */
    private Map<OperatingState, Map<Demand, DemandFlow>> _demandFlows;

    /**
     * Caches the link flows in this solved problem.
     */
    private Map<OperatingState, Map<Link, LinkFlow>> _linkFlows;

    /**
     * Caches the demand-link flows in this solved problem.
     */
    private Map<OperatingState, Map<Link, Map<Demand, DemandLinkFlow>>> _demandLinkFlows;

    /**
     * Caches the routing paths in this solved problem.
     */
    private Map<FlowPath, RoutingPath> _routingPaths;

    /**
     * Constructs a new <tt>SolvedProblem</tt>.
     * 
     * @param network the problem's network
     * @param model the problem's model
     * @param solution a specific solution of the problem
     */
    public SolvedProblem(Network network, Model model, Solution solution) {

        super(network, model);

        checkNotNull(solution, "solution");
        _solution = solution;
    }

    /**
     * Constructs a new <tt>SolvedProblem</tt>.
     * 
     * @param problem the problem
     * @param solution a specific solution of the problem
     */
    public SolvedProblem(Problem problem, Solution solution) {

        this(problem.getNetwork(), problem.getModel(), solution);

        setName(problem.getName());
    }

    /**
     * Returns the flow on the specified link in the specified operating state.
     * <br/><br/>
     * 
     * If the flows are not yet computed, this method initiates the computation
     * of all routing paths and flows (see {@link #computeFlowsAndPaths()}) 
     * automatically.
     * 
     * @param link the link
     * @param state the operating state
     * 
     * @return the link flow
     * 
     * @throws IllegalArgumentException if the specified link does not exist
     * in the underlying network
     * @throws IllegalStateException if the link flows are not yet computed
     * and the underlying solution is inconsistent with the problem
     */
    public LinkFlow getLinkFlow(Link link, OperatingState state)
        throws IllegalArgumentException, IllegalStateException {

        checkNotNull(link, "link");
        checkNotNull(state, "state");

        if(!getNetwork().containsLink(link)) {
            throw new IllegalArgumentException("link '" + link.getId()
                + "' not found in network");
        }

        return doGetLinkFlow(link, state);
    }

    /**
     * Returns the flow between the source and target node of the specified 
     * demand in the specified operating state.<br/><br/>
     * 
     * If the flows are not yet computed, this method initiates the computation
     * of all routing paths and flows (see {@link #computeFlowsAndPaths()})
     * automatically.
     * 
     * @param demand the demand
     * @param state the operating state
     * 
     * @return the demand flow
     * 
     * @throws IllegalArgumentException if the specified demand does not 
     * exist in the underlying network
     * @throws IllegalStateException if the demand flows are not yet computed
     * and the underlying solution is inconsistent with the problem
     */
    public DemandFlow getDemandFlow(Demand demand, OperatingState state)
        throws IllegalArgumentException, IllegalStateException {

        checkNotNull(demand, "demand");
        checkNotNull(state, "state");

        if(!getNetwork().containsDemand(demand)) {
            throw new IllegalArgumentException("demand " + demand
                + " not found in network");
        }

        return doGetDemandFlow(demand, state);
    }

    /**
     * Returns the flow on the specified link incurred by the given demand
     * in the specified operating state.<br/><br/>
     * 
     * If the flows are not yet computed, this method initiates the computation
     * of all routing paths and flows (see {@link #computeFlowsAndPaths()}) 
     * automatically.
     * 
     * @param demand the demand incurring the flow on the link
     * @param link the link
     * @param state the operating state
     * 
     * @return the link flow 
     * 
     * @throws IllegalArgumentException if the specified link or demand does 
     * not exist in the underlying network
     * @throws IllegalStateException if the link flows are not yet computed
     * and the underlying solution is inconsistent with the problem
     */
    public DemandLinkFlow getDemandLinkFlow(Demand demand, Link link,
        OperatingState state) throws IllegalArgumentException, IllegalStateException {

        checkNotNull(demand, "demand");
        checkNotNull(link, "link");
        checkNotNull(state, "state");

        Network net = getNetwork();
        if(!net.containsLink(link)) {
            throw new IllegalArgumentException("link '" + link.getId()
                + "' not found in network");
        }

        if(!net.containsDemand(demand)) {
            throw new IllegalArgumentException("demand '" + demand.getId()
                + "' not found in network");
        }

        return doGetDemandLinkFlow(demand, link, state);
    }

    /**
     * Returns the total flow on the given path which is incurred by the
     * specified demand when the network is in the given state.<br/><br/>
     * 
     * Paths are compared with respect to the link model. If the link model 
     * is <tt>UNDIRECTED</tt> the direction into which the path is traversed 
     * is not taken into account.
     * 
     * @param demand the demand
     * @param path the path for which the flow value is determined
     * @param state the operating state
     * 
     * @return the total flow on the given path which is incurred by the
     * specified demand in the given state
     */
    public DemandPathFlow getDemandPathFlow(Demand demand, List<String> path,
        OperatingState state) {

        checkNotNull(path, "path");
        checkNotNull(demand, "demand");
        checkNotNull(state, "state");

        if(!getNetwork().containsDemand(demand)) {
            throw new IllegalArgumentException("demand '" + demand.getId()
                + "' not found in network");
        }

        if(_routingPaths == null) {
            computeFlowsAndPaths();
        }

        DemandPathFlow pathFlow = new DemandPathFlow(demand, path);

        DemandRouting routing = getRouting(demand, state);
        if(routing == null) {
            return pathFlow;
        }

        for(FlowPath fp : routing.flowPaths()) {
            // check path feasibility
            RoutingPath routingPath = _routingPaths.get(fp);
            if(routingPath == null) {
                continue;
            }

            // check paths equality w.r.t link model
            PathSet.PathComparator comp = new PathSet.PathComparator(getLinkModel());
            if(comp.compare(fp.linkIds(), path) == 0) {
                if(routingPath.getFirst().getSource() == demand.getFirstNode()) {
                    pathFlow.increasePosistiveFlow(fp.flowPathValue());
                }
                else {
                    pathFlow.increaseNegativeFlow(fp.flowPathValue());
                }
            }
        }
        return pathFlow;
    }

    /**
     * Returns the <tt>RoutingPath</tt> corresponding to the specified flow
     * path.<br/></br/>
     * 
     * If the routing paths are not yet computed, this method initiates the 
     * computation of all routing paths and flows (see 
     * {@link #computeFlowsAndPaths()}) automatically.
     * 
     * @param flowPath a flow path contained in the underlying solution
     * 
     * @return the routing path corresponding to the specified flow path;
     * <tt>null</tt> if the given flow path could not be evaluated to a
     * valid routing path according to the underlying network
     */
    public RoutingPath getRoutingPath(FlowPath flowPath)
        throws IllegalArgumentException, IllegalStateException {

        checkNotNull(flowPath, "flow path");

        if(_routingPaths == null) {
            computeFlowsAndPaths();
        }

        if(!_routingPaths.containsKey(flowPath)) {
            throw new IllegalArgumentException(
                "given flow path is not contained in this solution");
        }

        return _routingPaths.get(flowPath);
    }

    /**
     * Initiates the computation of all routing paths, link flows and demand
     * flows.<br/>
     * The results will be cached for later calls to the corresponding supply
     * methods.<br/><br/>
     * 
     * If the underlying solution and problem are not consistent with each 
     * other, an <tt>IllegalStateException</tt> is thrown.
     * 
     * @throws IllegalStateException if the underlying solution and 
     * problem are not consistent with each other
     * 
     * @see #getDemandFlow(Demand, OperatingState)
     * @see #getLinkFlow(Link, OperatingState)
     * @see #getRoutingPath(FlowPath) 
     */
    public void computeFlowsAndPaths() throws IllegalStateException {

        Messages errors = checkConsistency();
        if(errors.size() > 0) {
            throw new IllegalStateException(
                "this solved problem is not consistent; see errors below:\n"
                    + errors.getMessages());

        }

        computeRoutingPaths();
        computeLinkFlows();
        computeDemandFlows();
    }

    /**
     * Tests whether the underlying solution and problem are consistent with
     * each other.<br/><br/>
     * 
     * This method is equivalent to <tt>checkConsistency().size() == 0</tt>.
     * 
     * @return <tt>true</tt> if and only if the underlying solution and 
     * problem are consistent to each other; <tt>false</tt> otherwise
     */
    public boolean isConsistent() {

        return checkConsistency().size() == 0;
    }

    /**
     * Checks whether the underlying solution and problem are consistent with
     * each other.<br/> <br/>
     * 
     * If the returned messages are empty then the solution is consistent with
     * the problem (see {@link #isConsistent()}).<br/>
     * If an consisency error occurs then all the cached routing paths and
     * flows will be released.<br/><br/>
     * 
     * Note: This is no check for solution feasibility! For doing this use  
     * {@link sndlib.core.validation.SolutionValidators}.
     * 
     * @return the error messages indicating where the consistency is violated
     */
    public Messages checkConsistency() {

        Messages errors = doCheckConsistency();
        if(errors.size() > 0) {
            clean();
        }

        return errors;
    }

    /**
     * Cleans the routing paths and flows cache.
     */
    private void clean() {

        _linkFlows = null;
        _demandFlows = null;
        _routingPaths = null;
        _demandLinkFlows = null;
    }

    /**
     * Internal helper method to get link flow corresponding to the given 
     * link.<br/>
     * <br/>
     * 
     * If there is already link flow in cache, it is returned, otherwise a new
     * one is constructed. 
     * 
     * @param link the link
     * 
     * @return the link flow corresponding to the given link.
     */
    private LinkFlow doGetLinkFlow(Link link, OperatingState state) {

        if(_linkFlows == null) {
            computeFlowsAndPaths();
        }

        Map<Link, LinkFlow> flowsInState = _linkFlows.get(state);
        if(flowsInState == null) {
            flowsInState = new IdentityHashMap<Link, LinkFlow>();
            _linkFlows.put(state, flowsInState);
        }

        LinkFlow linkFlow = flowsInState.get(link);
        if(linkFlow == null) {
            linkFlow = new LinkFlow(link);
            flowsInState.put(link, linkFlow);
        }

        return linkFlow;
    }

    /**
     * Internal helper method to get demand flow corresponding to the given 
     * demand.<br/>
     * <br/>
     * 
     * If there is already demand flow in cache, it is returned, otherwise a new
     * one is constructed. 
     * 
     * @param demand the demand
     * 
     * @return the demand flow corresponding to the given demand.
     */
    private DemandFlow doGetDemandFlow(Demand demand, OperatingState state) {

        if(_demandFlows == null) {
            computeFlowsAndPaths();
        }

        Map<Demand, DemandFlow> flowsInState = _demandFlows.get(state);
        if(flowsInState == null) {
            flowsInState = new IdentityHashMap<Demand, DemandFlow>();
            _demandFlows.put(state, flowsInState);
        }

        DemandFlow demandFlow = flowsInState.get(demand);
        if(demandFlow == null) {
            demandFlow = new DemandFlow(demand);
            flowsInState.put(demand, demandFlow);
        }

        return demandFlow;
    }

    /**
     * Internal helper method to get link flow incurred by a specific demand.
     * <br/><br/>
     * 
     * If there is already demand flow in cache, it is returned, otherwise a new
     * one is constructed. 
     * 
     * @param demand the demand incurring the flow
     * @param link the link 
     * 
     * @return the demand flow corresponding to the given demand.
     */
    private DemandLinkFlow doGetDemandLinkFlow(Demand demand, Link link,
        OperatingState state) {

        if(_demandLinkFlows == null) {
            computeFlowsAndPaths();
        }

        Map<Link, Map<Demand, DemandLinkFlow>> flowsInState = _demandLinkFlows.get(state);
        if(flowsInState == null) {
            flowsInState = new IdentityHashMap<Link, Map<Demand, DemandLinkFlow>>();
            _demandLinkFlows.put(state, flowsInState);
        }

        Map<Demand, DemandLinkFlow> demandsLinkFlows = flowsInState.get(link);
        if(demandsLinkFlows == null) {
            demandsLinkFlows = new IdentityHashMap<Demand, DemandLinkFlow>();
            flowsInState.put(link, demandsLinkFlows);
        }

        DemandLinkFlow demandLinkFlow = demandsLinkFlows.get(demand);
        if(demandLinkFlow == null) {
            demandLinkFlow = new DemandLinkFlow(demand, link);
            demandsLinkFlows.put(demand, demandLinkFlow);
        }

        return demandLinkFlow;
    }

    /**
     * Lists the message keys referencing the error messages reported by the 
     * consistency check.
     */
    private static class ConsistencyErrorKeys {

        final static MessageKey LINK_NOT_FOUND = new SimpleMessageKey(
            "validator.solution.error.linkNotFound");

        final static MessageKey MODULE_FOR_LINK_NOT_FOUND = new SimpleMessageKey(
            "validator.solution.error.moduleForLinkNotFound");

        final static MessageKey DEMAND_NOT_FOUND = new SimpleMessageKey(
            "validator.solution.error.demandNotFound");

        final static MessageKey LINK_FOR_ROUTING_NOT_FOUND = new SimpleMessageKey(
            "validator.solution.error.linkInRoutingNotFound");
    }

    /**
     * Checks whether the given solution and problem are consistent with 
     * each other.<br/>
     * <br/>
     * 
     * It is checked whether
     * <ul>
     *  <li>for all link configurations there is a corresponding link in the 
     *  network</li>
     *  <li>for all module configurations the corresponding network link allows
     *   for a module having the capacity specified in the configuration</li>
     *  <li>for all demand routings there is a corresponding demand in the 
     *  network</li>
     *  <li>all specified links building up the demand's routing paths are 
     *  really contained in the underlying network.</li>
     * </ul>
     * 
     * @return the error messages indicating where the consistency is
     * violated
     */
    private Messages doCheckConsistency() {

        Messages errors = new Messages();

        Network network = getNetwork();

        for(LinkConfiguration linkConf : _solution.linkConfigs()) {

            String linkId = linkConf.getLinkId();
            if(!network.hasLink(linkId)) {
                errors.add(SNDlibMessages.error(ConsistencyErrorKeys.LINK_NOT_FOUND,
                    linkId));
                continue;
            }

            Link link = network.getLink(linkId);

            for(ModuleConfiguration moduleConf : linkConf.moduleConfigs()) {

                double moduleCapacity = moduleConf.getModuleCapacity();
                if(moduleCapacity != link.getPreCapacity()) {
                    if(!link.hasModule(moduleCapacity)) {
                        errors.add(SNDlibMessages.error(
                            ConsistencyErrorKeys.MODULE_FOR_LINK_NOT_FOUND,
                            moduleCapacity, linkId));
                    }
                }
            }
        }
        for(Map.Entry<OperatingState, Collection<DemandRouting>> routingsInState : _solution.allRoutings().entrySet()) {
            for(DemandRouting routing : routingsInState.getValue()) {

                String demandId = routing.getDemandId();
                if(!network.hasDemand(demandId)) {
                    errors.add(SNDlibMessages.error(
                        ConsistencyErrorKeys.DEMAND_NOT_FOUND, demandId));
                }

                for(FlowPath flowPath : routing.flowPaths()) {
                    for(String linkId : flowPath.linkIds()) {
                        if(!network.hasLink(linkId)) {
                            errors.add(SNDlibMessages.error(
                                ConsistencyErrorKeys.LINK_FOR_ROUTING_NOT_FOUND,
                                linkId, demandId, routingsInState.getKey()));
                        }
                    }
                }
            }
        }
        return errors;
    }

    /**
     * Internal helper method which, for all links, calculates the total flow 
     * and the flow incurred by all demands. 
     * 
     * It is called from {@link #computeFlowsAndPaths()}.
     */
    private void computeLinkFlows() {

        _linkFlows = new HashMap<OperatingState, Map<Link, LinkFlow>>();
        _demandLinkFlows = new HashMap<OperatingState, Map<Link, Map<Demand, DemandLinkFlow>>>();

        for(OperatingState state : operatingStates()) {
            for(DemandRouting routing : _solution.routings(state)) {

                String demandId = routing.getDemandId();
                Demand demand = getNetwork().getDemand(demandId);
                if(demand == null) {
                    continue;
                }

                for(FlowPath flowPath : routing.flowPaths()) {

                    double flowPathValue = flowPath.flowPathValue();
                    RoutingPath routingPath = _routingPaths.get(flowPath);
                    if(routingPath == null) {
                        continue;
                    }

                    for(RoutingLink routingLink : routingPath.routingLinks()) {

                        Link link = routingLink.getLink();
                        LinkFlow linkFlow = doGetLinkFlow(link, state);
                        DemandLinkFlow demandLinkFlow = doGetDemandLinkFlow(demand,
                            link, state);

                        if(routingLink.isPositiveDirection()) {
                            linkFlow.increasePosistiveFlow(demand.getRoutingUnit()
                                * flowPathValue);
                            demandLinkFlow.increasePosistiveFlow(demand.getRoutingUnit()
                                * flowPathValue);
                        }
                        else {
                            linkFlow.increaseNegativeFlow(demand.getRoutingUnit()
                                * flowPathValue);
                            demandLinkFlow.increaseNegativeFlow(demand.getRoutingUnit()
                                * flowPathValue);
                        }
                    }
                }
            }
        }
    }

    /**
     * Internal helper method which calculates the flow between the end nodes
     * of all demands. 
     * 
     * It is called from {@link #computeFlowsAndPaths()}.
     */
    private void computeDemandFlows() {

        _demandFlows = new HashMap<OperatingState, Map<Demand, DemandFlow>>();

        for(OperatingState state : operatingStates()) {
            for(DemandRouting routing : _solution.routings(state)) {

                String demandId = routing.getDemandId();
                Demand demand = getNetwork().getDemand(demandId);
                if(demand == null) {
                    continue;
                }

                DemandFlow demandFlow = doGetDemandFlow(demand, state);
                for(FlowPath flowPath : routing.flowPaths()) {

                    RoutingPath routingPath = _routingPaths.get(flowPath);
                    if(routingPath == null) {
                        continue;
                    }

                    if(routingPath.getFirst().getSource() == demand.getFirstNode()) {
                        demandFlow.increasePosistiveFlow(flowPath.flowPathValue());
                    }
                    else {
                        demandFlow.increaseNegativeFlow(flowPath.flowPathValue());
                    }
                }
            }
        }
    }

    /**
     * Internal helper method which computes all the routing paths used to 
     * route the demands. 
     * 
     * It is called from {@link #computeFlowsAndPaths()}.
     */
    private void computeRoutingPaths() {

        RoutingPath.Builder pathBuilder = RoutingPath.getBuilderFor(getLinkModel(),
            getDemandModel());

        _routingPaths = new IdentityHashMap<FlowPath, RoutingPath>();

        for(OperatingState state : operatingStates()) {
            for(DemandRouting routing : _solution.routings(state)) {

                String demandId = routing.getDemandId();
                Demand demand = getNetwork().getDemand(demandId);
                if(demand == null) {
                    continue;
                }

                for(FlowPath flowPath : routing.flowPaths()) {

                    List<Link> links = NetworkUtils.getLinksByIds(
                        flowPath.linkIds(), getNetwork());

                    RoutingPath routingPath = pathBuilder.newPath(links,
                        demand.getFirstNode(), demand.getSecondNode());

                    _routingPaths.put(flowPath, routingPath);
                }
            }
        }
    }

    /**
     * Returns the link corresponding to the given link configuration.
     * 
     * @param linkConf the link configuration
     * 
     * @return the link corresponding to the given link configuration
     * 
     * @throws IllegalArgumentException if the underlying solution does 
     * not specify the given link configuration
     * @throws IllegalStateException if there is no corresponding link 
     * in the underlying network
     */
    public Link getLink(LinkConfiguration linkConf) {

        checkNotNull(linkConf, "link configuration");

        if(!_solution.containsLinkConfig(linkConf)) {
            throw new IllegalArgumentException(
                "given link configuration does not belong to this solution");
        }

        Link link = getNetwork().getLink(linkConf.getLinkId());
        if(link == null) {
            throw new IllegalStateException("link " + linkConf.getLinkId()
                + " exists in solution, but not in problem");
        }

        return link;
    }

    /**
     * Returns the link with the specified ID in the underlying network.
     * 
     * @param linkId the ID of the link
     * 
     * @return the link with the specified ID; <tt>null</tt> if the
     * underlying network does not contain such a link
     */
    public Link getLink(String linkId) {

        checkNotNull(linkId, "link id");

        return getNetwork().getLink(linkId);
    }

    /**
     * Returns the link configuration corresponding to the given link.
     * 
     * @param link the link
     * 
     * @return the link configuration corresponding to the given link
     * 
     * @throws IllegalArgumentException if the given link does not belong
     * to the underlying network
     */
    public LinkConfiguration getLinkConf(Link link) {

        checkNotNull(link, "link");

        if(!getNetwork().containsLink(link)) {
            throw new IllegalArgumentException(
                "given link does not belong to this problem");
        }

        return _solution.getLinkConfig(link.getId());
    }

    /**
     * Returns a <tt>Collection</tt> view of the link configurations specified 
     * in the underlying solution.<br/>
     * <br/>
     * 
     * The returned collection is unmodifiable. That means that each attempt to 
     * modify the collection will cause an <tt>UnsupportedOperationException</tt>.
     * 
     * @return an unmodifiable collection view of the link configurations 
     * specified underlying solution
     */
    public Collection<LinkConfiguration> linkConfigs() {

        return _solution.linkConfigs();
    }

    /**
     * Returns a <tt>Collection</tt> view of the demand routings which are 
     * used when the network is in the given state.<br/>
     * <br/>
     * 
     * The returned collection is unmodifiable. That means that each attempt 
     * to modify the collection will cause an 
     * <tt>UnsupportedOperationException</tt>.<br/>
     * <br/>
     * 
     * This method is for convenience and is the same as 
     * <tt>getSolution().routingsInState(state)</tt>
     * 
     * @return an unmodifiable collection view of the demand routings which 
     * are used when the network is in the given state
     */
    public Collection<DemandRouting> routings(OperatingState state) {

        return _solution.routings(state);
    }

    /**
     * Returns for the specified demand the routing, which is used when the 
     * network is in the given operating state.<br/>
     * <br/>
     * 
     * This method is for convenience and is the same as 
     * <tt>getSolution().getRoutingInState(demandId, state)</tt>
     * 
     * @param demand the demand
     * @param state the operating state 
     * 
     * @return for the specified demand the routing which is used when the
     * network is in the given State
     */
    public DemandRouting getRouting(Demand demand, OperatingState state) {

        return _solution.getRouting(demand.getId(), state);
    }

    /**
     * Returns the underlying solution.
     * 
     * @return the underlying solution
     */
    public Solution getSolution() {

        return _solution;
    }

    /**
     * Returns the name of the underlying solution.<br/>
     * <br/>
     * 
     * This method is for convenience and is the same as 
     * <tt>getSolution().getName()</tt>
     * 
     * @return the name of the underlying solution
     */
    public String getSolutionName() {

        return _solution.getName();
    }

    /**
     * Returns the name of the underlying problem.<br/>
     * <br/>
     * 
     * This method is equivalent to {@link Problem#getName()}. 
     * 
     * @return the name of the underlying problem
     */
    public String getProblemName() {

        return getName();
    }

    /**
     * Returns a textual representation of this solved problem.
     * 
     * @return a textual representation of this solved problem
     */
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append("solvedProblem [\n\n");
        result.append(StringFormatUtils.indentByLine(super.toString(), 1) + "\n\n");
        result.append(StringFormatUtils.indentByLine(_solution.toString(), 1) + "\n");
        result.append("]");

        return result.toString();
    }
}
