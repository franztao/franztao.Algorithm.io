package sndlib.ext.generators.gml;

import java.util.Collection;
import java.util.Properties;

import sndlib.core.network.Node;

import com.atesio.gml.gen.GmlCoordinatesProvider;
import com.atesio.gml.gen.GmlProperties;

/**
 * Base implementation of the GML properties used in SNDlib. 
 * 
 * @author Roman Klaehne
 */
public class BaseGmlProperties extends GmlProperties {

    /**
     * Default constructor.
     */
    public BaseGmlProperties() {

        super();
    }

    /**
     * Copy constructor inheriting the given defaults.
     * 
     * @param defaults the default properties 
     */
    public BaseGmlProperties(BaseGmlProperties defaults) {

        super(defaults);
    }

    /**
     * Loads the GML properties from the given <tt>Properties</tt> instance.
     * 
     * @param props the properties containing the GML properties
     */
    public void load(Properties props) {

        super.load(props);
    }

    /**
     * Returns the appropriate coordinates provider for the given nodes.
     * <br/>
     * If an individual coordinates provider was previously set using 
     * {@link #setCoordinatesProvider(GmlCoordinatesProvider)}, it is 
     * returned. Otherwise a new instance of 
     * {@link GmlCoordinatesFromNodeProvider} is created and returned.
     * 
     * @param nodes the nodes for those the appropriate coordinates provider 
     * is returned
     * 
     * @return the appropriate coordinates provider for the given nodes.
     */
    public GmlCoordinatesProvider getCoordinatesProvider(Collection<Node> nodes) {

        GmlCoordinatesProvider provider = getCoordinatesProvider();

        return provider != null ? provider : new GmlCoordinatesFromNodeProvider(
            nodes);
    }
}
