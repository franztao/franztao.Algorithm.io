/*
 * $Id: SolutionNativeParser.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

import static sndlib.core.io.sndnative.RegexConstants.LINE_COMMENT_SIGN;
import static sndlib.core.io.sndnative.SolutionNativeSyntax.DEMAND_ROUTING_BEGIN_PATTERN;
import static sndlib.core.io.sndnative.SolutionNativeSyntax.DEMAND_ROUTING_LINE_PATTERN;
import static sndlib.core.io.sndnative.SolutionNativeSyntax.LINK_CONFIGURATIONS;
import static sndlib.core.io.sndnative.SolutionNativeSyntax.LINK_CONF_LINE_PATTERN;
import static sndlib.core.io.sndnative.SolutionNativeSyntax.ROUTINGS;
import static sndlib.core.io.sndnative.SolutionNativeSyntax.SOLUTION_SECTION_BEGIN_PATTERN;
import static sndlib.core.io.sndnative.SolutionNativeSyntax.SOLUTION_SECTION_END_PATTERN;
import static sndlib.core.util.DoubleFormat.parse;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import sndlib.core.io.SNDlibParseException;
import sndlib.core.problem.OperatingState;
import sndlib.core.solution.DemandRouting;
import sndlib.core.solution.LinkConfiguration;
import sndlib.core.solution.ModuleConfiguration;
import sndlib.core.solution.Solution;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.ArgChecker;
import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This class provides the method to parse a solution file written in the 
 * native format of SNDlib to produce a {@link sndlib.core.solution.Solution} 
 * instance.<br/>
 * <br/>
 *
 * The native format of a solution file is described on the 
 * <a href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html" 
 * target="_blank">SNDlib webpage</a>.<br/>
 * <br/>
 * 
 * This class is considered as an internal helper of this package.
 * 
 * @see SolutionNativeSyntax
 * @see SNDlibNativeParser
 * @see sndlib.core.solution.Solution
 * 
 * @author Roman Klaehne
 */
class SolutionNativeParser {

    /**
     * Lists the error messages which may be produced by this parser.
     */
    private static class ErrorKeys {

        final static MessageKey SECTION_NOT_FOUND = new SimpleMessageKey(
            "parser.text.general.error.sectionNotFound");

        final static MessageKey DUPLICATE_LINK_CONF = new SimpleMessageKey(
            "parser.text.solution.error.duplicateLinkConf");

        final static MessageKey DUPLICATE_MODULE_CONF = new SimpleMessageKey(
            "parser.text.solution.error.duplicateModuleConf");

        final static MessageKey DUPLICATE_ROUTING = new SimpleMessageKey(
            "parser.text.solution.error.duplicateRouting");

        final static MessageKey DUPLICATE_ROUTING_SECTION = new SimpleMessageKey(
            "parser.text.solution.error.duplicateRoutingSection");

        final static MessageKey LINK_ID_IN_ROUTING_PATH_NOT_FOUND = new SimpleMessageKey(
            "parser.text.solution.error.linkIdInRoutingPathNotFound");

        final static MessageKey NO_PATH_FLOW_IN_ROUTING = new SimpleMessageKey(
            "parser.text.solution.error.noFlowPathInRouting");

        final static MessageKey INVALID_STATE = new SimpleMessageKey(
            "parser.text.solution.error.invalidState");
    }

    /**
     * Parses the given source to produce a <tt>Solution</tt>.
     * 
     * @param source the source to parse the solution from
     * 
     * @return the parsed <tt>Solution</tt>
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibParseException if an error concerning the syntax or
     * consistency occured
     */
    static Solution parseSolution(LineNumberReader source) throws IOException,
        SNDlibParseException {

        ArgChecker.checkNotNull(source, "stream");

        Messages errors = new Messages();

        PatternLineReader reader = createReader(source, errors);

        Section solutionSection = null;

        try {
            solutionSection = readSolution(reader);
        }
        finally {
            reader.closeReader();
        }

        Solution solution = new Solution();

        Set<Section> sections = solutionSection.getChildSections(LINK_CONFIGURATIONS);
        if(sections.size() > 0) {
            for(Section section : sections) {
                parseLinkConfigurations(section, errors, solution);
            }
        }
        else {
            errors.add(SNDlibMessages.error(ErrorKeys.SECTION_NOT_FOUND,
                LINK_CONFIGURATIONS));
        }

        sections = solutionSection.getChildSections(ROUTINGS);
        if(sections.size() > 0) {
            for(Section section : sections) {
                parseRoutings(section, errors, solution);
            }
        }
        else {
            errors.add(SNDlibMessages.error(ErrorKeys.SECTION_NOT_FOUND, ROUTINGS));
        }

        if(errors.size() > 0) {
            throw new SNDlibParseException(errors);
        }

        return solution;
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
        reader.setEndOfSectionPattern(SOLUTION_SECTION_END_PATTERN);
        reader.setCommentBeginString(LINE_COMMENT_SIGN);

        return reader;
    }

    /**
     * Parses the given section object in order to produce the link 
     * configurations in the given solution.<br/>
     * <br/>
     * 
     * Errors occured while parsing are put into the specified error
     * container.
     * 
     * @param linkConfSection the section containing the link configuration 
     * data
     * @param solution the solution
     * @param errors the error container
     */
    private static void parseLinkConfigurations(Section linkConfSection,
        Messages errors, Solution solution) {

        for(Line linkConfLine : linkConfSection.getLines()) {

            String linkId = linkConfLine.getLineElement(0);
            if(solution.hasLinkConfig(linkId)) {
                errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_LINK_CONF,
                    linkConfLine.getLineNumber(), linkId));
                continue;
            }

            LinkConfiguration linkConf = solution.newLinkConfig(linkId);

            String[] modCapacities8Counts = linkConfLine.getLineElement(1).split(
                "\\s+");

            int count = modCapacities8Counts.length / 2;
            for(int i = 0; i < count; ++i) {

                double modCapacity = parse(modCapacities8Counts[2 * i]);
                double modCount = parse(modCapacities8Counts[2 * i + 1]);
                if(linkConf.hasModuleConfig(modCapacity)) {
                    errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_MODULE_CONF,
                        linkConfLine.getLineNumber(), modCapacity, linkId));
                    continue;
                }
                linkConf.addModuleConfig(new ModuleConfiguration(modCapacity,
                    modCount));
            }
        }
    }

    /**
     * Parses the given section object in order to produce the demand 
     * routings for a specific state in the given solution.<br/><br/>
     * 
     * Errors occured while parsing are put into the specified error
     * container.
     * 
     * @param routingSection the section containing the routing data
     * @param solution the solution
     * @param errors the error container
     */
    private static void parseRoutings(Section routingSection, Messages errors,
        Solution solution) {

        String stateName = routingSection.getBeginLine().getLineElement(1);

        OperatingState state = null;
        if(stateName == null) {
            state = OperatingState.NOS;
        }
        else {
            state = OperatingState.getInstance(stateName);
        }

        for(Section routingsForDemand : routingSection.getChildSections()) {
            String demandId = routingsForDemand.getName();

            DemandRouting existingRouting = solution.getRouting(demandId, state);
            if(existingRouting != null && existingRouting.getState().equals(state)) {
                errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_ROUTING,
                    routingsForDemand.getBeginLine().getLineNumber(), demandId,
                    state));
                continue;
            }
            DemandRouting routing = solution.newRouting(demandId, state);

            nextFlowPath: for(Line flowPathLine : routingsForDemand.getLines()) {
                double flowPathValue = parse(flowPathLine.getLineElement(0));
                String[] linkIds = flowPathLine.getLineElement(1).split("\\s+");

                for(String linkId : linkIds) {
                    if(!solution.hasLinkConfig(linkId)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.LINK_ID_IN_ROUTING_PATH_NOT_FOUND,
                            flowPathLine.getLineNumber(), linkId, demandId, state));
                        continue nextFlowPath;
                    }
                }
                solution.newFlowPath(routing, flowPathValue, Arrays.asList(linkIds));
            }
            if(routing.flowPathCount() == 0) {
                errors.add(SNDlibMessages.error(ErrorKeys.NO_PATH_FLOW_IN_ROUTING,
                    routingsForDemand.getBeginLine().getLineNumber(), demandId,
                    state));
            }
        }
    }

    /**
     * Reads the input using the given reader in order to produce a section 
     * object containing the solution data.
     * 
     * @param reader the reader
     * 
     * @return a section containing the solution data
     * 
     * @throws IOException if an IO error occured
     */
    private static Section readSolution(PatternLineReader reader) throws IOException {

        Section solutionSection = new Section("SOLUTION");

        Line line = null;
        while((line = reader.readLine(SOLUTION_SECTION_BEGIN_PATTERN)) != Line.EOF) {

            String childSectionName = line.getLineElement(0);

            Section childSection = new Section(childSectionName, line);

            if(childSectionName.equals(LINK_CONFIGURATIONS)) {
                List<Line> lines = reader.readLinesUntilEOS(LINK_CONF_LINE_PATTERN);
                childSection.addLines(lines);
            }
            else if(childSectionName.equals(ROUTINGS)) {
                line = reader.readLineUntilEOS(DEMAND_ROUTING_BEGIN_PATTERN);

                while(line != Line.EOS && line != Line.EOF) {
                    String demandId = line.getLineElement(0);

                    Section routingSection = new Section(demandId, line);

                    List<Line> lines = reader.readLinesUntilEOS(DEMAND_ROUTING_LINE_PATTERN);
                    routingSection.addLines(lines);

                    childSection.addChildSection(routingSection);

                    line = reader.readLineUntilEOS(DEMAND_ROUTING_BEGIN_PATTERN);
                }
            }
            solutionSection.addChildSection(childSection);
        }

        return solutionSection;
    }

    private SolutionNativeParser() {

        /* cannot be instantiated */
    }

}
