/*
 * $Id: NamingConventions.java 99 2006-07-04 14:05:16Z bzforlow $
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

package sndlib.core.util;

import sndlib.core.model.Model;

/**
 * This class provides methods to construct the conventional names of 
 * {@link sndlib.core.problem.Problem}s and {@link sndlib.core.model.Model}s 
 * in SNDlib.
 * 
 * @author Roman Klaehne
 */
public class NamingConventions {

    /**
     * Constructs the common name of a problem from the given names denoting
     * the problem's network and model.
     * 
     * @param networkName the name of the problem's network
     * @param modelName the name of the problem's model
     * 
     * @return the constructed problem name
     */
    public static String constructProblemName(String networkName, String modelName) {

        return networkName + "--" + modelName;
    }

    /**
     * Constructs the common name of the given model.
     * 
     * @param model the model to construct the name for
     * 
     * @return the constructed model name
     */
    public static String constructModelName(Model model) {

        String result = "";

        result += model.getDemandModel().getShortCut() + "-";
        result += model.getLinkModel().getShortCut() + "-";
        result += model.getLinkCapacityModel().getShortCut() + "-";
        result += model.getFixedChargeModel().getShortCut() + "-";
        result += model.getRoutingModel().getShortCut() + "-";
        result += model.getAdmissiblePathModel().getShortCut() + "-";
        result += model.getHopLimitModel().getShortCut() + "-";
        result += model.getSurvivabilityModel().getShortCut();

        return result;
    }

    private NamingConventions() {

        /* not instantiable */
    }
}

