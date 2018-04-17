/*
 * $Id: ModelXmlParser.java 429 2008-01-23 14:44:07Z roman.klaehne $
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
import java.io.Reader;

import sndlib.core.io.SNDlibParseException;
import sndlib.core.io.xml.castorgen.model.ModelEl;
import sndlib.core.model.AdmissiblePathModel;
import sndlib.core.model.DemandModel;
import sndlib.core.model.FixedChargeModel;
import sndlib.core.model.HopLimitModel;
import sndlib.core.model.LinkCapacityModel;
import sndlib.core.model.LinkModel;
import sndlib.core.model.Model;
import sndlib.core.model.ModelData;
import sndlib.core.model.NodeModel;
import sndlib.core.model.ObjectiveModel;
import sndlib.core.model.RoutingModel;
import sndlib.core.model.SurvivabilityModel;

import com.atesio.utils.xml.XmlException;
import com.atesio.utils.xml.XmlFacade;
import com.atesio.utils.xml.XmlValidationException;

/**
 * This class provides the method to a parse a model file written in the XML
 * format of SNDlib to produce a {@link sndlib.core.model.Model} instance.
 * <br/><br/>
 * 
 * The SNDlib XML format of a model file is described on the <a target="_blank" 
 * href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html">
 * SNDlib webpage</a><br/><br/>
 * 
 * This class is considered as an internal helper of this package.
 * 
 * @see SNDlibXmlParser
 * @see sndlib.core.model.Model
 * 
 * @author Roman Klaehne
 */
class ModelXmlParser {

    /**
     * Parses the given source in order to produce a <tt>Model</tt>.
     * 
     * @param source the source to parse the model from
     * 
     * @return the parsed <tt>Model</tt>
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibParseException if an error concerning the syntax or
     * consistency occured
     */
    static Model parseModel(Reader source) throws IOException, SNDlibParseException {

        ModelEl modelEl = null;

        try {
            modelEl = XmlFacade.unmarshal(source, ModelEl.class,
                SNDlibXmlValidator.MODEL_SCHEMA);
        }
        catch (XmlException xmlx) {
            throw new SNDlibParseException("error parsing model xml: "
                + xmlx.getMessage(), xmlx);
        }
        catch (XmlValidationException xmlvx) {
            throw new SNDlibParseException(xmlvx.getErrors());
        }

        return xsd2Model(modelEl);
    }

    /**
     * Transforms the given XML-element into a <tt>Model</tt> object.
     * 
     * @param modelEl the XML-element
     * 
     * @return a <tt>Model</tt> instance
     */
    private static Model xsd2Model(ModelEl modelEl) {

        ModelData modelData = new ModelData();

        String model = modelEl.getAdmissiblePathModelEl().toString();
        modelData.setAdmissiblePathModel(AdmissiblePathModel.valueOfIgnoreCase(model));

        model = modelEl.getDemandModelEl().toString();
        modelData.setDemandModel(DemandModel.valueOfIgnoreCase(model));

        model = modelEl.getLinkModelEl().toString();
        modelData.setLinkModel(LinkModel.valueOfIgnoreCase(model));

        model = modelEl.getLinkCapacityModelEl().toString();
        modelData.setLinkCapacityModel(LinkCapacityModel.valueOfIgnoreCase(model));

        model = modelEl.getRoutingModelEl().toString();
        modelData.setRoutingModel(RoutingModel.valueOfIgnoreCase(model));

        model = modelEl.getHopLimitModelEl().toString();
        modelData.setHopLimitModel(HopLimitModel.valueOfIgnoreCase(model));

        model = modelEl.getNodeModelEl().toString();
        modelData.setNodeModel(NodeModel.valueOfIgnoreCase(model));

        model = modelEl.getObjectiveModelEl().toString();
        modelData.setObjectiveModel(ObjectiveModel.valueOfIgnoreCase(model));

        model = modelEl.getFixedChargeModelEl().toString();
        modelData.setFixedChargeModel(FixedChargeModel.valueOfIgnoreCase(model));

        model = modelEl.getSurvivabilityModelEl().toString();
        modelData.setSurvivabilityModel(SurvivabilityModel.valueOfIgnoreCase(model));

        return new Model(modelData);
    }
}
