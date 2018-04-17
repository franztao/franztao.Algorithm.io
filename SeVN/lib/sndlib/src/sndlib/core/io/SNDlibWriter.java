/*
 * $Id: SNDlibWriter.java 429 2008-01-23 14:44:07Z roman.klaehne $
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
import java.io.Writer;

import sndlib.core.model.Model;
import sndlib.core.network.Network;
import sndlib.core.solution.Solution;

/**
 * This interface provides the common methods to write {@link Network}s,
 * {@link Model}s and {@link Solution}s.<br/>
 * <br/>
 * 
 * An implementation of this interface for handling a specific IO-format can 
 * be obtained by calling the static factory method 
 * {@link SNDlibIOFactory#newWriter(String)}.<br/>
 * <br/>
 * 
 * The IO-formats currently implemented are listed in {@link SNDlibIOFormat}.
 * 
 * @see SNDlibIOFactory
 * @see SNDlibIOFormat
 * 
 * @author Roman Klaehne
 */
public interface SNDlibWriter {

    /**
     * Writes the given network to the specified target writer.
     * 
     * @param network the network to write
     * @param target the target to which the network is written
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibWriteException if an error concerning the IO-format 
     * occured
     */
    public void writeNetwork(Network network, Writer target) throws IOException,
        SNDlibWriteException;

    /**
     * Writes the given solution to the specified target writer.
     * 
     * @param solution the solution to write
     * @param target the target to which the solution is written
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibWriteException if an error concerning the IO-format 
     * occured
     */
    public void writeSolution(Solution solution, Writer target) throws IOException,
        SNDlibWriteException;

    /**
     * Writes the given model to the specified target writer.
     * 
     * @param model the model to write
     * @param target the target to which the model is written
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibWriteException if an error concerning the IO-format
     * occured
     */
    public void writeModel(Model model, Writer target) throws IOException,
        SNDlibWriteException;

    /**
     * Returns the string indicating the IO-format handled by this writer.
     * 
     * @return the IO-format handled by this writer
     * 
     * @see SNDlibIOFormat
     */
    public String getFormat();
}

