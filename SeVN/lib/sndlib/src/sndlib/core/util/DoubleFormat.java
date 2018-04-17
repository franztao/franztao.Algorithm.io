/*
 * $Id: DoubleFormat.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.Locale;

import com.atesio.utils.ArgChecker;

/**
 * This class encapsulates the common format for double numbers in SNDlib.
 *  
 * @author Roman Klaehne
 */
public class DoubleFormat {

    /**
     * The format for a short rounded double representation with exactly two
     * fraction digits.
     */
    private static final DecimalFormat SHORT_DOUBLE_FORMAT;

    /**
     * The format for a short rounded double representation with a maximum
     * of two fraction digits.
     */
    private static final DecimalFormat SHORT_DOUBLE_FORMAT_MIN_FRAC_DIGITS;

    /**
     * The format for a long, more exactly representation of double values.
     */
    private static final DecimalFormat LONG_DOUBLE_FORMAT;

    /**
     * Sets the properties of the common double format.
     */
    static {
        SHORT_DOUBLE_FORMAT = new DecimalFormat();
        SHORT_DOUBLE_FORMAT.setGroupingUsed(false);
        SHORT_DOUBLE_FORMAT.setMinimumIntegerDigits(1);
        SHORT_DOUBLE_FORMAT.setMaximumFractionDigits(2);
        SHORT_DOUBLE_FORMAT.setMinimumFractionDigits(2);

        SHORT_DOUBLE_FORMAT.setDecimalFormatSymbols(new DecimalFormatSymbols(
            Locale.ENGLISH));

        SHORT_DOUBLE_FORMAT_MIN_FRAC_DIGITS = new DecimalFormat();
        SHORT_DOUBLE_FORMAT_MIN_FRAC_DIGITS.setGroupingUsed(false);
        SHORT_DOUBLE_FORMAT_MIN_FRAC_DIGITS.setMinimumIntegerDigits(1);
        SHORT_DOUBLE_FORMAT_MIN_FRAC_DIGITS.setMaximumFractionDigits(2);
        SHORT_DOUBLE_FORMAT_MIN_FRAC_DIGITS.setMinimumFractionDigits(0);

        SHORT_DOUBLE_FORMAT_MIN_FRAC_DIGITS.setDecimalFormatSymbols(new DecimalFormatSymbols(
            Locale.ENGLISH));

        LONG_DOUBLE_FORMAT = new DecimalFormat();
        LONG_DOUBLE_FORMAT.setGroupingUsed(false);
        LONG_DOUBLE_FORMAT.setMinimumIntegerDigits(1);
        LONG_DOUBLE_FORMAT.setMaximumFractionDigits(6);
        LONG_DOUBLE_FORMAT.setMinimumFractionDigits(2);

        LONG_DOUBLE_FORMAT.setDecimalFormatSymbols(new DecimalFormatSymbols(
            Locale.ENGLISH));
    }

    /**
     * Returns a short string representation 
     * (i.e. with exactly two fraction digits) of the given double 
     * value.
     * 
     * @param value the double value to format
     * 
     * @return the string representation of the given double value
     */
    public static String formatShort(double value) {

        synchronized (SHORT_DOUBLE_FORMAT) {
            return SHORT_DOUBLE_FORMAT.format(value);
        }
    }

    /**
     * Returns a short string representation 
     * (i.e. with a maximum of two fraction digits) of the given 
     * double value.
     * 
     * @param value the double value to format
     * 
     * @return the string representation of the given double value
     */
    public static String formatShortToMinimumFractionDigits(double value) {

        synchronized (SHORT_DOUBLE_FORMAT_MIN_FRAC_DIGITS) {
            return SHORT_DOUBLE_FORMAT_MIN_FRAC_DIGITS.format(value);
        }
    }

    /**
     * Returns a long, more exactly string representation 
     * (i.e. with six fraction digits) of the given double value.
     * 
     * @param value the double value to format
     * 
     * @return the string representation of the given double value
     */
    public static String formatLong(double value) {

        synchronized (LONG_DOUBLE_FORMAT) {
            return LONG_DOUBLE_FORMAT.format(value);
        }
    }

    /**
     * Parses the given string into a double number by applying the common 
     * double format.
     * 
     * @param value the string to parse into a double value
     * 
     * @return the parsed double value
     * 
     * @throws NumberFormatException if the given string cannot be parsed
     * into a double value
     */
    public static double parse(String value) throws NumberFormatException {

        ArgChecker.checkNotNull(value, "value");

        if((value = value.trim()).startsWith("+")) {
            value = value.substring(1);
        }

        ParsePosition pos = new ParsePosition(0);

        Number parsed = null;
        synchronized (SHORT_DOUBLE_FORMAT) {
            parsed = SHORT_DOUBLE_FORMAT.parse(value, pos);
        }

        if(parsed == null || pos.getIndex() < value.length()) {
            throw new NumberFormatException("unparseable double value: " + value);
        }

        return parsed.doubleValue();
    }

    private DoubleFormat() {

        /* not instantiable */
    }
}
