/*
 * $Id$
 *
 * Copyright (c) 2005-2008 by Konrad-Zuse-Zentrum fuer Informationstechnik Berlin. 
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
package sndlib.ext.generators.gml;

import java.awt.Color;
import java.util.Properties;

import sndlib.core.problem.SolvedProblem;

import com.atesio.gml.base.GmlObjectValues.LinkStyle;
import com.atesio.gml.gen.GmlUtils;

/**
 * This class collects the properties to be used when generating a 
 * <a target="_blank" 
 * href="http://www.infosun.fim.uni-passau.de/Graphlet/GML/gml-tr.html">
 * GML</a> for a {@link SolvedProblem}.<br/><br/>
 *
 * Note: The support of property values resp. their ranges depends on 
 * the used visualizer. We can recommend the free graph editor 
 * <a href="http://www.yworks.com/en/products_yed_about.htm" 
 * target="_blank">yEd</a>, which is a simple, easy to use tool for 
 * viewing, editing and analyzing GML graphs.<br/>
 * Some style constants supported by yEd are collected in {@link GmlStyles}.
 * 
 * @see GmlStyles
 * @see SolutionGmlGenerator
 * 
 * @author Roman Klaehne
 */
public class SolutionGmlProperties extends BaseGmlProperties {

    /**
     * The default properties.
     */
    private static final SolutionGmlProperties DEFAULTS;

    static {
        DEFAULTS = new SolutionGmlProperties();
        GmlUtils.loadProperties(DEFAULTS, "gml.properties");
    }

    /**
     * Returns the default properties as specified in 
     * {@code gml.properties}.
     * 
     * @return the default properties
     */
    public static SolutionGmlProperties getDefaults() {

        return new SolutionGmlProperties(DEFAULTS);
    }

    /**
     * Default property values.
     */
    public static class Defaults {

        /**
         * The default link color gradient type is {@link GradientType#USAGE}.
         */
        public static final GradientType LINK_COLOR_GRADIENT_TYPE = GradientType.USAGE;

        /**
         * The default link color gradient resolution is {@code 8}.
         */
        public static final int LINK_COLOR_GRADIENT_RESOLUTION = 8;

        /**
         * The default start-color is {@link Color#RED}.
         */
        public static final Color LINK_COLOR_GRADIENT_START = Color.RED;

        /**
         * The default end-color is {@link Color#BLACK}.
         */
        public static final Color LINK_COLOR_GRADIENT_END = Color.BLACK;

        /**
         * The default link width gradient type is {@link GradientType#CAPACITY}.
         */
        public static final GradientType LINK_WIDTH_GRADIENT_TYPE = GradientType.CAPACITY;

        /**
         * The default link width gradient resolution is {@code 7}.
         */
        public static final int LINK_WIDTH_GRADIENT_RESOLUTION = 7;

        /**
         * The default color to be used for unused links is 
         * {@link Color#LIGHT_GRAY}.
         */
        public static final Color LINK_COLOR_UNUSED = Color.LIGHT_GRAY;

        /**
         * The default link width for unused links is {@code 1}.
         */
        public static final int LINK_WIDTH_UNUSED = 1;

        /**
         * The default link style to be used for unused links is 
         * {@link LinkStyle#DASHED}.
         */
        public static final String LINK_STYLE_UNUSED = LinkStyle.DASHED;

        /**
         * Blocked constructor
         */
        private Defaults() {

        }
    }

    /**
     * Enumerates the available gradient types.<br/>
     * The links contained in a solution topology will be classified by 
     * a specific property (gradient type) such as the usage/load or the 
     * installed capacity. This classification can be used to determine the 
     * color and/or the width of the links in a solution GML.<br/>
     * 
     * @see SolutionGmlProperties#getLinkColorGradientType()
     * @see SolutionGmlProperties#getLinkWidthGradientType()
     * 
     * @author Roman Klaehne
     */
    public static enum GradientType {

        /**
         * The links are classified by their usage/load.
         */
        USAGE,

        /**
         * The links are classified by their installed capacity.
         */
        CAPACITY,

        /**
         * The links are classified by their cost (capacity and 
         * routing cost).
         */
        COST,

        /**
         * No classification is done. All links share the same 
         * color and width (specified by 
         * {@link SolutionGmlProperties#getSolutionLinkColor()} and
         * {@link SolutionGmlProperties#getSolutionLinkWidth()}).
         */
        CONSTANT;
    }

    private GradientType _linkColorGradientType;

    private int _linkColorGradientResolution;

    private Color _linkColorGradientStart;

    private Color _linkColorGradientEnd;

    private GradientType _linkWidthGradientType;

    private int _linkWidthGradientResolution;

    private Color _linkColorUnused;

    private int _linkWidthUnused;

    private String _linkStyleUnused;

    private Color _solutionLinkColor;

    private int _solutionLinkWidth;

    private String _solutionLinkStyle;

    /**
     * Default Constructor.
     */
    public SolutionGmlProperties() {

        super();
    }

    /**
     * Copy constructor inheriting the given defaults.
     * 
     * @param defaults the default properties 
     */
    public SolutionGmlProperties(SolutionGmlProperties defaults) {

        super(defaults);

        _linkColorGradientType = defaults._linkColorGradientType;
        _linkColorGradientStart = defaults._linkColorGradientStart;
        _linkColorGradientEnd = defaults._linkColorGradientEnd;
        _linkColorGradientResolution = defaults._linkColorGradientResolution;
        _linkWidthGradientType = defaults._linkWidthGradientType;
        _linkWidthGradientResolution = defaults._linkWidthGradientResolution;
        _linkColorUnused = defaults._linkColorUnused;
        _linkWidthUnused = defaults._linkWidthUnused;
        _linkStyleUnused = defaults._linkStyleUnused;
        _solutionLinkColor = defaults._solutionLinkColor;
        _solutionLinkWidth = defaults._solutionLinkWidth;
        _solutionLinkStyle = defaults._solutionLinkStyle;
    }

    /**
     * Loads the GML properties from the given <tt>Properties</tt> instance.
     * 
     * @param props the properties containing the GML properties
     */
    public void load(Properties props) {

        super.load(props);

        _linkColorGradientType = GmlUtils.defaultIfEmptyOrInvalid(
            GradientType.class,
            props.getProperty("gml.solution.link.color.gradient"),
            _linkColorGradientType);

        _linkColorGradientStart = GmlUtils.defaultIfEmptyOrInvalid(
            props.getProperty("gml.solution.link.color.gradient.start"),
            _linkColorGradientStart);

        _linkColorGradientEnd = GmlUtils.defaultIfEmptyOrInvalid(
            props.getProperty("gml.solution.link.color.gradient.end"),
            _linkColorGradientEnd);

        _linkColorGradientResolution = GmlUtils.defaultIfEmptyOrInvalid(
            props.getProperty("gml.solution.link.color.gradient.res"),
            _linkColorGradientResolution);

        _linkColorUnused = GmlUtils.defaultIfEmptyOrInvalid(
            props.getProperty("gml.solution.link.unused.color"), _linkColorUnused);

        _linkWidthUnused = GmlUtils.defaultIfEmptyOrInvalid(
            props.getProperty("gml.solution.link.unused.width"), _linkWidthUnused);

        _linkStyleUnused = GmlUtils.defaultIfEmpty(
            props.getProperty("gml.solution.link.unused.style"), _linkStyleUnused);

        _linkWidthGradientType = GmlUtils.defaultIfEmptyOrInvalid(
            GradientType.class,
            props.getProperty("gml.solution.link.width.gradient"),
            _linkWidthGradientType);

        _linkWidthGradientResolution = GmlUtils.defaultIfEmptyOrInvalid(
            props.getProperty("gml.solution.link.width.gradient.res"),
            _linkWidthGradientResolution);

        _solutionLinkColor = GmlUtils.defaultIfEmptyOrInvalid(
            props.getProperty("gml.solution.link.color"), _solutionLinkColor);

        _solutionLinkWidth = GmlUtils.defaultIfEmptyOrInvalid(
            props.getProperty("gml.solution.link.width"), _solutionLinkWidth);

        _solutionLinkStyle = GmlUtils.defaultIfEmpty(
            props.getProperty("gml.solution.link.style"), _solutionLinkStyle);
    }

    /**
     * Returns the gradient type to be used when determining the color 
     * of links in the solution GML.<br/> 
     * See {@link #setLinkColorGradientType(GradientType)} for more 
     * information about what the gradient type means.<br/><br/>
     * 
     * If no individual gradient type was set, explicitely by 
     * {@link #setLinkColorGradientType(GradientType)} or implicitely 
     * by {@link #load(Properties)}, the default type 
     * {@link Defaults#LINK_COLOR_GRADIENT_TYPE} is returned.
     * 
     * @return the link color gradient type 
     *  
     * @see #getLinkColorGradientResolution()
     * @see #getLinkColorGradientStart()
     * @see #getLinkColorGradientEnd()
     */
    public GradientType getLinkColorGradientType() {

        return (_linkColorGradientType != null) ? _linkColorGradientType
            : Defaults.LINK_COLOR_GRADIENT_TYPE;
    }

    /**
     * Sets the gradient type to be used when determining the color of 
     * links in the solution GML.<br/>
     * The link color gradient is defined by its start/end-color and its 
     * resolution. The lower the value of a link regarding the gradient 
     * type the closer is its color to the start-color.<br/>
     * Given, e.g., red/black as start/end-color and assuming the
     * {@link GradientType#USAGE} gradient type, then the color of links 
     * with low usage will be some kind of red while black links indicate 
     * full use of the capacity.
     * 
     * @param linkColorGradientType the gradient type 
     * 
     * @see #setLinkColorGradientResolution(int)
     * @see #setLinkColorGradientStart(Color)
     * @see #setLinkColorGradientEnd(Color)
     */
    public void setLinkColorGradientType(GradientType linkColorGradientType) {

        _linkColorGradientType = linkColorGradientType;
    }

    /**
     * Returns the start-color of the link color gradient.<br/><br/>
     * 
     * If no start-color was set, explicitely by 
     * {@link #setLinkColorGradientStart(Color)} or implicitely 
     * by {@link #load(Properties)}, the default start-color  
     * {@link Defaults#LINK_COLOR_GRADIENT_START} is returned.
     * 
     * @return the start-color of the link color gradient
     *  
     * @see #getLinkColorGradientType()
     * @see #getLinkColorGradientResolution()
     * @see #getLinkColorGradientEnd()
     */
    public Color getLinkColorGradientStart() {

        return (_linkColorGradientStart != null) ? _linkColorGradientStart
            : Defaults.LINK_COLOR_GRADIENT_START;
    }

    /**
     * Sets the start-color of the link color gradient.
     * 
     * @param linkColorGradientStart the start-color of the link color 
     * gradient
     *  
     * @see #setLinkColorGradientType(GradientType)
     * @see #setLinkColorGradientResolution(int)
     * @see #setLinkColorGradientEnd(Color)
     */
    public void setLinkColorGradientStart(Color linkColorGradientStart) {

        _linkColorGradientStart = linkColorGradientStart;
    }

    /**
     * Returns the end-color of the link color gradient.<br/><br/>
     * 
     * If no end-color was set, explicitely by 
     * {@link #setLinkColorGradientEnd(Color)} or implicitely 
     * by {@link #load(Properties)}, the default end-color 
     * {@link Defaults#LINK_COLOR_GRADIENT_END} is returned.
     * 
     * @return the end-color of the link color gradient
     *  
     * @see #getLinkColorGradientType()
     * @see #getLinkColorGradientResolution()
     * @see #getLinkColorGradientStart()
     */
    public Color getLinkColorGradientEnd() {

        return (_linkColorGradientEnd != null) ? _linkColorGradientEnd
            : Defaults.LINK_COLOR_GRADIENT_END;
    }

    /**
     * Sets the end-color of the link color gradient.
     * 
     * @param linkColorGradientEnd the end-color of the link color 
     * gradient
     *  
     * @see #setLinkColorGradientType(GradientType)
     * @see #setLinkColorGradientResolution(int)
     * @see #setLinkColorGradientStart(Color)
     */
    public void setLinkColorGradientEnd(Color linkColorGradientEnd) {

        _linkColorGradientEnd = linkColorGradientEnd;
    }

    /**
     * Returns the resolution of the link color gradient.<br/><br/>
     *  
     * If no resolution was set, explicitely by 
     * {@link #setLinkColorGradientResolution(int)} or implicitely 
     * by {@link #load(Properties)}, the default 
     * {@link Defaults#LINK_COLOR_GRADIENT_RESOLUTION} is returned.
     * 
     * @return the resolution of the link color gradient
     * 
     * @see #setLinkColorGradientResolution(int)
     * @see #getLinkColorGradientType()
     * @see #getLinkColorGradientStart()
     * @see #getLinkColorGradientEnd()
     */
    public int getLinkColorGradientResolution() {

        return (_linkColorGradientResolution > 0) ? _linkColorGradientResolution
            : Defaults.LINK_COLOR_GRADIENT_RESOLUTION;
    }

    /**
     * Sets the resolution of the link color gradient.<br/>
     * The resolution defines the length of the color sequence which 
     * constitutes the color gradient. This color sequence starts at 
     * the start-color and ends at the end-color, to be defined by 
     * {@link #setLinkColorGradientStart(Color)} resp. 
     * {@link #setLinkColorGradientEnd(Color)}.<br/><br/>
     * 
     * Given, e.g., a resolution of {@code r}, the links are distributed 
     * on {@code r} classes, according to the property of the gradient 
     * type. To each of these classes one of the {@code r} colors is 
     * assigned.<br/> 
     * If {@code r == 1}, all links share the same color, namely the 
     * start-color.<br/>
     * If {@code r < 1}, it is treated as it would be {@code 1}.
     * 
     * @param linkColorGradientResolution the resolution of the link 
     * color gradient
     *  
     * @see #setLinkColorGradientType(GradientType)
     * @see #setLinkColorGradientStart(Color)
     * @see #setLinkColorGradientEnd(Color)
     */
    public void setLinkColorGradientResolution(int linkColorGradientResolution) {

        _linkColorGradientResolution = linkColorGradientResolution;
    }

    /**
     * Returns the style to be used for links which are not contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).<br/><br/>
     * 
     * If no link style was set, explicitely by 
     * {@link #setLinkStyleUnused(String)} or implicitely 
     * by {@link #load(Properties)}, the default 
     * {@link Defaults#LINK_STYLE_UNUSED} is returned.
     * 
     * @return the style to be used for unused links
     */
    public String getSolutionLinkStyleUnused() {

        return (_linkStyleUnused != null) ? _linkStyleUnused : LinkStyle.DASHED;
    }

    /**
     * Sets the style to be used for links which are not contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).<br/><br/>
     * 
     * Note: The available link styles depend on the used visualizer. 
     * If using yEd, some link style constants are collected in 
     * {@link GmlStyles.LinkStyle}.
     * 
     * @param linkStyleUnused style to be used for unused links
     */
    public void setLinkStyleUnused(String linkStyleUnused) {

        _linkStyleUnused = linkStyleUnused;
    }

    /**
     * Returns the color to be used for links which are not contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).<br/><br/>
     * 
     * If no link color was set, explicitely by 
     * {@link #setLinkColorUnused(Color)} or implicitely 
     * by {@link #load(Properties)}, the default 
     * {@link Defaults#LINK_COLOR_UNUSED} is returned.
     * 
     * @return the color to be used for unused links
     */
    public Color getLinkColorUnused() {

        return (_linkColorUnused != null) ? _linkColorUnused : Color.LIGHT_GRAY;
    }

    /**
     * Sets the color to be used for links which are not contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).
     * 
     * @param linkColorUnused color to be used for unused links
     */
    public void setLinkColorUnused(Color linkColorUnused) {

        _linkColorUnused = linkColorUnused;
    }

    /**
     * Returns the width to be used for links which are not contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).<br/><br/>
     * 
     * If no width was set, explicitely by 
     * {@link #setLinkWidthUnused(int)} or implicitely 
     * by {@link #load(Properties)}, the default 
     * {@link Defaults#LINK_WIDTH_UNUSED} is returned.
     * 
     * @return the width to be used for unused links
     */
    public int getLinkWidthUnused() {

        return (_linkWidthUnused > 0) ? _linkWidthUnused : 1;
    }

    /**
     * Sets the width to be used for links not contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).<br/><br/>
     * 
     * Note: The available link widths depend on the used visualizer.
     * If using yEd, some link width constants are collected in 
     * {@link GmlStyles.LinkWidth}.
     * 
     * @param linkWidthUnused width to be used for unused links
     */
    public void setLinkWidthUnused(int linkWidthUnused) {

        _linkWidthUnused = linkWidthUnused;
    }

    /**
     * Returns the style to be used for links which are contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).<br/>
     * <br/>
     * 
     * If no style was set, explicitely by 
     * {@link #setSolutionLinkStyle(String)} or implicitely 
     * by {@link #load(Properties)}, the default 
     * {@link #getLinkStyle()} is returned.
     * 
     * @return the style to be used for links which are contained in the 
     * solution topology
     */
    public String getSolutionLinkStyle() {

        return (_solutionLinkStyle != null) ? _solutionLinkStyle : getLinkStyle();
    }

    /**
     * Sets the style to be used for links which are contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).<br/><br/>
     * 
     * Note: The available link styles depend on the used visualizer. 
     * If using yEd, some link style constants are collected in 
     * {@link GmlStyles.LinkStyle}.
     * 
     * @param solutionLinkStyle style to be used for links which are 
     * contained in the solution topology
     */
    public void setSolutionLinkStyle(String solutionLinkStyle) {

        _solutionLinkStyle = solutionLinkStyle;
    }

    /**
     * Returns the width to be used for links which are contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).<br/>
     * This width is only used if the link width gradient type is set to
     * {@link GradientType#CONSTANT}.<br/><br/>
     * 
     * If no width was set, explicitely by 
     * {@link #setSolutionLinkWidth(int)} or implicitely 
     * by {@link #load(Properties)}, the default 
     * {@link #getLinkWidth()} is returned.
     * 
     * @return the width to be used for links which are contained in the 
     * solution topology (if the link width gradient is 
     * {@link GradientType#CONSTANT})
     * 
     * @see #getLinkWidthGradientType()
     */
    public int getSolutionLinkWidth() {

        return (_solutionLinkWidth > 0) ? _solutionLinkWidth : getLinkWidth();
    }

    /**
     * Sets the width to be used for links which are contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).<br/>
     * This width is only used if the link width gradient type is set to
     * {@link GradientType#CONSTANT}.<br/><br/>
     * 
     * Note: The available link widths depend on the used visualizer.
     * If using yEd, some link width constants are collected in 
     * {@link GmlStyles.LinkWidth}.
     * 
     * @param solutionLinkWidth the width to be used for links which are 
     * contained in the solution topology (if the link width gradient is 
     * {@link GradientType#CONSTANT})
     * 
     * @see #getLinkWidthGradientType()
     */
    public void setSolutionLinkWidth(int solutionLinkWidth) {

        _solutionLinkWidth = solutionLinkWidth;
    }

    /**
     * Returns the color to be used for links which are contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).<br/>
     * This color is only used if the link color gradient type is set to
     * {@link GradientType#CONSTANT}.<br/><br/>
     * 
     * If no color was set, explicitely by 
     * {@link #setSolutionLinkColor(Color)} or implicitely 
     * by {@link #load(Properties)}, the default 
     * {@link #getLinkColor()} is returned.
     * 
     * @return the color to be used for links which are contained in 
     * the solution topology (if the link color gradient is 
     * {@link GradientType#CONSTANT})
     * 
     * @see #getLinkColorGradientType()
     */
    public Color getSolutionLinkColor() {

        return (_solutionLinkColor != null) ? _solutionLinkColor : getLinkColor();
    }

    /**
     * Sets the color to be used for links which are contained 
     * in the solution topology. A link is contained in the solution 
     * topology if it has any capacity installed (pre-installed or 
     * additional).<br/>
     * This color is only used if the link color gradient type is set to
     * {@link GradientType#CONSTANT}.
     * 
     * @param solutionLinkColor the color to be used for links which are
     * contained in the solution topology (if the link color gradient is 
     * {@link GradientType#CONSTANT})
     * 
     * @see #getLinkColorGradientType()
     */
    public void setSolutionLinkColor(Color solutionLinkColor) {

        _solutionLinkColor = solutionLinkColor;
    }

    /**
     * Returns the gradient type to be used when determining the width of 
     * links in the solution GML.<br/> 
     * See {@link #setLinkWidthGradientType(GradientType)} for more 
     * information about what the gradient type means.<br/><br/>
     * 
     * If no individual gradient type was set, explicitely by 
     * {@link #setLinkWidthGradientType(GradientType)} or implicitely 
     * by {@link #load(Properties)}, the default type 
     * {@link Defaults#LINK_WIDTH_GRADIENT_TYPE} is returned.
     * 
     * @return the link width gradient type 
     *  
     * @see #getLinkWidthGradientResolution()
     */
    public GradientType getLinkWidthGradientType() {

        return (_linkWidthGradientType != null) ? _linkWidthGradientType
            : GradientType.CAPACITY;
    }

    /**
     * Sets the gradient type to be used when determining the width of 
     * links in the solution GML.<br/>
     * The link width gradient is only defined by its resolution.<br/>
     * Assuming, e.g., a resolution of {@code r} and the 
     * {@link GradientType#CAPACITY} gradient type then the link width
     * vary from {@code 1} to {@code r}, according to the capacity which 
     * is installed on the link. The more capacity is installed on the link 
     * the thicker will be its width in the solution GML. A width near 
     * {@code 1} is assigned to links with low capacity while highest 
     * capacity links will get the thickest width {@code r}.
     * 
     * @param linkWidthGradientType the link width gradient type 
     * 
     * @see #setLinkWidthGradientResolution(int)
     */
    public void setLinkWidthGradientType(GradientType linkWidthGradientType) {

        _linkWidthGradientType = linkWidthGradientType;
    }

    /**
     * Returns the resolution of the link width gradient.<br/><br/>
     * 
     * If no resolution was set, explicitely by 
     * {@link #setLinkWidthGradientResolution(int)} or implicitely 
     * by {@link #load(Properties)}, the default 
     * {@link Defaults#LINK_WIDTH_GRADIENT_RESOLUTION} is returned.
     * 
     * @return the resolution of the link width gradient
     *  
     * @see #setLinkWidthGradientType(GradientType)
     * @see #getLinkWidthGradientType()
     */
    public int getLinkWidthGradientResolution() {

        return (_linkWidthGradientResolution > 0) ? _linkWidthGradientResolution : 6;
    }

    /**
     * Sets the resolution of the link width gradient.<br/>
     * Given, e.g., a resolution of {@code r}, the links are distributed 
     * on {@code r} classes, according to the property of the gradient 
     * type. To each of these classes one of the widths varying from 
     * {@code 1} to {@code r} is assigned.<br/>
     * 
     * @param linkWidthGradientResolution the resolution of the width 
     * gradient
     *  
     * @see #setLinkWidthGradientType(GradientType)
     */
    public void setLinkWidthGradientResolution(int linkWidthGradientResolution) {

        _linkWidthGradientResolution = linkWidthGradientResolution;
    }
}
