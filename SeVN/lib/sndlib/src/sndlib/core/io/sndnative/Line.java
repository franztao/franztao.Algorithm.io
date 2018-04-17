/*
 * $Id: Line.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

/**
 * This class represents a single line of text contained in a {@link Section} 
 * of a network or solution file written in the SNDlib native format.<br/>
 * <br/>
 * 
 * A <tt>Line</tt> is produced by the {@link PatternLineReader} and 
 * encapsulates the parsed line elements (resp. the matching groups of the 
 * line pattern) and the line number at which it was read from the file.<br/>
 * <br/>
 * 
 * This class is considered as an internal helper class of this package.
 * 
 * @see PatternLineReader
 * 
 * @author Roman Klaehne
 */
class Line {

    /**
     * This is the constant to identify the end of the input.
     */
    public static final Line EOF = new Line("EOF", -1);

    /**
     * This is the constant to identify the end of a section.
     */
    public static final Line EOS = new Line("EOS", -1);

    /**
     * The elements of this line.
     */
    private String[] _lineElements;

    /**
     * The number at which this line was read from the file.
     */
    private int _lineNumber;

    /**
     * The entire input line as it was read from the file.
     */
    private String _line;

    /**
     * Constructs a new line with the specified arguments.
     * 
     * @param lineElements the elements of the line
     * @param lineNumber the number of the line at which it was read from
     * the file
     * @param line the entire line as it was read from the file
     */
    Line(String[] lineElements, int lineNumber, String line) {

        _lineElements = lineElements;
        _lineNumber = lineNumber;
        _line = line;
    }

    /**
     * Constructs a new line with the specified arguments.
     * 
     * @param line the entire line as it was read from the file
     * @param lineNumber the number of the line at which it was read from
     * the file
     */
    Line(String line, int lineNumber) {

        this(new String[] {line}, lineNumber, line);
    }

    /**
     * Returns the entire line as it was read from the file.
     * 
     * @return the entire line
     */
    String getLine() {

        return _line;
    }

    /**
     * Returns the number of the line at which it was read from the file.
     * 
     * @return the number of the line at which it was read from the file
     */
    int getLineNumber() {

        return _lineNumber;
    }

    /**
     * Returns the element of this line at the specified position.
     * 
     * @param index the position
     * 
     * @return the line element at the specified position
     */
    String getLineElement(int index) {

        return _lineElements[index];
    }

    /**
     * Returns the number of elements in this line.
     * 
     * @return the number of elements in this line
     */
    int getLineElementCount() {

        return _lineElements.length;
    }

}

