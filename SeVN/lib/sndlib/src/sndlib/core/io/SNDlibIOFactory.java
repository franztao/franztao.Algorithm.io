/*
 * $Id: SNDlibIOFactory.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

package sndlib.core.io;

import sndlib.core.ConfigurationException;

import com.atesio.utils.ArgChecker;

/**
 * This factory constructs {@link SNDlibParser}s and {@link SNDlibWriter}s 
 * to be used to handle specific IO-formats of SNDlib.
 * 
 * @see SNDlibIOFormat
 * @see SNDlibParser
 * @see SNDlibWriter
 * 
 * @author Roman Klaehne
 */
public class SNDlibIOFactory {

    /**
     * Returns a new parser instance which handles the specified IO-format.
     * 
     * @param format the IO-format
     * 
     * @return the appropriate parser to handle the specified IO-format
     * 
     * @throws IllegalArgumentException if the specified IO-format is unknown.
     * @throws ConfigurationException if there is no parser implementation 
     * available for the specified IO-format; or an error occured while 
     * constructing the new parser instance 
     */
    public static SNDlibParser newParser(String format) {

        ArgChecker.checkNotNull(format, "format");

        return newParser(SNDlibIOFormat.valueOf(format));
    }

    /**
     * Returns a new parser instance which handles the specified IO-format.
     * 
     * @param format the IO-format
     * 
     * @return the appropriate parser to handle the specified IO-format
     * 
     * @throws ConfigurationException if there is no parser implementation 
     * available for the specified IO-format; or an error occured while 
     * constructing the new parser instance 
     */
    public static SNDlibParser newParser(SNDlibIOFormat format) {

        ArgChecker.checkNotNull(format, "format");

        String className = format.getParserClassName();
        if(className == null || className.length() == 0) {
            throw new ConfigurationException("no parser available for format "
                + format);
        }

        Object parserInstance = newInstance(className);
        if(parserInstance == null) {
            throw new ConfigurationException("could not find parser class "
                + className);
        }

        if(!(parserInstance instanceof SNDlibParser)) {
            throw new ConfigurationException("class " + className
                + " does not implement the SndLibParser interface");
        }

        return (SNDlibParser) parserInstance;

    }

    /**
     * Returns a new writer instance which handles the specified IO-format.
     * 
     * @param format the IO-format
     * 
     * @return the appropriate writer to handle the specified IO-format
     * 
     * @throws IllegalArgumentException if the specified IO-format is unknown.
     * @throws ConfigurationException if there is no writer implementation 
     * available for the specified IO-format; or an error occured while 
     * constructing the new writer instance 
     */
    public static SNDlibWriter newWriter(String format) {

        ArgChecker.checkNotNull(format, "format");

        return newWriter(SNDlibIOFormat.valueOf(format));
    }

    /**
     * Returns a new writer instance which handles the specified IO-format.
     * 
     * @param format the IO-format
     * 
     * @return the appropriate writer to handle the specified IO-format
     * 
     * @throws ConfigurationException if there is no writer implementation 
     * available for the specified IO-format; or an error occured while 
     * constructing the new writer instance 
     */
    public static SNDlibWriter newWriter(SNDlibIOFormat format) {

        ArgChecker.checkNotNull(format, "format");

        String className = format.getWriterClassName();
        if(className == null || className.length() == 0) {
            throw new ConfigurationException("no writer available for format "
                + format);
        }

        Object writerInstance = newInstance(className);
        if(writerInstance == null) {
            throw new ConfigurationException("could not find writer class "
                + className);
        }

        if(!(writerInstance instanceof SNDlibWriter)) {
            throw new ConfigurationException("class " + className
                + " does not implement the SndLibWriter interface");
        }

        return (SNDlibWriter) writerInstance;
    }

    /**
     * Constructs a new instance of the specified class. 
     * 
     * @param className the name of the class to load
     * 
     * @return a new instance of the specified class; <tt>null</tt> if the 
     * class cannot be found
     * 
     * @throws ConfigurationException if the specified class cannot be 
     * instantiated
     */
    private static Object newInstance(String className)
        throws ConfigurationException {

        Class clazz = null;

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl != null) {
            try {
                clazz = cl.loadClass(className);
            }
            catch (ClassNotFoundException cnfx) {
                /* try to load the class using the current class loader; see below */
            }
        }

        if(clazz == null) {
            cl = SNDlibIOFactory.class.getClassLoader();
            try {
                clazz = cl.loadClass(className);
            }
            catch (ClassNotFoundException cnfx) {
                return null;
            }
        }

        try {
            return clazz.newInstance();
        }
        catch (Exception x) {
            throw new ConfigurationException("could not instantiate class "
                + className + ": " + x.getMessage(), x);
        }
    }

    private SNDlibIOFactory() {

        /* not instantiable */
    }
}

