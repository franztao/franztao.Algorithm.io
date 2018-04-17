/*
 * $Id: OperatingState.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import com.atesio.utils.ArgChecker;

/**
 * This class represents an operating state in a survivable network design
 * instance. Besides the normal operating state (NOS), in which all
 * links are properly working, only single link failures are considered
 * in SNDlib.
 * <br/><br/>
 *  
 * An operating state can be instantiated using on of the static factory 
 * methods of this class.
 * 
 * @author Roman Klaehne
 */
public class OperatingState {

    /**
     * Lists the admissible types of an operating state.
     */
    public static enum Type {

        /**
         * The type of the normal operating state, in which all links work 
         * properly.  
         */
        NOS,

        /**
         * The type of the default backup routing. For a detailed description
         * of the state corresponding to this type see 
         * {@link OperatingState#DEFAULT_BACKUP}.
         * 
         * @see OperatingState#DEFAULT_BACKUP
         */
        DEFAULT_BACKUP,

        /**
         * The type of a single link failure operating state.
         */
        SINGLE_LINK_FAILURE;
    }

    /**
     * Constant specifying the normal operationg state, in which all links
     * work properly.
     */
    public static OperatingState NOS = new OperatingState("NOS", Type.NOS);

    /**
     * This is  a special state used to simplify the definition of 
     * state-dependent routings especially in the case of 1+1 dedicated path 
     * protection.<br/>
     * <br/>
     *  
     * This state means: if any link in the NOS-routing of a demand fails and
     * no corresponding backup routing is specified, then the backup routing 
     * defined under this state is assumed to be the default.<br/>
     * For 1+1 protection this state is used to define the demand's backup 
     * paths.<br/>
     * <br/>
     * As an example imagine you have the following routing in the normal 
     * operating state (NOS):<br/>
     * <pre>
     *   ROUTINGS NOS (
     *     Demand1 (Link1 Link2)
     *     Demand2 (Link3)
     *   ) 
     * </pre>
     * For every single link failure you have to specify the backup routing 
     * which leads to three routing sections:<br/>
     * <pre>
     *   ROUTINGS Link1 (
     *     Demand1 (Link4 Link5)
     *   )
     *   ROUTINGS Link2 (
     *     Demand1 (Link4 Link5)
     *   )
     *   ROUTINGS Link3 (
     *     Demand2 (Link6)
     *   )
     * </pre>
     * Using the default backup routing state you may abbreviate the 
     * definitions of the backup routings:<br/>
     * <pre>
     *   ROUTINGS DEFAULT_BACKUP (
     *     Demand1 (Link4 Link5)
     *     Demand2 (Link6)
     *   )
     * </pre>.
     */
    public static OperatingState DEFAULT_BACKUP = new OperatingState(
        "DEFAULT_BACKUP", Type.DEFAULT_BACKUP);

    /**
     * Constructs an operating state representing the failure of the link with
     * the specified ID.
     * 
     * @param linkId the ID of a link
     * 
     * @return an operating state representing the failure of the link with
     * the specified ID
     * 
     * @throws IllegalArgumentException if the given link ID equals the name 
     * of an predefined operating state (e.g. {@link OperatingState#NOS}) 
     */
    public static OperatingState singleLinkFailureState(String linkId) {

        ArgChecker.checkNotNull(linkId, "link ID");

        if(linkId.equalsIgnoreCase(NOS.getName())
            || linkId.equalsIgnoreCase(DEFAULT_BACKUP.getName())) {
            throw new IllegalArgumentException("could not use predefined state "
                + linkId + " as a name of a single link failure state");
        }
        return new OperatingState(linkId, Type.SINGLE_LINK_FAILURE);
    }

    /**
     * Returns an appropriate operating state instance corresponding to the
     * given name.<br/>
     * If the specified name equals one of the predefined states 
     * (e.g. {@link OperatingState#NOS}) the correspondig state is returned.
     * 
     * @param stateName the name of the state
     * 
     * @return a predefined state instance or a new constructed operating
     * state representing a single link failure.
     */
    public static OperatingState getInstance(String stateName) {

        ArgChecker.checkNotNull(stateName, "state name");

        if(stateName.equalsIgnoreCase(NOS.getName())) {
            return NOS;
        }
        if(stateName.equalsIgnoreCase(DEFAULT_BACKUP.getName())) {
            return DEFAULT_BACKUP;
        }

        return singleLinkFailureState(stateName);
    }

    /**
     * The name of this operating state.
     */
    private String _name;

    /**
     * The type of this operating state.
     */
    private Type _type;

    /**
     * Constructs a new operating state with the specified name and type.
     * 
     * @param name the of the operating state
     * @param type the type of the operating state
     */
    private OperatingState(String name, Type type) {

        _name = name;
        _type = type;
    }

    /**
     * Returns the name of this operating state.
     * 
     * @return the name of this operating state.
     */
    public String getName() {

        return _name;
    }

    /**
     * Returns the type of this operating state.
     * 
     * @return the type of this operating state.
     */
    public Type getType() {

        return _type;
    }

    /**
     * Returns <tt>true</tt> if the given object is an instance of this 
     * class and its name is equal to the name of this operating state.<br/>
     * Returns <tt>false</tt> otherwise.
     * 
     * @return <tt>true</tt> if the name of the given operating state is equal
     * to the name of this operating state; <tt>false</tt> otherwise
     */
    public boolean equals(Object o) {

        if(o == null || !(o instanceof OperatingState)) {
            return false;
        }

        return ((OperatingState) o).getName().equals(_name);
    }

    /**
     * Returns the hashcode of the name of this operating state.
     * 
     * @return the hashcode of the name of this operating state
     */
    public int hashCode() {

        return _name.hashCode();
    }

    /**
     * Returns the name of this operating state.<br/><br/>
     * 
     * This method is equivalent to <tt>getName()</tt>.
     * 
     * @return the name of this operating state
     */
    public String toString() {
        
        return getName();
    }
}
