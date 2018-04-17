/*
 * $Id: NetworkXmlWriter.java 631 2011-04-27 08:16:04Z bzfraack $
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
import java.io.Writer;

import sndlib.core.io.SNDlibWriteException;
import sndlib.core.io.xml.castorgen.network.AddModuleEl;
import sndlib.core.io.xml.castorgen.network.AdditionalModulesEl;
import sndlib.core.io.xml.castorgen.network.AdditionalModulesElItem;
import sndlib.core.io.xml.castorgen.network.AdmissiblePathEl;
import sndlib.core.io.xml.castorgen.network.AdmissiblePathElItem;
import sndlib.core.io.xml.castorgen.network.AdmissiblePathsEl;
import sndlib.core.io.xml.castorgen.network.AdmissiblePathsElItem;
import sndlib.core.io.xml.castorgen.network.CoordinatesEl;
import sndlib.core.io.xml.castorgen.network.DemandEl;
import sndlib.core.io.xml.castorgen.network.DemandsEl;
import sndlib.core.io.xml.castorgen.network.DemandsElItem;
import sndlib.core.io.xml.castorgen.network.LinkEl;
import sndlib.core.io.xml.castorgen.network.LinksEl;
import sndlib.core.io.xml.castorgen.network.LinksElItem;
import sndlib.core.io.xml.castorgen.network.NetworkEl;
import sndlib.core.io.xml.castorgen.network.NetworkStructureEl;
import sndlib.core.io.xml.castorgen.network.MetaEl;
import sndlib.core.io.xml.castorgen.network.NodeEl;
import sndlib.core.io.xml.castorgen.network.NodesEl;
import sndlib.core.io.xml.castorgen.network.NodesElItem;
import sndlib.core.io.xml.castorgen.network.PreInstalledModuleEl;
import sndlib.core.io.xml.castorgen.network.types.CoordinatesType;
import sndlib.core.network.AdmissiblePath;
import sndlib.core.network.CapacityModule;
import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.network.NodeCoordinatesType;

import com.atesio.utils.xml.XmlException;
import com.atesio.utils.xml.XmlFacade;

/**
 * This class provides the method to write a 
 * {@link sndlib.core.network.Network} to the XML format of SNDlib.<br/>
 * <br/>
 * 
 * The SNDlib XML format of a network file is described on the 
 * <a target="_blank" 
 * href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html">
 * SNDlib webpage</a><br/><br/>
 * 
 * This class is considered as an internal helper of this package.
 * 
 * @see SNDlibXmlWriter
 * @see sndlib.core.network.Network
 * 
 * @author Roman Klaehne
 */
class NetworkXmlWriter {

    /**
     * Writes the given network to the specified target writer.
     * 
     * @param network the network to write
     * @param target the target to which the network is written
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibWriteException if an error concerning the IO-format
     * occured
     */
    static void writeNetwork(Network network, Writer target) throws IOException,
        SNDlibWriteException {

        NetworkEl networkEl = network2Xsd(network);

        try {
            XmlFacade.marshal(networkEl, target);
        }
        catch (XmlException xmlx) {
            throw new SNDlibWriteException("error marshalling network to xml: "
                + xmlx.getMessage(), xmlx);
        }
    }

    /**
     * Transforms the given <tt>Network</tt> into its corresponding 
     * XML-element.
     * 
     * @param network the network
     * 
     * @return the XML-element corresponding to the specified network
     */
    private static NetworkEl network2Xsd(Network network) {

        NetworkEl networkEl = new NetworkEl();

        NetworkStructureEl networkStructurEl = new NetworkStructureEl();

        LinksEl linksEl = createLinksEl(network);
        NodesEl nodesEl = createNodesEl(network);

        networkStructurEl.setLinksEl(linksEl);
        networkStructurEl.setNodesEl(nodesEl);

        DemandsEl demandsEl = createDemandsEl(network);

        networkEl.setNetworkStructureEl(networkStructurEl);
        networkEl.setDemandsEl(demandsEl);
        
        if( network.getMeta() != null ) {

            MetaEl metaEl = new MetaEl();
            metaEl.setGranularityEl(network.getMeta().getGranularity());
            metaEl.setTimeEl(network.getMeta().getTime());
            metaEl.setUnitEl(network.getMeta().getUnit());
            metaEl.setOriginEl(network.getMeta().getOrigin());
            networkEl.setMetaEl(metaEl);
        }

        return networkEl;
    }

    /**
     * Transforms the <tt>Links</tt>s of the given network into their 
     * corresponding XML-element.
     * 
     * @param network the network
     * 
     * @return the XML-element corresponding to the links of the specified 
     * network
     */
    private static LinksEl createLinksEl(Network network) {

        LinksEl linksEl = new LinksEl();

        for(Link link : network.links()) {

            LinkEl linkEl = new LinkEl();

            linkEl.setId(link.getId());
            linkEl.setSourceEl(link.getFirstNode().getId());
            linkEl.setTargetEl(link.getSecondNode().getId());

            double setupCost = link.getSetupCost();
            if(setupCost != 0.0) {
                linkEl.setSetupCostEl(link.getSetupCost());
            }

            double routingCost = link.getRoutingCost();
            if(routingCost != 0.0) {
                linkEl.setRoutingCostEl(link.getRoutingCost());
            }

            double preCapacity = link.getPreCapacity();
            double preCost = link.getPreCost();
            if(preCapacity != 0.0 || preCost != 0.0) {
                PreInstalledModuleEl preInstModule = new PreInstalledModuleEl();
                preInstModule.setCapacityEl(preCapacity);
                preInstModule.setCostEl(preCost);
                linkEl.setPreInstalledModuleEl(preInstModule);
            }

            if(link.moduleCount() > 0) {
                AdditionalModulesEl addModulesEl = new AdditionalModulesEl();

                for(CapacityModule module : link.modules()) {

                    AddModuleEl addModule = new AddModuleEl();
                    addModule.setCapacityEl(module.getCapacity());
                    addModule.setCostEl(module.getCost());

                    AdditionalModulesElItem moduleItem = new AdditionalModulesElItem();
                    moduleItem.setAddModuleEl(addModule);

                    addModulesEl.addAdditionalModulesElItem(moduleItem);
                }
                linkEl.setAdditionalModulesEl(addModulesEl);
            }
            LinksElItem linksItem = new LinksElItem();
            linksItem.setLinkEl(linkEl);

            linksEl.addLinksElItem(linksItem);
        }
        return linksEl;
    }

    /**
     * Transforms the <tt>Nodes</tt>s of the given network into their 
     * corresponding XML-element.
     * 
     * @param network the network
     * 
     * @return the XML-element corresponding to the nodes of the specified 
     * network
     */
    private static NodesEl createNodesEl(Network network) {

        NodesEl nodesEl = new NodesEl();
        
        if(network.getNodeCoordinatesType() == NodeCoordinatesType.GEOGRAPHICAL) {
            nodesEl.setCoordinatesType(CoordinatesType.GEOGRAPHICAL);
        }
        else {
            nodesEl.setCoordinatesType(CoordinatesType.PIXEL);
        }

        for(Node node : network.nodes()) {

            NodeEl nodeEl = new NodeEl();

            nodeEl.setId(node.getId());

            CoordinatesEl coordinates = new CoordinatesEl();
            coordinates.setXEl(node.getXCoordinate());
            coordinates.setYEl(node.getYCoordinate());
            nodeEl.setCoordinatesEl(coordinates);

            NodesElItem nodesItem = new NodesElItem();
            nodesItem.setNodeEl(nodeEl);
            nodesEl.addNodesElItem(nodesItem);
        }

        return nodesEl;
    }

    /**
     * Transforms the <tt>Demand</tt>s of the given network into their 
     * corresponding XML-element.
     * 
     * @param network the network
     * 
     * @return the XML-element corresponding to the demands of the specified 
     * network
     */
    private static DemandsEl createDemandsEl(Network network) {

        DemandsEl demandsEl = new DemandsEl();

        for(Demand demand : network.demands()) {

            DemandEl demandEl = new DemandEl();

            demandEl.setId(demand.getId());
            demandEl.setDemandValueEl(demand.getDemandValue());
            if(demand.getRoutingUnit() != 1) {
                demandEl.setRoutingUnitEl(demand.getRoutingUnit());
            }

            if(demand.hasMaxPathLength()) {
                demandEl.setMaxPathLengthEl(demand.getMaxPathLength());
            }

            demandEl.setSourceEl(demand.getFirstNode().getId());
            demandEl.setTargetEl(demand.getSecondNode().getId());

            if(demand.admissiblePathCount() > 0) {

                AdmissiblePathsEl pathsEl = new AdmissiblePathsEl();

                for(AdmissiblePath path : demand.admissiblePaths()) {

                    AdmissiblePathEl pathEl = new AdmissiblePathEl();
                    pathEl.setId(path.getId());

                    for(Link link : path.links()) {
                        AdmissiblePathElItem pathElItem = new AdmissiblePathElItem();
                        pathElItem.setLinkIdEl(link.getId());

                        pathEl.addAdmissiblePathElItem(pathElItem);
                    }

                    AdmissiblePathsElItem pathsElItem = new AdmissiblePathsElItem();
                    pathsElItem.setAdmissiblePathEl(pathEl);
                    pathsEl.addAdmissiblePathsElItem(pathsElItem);
                }
                demandEl.setAdmissiblePathsEl(pathsEl);
            }
            DemandsElItem demandsElItem = new DemandsElItem();
            demandsElItem.setDemandEl(demandEl);
            demandsEl.addDemandsElItem(demandsElItem);
        }
        return demandsEl;
    }
}

