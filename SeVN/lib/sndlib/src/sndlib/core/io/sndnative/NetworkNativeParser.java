/*
 * $Id: NetworkNativeParser.java 631 2011-04-27 08:16:04Z bzfraack $
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

import static sndlib.core.io.sndnative.NetworkNativeSyntax.ADMISSIBLE_PATHS;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.DEMANDS;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.DEMAND_LINE_PATTERN;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.DEMAND_PATH_BEGIN_PATTERN;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.LINKS;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.LINK_LINE_PATTERN;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.NETWORK_SECTION_BEGIN_PATTERN;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.NETWORK_SECTION_END_PATTERN;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.NODES;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.NODE_LINE_PATTERN;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.META;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.META_LINE_PATTERN;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.PATH_LINE_PATTERN;
import static sndlib.core.io.sndnative.NetworkNativeSyntax.PATH_UNLIMITED;
import static sndlib.core.io.sndnative.RegexConstants.LINE_COMMENT_SIGN;
import static sndlib.core.util.DoubleFormat.parse;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import sndlib.core.io.SNDlibParseException;
import sndlib.core.network.CapacityModule;
import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.network.Meta;
import sndlib.core.network.NodeCoordinatesType;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.ArgChecker;
import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This class provides the method to parse a network file written in the 
 * native format of SNDlib to produce a {@link sndlib.core.network.Network} 
 * instance.<br/>
 * <br/>
 * 
 * The native format of a network file is described on the 
 * <a href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html" 
 * target="_blank">SNDlib webpage</a>.<br/>
 * <br/>
 * 
 * This class is considered as an internal helper of this package.
 * 
 * @see NetworkNativeSyntax
 * @see SNDlibNativeParser
 * @see sndlib.core.network.Network
 * 
 * @author Roman Klaehne
 */
class NetworkNativeParser {

    /**
     * Lists the error messages which may be produced by this parser.
     */
    private static class ErrorKeys {

        final static MessageKey SECTION_NOT_FOUND = new SimpleMessageKey(
            "parser.text.general.error.sectionNotFound");

        final static MessageKey DEMAND_NOT_FOUND = new SimpleMessageKey(
            "parser.text.network.error.demandNotFound");

        final static MessageKey NODE_NOT_FOUND = new SimpleMessageKey(
            "parser.text.network.error.nodeNotFound");

        final static MessageKey LINK_NOT_FOUND = new SimpleMessageKey(
            "parser.text.network.error.linkNotFound");

        final static MessageKey DUPLICATE_NODE_ID = new SimpleMessageKey(
            "parser.text.network.error.duplicateNodeId");

        final static MessageKey DUPLICATE_LINK_ID = new SimpleMessageKey(
            "parser.text.network.error.duplicateLinkId");

        final static MessageKey DUPLICATE_DEMAND_ID = new SimpleMessageKey(
            "parser.text.network.error.duplicateDemandId");

        final static MessageKey DUPLICATE_PATH_ID = new SimpleMessageKey(
            "parser.text.network.error.duplicatePathId");

        final static MessageKey DUPLICATE_MODULE_CAPACITY = new SimpleMessageKey(
            "parser.text.network.error.duplicateModuleCapacity");
    }

    /**
     * Parses the given source to produce a <tt>Network</tt>.
     * 
     * @param source the source to parse the network from
     * 
     * @return the parsed <tt>Network</tt>
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibParseException if an error concerning the syntax or
     * consistency occured
     */
    static Network parseNetwork(LineNumberReader source)
        throws SNDlibParseException, IOException {

        ArgChecker.checkNotNull(source, "source reader");
        Messages errors = new Messages();

        PatternLineReader reader = createReader(source, errors);

        Section networkSection = null;
        try {
            networkSection = readNetwork(reader);
        }
        finally {
            reader.closeReader();
        }

        Network network = new Network();

        /* read sections in proper order */
        Section section = networkSection.getChildSection(META);
        if(section != null) {
            parseMeta(section, network, errors);
        }
        
        section = networkSection.getChildSection(NODES);
        if(section != null) {
            parseNodes(section, network, errors);
        }
        else {
            errors.add(SNDlibMessages.error(ErrorKeys.SECTION_NOT_FOUND, NODES));
        }

        section = networkSection.getChildSection(LINKS);
        if(section != null) {
            parseLinks(section, network, errors);
        }
        else {
            errors.add(SNDlibMessages.error(ErrorKeys.SECTION_NOT_FOUND, LINKS));
        }

        section = networkSection.getChildSection(DEMANDS);
        if(section != null) {
            parseDemands(section, network, errors);
        }
        else {
            errors.add(SNDlibMessages.error(ErrorKeys.SECTION_NOT_FOUND, DEMANDS));
        }

        section = networkSection.getChildSection(ADMISSIBLE_PATHS);
        if(section != null) {
            parseAdmissiblePaths(section, network, errors);
        }

        if(errors.size() > 0) {
            throw new SNDlibParseException(errors);
        }

        return network;
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
        reader.setEndOfSectionPattern(NETWORK_SECTION_END_PATTERN);
        reader.setCommentBeginString(LINE_COMMENT_SIGN);

        return reader;
    }

    /**
     * Parses the given section object in order to produce the admissible 
     * paths of the demands in the given network.<br/>
     * <br/>
     * 
     * Errors occured while parsing are put into the specified error 
     * container.
     * 
     * @param pathSection the section containing the admissible path data
     * @param network the network 
     * @param errors the error container
     */
    private static void parseAdmissiblePaths(Section pathSection, Network network,
        Messages errors) {

        for(Section pathsForDemandSection : pathSection.getChildSections()) {

            String demandId = pathsForDemandSection.getName();
            Demand demand = network.getDemand(demandId);

            if(demand == null) {
                errors.add(SNDlibMessages.error(ErrorKeys.DEMAND_NOT_FOUND,
                    pathsForDemandSection.getBeginLine().getLineNumber(), demandId));
                continue;
            }

            for(Line pathForDemandLine : pathsForDemandSection.getLines()) {

                String pathId = pathForDemandLine.getLineElement(0);
                if(demand.hasAdmissiblePath(pathId)) {
                    errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_PATH_ID,
                        pathForDemandLine.getLineNumber(), pathId, demandId));
                    continue;
                }

                String[] linkIds = pathForDemandLine.getLineElement(1).split("\\s+");
                List<Link> links = new ArrayList<Link>(linkIds.length);

                for(String linkId : linkIds) {
                    Link link = network.getLink(linkId);

                    if(link == null) {
                        errors.add(SNDlibMessages.error(ErrorKeys.LINK_NOT_FOUND,
                            pathForDemandLine.getLineNumber(), linkId, pathId,
                            demandId));
                    }
                    else {
                        links.add(link);
                    }
                }
                if(!links.isEmpty()) {
                    network.newAdmissiblePath(pathId, links, demand);
                }
            }
        }
    }

    /**
     * Parses the given section object in order to produce the demands in
     * the given network.<br/>
     * <br/>
     * 
     * If an error occurs while parsing it is put into the specified error
     * container.
     * 
     * @param demandSection the section containing the demand data
     * @param network the network 
     * @param errors the error container
     */
    private static void parseDemands(Section demandSection, Network network,
        Messages errors) {

        for(Line demandLine : demandSection.getLines()) {

            String demandId = demandLine.getLineElement(0);
            if(network.hasDemand(demandId)) {
                errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_DEMAND_ID,
                    demandLine.getLineNumber(), demandId));
                continue;
            }

            String firstNodeId = demandLine.getLineElement(1);
            Node firstNode = network.getNode(firstNodeId);
            if(firstNode == null) {
                errors.add(SNDlibMessages.error(ErrorKeys.NODE_NOT_FOUND,
                    demandLine.getLineNumber(), firstNodeId, demandId));
                continue;
            }

            String secondNodeId = demandLine.getLineElement(2);
            Node secondNode = network.getNode(secondNodeId);
            if(secondNode == null) {
                errors.add(SNDlibMessages.error(ErrorKeys.NODE_NOT_FOUND,
                    demandLine.getLineNumber(), firstNodeId, demandId));
                continue;
            }

            Demand demand = network.newDemand(demandId, firstNode, secondNode);

            int routingUnit = Integer.parseInt(demandLine.getLineElement(3));
            double demandValue = parse(demandLine.getLineElement(4));

            demand.setRoutingUnit(routingUnit);
            demand.setDemandValue(demandValue);

            String maxPathLength = demandLine.getLineElement(5);
            if(!maxPathLength.equals(PATH_UNLIMITED)) {
                demand.setMaxPathLength(Integer.parseInt(maxPathLength));
            }
        }
    }

    /**
     * Parses the given section object in order to produce the links in
     * the given network.<br/>
     * <br/>
     * 
     * If an error occurs while parsing it is put into the specified error
     * container.
     * 
     * @param linkSection the section containing the link data
     * @param network the network 
     * @param errors the error container
     */
    private static void parseLinks(Section linkSection, Network network,
        Messages errors) {

        for(Line linkLine : linkSection.getLines()) {

            String linkId = linkLine.getLineElement(0);
            if(network.hasLink(linkId)) {
                errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_LINK_ID,
                    linkLine.getLineNumber(), linkId));
                continue;
            }

            String firstNodeId = linkLine.getLineElement(1);
            Node firstNode = network.getNode(firstNodeId);
            if(firstNode == null) {
                errors.add(SNDlibMessages.error(ErrorKeys.NODE_NOT_FOUND,
                    linkLine.getLineNumber(), firstNodeId, linkId));
                continue;
            }

            String secondNodeId = linkLine.getLineElement(2);
            Node secondNode = network.getNode(secondNodeId);
            if(secondNode == null) {
                errors.add(SNDlibMessages.error(ErrorKeys.NODE_NOT_FOUND,
                    linkLine.getLineNumber(), secondNodeId, linkId));
                continue;
            }

            Link link = network.newLink(linkId, firstNode, secondNode);

            double preCapacity = parse(linkLine.getLineElement(3));
            double preCost = parse(linkLine.getLineElement(4));
            double unitRoutingCost = parse(linkLine.getLineElement(5));
            double setupCost = parse(linkLine.getLineElement(6));

            link.setPreCapacity(preCapacity);
            link.setPreCost(preCost);
            link.setRoutingCost(unitRoutingCost);
            link.setSetupCost(setupCost);

            String[] capacities8Costs = linkLine.getLineElement(7).split("\\s+");

            int count = capacities8Costs.length / 2;
            for(int i = 0; i < count; ++i) {

                double capacity = parse(capacities8Costs[2 * i]);
                if(link.hasModule(capacity)) {
                    errors.add(SNDlibMessages.error(
                        ErrorKeys.DUPLICATE_MODULE_CAPACITY,
                        linkLine.getLineNumber(), capacity, linkId));
                    continue;
                }

                double cost = parse(capacities8Costs[2 * i + 1]);
                CapacityModule module = new CapacityModule(capacity, cost);
                link.addModule(module);
            }
        }
    }

    /**
     * Parses the given section object in order to produce the meta data in
     * the given network.<br/>
     * <br/>
     * 
     * If an error occurs while parsing it is put into the specified error
     * container.
     * 
     * @param metaSection the section containing the node data
     * @param network the network 
     * @param errors the error container
     */
    private static void parseMeta(Section metaSection, Network network,
        Messages errors) {

    	Meta meta_ = new Meta();
    	
    	
        for(Line metaLine : metaSection.getLines()) {

            String whichMeta = metaLine.getLineElement(0);
            String content = metaLine.getLineElement(1);
            
            if(whichMeta.equals("granularity")){
            	meta_.setGranularity(content);
            }
            if(whichMeta.equals("time"))
            	meta_.setTime(content);
            if(whichMeta.equals("unit"))
            	meta_.setUnit(content);
            if(whichMeta.equals("origin"))
            	meta_.setOrigin(content);
            
        }
        network.setMeta(meta_);
    }
    
    
    /**
     * Parses the given section object in order to produce the nodes in
     * the given network.<br/>
     * <br/>
     * 
     * If an error occurs while parsing it is put into the specified error
     * container.
     * 
     * @param nodeSection the section containing the node data
     * @param network the network 
     * @param errors the error container
     */
    private static void parseNodes(Section nodeSection, Network network,
        Messages errors) {

        NodeCoordinatesType nodeCoordType = NodeCoordinatesType.GEOGRAPHICAL;

        for(Line nodeLine : nodeSection.getLines()) {

            String nodeId = nodeLine.getLineElement(0);
            if(network.hasNode(nodeId)) {
                errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_NODE_ID,
                    nodeLine.getLineNumber(), nodeId));
                continue;
            }

            Node node = network.newNode(nodeId);

            String xCoord = nodeLine.getLineElement(1);
            String yCoord = nodeLine.getLineElement(2);

            if(xCoord != null) {
                double x = parse(xCoord);
                double y = parse(yCoord);

                if(x <= -180 || x > 180 || y < -90 || y > 90) {
                    nodeCoordType = NodeCoordinatesType.PIXEL;
                }
                node.setCoordinates(x, y);
            }
        }
        network.setNodeCoordinatesType(nodeCoordType);
    }

    /**
     * Reads the input using the given reader in order to produce a section 
     * object containing the network data.
     * 
     * @param reader the reader
     * 
     * @return a section containing the network data
     * 
     * @throws IOException if an IO error occured
     */
    private static Section readNetwork(PatternLineReader reader) throws IOException {

        Section networkSection = new Section("NETWORK");

        Line line = null;
        while((line = reader.readLine(NETWORK_SECTION_BEGIN_PATTERN)) != Line.EOF) {
            String childSectionName = line.getLineElement(0);

            Section childSection = networkSection.getChildSection(childSectionName);
            if(childSection == null) {
                childSection = new Section(childSectionName);
            }

            if(childSectionName.equals(NODES)) {
                readSection(childSection, NODE_LINE_PATTERN, reader);
            }
            else if(childSectionName.equals(LINKS)) {
                readSection(childSection, LINK_LINE_PATTERN, reader);
            }
            else if(childSectionName.equals(META)) {
                readSection(childSection, META_LINE_PATTERN, reader);
            }
            else if(childSectionName.equals(DEMANDS)) {
                readSection(childSection, DEMAND_LINE_PATTERN, reader);
            }
            else if(childSectionName.equals(ADMISSIBLE_PATHS)) {
                readAdmissiblePathSection(childSection, reader);
            }

            networkSection.addChildSection(childSection);
        }

        return networkSection;
    }

    /**
     * Reads the input using the given reader in order to produce the section 
     * objects containing the admissible path data. The produced section 
     * objects are added as child sections to the specified section.
     * 
     * @param section the admissible path section
     * @param reader the reader
     * 
     * @throws IOException if an IO error occured
     */
    private static void readAdmissiblePathSection(Section section,
        PatternLineReader reader) throws IOException {

        Line line = reader.readLineUntilEOS(DEMAND_PATH_BEGIN_PATTERN);
        while(line != Line.EOS && line != Line.EOF) {

            String demandId = line.getLineElement(0);

            Section pathsForDemandSection = section.getChildSection(demandId);
            if(pathsForDemandSection == null) {
                pathsForDemandSection = new Section(demandId, line);
            }

            List<Line> lines = reader.readLinesUntilEOS(PATH_LINE_PATTERN);
            pathsForDemandSection.addLines(lines);

            section.addChildSection(pathsForDemandSection);

            line = reader.readLineUntilEOS(DEMAND_PATH_BEGIN_PATTERN);
        }
    }

    /**
     * Reads lines from the input using the specified pattern. The lines 
     * are added to the given section.
     * 
     * @param section the current section
     * @param linePattern the pattern for the lines
     * @param reader the reader
     */
    private static void readSection(Section section, Pattern linePattern,
        PatternLineReader reader) throws IOException {

        List<Line> lines = reader.readLinesUntilEOS(linePattern);
        section.addLines(lines);
    }

    private NetworkNativeParser() {

        /* cannot be instantiated */
    }

}
