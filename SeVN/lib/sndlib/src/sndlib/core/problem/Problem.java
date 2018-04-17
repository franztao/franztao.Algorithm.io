/*
 * $Id: Problem.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import static com.atesio.utils.ArgChecker.checkNotEmpty;
import static com.atesio.utils.ArgChecker.checkNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import sndlib.core.model.AdmissiblePathModel;
import sndlib.core.model.DemandModel;
import sndlib.core.model.FixedChargeModel;
import sndlib.core.model.HopLimitModel;
import sndlib.core.model.LinkCapacityModel;
import sndlib.core.model.LinkModel;
import sndlib.core.model.Model;
import sndlib.core.model.NodeModel;
import sndlib.core.model.ObjectiveModel;
import sndlib.core.model.RoutingModel;
import sndlib.core.model.SurvivabilityModel;
import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.util.NamingConventions;

import com.atesio.utils.StringFormatUtils;

/**
 * A <tt>Problem</tt> couples a {@link Network} with a {@link Model}. 
 * <br/><br/>
 * 
 * To check the feasibility of a <tt>Problem</tt> or to calculate statistics
 * of the same, have a look at {@link sndlib.core.validation.ProblemValidators}
 * and {@link sndlib.core.statistics.ProblemStatistics} respectively.
 * 
 * @see Network
 * @see Model
 * @see sndlib.core.validation.ProblemValidators
 * @see sndlib.core.statistics.ProblemStatistics
 * 
 * @author Roman Klaehne
 */
public class Problem {

    /**
     * The name of the problem.
     */
    private String _name;

    /**
     * The problem's network.
     */
    private Network _network;

    /**
     * The problem's model.
     */
    private Model _model;

    /**
     * Constructs a new problem from the given network and model.
     * 
     * The name of the problem is determined by 
     * {@link NamingConventions#constructProblemName(String, String)}
     * 
     * @param network the problem's network
     * @param model the problem's model
     */
    public Problem(Network network, Model model) {

        this(network, model, NamingConventions.constructProblemName(
            network.getName(), model.getName()));
    }

    /**
     * Constructs a new problem from the given network and model with the 
     * specified name.
     * 
     * @param network the problem's network
     * @param model the problem's model
     * @param name the name of the problem
     */
    public Problem(Network network, Model model, String name) {

        checkNotNull(network, "network");
        checkNotNull(model, "model");

        setName(name);

        _network = network;
        _model = model;
    }

    /**
     * Sets the name of this problem.
     * 
     * @param name the name for this problem.
     * 
     * @throws IllegalArgumentException if the given name is <tt>null</tt>
     * or an empty string
     */
    public void setName(String name) {

        checkNotEmpty(name, "name");
        _name = name;
    }

    /**
     * Returns the name of this problem.
     * 
     * @return the name of this problem
     */
    public String getName() {

        return _name;
    }

    /**
     * Returns this problem's network.
     * 
     * @return this problem's network
     */
    public Network getNetwork() {

        return _network;
    }

    /**
     * Returns the number of nodes in this problem's network.
     * <br/><br/>
     * 
     * This method is for convenience and the same as 
     * <tt>getNetwork().nodeCount()</tt>.
     * 
     * @return the number of nodes in this problem's network
     */
    public int nodeCount() {

        return _network.nodeCount();
    }

    /**
     * Returns the number of links in this problem's network.
     * <br/><br/>
     * 
     * This method is for convenience and the same as 
     * <tt>getNetwork().linkCount()</tt>.
     * 
     * @return the number of links in this problem's network
     */
    public int linkCount() {

        return _network.linkCount();
    }

    /**
     * Returns the number of demands in this problem's network.
     * <br/><br/>
     * 
     * This method is for convenience and the same as 
     * <tt>getNetwork().demandCount()</tt>.
     * 
     * @return the number of demands in this problem's network
     */
    public int demandCount() {

        return _network.demandCount();
    }

    /**
     * Returns a <tt>Collection</tt> view of demands in the underlying 
     * network.<br/><br/>
     * 
     * The returned collection is unmodifiable. That means that each attempt to 
     * modify the collection will cause an <tt>UnsupportedOperationException</tt>.
     * 
     * @return an unmodifiable collection view of the demans in the
     * underlying network
     */
    public Collection<Demand> demands() {

        return _network.demands();
    }

    /**
     * Returns a <tt>Collection</tt> view of links in the underlying 
     * network.<br/><br/>
     * 
     * The returned collection is unmodifiable. That means that each attempt to 
     * modify the collection will cause an <tt>UnsupportedOperationException</tt>.
     * 
     * @return an unmodifiable collection view of the links in the
     * underlying network
     */
    public Collection<Link> links() {

        return _network.links();
    }

    /**
     * Returns a <tt>Collection</tt> view of nodes in the underlying 
     * network.<br/><br/>
     * 
     * The returned collection is unmodifiable. That means that each attempt to 
     * modify the collection will cause an <tt>UnsupportedOperationException</tt>.
     * 
     * @return an unmodifiable collection view of the nodes in the
     * underlying network
     */
    public Collection<Node> nodes() {

        return _network.nodes();
    }

    /**
     * Returs the operating states of this problem.<br/>
     * <br/>
     * If there is no survivability mechanism specified in this problem's 
     * survivability model the returned set will only contain the normal
     * operating State (NOS; see {@link OperatingState#NOS}). Otherwise the 
     * set will also contain all states corresponding to single link 
     * failures.
     * 
     * @return a set containing the operating states of this problem  
     */
    public Set<OperatingState> operatingStates() {

        Set<OperatingState> opStates = new HashSet<OperatingState>();
        opStates.add(OperatingState.NOS);

        if(_model.getSurvivabilityModel() != SurvivabilityModel.NO_SURVIVABILITY) {
            for(Link link : _network.links()) {
                opStates.add(OperatingState.singleLinkFailureState(link.getId()));
            }
        }

        return opStates;
    }

    /**
     * Returns this problem's model.
     * 
     * @return this problem's model
     */
    public Model getModel() {

        return _model;
    }

    /**
     * Returns the admissible path model of this problem.
     * 
     * This method is for convenience and is the same as
     * <tt>getModel().getAdmissiblePathModel()</tt>.
     * 
     * @return the admissible path model of this problem
     */
    public AdmissiblePathModel getAdmissiblePathModel() {

        return _model.getAdmissiblePathModel();
    }

    /**
     * Returns the demand model of this problem.
     * 
     * This method is for convenience and is the same as
     * <tt>getModel().getDemandModel()</tt>.
     * 
     * @return the demand model of this problem
     */
    public DemandModel getDemandModel() {

        return _model.getDemandModel();
    }

    /**
     * Returns the fixed-charge model of this problem.
     * 
     * This method is for convenience and is the same as
     * <tt>getModel().getFixedChargeModel()</tt>.
     * 
     * @return the fixed-charge model of this problem
     */
    public FixedChargeModel getFixedChargeModel() {

        return _model.getFixedChargeModel();
    }

    /**
     * Returns the hop-limit model of this problem.
     * 
     * This method is for convenience and is the same as
     * <tt>getModel().getHopLimitModel()</tt>.
     * 
     * @return the hop-limit model of this problem
     */
    public HopLimitModel getHopLimitModel() {

        return _model.getHopLimitModel();
    }

    /**
     * Returns the survivability model of this problem.
     * 
     * This method is for convenience and is the same as
     * <tt>getModel().getSurvivabilityModel()</tt>.
     * 
     * @return the survivability model of this problem
     */
    public SurvivabilityModel getSurvivabilityModel() {

        return _model.getSurvivabilityModel();
    }

    /**
     * Returns the link capacity model of this problem.
     * 
     * This method is for convenience and is the same as
     * <tt>getModel().getLinkCapacityModel()</tt>.
     * 
     * @return the link capacity model of this problem
     */
    public LinkCapacityModel getLinkCapacityModel() {

        return _model.getLinkCapacityModel();
    }

    /**
     * Returns the link model of this problem.
     * 
     * This method is for convenience and is the same as
     * <tt>getModel().getLinkModel()</tt>.
     * 
     * @return the link model of this problem
     */
    public LinkModel getLinkModel() {

        return _model.getLinkModel();
    }

    /**
     * Returns the node model of this problem.
     * 
     * This method is for convenience and is the same as
     * <tt>getModel().getNodeModel()</tt>.
     * 
     * @return the node model of this problem
     */
    public NodeModel getNodeModel() {

        return _model.getNodeModel();
    }

    /**
     * Returns the objective model of this problem.
     * 
     * This method is for convenience and is the same as
     * <tt>getModel().getObjectiveModel()</tt>.
     * 
     * @return the objective model of this problem
     */
    public ObjectiveModel getObjectiveModel() {

        return _model.getObjectiveModel();
    }

    /**
     * Returns the routing model of this problem.
     * 
     * This method is for convenience and is the same as
     * <tt>getModel().getRoutingModel()</tt>.
     * 
     * @return the routing model of this problem
     */
    public RoutingModel getRoutingModel() {

        return _model.getRoutingModel();
    }

    /**
     * Returns a textual representation of this problem.
     * 
     * @return a textual representation
     */
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append("problem [\n\n");

        result.append(StringFormatUtils.indentByLine(_model.toString(), 1) + "\n\n");
        result.append(StringFormatUtils.indentByLine(_network.toString(), 1) + "\n");

        result.append("]");

        return result.toString();
    }
}
