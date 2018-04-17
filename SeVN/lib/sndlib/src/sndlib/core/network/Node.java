/*
 * $Id: Node.java 99 2006-07-04 14:05:16Z bzforlow $
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

package sndlib.core.network;

/**
 * This class represents a single node in a {@link Network}.
 * <br/><br/>
 * 
 * Besides its ID a node can possesses coordinates, which can be either simple 
 * pixel coordinates or geographical, with the x-coordinate to be the longitude 
 * and the y-coordinate the latitude.<br/>
 * <br/>
 * 
 * For constructing a new <tt>Node</tt> a parent <tt>Network</tt> is needed.   
 * 
 * @see Network
 * @see Network#newNode(String)
 * @see Network#newNode(String, double, double)
 * 
 * @author Roman Klaehne
 */
final public class Node {

    /**
     * The ID of this node.
     */
    private String _id;

    /**
     * The type of this node's coordinates.
     */
    private NodeCoordinatesType _coordType;

    /**
     * The x-coordinate (longitude) of this node.
     */
    private double _xCoord;

    /**
     * The y-coordinate (latitude) of this node.
     */
    private double _yCoord;

    /**
     * Constructs a new node with the specified ID and coordinates. 
     * 
     * @param nodeId the ID of the node
     * @param coordType the coordinates type 
     * @param xCoord the x-coordinate (longitude)
     * @param yCoord the y-coordinate (latitude)
     */
    Node(String nodeId, NodeCoordinatesType coordType, double xCoord, double yCoord) {

        _id = nodeId;

        checkCoordinates(coordType, xCoord, yCoord);
        _coordType = coordType;
        _xCoord = xCoord;
        _yCoord = yCoord;
    }

    /**
     * Checks whether the given coordinates are valid with respect to the 
     * given coordinates type.<br/>
     * <br/>
     * 
     * If the given coordinates type is 
     * {@link NodeCoordinatesType#GEOGRAPHICAL} then the given x-coordinate
     * must be in the range (-180, 180] and the given y-coordinate in the 
     * range [-90, 90].
     * 
     * @throws IllegalArgumentException if the given coordinates are not valid
     * with respect to the given coordinates type
     */
    static void checkCoordinates(NodeCoordinatesType coordType, double xCoord,
        double yCoord) throws IllegalArgumentException {

        if(coordType == NodeCoordinatesType.GEOGRAPHICAL) {
            if(xCoord <= -180 || xCoord > 180) {
                throw new IllegalArgumentException(
                    "geographical longitude must be in range (-180,180]");
            }

            if(yCoord < -90 || yCoord > 90) {
                throw new IllegalArgumentException(
                    "geographical latitude must be in range [-90,90]");
            }
        }
    }

    /**
     * Returns the ID of this node.
     * 
     * @return the ID of this node
     */
    public String getId() {

        return _id;
    }

    /** 
     * Returns the x-coordinate (longitude) of this node. 
     * 
     * @return the x-coordinates (longitude) of this node.
     */
    public double getXCoordinate() {

        return _xCoord;
    }

    /** 
     * Returns the y-coordinate (latitude) of this node. 
     * 
     * @return the y-coordinates (latitude) of this node.
     */
    public double getYCoordinate() {

        return _yCoord;
    }

    /**
     * Sets the type of this node's coordinates.
     * 
     * @param coordType the coordinates type
     * 
     * @throws IllegalArgumentException if the coordinates type is 
     * {@link NodeCoordinatesType#GEOGRAPHICAL} and the coordinates
     * of this node cannot be interpreted as geographical longitude and 
     * latitude
     */
    void setCoordinateType(NodeCoordinatesType coordType) {

        checkCoordinates(coordType, _xCoord, _yCoord);
        _coordType = coordType;
    }

    /**
     * Returns the type of this node's coordinates.
     * 
     * @return the type of this node's coordinates
     */
    public NodeCoordinatesType getCoordinatesType() {

        return _coordType;
    }

    /**
     * Sets the coordinates of this node.
     * 
     * @param xCoord the x-coordinate (longitude)
     * @param yCoord the y-coordinate (latitude)
     * 
     * @throws IllegalArgumentException if this node's coordinates type is 
     * {@link NodeCoordinatesType#GEOGRAPHICAL} and the given x-coordinate
     * (resp. y-coordinate) is not in the range (-180, 180] (resp. [-90, 90]) 
     */
    public void setCoordinates(double xCoord, double yCoord) {

        checkCoordinates(_coordType, xCoord, yCoord);
        _xCoord = xCoord;
        _yCoord = yCoord;
    }

    /**
     * Returns a textual representation of this node.
     * 
     * @return a textual representation of this node.
     */
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append("node [\n");
        result.append(" id           = " + getId() + "\n");

        if(_coordType == NodeCoordinatesType.GEOGRAPHICAL) {
            result.append(" longitude    = " + _xCoord + "\n");
            result.append(" latitude     = " + _yCoord + "\n");
        }
        else {
            result.append(" x-coordinate = " + _xCoord + "\n");
            result.append(" y-coordinate = " + _yCoord + "\n");
        }
        result.append("]");

        return result.toString();
    }

}
