/*
 * $Id: Solution.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.solution;

import static com.atesio.utils.ArgChecker.checkNotEmpty;
import static com.atesio.utils.ArgChecker.checkNotLowerThanZero;
import static com.atesio.utils.ArgChecker.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sndlib.core.problem.OperatingState;
import sndlib.core.problem.SolvedProblem;

import com.atesio.utils.StringFormatUtils;

/**
 * This class represents a solution for a specific network design problem.
 * <br/><br/>
 *  
 * A <tt>Solution</tt> encapsulates all information about a solution, such 
 * as the installed link capacities and the demand routings used in the 
 * several states. It can be coupled with a {@link sndlib.core.problem.Problem} 
 * using a {@link SolvedProblem} to check its consistency and feasibility or 
 * to compute statistics.<br/><br/>
 * 
 * In a <tt>Solution</tt> the following invariant is maintained:
 * 
 * <ul>
 *  <li>for all links contained in any flow path of the solution's demand 
 *      routings there also exists a link configuration in the solution
 *  </li>
 * </ul>
 * 
 * @see DemandRouting
 * @see LinkConfiguration
 * @see SolvedProblem
 * @see sndlib.core.validation.SolutionValidators
 * @see sndlib.core.statistics.SolutionStatistics
 *
 * @author Roman Klaehne
 */
final public class Solution {

    /**
     * The name of this solution.
     */
    private String _name;

    /**
     * The link configurations of this solution.
     */
    private Map<String, LinkConfiguration> _linkConfigs;

    /**
     * The demand routings of this solution keyed by the operating state.
     */
    private Map<OperatingState, Map<String, DemandRouting>> _demandRoutings;

    /**
     * Constructs a new solution without specifying a name. The name of the
     * new solution will be <tt>noName</tt>.
     */
    public Solution() {

        this("noName");
    }

    /**
     * Constructs a new solution with the specified name.
     * 
     * @param name the name of the solution
     */
    public Solution(String name) {

        setName(name);

        _linkConfigs = new LinkedHashMap<String, LinkConfiguration>();
        _demandRoutings = new LinkedHashMap<OperatingState, Map<String, DemandRouting>>();
    }

    /**
     * Sets the name of this solution.
     * 
     * @param name the new name of this solution
     */
    public void setName(String name) {

        checkNotEmpty(name, "name");
        _name = name;
    }

    /**
     * Returns the name of this solution.
     * 
     * @return the name of this solution
     */
    public String getName() {

        return _name;
    }

    /**
     * Constructs a new <tt>LinkConfiguration</tt> for the specified link 
     * in this solution.
     * 
     * @param linkId the ID of the link for which the link configuration 
     * is constructed
     * 
     * @return a new <tt>LinkConfiguration</tt> instance
     * 
     * @throws IllegalArgumentException if this solution already contains a
     * configuration for the specified link 
     */
    public LinkConfiguration newLinkConfig(String linkId) {

        checkNotNull(linkId, "link id");

        if(_linkConfigs.containsKey(linkId)) {
            throw new IllegalArgumentException("configuration for link " + linkId
                + " is already contained in this solution");
        }

        LinkConfiguration linkConf = new LinkConfiguration(linkId);
        _linkConfigs.put(linkId, linkConf);

        return linkConf;
    }

    /**
     * Constructs a new <tt>DemandRouting</tt> for the specified demand in 
     * this solution. The routing is assumed to be used when the network is
     * in the given operating state.
     * 
     * @param demandId the ID of the demand for which the routing is 
     * constructed
     * @param state the operating state 
     * 
     * @throws IllegalArgumentException if this solution already contains 
     * a routing in the given operating state for the specified demand
     */
    public DemandRouting newRouting(String demandId, OperatingState state) {

        checkNotNull(demandId, "demand id");
        checkNotNull(state, "state");

        Map<String, DemandRouting> routingsInState = _demandRoutings.get(state);
        if(routingsInState == null) {
            routingsInState = new HashMap<String, DemandRouting>();
            _demandRoutings.put(state, routingsInState);
        }

        if(routingsInState.containsKey(demandId)) {
            throw new IllegalArgumentException("routing for demand " + demandId
                + " in state " + state + " already defined in this solution");
        }

        DemandRouting routing = new DemandRouting(demandId, state);

        routingsInState.put(demandId, routing);

        return routing;
    }

    /**
     * Constructs a new <tt>FlowPath</tt> having the specified flow value 
     * and running along a path specified by the given list of link ID's.
     * <br/>
     * The flow path is added to the given demand routing.
     * 
     * @param routing the routing the created flow path will be added to
     * @param flowPathValue the flow value
     * @param path a list containing the ID's of the links forming the path
     * 
     * @return the new <tt>FlowPath</tt> instance
     * 
     * @throws IllegalArgumentException if the given flow path value is lower
     * than zero
     * @throws IllegalArgumentException if the given list of link ID's is 
     * empty
     * @throws IllegalStateException if the given routing does not belong to
     * this solution
     * @throws IllegalStateException if for any link in the given path there 
     * is no corresponding link configuration in this solution.
     */
    public FlowPath newFlowPath(DemandRouting routing, double flowPathValue,
        List<String> path) {

        checkNotLowerThanZero(flowPathValue, "flow path value");
        checkNotNull(path, "link ids");

        if(!containsRouting(routing)) {
            throw new IllegalStateException(
                "the given routing does not belong to this solution");
        }

        if(path.size() == 0) {
            throw new IllegalArgumentException(
                "there must be at least one link in the path");
        }

        for(String linkId : path) {
            if(!hasLinkConfig(linkId)) {
                throw new IllegalStateException("there is no configuration for "
                    + linkId + " in this solution; add link configuration first!");
            }
        }

        FlowPath flowPath = new FlowPath(flowPathValue, path);
        routing.addFlowPath(flowPath);

        return flowPath;
    }

    /**
     * Constructs a new <tt>FlowPath</tt> having the specified flow value 
     * and running along a path specified by the given list of link ID's.
     * <br/>
     * The flow path is added to the given demand routing.
     * 
     * @param routing the routing the created flow path will be added to
     * @param flowPathValue the flow value
     * @param path a list containing the ID's of the links forming the path
     * 
     * @return the new <tt>FlowPath</tt> instance
     * 
     * @throws IllegalArgumentException if the given flow path value is lower
     * than zero
     * @throws IllegalArgumentException if the given list of link ID's is 
     * empty
     * @throws IllegalStateException if the given routing does not belong to
     * this solution
     * @throws IllegalStateException if for any link in the given path there 
     * is no corresponding link configuration in this solution.
     */
    public FlowPath newFlowPath(DemandRouting routing, double flowPathValue,
        String... path) {

        return newFlowPath(routing, flowPathValue, Arrays.asList(path));
    }

    /**
     * Returns the number of link configurations in this solution.
     * 
     * @return the number of link configurations
     */
    public int linkConfigCount() {

        return _linkConfigs.size();
    }

    /**
     * Returns the link configuration corresponding to the specified link.
     * 
     * @param linkId the link ID
     * 
     * @return the link configuration corresponding to the specified link;
     * <tt>null</tt> if there is no such link configuration
     */
    public LinkConfiguration getLinkConfig(String linkId) {

        checkNotNull(linkId, "link id");

        return _linkConfigs.get(linkId);
    }

    /**
     * Returns for the specified demand the routing, which is used when the 
     * network is in the given operating state.
     * 
     * @return for the specified demand the routing which is used when the
     * network is in the given State
     */
    public DemandRouting getRouting(String demandId, OperatingState state) {

        checkNotNull(demandId, "demand id");
        checkNotNull(state, "state");

        DemandRouting routingInState = null;

        Map<String, DemandRouting> routingsInState = _demandRoutings.get(state);
        if(routingsInState != null) {
            routingInState = routingsInState.get(demandId);
        }

        if(routingInState == null && state.getType() != OperatingState.Type.NOS) {

            DemandRouting nosRouting = getRouting(demandId, OperatingState.NOS);
            if(state.getType() == OperatingState.Type.SINGLE_LINK_FAILURE) {
                if(nosRouting != null && nosRouting.containsLink(state.getName())) {
                    routingsInState = _demandRoutings.get(OperatingState.DEFAULT_BACKUP);
                    if(routingsInState != null) {
                        routingInState = routingsInState.get(demandId);
                    }
                }
            }
            if(routingInState == null) {
                routingInState = nosRouting;
            }
        }
        return routingInState;
    }

    /**
     * Returns <tt>true</tt> if this solution contains a configuration
     * for the specified link.
     * 
     * @param linkId the ID of the link
     * 
     * @return <tt>true</tt> if and only if there is a configuration for
     * the specified link; <tt>false</tt> otherwise
     */
    public boolean hasLinkConfig(String linkId) {

        checkNotNull(linkId, "link id");

        return _linkConfigs.containsKey(linkId);
    }

    /**
     * Tests whether this solution contains the given link configuration
     * instance.
     * 
     * @param linkConf the link configuration
     * 
     * @return <tt>true</tt> if and only if this solution contains the
     * given link configuration; <tt>false</tt> otherwise
     */
    public boolean containsLinkConfig(LinkConfiguration linkConf) {

        checkNotNull(linkConf, "link configuration");

        return _linkConfigs.get(linkConf.getLinkId()) == linkConf;
    }

    /**
     * Tests whether this solution contains the given demand routing instance.
     * 
     * @param routing the demand routing to look for
     * 
     * @return <tt>true</tt> if and only if this solution contains the
     * given demand routing; <tt>false</tt> otherwise
     */
    public boolean containsRouting(DemandRouting routing) {

        checkNotNull(routing, "routing");

        return getRouting(routing.getDemandId(), routing.getState()) == routing;
    }

    /**
     * Returns a <tt>Collection</tt> view of the link configurations in 
     * this solution.<br/><br/>
     * 
     * The returned collection is unmodifiable. That means that each 
     * attempt to modify the collection will cause an 
     * <tt>UnsupportedOperationException</tt>.
     * 
     * @return an unmodifiable collection view of the link configurations 
     * in this solution. 
     */
    public Collection<LinkConfiguration> linkConfigs() {

        return Collections.unmodifiableCollection(_linkConfigs.values());
    }

    /**
     * Returns a <tt>Collection</tt> view of the demand routings which are 
     * used when the network is in the given state.<br/><br/>
     * 
     * @return a collection view of the demand routings which are used when 
     * the network is in the given state
     */
    public Collection<DemandRouting> routings(OperatingState state) {

        Map<String, DemandRouting> routingsInState = _demandRoutings.get(state);
        if(routingsInState == null) {
            routingsInState = new HashMap<String, DemandRouting>();
        }
        else {
            routingsInState = new HashMap<String, DemandRouting>(routingsInState);
        }

        if(state.getType() == OperatingState.Type.NOS) {
            return routingsInState.values();
        }

        Map<String, DemandRouting> nosRoutings = _demandRoutings.get(OperatingState.NOS);
        if(state.getType() == OperatingState.Type.SINGLE_LINK_FAILURE) {
            String failureLink = state.getName();
            Map<String, DemandRouting> defBackupRoutings = _demandRoutings.get(OperatingState.DEFAULT_BACKUP);

            if(defBackupRoutings != null) {
                for(Map.Entry<String, DemandRouting> routing : defBackupRoutings.entrySet()) {
                    String demandId = routing.getKey();
                    DemandRouting nosRouting = getRouting(demandId,
                        OperatingState.NOS);

                    if(!routingsInState.containsKey(demandId)
                        && (nosRouting != null && nosRouting.containsLink(failureLink))) {
                        routingsInState.put(demandId, routing.getValue());
                    }
                }
            }
        }
        if(nosRoutings != null) {
            for(Map.Entry<String, DemandRouting> nosRouting : nosRoutings.entrySet()) {
                if(!routingsInState.containsKey(nosRouting.getKey())) {
                    routingsInState.put(nosRouting.getKey(), nosRouting.getValue());
                }
            }
        }

        return routingsInState.values();
    }

    /**
     * Returns a <tt>Map</tt> view of all demand routings in this solution. 
     * The map keys referencing the operating states and the corresponding 
     * demand routings are exactly those as they were created in this solution.
     * 
     * The returned map is unmodifiable. That means that each attempt 
     * to modify the map will cause an 
     * <tt>UnsupportedOperationException</tt>
     * 
     * @return an unmodifiable map view of al demand routings in this solution
     */
    public Map<OperatingState, Collection<DemandRouting>> allRoutings() {

        Map<OperatingState, Collection<DemandRouting>> allRoutings = new HashMap<OperatingState, Collection<DemandRouting>>();

        for(Map.Entry<OperatingState, Map<String, DemandRouting>> entry : _demandRoutings.entrySet()) {
            allRoutings.put(entry.getKey(), entry.getValue().values());
        }

        return Collections.unmodifiableMap(allRoutings);
    }

    /**
     * Returns a textual representation of this solution.
     * 
     * @return a textual representation
     */
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append("solution [\n\n");

        result.append(" linkConfigurations [\n");
        for(LinkConfiguration linkConf : _linkConfigs.values()) {
            result.append(StringFormatUtils.indentByLine(linkConf.toString(), 2)
                + "\n");
        }
        result.append(" ]\n");

        for(OperatingState state : _demandRoutings.keySet()) {
            if(_demandRoutings.get(state).size() > 0) {
                result.append("\n routings in state " + state.getName() + " [\n");
                for(DemandRouting routing : _demandRoutings.get(state).values()) {
                    result.append(StringFormatUtils.indentByLine(routing.toString(),
                        2)
                        + "\n");
                }
                result.append(" ]\n");
            }
        }

        result.append("]");

        return result.toString();
    }
}
