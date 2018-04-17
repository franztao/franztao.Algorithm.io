/*
 * $Id: RegexConstants.java 631 2011-04-27 08:16:04Z bzfraack $
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


/**
 * This class lists some regular expressions commonly used in this package.
 * <br/><br/>
 *
 * This class is considered as an internal helper class of this package
 *
 * @see NetworkNativeSyntax
 * @see SolutionNativeSyntax
 *
 * @author Roman Klaehne
 */
class RegexConstants {


    /**
     *  Defines a random String
     */
    public static final String META_VALUE = ".+";

    /**
     * Defines the syntax of an identifier.
     */
    public static final String IDENTIFIER = "[a-zA-Z0-9][a-zA-Z0-9\\-_\\.]*";

    /**
     * Defines the syntax of a double value.
     */
    public static final String DOUBLE = "[\\+-]?\\d+(?:\\.\\d+)?";

    /**
     * Defines the syntax of a non-negative double value.
     */
    public static final String NON_NEGATIVE_DOUBLE = "\\+?\\d+(?:\\.\\d+)?";

    /**
     * Defines the syntax of a positive double value.
     */
    public static final String POSITIVE_DOUBLE = "(?:\\+?\\d*[1-9]\\d*(?:\\.\\d+)?|\\+?\\d+\\.\\d*[1-9]\\d*)";

    /**
     * Defines the syntax of an integer value.
     */
    public static final String INTEGER = "[\\+-]?\\d+";

    /**
     * Defines the syntax of a positive integer value.
     */
    public static final String POSITIVE_INTEGER = "\\+?[1-9]\\d*";

    /**
     * Defines the syntax of a non-negative integer value.
     */
    public static final String NON_NEGATIVE_INTEGER = "\\+?\\d+";

    /**
     * Defines the regular expression for strings only consisting of zero or
     * more spaces.
     */
    public static final String ZERO_OR_MORE_SPACES = "\\s*";

    /**
     * Defines the regular expression for strings only consisting of one or
     * more spaces.
     */
    public static final String ONE_OR_MORE_SPACES = "\\s+";

    /**
     * Defines the regular expression for an opening brace.
     */
    public static final String OPEN_BRACE = "\\(";

    /**
     * Defines the regular expression for a closing brace.
     */
    public static final String CLOSE_BRACE = "\\)";

    /**
     * Defines the regular expression for an empty brace pair.
     */
    public static final String EMPTY_BRACES = OPEN_BRACE + ZERO_OR_MORE_SPACES
        + CLOSE_BRACE;

    /**
     * Defines the line comment begin string.
     */
    public static final String LINE_COMMENT_SIGN = "#";
}

