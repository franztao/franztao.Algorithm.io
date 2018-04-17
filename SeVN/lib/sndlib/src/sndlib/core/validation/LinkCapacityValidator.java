/*
 * $Id: LinkCapacityValidator.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

import sndlib.core.model.LinkCapacityModel;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.problem.SolvedProblem;
import sndlib.core.solution.LinkConfiguration;
import sndlib.core.solution.ModuleConfiguration;
import sndlib.core.util.DoubleComparator;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * This validator checks whether the installed link capacity modules are 
 * admissible in a given solution.<br/>
 * <br/>
 * 
 * The admissibility of the installed capacity modules thereby depends on
 * the link capacity model.<br/>
 * For a link which possesses a pre-installed capacity this validator 
 * also checks for the existance of a corresponding link configuration 
 * which specifies at least that pre-installed capacity.
 * 
 * @see sndlib.core.model.LinkCapacityModel
 * @see sndlib.core.network.CapacityModule
 * @see sndlib.core.network.Link
 * @see sndlib.core.solution.ModuleConfiguration
 * 
 * @author Roman Klaehne
 */
class LinkCapacityValidator implements SolutionValidator {

    private static class ErrorKeys {

        final static MessageKey NO_PREINSTALLED_CAPACITY_MODULE = new SimpleMessageKey(
            "validator.solution.error.noPreinstalledCapacityModule");

        final static MessageKey NO_LINK_CONF_FOR_LINK_WITH_PRE_INST_CAP = new SimpleMessageKey(
            "validator.solution.error.noLinkConfForLinkWithPreInstalledCapacity");

        final static MessageKey MODULE_COUNT_LOWER_THAN_ZERO = new SimpleMessageKey(
            "validator.solution.error.moduleCountLowerThanZero");

        final static MessageKey MODULE_COUNT_NOT_AN_INTEGER = new SimpleMessageKey(
            "validator.solution.error.moduleCountNotAnInteger");

        final static MessageKey MODULE_COUNT_NOT_EQUAL_TO_ONE = new SimpleMessageKey(
            "validator.solution.error.moduleCountNotEqualToOne");

        final static MessageKey MORE_THAN_ONE_ADD_MODULE = new SimpleMessageKey(
            "validator.solution.error.moreThanOneAdditionalModule");
    }

    /**
     * Constructs a new instance of this class.
     */
    LinkCapacityValidator() {

        /* nothing to initialize */
    }

    /**
     * This validator checks whether the installed link capacity modules are 
     * admissible in the given solution.<br/>
     * <br/>
     * 
     * The admissibility of the installed capacity modules thereby depends on
     * the link capacity model.<br/>
     * <br/>
     * 
     * For a link which possesses a pre-installed capacity module this method
     * also checks whether there is a corresponding <tt>ModuleConfiguration</tt> 
     * in that link's configuration.
     * 
     * @param solvedProblem the solved problem to validate
     * @param errors the message container into those the errors are put
     * 
     * @see sndlib.core.model.LinkCapacityModel
     */
    public void validate(SolvedProblem solvedProblem, Messages errors) {

        Network network = solvedProblem.getNetwork();

        LinkCapacityModel linkCapacityModel = solvedProblem.getLinkCapacityModel();
        CapacityValidator validator = CapacityValidator.getValidatorFor(linkCapacityModel);

        for(Link link : network.links()) {

            LinkConfiguration linkConf = solvedProblem.getLinkConf(link);

            double preInstalledCapacity = link.getPreCapacity();
            if(!DoubleComparator.getInstance().isZero(preInstalledCapacity)) {
                if(linkConf == null) {
                    errors.add(SNDlibMessages.error(
                        ErrorKeys.NO_LINK_CONF_FOR_LINK_WITH_PRE_INST_CAP,
                        link.getId()));
                }
                else {
                    ModuleConfiguration preInstalledModuleConf = linkConf.getModuleConfig(preInstalledCapacity);
                    if(preInstalledModuleConf == null
                        || DoubleComparator.getInstance().less(
                            preInstalledModuleConf.getInstallCount(), 1.0)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.NO_PREINSTALLED_CAPACITY_MODULE,
                            linkConf.getLinkId()));
                        continue;
                    }
                }
            }
            if(linkConf != null) {
                validator.validate(linkConf, link, errors);
            }
        }
    }

    /**
     * This is an internal helper class which abstracts the validation from a 
     * specific link capacity module.
     * 
     * @author Roman Klaehne
     */
    private static abstract class CapacityValidator {

        /**
         * Performs the validation in case of modular link capacities.
         */
        static final CapacityValidator MODULAR_CAPACITIES = new CapacityValidator() {

            void validate(LinkConfiguration linkConfig, Link link, Messages errors) {

                for(ModuleConfiguration moduleConf : linkConfig.moduleConfigs()) {

                    double installCount = moduleConf.getInstallCount();

                    /* install count must be an integer not lower than zero */
                    if(!DoubleComparator.getInstance().isInteger(installCount)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.MODULE_COUNT_NOT_AN_INTEGER, installCount,
                            moduleConf.getModuleCapacity(), link.getId()));
                    }

                    if(DoubleComparator.getInstance().less(installCount, 0)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.MODULE_COUNT_LOWER_THAN_ZERO, installCount,
                            moduleConf.getModuleCapacity(), link.getId()));
                    }
                }
            }
        };

        /**
         * Performs the validation in case of linear link capacities.
         */
        static final CapacityValidator LINEAR_CAPACITIES = new CapacityValidator() {

            void validate(LinkConfiguration linkConfig, Link link, Messages errors) {

                for(ModuleConfiguration moduleConf : linkConfig.moduleConfigs()) {

                    double installCount = moduleConf.getInstallCount();
                    if(DoubleComparator.getInstance().less(installCount, 0)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.MODULE_COUNT_LOWER_THAN_ZERO, installCount,
                            moduleConf.getModuleCapacity(), link.getId()));
                    }
                }
            }
        };

        /** 
         * Performs the validation in case of explicit link capacities.
         */
        static final CapacityValidator EXPLICIT_CAPACITIES = new CapacityValidator() {

            void validate(LinkConfiguration linkConfig, Link link, Messages errors) {

                double preInstalledCapacity = link.getPreCapacity();

                /* it can be at most the preinstalled plus one additional module installed */
                boolean preInstalledModuleOut = false;
                boolean addModuleOut = false;
                for(ModuleConfiguration moduleConf : linkConfig.moduleConfigs()) {

                    double installCount = moduleConf.getInstallCount();

                    /* installCount should be 0 or 1, thus integer */
                    if(!DoubleComparator.getInstance().isInteger(installCount)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.MODULE_COUNT_NOT_AN_INTEGER, installCount,
                            moduleConf.getModuleCapacity(), link.getId()));
                    }

                    if(DoubleComparator.getInstance().equals(
                        moduleConf.getModuleCapacity(), preInstalledCapacity)
                        && !preInstalledModuleOut) {
                        installCount -= 1;
                        preInstalledModuleOut = true;
                        if(DoubleComparator.getInstance().isZero(installCount)) {
                            continue;
                        }
                    }

                    if(!DoubleComparator.getInstance().equals(installCount, 1.0)) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.MODULE_COUNT_NOT_EQUAL_TO_ONE, installCount,
                            moduleConf.getModuleCapacity(), link.getId()));
                        break;
                    }

                    if(addModuleOut) {
                        errors.add(SNDlibMessages.error(
                            ErrorKeys.MORE_THAN_ONE_ADD_MODULE, link.getId()));
                        break;
                    }
                    addModuleOut = true;
                }
            }
        };

        /**
         * Validates the capacity modules installed on the given link 
         * independently from a specific link capacity model.
         * 
         * @param linkConfig the configuration of the link
         * @param link the link
         * @param errors the message container into those the errors are put
         */
        abstract void validate(LinkConfiguration linkConfig, Link link,
            Messages errors);

        /**
         * Returns the appropriate validator for the specified link capacity
         * model.
         * 
         * @param linkCapacityModel the link capacity model
         * 
         * @return the appropriate validator for the specified link capacity
         * model 
         */
        static CapacityValidator getValidatorFor(LinkCapacityModel linkCapacityModel) {

            CapacityValidator validator = null;

            switch (linkCapacityModel) {

            case LINEAR_LINK_CAPACITIES:
                validator = LINEAR_CAPACITIES;
                break;

            case MODULAR_LINK_CAPACITIES:
                validator = MODULAR_CAPACITIES;
                break;

            case EXPLICIT_LINK_CAPACITIES:
                validator = EXPLICIT_CAPACITIES;
                break;

            default:
                throw new AssertionError("unknown link capacity model: "
                    + linkCapacityModel);
            }

            return validator;
        }
    }
}
