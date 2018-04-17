/*
 * $Id: NetworkNativeSyntax.java 631 2011-04-27 08:16:04Z bzfraack $
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

import static sndlib.core.io.sndnative.RegexConstants.CLOSE_BRACE;
import static sndlib.core.io.sndnative.RegexConstants.DOUBLE;
import static sndlib.core.io.sndnative.RegexConstants.EMPTY_BRACES;
import static sndlib.core.io.sndnative.RegexConstants.IDENTIFIER;
import static sndlib.core.io.sndnative.RegexConstants.NON_NEGATIVE_DOUBLE;
import static sndlib.core.io.sndnative.RegexConstants.ONE_OR_MORE_SPACES;
import static sndlib.core.io.sndnative.RegexConstants.OPEN_BRACE;
import static sndlib.core.io.sndnative.RegexConstants.POSITIVE_DOUBLE;
import static sndlib.core.io.sndnative.RegexConstants.POSITIVE_INTEGER;
import static sndlib.core.io.sndnative.RegexConstants.ZERO_OR_MORE_SPACES;
import static sndlib.core.io.sndnative.RegexConstants.META_VALUE;

import java.util.regex.Pattern;

/**
 * Defines the regular expression patterns used to parse a network file 
 * written in the native format of SNDlib.<br/>
 * <br/>
 * 
 * This class is considered as an internal helper class of this package.
 * 
 * @see NetworkNativeParser
 * 
 * @author Roman Klaehne
 */
class NetworkNativeSyntax {

    /**
     * The node section keyword.
     */
    public static final String META = "META";
	
    /**
     * The node section keyword.
     */
    public static final String NODES = "NODES";

    /**
     * The demand section keyword.
     */
    public static final String DEMANDS = "DEMANDS";

    /**
     * The link section keyword.
     */
    public static final String LINKS = "LINKS";

    /**
     * The admissible path section keyword.
     */
    public static final String ADMISSIBLE_PATHS = "ADMISSIBLE_PATHS";

    /**
     * Defines the syntax of a line which opens a section.
     * 
     * Capturing groups are:
     * 
     * <ul>
     *  <li>1. group: section begin keyword (NODES, LINKS, ....)</li>
     * </ul>
     */
    public static final String NETWORK_SECTION_BEGIN = ZERO_OR_MORE_SPACES + "("
        + NODES + "|" + LINKS + "|" + META +"|" + DEMANDS + "|" + ADMISSIBLE_PATHS + ")"
        + ONE_OR_MORE_SPACES + OPEN_BRACE + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a line opening a section.
     */
    public static final Pattern NETWORK_SECTION_BEGIN_PATTERN = Pattern.compile(NETWORK_SECTION_BEGIN);

    /**
     * Defines the syntax of a line finishing a section.
     */
    public static final String NETWORK_SECTION_END = ZERO_OR_MORE_SPACES
        + CLOSE_BRACE + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a line finishing a section.
     */
    public static final Pattern NETWORK_SECTION_END_PATTERN = Pattern.compile(NETWORK_SECTION_END);


    /*
     * ****************************************************************
     * NODE SECTION SYNTAX - NODE SECTION SYNTAX - NODE SECTION SYNTAX
     * ****************************************************************
     */
    
    
    /**
     * Defines the syntax of the x-coordinate of a node.
     */
    public static final String NODE_X = DOUBLE;

    /**
     * Defines the syntax of the y-coordinate of a node
     */
    public static final String NODE_Y = DOUBLE;

    /**
     * Defines the syntax of the ID of a node.
     */
    public static final String NODE_ID = IDENTIFIER;

    /**
     * Defines the syntax of a node line.
     * 
     * Capturing groups are:
     * 
     * <ul>
     *  <li>1. group: node id </li>
     *  <li>2. group: x-coordinate of the node (optional)</li>
     *  <li>2. group: y-coordinate of the node (optional)</li>
     * </ul>
     */
    public static final String NODE_LINE = ZERO_OR_MORE_SPACES + "(" + NODE_ID + ")"
        + "(?:" + ONE_OR_MORE_SPACES + OPEN_BRACE + ZERO_OR_MORE_SPACES + "("
        + NODE_X + ")" + ONE_OR_MORE_SPACES + "(" + NODE_Y + ")"
        + ZERO_OR_MORE_SPACES + CLOSE_BRACE + ")?" + "(?:" + ONE_OR_MORE_SPACES
        + EMPTY_BRACES + "){0,2}" + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a node line.
     */
    public static final Pattern NODE_LINE_PATTERN = Pattern.compile(NODE_LINE);

    
    /**
     * A textual description of the syntax of a node line.
     */
    public static final String NODE_DESC_LINE = "<node_id> [(<longitude>, <latitude>)]";
    
    /*
     * *****************************************************************
     * META SECTION SYNTAX - META SECTION SYNTAX - META SECTION SYNTAX
     * *****************************************************************
     * 
     */
    public static final String META_KEY = IDENTIFIER;

    /**
     * Defines the syntax of a meta line.
     * 
     * Capturing groups are:
     * 
     * <ul>
     * <li>1. group: meta key</li>
     * <li>2. group: meta value</li>
     * </ul>
     */
    public static final String META_LINE = ZERO_OR_MORE_SPACES + "(" + META_KEY
        + ")" + ZERO_OR_MORE_SPACES + "=" + ZERO_OR_MORE_SPACES + "(" + META_VALUE
        + ")" + ZERO_OR_MORE_SPACES;


    /**
     * The pre-compiled pattern for a node line.
     */
    public static final Pattern META_LINE_PATTERN = Pattern.compile(META_LINE);
    
    public static final String META_DESC_LINE = "<attribute> <content>";

    /*
     * *****************************************************************
     * LINK SECTION SYNTAX - LINK SECTION SYNTAX - LINK SECTION SYNTAX
     * *****************************************************************
     */
    

    /**
     * Defines the syntax of a link id.
     */
    public static final String LINK_ID = IDENTIFIER;

    /**
     * Defines the syntax of the pre-installed capacity value.
     */
    public static final String PRE_CAPACITY = NON_NEGATIVE_DOUBLE;

    /**
     * Defines the syntax of the pre-installed capacity cost value.
     */
    public static final String PRE_COST = NON_NEGATIVE_DOUBLE;

    /**
     * Defines the syntax of the setup cost value.
     */
    public static final String SETUP_COST = NON_NEGATIVE_DOUBLE;

    /**
     * Defines the syntax of the unit routing cost value.
     */
    public static final String ROUTING_COST = NON_NEGATIVE_DOUBLE;

    /**
     * Defines the syntax of the additional module capacity value.
     */
    public static final String MODULE_CAPACITY = POSITIVE_DOUBLE;

    /**
     * Defines the syntax of the additional module cost value.
     */
    public static final String MODULE_COST = NON_NEGATIVE_DOUBLE;

    /**
     * Defines the syntax of a link line.
     *
     * Capturing groups are:
     * 
     * <ul>
     *  <li>1. group: link id</li>
     *  <li>2. group: first node id</li>
     *  <li>3. group: second node id</li>
     *  <li>4. group: pre-installed capacity</li>
     *  <li>5. group: pre-installed capacity cost</li>
     *  <li>6. group: unit routing cost</li>
     *  <li>7. group: setup cost</li>
     *  <li>8. group: module capacity-cost string</li>
     * </ul>
     */
    public static final String LINK_LINE = ZERO_OR_MORE_SPACES + "(" + LINK_ID + ")"
        + ONE_OR_MORE_SPACES + OPEN_BRACE + ZERO_OR_MORE_SPACES + "(" + NODE_ID
        + ")" + ONE_OR_MORE_SPACES + "(" + NODE_ID + ")" + ZERO_OR_MORE_SPACES
        + CLOSE_BRACE + ONE_OR_MORE_SPACES + "(" + PRE_CAPACITY + ")"
        + ONE_OR_MORE_SPACES + "(" + PRE_COST + ")" + ONE_OR_MORE_SPACES + "("
        + ROUTING_COST + ")" + ONE_OR_MORE_SPACES + "(" + SETUP_COST + ")"
        + ONE_OR_MORE_SPACES + OPEN_BRACE + ZERO_OR_MORE_SPACES + "((?:"
        + MODULE_CAPACITY + ONE_OR_MORE_SPACES + MODULE_COST + ")?(?:"
        + ONE_OR_MORE_SPACES + MODULE_CAPACITY + ONE_OR_MORE_SPACES + MODULE_COST
        + ")*)" + ZERO_OR_MORE_SPACES + CLOSE_BRACE + "(?:" + ONE_OR_MORE_SPACES
        + EMPTY_BRACES + ")?" + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a link line.
     */
    public static final Pattern LINK_LINE_PATTERN = Pattern.compile(LINK_LINE);

    /**
     * A textual description of the syntax of a link line.
     */
    public static final String LINK_DESC_LINE = "<link_id> ( <source> <target> ) <pre_installed_capacity> <pre_installed_capacity_cost> <routing_cost> <setup_cost> ( {<module_capacity> <module_cost>}* )";
    
    
    /*
     * **********************************************************************
     * DEMAND SECTION SYNTAX - DEMAND SECTION SYNTAX - DEMAND SECTION SYNTAX
     * **********************************************************************
     */
    

    /**
     * Defines the syntax of a demand id.
     */
    public static final String DEMAND_ID = IDENTIFIER;

    /**
     * Defines the syntax of the demand value.
     */
    public static final String DEMAND_VALUE = POSITIVE_DOUBLE;

    /**
     * Defines the syntax of the max path length value.
     */
    public static final String MAX_PATH_LENGTH = POSITIVE_INTEGER;

    /**
     * Defines the syntax of the routing unit value.
     */
    public static final String ROUTING_UNIT = POSITIVE_INTEGER;

    /**
     * Defines a constant for the unlimited path length
     */
    public static final String PATH_UNLIMITED = "UNLIMITED";

    
    /**
     * Defines the syntax of a demand line.
     * 
     * Capturing groups are:
     * 
     * <ul>
     *  <li> 1. group: demand id</li>
     *  <li> 2. group: first node id</li>
     *  <li> 3. group: second node id</li>
     *  <li> 4. group: routing unit</li>
     *  <li> 5. group: demand value</li>
     *  <li> 6. group: max path length (an integer or 'UNLIMITED')</li>
     * </ul>
     */
    public static final String DEMAND_LINE = ZERO_OR_MORE_SPACES + "(" + DEMAND_ID
        + ")" + ONE_OR_MORE_SPACES + OPEN_BRACE + ZERO_OR_MORE_SPACES + "("
        + NODE_ID + ")" + ONE_OR_MORE_SPACES + "(" + NODE_ID + ")"
        + ZERO_OR_MORE_SPACES + CLOSE_BRACE + ONE_OR_MORE_SPACES + "("
        + ROUTING_UNIT + ")" + ONE_OR_MORE_SPACES + "(" + DEMAND_VALUE + ")"
        + ONE_OR_MORE_SPACES + "(" + MAX_PATH_LENGTH + "|" + PATH_UNLIMITED + ")"
        + "(?:" + ONE_OR_MORE_SPACES + EMPTY_BRACES + ")?" + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a demand line.
     */
    public static final Pattern DEMAND_LINE_PATTERN = Pattern.compile(DEMAND_LINE);

    /**
     * A textual description of the syntax of a demand line.
     */
    public static final String DEMAND_DESC_LINE = "<demand_id> ( <source> <target> ) <routing_unit> <demand_value> <max_path_length>";
    
        
    /*
     * ****************************************************************
     * ADMISSIBLE PATH SECTION SYNTAX - ADMISSIBLE PATH SECTION SYNTAX
     * ****************************************************************
     */
    

    /**
     * Defines the syntax of the line which opens the admissible path section
     * of a demand.
     * 
     * Capturing groups are:
     * 
     * <ul>
     *  <li> 1. group: demand id</li>
     * </ul>
     */
    public static final String DEMAND_PATH_BEGIN = ZERO_OR_MORE_SPACES + "("
        + DEMAND_ID + ")" + ONE_OR_MORE_SPACES + OPEN_BRACE + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for begin line of admissible-path-per-demand-section.
     */
    public static final Pattern DEMAND_PATH_BEGIN_PATTERN = Pattern.compile(DEMAND_PATH_BEGIN);

    /**
     * Defines the syntax of a path id.
     */
    public static final String PATH_ID = IDENTIFIER;

    /**
     * Defines the syntax of a path line.
     * 
     * Capturing groups are:
     * 
     * <ul>
     *  <li> 1. group: path id</li>
     *  <li> 2. group: string of link ids</li>
     * </ul>
     */
    public static final String PATH_LINE = ZERO_OR_MORE_SPACES + "(" + PATH_ID + ")"
        + ONE_OR_MORE_SPACES + OPEN_BRACE + ZERO_OR_MORE_SPACES + "((?:" + LINK_ID
        + ONE_OR_MORE_SPACES + ")*(?:" + LINK_ID + "))" + ZERO_OR_MORE_SPACES
        + CLOSE_BRACE + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a path line.
     */
    public static final Pattern PATH_LINE_PATTERN = Pattern.compile(PATH_LINE);

    /**
     * A textual description of the syntax of a path line.
     */
    public static final String PATHS_DESC_LINE = "<demand_id> ( {<path_id> ( <link_id>+ )}+ )"; 
}

