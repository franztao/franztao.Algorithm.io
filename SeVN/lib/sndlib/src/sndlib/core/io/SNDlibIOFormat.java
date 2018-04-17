/*
 * $Id: SNDlibIOFormat.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import sndlib.core.ConfigurationException;

import com.atesio.utils.ArgChecker;
import com.atesio.utils.ResourceLoader;

/**
 * This class represents a SNDlib IO format.<br/>
 * <br/>
 * 
 * The available formats and their properties are defined in the property file 
 * named <tt>sndlib.io.formats.properties</tt>, which is contained in the package 
 * of this class.<br/>
 * <br/>
 * 
 * The format definitions in the property file mentioned above can be expanded
 * resp. overridden by providing a property file with the same name located 
 * on the classpath (not in this class's package).<br/>
 * <br/>
 * 
 * The format of the property file is intentionally simple. An entry looks 
 * like:
 * 
 * <p><tt>&lt;format&gt;.&lt;property&gt;=&lt;value&gt;</tt></p>
 * 
 * The available properties are <tt>parserClass</tt>, <tt>writerClass</tt> and
 * <tt>fileExtension</tt>. The values for the former are the fully qualified
 * names of the classes implementing the {@link sndlib.core.io.SNDlibParser} resp. 
 * {@link sndlib.core.io.SNDlibWriter} interfaces. The latter specifies the common
 * filename extension to be used when writing files in that format.
 * 
 * @author Roman Klaehne
 */
public class SNDlibIOFormat {

    /**
     * Indentifies the native format of SNDlib.
     */
    public static final String NATIVE = "native";

    /**
     * Identifies the XML format of SNDlib.
     */
    public static final String XML = "xml";

    /**
     * The properties specifying the available IO formats.
     */
    private static Properties _ioProps;

    /**
     * Caches the already loaded IO formats.
     */
    private static Map<String, SNDlibIOFormat> _loadedFormats = new HashMap<String, SNDlibIOFormat>();;

    /**
     * Reads the IO properties.
     */
    static {
        Properties defaultIOProps = new Properties();

        String defaultIOPropsId = SNDlibIOFormat.class.getPackage().getName().replace(
            '.', '/')
            + "/sndlib.io.formats.properties";

        InputStream propsIn = ResourceLoader.loadResource(SNDlibIOFormat.class,
            defaultIOPropsId);
        if(propsIn == null) {
            throw new ConfigurationException("could not found " + defaultIOPropsId);
        }

        try {
            defaultIOProps.load(propsIn);
        }
        catch (IOException iox) {
            throw new ConfigurationException("io error while loading "
                + defaultIOPropsId);
        }

        _ioProps = new Properties();
        _ioProps.putAll(defaultIOProps);

        propsIn = ResourceLoader.loadResource(SNDlibIOFormat.class,
            "sndlib.io.formats.properties");
        if(propsIn != null) {
            Properties providedProps = new Properties();
            try {
                providedProps.load(propsIn);
            }
            catch (IOException iox) {
                throw new ConfigurationException(
                    "io error while loading sndlib.io.formats.properties");
            }
            _ioProps.putAll(providedProps);
        }
    }

    /**
     * Returns the IO format corresponding to the specified name.<br/>
     * <br/>
     * 
     * This method first searches for the specified format in a property 
     * file named <tt>sndlib.io.formats.properties</tt>. If that property
     * file does not exist or does not contain the definition of the specified 
     * format this method looks in the default property file contained in the 
     * package of this class.
     * 
     * @param format the name of the format to be returned
     * 
     * @return the IO format corresponding to the specified
     * name
     * 
     * @throws IllegalArgumentException if the specified format is unknown
     */
    public static SNDlibIOFormat valueOf(String format)
        throws IllegalArgumentException {

        SNDlibIOFormat ioFormat = loadFormat(format);
        if(ioFormat == null) {
            throw new IllegalArgumentException("unknown format: " + format);
        }

        return ioFormat;
    }

    /**
     * Tests whether there is an IO format with the specified name.
     * 
     * @param format the name of the format
     * 
     * @return <tt>true</tt> if and only if there is a format with the 
     * specified name; <tt>false</tt> otherwise
     */
    public static boolean hasFormat(String format) {

        return loadFormat(format) != null;
    }

    /**
     * Loads the IO format with the specified name.
     * 
     * @param format the name of the format to be loaded
     * 
     * @return the loaded IO format; <tt>null</tt> if there is no such 
     * format 
     */
    private static SNDlibIOFormat loadFormat(String format) {

        ArgChecker.checkNotNull(format, "format");

        format = format.toLowerCase();

        SNDlibIOFormat ioFormat = _loadedFormats.get(format);
        if(ioFormat == null) {

            String parserClassName = _ioProps.getProperty(format + ".parserClass");
            String writerClassName = _ioProps.getProperty(format + ".writerClass");

            if((parserClassName == null || parserClassName.length() == 0)
                && (writerClassName == null || writerClassName.length() == 0)) {
                return null;
            }

            ioFormat = new SNDlibIOFormat();

            ioFormat._parserClassName = parserClassName;
            ioFormat._writerClassName = writerClassName;
            ioFormat._fileExtension = _ioProps.getProperty(
                format + ".fileExtension", "unknown");
            ioFormat._name = format;

            _loadedFormats.put(format, ioFormat);
        }

        return ioFormat;
    }

    /**
     * The class name of the {@link sndlib.core.io.SNDlibParser} implementation 
     * handling this format.
     */
    private String _parserClassName;

    /**
     * The class name of the {@link sndlib.core.io.SNDlibWriter} implementation
     * handling this format.
     */
    private String _writerClassName;

    /**
     * The common filename extension to be used when writing files in this IO 
     * format.
     */
    private String _fileExtension;

    /**
     * The name of this format.
     */
    private String _name;

    /**
     * Returns the class name of the {@link sndlib.core.io.SNDlibParser} 
     * implementation handling this format.
     * 
     * @return the class name of the {@link sndlib.core.io.SNDlibParser} 
     * implementation handling this format
     */
    public String getParserClassName() {

        return _parserClassName;
    }

    /**
     * Returns the class name of the {@link sndlib.core.io.SNDlibWriter} 
     * implementation handling this format.
     * 
     * @return the class name of the {@link sndlib.core.io.SNDlibWriter} 
     * implementation handling this format
     */
    public String getWriterClassName() {

        return _writerClassName;
    }

    /**
     * Returns the common filename extension to be used when writing files in 
     * this format. If no filename extension is specified this method 
     * returns <tt>unknown</tt>.
     * 
     * @return the common filename extension to be used when writing files in 
     * this format; <tt>unknown</tt> if no filename extension is specified
     */
    public String getFileExtension() {

        return _fileExtension;
    }

    /**
     * Returns the name of this format.
     * 
     * @return the name of this format
     */
    public String getName() {

        return _name;
    }

    /**
     * Constructs a new IO format.
     */
    private SNDlibIOFormat() {

        /* nothing to initialize; see above */
    }
}

