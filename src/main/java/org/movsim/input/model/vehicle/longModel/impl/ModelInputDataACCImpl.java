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

import org.movsim.input.model.vehicle.longModel.ModelInputDataACC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ModelInputDataACCImpl extends ModelInputDataImpl implements ModelInputDataACC{
    
    final static Logger logger = LoggerFactory.getLogger(ModelInputDataACCImpl.class);
    
    private final double v0;
    private final double T;
    private final double s0;
    private final double s1;
    private final double delta;
    private final double a;
    private final double b;
    private final double coolness;
    
    
    public ModelInputDataACCImpl(String modelName, Map<String,String> map) {
        super(modelName);
        this.v0 = Double.parseDouble(map.get("v0"));
        this.T  = Double.parseDouble(map.get("T"));
        this.s0 = Double.parseDouble(map.get("s0"));
        this.s1 = Double.parseDouble(map.get("s1"));
        this.delta = Double.parseDouble(map.get("delta"));
        this.a  = Double.parseDouble(map.get("a"));
        this.b  = Double.parseDouble(map.get("b"));
        this.coolness = Double.parseDouble(map.get("coolness"));
        if(coolness<0 || coolness > 1){
            logger.error(" coolness parameter = {} not well defined in input. please choose value within [0,1]. exit");
            System.exit(-1);
        }
        if(v0<0 || T<0 || s0<0 || s1<0 || delta<0 || a<0 || b<0){
            logger.error(" negative parameter values for {} not defined in input. please choose positive values. exit", modelName);
            System.exit(-1);
        }
    }

    /* (non-Javadoc)
     * @see org.movsim.input.model.vehicle.longModel.impl.ModelInputDataACC#getV0()
     */
    public double getV0(){ return v0; }
    /* (non-Javadoc)
     * @see org.movsim.input.model.vehicle.longModel.impl.ModelInputDataACC#getT()
     */
    public double getT(){ return T; }
    /* (non-Javadoc)
     * @see org.movsim.input.model.vehicle.longModel.impl.ModelInputDataACC#getS0()
     */
    public double getS0(){ return s0; }
    /* (non-Javadoc)
     * @see org.movsim.input.model.vehicle.longModel.impl.ModelInputDataACC#getS1()
     */
    public double getS1(){ return s1; }
    /* (non-Javadoc)
     * @see org.movsim.input.model.vehicle.longModel.impl.ModelInputDataACC#getDelta()
     */
    public double getDelta(){ return delta; }
    /* (non-Javadoc)
     * @see org.movsim.input.model.vehicle.longModel.impl.ModelInputDataACC#getA()
     */
    public double getA(){ return a; }
    /* (non-Javadoc)
     * @see org.movsim.input.model.vehicle.longModel.impl.ModelInputDataACC#getB()
     */
    public double getB(){ return b; }
    /* (non-Javadoc)
     * @see org.movsim.input.model.vehicle.longModel.impl.ModelInputDataACC#getCoolness()
     */
    public double getCoolness(){ return coolness; }
    
}