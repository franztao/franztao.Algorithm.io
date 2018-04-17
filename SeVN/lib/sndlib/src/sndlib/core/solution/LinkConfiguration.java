/*
 * $Id: LinkConfiguration.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.solution;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.atesio.utils.ArgChecker;
import com.atesio.utils.StringFormatUtils;

/**
 * This class represents the configuration of a single link in a specific
 * {@link Solution}.<br/><br/>
 * 
 * A link configuration comprises the information how often each capacity 
 * module is installed. A single capacity module together with its install 
 * count is represented by a {@link ModuleConfiguration}.
 * <br/><br/>
 * 
 * For every link contained in the final solution topology there must be a 
 * <tt>LinkConfiguration</tt>.
 *
 * If a link has a pre-installed capacity module, then an explicitly given 
 * <tt>ModuleConfiguration</tt> for that module must be included in its
 * corresponding <tt>LinkConfiguration</tt>. 
 * <br/><br/>
 *
 * For constructing a new <tt>LinkConfiguration</tt> a parent 
 * <tt>Solution</tt> is needed.
 * 
 * @see Solution
 * @see ModuleConfiguration
 * @see sndlib.core.network.Link
 * 
 * @author Roman Klaehne
 */
final public class LinkConfiguration {

    /**
     * The ID of the corresponding network link.
     */
    private String _linkId;

    /**
     * The module configurations. 
     */
    private Map<Double, ModuleConfiguration> _moduleConfigs;

    /**
     * Constructs a new <tt>LinkConfiguration</tt> for the link with the
     * specified ID.
     * 
     * @param linkId the ID of the corresponding network link
     */
    LinkConfiguration(String linkId) {

        _linkId = linkId;
        _moduleConfigs = new LinkedHashMap<Double, ModuleConfiguration>();
    }

    /**
     * Adds a new <tt>ModuleConfiguration</tt> to this link configuration.
     * 
     * @param moduleConfig the module configuration 
     * 
     * @throws IllegalStateException if there is already a configuration 
     * for a module with the specified capacity in this link configuration 
     */
    public void addModuleConfig(ModuleConfiguration moduleConfig) {

        ArgChecker.checkNotNull(moduleConfig, "module configuration");

        if(hasModuleConfig(moduleConfig.getModuleCapacity())) {
            throw new IllegalStateException(
                "this link configuration already contains a configuration"
                    + " for a module with capacity "
                    + moduleConfig.getModuleCapacity());
        }

        _moduleConfigs.put(moduleConfig.getModuleCapacity(), moduleConfig);
    }

    /**
     * Returns <tt>true</tt> if there is a configuration for the module
     * with the specified capacity.
     * 
     * @param capacity the capacity of the module
     * 
     * @return <tt>true</tt> if and only if there is such a module 
     * configuration; <tt>false</tt> otherwise
     */
    public boolean hasModuleConfig(double capacity) {

        return _moduleConfigs.containsKey(capacity);
    }

    /**
     * Returns the configuration of the module with the specified capacity.
     * 
     * @param capacity the capacity of the module
     * 
     * @return the configuration of the module with the specified capacity;
     * <tt>null</tt> if there is no such configuration
     */
    public ModuleConfiguration getModuleConfig(double capacity) {

        return _moduleConfigs.get(capacity);
    }

    /**
     * Returns the number of module configurations in this link configuration.
     * 
     * @return the number of module configurations
     */
    public int moduleConfigCount() {

        return _moduleConfigs.size();
    }

    /**
     * Returns a <tt>Collection</tt> view of the module configurations in 
     * this link configuration.<br/><br/>
     * 
     * The returned collection is unmodifiable. That means that each attempt 
     * to modify the collection will cause an 
     * <tt>UnsupportedOperationException</tt>.
     * 
     * @return an unmodifiable collection view of the module configurations 
     * in this link configuration. 
     */
    public Collection<ModuleConfiguration> moduleConfigs() {

        return Collections.unmodifiableCollection(_moduleConfigs.values());
    }

    /**
     * Returns the ID of the corresponding network link.
     * 
     * @return the ID of the corresponding network link
     */
    public String getLinkId() {

        return _linkId;
    }

    /**
     * Returns a textual representation of this link configuration.
     * 
     * @return a textual representation
     */
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append("linkConfiguration [\n");
        result.append(" linkId           = " + _linkId + "\n\n");
        result.append(" moduleConfigurations [\n");

        for(ModuleConfiguration modConf : _moduleConfigs.values()) {
            result.append(StringFormatUtils.indentByLine(modConf.toString(), 2)
                + "\n");
        }

        result.append(" ]\n");
        result.append("]");

        return result.toString();
    }
}

