/*
 * $Id: ExplicitPathsFeasibilityValidator.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.validation;

import sndlib.core.model.AdmissiblePathModel;
import sndlib.core.model.DemandModel;
import sndlib.core.model.LinkModel;
import sndlib.core.network.AdmissiblePath;
import sndlib.core.network.Demand;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.Problem;
import sndlib.core.problem.RoutingPath;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.ArgChecker;
import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * If the admissible path model of a network design problem is set to 
 * <tt>EXPLICIT_LIST</tt> this validator checks whether the admissible paths 
 * of all {@link sndlib.core.network.Demand}s in a given network are 
 * feasible.<br/>
 * An admissible path is feasible if its end nodes meet those of the demand 
 * and if it is valid in terms of link connectivity.<br/>
 * <br/>
 *  
 * Whether a demand specifies a definite source and target node depends on 
 * the demand model.<br/>
 * The direction into which the links can be traversed is determined by the 
 * link model.  
 * 
 * @see sndlib.core.model.AdmissiblePathModel
 * @see sndlib.core.network.AdmissiblePath
 * @see sndlib.core.network.Demand
 *  
 * @author Roman Klaehne
 */
class ExplicitPathsFeasibilityValidator implements ProblemValidator {

    private static class ErrorKeys {

        final static MessageKey NO_DIRECTED_PATH_BETWEEN_SOURCE_TARGET = new SimpleMessageKey(
            "validator.problem.error.noDirectedPathBetweenSourceTarget");

        final static MessageKey NO_UNDIRECTED_PATH_BETWEEN_SOURCE_TARGET = new SimpleMessageKey(
            "validator.problem.error.noUndirectedPathBetweenSourceTarget");

        final static MessageKey INVALID_DIRECTED_PATH = new SimpleMessageKey(
            "validator.problem.error.invalidDirectedPath");

        final static MessageKey INVALID_UNDIRECTED_PATH = new SimpleMessageKey(
            "validator.problem.error.invalidUndirectedPath");

        final static MessageKey EMPTY_PATH = new SimpleMessageKey(
            "validator.problem.error.emptyPath");
    }

    /**
     * Constructs a new instance of this validator.
     */
    ExplicitPathsFeasibilityValidator() {

        /* nothing to initialize */
    }

    /**
     * If the admissible path model of a network design problem is set to 
     * <tt>EXPLICIT_LIST</tt> this validator checks whether the admissible 
     * paths of all demands in the given problem's network are feasibible.<br/>
     * An admissible path is feasible if its end nodes meet those of the demand 
     * and if it is valid in terms of link connectivity.<br/>
     * <br/>
     *  
     * Whether a demand specifies a definite source and target node depends on 
     * the demand model.<br/>
     * The direction into which the links can be traversed is determined by the 
     * link model.<br/>
     * <br/>  
     * 
     * If the admissible path model is not <tt>EXPLICIT_LIST</tt> this mehtod 
     * has no effect.
     * 
     * @param problem the problem to validate
     * @param errors the message container into those the errors are put
     */
    public void validate(Problem problem, Messages errors) {

        if(problem.getAdmissiblePathModel() != AdmissiblePathModel.EXPLICIT_LIST) {
            return;
        }

        Network network = problem.getNetwork();

        LinkModel linkModel = problem.getLinkModel();
        DemandModel demandModel = problem.getDemandModel();

        PathValidator pathValidator = PathValidator.getPathValidatorFor(demandModel,
            linkModel);

        for(Demand demand : network.demands()) {

            if(demand.admissiblePathCount() > 0) {

                Node firstNode = demand.getFirstNode();
                Node secondNode = demand.getSecondNode();

                for(AdmissiblePath path : demand.admissiblePaths()) {

                    if(path.linkCount() > 0) {
                        pathValidator.validate(demand, path, firstNode, secondNode,
                            errors);
                    }
                    else {
                        errors.add(SNDlibMessages.error(ErrorKeys.EMPTY_PATH,
                            path.getId(), demand.getId()));
                    }
                }
            }
        }
    }

    /**
     * This is an internal helper class which abstracts the path validation
     * from a specific link and demand model. 
     * 
     * @author Roman Klaehne
     */
    abstract private static class PathValidator {

        /**
         * Validates the given path independently from the link and demand 
         * model.
         * 
         * @param demand the path's parent demand
         * @param path the path to validate
         * @param one the one end of the path
         * @param other the other end of the path
         */
        abstract void validate(Demand demand, AdmissiblePath path, Node one,
            Node other, Messages errors);

        /**
         * Performs the validation in case of a bidirected or an undirected 
         * link model and a directed demand model.
         */
        static final PathValidator ST_UNDIRECTED = new PathValidator() {

            void validate(Demand demand, AdmissiblePath path, Node one, Node other,
                Messages errors) {

                RoutingPath routingPath = RoutingPath.newUndirectedSTPath(
                    path.links(), one, other);

                if(routingPath == null) {
                    errors.add(SNDlibMessages.error(ErrorKeys.INVALID_UNDIRECTED_PATH,
                        path.getId(), demand.getId(), one.getId(), other.getId()));
                }
            }
        };

        /**
         * Performs the validation in case of a directed link model and 
         * a directed demand model.
         */
        static final PathValidator ST_DIRECTED = new PathValidator() {

            void validate(Demand demand, AdmissiblePath path, Node source,
                Node target, Messages errors) {

                RoutingPath routingPath = RoutingPath.newDirectedSTPath(
                    path.links(), source, target);

                if(routingPath == null) {
                    errors.add(SNDlibMessages.error(ErrorKeys.INVALID_DIRECTED_PATH,
                        path.getId(), demand.getId(), source.getId(), target.getId()));
                }
            }
        };

        /**
         * Performs the validation in case of a bidirected or an undirected 
         * link model and a symmetric or an undirected demand model.
         */
        static final PathValidator ST_TS_UNDIRECTED = new PathValidator() {

            void validate(Demand demand, AdmissiblePath path, Node one, Node other,
                Messages errors) {

                RoutingPath routingPath = RoutingPath.newUndirectedSTOrTSPath(
                    path.links(), one, other);

                if(routingPath == null) {
                    errors.add(SNDlibMessages.error(
                        ErrorKeys.NO_UNDIRECTED_PATH_BETWEEN_SOURCE_TARGET,
                        path.getId(), demand.getId(), one.getId(), other.getId()));
                }
            }
        };

        /**
         * Performs the validation in case of a directed link model and 
         * a symmetric or an undirected demand model.
         */
        static final PathValidator ST_TS_DIRECTED = new PathValidator() {

            void validate(Demand demand, AdmissiblePath path, Node source,
                Node target, Messages errors) {

                RoutingPath routingPath = RoutingPath.newDirectedSTOrTSPath(
                    path.links(), source, target);

                if(routingPath == null) {
                    errors.add(SNDlibMessages.error(
                        ErrorKeys.NO_DIRECTED_PATH_BETWEEN_SOURCE_TARGET,
                        path.getId(), demand.getId(), source.getId(), target.getId()));
                }
            }
        };

        /**
         * Returns the appropriate path validator for the specified link
         * and demand model.
         * 
         * @param demandModel the demand model
         * @param linkModel the link model
         * 
         * @return the appropriate validator for the specified models
         */
        static PathValidator getPathValidatorFor(DemandModel demandModel,
            LinkModel linkModel) {

            PathValidator validator = null;

            switch (demandModel) {

            case UNDIRECTED:
                switch (linkModel) {

                case DIRECTED:
                    validator = ST_TS_DIRECTED;
                    break;

                case UNDIRECTED: /* or */
                case BIDIRECTED:
                    validator = ST_TS_UNDIRECTED;
                    break;
                }
                break;

            case DIRECTED:
                switch (linkModel) {

                case DIRECTED:
                    validator = ST_DIRECTED;
                    break;

                case BIDIRECTED: /* or */
                case UNDIRECTED:
                    validator = ST_UNDIRECTED;
                    break;
                }
                break;
            }

            ArgChecker.assertNotNull(validator, "path validator");

            return validator;
        }
    }
}
