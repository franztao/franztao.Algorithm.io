/*
 * $Id: ModelData.java 99 2006-07-04 14:05:16Z bzforlow $
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

/**
 * A <tt>ModelData</tt> object is a mutable version of a {@link Model} and
 * is used to construct a <tt>Model</tt> instance.  
 * 
 * @see Model
 * 
 * @author Roman Klaehne
 */
public class ModelData {

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
     * Constructs a new empty <tt>ModelData</tt> object. All submodels 
     * will be initialized with <tt>null</tt>.
     */
    public ModelData() {

    }

    /**
     * Constructs a new <tt>ModelData</tt> object from the given <tt>Model</tt>.
     * All submodels will be initialized with the values in the given model.
     * 
     * @param model the model to initialize the submodels from
     */
    public ModelData(Model model) {

        _linkModel = model.getLinkModel();
        _nodeModel = model.getNodeModel();
        _demandModel = model.getDemandModel();
        _linkCapacityModel = model.getLinkCapacityModel();
        _objectiveModel = model.getObjectiveModel();
        _admissiblePathModel = model.getAdmissiblePathModel();
        _fixedChargeModel = model.getFixedChargeModel();
        _hopLimitModel = model.getHopLimitModel();
        _routingModel = model.getRoutingModel();
        _survivabilityModel = model.getSurvivabilityModel();
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
     * Returns the survivability submodel.
     * 
     * @return the survivability submodel
     */
    public SurvivabilityModel getSurvivabilityModel() {

        return _survivabilityModel;
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
     * Sets the admissible path submodel.
     * 
     * @param admissiblePathModel the admissible path submodel
     */
    public void setAdmissiblePathModel(AdmissiblePathModel admissiblePathModel) {

        _admissiblePathModel = admissiblePathModel;
    }

    /**
     * Sets the demand submodel.
     * 
     * @param demandModel the demand submodel
     */
    public void setDemandModel(DemandModel demandModel) {

        _demandModel = demandModel;
    }

    /**
     * Sets the survivability submodel.
     * 
     * @param survivabilityModel the demand submodel
     */
    public void setSurvivabilityModel(SurvivabilityModel survivabilityModel) {

        _survivabilityModel = survivabilityModel;
    }

    /**
     * Sets the fixed-charge submodel.
     * 
     * @param fixedChargeModel the fixed-charge submodel
     */
    public void setFixedChargeModel(FixedChargeModel fixedChargeModel) {

        _fixedChargeModel = fixedChargeModel;
    }

    /**
     * Sets the hop-limit submodel.
     * 
     * @param hopLimitModel the hop-limit submodel
     */
    public void setHopLimitModel(HopLimitModel hopLimitModel) {

        _hopLimitModel = hopLimitModel;
    }

    /**
     * Sets the link capacity submodel.
     * 
     * @param linkCapacityModel the link capacity submodel
     */
    public void setLinkCapacityModel(LinkCapacityModel linkCapacityModel) {

        _linkCapacityModel = linkCapacityModel;
    }

    /**
     * Sets the link submodel.
     * 
     * @param linkModel the link submodel
     */
    public void setLinkModel(LinkModel linkModel) {

        _linkModel = linkModel;
    }

    /**
     * Sets the node submodel.
     * 
     * @param nodeModel the node submodel
     */
    public void setNodeModel(NodeModel nodeModel) {

        _nodeModel = nodeModel;
    }

    /**
     * Sets the objective submodel.
     * 
     * @param objectiveModel the objective submodel
     */
    public void setObjectiveModel(ObjectiveModel objectiveModel) {

        _objectiveModel = objectiveModel;
    }

    /**
     * Sets the routing submodel.
     * 
     * @param routingModel the routing submodel
     */
    public void setRoutingModel(RoutingModel routingModel) {

        _routingModel = routingModel;
    }

    /**
     * Returns the submodel corresonding to the given model property. 
     * 
     * @param modelProperty the model property
     * 
     * @return the submodel corresponding to the given model property
     */
    public Enum getModel(ModelProperty modelProperty) {

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
     * Sets the submodel corresponding to the given model property.
     * 
     * @param modelProperty the model property
     * @param submodel the submodel
     * 
     * @throws IllegalArgumentException if the given submodel is 
     * illegal or unknown for the given model property
     */
    public void setModel(ModelProperty modelProperty, String submodel) {

        switch (modelProperty) {

        case ADMISSIBLE_PATH_MODEL:
            _admissiblePathModel = AdmissiblePathModel.valueOfIgnoreCase(submodel);
            break;

        case ROUTING_MODEL:
            _routingModel = RoutingModel.valueOfIgnoreCase(submodel);
            break;

        case SURVIVABILITY_MODEL:
            _survivabilityModel = SurvivabilityModel.valueOfIgnoreCase(submodel);
            break;

        case LINK_CAPACITY_MODEL:
            _linkCapacityModel = LinkCapacityModel.valueOfIgnoreCase(submodel);
            break;

        case LINK_MODEL:
            _linkModel = LinkModel.valueOfIgnoreCase(submodel);
            break;

        case NODE_MODEL:
            _nodeModel = NodeModel.valueOfIgnoreCase(submodel);
            break;

        case HOP_LIMIT_MODEL:
            _hopLimitModel = HopLimitModel.valueOfIgnoreCase(submodel);
            break;

        case FIXED_CHARGE_MODEL:
            _fixedChargeModel = FixedChargeModel.valueOfIgnoreCase(submodel);
            break;

        case DEMAND_MODEL:
            _demandModel = DemandModel.valueOfIgnoreCase(submodel);
            break;

        case OBJECTIVE_MODEL:
            _objectiveModel = ObjectiveModel.valueOfIgnoreCase(submodel);
            break;

        default:
            throw new AssertionError("unknown model property: " + modelProperty);
        }
    }

    /**
     * Sets the submodel corresponding to the given model property.
     * 
     * @param modelProperty the model property
     * @param submodel the submodel
     * 
     * @throws ClassCastException if the given submodel is not an instance of
     * the class which corresponds to the given model property
     */
    public void setModel(ModelProperty modelProperty, Enum submodel) {

        switch (modelProperty) {

        case ADMISSIBLE_PATH_MODEL:
            _admissiblePathModel = (AdmissiblePathModel) submodel;
            break;

        case ROUTING_MODEL:
            _routingModel = (RoutingModel) submodel;
            break;

        case SURVIVABILITY_MODEL:
            _survivabilityModel = (SurvivabilityModel) submodel;
            break;

        case LINK_CAPACITY_MODEL:
            _linkCapacityModel = (LinkCapacityModel) submodel;
            break;

        case LINK_MODEL:
            _linkModel = (LinkModel) submodel;
            break;

        case NODE_MODEL:
            _nodeModel = (NodeModel) submodel;
            break;

        case HOP_LIMIT_MODEL:
            _hopLimitModel = (HopLimitModel) submodel;
            break;

        case FIXED_CHARGE_MODEL:
            _fixedChargeModel = (FixedChargeModel) submodel;
            break;

        case DEMAND_MODEL:
            _demandModel = (DemandModel) submodel;
            break;

        case OBJECTIVE_MODEL:
            _objectiveModel = (ObjectiveModel) submodel;
            break;

        default:
            throw new AssertionError("unknown model property: " + modelProperty);
        }
    }
}

