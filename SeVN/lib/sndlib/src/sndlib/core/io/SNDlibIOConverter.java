/*
 * $Id: SNDlibIOConverter.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

package sndlib.core.io;

import static com.atesio.utils.ArgChecker.checkNotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import sndlib.core.model.Model;
import sndlib.core.network.Network;
import sndlib.core.solution.Solution;

/**
 * Converts network, model and solution files written in one IO-format into 
 * any other IO-format.
 * 
 * @see SNDlibIOFormat
 * 
 * @author Roman Klaehne
 */
public class SNDlibIOConverter {

    /**
     * Reads a network written in the specified format from the given source 
     * and writes it to the given target using the specified target format. 
     * 
     * @param source the network source
     * @param sourceFormat the network's source format
     * @param target the target to which the network is written
     * @param targetFormat the network's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertNetwork(Reader source, String sourceFormat,
        Writer target, String targetFormat) throws IOException, SNDlibIOException {

        checkNotNull(source, "source");
        checkNotNull(sourceFormat, "source format");
        checkNotNull(target, "target");
        checkNotNull(targetFormat, "target format");

        Network network = SNDlibIOFactory.newParser(sourceFormat).parseNetwork(
            source);
        SNDlibIOFactory.newWriter(targetFormat).writeNetwork(network, target);
    }

    /**
     * Reads a network written in the specified format from the given source 
     * and writes it to the given target using the specified target format. 
     * 
     * @param source the network source
     * @param sourceFormat the network's source format
     * @param target the target to which the network is written
     * @param targetFormat the network's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertNetwork(InputStream source, String sourceFormat,
        OutputStream target, String targetFormat) throws IOException,
        SNDlibIOException {

        checkNotNull(source, "source");
        checkNotNull(target, "target");

        convertNetwork(new InputStreamReader(source), sourceFormat,
            new OutputStreamWriter(target), targetFormat);
    }

    /**
     * Reads a network written in the specified format from the given source
     * file and writes it to the given target file using the specified target 
     * format. 
     * 
     * @param sourceFile the network source file
     * @param sourceFormat the network's source format
     * @param targetFile the target file to which the network is written
     * @param targetFormat the network's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertNetwork(File sourceFile, String sourceFormat,
        File targetFile, String targetFormat) throws IOException, SNDlibIOException {

        checkNotNull(sourceFile, "source file");
        checkNotNull(targetFile, "target file");

        Writer target = new FileWriter(targetFile);
        convertNetwork(new FileReader(sourceFile), sourceFormat, target,
            targetFormat);
        target.close();
    }

    /**
     * Reads a network written in the specified format from the given source
     * file and writes it to the given target file using the specified target 
     * format. 
     * 
     * @param sourceFileName the name of the network's source file
     * @param sourceFormat the network's source format
     * @param targetFileName the name of the target file to which the network 
     * is written
     * @param targetFormat the network's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertNetwork(String sourceFileName, String sourceFormat,
        String targetFileName, String targetFormat) throws IOException,
        SNDlibIOException {

        checkNotNull(sourceFileName, "source file name");
        checkNotNull(targetFileName, "target file name");

        Writer target = new FileWriter(targetFileName);
        convertNetwork(new FileReader(sourceFileName), sourceFormat, target,
            targetFormat);
        target.close();
    }

    /**
     * Reads a solution written in the specified format from the given source 
     * and writes it to the given target using the specified target format. 
     * 
     * @param source the solution source
     * @param sourceFormat the solution's source format
     * @param target the target to which the solution is written
     * @param targetFormat the solution's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertSolution(Reader source, String sourceFormat,
        Writer target, String targetFormat) throws IOException, SNDlibIOException {

        checkNotNull(source, "source");
        checkNotNull(sourceFormat, "source format");
        checkNotNull(target, "target");
        checkNotNull(targetFormat, "target format");

        Solution solution = SNDlibIOFactory.newParser(sourceFormat).parseSolution(
            source);
        SNDlibIOFactory.newWriter(targetFormat).writeSolution(solution, target);
    }

    /**
     * Reads a solution written in the specified format from the given source 
     * and writes it to the given target using the specified target format. 
     * 
     * @param source the solution source
     * @param sourceFormat the solution's source format
     * @param target the target to which the solution is written
     * @param targetFormat the solution's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertSolution(InputStream source, String sourceFormat,
        OutputStream target, String targetFormat) throws IOException,
        SNDlibIOException {

        checkNotNull(source, "source");
        checkNotNull(target, "target");

        convertSolution(new InputStreamReader(source), sourceFormat,
            new OutputStreamWriter(target), targetFormat);
    }

    /**
     * Reads a solution written in the specified format from the given source 
     * file and writes it to the given target using the specified target 
     * format. 
     * 
     * @param sourceFile the solution source file
     * @param sourceFormat the solution's source format
     * @param targetFile the target file to which the solution is written
     * @param targetFormat the solution's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertSolution(File sourceFile, String sourceFormat,
        File targetFile, String targetFormat) throws IOException, SNDlibIOException {

        checkNotNull(sourceFile, "source file");
        checkNotNull(targetFile, "target file");

        Writer target = new FileWriter(targetFile);
        convertSolution(new FileReader(sourceFile), sourceFormat, target,
            targetFormat);
        target.close();
    }

    /**
     * Reads a solution written in the specified format from the given source 
     * file and writes it to the given target using the specified target 
     * format. 
     * 
     * @param sourceFileName the name of the solution's source file
     * @param sourceFormat the solution's source format
     * @param targetFileName the name of the target file to which the solution 
     * is written
     * @param targetFormat the solution's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertSolution(String sourceFileName, String sourceFormat,
        String targetFileName, String targetFormat) throws IOException,
        SNDlibIOException {

        checkNotNull(sourceFileName, "source file name");
        checkNotNull(targetFileName, "target file name");

        Writer target = new FileWriter(targetFileName);
        convertSolution(new FileReader(sourceFileName), sourceFormat, target,
            targetFormat);
        target.close();
    }

    /**
     * Reads a model written in the specified format from the given source 
     * and writes it to the given target using the specified target format. 
     * 
     * @param source the model source
     * @param sourceFormat the model's source format
     * @param target the target to which the model is written
     * @param targetFormat the model's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertModel(Reader source, String sourceFormat,
        Writer target, String targetFormat) throws IOException, SNDlibIOException {

        checkNotNull(source, "source");
        checkNotNull(sourceFormat, "source format");
        checkNotNull(target, "target");
        checkNotNull(targetFormat, "target format");

        Model model = SNDlibIOFactory.newParser(sourceFormat).parseModel(source);
        SNDlibIOFactory.newWriter(targetFormat).writeModel(model, target);
    }

    /**
     * Reads a model written in the specified format from the given source 
     * and writes it to the given target using the specified target format. 
     * 
     * @param source the model source
     * @param sourceFormat the model's source format
     * @param target the target to which the model is written
     * @param targetFormat the model's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertModel(InputStream source, String sourceFormat,
        OutputStream target, String targetFormat) throws IOException,
        SNDlibIOException {

        checkNotNull(source, "source");
        checkNotNull(target, "target");

        convertModel(new InputStreamReader(source), sourceFormat,
            new OutputStreamWriter(target), targetFormat);
    }

    /**
     * Reads a model written in the specified format from the given source 
     * file and writes it to the given target using the specified target 
     * format. 
     * 
     * @param sourceFile the model source file
     * @param sourceFormat the model's source format
     * @param targetFile the target file to which the model is written
     * @param targetFormat the model's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertModel(File sourceFile, String sourceFormat,
        File targetFile, String targetFormat) throws IOException, SNDlibIOException {

        checkNotNull(sourceFile, "source file");
        checkNotNull(targetFile, "target file");

        Writer target = new FileWriter(targetFile);
        convertModel(new FileReader(sourceFile), sourceFormat, target, targetFormat);
        target.close();
    }

    /**
     * Reads a model written in the specified format from the given source 
     * file and writes it to the given target using the specified target 
     * format. 
     * 
     * @param sourceFileName the name of the model's source file
     * @param sourceFormat the model's source format
     * @param targetFileName the name of the target file to which the model is 
     * written
     * @param targetFormat the model's target format
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibIOException if an error concerning the IO-format occured
     */
    public static void convertModel(String sourceFileName, String sourceFormat,
        String targetFileName, String targetFormat) throws IOException,
        SNDlibIOException {

        checkNotNull(sourceFileName, "source file name");
        checkNotNull(targetFileName, "target file name");

        Writer target = new FileWriter(targetFileName);
        convertModel(new FileReader(sourceFileName), sourceFormat, target,
            targetFormat);
        target.close();
    }

    private SNDlibIOConverter() {

        /* not instantiable */
    }
}

