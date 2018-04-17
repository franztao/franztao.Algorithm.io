/*
 * $Id: NetworkXmlParser.java 631 2011-04-27 08:16:04Z bzfraack $
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
import java.util.ArrayList;
import java.util.List;

import sndlib.core.io.SNDlibParseException;
import sndlib.core.io.xml.castorgen.network.AddModuleEl;
import sndlib.core.io.xml.castorgen.network.AdditionalModulesEl;
import sndlib.core.io.xml.castorgen.network.AdmissiblePathEl;
import sndlib.core.io.xml.castorgen.network.AdmissiblePathElItem;
import sndlib.core.io.xml.castorgen.network.AdmissiblePathsEl;
import sndlib.core.io.xml.castorgen.network.CoordinatesEl;
import sndlib.core.io.xml.castorgen.network.DemandEl;
import sndlib.core.io.xml.castorgen.network.DemandsEl;
import sndlib.core.io.xml.castorgen.network.LinkEl;
import sndlib.core.io.xml.castorgen.network.LinksEl;
import sndlib.core.io.xml.castorgen.network.NetworkEl;
import sndlib.core.io.xml.castorgen.network.NetworkStructureEl;
import sndlib.core.io.xml.castorgen.network.MetaEl;
import sndlib.core.io.xml.castorgen.network.NodeEl;
import sndlib.core.io.xml.castorgen.network.NodesEl;
import sndlib.core.io.xml.castorgen.network.PreInstalledModuleEl;
import sndlib.core.io.xml.castorgen.network.types.CoordinatesType;
import sndlib.core.network.CapacityModule;
import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.network.NodeCoordinatesType;
import sndlib.core.network.Meta;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;
import com.atesio.utils.xml.XmlException;
import com.atesio.utils.xml.XmlFacade;
import com.atesio.utils.xml.XmlValidationException;

/**
 * This class provides the method to a parse a network file written in the XML
 * format of SNDlib to produce a {@link sndlib.core.network.Network} instance.
 * <br/><br/>
 *
 * The SNDlib XML format of a network file is described on the
 * <a target="_blank"
 * href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html">
 * SNDlib webpage</a><br/><br/>
 *
 * This class is considered as an internal helper of this package.
 *
 * @see SNDlibXmlParser
 * @see sndlib.core.network.Network
 *
 * @author Roman Klaehne
 */
class NetworkXmlParser {

    /**
     * Lists the errors which may be produced by this parser.
     */
    private static class ErrorKeys {

        final static MessageKey NODE_NOT_FOUND = new SimpleMessageKey(
            "parser.xml.network.error.nodeNotFound");

        final static MessageKey INVALID_GEO_LONGITUDE = new SimpleMessageKey(
            "parser.xml.network.error.invalidGeographicalLongitude");

        final static MessageKey INVALID_GEO_LATITUDE = new SimpleMessageKey(
            "parser.xml.network.error.invalidGeographicalLatitude");

        final static MessageKey LINK_NOT_FOUND = new SimpleMessageKey(
            "parser.xml.network.error.linkNotFound");

        final static MessageKey DUPLICATE_NODE_ID = new SimpleMessageKey(
            "parser.xml.network.error.duplicateNodeId");

        final static MessageKey DUPLICATE_LINK_ID = new SimpleMessageKey(
            "parser.xml.network.error.duplicateLinkId");

        final static MessageKey DUPLICATE_DEMAND_ID = new SimpleMessageKey(
            "parser.xml.network.error.duplicateDemandId");

        final static MessageKey DUPLICATE_PATH_ID = new SimpleMessageKey(
            "parser.xml.network.error.duplicatePathId");

        final static MessageKey DUPLICATE_MODULE_CAPACITY = new SimpleMessageKey(
            "parser.xml.network.error.duplicateModuleCapacity");
    }

    /**
     * Parses the given source in order to produce a <tt>Network</tt>.
     *
     * @param source the source to parse the network from
     *
     * @return the parsed <tt>Network</tt>
     *
     * @throws IOException if an IO error occured
     * @throws SNDlibParseException if an error concerning the syntax or
     * consistency occured
     */
    static Network parseNetwork(Reader source) throws IOException,
        SNDlibParseException {

        NetworkEl networkEl = null;

        try {
            networkEl = XmlFacade.unmarshal(source, NetworkEl.class,
                SNDlibXmlValidator.NETWORK_SCHEMA);
        }
        catch (XmlException xmlx) {
            throw new SNDlibParseException("error parsing network xml: "
                + xmlx.getMessage(), xmlx);
        }
        catch (XmlValidationException xmlvx) {
            throw new SNDlibParseException(xmlvx.getErrors());
        }

        Messages errors = new Messages();
        Network network = xsd2Network(networkEl, errors);

        if(errors.size() > 0) {
            throw new SNDlibParseException(errors);
        }

        return network;
    }

    /**
     * Transforms the given XML-element into a <tt>Network</tt> object.<br/>
     * <br/>
     *
     * Errors occured while transforming (e.g. regarding the consistency) are
     * put to the given error container.
     *
     * @param networkEl the XML-element
     * @param errors the error container
     *
     * @return a <tt>Network</tt> instance
     */
    private static Network xsd2Network(NetworkEl networkEl, Messages errors) {

        Network network = new Network();

        NetworkStructureEl structur = networkEl.getNetworkStructureEl();

        putNodesIntoNetwork(network, structur.getNodesEl(), errors);
        putLinksIntoNetwork(network, structur.getLinksEl(), errors);

        DemandsEl demands = networkEl.getDemandsEl();
        putDemandsIntoNetwork(network, demands, errors);
        
        MetaEl    meta    = networkEl.getMetaEl();
        if(meta != null) {
            
            putMetaIntoNetwork(network, meta, errors);
        }
         

        return network;
    }
    
    /**
     * Transforms the given XML-element into properties of a network and
     * puts them into the given network.<br/>
     * <br/>
     *
     * Errors occured while transforming (e.g. regarding the consistency) are
     * put to the given error container.
     *
     * @param network the network
     * @param metaEl the XML-element containing the meta data
     * @param errors the error container
     */
    private static void putMetaIntoNetwork(Network network, MetaEl metaEl,
        Messages errors) {

        String granularity = metaEl.getGranularityEl();
        String time        = metaEl.getTimeEl();
        String unit        = metaEl.getUnitEl();
        String origin      = metaEl.getOriginEl();
        
        Meta meta = new Meta();
        
        if( granularity != null ) meta.setGranularity(granularity);
        if( time != null )        meta.setTime(time);
        if( unit != null )        meta.setUnit(unit);
        if( origin != null )      meta.setOrigin(origin);
        
        network.setMeta(meta);
    }

    /**
     * Transforms the given XML-element into a set of <tt>Demand</tt>s and
     * puts them into the given network.<br/>
     * <br/>
     *
     * Errors occured while transforming (e.g. regarding the consistency) are
     * put to the given error container.
     *
     * @param network the network
     * @param demandsEl the XML-element containing the demand data
     * @param errors the error container
     */
    private static void putDemandsIntoNetwork(Network network, DemandsEl demandsEl,
        Messages errors) {

        for(int i = 0; i < demandsEl.getDemandsElItemCount(); ++i) {
            DemandEl demandEl = demandsEl.getDemandsElItem(i).getDemandEl();

            String demandId = demandEl.getId();
            if(network.hasDemand(demandId)) {
                errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_DEMAND_ID,
                    demandId));
                continue;
            }

            String sourceNodeId = demandEl.getSourceEl();
            Node source = network.getNode(sourceNodeId);
            if(source == null) {
                errors.add(SNDlibMessages.error(ErrorKeys.NODE_NOT_FOUND,
                    sourceNodeId, demandId));
                continue;
            }
            String targetNodeId = demandEl.getTargetEl();
            Node target = network.getNode(targetNodeId);
            if(target == null) {
                errors.add(SNDlibMessages.error(ErrorKeys.NODE_NOT_FOUND,
                    targetNodeId, demandId));
                continue;
            }

            Demand demand = network.newDemand(demandEl.getId(), source, target);
            demand.setDemandValue(demandEl.getDemandValueEl());

            if(demandEl.hasRoutingUnitEl()) {
                demand.setRoutingUnit(demandEl.getRoutingUnitEl());
            }
            if(demandEl.hasMaxPathLengthEl()) {
                demand.setMaxPathLength(demandEl.getMaxPathLengthEl());
            }

            AdmissiblePathsEl admissiblePaths = demandEl.getAdmissiblePathsEl();
            if(admissiblePaths != null) {
                for(int j = 0; j < admissiblePaths.getAdmissiblePathsElItemCount(); ++j) {
                    AdmissiblePathEl pathEl = admissiblePaths.getAdmissiblePathsElItem(
                        j).getAdmissiblePathEl();

                    String pathId = pathEl.getId();
                    if(demand.hasAdmissiblePath(pathId)) {
                        errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_PATH_ID,
                            pathId, demandId));
                        continue;
                    }

                    List<Link> links = new ArrayList<Link>();
                    for(int k = 0; k < pathEl.getAdmissiblePathElItemCount(); ++k) {
                        AdmissiblePathElItem pathItem = pathEl.getAdmissiblePathElItem(k);

                        String linkId = pathItem.getLinkIdEl();
                        Link link = network.getLink(linkId);
                        if(link == null) {
                            errors.add(SNDlibMessages.error(ErrorKeys.LINK_NOT_FOUND,
                                linkId, pathId, demandId));
                            continue;
                        }
                        else {
                            links.add(link);
                        }
                    }
                    network.newAdmissiblePath(pathId, links, demand);
                }
            }
        }
    }

    /**
     * Transforms the given XML-element into a set of <tt>Link</tt>s and
     * puts them into the given network.<br/>
     * <br/>
     *
     * Errors occured while transforming (e.g. regarding the consistency) are
     * put to the given error container.
     *
     * @param network the network
     * @param linksEl the XML-element containing the link data
     * @param errors the error container
     */
    private static void putLinksIntoNetwork(Network network, LinksEl linksEl,
        Messages errors) {

        for(int i = 0; i < linksEl.getLinksElItemCount(); ++i) {
            LinkEl linkEl = linksEl.getLinksElItem(i).getLinkEl();

            String linkId = linkEl.getId();
            if(network.hasLink(linkId)) {
                errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_LINK_ID, linkId));
                continue;
            }
            String sourceNodeId = linkEl.getSourceEl();
            Node source = network.getNode(sourceNodeId);
            if(source == null) {
                errors.add(SNDlibMessages.error(ErrorKeys.NODE_NOT_FOUND,
                    sourceNodeId, linkId));
                continue;
            }
            String targetNodeId = linkEl.getTargetEl();
            Node target = network.getNode(targetNodeId);
            if(target == null) {
                errors.add(SNDlibMessages.error(ErrorKeys.NODE_NOT_FOUND,
                    targetNodeId, linkId));
                continue;
            }

            Link link = network.newLink(linkEl.getId(), source, target);
            if(linkEl.hasSetupCostEl()) {
                link.setSetupCost(linkEl.getSetupCostEl());
            }
            if(linkEl.hasRoutingCostEl()) {
                link.setRoutingCost(linkEl.getRoutingCostEl());
            }

            PreInstalledModuleEl preInstModule = linkEl.getPreInstalledModuleEl();
            if(preInstModule != null) {
                link.setPreCapacity(preInstModule.getCapacityEl());
                link.setPreCost(preInstModule.getCostEl());
            }

            AdditionalModulesEl modules = linkEl.getAdditionalModulesEl();
            if(modules != null) {
                for(int j = 0; j < modules.getAdditionalModulesElItemCount(); ++j) {
                    AddModuleEl module = modules.getAdditionalModulesElItem(j).getAddModuleEl();

                    double capacity = module.getCapacityEl();
                    if(link.hasModule(capacity)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.DUPLICATE_MODULE_CAPACITY, capacity, linkId));
                        continue;
                    }
                    link.addModule(new CapacityModule(capacity, module.getCostEl()));
                }
            }
        }
    }

    /**
     * Transforms the given XML-element into a set of <tt>Node</tt>s and
     * puts them into the given network.<br/>
     * <br/>
     *
     * Errors occured while transforming (e.g. regarding the consistency) are
     * put to the given error container.
     *
     * @param network the network
     * @param nodesEl the XML-element containing the node data
     * @param errors the error container
     */
    private static void putNodesIntoNetwork(Network network, NodesEl nodesEl,
        Messages errors) {

        if(nodesEl.getCoordinatesType() == CoordinatesType.GEOGRAPHICAL) {
            network.setNodeCoordinatesType(NodeCoordinatesType.GEOGRAPHICAL);
        }
        else {
            network.setNodeCoordinatesType(NodeCoordinatesType.PIXEL);
        }

        for(int i = 0; i < nodesEl.getNodesElItemCount(); ++i) {
            NodeEl nodeEl = nodesEl.getNodesElItem(i).getNodeEl();

            String nodeId = nodeEl.getId();
            if(network.hasNode(nodeId)) {
                errors.add(SNDlibMessages.error(ErrorKeys.DUPLICATE_NODE_ID, nodeId));
                continue;
            }
            Node node = network.newNode(nodeEl.getId());

            CoordinatesEl coords = nodeEl.getCoordinatesEl();
            if(coords != null) {
                double x = coords.getXEl();
                double y = coords.getYEl();

                boolean coordOk = true;
                if(network.getNodeCoordinatesType() == NodeCoordinatesType.GEOGRAPHICAL) {
                    if(x <= -180 || x > 180) {
                        coordOk = false;
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.INVALID_GEO_LONGITUDE, nodeId, x));
                    }
                    if(y < -90 || y > 90) {
                        coordOk = false;
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.INVALID_GEO_LATITUDE, nodeId, y));
                    }
                }
                if(coordOk) {
                    node.setCoordinates(x, y);
                }
            }
        }
    }
}
