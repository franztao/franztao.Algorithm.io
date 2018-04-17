/*
 * $Id: ModelXmlWriter.java 429 2008-01-23 14:44:07Z roman.klaehne $
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
import java.io.Writer;

import sndlib.core.io.SNDlibWriteException;
import sndlib.core.io.xml.castorgen.model.ModelEl;
import sndlib.core.io.xml.castorgen.model.types.AdmissiblePathModelType;
import sndlib.core.io.xml.castorgen.model.types.DemandModelType;
import sndlib.core.io.xml.castorgen.model.types.FixedChargeModelType;
import sndlib.core.io.xml.castorgen.model.types.HopLimitModelType;
import sndlib.core.io.xml.castorgen.model.types.LinkCapacityModelType;
import sndlib.core.io.xml.castorgen.model.types.LinkModelType;
import sndlib.core.io.xml.castorgen.model.types.NodeModelType;
import sndlib.core.io.xml.castorgen.model.types.ObjectiveModelType;
import sndlib.core.io.xml.castorgen.model.types.RoutingModelType;
import sndlib.core.io.xml.castorgen.model.types.SurvivabilityModelType;
import sndlib.core.model.Model;

import com.atesio.utils.xml.XmlException;
import com.atesio.utils.xml.XmlFacade;

/**
 * This class provides the method to write a {@link sndlib.core.model.Model} 
 * to the XML format of SNDlib.<br/>
 * <br/>
 * 
 * The SNDlib XML format of a model file is described on the <a target="_blank" 
 * href="http://sndlib.zib.de/home.action?show=html/docu/io-formats/index.html">
 * SNDlib webpage</a><br/><br/>
 * 
 * This class is considered as an internal helper of this package.
 * 
 * @see SNDlibXmlWriter
 * @see sndlib.core.model.Model
 * 
 * @author Roman Klaehne
 */
class ModelXmlWriter {

    /**
     * Writes the given model to the specified target writer.
     * 
     * @param model the model to write
     * @param target the target to which the model is written
     * 
     * @throws IOException if an IO error occured
     * @throws SNDlibWriteException if an error concerning the IO-format
     * occured
     */
    static void writeModel(Model model, Writer target) throws IOException,
        SNDlibWriteException {

        ModelEl modelEl = model2Xsd(model);

        try {
            XmlFacade.marshal(modelEl, target);
        }
        catch (XmlException x) {
            throw new SNDlibWriteException(
                "error marshalling network model to xml: " + x.getMessage(), x);
        }
    }

    /**
     * Transforms the given <tt>Model</tt> into its corresponding XML-element.
     * 
     * @param model the model
     * 
     * @return the XML-element corresponding to the specified model
     */
    private static ModelEl model2Xsd(Model model) {

        ModelEl modelEl = new ModelEl();

        String modelName = model.getAdmissiblePathModel().name();
        modelEl.setAdmissiblePathModelEl(AdmissiblePathModelType.valueOf(modelName));

        modelName = model.getDemandModel().name();
        modelEl.setDemandModelEl(DemandModelType.valueOf(modelName));

        modelName = model.getLinkModel().name();
        modelEl.setLinkModelEl(LinkModelType.valueOf(modelName));

        modelName = model.getLinkCapacityModel().name();
        modelEl.setLinkCapacityModelEl(LinkCapacityModelType.valueOf(modelName));

        modelName = model.getNodeModel().name();
        modelEl.setNodeModelEl(NodeModelType.valueOf(modelName));

        modelName = model.getFixedChargeModel().name();
        modelEl.setFixedChargeModelEl(FixedChargeModelType.valueOf(modelName));

        modelName = model.getRoutingModel().name();
        modelEl.setRoutingModelEl(RoutingModelType.valueOf(modelName));

        modelName = model.getHopLimitModel().name();
        modelEl.setHopLimitModelEl(HopLimitModelType.valueOf(modelName));

        modelName = model.getObjectiveModel().name();
        modelEl.setObjectiveModelEl(ObjectiveModelType.valueOf(modelName));

        modelName = model.getSurvivabilityModel().name();
        modelEl.setSurvivabilityModelEl(SurvivabilityModelType.valueOf(modelName));

        return modelEl;
    }
}
