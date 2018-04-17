/*
 * $Id: PatternLineReader.java 429 2008-01-23 14:44:07Z roman.klaehne $
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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.ArgChecker;
import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This class represents a reader useful for parsing text files line by line. 
 * It provides methods to validate each line read against a given regular 
 * expression {@link java.util.regex.Pattern}.<br/>
 * <br/>
 * 
 * This class is considered as an internal helper class of this package. 
 * 
 * @see Line
 * @see NetworkNativeParser
 * @see SolutionNativeParser
 * 
 * @author Roman Klaehne
 */
final class PatternLineReader {

    /**
     * The error messages used by this reader.
     */
    private static class ErrorKeys {

        final static MessageKey INVALID_LINE = new SimpleMessageKey(
            "parser.text.general.error.invalidLine");

        final static MessageKey END_OF_SECTION_EXPECTED = new SimpleMessageKey(
            "parser.text.general.error.endOfSectionExpected");
    }

    /**
     * The underlying reader instance.
     */
    private LineNumberReader _reader;

    /**
     * The error container.
     */
    private Messages _errors;

    /**
     * The pattern indicating the end of a section.
     */
    private Pattern _eosPattern;

    /**
     * The string indicating the begin of a comment.
     */
    private String _commentBegin;

    /**
     * Constructs a new <tt>PatternLineReader</tt>.
     * 
     * @param reader the underlying reader
     * @param errors the error container into which the errors to be put
     */
    PatternLineReader(LineNumberReader reader, Messages errors) {

        ArgChecker.checkNotNull(reader, "reader");
        ArgChecker.checkNotNull(errors, "errors");
        _reader = reader;
        _errors = errors;
    }

    /**
     * Returns the error container of this reader. It contains all the errors 
     * occured since this reader was constructed.
     * 
     * @return the error container of this reader
     */
    Messages getErrors() {

        return _errors;
    }

    /**
     * Closes this reader. After closing this reader all methods except
     * {@link #getErrors()} will throw an <tt>IllegalStateException</tt>
     */
    void closeReader() {

        checkState();

        try {
            _reader.close();
        }
        catch (IOException iox) {
            /* see below */
        }
        finally {
            _reader = null;
        }
    }

    /**
     * Reads lines from the underlying reader until reaching a line which 
     * matches the given pattern. The first line which matches the pattern
     * is returned.<br/>
     * <br/>
     *  
     * Lines which do not match the pattern are skipped, putting an error to
     * the error container.<br/>
     * If the specified pattern is <tt>null</tt> simply the next line read is 
     * returned.<br/>
     * 
     * @param pattern the pattern the line is validated against; 
     * can be <tt>null</tt>
     * 
     * @return the next line read which matches the given pattern; 
     * <tt>Line.EOF</tt> if the end of the input is reached; 
     * 
     * @throws IOException if an IO error occured
     */
    Line readLine(Pattern pattern) throws IOException {

        Line line = null;
        do {
            line = createLine(pattern, readNextLine());
        }
        while(line == null);

        return line;
    }

    /**
     * Reads lines from the underlying reader until reaching a line which 
     * matches the given pattern. The first line which matches the pattern
     * is returned.<br/>
     * <br/>
     * 
     * If there is a globally end-of-section-pattern set and the line read 
     * matches that pattern this method returns <tt>Line.EOS</tt>.<br/>
     * Lines which do not match the pattern are skipped, putting an error to
     * the error container.<br/>
     * If the specified pattern is <tt>null</tt> simply the next line read is 
     * returned.
     * 
     * @param pattern the pattern the line is validated against; 
     * can be <tt>null</tt>
     * 
     * @return the next line read which matches the given pattern; 
     * <tt>Line.EOF</tt> if the end of the input is reached; 
     * <tt>Line.EOS</tt> if the line read matches the globally 
     * end-of-section-pattern
     * 
     * @throws IOException if an IO error occured
     */
    Line readLineUntilEOS(Pattern pattern) throws IOException {

        if(_eosPattern == null) {
            return readLine(pattern);
        }

        Line line = null;

        do {
            String textLine = readNextLine();

            if(textLine != null && matches(_eosPattern, textLine)) {
                return Line.EOS;
            }

            line = createLine(pattern, textLine);
        }
        while(line == null);

        if(line == Line.EOF) {
            _errors.add(SNDlibMessages.error(ErrorKeys.END_OF_SECTION_EXPECTED,
                getLineNumber()));
        }

        return line;
    }

    /**
     * Reads lines from the underlying reader until reaching a line which 
     * matches the globally end-of-section-pattern. All lines are validated
     * against the specified pattern.<br/>
     * <br/>
     *
     * If there is no globally end-of-section-pattern set this method reads
     * until reaching the end of the input.<br/>
     * Lines which do not match the pattern are skipped, putting an error to
     * the error container.<br/>
     * If the specified pattern is <tt>null</tt> no validation is made.
     * 
     * @param pattern the pattern the lines are validated against; 
     * can be <tt>null</tt>
     * 
     * @return the lines read which matches the given pattern
     *
     * @throws IOException if an IO error occured
     */
    List<Line> readLinesUntilEOS(Pattern pattern) throws IOException {

        List<Line> lines = new LinkedList<Line>();

        for(Line line = readLineUntilEOS(pattern); line != Line.EOS
            && line != Line.EOF; line = readLineUntilEOS(pattern)) {
            lines.add(line);
        }

        return lines;
    }

    /**
     * Sets the string which indicates the begin of a line comment.
     * 
     * @param commentBegin the string which indicates the begin of a line 
     * comment 
     */
    void setCommentBeginString(String commentBegin) {

        _commentBegin = commentBegin;
    }

    /**
     * Sets the globally end-of-section-pattern.<br/>
     * <br/>
     * 
     * The end-of-section-pattern is used in 
     * {@link #readLinesUntilEOS(Pattern)} and 
     * {@link #readLineUntilEOS(Pattern)}.
     * 
     * @param eosPattern the globally end-of-section-pattern
     */
    void setEndOfSectionPattern(Pattern eosPattern) {

        _eosPattern = eosPattern;
    }

    /**
     * Returns the current line number.
     * 
     * @return the current line number
     */
    int getLineNumber() {

        checkState();

        return _reader.getLineNumber();
    }

    /**
     * Validates the given line of text against the given pattern. If the 
     * line matches the pattern a <tt>Line</tt> object is created and 
     * returned. Otherwise this method returns <tt>null</tt>, putting an
     * error to the error containter.<br/>
     * <br/>
     * 
     * If the given line is <tt>null</tt> then <tt>Line.EOF</tt> is 
     * returned.<br/>
     * If the given pattern is <tt>null</tt> no validation is made.<br/>
     * 
     * @param pattern the pattern to validate the given line
     * @param line the line of text
     * 
     * @return the parsed <tt>Line</tt> object; <tt>null</tt> if the given
     * line does not match the given pattern
     */
    private Line createLine(Pattern pattern, String line) {

        if(line == null) {
            return Line.EOF;
        }

        if(pattern == null) {
            return new Line(new String[] {line}, getLineNumber(), line);
        }

        Matcher matcher = pattern.matcher(line);
        if(!matcher.matches()) {
            _errors.add(SNDlibMessages.error(ErrorKeys.INVALID_LINE, getLineNumber(),
                line));
            return null;
        }

        String[] lineElements = getMatchingElements(matcher);

        return new Line(lineElements, getLineNumber(), line);
    }

    /**
     * Tests whether the given line matches the given pattern. If the given 
     * line is <tt>null</tt> this method returns <tt>false</tt>.
     * 
     * @param pattern the pattern
     * @param line the line
     * 
     * @return <tt>true</tt> if and only if the given line matches the given
     *         pattern; <tt>false</tt> otherwise
     */
    private boolean matches(Pattern pattern, String line) {

        if(line == null) {
            return false;
        }

        return pattern.matcher(line).matches();
    }

    /**
     * Reads the next line from the input source. Empty lines and comments 
     * will be skipped.
     * 
     * @return the next line
     */
    private String readNextLine() throws IOException {

        checkState();

        String line = null;
        while((line = _reader.readLine()) != null) {
            line = skipComment(line);

            /* skip empty lines */
            if(line.trim().length() > 0) {
                return line;
            }
        }

        return null;
    }

    /**
     * Removes comments from the given line. If the given line does not 
     * contain comments this method has no effect.
     * 
     * @param line the line to remove the comments from
     * 
     * @return the line without comments
     */
    private String skipComment(String line) {

        if(_commentBegin != null) {
            int indexOfComment = line.indexOf(_commentBegin);
            if(indexOfComment >= 0) {
                line = line.substring(0, indexOfComment);
            }
        }
        return line;
    }

    /**
     * Checks the state of this reader. If the reader was already closed then 
     * this method throws an <tt>IllegalStateException</tt>.
     * 
     * @throws IllegalStateException if the reader was already closed.
     */
    private void checkState() throws IllegalStateException {

        if(_reader == null) {
            throw new IllegalStateException("stream already consumed");
        }
    }

    /**
     * Transforms the maching groups of the given match result to an array of 
     * <tt>String</tt>s.
     * 
     * @param result the match result
     * 
     * @return a <tt>String</tt> array containing the matching groups of the
     * given match result
     */
    private static String[] getMatchingElements(MatchResult result) {

        String[] lineElements = new String[result.groupCount()];
        for(int i = 0; i < lineElements.length; ++i) {
            lineElements[i] = result.group(i + 1);
        }

        return lineElements;
    }
}
