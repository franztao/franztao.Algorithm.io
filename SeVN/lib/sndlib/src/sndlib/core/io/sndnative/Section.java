/*
 * $Id: Section.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class represents the content of a section of a network or a solution 
 * file written in the SNDlib native format.<br/>
 * <br/>
 * 
 * A <tt>Section</tt> consists of a name, {@link Line}s and potentially a set 
 * of child sections.<br/>
 * <br/>
 * 
 * This class is considered as an internal helper class of this package.
 * 
 * @author Roman Klaehne
 */
class Section {

    /**
     * The child sections of this section.
     */
    private Set<Section> _childSections;

    /**
     * The lines of this section.
     */
    private List<Line> _lines;

    /**
     * The name of this section.
     */
    private String _sectionName;

    /**
     * The line which opened the section.
     */
    private Line _beginLine;

    /**
     * Constructs a new section with the specified name and line which 
     * opened the section.
     * 
     * @param sectionName the name of the section
     * @param beginLine the line opened the section
     */
    Section(String sectionName, Line beginLine) {

        _sectionName = sectionName;
        _childSections = new HashSet<Section>();
        _lines = new LinkedList<Line>();
        _beginLine = beginLine;
    }

    /**
     * Constructs a new section with the specified name.
     * 
     * @param sectionName the name of the section
     */
    Section(String sectionName) {

        this(sectionName, new Line("", 0));
    }

    /**
     * Tests whether this section contains a child section with the specified
     * name.
     * 
     * @param name the name of the child section to look for
     * 
     * @return <tt>true</tt> if and only if this section contains a child
     * section with the specified name; <tt>false</tt> otherwise
     */
    boolean contains(String name) {

        return getChildSection(name) != null;
    }

    /**
     * Returns the first occurence of a child section with the 
     * specified name.
     * 
     * @param name the name of the child section to return
     * 
     * @return the child section with the specified name; <tt>null</tt>
     * if there is no such child section
     */
    Section getChildSection(String name) {

        if(_childSections == null) {
            return null;
        }

        for(Section section : _childSections) {
            if(section._sectionName.equals(name)) {
                return section;
            }
        }
        return null;
    }

    /**
     * Returns all child sections with the specified name.
     * 
     * @param name the name of the child section to return
     * 
     * @return the child section with the specified name; <tt>null</tt>
     * if there is no such child section
     */
    Set<Section> getChildSections(String name) {

        Set<Section> sections = new HashSet<Section>();

        if(_childSections != null) {
            for(Section section : _childSections) {
                if(section._sectionName.equals(name)) {
                    sections.add(section);
                }
            }
        }

        return sections;
    }

    /**
     * Returns the line which opened this section.
     * 
     * @return the line which opened this section
     */
    Line getBeginLine() {

        return _beginLine;
    }

    /**
     * Returns all lines of this section.
     * 
     * @return all lines of this section
     */
    List<Line> getLines() {

        return _lines;
    }

    /**
     * Returns the all child sections of this section.
     * 
     * @return all child sections of this section
     */
    Set<Section> getChildSections() {

        return _childSections;
    }

    /**
     * Returns the name of this section.
     * 
     * @return the name of this section
     */
    String getName() {

        return _sectionName;
    }

    /**
     * Adds the given child section to this section.
     * 
     * @param childSection the child section to add
     */
    void addChildSection(Section childSection) {

        _childSections.add(childSection);
    }

    /**
     * Adds the given line to this section.
     * 
     * @param line the line to add
     */
    void addLine(Line line) {

        _lines.add(line);
    }

    /**
     * Adds the given lines to this section.
     * 
     * @param lines the lines to add
     */
    void addLines(List<Line> lines) {

        _lines.addAll(lines);
    }
}

