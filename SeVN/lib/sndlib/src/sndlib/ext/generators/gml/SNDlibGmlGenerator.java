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
import java.util.Properties;

import sndlib.core.problem.Problem;
import sndlib.core.problem.SolvedProblem;

import com.atesio.utils.ArgChecker;

/**
 * This class provides a simple interface for generating <a target="_blank" 
 * href="http://www.infosun.fim.uni-passau.de/Graphlet/GML/gml-tr.html">
 * GML</a>s for {@link Problem}s and {@link SolvedProblem}s. 
 * 
 * @see SNDlibGmlProperties
 * @see ProblemGmlGenerator
 * @see SolutionGmlGenerator
 * 
 * @author Roman Klaehne
 */
public class SNDlibGmlGenerator {

    private static final SNDlibGmlGenerator DEFAULT_INSTANCE = new SNDlibGmlGenerator();

    /**
     * Returns the default GML generator, initialized with the default 
     * properties as returned by {@link SNDlibGmlProperties#getDefaults()}.
     * 
     * @return the default GML generator
     */
    public static SNDlibGmlGenerator getDefaultInstance() {

        return DEFAULT_INSTANCE;
    }

    /**
     * Returns a new GML generator, initialized with the specified 
     * properties.
     * 
     * @param gmlProps the GML properties to be used when generating GML 
     * documents 
     * 
     * @return the default GML generator
     */
    public static SNDlibGmlGenerator getInstance(SNDlibGmlProperties gmlProps) {

        ArgChecker.checkNotNull(gmlProps, "gml properties");

        return new SNDlibGmlGenerator(gmlProps);
    }

    /**
     * Returns a new GML generator, initialized with the specified 
     * properties.
     * 
     * @param props the properties containing the GML properties
     * 
     * @return the default GML generator
     * 
     * @see SNDlibGmlProperties#load(Properties)
     */
    public static SNDlibGmlGenerator getInstance(Properties props) {

        ArgChecker.checkNotNull(props, "gml properties");

        SNDlibGmlProperties gmlProps = new SNDlibGmlProperties();
        gmlProps.load(props);

        return new SNDlibGmlGenerator(gmlProps);
    }

    private ProblemGmlGenerator _problemGmlGenerator;

    private SolutionGmlGenerator _solutionGmlGenerator;

    /**
     * Private constructor.
     * 
     * @see #getDefaultInstance()
     * @see #getInstance(Properties)
     * @see #getInstance(SNDlibGmlProperties)
     */
    private SNDlibGmlGenerator(SNDlibGmlProperties gmlProps) {

        _problemGmlGenerator = ProblemGmlGenerator.getInstance(gmlProps);
        _solutionGmlGenerator = SolutionGmlGenerator.getInstance(gmlProps);
    }

    /**
     * Private constructor.
     * 
     * @see #getDefaultInstance()
     * @see #getInstance(Properties)
     * @see #getInstance(SNDlibGmlProperties)
     */
    private SNDlibGmlGenerator() {

        _problemGmlGenerator = ProblemGmlGenerator.getDefaultInstance();
        _solutionGmlGenerator = SolutionGmlGenerator.getDefaultInstance();
    }

    /**
     * Generates a GML for the given problem and writes it to the specified
     * output stream.
     * 
     * @param problem the problem
     * @param target the target output stream
     * 
     * @throws IOException if an underlying I/O error occured
     * 
     * @see ProblemGmlGenerator
     * @see ProblemGmlGenerator#generateGml(Problem, OutputStream)
     */
    public void generateProblemGml(Problem problem, OutputStream target)
        throws IOException {

        _problemGmlGenerator.generateGml(problem, target);
    }

    /**
     * Generates a GML for the given solved problem and writes it to the 
     * specified output stream.
     * 
     * @param solvedProblem the solved problem
     * @param target the target output stream
     * 
     * @throws IOException if an underlying I/O error occured
     * 
     * @see SolutionGmlGenerator
     * @see SolutionGmlGenerator#generateGml(SolvedProblem, OutputStream)
     */
    public void generateSolutionGml(SolvedProblem solvedProblem, OutputStream target)
        throws IOException {

        _solutionGmlGenerator.generateGml(solvedProblem, target);
    }
}
