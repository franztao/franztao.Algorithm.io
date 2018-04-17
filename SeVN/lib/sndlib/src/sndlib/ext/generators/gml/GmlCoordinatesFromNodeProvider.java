package sndlib.ext.generators.gml;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sndlib.core.network.Node;
import sndlib.core.network.NodeCoordinatesType;

import com.atesio.gml.gen.GmlCoordinatesProvider;
import com.atesio.gml.gen.GmlUtils;

/**
 * Simple implementation of the {@link GmlCoordinatesProvider} interface.
 * <br/>
 * The coordinates returned in {@link #getCoordinates(String)} are those 
 * provided by the corresponding {@link Node} instance, i.e. by its methods 
 * {@link Node#getXCoordinate()} and {@link Node#getYCoordinate()}.<br/>
 * <br/>
 * 
 * If the node has {@link NodeCoordinatesType#GEOGRAPHICAL} coordinates
 * a mercator projection is applied to convert them to screen coordinates. 
 *
 * @see com.atesio.gml.gen.GmlUtils#projectGeographicalCoordinates(double, double)
 * 
 * @author Roman Klaehne
 */
public class GmlCoordinatesFromNodeProvider implements GmlCoordinatesProvider {

    /**
     * Maps the node ID's to the corresponding node instances.
     */
    private Map<String, Node> _nodes;

    /**
     * Constructs a new coordinates provider for the given nodes.
     * 
     * @param nodes the nodes for those the coordinates provider 
     * is constructed
     */
    public GmlCoordinatesFromNodeProvider(Collection<Node> nodes) {

        _nodes = new HashMap<String, Node>();

        for(Node node : nodes) {
            _nodes.put(node.getId(), node);
        }
    }

    /**
     * Returns the coordinates of the node with the specified ID.<br/>
     * If the node has {@link NodeCoordinatesType#GEOGRAPHICAL} coordinates
     * a mercator projection is applied to convert them to screen coordinates. 
     *
     * @param nodeId the ID of the node
     * 
     * @return the coordinates of the node
     *   
     * @see com.atesio.gml.gen.GmlUtils#projectGeographicalCoordinates(double, double)
     * 
     */
    public Point2D.Double getCoordinates(String nodeId) {

        Node node = _nodes.get(nodeId);

        return node != null ? getCoordinates(node) : new Point2D.Double(0, 0);
    }

    /**
     * Returns <tt>true</tt> because the coordinates provided
     * by this implementation need to be scaled to achieve a proper GML 
     * output.<br/>
     * The coordinates first will be moved to the center of the coordinates
     * space and then scaled according to {@code minNodeInterDistance}, to
     * be defined by {@link BaseGmlProperties#setMinNodeInterDistance(int)}.
     * 
     * @return <tt>true</tt>
     * 
     * @see com.atesio.gml.gen.GmlUtils#scaleCoordinates(Collection, double)
     */
    public boolean needsScaling() {

        return true;
    }

    /**
     * Returns the coordinates for the given node.<br/>
     * If the node has {@link NodeCoordinatesType#GEOGRAPHICAL} coordinates
     * a mercator projection is applied to convert them to screen coordinates. 
     * 
     * @param node the node
     * 
     * @return the coordinates of the node  
     */
    private Point2D.Double getCoordinates(Node node) {

        Point2D.Double gmlCoords = new Point2D.Double();

        if(node.getCoordinatesType() == NodeCoordinatesType.GEOGRAPHICAL) {
            Point2D.Double projectedCoords = GmlUtils.projectGeographicalCoordinates(
                node.getXCoordinate(), node.getYCoordinate());
            gmlCoords.setLocation(projectedCoords);
        }
        else {
            gmlCoords.setLocation(node.getXCoordinate(), node.getYCoordinate());
        }
        return gmlCoords;
    }
}
