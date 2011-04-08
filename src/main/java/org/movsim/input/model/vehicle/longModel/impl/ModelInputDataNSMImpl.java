/**
 * Copyright (C) 2010, 2011 by Arne Kesting <movsim@akesting.de>, 
 *                             Martin Treiber <treibi@mtreiber.de>,
 *                             Ralph Germ <germ@ralphgerm.de>,
 *                             Martin Budden <mjbudden@gmail.com>
 *
 * ----------------------------------------------------------------------
 * 
 *  This file is part of 
 *  
 *  MovSim - the multi-model open-source vehicular-traffic simulator 
 *
 *  MovSim is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MovSim is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MovSim.  If not, see <http://www.gnu.org/licenses/> or
 *  <http://www.movsim.org>.
 *  
 * ----------------------------------------------------------------------
 */
package org.movsim.input.model.vehicle.longModel.impl;

import java.util.Map;

import org.movsim.input.model.vehicle.longModel.ModelInputDataNSM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ModelInputDataNSMImpl extends ModelInputDataImpl implements ModelInputDataNSM{
    
    final static Logger logger = LoggerFactory.getLogger(ModelInputDataNSMImpl.class);
    
    private double v0; // desired velocity (cell units/time unit)
    private double pTroedel; // Troedelwahrscheinlichkeit
    private double pSlowToStart; // slow-to-start rule (Barlovic)

    // dt = 1 constant 
    
    
    public ModelInputDataNSMImpl(String modelName, Map<String, String> map) {
        super(modelName);
        this.v0 = Double.parseDouble(map.get("v0"));
        this.pTroedel = Double.parseDouble(map.get("p_troedel"));
        this.pSlowToStart = Double.parseDouble(map.get("p_slow_start"));
        if(pSlowToStart < pTroedel){
            logger.error("slow to start logic requires pSlowToStart > pTroedel, but input {} < {} ", pSlowToStart, pTroedel);
            System.exit(-1);
        }
        
        if( v0<0 || pTroedel<0 || pSlowToStart <0 ){
            logger.error(" negative parameter values for {} not defined in input. please choose positive values. exit", modelName);
            System.exit(-1);
        }
    }
    
    /* (non-Javadoc)
     * @see org.movsim.input.model.vehicle.longModel.impl.ModelInputDataNSM#getV0()
     */
    public double getV0() {
        return v0;
    }

    /* (non-Javadoc)
     * @see org.movsim.input.model.vehicle.longModel.impl.ModelInputDataNSM#getP()
     */
    public double getTroedel() {
        return pTroedel;
    }

    /* (non-Javadoc)
     * @see org.movsim.input.model.vehicle.longModel.impl.ModelInputDataNSM#getP0()
     */
    public double getSlowToStart() {
        return pSlowToStart;
    }

}