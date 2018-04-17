package sndlib.ext.generators.gml;

import java.awt.Color;
import java.util.Collection;

import sndlib.core.model.LinkModel;
import sndlib.core.network.Link;
import sndlib.core.network.Node;

import com.atesio.gml.base.GmlDocument;
import com.atesio.gml.base.GmlObjectValues.ArrowStyle;
import com.atesio.gml.gen.GmlCoordinatesProvider;
import com.atesio.gml.gen.GmlGraph;
import com.atesio.gml.gen.GmlLink;

/**
 * This class is a simple helper class representing a GML graph. It 
 * provides various {@code addNode} and {@code addLink} methods to 
 * easy build up a GML document.
 * 
 * @author Roman Klaehne
 */
class SNDlibGmlGraph extends GmlGraph {

    private BaseGmlProperties _gmlProps;

    private GmlCoordinatesProvider _coordinatesProvider;

    /**
     * Constructs a new GML graph.
     * 
     * @param name the name of the graph
     * @param directed {@code true} if the graph is directed; {@code false} 
     * otherwise
     * @param coordinatesProvider the coordinates provider to be used for 
     * determining the node coordinates
     * @param gmlProps the GML properties to be used
     */
    public SNDlibGmlGraph(String name, boolean directed,
        GmlCoordinatesProvider coordinatesProvider, BaseGmlProperties gmlProps) {

        super(name, directed);

        _gmlProps = gmlProps;
        _coordinatesProvider = coordinatesProvider;
    }

    /**
     * Adds the given nodes to this GML graph.
     * 
     * @param nodes the nodes to be added
     */
    public void addNodes(Collection<Node> nodes) {

        int maxLabelLength = SNDlibGmlUtils.calculateMaxElementIdLength(nodes, 10);

        int nodeLabelFontSize = SNDlibGmlUtils.calculateNodeLabelFontSize(
            _gmlProps.getNodeHeight(), _gmlProps.getNodeWidth(), maxLabelLength);

        for(Node node : nodes) {
            String elementId = node.getId();
            String nodeLabel = (maxLabelLength < 4) ? elementId
                : SNDlibGmlUtils.abbreviate(elementId, maxLabelLength);

            addNode(elementId, nodeLabel, nodeLabelFontSize,
                _coordinatesProvider.getCoordinates(elementId));
        }
    }

    /**
     * Adds the given links to this GML graph.
     * 
     * @param links the links to be added
     * @param linkWidth the width to be used
     * @param linkColor the color to be used
     * @param linkStyle the style to be used
     * @param linkModel the link model to determine whether the links will
     * have source/target arrows in the resulting GML
     */
    public void addLinks(Collection<Link> links, int linkWidth, Color linkColor,
        String linkStyle, LinkModel linkModel) {

        for(Link link : links) {
            addLink(link, linkWidth, linkColor, linkStyle, linkModel);
        }
    }

    /**
     * Adds a single link to this GML graph.
     * 
     * @param link the link to be added
     * @param linkWidth the width to be used
     * @param linkColor the color to be used
     * @param linkStyle the style to be used
     * @param linkModel the link model to determine whether the link will
     * have source/target arrows in the resulting GML
     * 
     * @return the created GML link
     */
    public GmlLink addLink(Link link, int linkWidth, Color linkColor,
        String linkStyle, LinkModel linkModel) {

        GmlLink gmlLink = addLink(link.getId(), link.getFirstNode().getId(),
            link.getSecondNode().getId(), linkWidth, linkColor, linkStyle);

        String sourceArrowStyle = (linkModel == LinkModel.BIDIRECTED)
            ? ArrowStyle.STANDARD : null;
        String targetArrowStyle = (linkModel == LinkModel.BIDIRECTED || linkModel == LinkModel.DIRECTED)
            ? ArrowStyle.STANDARD : null;

        gmlLink.setSourceArrowStyle(sourceArrowStyle);
        gmlLink.setTargetArrowStyle(targetArrowStyle);

        return gmlLink;
    }

    /**
     * Creates a GML document from this GML graph. The node coordinates are 
     * scaled before creating the GML document if and only if the 
     * coordinates provider of this graph needs scaling, indicated by 
     * {@link GmlCoordinatesProvider#needsScaling()}.
     * 
     * @param creator the creator of the GML document.
     * 
     * @return the created GML document
     */
    public GmlDocument createGmlDocument(String creator) {

        if(_coordinatesProvider.needsScaling()) {
            scaleNodeCoordiantes(_gmlProps.getMinNodeInterDistance());
        }

        return super.createGmlDocument(creator, _gmlProps);
    }
}
