package sndlib.ext.generators.gml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.atesio.gml.gen.GmlCoordinatesProvider;
import com.atesio.gml.gen.GmlProperties;

/**
 * This class aggregates the {@link ProblemGmlProperties} and the 
 * {@link SolutionGmlProperties}. It is used in conjunction with the 
 * {@link SNDlibGmlGenerator}.
 * 
 * @see SNDlibGmlGenerator
 * 
 * @author Roman Klaehne
 */
public class SNDlibGmlProperties {

    /**
     * The default properties.
     */
    private static final SNDlibGmlProperties DEFAULTS;

    static {
        DEFAULTS = new SNDlibGmlProperties();
    }

    /**
     * Returns the default properties as specified in <tt>gml.properties</tt>.
     * 
     * @return the default properties
     */
    public static SNDlibGmlProperties getDefaults() {

        return new SNDlibGmlProperties(DEFAULTS);
    }

    private ProblemGmlProperties _problemGmlProps;

    private SolutionGmlProperties _solutionGmlProps;

    /**
     * Default Constructor. The initial state of the properties is
     * determined by the {@link ProblemGmlProperties#getDefaults()} resp.
     * {@link SolutionGmlProperties#getDefaults()}.
     */
    public SNDlibGmlProperties() {

        _problemGmlProps = ProblemGmlProperties.getDefaults();
        _solutionGmlProps = SolutionGmlProperties.getDefaults();
    }

    /**
     * Copy constructor inheriting the given defaults.
     * 
     * @param defaults the default properties 
     */
    public SNDlibGmlProperties(SNDlibGmlProperties defaults) {

        _problemGmlProps = new ProblemGmlProperties(defaults._problemGmlProps);
        _solutionGmlProps = new SolutionGmlProperties(defaults._solutionGmlProps);
    }

    /**
     * Loads the GML properties from the given input stream.
     * 
     * @param source the input stream containing the GML properties
     * 
     * @see Properties#load(InputStream)
     */
    public void load(InputStream source) throws IOException {

        Properties props = new Properties();
        props.load(source);

        load(props);
    }

    /**
     * Loads the GML properties from the given <tt>Properties</tt> instance.
     * 
     * @param props the properties containing the GML properties
     */
    public void load(Properties props) {

        _problemGmlProps.load(props);
        _solutionGmlProps.load(props);
    }

    /**
     * Returns the problem GML properties.
     * 
     * @return the problem GML properties
     */
    public ProblemGmlProperties getProblemGmlProperties() {

        return _problemGmlProps;
    }

    /**
     * Returns the solution GML properties.
     * 
     * @return the solution GML properties
     */
    public SolutionGmlProperties getSolutionGmlProperties() {

        return _solutionGmlProps;
    }

    /**
     * Sets the specified coordinates provider to both, the solution and 
     * the problem GML properties.
     * 
     * @param coordsProvider the coordinates to be used when generating 
     * problem or solution GMLs
     * 
     * @see GmlProperties#setCoordinatesProvider(GmlCoordinatesProvider)
     */
    public void setCoordinatesProvider(GmlCoordinatesProvider coordsProvider) {

        _problemGmlProps.setCoordinatesProvider(coordsProvider);
        _solutionGmlProps.setCoordinatesProvider(coordsProvider);
    }
}
