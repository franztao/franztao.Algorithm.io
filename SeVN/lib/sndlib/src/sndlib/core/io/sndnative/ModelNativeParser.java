/*
 * $Id: ModelNativeParser.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

import static sndlib.core.io.sndnative.RegexConstants.IDENTIFIER;
import static sndlib.core.io.sndnative.RegexConstants.LINE_COMMENT_SIGN;
import static sndlib.core.io.sndnative.RegexConstants.ZERO_OR_MORE_SPACES;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.EnumSet;
import java.util.regex.Pattern;

import sndlib.core.io.SNDlibParseException;
import sndlib.core.model.Model;
import sndlib.core.model.ModelData;
import sndlib.core.model.ModelProperty;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.ArgChecker;
import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This class provides the method to parse model a file written in the native
 * format of SNDlib to produce a {@link sndlib.core.model.Model} instance.
 * <br/><br/>
 * 
 * The native format of a model file is described on the 
 * <a href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html" 
 * target="_blank">SNDlib webpage</a>.<br/>
 * <br/>
 * 
 * This class is considered as an internal helper of this package.
 * 
 * @see SNDlibNativeParser
 * @see sndlib.core.model.Model
 * 
 * @author Roman Klaehne
 */
class ModelNativeParser {

    /**
     * Lists the error messages which may be produced by this parser.
     */
    private static class ErrorKeys {

        final static MessageKey INVALID_MODEL_PROPERTY = new SimpleMessageKey(
            "parser.text.model.error.invalidModelProperty");

        final static MessageKey INVALID_PROPERTY_VALUE = new SimpleMessageKey(
            "parser.text.model.error.invalidPropertyValue");

        final static MessageKey DUPLICATE_MODEL_PROPERTY = new SimpleMessageKey(
            "parser.text.model.error.duplicateModelProperty");

        final static MessageKey MODEL_PROPERTY_NOT_FOUND = new SimpleMessageKey(
            "parser.text.model.error.modelPropertyNotFound");
    }

    /**
     * Defines the syntax of a line in a model file.
     * 
     * Capturing groups are:
     * 
     * <ul>
     *  <li>1. group: model property</li> 
     *  <li>2. group: model property value</li> 
     * </ul>
     */
    private static final String MODEL_LINE = ZERO_OR_MORE_SPACES + "(" + IDENTIFIER
        + ")" + ZERO_OR_MORE_SPACES + "=" + ZERO_OR_MORE_SPACES + "(" + IDENTIFIER
        + ")" + ZERO_OR_MORE_SPACES;

    /**
     * The pre-compiled pattern for a model line.
     */
    private static final Pattern MODEL_LINE_PATTERN = Pattern.compile(MODEL_LINE);

    /**
     * Parses the given source to produce a <tt>Model</tt>.
     * 
     * @param source the source to parse the model from
     * 
     * @return the parsed <tt>Model</tt>
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibParseException if an error concerning the syntax or
     * consistency occured
     */
    static Model parseModel(LineNumberReader source) throws IOException,
        SNDlibParseException {

        ArgChecker.checkNotNull(source, "source reader");
        Messages errors = new Messages();

        PatternLineReader reader = createReader(source, errors);

        ModelData modelData = new ModelData();

        try {
            Line line = null;
            while((line = reader.readLine(MODEL_LINE_PATTERN)) != Line.EOF) {

                String key = line.getLineElement(0);
                String value = line.getLineElement(1);

                if(!ModelProperty.hasEnumIgnoreCase(key)) {
                    errors.add(SNDlibMessages.error(ErrorKeys.INVALID_MODEL_PROPERTY,
                        line.getLineNumber(), key));
                    continue;
                }

                ModelProperty property = ModelProperty.valueOfIgnoreCase(key);

                if(modelData.getModel(property) != null) {
                    errors.add(SNDlibMessages.error(
                        ErrorKeys.DUPLICATE_MODEL_PROPERTY, line.getLineNumber(),
                        property.name()));
                }
                else if(!property.hasValueIgnoreCase(value)) {
                    errors.add(SNDlibMessages.error(ErrorKeys.INVALID_PROPERTY_VALUE,
                        line.getLineNumber(), value, property.name()));
                }
                else {
                    modelData.setModel(property, value);
                }
            }
        }
        finally {
            reader.closeReader();
        }

        Model model = createNetworkModel(modelData, errors);

        if(errors.size() > 0) {
            throw new SNDlibParseException(errors);
        }

        return model;
    }

    /**
     * Creates the <tt>PatternLineReader</tt> with the specified underlying 
     * reader instance and error container.
     * 
     * @param source the underlying reader
     * @param errors the error container
     * 
     * @return a new created <tt>PatternLineReader</tt>
     */
    private static PatternLineReader createReader(LineNumberReader source,
        Messages errors) {

        PatternLineReader reader = new PatternLineReader(source, errors);
        reader.setCommentBeginString(LINE_COMMENT_SIGN);

        return reader;
    }

    /**
     * Creates a new <tt>Model</tt> instance from the specified model data.
     * <br/><br/>
     * 
     * If there is any model property undefined in the given data object 
     * this method returns <tt>null</tt>, putting an error to the given
     * error container.
     * 
     * @param modelData the model data
     * @param errors the error container into which the errors to be put
     * 
     * @return the created <tt>Model</tt> instance; <tt>null</tt> if the 
     * given data object in incomplete
     */
    private static Model createNetworkModel(ModelData modelData, Messages errors) {

        int errorSizeBefore = errors.size();

        for(ModelProperty property : EnumSet.allOf(ModelProperty.class)) {

            if(modelData.getModel(property) == null) {
                errors.add(SNDlibMessages.error(ErrorKeys.MODEL_PROPERTY_NOT_FOUND,
                    property.name()));
            }
        }

        /* at least on model is undefined */
        if(errors.size() != errorSizeBefore) {
            return null;
        }

        return new Model(modelData);
    }

    private ModelNativeParser() {

        /* cannot be instantiated */
    }

}
