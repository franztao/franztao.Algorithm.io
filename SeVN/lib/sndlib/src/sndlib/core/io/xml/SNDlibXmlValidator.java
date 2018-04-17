/*
 * $Id: SNDlibXmlValidator.java 429 2008-01-23 14:44:07Z roman.klaehne $
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

package sndlib.core.io.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.validation.Schema;

import org.w3c.dom.Document;

import com.atesio.utils.ResourceLoader;
import com.atesio.utils.message.Messages;
import com.atesio.utils.xml.XmlConfigurationException;
import com.atesio.utils.xml.XmlException;
import com.atesio.utils.xml.XmlFacade;

/**
 * This class contains the XML schemas for the network, model and solution
 * XML files. It provides methods to validate the said XML files against 
 * their schemas.<br/><br/>
 * 
 * The SNDlib XML format is described on the <a target="_blank" 
 * href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html">
 * SNDlib webpage</a>
 * 
 * @author Roman Klaehne
 */
public class SNDlibXmlValidator {

    /**
     * The schema of a network XML file.
     */
    public static final Schema NETWORK_SCHEMA;

    /**
     * The schema of a model XML file.
     */
    public static final Schema MODEL_SCHEMA;

    /**
     * The schema of a solution XML file.
     */
    public static final Schema SOLUTION_SCHEMA;

    /**
     * Indicates whether the schemas were loaded successfully.
     */
    private static boolean _initSuccessfully;

    static {
        _initSuccessfully = false;

        String baseId = SNDlibXmlValidator.class.getPackage().getName().replace('.',
            '/')
            + "/";

        String schemaId = baseId + "network.xsd";
        try {
            URL schemaUrl = ResourceLoader.getResourceUrl(SNDlibXmlValidator.class,
                schemaId);
            if(schemaUrl == null) {
                throw new AssertionError("could not found schema " + schemaId);
            }
            NETWORK_SCHEMA = XmlFacade.createSchema(schemaUrl);

            schemaId = baseId + "model.xsd";
            schemaUrl = ResourceLoader.getResourceUrl(SNDlibXmlValidator.class,
                schemaId);
            if(schemaUrl == null) {
                throw new AssertionError("could not found schema " + schemaId);
            }
            MODEL_SCHEMA = XmlFacade.createSchema(schemaUrl);

            schemaId = baseId + "solution.xsd";
            schemaUrl = ResourceLoader.getResourceUrl(SNDlibXmlValidator.class,
                schemaId);
            if(schemaUrl == null) {
                throw new AssertionError("could not found schema " + schemaId);
            }
            SOLUTION_SCHEMA = XmlFacade.createSchema(schemaUrl);
        }
        catch (Exception x) {
            throw new XmlConfigurationException("could not compile schema "
                + schemaId + ": " + x);
        }

        _initSuccessfully = true;
    }

    /**
     * Validates the given XML document representing a model against its 
     * XML schema.
     * 
     * @param modelDoc the XML document representing a model
     * 
     * @return the validation errors
     * 
     * @throws IOException if an IO error occured
     * @throws XmlException if an error occured while reading the XML
     * document
     */
    public static Messages validateModelDoc(Document modelDoc) throws XmlException,
        IOException {

        checkInitSuccessfully();
        return XmlFacade.validateDoc(modelDoc, MODEL_SCHEMA);
    }

    /**
     * Validates the given XML document representing a network against its 
     * XML schema.
     * 
     * @param networkDoc the XML document representing a network
     * 
     * @return the validation errors
     * 
     * @throws IOException if an IO error occured
     * @throws XmlException if an error occured while reading the XML
     * document
     */
    public static Messages validateNetworkDoc(Document networkDoc)
        throws XmlException, IOException {

        checkInitSuccessfully();
        return XmlFacade.validateDoc(networkDoc, NETWORK_SCHEMA);
    }

    /**
     * Validates the given XML document representing a solution against its 
     * XML schema.
     * 
     * @param solutionDoc the XML document representing a solution
     * 
     * @return the validation errors
     * 
     * @throws IOException if an IO error occured
     * @throws XmlException if an error occured while reading the XML
     * document
     */
    public static Messages validateSolutionDoc(Document solutionDoc)
        throws XmlException, IOException {

        checkInitSuccessfully();
        return XmlFacade.validateDoc(solutionDoc, SOLUTION_SCHEMA);
    }

    /**
     * Validates the given XML source representing a model against its 
     * XML schema.
     * 
     * @param modelSource the XML source representing a model
     * 
     * @return the validation errors
     * 
     * @throws IOException if an IO error occured
     * @throws XmlException if an error occured while reading the XML
     * document
     */
    public static Messages validateModelXml(InputStream modelSource)
        throws IOException, XmlException {

        checkInitSuccessfully();
        return XmlFacade.validateXml(modelSource, MODEL_SCHEMA);
    }

    /**
     * Validates the given XML source representing a solution against its 
     * XML schema.
     * 
     * @param solutionSource the XML source representing a solution
     * 
     * @return the validation errors
     * 
     * @throws IOException if an IO error occured
     * @throws XmlException if an error occured while reading the XML
     * document
     */
    public static Messages validateSolutionXml(InputStream solutionSource)
        throws IOException, XmlException {

        checkInitSuccessfully();
        return XmlFacade.validateXml(solutionSource, SOLUTION_SCHEMA);
    }

    /**
     * Validates the given XML source representing a network against its 
     * XML schema.
     * 
     * @param networkSource the XML source representing a network
     * 
     * @return the validation errors
     * 
     * @throws IOException if an IO error occured
     * @throws XmlException if an error occured while reading the XML
     * document
     */
    public static Messages validateNetworkXml(InputStream networkSource)
        throws IOException, XmlException {

        checkInitSuccessfully();
        return XmlFacade.validateXml(networkSource, NETWORK_SCHEMA);
    }

    /**
     * Checks whether the XML schemas were loaded successfully.
     * 
     * @throws XmlConfigurationException if the XML schemas were not loaded
     * successfully
     */
    private static void checkInitSuccessfully() {

        if(!_initSuccessfully) {
            throw new XmlConfigurationException(
                "validator was not init successfully");
        }
    }

    private SNDlibXmlValidator() {

        /* cannot be instantiated */
    }
}

