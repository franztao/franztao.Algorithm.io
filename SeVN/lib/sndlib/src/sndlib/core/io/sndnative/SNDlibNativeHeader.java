/*
 * $Id: SNDlibNativeHeader.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

import static sndlib.core.io.sndnative.RegexConstants.ONE_OR_MORE_SPACES;
import static sndlib.core.io.sndnative.RegexConstants.ZERO_OR_MORE_SPACES;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sndlib.core.io.SNDlibParseException;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This class represents the header of a network, model or solution file 
 * written in the SNDlib native format. The header consists of the file type
 * (<tt>network</tt>, <tt>model</tt> or <tt>solution</tt>) and the
 * native format version in which the file is written.<br/>
 * In more detail a header is of the following form:
 * 
 * <p><tt>
 *   ?SNDlib native format; type: {network | model | solution}; 
 *   version: &lt;version&gt;
 * </tt></p><br/>
 * 
 * This class provides methods for reading and writing a header.<br/>
 * <br/>
 * 
 * This class is considered as an internal helper of this package.
 * 
 * @author Roman Klaehne
 */
class SNDlibNativeHeader {

    /**
     * Lists the errors which may be produced while parsing the header.
     */
    private static class ErrorKeys {

        final static MessageKey HEADER_NOT_FOUND = new SimpleMessageKey(
            "parser.text.header.error.notFound");
    }

    /**
     * Defines the syntax of the header line.
     * 
     * Capturing groups are:
     * 
     * <ul>
     * <li>1.group: file type</li>
     * <li>2. group: format version</li>
     * </ul>
     */
    private static final Pattern HEADER_PATTERN = Pattern.compile(ZERO_OR_MORE_SPACES
        + "\\?SNDlib"
        + ONE_OR_MORE_SPACES
        + "native"
        + ONE_OR_MORE_SPACES
        + "format;"
        + ONE_OR_MORE_SPACES
        + "type:"
        + ONE_OR_MORE_SPACES
        + "(\\w+);"
        + ONE_OR_MORE_SPACES
        + "version:"
        + ONE_OR_MORE_SPACES
        + "(\\d{1,2}\\.\\d{1,2})");

    /**
     * The format used to write the header line.
     */
    private static final MessageFormat HEADER_FORMAT = new MessageFormat(
        "?SNDlib native format; type: {0}; version: {1}");

    /**
     * The newline character.
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * Lists the file types.
     */
    static enum FileType {
        network, solution, model;
    }

    /**
     * Parses the header line using the given reader.
     * 
     * @param reader the reader
     * 
     * @return the parsed header
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibParseException if the header line was not found
     */
    static SNDlibNativeHeader parse(LineNumberReader reader) throws IOException,
        SNDlibParseException {

        String headerLine = reader.readLine();
        while(headerLine != null && (headerLine = headerLine.trim()).length() == 0) {
            headerLine = reader.readLine();
        }

        if(headerLine == null) {
            throw new SNDlibParseException(SNDlibMessages.error(
                ErrorKeys.HEADER_NOT_FOUND, reader.getLineNumber()));
        }

        Matcher matcher = HEADER_PATTERN.matcher(headerLine);

        if(!matcher.matches()) {
            throw new SNDlibParseException(SNDlibMessages.error(
                ErrorKeys.HEADER_NOT_FOUND, reader.getLineNumber()));
        }

        String fileType = matcher.group(1);
        String version = matcher.group(2);

        return new SNDlibNativeHeader(fileType, version);
    }

    /**
     * Writes the header line with the specified information.
     * 
     * @param fileType the file type
     * @param version the format version
     * @param target the target to which the header is written
     * 
     * @throws IOException if an IO error occured
     */
    static void write(String fileType, String version, Writer target)
        throws IOException {

        target.write(HEADER_FORMAT.format(new Object[] {fileType, version},
            new StringBuffer(), null).toString()
            + NEW_LINE);
    }

    /**
     * The file type.
     */
    private String _fileType;

    /**
     * The format version.
     */
    private String _version;

    /**
     * Constructs a new header with the specified file type and format 
     * version.
     * 
     * @param fileType the file type
     * @param version the format version
     */
    private SNDlibNativeHeader(String fileType, String version) {

        _fileType = fileType;
        _version = version;
    }

    /**
     * Returns the file type.
     * 
     * @return the file type
     */
    String getFileType() {

        return _fileType;
    }

    /**
     * Returns the format version.
     * 
     * @return the format version
     */
    String getVersion() {

        return _version;
    }
}
