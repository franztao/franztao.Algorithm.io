/*
 * $Id: SNDlibRuntimeException.java 428 2008-01-23 14:43:09Z roman.klaehne $
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

import com.atesio.utils.message.Message;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessagingRuntimeException;

/**
 * This is the super class of all runtime exceptions in the sndlib project.
 * 
 * @author Roman Klaehne
 */
public class SNDlibRuntimeException extends MessagingRuntimeException {

    private static final long serialVersionUID = -810927716266078823L;

    /**
     * Constructs a new <tt>SNDlibRuntimeException</tt> without providing any
     * additional information.
     */
    public SNDlibRuntimeException() {

        super();
    }

    /**
     * Constructs a new <tt>SNDlibRuntimeException</tt> with the specified 
     * error message.
     * 
     * @param msg the error message
     */
    public SNDlibRuntimeException(String msg) {

        super(msg);
    }

    /**
     * Constructs a new <tt>SNDlibException</tt> with the specified error 
     * messages.
     * 
     * @param errors the error messages
     */
    public SNDlibRuntimeException(Messages errors) {

        super(errors);
    }

    /**
     * Constructs a new <tt>SNDlibException</tt> with the specified error 
     * message.
     * 
     * @param error the error message
     */
    public SNDlibRuntimeException(Message error) {

        super(error);
    }

    /**
     * Constructs a new <tt>SNDlibRuntimeException</tt> with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public SNDlibRuntimeException(Throwable cause) {

        super(cause);
    }

    /**
     * Constructs a new <tt>SNDlibRuntimeException</tt> with the specified message
     * and cause.
     * 
     * @param msg the error message
     * @param cause the cause of the exception
     */
    public SNDlibRuntimeException(String msg, Throwable cause) {

        super(msg, cause);
    }
}
