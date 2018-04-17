/*
 * $Id: Meta.java 631 2011-04-27 08:16:04Z bzfraack $
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

package sndlib.core.network;

import com.atesio.utils.ArgChecker;

/**
 * This class represents meta information for a network and its demands
 * 
 * @see Network
 * 
 * @author Christian Raack
 */
final public class Meta {


    /**
     * The granularity of the demands (or null), that is, the period in which the demands have been averaged
     */
    private String _granularity;

    /**
     * Point in time demand data has been taken or null
     */
    private String _time;

    /**
     * Unit of the demands, typically Mbits or null
     */
    private String _unit;
    
    /**
     * Origin of the network (and demands) or null
     */
    private String _origin;
    
   

    /**
     * Constructs a meta object.
     * 
     */
    public Meta(){
    	
       this._granularity = null;
       this._time = null;
       this._unit = null;
       this._origin = null;
    	
    	
    }

    
    /**
     * Returns the granularity of the demands
     * (over which time horizon has the demand been averaged)
     *
     * @return the granularity
     */
    public String getGranularity() {
    
        return _granularity;
    }


    /**
     * Returns the point in time the demand has been taken
     * 
     * @return the point in time
     */
    public String getTime() {
    
        return _time;
    }


    /**
     * Returns the demand value unit
     * 
     * @return the demand value unit
     */
    public String getUnit() {
    
        return _unit;
    }


    /**
     * Returns the data origin (nework and or demands)
     * 
     * @return the data origin
     */
    public String getOrigin() {
    
        return _origin;
    }


    /**
     * Sets the granularity of the demands
     * (over which time horizon has the demand been averaged)
     *
     * @param granularity the new granularity
     */
    public void setGranularity(String granularity) {

        ArgChecker.checkNotNull(granularity, "granularity");
        this._granularity = granularity;
    }

    
    /**
     * Sets the point in time the demand has been taken
     *
     * @param time the new time
     */
    public void setTime(String time) {

        ArgChecker.checkNotNull(time, "time");
        this._time = time;
    }   
    
    
    /**
     * Sets the data origin (nework and or demands)
     *
     * @param origin the new origin
     */
    public void setOrigin(String origin) {
    
        ArgChecker.checkNotNull(origin, "origin");
        this._origin = origin;
    }    
    
    
    /**
     * Sets the demand value unit
     *
     * @param unit the new unit
     */
    public void setUnit(String unit) {
    
        ArgChecker.checkNotNull(unit, "unit");
        this._unit = unit;
    }
}
