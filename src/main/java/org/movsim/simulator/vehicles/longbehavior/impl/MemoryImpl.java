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
package org.movsim.simulator.vehicles.longbehavior.impl;

import org.movsim.input.model.vehicle.behavior.MemoryInputData;
import org.movsim.simulator.vehicles.longbehavior.Memory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// Resignation or Memory effect, see paper:
// M. Treiber, D. Helbing:
// Memory effects in microscopic traffic models and wide scattering in flow-density data
// Physical Review E 68, 046119 (2003)

public class MemoryImpl implements Memory {

    final static Logger logger = LoggerFactory.getLogger(MemoryImpl.class);

    // parameters: 
    private double tau; // relaxation time 

    private double resignationMinAlphaA; 

    private double resignationMinAlphaV0;

    private double resignationMaxAlphaT;

    // dynamic state variables:
    
    private double alphaA;

    private double alphaV0;

    private double alphaT;

    public MemoryImpl(MemoryInputData parameters) {
        // parameters
        tau = parameters.getTau();
        resignationMaxAlphaT = parameters.getResignationMaxAlphaT();
        resignationMinAlphaV0 = parameters.getResignationMinAlphaV0();
        resignationMinAlphaA = parameters.getResignationMinAlphaA();

        // init
        alphaA = 1;
        alphaV0 = 1;
        alphaT = 1;

    }

    public void update(double dt, double v, double v0) {
        // exponential moving average
        final double gamma = Math.exp(-dt / tau);

        // level of service function
        double vRel = v / v0;

        // integration of alpha-factors
        alphaT = gamma * alphaT + (1 - gamma) * (resignationMaxAlphaT + vRel * (1. - resignationMaxAlphaT));
        alphaV0 = gamma * alphaV0 + (1 - gamma) * (resignationMinAlphaV0 + vRel * (1. - resignationMinAlphaV0));
        alphaA = gamma * alphaA + (1 - gamma) * (resignationMinAlphaA + vRel * (1. - resignationMinAlphaA));

        logger.debug("vRel = {}, v0 = {}", vRel, v0);
        logger.debug("alphaT = {}, alphaV0 = {}", alphaT, alphaV0);

    }

    public double alphaA() {
        return alphaA;
    }

    public double alphaV0() {
        return alphaV0;
    }

    public double alphaT() {
        return alphaT;
    }

}