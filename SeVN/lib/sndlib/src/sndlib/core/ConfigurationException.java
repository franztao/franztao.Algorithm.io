/*
 * $Id: ConfigurationException.java 99 2006-07-04 14:05:16Z bzforlow $
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

package sndlib.core;

/**
 * This exception is used to indicate any configuration error, e.g. 
 * missing property files.
 *  
 * @author Roman Klaehne
 */
public class ConfigurationException extends SNDlibRuntimeException {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -3383038736473888183L;

    /**
     * Constructs a new <tt>ConfigurationException</tt> without providing
     * a message.
     */
    public ConfigurationException() {

        super();
    }

    /**
     * Constructs a new <tt>ConfigurationException</tt> with the specified
     * message.
     * 
     * @param msg the exception message
     */
    public ConfigurationException(String msg) {

        super(msg);
    }

    /**
     * Constructs a new <tt>ConfigurationException</tt> with the specified
     * message and cause.
     * 
     * @param msg the exception message
     * @param cause the cause of the exception
     */
    public ConfigurationException(String msg, Throwable cause) {

        super(msg, cause);
    }
}

