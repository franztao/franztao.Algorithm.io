/*
 * $Id$
 *
 * Copyright (c) 2005-2008 by Konrad-Zuse-Zentrum fuer Informationstechnik Berlin. 
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.atesio.utils.ConfigurationException;
import com.atesio.utils.ResourceLoader;
import com.atesio.utils.message.KeyedMessage;
import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.MessageResources;

/**
 * This class provides a static interface for creating keyed messages
 * used in SNDlib, e.g. the IO implementation. The message formats are stored 
 * in the file  <tt>MessageResources.properties</tt> contained in this 
 * package.<br/><br/> 
 * 
 * It is used in the implementation of this API but should not be used 
 * directly by the users of the same.
 * 
 * @author Roman Klaehne
 */
public class SNDlibMessages {

    /**
     * Contains the keyed message formats.
     */
    private static final MessageResources SNDLIB_MESSAGE_RESOURCES;

    static {
        Properties messageResources = new Properties();

        String resourceId = SNDlibMessages.class.getPackage().getName().replace('.',
            '/')
            + "/MessageResources.properties";

        InputStream resourceIn = ResourceLoader.loadResource(MessageResources.class,
            resourceId);
        if(resourceIn != null) {
            try {
                messageResources.load(resourceIn);
            }
            catch (IOException iox) {
                throw new ConfigurationException("could not load " + resourceId);
            }
        }
        SNDLIB_MESSAGE_RESOURCES = new MessageResources(messageResources);
    }

    /**
     * Constructs a new error message.
     * 
     * @param messageKey the key identifying the error message (resp. the 
     * message format pattern)
     * @param arguments a set of objects into the message to be placed
     * 
     * @return a new <tt>KeyedMessage</tt>
     */
    public static KeyedMessage error(MessageKey messageKey, Object... arguments) {

        return KeyedMessage.error(messageKey, SNDLIB_MESSAGE_RESOURCES, arguments);
    }

    /**
     * Constructs a new warning message.
     * 
     * @param messageKey the key identifying the warning message (resp. the 
     * message format pattern)
     * @param arguments a set of objects into the message to be placed
     * 
     * @return a new <tt>KeyedMessage</tt>
     */
    public static KeyedMessage warn(MessageKey messageKey, Object... arguments) {

        return KeyedMessage.warn(messageKey, SNDLIB_MESSAGE_RESOURCES, arguments);
    }

    /**
     * Constructs a new information.
     * 
     * @param messageKey the key identifying the information (resp. the 
     * message format pattern)
     * @param arguments a set of objects into the message to be placed
     * 
     * @return a new <tt>KeyedMessage</tt>
     */
    public static KeyedMessage info(MessageKey messageKey, Object... arguments) {

        return KeyedMessage.info(messageKey, SNDLIB_MESSAGE_RESOURCES, arguments);
    }

    /**
     * Blocked constructor
     */
    private SNDlibMessages() {

    }
}
