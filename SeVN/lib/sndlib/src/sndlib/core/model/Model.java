/*
 * $Id: Model.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.model;

import static com.atesio.utils.ArgChecker.checkNotEmpty;
import static com.atesio.utils.ArgChecker.checkNotNull;

import sndlib.core.util.NamingConventions;

/**
 * This class represents the model component of a network design problem.<br/>
 * It consists of several submodels describing whether the links are 
 * directed or undirected, whether the capacities are modular or linear, 
 * whether an explicit list of admissible paths should be taken into account 
 * or not, etc.
 * <br/>
 * 
 * Instances of this class are immutable, that means that a <tt>Model</tt> 
 * cannot be changed after construction. For this reason a {@link ModelData} 
 * object is needed to construct a <tt>Model</tt> instance.
 * 
 * @see ModelData
 * @see sndlib.core.problem.Problem
 * 
 * @author Roman Klaehne
 */
final public class Model {

    /**
     * The name of this model.
     */
    private String _name;

    /**
     * The link submodel.
     */
    private LinkModel _linkModel;

    /**
     * The node submodel.
     */
    private NodeModel _nodeModel;

    /**
     * The demand submodel.
     */
    private DemandModel _demandModel;

    /**
     * The link capacity submodel.
     */
    private LinkCapacityModel _linkCapacityModel;

    /**
     * The objective submodel.
     */
    private ObjectiveModel _objectiveModel;

    /**
     * The admissible path submodel.
     */
    private AdmissiblePathModel _admissiblePathModel;

    /**
     * The fixed-charge submodel.
     */
    private FixedChargeModel _fixedChargeModel;

    /**
     * The hop-limit submodel.
     */
    private HopLimitModel _hopLimitModel;

    /**
     * The routing submodel.
     */
    private RoutingModel _routingModel;

    /**
     * The survivability submodel.
     */
    private SurvivabilityModel _survivabilityModel;

    /**
     * Constructs a new <tt>Model</tt>. The name of the model is determined by
     * {@link NamingConventions#constructModelName(Model)}
     * 
     * @param data the model data
     * 
     * @throws IllegalArgumentException if any submodel is undefined in the 
     * given <tt>ModelData</tt>
     */
    public Model(ModelData data) {

        init(data);
        setName(NamingConventions.constructModelName(this));
    }

    /**
     * Constructs a new <tt>Model</tt> with the specified name.
     * 
     * @param data the model data
     * @param name the name of the model
     * 
     * @throws IllegalArgumentException if any submodel is undefined in the 
     * given <tt>ModelData</tt>
     * @throws IllegalArgumentException if the given name is <tt>null</tt>
     * or an empty string
     */
    public Model(ModelData data, String name) throws IllegalArgumentException {

        init(data);
        setName(name);
    }

    /**
     * Initializes this model from the given model data object.
     * 
     * @param data the model data
     * 
     * @throws IllegalArgumentException if any submodel is undefined in the 
     * given <tt>ModelData</tt>
     */
    private void init(ModelData data) throws IllegalArgumentException {

        _linkModel = data.getLinkModel();
        if(_linkModel == null) {
            throw new IllegalArgumentException(
                "no link submodel specified in model data");
        }

        _nodeModel = data.getNodeModel();
        if(_nodeModel == null) {
            throw new IllegalArgumentException(
                "no node submodel specified in model data");
        }

        _demandModel = data.getDemandModel();
        if(_demandModel == null) {
            throw new IllegalArgumentException(
                "no demand submodel specified in model data");
        }

        _linkCapacityModel = data.getLinkCapacityModel();
        if(_linkCapacityModel == null) {
            throw new IllegalArgumentException(
                "no link capacity submodel specified in model data");
        }

        _objectiveModel = data.getObjectiveModel();
        if(_objectiveModel == null) {
            throw new IllegalArgumentException(
                "no link submodel specified in model data");
        }

        _admissiblePathModel = data.getAdmissiblePathModel();
        if(_admissiblePathModel == null) {
            throw new IllegalArgumentException(
                "no admissible routing submodel specified in model data");
        }

        _fixedChargeModel = data.getFixedChargeModel();
        if(_fixedChargeModel == null) {
            throw new IllegalArgumentException(
                "no fixed charge submodel specified in model data");
        }

        _hopLimitModel = data.getHopLimitModel();
        if(_hopLimitModel == null) {
            throw new IllegalArgumentException(
                "no hop limit submodel specified in model data");
        }

        _routingModel = data.getRoutingModel();
        if(_routingModel == null) {
            throw new IllegalArgumentException(
                "no routing submodel specified in model data");
        }

        _survivabilityModel = data.getSurvivabilityModel();
        if(_survivabilityModel == null) {
            throw new IllegalArgumentException(
                "no survivability submodel specified in model data");
        }
    }

    /**
     * Returns the name of this model.
     * 
     * @return this model's name
     */
    public String getName() {

        return _name;
    }

    /**
     * Sets the name of this model.
     * 
     * @param name the name for this model
     * 
     * @throws IllegalArgumentException if the given name is <tt>null</tt>
     * or an empty string
     */
    public void setName(String name) {

        checkNotEmpty(name, "name");
        _name = name;
    }

    /**
     * Returns the admissible path submodel.
     * 
     * @return the admissible path submodel
     */
    public AdmissiblePathModel getAdmissiblePathModel() {

        return _admissiblePathModel;
    }

    /**
     * Returns the demand submodel.
     * 
     * @return the demand submodel
     */
    public DemandModel getDemandModel() {

        return _demandModel;
    }

    /**
     * Returns the fixed-charge submodel.
     * 
     * @return the fixed-charge submodel
     */
    public FixedChargeModel getFixedChargeModel() {

        return _fixedChargeModel;
    }

    /**
     * Returns the hop-limit submodel.
     * 
     * @return the hop-limit submodel
     */
    public HopLimitModel getHopLimitModel() {

        return _hopLimitModel;
    }

    /**
     * Returns the link capacity submodel.
     * 
     * @return the link capacity submodel
     */
    public LinkCapacityModel getLinkCapacityModel() {

        return _linkCapacityModel;
    }

    /**
     * Returns the link submodel.
     * 
     * @return the link submodel
     */
    public LinkModel getLinkModel() {

        return _linkModel;
    }

    /**
     * Returns the node submodel.
     * 
     * @return the node submodel
     */
    public NodeModel getNodeModel() {

        return _nodeModel;
    }

    /**
     * Returns the objective submodel.
     * 
     * @return the objective submodel
     */
    public ObjectiveModel getObjectiveModel() {

        return _objectiveModel;
    }

    /**
     * Returns the routing submodel.
     * 
     * @return the routing submodel
     */
    public RoutingModel getRoutingModel() {

        return _routingModel;
    }

    /**
     * Returns the survivability submodel.
     * 
     * @return the survivability submodel
     */
    public SurvivabilityModel getSurvivabilityModel() {

        return _survivabilityModel;
    }

    /**
     * Returns the submodel corresponding to the given model property.
     * 
     * @param modelProperty the model property
     * 
     * @return the submodel corresponding to the given model property
     */
    public Enum getModel(ModelProperty modelProperty) {

        checkNotNull(modelProperty, "model property");

        switch (modelProperty) {

        case ADMISSIBLE_PATH_MODEL:
            return _admissiblePathModel;

        case ROUTING_MODEL:
            return _routingModel;

        case SURVIVABILITY_MODEL:
            return _survivabilityModel;

        case LINK_CAPACITY_MODEL:
            return _linkCapacityModel;

        case LINK_MODEL:
            return _linkModel;

        case NODE_MODEL:
            return _nodeModel;

        case HOP_LIMIT_MODEL:
            return _hopLimitModel;

        case FIXED_CHARGE_MODEL:
            return _fixedChargeModel;

        case DEMAND_MODEL:
            return _demandModel;

        case OBJECTIVE_MODEL:
            return _objectiveModel;

        default:
            throw new AssertionError("unknown model property: " + modelProperty);
        }
    }

    /**
     * Returns a textual representation of this model.
     * 
     * @return a textual representation
     */
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append("model [\n");

        result.append(" " + ModelProperty.LINK_MODEL.name() + " = "
            + _linkModel.name() + "\n");
        result.append(" " + ModelProperty.LINK_CAPACITY_MODEL.name() + " = "
            + _linkCapacityModel.name() + "\n");
        result.append(" " + ModelProperty.NODE_MODEL.name() + " = "
            + _nodeModel.name() + "\n");
        result.append(" " + ModelProperty.DEMAND_MODEL.name() + " = "
            + _demandModel.name() + "\n");
        result.append(" " + ModelProperty.ROUTING_MODEL.name() + " = "
            + _routingModel.name() + "\n");
        result.append(" " + ModelProperty.SURVIVABILITY_MODEL.name() + " = "
            + _survivabilityModel.name() + "\n");
        result.append(" " + ModelProperty.OBJECTIVE_MODEL.name() + " = "
            + _objectiveModel.name() + "\n");
        result.append(" " + ModelProperty.HOP_LIMIT_MODEL.name() + " = "
            + _hopLimitModel.name() + "\n");
        result.append(" " + ModelProperty.FIXED_CHARGE_MODEL.name() + " = "
            + _fixedChargeModel.name() + "\n");
        result.append(" " + ModelProperty.ADMISSIBLE_PATH_MODEL.name() + " = "
            + _admissiblePathModel.name() + "\n");

        result.append("]");

        return result.toString();
    }
}
