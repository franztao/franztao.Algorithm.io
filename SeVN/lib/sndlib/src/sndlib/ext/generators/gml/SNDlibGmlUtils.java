package sndlib.ext.generators.gml;

import java.util.Collection;

import sndlib.core.network.Node;
import sndlib.core.problem.Problem;

import com.atesio.gml.gen.GmlUtils;

/**
 * 
 * @author Roman Klaehne
 *
 */
class SNDlibGmlUtils extends GmlUtils {

    static int calculateMaxElementIdLength(Collection<Node> nodes, int maxLength) {

        int maxIdLength = 0;
        for(Node node : nodes) {
            maxIdLength = Math.max(maxIdLength, node.getId().length());
        }

        return Math.min(maxIdLength, maxLength);
    }

    static String abbreviate(String s, int maxLength) {

        if(s.length() <= maxLength) {
            return s;
        }

        return s.substring(0, maxLength - 3) + "...";
    }

    static int calculateNodeLabelFontSize(int nodeHeight, int nodeWidth,
        int maxLabelLength) {

        return (int) Math.round(Math.min(nodeHeight, nodeWidth / maxLabelLength
            * 1.3));
    }

    static boolean getIsDirected(Problem problem) {

        switch (problem.getLinkModel()) {
        case DIRECTED:
        case BIDIRECTED:
            return true;
        case UNDIRECTED:
            return false;
        }

        throw new AssertionError("unknown link model: " + problem.getLinkModel());
    }
}
