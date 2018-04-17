/*
 * $Id: SNDlibXmlParser.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

package sndlib.core.io.xml;

import java.io.IOException;
import java.io.Reader;

import sndlib.core.io.SNDlibIOFormat;
import sndlib.core.io.SNDlibParseException;
import sndlib.core.io.SNDlibParser;
import sndlib.core.model.Model;
import sndlib.core.network.Network;
import sndlib.core.solution.Solution;

/**
 * This {@link SNDlibParser} implementation handles the SNDlib XML format.
 * <br/><br/>
 * 
 * Instances of this class should not be created directly but obtained by 
 * calling the static factory method of {@link sndlib.core.io.SNDlibIOFactory}. 
 * <br/><br/>
 * 
 * The SNDlib XML format is described on the <a target="_blank" 
 * href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html">
 * SNDlib webpage</a>
 * 
 * @see sndlib.core.io.SNDlibIOFactory
 * @see SNDlibIOFormat#XML
 * 
 * @author Roman Klaehne
 */
public class SNDlibXmlParser implements SNDlibParser {

    /**
     * @inheritDoc
     */
    public Network parseNetwork(Reader source) throws IOException,
        SNDlibParseException {

        return NetworkXmlParser.parseNetwork(source);
    }

    /**
     * @inheritDoc
     */
    public Solution parseSolution(Reader source) throws IOException,
        SNDlibParseException {

        return SolutionXmlParser.parseSolution(source);
    }

    /**
     * @inheritDoc
     */
    public Model parseModel(Reader source) throws IOException, SNDlibParseException {

        return ModelXmlParser.parseModel(source);
    }

    /**
     * Returns {@link SNDlibIOFormat#XML}.
     * 
     * @return {@link SNDlibIOFormat#XML}
     */
    public String getFormat() {

        return SNDlibIOFormat.XML;
    }
}

