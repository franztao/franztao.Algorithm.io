/*
 * $Id: ModelProperty.java 99 2006-07-04 14:05:16Z bzforlow $
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

import java.util.EnumSet;

/**
 * This enum is used to identify the submodels constituting a network 
 * {@link Model}.<br/><br/>
 * Each of the submodel identifiers corresponds to a enumeration class 
 * declaring the submodel values.
 * 
 * @author Roman Klaehne
 */
public enum ModelProperty {

    /**
     * Identifies the node submodel. It corresponds to {@link NodeModel}.
     */
    NODE_MODEL('N') {

        public boolean hasValueIgnoreCase(String value) {

            return NodeModel.hasEnumIgnoreCase(value);
        }

        public Enum getValueByShortCut(char shortCut) {

            return NodeModel.valueOfShortCut(shortCut);
        }

        public <E extends Enum<?>> EnumSet<?> getValues() {

            return EnumSet.allOf(NodeModel.class);
        }

        public String getValueShortCut(Enum value) {

            if(!(value instanceof NodeModel)) {
                return null;
            }
            return String.valueOf(((NodeModel) value).getShortCut());
        }
    },

    /**
     * Identifies the link submodel.  It corresponds to {@link LinkModel}.
     */
    LINK_MODEL('L') {

        public boolean hasValueIgnoreCase(String value) {

            return LinkModel.hasEnumIgnoreCase(value);
        }

        public Enum getValueByShortCut(char shortCut) {

            return LinkModel.valueOfShortCut(shortCut);
        }

        public <E extends Enum<?>> EnumSet<?> getValues() {

            return EnumSet.allOf(LinkModel.class);
        }

        public String getValueShortCut(Enum value) {

            if(!(value instanceof LinkModel)) {
                return null;
            }
            return String.valueOf(((LinkModel) value).getShortCut());
        }
    },

    /**
     * Identifies the demand submodel. It corresponds to {@link DemandModel}.
     */
    DEMAND_MODEL('D') {

        public boolean hasValueIgnoreCase(String value) {

            return DemandModel.hasEnumIgnoreCase(value);
        }

        public Enum getValueByShortCut(char shortCut) {

            return DemandModel.valueOfShortCut(shortCut);
        }

        public <E extends Enum<?>> EnumSet<?> getValues() {

            return EnumSet.allOf(DemandModel.class);
        }

        public String getValueShortCut(Enum value) {

            if(!(value instanceof DemandModel)) {
                return null;
            }
            return String.valueOf(((DemandModel) value).getShortCut());
        }
    },

    /**
     * Identifies the survivability submodel. It corresponds to 
     * {@link SurvivabilityModel}.
     */
    SURVIVABILITY_MODEL('S') {

        public boolean hasValueIgnoreCase(String value) {

            return SurvivabilityModel.hasEnumIgnoreCase(value);
        }

        public Enum getValueByShortCut(char shortCut) {

            return SurvivabilityModel.valueOfShortCut(shortCut);
        }

        public <E extends Enum<?>> EnumSet<?> getValues() {

            return EnumSet.allOf(SurvivabilityModel.class);
        }

        public String getValueShortCut(Enum value) {

            if(!(value instanceof SurvivabilityModel)) {
                return null;
            }
            return String.valueOf(((SurvivabilityModel) value).getShortCut());
        }
    },
    /**
     * Identifies the link capacity submodel. It corresponds to 
     * {@link LinkCapacityModel}.
     */
    LINK_CAPACITY_MODEL('C') {

        public boolean hasValueIgnoreCase(String value) {

            return LinkCapacityModel.hasEnumIgnoreCase(value);
        }

        public Enum getValueByShortCut(char shortCut) {

            return LinkCapacityModel.valueOfShortCut(shortCut);
        }

        public <E extends Enum<?>> EnumSet<?> getValues() {

            return EnumSet.allOf(LinkCapacityModel.class);
        }

        public String getValueShortCut(Enum value) {

            if(!(value instanceof LinkCapacityModel)) {
                return null;
            }
            return String.valueOf(((LinkCapacityModel) value).getShortCut());
        }
    },

    /**
     * Identifies the hop-limit submodel. It corresponds to 
     * {@link HopLimitModel}.
     */
    HOP_LIMIT_MODEL('H') {

        public boolean hasValueIgnoreCase(String value) {

            return HopLimitModel.hasEnumIgnoreCase(value);
        }

        public Enum getValueByShortCut(char shortCut) {

            return HopLimitModel.valueOfShortCut(shortCut);
        }

        public <E extends Enum<?>> EnumSet<?> getValues() {

            return EnumSet.allOf(HopLimitModel.class);
        }

        public String getValueShortCut(Enum value) {

            if(!(value instanceof HopLimitModel)) {
                return null;
            }
            return String.valueOf(((HopLimitModel) value).getShortCut());
        }
    },

    /**
     * Identifies the fixed-charge submodel. It corresponds to 
     * {@link FixedChargeModel}.
     */
    FIXED_CHARGE_MODEL('F') {

        public boolean hasValueIgnoreCase(String value) {

            return FixedChargeModel.hasEnumIgnoreCase(value);
        }

        public Enum getValueByShortCut(char shortCut) {

            return FixedChargeModel.valueOfShortCut(shortCut);
        }

        public <E extends Enum<?>> EnumSet<?> getValues() {

            return EnumSet.allOf(FixedChargeModel.class);
        }

        public String getValueShortCut(Enum value) {

            if(!(value instanceof FixedChargeModel)) {
                return null;
            }
            return String.valueOf(((FixedChargeModel) value).getShortCut());
        }
    },

    /**
     * Identifies the objective submodel. It corresponds to 
     * {@link ObjectiveModel}.
     */
    OBJECTIVE_MODEL('O') {

        public boolean hasValueIgnoreCase(String value) {

            return ObjectiveModel.hasEnumIgnoreCase(value);
        }

        public Enum getValueByShortCut(char shortCut) {

            return ObjectiveModel.valueOfShortCut(shortCut);
        }

        public <E extends Enum<?>> EnumSet<?> getValues() {

            return EnumSet.allOf(ObjectiveModel.class);
        }

        public String getValueShortCut(Enum value) {

            if(!(value instanceof ObjectiveModel)) {
                return null;
            }
            return String.valueOf(((ObjectiveModel) value).getShortCut());
        }
    },

    /**
     * Identifies the routing submodel. It corresponds to 
     * {@link RoutingModel}.
     */
    ROUTING_MODEL('R') {

        public boolean hasValueIgnoreCase(String value) {

            return RoutingModel.hasEnumIgnoreCase(value);
        }

        public Enum getValueByShortCut(char shortCut) {

            return RoutingModel.valueOfShortCut(shortCut);
        }

        public <E extends Enum<?>> EnumSet<?> getValues() {

            return EnumSet.allOf(RoutingModel.class);
        }

        public String getValueShortCut(Enum value) {

            if(!(value instanceof RoutingModel)) {
                return null;
            }
            return String.valueOf(((RoutingModel) value).getShortCut());
        }
    },

    /**
     * Identifies the admissible path submodel. It corresponds to 
     * {@link AdmissiblePathModel}.
     */
    ADMISSIBLE_PATH_MODEL('P') {

        public boolean hasValueIgnoreCase(String value) {

            return AdmissiblePathModel.hasEnumIgnoreCase(value);
        }

        public Enum getValueByShortCut(char shortCut) {

            return AdmissiblePathModel.valueOfShortCut(shortCut);
        }

        public <E extends Enum<?>> EnumSet<?> getValues() {

            return EnumSet.allOf(AdmissiblePathModel.class);
        }

        public String getValueShortCut(Enum value) {

            if(!(value instanceof AdmissiblePathModel)) {
                return null;
            }
            return String.valueOf(((AdmissiblePathModel) value).getShortCut());
        }
    };

    /**
     * The model property's shortcut.
     */
    private char _shortCut;

    /**
     * Constructs a new model property constant and assigns the given 
     * shortcut to it.
     * 
     * @param shortCut the shortcut for the model property
     */
    private ModelProperty(char shortCut) {

        _shortCut = shortCut;
    }

    /**
     * Tests whether this model property provides the specified submodel.  
     * 
     * @param submodel the submodel
     * 
     * @return <tt>true</tt> if and only if this model property provides
     * the given submodel; <tt>false</tt> otherwise
     */
    abstract public boolean hasValueIgnoreCase(String submodel);

    /**
     * Returns all submodels provided by this model property.
     * 
     * @return all submodels provided by this model property
     */
    abstract public <E extends Enum<?>> EnumSet<?> getValues();

    /**
     * Returns the submodel with the specified shortcut or <tt>null</tt> if 
     * no such submodel is provided by this model property.
     * 
     * @param shortCut the shortcut of the submodel
     * 
     * @return the submodel for the given shortcut; <tt>null</tt> if such a
     * submodel does not exist
     */
    abstract public Enum getValueByShortCut(char shortCut);

    /**
     * Returns the shortcut of the given submodel. If the given enum 
     * constant is not an instance of the class corresponding to this model
     * property <tt>null</tt> is returned.
     * 
     * @param submodel the submodel to return the shortcut for
     * 
     * @return the shortcut of the given submodel; <tt>null</tt> if the 
     * given enum constant is not an instance of the class corresponding 
     * to this model property
     */
    abstract public String getValueShortCut(Enum submodel);

    /**
     * Returns <tt>true</tt> if the given submodel is provided by this model
     * property.
     * 
     * @param submodel the submodel
     * 
     * @return <tt>true</tt> if and only if the given submodel is provided 
     * by this model property; <tt>false</tt> otherwise
     */
    public boolean containsValue(Enum submodel) {

        return getValues().contains(submodel);
    }

    /**
     * Returns the shortcut of this model property.
     * 
     * @return the shortcut of this model property
     */
    public String getShortCut() {

        return String.valueOf(_shortCut);
    }

    /**
     * Tests whether there is a model property with the specified name, 
     * ignoring case considerations. 
     * 
     * @param name the name of the model property to look for
     * 
     * @return <tt>true</tt> if and only if there is model property with
     * the given name; <tt>false</tt> otherwise
     */
    public static boolean hasEnumIgnoreCase(String name) {

        try {
            valueOfIgnoreCase(name);
        }
        catch (IllegalArgumentException iax) {
            return false;
        }
        return true;
    }

    /**
     * Returns the model property with the specified name, ignoring case 
     * considerations.
     * 
     * @param name the name of the model property to look for
     * 
     * @return the model property with the specified name; <tt>null</tt> if 
     * there is no such model property
     */
    public static ModelProperty valueOfIgnoreCase(String name) {

        return ModelProperty.valueOf(name.toUpperCase());
    }

    /**
     * Returns the model property with the specified shortcut.
     * 
     * @param shortCut the shortcut of the model property
     * 
     * @return the model property with the specified shortcut; <tt>null</tt> if
     * there is no such model property
     */
    public static ModelProperty valueOfShortCut(char shortCut) {

        for(ModelProperty prop : values()) {
            if(prop._shortCut == shortCut) {
                return prop;
            }
        }
        return null;
    }
}

