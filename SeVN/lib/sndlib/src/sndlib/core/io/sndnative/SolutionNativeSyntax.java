/*
 * $Id: SolutionNativeSyntax.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

package sndlib.core.io.sndnative;

import static sndlib.core.io.sndnative.NetworkNativeSyntax.DEMAND_ID;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.LINK_ID;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.MODULE_CAPACITY;
import static sndlib.core.io.sndnative.RegexConstants.CLOSE_BRACE;
import static sndlib.core.io.sndnative.RegexConstants.DOUBLE;
import static sndlib.core.io.sndnative.RegexConstants.IDENTIFIER;
import static sndlib.core.io.sndnative.RegexConstants.ONE_OR_MORE_SPACES;
import static sndlib.core.io.sndnative.RegexConstants.OPEN_BRACE;
import static sndlib.core.io.sndnative.RegexConstants.ZERO_OR_MORE_SPACES;

import java.util.regex.Pattern;

/**
 * Defines the regular expression patterns used to parse a solution file 
 * written in the native format of SNDlib.<br/>
 * <br/>
 * 
 * This class is considered as an internal helper class of this package.
 * 
 * @see SolutionNativeParser
 * 
 * @author Roman Klaehne
 */
class SolutionNativeSyntax {

    /**
     * The keyword of the link configuration section.
     */
    public static final String LINK_CONFIGURATIONS = "LINK-CONFIGURATIONS";

    /**
     * The routing section keyword.
     */
    public static final String ROUTINGS = "ROUTINGS";

    /**
     * The state in which a routing section is valid.
     */
    public static final String ROUTINGS_IN_STATE = IDENTIFIER;

    /**
     * Defines the syntax of a line which opens a section.
     * 
     * Capturing groups are:
     * 
     * <ul>
     *  <li>1. group: section begin keyword (LINK-CONFIGURATIONS, ROUTINGS, ....)</li>
     *  <li>2. group: operating state (NOS, DEFAULT_BACKUP,....)</li>
     * </ul>
     */
    public static final String SOLUTION_SECTION_BEGIN = ZERO_OR_MORE_SPACES + "(?=("
        + LINK_CONFIGURATIONS + "|" + ROUTINGS + "))(?:" + LINK_CONFIGURATIONS + "|"
        + ROUTINGS + "(?:" + ONE_OR_MORE_SPACES + "(" + ROUTINGS_IN_STATE + "))?)"
        + ONE_OR_MORE_SPACES + OPEN_BRACE + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a line opening a section.
     */
    public static final Pattern SOLUTION_SECTION_BEGIN_PATTERN = Pattern.compile(SOLUTION_SECTION_BEGIN);

    /**
     * Defines the syntax of a line closing a section.
     */
    public static final String SOLUTION_SECTION_END = ZERO_OR_MORE_SPACES
        + CLOSE_BRACE + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a line finishing a section.
     */
    public static final Pattern SOLUTION_SECTION_END_PATTERN = Pattern.compile(SOLUTION_SECTION_END);

    /*
     * ************************************************************************
     * LINK CONFIGURATION SECTION SYNTAX - LINK CONFIGURATION SECTION SYNTAX
     * ************************************************************************
     */

    /**
     * Defines the syntax of the install count value.
     */
    public static final String INSTALL_COUNT = DOUBLE;

    /**
     * Defines the syntax of a line in the link configuration section.
     * 
     * Capturing groups are
     * 
     * <ul>
     * <li>1. group: link id</li>
     * <li>2. group: module capacity-install count string </li>
     * </ul>
     */
    public static final String LINK_CONF_LINE = ZERO_OR_MORE_SPACES
        + ZERO_OR_MORE_SPACES + "(" + LINK_ID + ")" + ZERO_OR_MORE_SPACES
        + ONE_OR_MORE_SPACES + OPEN_BRACE + ZERO_OR_MORE_SPACES + "((?:"
        + MODULE_CAPACITY + ONE_OR_MORE_SPACES + INSTALL_COUNT + ")?(?:"
        + ONE_OR_MORE_SPACES + MODULE_CAPACITY + ONE_OR_MORE_SPACES + INSTALL_COUNT
        + ")*)" + ZERO_OR_MORE_SPACES + CLOSE_BRACE + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a line in the link configuration section.
     */
    public static final Pattern LINK_CONF_LINE_PATTERN = Pattern.compile(LINK_CONF_LINE);

    /**
     * A textual description of a link configuration line.
     */
    public static final String LINK_CONF_DESC_LINE = "<link_id> ( {<module_capacity> <install_count>}+ )";

    /*
     * ***************************************************************************
     * ROUTING SECTION SYNTAX - ROUTING SECTION SYNTAX - ROUTING SECTION SYNTAX
     * ***************************************************************************
     */

    /**
     * Defines the syntax of a line which opens the routing section of a 
     * single demand.
     * 
     * Capturing groups are:
     * 
     * <ul>
     * <li> 1. group: demand id</li>
     * </ul>
     */
    public static final String DEMAND_ROUTING_BEGIN = ZERO_OR_MORE_SPACES + "("
        + DEMAND_ID + ")" + ONE_OR_MORE_SPACES + OPEN_BRACE + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a line which opens the routing section 
     * for a single demand.
     */
    public static final Pattern DEMAND_ROUTING_BEGIN_PATTERN = Pattern.compile(DEMAND_ROUTING_BEGIN);

    /**
     * Defines the syntax of the flow path value.
     */
    public static final String FLOW_PATH_VALUE = DOUBLE;

    /**
     * Defines the syntax of a line in the routing section of a single demand.
     * 
     * Capturing groups are:
     * 
     * <ul>
     * <li>1. group: flow path value</li>
     * <li>2. group: string containing the link ID's</li>
     * </ul>
     */
    public static final String DEMAND_ROUTING_LINE = ZERO_OR_MORE_SPACES + "("
        + FLOW_PATH_VALUE + ")" + ONE_OR_MORE_SPACES + OPEN_BRACE
        + ZERO_OR_MORE_SPACES + "((?:" + LINK_ID + ONE_OR_MORE_SPACES + ")*(?:"
        + LINK_ID + "))" + ZERO_OR_MORE_SPACES + CLOSE_BRACE + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a line in the routing section of a single 
     * demand.
     */
    public static final Pattern DEMAND_ROUTING_LINE_PATTERN = Pattern.compile(DEMAND_ROUTING_LINE);

    /**
     * A textual desription of a demand routing line.
     */
    public static final String DEMAND_ROUTINGS_DESC_LINE = "<demand_id> ( {<flow_path_value> ( <link_id>+ )}+ )";
}

