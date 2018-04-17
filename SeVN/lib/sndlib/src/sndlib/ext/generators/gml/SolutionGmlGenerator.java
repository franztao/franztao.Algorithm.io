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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sndlib.core.model.FixedChargeModel;
import sndlib.core.model.LinkModel;
import sndlib.core.network.Link;
import sndlib.core.problem.SolvedProblem;
import sndlib.core.solution.LinkConfiguration;
import sndlib.core.util.MaximumLinkFlow;
import sndlib.core.util.SolutionUtils;
import sndlib.ext.generators.gml.SolutionGmlProperties.GradientType;

import com.atesio.gml.base.GmlWriter;
import com.atesio.gml.gen.GmlCoordinatesProvider;
import com.atesio.utils.ArgChecker;

/**
 * This class generates a <a target="_blank" 
 * href="http://www.infosun.fim.uni-passau.de/Graphlet/GML/gml-tr.html">
 * GML</a> document for a given {@link SolvedProblem}.
 * 
 * @see SolutionGmlProperties
 * @see SNDlibGmlProperties
 * @see SNDlibGmlGenerator
 * 
 * @author Roman Klaehne
 */
public class SolutionGmlGenerator {

    private static final SolutionGmlGenerator DEFAULT_INSTANCE = new SolutionGmlGenerator(
        SolutionGmlProperties.getDefaults());

    /**
     * Returns the default solution GML generator, initialized with the 
     * default properties as returned by 
     * {@link SolutionGmlProperties#getDefaults()}.
     * 
     * @return the default problem GML generator 
     */
    public static SolutionGmlGenerator getDefaultInstance() {

        return DEFAULT_INSTANCE;
    }

    /**
     * Creates a new solution GML generator, initialized with the specified 
     * properties.<br/><br/>
     * 
     * This method is equivalent to 
     * <tt>getInstance(gmlProps.getSolutionGmlProperties())</tt>
     * 
     * @param gmlProps the GML properties to be used when generating GML 
     * documents 
     * 
     * @return the solution GML generator using the specified properties
     */
    public static SolutionGmlGenerator getInstance(SNDlibGmlProperties gmlProps) {

        return getInstance(gmlProps.getSolutionGmlProperties());
    }

    /**
     * Creates a new solution GML generator, initialized with the specified 
     * properties.
     * 
     * @param gmlProps the GML properties to be used when generating GML 
     * documents 
     * 
     * @return the solution GML generator using the specified properties
     */
    public static SolutionGmlGenerator getInstance(SolutionGmlProperties gmlProps) {

        return new SolutionGmlGenerator(gmlProps);
    }

    private SolutionGmlProperties _gmlProps;

    /**
     * Constructs a GML generator for the given solved problem, using the 
     * specified properties for the generation. 
     * 
     * @param solvedProblem the solved problem 
     * @param gmlProps the properties to be used for the generation
     * 
     * @return the GML generator for the given solved problem and properties
     */
    private SolutionGmlGenerator(SolutionGmlProperties gmlProps) {

        ArgChecker.checkNotNull(gmlProps, "gml properties");

        _gmlProps = gmlProps;
    }

    /**
     * Generates a GML for the given solved problem and writes it to the 
     * specified output stream.
     * 
     * @param solvedProblem the solved problem
     * @param target the target output stream
     * 
     * @throws IOException if an underlying I/O error occured
     */
    public void generateGml(SolvedProblem solvedProblem, OutputStream target)
        throws IOException {

        ArgChecker.checkNotNull(solvedProblem, "solved problem");
        ArgChecker.checkNotNull(target, "target stream");

        GmlCoordinatesProvider coordinatesProvider = _gmlProps.getCoordinatesProvider(solvedProblem.nodes());

        SNDlibGmlGraph gmlGraph = new SNDlibGmlGraph(
            solvedProblem.getSolutionName(),
            SNDlibGmlUtils.getIsDirected(solvedProblem), coordinatesProvider,
            _gmlProps);

        gmlGraph.addNodes(solvedProblem.nodes());

        addLinks(gmlGraph, solvedProblem);
        GmlWriter.writeDocument(
            gmlGraph.createGmlDocument(SolutionGmlGenerator.class.getName()), target);
    }

    /**
     * Internal helper method which adds the links to the GML graph.
     */
    private void addLinks(SNDlibGmlGraph gmlGraph, SolvedProblem solvedProblem) {

        LinkModel linkModel = solvedProblem.getLinkModel();

        Set<Link> unusedLinks = new HashSet<Link>();
        Set<Link> usedLinks = new HashSet<Link>();

        for(Link link : solvedProblem.links()) {
            if(SolutionUtils.getTotalInstalledCapacity(link, solvedProblem) == 0) {
                unusedLinks.add(link);
            }
            else {
                usedLinks.add(link);
            }
        }

        gmlGraph.addLinks(unusedLinks, _gmlProps.getLinkWidthUnused(),
            _gmlProps.getLinkColorUnused(), _gmlProps.getSolutionLinkStyleUnused(),
            linkModel);

        LinkRanker<Integer> widthRanker = getWidthRanker(solvedProblem);
        LinkRanker<Color> colorRanker = getColorRanker(solvedProblem);

        for(Link link : usedLinks) {
            gmlGraph.addLink(link, widthRanker.rank(link), colorRanker.rank(link),
                _gmlProps.getSolutionLinkStyle(), linkModel);
        }
    }

    /**
     * Returns the ranker to be used when assigning the width to a specific 
     * link.
     */
    private LinkRanker<Integer> getWidthRanker(SolvedProblem solvedProblem) {

        if(_gmlProps.getLinkWidthGradientType() == GradientType.CONSTANT) {
            return new LinkRanker<Integer>() {

                public Integer rank(Link link) {

                    return _gmlProps.getSolutionLinkWidth();
                }
            };
        }
        return new LinkToIntegerRanker(solvedProblem,
            _gmlProps.getLinkWidthGradientType(),
            _gmlProps.getLinkWidthGradientResolution());
    }

    /**
     * Returns the ranker to be used when assigning the color to a specific 
     * link.
     */
    private LinkRanker<Color> getColorRanker(SolvedProblem solvedProblem) {

        if(_gmlProps.getLinkColorGradientType() == GradientType.CONSTANT) {
            return new LinkRanker<Color>() {

                public Color rank(Link link) {

                    return _gmlProps.getSolutionLinkColor();
                }
            };
        }
        return new LinkToColorRanker(solvedProblem,
            _gmlProps.getLinkColorGradientType(),
            _gmlProps.getLinkColorGradientStart(),
            _gmlProps.getLinkColorGradientEnd(),
            _gmlProps.getLinkColorGradientResolution());
    }

    private static interface LinkRanker<T> {

        public T rank(Link link);
    }

    /**
     * Depending on the specified gradient type, this class ranks links to 
     * an integer number in the interval [0, resolution]. 
     */
    private static class LinkToIntegerRanker implements LinkRanker<Integer> {

        private Map<Link, Integer> _linkRanks;

        private LinkToIntegerRanker(SolvedProblem solvedProblem,
            GradientType gradientType, int resolution) {

            _linkRanks = new HashMap<Link, Integer>();

            switch (gradientType) {
            case USAGE:
                createUsageGradient(solvedProblem, resolution);
                break;
            case CAPACITY:
                createCapacityGradient(solvedProblem, resolution);
                break;
            case COST:
                createCostGradient(solvedProblem, resolution);
                break;
            }
        }

        private void createUsageGradient(SolvedProblem solvedProblem, int resolution) {

            LinkModel lm = solvedProblem.getLinkModel();

            for(Link link : solvedProblem.getNetwork().links()) {
                LinkConfiguration linkConfig = solvedProblem.getLinkConf(link);

                if(linkConfig == null) {
                    _linkRanks.put(link, 0);
                }
                else {
                    double capacity = SolutionUtils.getTotalInstalledCapacity(linkConfig);
                    if(capacity == 0) {
                        _linkRanks.put(link, 0);
                    }
                    else {
                        MaximumLinkFlow maxFlow = SolutionUtils.calculateMaximumLinkFlow(
                            link, solvedProblem);
                        double flow = (lm == LinkModel.BIDIRECTED) ? Math.max(
                            maxFlow.getMaxPositiveFlow(),
                            maxFlow.getMaxNegativeFlow())
                            : maxFlow.getMaxTotalFlow();
                        _linkRanks.put(link, Math.max((int) Math.round(flow
                            / capacity * (double) resolution), 1));
                    }
                }
            }
        }

        private void createCapacityGradient(SolvedProblem solvedProblem,
            int resolution) {

            Map<Link, Double> linkCapacities = new HashMap<Link, Double>();

            for(Link link : solvedProblem.getNetwork().links()) {
                linkCapacities.put(link, SolutionUtils.getTotalInstalledCapacity(
                    link, solvedProblem));
            }

            double maxCapacity = getMax(linkCapacities.values());

            for(Map.Entry<Link, Double> entry : linkCapacities.entrySet()) {
                double capacity = entry.getValue();
                if(capacity == 0.0) {
                    _linkRanks.put(entry.getKey(), 0);
                }
                else {
                    _linkRanks.put(entry.getKey(), Math.max(
                        (int) Math.round(capacity / maxCapacity
                            * (double) resolution), 1));
                }
            }
        }

        private void createCostGradient(SolvedProblem solvedProblem, int resolution) {

            LinkModel lm = solvedProblem.getLinkModel();

            Map<Link, Double> linkCosts = new HashMap<Link, Double>();

            for(Link link : solvedProblem.getNetwork().links()) {
                double capacity = SolutionUtils.getTotalInstalledCapacity(link,
                    solvedProblem);

                if(capacity > 0) {
                    double cost = 0.0;

                    LinkConfiguration linkConf = solvedProblem.getLinkConf(link);
                    if(linkConf != null) {
                        cost = SolutionUtils.getTotalInstalledCapacityCost(link,
                            linkConf);
                    }
                    else {
                        cost = link.getPreCost();
                    }

                    MaximumLinkFlow maxFlow = SolutionUtils.calculateMaximumLinkFlow(
                        link, solvedProblem);
                    double maxFlowValue = (lm == LinkModel.BIDIRECTED) ? Math.max(
                        maxFlow.getMaxPositiveFlow(), maxFlow.getMaxNegativeFlow())
                        : maxFlow.getMaxTotalFlow();

                    cost += maxFlowValue * link.getRoutingCost();
                    if(solvedProblem.getFixedChargeModel() == FixedChargeModel.YES) {
                        cost += link.getSetupCost();
                    }

                    if(cost == 0) {
                        linkCosts.put(link, -1.0);
                    }
                    else {
                        linkCosts.put(link, cost / capacity);
                    }
                }
                else {
                    linkCosts.put(link, 0.0);
                }
            }

            double maxCost = getMax(linkCosts.values());

            for(Map.Entry<Link, Double> entry : linkCosts.entrySet()) {
                double cost = entry.getValue();
                if(cost == 0) {
                    _linkRanks.put(entry.getKey(), 0);
                }
                else if(cost == -1) {
                    _linkRanks.put(entry.getKey(), 1);
                }
                else {
                    _linkRanks.put(entry.getKey(), Math.max((int) Math.round(cost
                        / maxCost * (double) resolution), 1));
                }
            }
        }

        public Integer rank(Link link) {

            return _linkRanks.get(link);
        }

        private static double getMax(Collection<Double> values) {

            double max = 0.0;
            for(double value : values) {
                max = Math.max(max, value);
            }

            return max;
        }
    }

    private static class LinkToColorRanker implements LinkRanker<Color> {

        private LinkToIntegerRanker _ranker;

        private Color[] _colorGradient;

        private LinkToColorRanker(SolvedProblem solvedProblem,
            GradientType gradientType, Color start, Color end, int resolution) {

            _ranker = new LinkToIntegerRanker(solvedProblem, gradientType,
                resolution);

            _colorGradient = createColorGradient(start, end, resolution);
        }

        public Color rank(Link link) {

            int rank = _ranker.rank(link);

            return (rank > 0) ? _colorGradient[rank - 1] : _colorGradient[0];
        }

        private static Color[] createColorGradient(Color start, Color end, int steps) {

            if(steps <= 1) {
                return new Color[] {start};
            }

            Color[] gradient = new Color[steps];

            double coeffStart = 1;
            double coeffEnd = 0;
            double step = 1.0 / (steps - 1);

            for(int i = 0; i < steps; ++i) {
                int red = (int) (coeffStart * start.getRed() + coeffEnd
                    * end.getRed());
                int green = (int) (coeffStart * start.getGreen() + coeffEnd
                    * end.getGreen());
                int blue = (int) (coeffStart * start.getBlue() + coeffEnd
                    * end.getBlue());

                gradient[i] = new Color(red, green, blue);
                coeffStart -= step;
                coeffEnd += step;
            }

            return gradient;
        }
    }
}
