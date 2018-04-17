package sndlib.ext.generators.gml;

import java.awt.Color;
import java.util.Properties;

import sndlib.core.problem.Problem;

import com.atesio.gml.gen.GmlUtils;

/**
 * This class collects the properties to be used when generating a 
 * <a target="_blank" 
 * href="http://www.infosun.fim.uni-passau.de/Graphlet/GML/gml-tr.html">
 * GML</a> for a {@link Problem}.<br/><br/>
 * 
 * Note: The support of property values resp. their ranges depends on 
 * the used visualizer. We can recommend the free graph editor 
 * <a href="http://www.yworks.com/en/products_yed_about.htm" 
 * target="_blank">yEd</a>, which is a simple, easy to use tool for 
 * viewing, editing and analyzing GML graphs.<br/>
 * Some style constants supported by yEd are collected in {@link GmlStyles}.
 *
 * @see GmlStyles
 * @see ProblemGmlGenerator
 * 
 * @author Roman Klaehne
 */
public class ProblemGmlProperties extends BaseGmlProperties {

    /**
     * The default properties.
     */
    private static final ProblemGmlProperties DEFAULTS;

    static {
        DEFAULTS = new ProblemGmlProperties();
        GmlUtils.loadProperties(DEFAULTS, "gml.properties");
    }

    /**
     * Returns the default properties as specified in 
     * {@code gml.properties}.
     * 
     * @return the default properties
     */
    public static ProblemGmlProperties getDefaults() {

        return new ProblemGmlProperties(DEFAULTS);
    }

    private int _problemLinkWidth;

    private Color _problemLinkColor;

    private String _problemLinkStyle;

    /**
     * Default Constructor.
     */
    public ProblemGmlProperties() {

        super();
    }

    /**
     * Copy constructor inheriting the given defaults.
     * 
     * @param defaults the default properties 
     */
    public ProblemGmlProperties(ProblemGmlProperties defaults) {

        super(defaults);

        _problemLinkWidth = defaults._problemLinkWidth;
        _problemLinkColor = defaults._problemLinkColor;
        _problemLinkStyle = defaults._problemLinkStyle;
    }

    /**
     * Loads the GML properties from the given {@code Properties} 
     * instance.
     * 
     * @param props the properties containing the GML properties
     */
    public void load(Properties props) {

        super.load(props);

        _problemLinkWidth = GmlUtils.defaultIfEmptyOrInvalid(
            props.getProperty("gml.problem.link.width"), _problemLinkWidth);
        _problemLinkColor = GmlUtils.defaultIfEmptyOrInvalid(
            props.getProperty("gml.problem.link.color"), _problemLinkColor);
        _problemLinkStyle = GmlUtils.defaultIfEmpty(
            props.getProperty("gml.problem.link.style"), _problemLinkStyle);
    }

    /**
     * Returns the link color to be used when generating a GML for a 
     * {@link Problem}.<br/><br/>
     * 
     * If no individual color was set, explicitly by 
     * {@link #setProblemLinkColor(Color)} or implicitly by 
     * {@link #load(Properties)}, the default color provided by 
     * {@link #getLinkColor()} is returned.
     * 
     * @return the link color 
     */
    public Color getProblemLinkColor() {

        return (_problemLinkColor != null) ? _problemLinkColor : getLinkColor();
    }

    /**
     * Sets the link color to be used when generating a GML for a 
     * {@link Problem}.<br/>
     *
     * @param problemLinkColor the link color
     */
    public void setProblemLinkColor(Color problemLinkColor) {

        _problemLinkColor = problemLinkColor;
    }

    /**
     * Returns the link style to be used when generating a GML for a 
     * {@link Problem}.<br/>
     * <br/>
     * 
     * If no individual style was set, explicitly by 
     * {@link #setProblemLinkStyle(String)} or implicitly by 
     * {@link #load(Properties)}, the default style provided by 
     * {@link #getLinkStyle()} is returned.
     * 
     * @return the link style
     */
    public String getProblemLinkStyle() {

        return (_problemLinkStyle != null) ? _problemLinkStyle : getLinkStyle();
    }

    /**
     * Sets the link style to be used when generating a GML for a 
     * {@link Problem}.<br/><br/>
     * 
     * Note: The available link styles depend on the used visualizer. 
     * If using yEd, some link style constants are collected in 
     * {@link GmlStyles.LinkStyle}.
     *
     * @param problemLinkStyle the link style
     */
    public void setProblemLinkStyle(String problemLinkStyle) {

        _problemLinkStyle = problemLinkStyle;
    }

    /**
     * Returns the link width to be used when generating a GML for a 
     * {@link Problem}.<br/><br/>
     * 
     * If no individual width was set, explicitly by 
     * {@link #setProblemLinkWidth(int)} or implicitly by 
     * {@link #load(Properties)}, the default width provided by 
     * {@link #getLinkWidth()} is returned.
     * 
     * @return the link width
     */
    public int getProblemLinkWidth() {

        return (_problemLinkWidth > 0) ? _problemLinkWidth : getLinkWidth();
    }

    /**
     * Sets the link width to be used when generating a GML for a 
     * {@link Problem}.<br/><br/>
     * 
     * Note: The available link widths depend on the used visualizer.
     * If using yEd, some link width constants are collected in 
     * {@link GmlStyles.LinkWidth}.
     * 
     * @param problemLinkWidth the link width
     */
    public void setProblemLinkWidth(int problemLinkWidth) {

        _problemLinkWidth = problemLinkWidth;
    }
}
