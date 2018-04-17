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

import java.io.IOException;
import java.io.OutputStream;

import sndlib.core.problem.Problem;

import com.atesio.gml.base.GmlWriter;
import com.atesio.gml.gen.GmlCoordinatesProvider;
import com.atesio.utils.ArgChecker;

/**
 * This class generates a <a target="_blank" 
 * href="http://www.infosun.fim.uni-passau.de/Graphlet/GML/gml-tr.html">
 * GML</a> document for a given {@link Problem}.
 * 
 * @see ProblemGmlProperties
 * @see SNDlibGmlProperties
 * @see SNDlibGmlGenerator
 * 
 * @author Roman Klaehne
 */
public class ProblemGmlGenerator {

    private static final ProblemGmlGenerator DEFAULT_INSTANCE = new ProblemGmlGenerator(
        ProblemGmlProperties.getDefaults());

    /**
     * Returns the default problem GML generator, initialized with the default 
     * properties as returned by {@link ProblemGmlProperties#getDefaults()}.
     * 
     * @return the default problem GML generator 
     */
    public static ProblemGmlGenerator getDefaultInstance() {

        return DEFAULT_INSTANCE;
    }

    /**
     * Creates a new problem GML generator, initialized with the specified 
     * properties.<br/><br/>
     * 
     * This method is equivalent to 
     * <tt>getInstance(gmlProps.getProblemGmlProperties())</tt>
     * 
     * @param gmlProps the GML properties to be used when generating GML 
     * documents 
     * 
     * @return the problem GML generator using the specified properties
     */
    public static ProblemGmlGenerator getInstance(SNDlibGmlProperties gmlProps) {

        return getInstance(gmlProps.getProblemGmlProperties());
    }

    /**
     * Creates a new problem GML generator, initialized with the specified 
     * properties.
     * 
     * @param gmlProps the GML properties to be used when generating GML 
     * documents 
     * 
     * @return the problem GML generator using the specified properties
     */
    public static ProblemGmlGenerator getInstance(ProblemGmlProperties gmlProps) {

        return new ProblemGmlGenerator(gmlProps);
    }

    private ProblemGmlProperties _gmlProps;

    /**
     * Constructs a GML generator using the specified properties for the 
     * generation. 
     * 
     * @param gmlProps the properties to be used for the generation
     */
    private ProblemGmlGenerator(ProblemGmlProperties gmlProps) {

        ArgChecker.checkNotNull(gmlProps, "gml properties");

        _gmlProps = gmlProps;
    }

    /**
     * Creates a GML document for the given problem and writes it to the 
     * specified output stream.
     * 
     * @param problem the problem
     * @param target the target output stream
     * 
     * @throws IOException if an underlying I/O error occured
     */
    public void generateGml(Problem problem, OutputStream target) throws IOException {

        ArgChecker.checkNotNull(problem, "problem");
        ArgChecker.checkNotNull(target, "target stream");

        GmlCoordinatesProvider coordinatesProvider = _gmlProps.getCoordinatesProvider(problem.nodes());

        SNDlibGmlGraph gmlGraph = new SNDlibGmlGraph(problem.getName(),
            SNDlibGmlUtils.getIsDirected(problem), coordinatesProvider, _gmlProps);

        gmlGraph.addNodes(problem.nodes());

        gmlGraph.addLinks(problem.links(), _gmlProps.getProblemLinkWidth(),
            _gmlProps.getProblemLinkColor(), _gmlProps.getProblemLinkStyle(),
            problem.getLinkModel());

        GmlWriter.writeDocument(
            gmlGraph.createGmlDocument(ProblemGmlGenerator.class.getName()), target);
    }
}
