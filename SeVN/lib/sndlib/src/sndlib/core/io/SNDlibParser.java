/*
 * $Id: SNDlibParser.java 429 2008-01-23 14:44:07Z roman.klaehne $
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
import java.io.Reader;

import sndlib.core.model.Model;
import sndlib.core.network.Network;
import sndlib.core.solution.Solution;

/**
 * This interface provides the common methods to parse {@link Network}s,
 * {@link Model}s and {@link Solution}s.<br/>
 * <br/>
 * 
 * An implementation of this interface for handling a specific IO-format can 
 * be obtained by calling the static factory method 
 * {@link SNDlibIOFactory#newParser(String)}.<br/>
 * <br/>
 * 
 * The IO-formats currently implemented are listed in {@link SNDlibIOFormat}.
 * 
 * @see SNDlibIOFactory
 * @see SNDlibIOFormat
 * 
 * @author Roman Klaehne
 */
public interface SNDlibParser {

    /**
     * Parses the given input source in order to produce a <tt>Network</tt>.
     * 
     * @param source the network source
     * 
     * @return the parsed network
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibParseException if an error concerning the format or
     * the consistency of the network occured
     */
    public Network parseNetwork(Reader source) throws IOException,
        SNDlibParseException;

    /**
     * Parses the given input source in order to produce a <tt>Solution</tt>.
     * 
     * @param source the solution source
     * 
     * @return the parsed solution
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibParseException if an error concerning the format or
     * the consistency of the solution occured
     */
    public Solution parseSolution(Reader source) throws IOException,
        SNDlibParseException;

    /**
     * Parses the given input source in order to produce a <tt>Model</tt>.
     * 
     * @param source the model source
     * 
     * @return the parsed model
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibParseException if an error concerning the format or
     * the consistency of the model occured
     */
    public Model parseModel(Reader source) throws IOException, SNDlibParseException;

    /**
     * Returns the string indicating the IO-format handled by this parser.
     * 
     * @return the IO-Format handled by this parser
     * 
     * @see SNDlibIOFormat
     */
    public String getFormat();
}

