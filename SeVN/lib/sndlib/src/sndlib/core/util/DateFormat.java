/*
 * $Id: DateFormat.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.atesio.utils.ArgChecker;

/**
 * This class encapsulates the common date format of SNDlib.
 *  
 * @author Roman Klaehne
 */
public class DateFormat {

    /**
     * The date format used to parse a given string into a date.
     */
    private static SimpleDateFormat PARSE_DATE_FORMAT = new SimpleDateFormat(
        "dd.MM.yy");

    /**
     * The date format used to format a given date into a string.
     */
    private static SimpleDateFormat FORMAT_DATE_FORMAT = new SimpleDateFormat(
        "dd.MM.yyyy");

    /**
     * Configures how to interpret a 2-digit-year in a date string being parsed.
     */
    static {
        Date startDate = null;
        try {
            startDate = parse("01.01.1950");
        }
        catch (ParseException px) {
            throw new AssertionError("invalid compilation unit: " + px);
        }

        PARSE_DATE_FORMAT.set2DigitYearStart(startDate);
    }

    private DateFormat() {

        /* not instantiable */
    }

    /**
     * Returns the format pattern of the common date format.
     * 
     * @return the date format pattern
     */
    public static String getFormatString() {

        return FORMAT_DATE_FORMAT.toPattern();
    }

    /**
     * Returns the common string representation of the given date.
     * 
     * @param date the date to format
     * 
     * @return the string representation of the given date
     */
    public static String format(Date date) {

        synchronized (FORMAT_DATE_FORMAT) {
            return FORMAT_DATE_FORMAT.format(date);
        }
    }

    /**
     * Parses the given date string into a date by applying the common date 
     * format.<br/>
     * <br/>
     * 
     * If the given string encodes a 2-digit-year it is placed in the range 
     * <tt>[1950, 2049]</tt>.
     * 
     * @param dateString the date string to parse into a date
     * 
     * @return the parsed date
     * 
     * @throws ParseException if the given string cannot be parsed into a 
     * date
     */
    public static Date parse(String dateString) throws ParseException {

        ArgChecker.checkNotNull(dateString, "date string");

        dateString = dateString.trim();

        ParsePosition pos = new ParsePosition(0);

        Date parsed = null;
        synchronized (PARSE_DATE_FORMAT) {
            parsed = PARSE_DATE_FORMAT.parse(dateString, pos);
        }

        if(parsed == null || pos.getIndex() < dateString.length()) {
            throw new ParseException("unparseable date: " + dateString,
                pos.getErrorIndex());
        }

        return parsed;
    }
}

