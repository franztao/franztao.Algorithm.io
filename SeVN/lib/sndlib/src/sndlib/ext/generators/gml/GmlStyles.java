package sndlib.ext.generators.gml;

import com.atesio.gml.base.GmlObjectValues;

/**
 * This class collects some style constants which can be addressed 
 * in the <a target="_blank" 
 * href="http://www.infosun.fim.uni-passau.de/Graphlet/GML/gml-tr.html">
 * GML</a> property settings. These constants are derived from the 
 * abilities of the free graph editor 
 * <a href="http://www.yworks.com/en/products_yed_about.htm" 
 * target="_blank">yEd</a>.
 * 
 * @see ProblemGmlProperties
 * @see SolutionGmlProperties
 * 
 * @author Roman Klaehne
 */
public class GmlStyles {

    /**
     * Collects some link style constants.
     * 
     * @see ProblemGmlProperties#setProblemLinkStyle(String)
     * @see SolutionGmlProperties#setSolutionLinkStyle(String)
     */
    public static class LinkStyle {

        public static final String DASHED = GmlObjectValues.LinkStyle.DASHED;

        public static final String DOTTED = GmlObjectValues.LinkStyle.DOTTED;

        public static final String STANDARD = GmlObjectValues.LinkStyle.STANDARD;
    }

    /**
     * Collects some link width constants. The values are in the range
     * {@code 1} to {@code 7}.
     * 
     * @see ProblemGmlProperties#setProblemLinkWidth(int)
     * @see SolutionGmlProperties#setSolutionLinkWidth(int)
     * @see SolutionGmlProperties#setLinkWidthUnused(int)
     */
    public static class LinkWidth {

        public static final int THINNEST = 1;

        public static final int VERY_THIN = 2;

        public static final int THIN = 3;

        public static final int MEDIUM = 4;

        public static final int THICK = 5;

        public static final int VERY_THICK = 6;

        public static final int THICKEST = 7;
    }

    /**
     * Collects some node shape constants.
     * 
     * @see BaseGmlProperties#setNodeShape(String)
     */
    public static class NodeShape {

        public static final String RECTANGLE = GmlObjectValues.NodeShape.RECTANGLE;

        public static final String ROUND_RECTANGLE = GmlObjectValues.NodeShape.ROUND_RECTANGLE;

        public static final String ELLIPSE = GmlObjectValues.NodeShape.ELLIPSE;

        public static final String TRAPEZOID = GmlObjectValues.NodeShape.TRAPEZOID;

        public static final String DIAMOND = GmlObjectValues.NodeShape.DIAMOND;

        public static final String TRIANGLE = GmlObjectValues.NodeShape.TRIANGLE;

        public static final String OCTAGON = GmlObjectValues.NodeShape.OCTAGON;
    }
}
