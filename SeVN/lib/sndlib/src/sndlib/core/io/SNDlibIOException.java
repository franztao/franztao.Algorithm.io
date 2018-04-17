/*
 * $Id: SNDlibIOException.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

import sndlib.core.SNDlibException;

import com.atesio.utils.message.Message;
import com.atesio.utils.message.Messages;

/**
 * This exception is the base type of all exceptions related to the 
 * IO-implementations of SNDlib. 
 * 
 * @author Roman Klaehne
 */
public class SNDlibIOException extends SNDlibException {

    /**
     * The serial version UID. 
     */
    
    private static final long serialVersionUID = -962410887707743077L;

    /**
     * Constructs a new exception without specifying any additional 
     * information.
     */
    public SNDlibIOException() {

        super();
    }

    /**
     * Constructs a new exception with the specified error message.
     * 
     * @param msg the error message
     */
    public SNDlibIOException(String msg) {

        super(msg);
    }

    /**
     * Constructs a new exception with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public SNDlibIOException(Throwable cause) {

        super(cause);
    }

    /**
     * Constructs a new exception with the specified error message and cause.
     * 
     * @param msg the error message
     * @param cause the cause of the exception
     */
    public SNDlibIOException(String msg, Throwable cause) {

        super(msg, cause);
    }

    /**
     * Constructs a new exception with the specified error messages.
     * 
     * @param errors the error messages
     */
    public SNDlibIOException(Messages errors) {

        super(errors);
    }

    /**
     * Constructs a new exception with the specified error message.
     * 
     * @param error the error message
     */
    public SNDlibIOException(Message error) {

        super(error);
    }
}

