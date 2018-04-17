/*
 * $Id: SNDlibNativeParser.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

package sndlib.core.io.sndnative;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import sndlib.core.io.SNDlibIOFormat;
import sndlib.core.io.SNDlibParseException;
import sndlib.core.io.SNDlibParser;
import sndlib.core.io.sndnative.SNDlibNativeHeader.FileType;
import sndlib.core.model.Model;
import sndlib.core.network.Network;
import sndlib.core.solution.Solution;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This {@link SNDlibParser} implementation handles the SNDlib native format.
 * <br/><br/>
 * 
 * Instances of this class should not be created directly but obtained by 
 * calling the static factory method of {@link sndlib.core.io.SNDlibIOFactory}. 
 * <br/><br/>
 * 
 * The SNDlib native format is described on the <a target="_blank" 
 * href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html">
 * SNDlib webpage</a>
 * 
 * @see sndlib.core.io.SNDlibIOFactory
 * @see SNDlibIOFormat#NATIVE
 * 
 * @author Roman Klaehne
 */
public class SNDlibNativeParser implements SNDlibParser {

    /**
     * Lists the error messages which may be produced by this parser.
     */
    private static class ErrorKeys {

        final static MessageKey INVALID_FILE_TYPE_IN_HEADER = new SimpleMessageKey(
            "parser.text.header.error.invalidType");

        final static MessageKey UNSUPPORTED_VERSION = new SimpleMessageKey(
            "parser.text.header.error.unsupportedVersion");
    }

    /**
     * @inheritDoc
     */
    public Network parseNetwork(Reader source) throws IOException,
        SNDlibParseException {

        LineNumberReader reader = new LineNumberReader(source);
        SNDlibNativeHeader header = SNDlibNativeHeader.parse(reader);

        checkHeader(FileType.network.name(), header);

        return NetworkNativeParser.parseNetwork(reader);
    }

    /**
     * @inheritDoc
     */
    public Solution parseSolution(Reader source) throws IOException,
        SNDlibParseException {

        LineNumberReader reader = new LineNumberReader(source);
        SNDlibNativeHeader header = SNDlibNativeHeader.parse(reader);

        checkHeader(FileType.solution.name(), header);

        return SolutionNativeParser.parseSolution(reader);
    }

    /**
     * @inheritDoc
     */
    public Model parseModel(Reader source) throws IOException, SNDlibParseException {

        LineNumberReader reader = new LineNumberReader(source);
        SNDlibNativeHeader header = SNDlibNativeHeader.parse(reader);

        checkHeader(FileType.model.name(), header);

        return ModelNativeParser.parseModel(reader);
    }

    /**
     * Checks whether the given header has the specified expected file type 
     * and valid format version.
     * 
     * @param expectedFileType the file type the header must have
     * @param header the header
     * 
     * @throws SNDlibParseException if the header is invalid
     */
    private void checkHeader(String expectedFileType, SNDlibNativeHeader header)
        throws SNDlibParseException {

        Messages errors = new Messages();

        String fileType = header.getFileType();
        if(!fileType.equals(expectedFileType)) {
            errors.add(SNDlibMessages.error(ErrorKeys.INVALID_FILE_TYPE_IN_HEADER,
                expectedFileType, fileType));
        }

        String version = header.getVersion();
        if(!version.equals("1.0")) {
            errors.add(SNDlibMessages.error(ErrorKeys.UNSUPPORTED_VERSION, version));
        }

        if(errors.size() > 0) {
            throw new SNDlibParseException(errors);
        }
    }

    /**
     * Returns {@link SNDlibIOFormat#NATIVE}.
     * 
     * @return {@link SNDlibIOFormat#NATIVE}
     */
    public String getFormat() {

        return SNDlibIOFormat.NATIVE;
    }
}
