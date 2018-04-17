/*
 * $Id: SNDlibParseException.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

import com.atesio.utils.message.Message;
import com.atesio.utils.message.Messages;

/**
 * This exception is thrown by classes related to parsing networks, models 
 * and solutions.
 * 
 * @author Roman Klaehne
 */
public class SNDlibParseException extends SNDlibIOException {

    /** 
     * The serial version UID.
     */
    private static final long serialVersionUID = 3875468287818632466L;

    /**
     * Constructs a new exception with the specified error message.
     * 
     * @param msg the error message
     */
    public SNDlibParseException(String msg) {

        super(msg);
    }

    /**
     * Constructs a new exception with the specified error message and cause.
     * 
     * @param msg the error message
     * @param cause the cause of the exception
     */
    public SNDlibParseException(String msg, Throwable cause) {

        super(msg, cause);
    }

    /**
     * Constructs a new exception with the specified error messages.
     * 
     * @param errors the error messages
     */
    public SNDlibParseException(Messages errors) {

        super(errors);
    }

    /**
     * Constructs a new exception with the specified error message.
     * 
     * @param error the error message
     */
    public SNDlibParseException(Message error) {

        super(error);
    }
}

