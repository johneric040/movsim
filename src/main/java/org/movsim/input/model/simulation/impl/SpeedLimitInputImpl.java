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
package org.movsim.input.model.simulation.impl;

import java.util.Map;

import org.movsim.input.model.simulation.SpeedLimitInput;


public class SpeedLimitInputImpl implements SpeedLimitInput {
	private double x; // in m
    private double speedlimit; // in m/s (SI units)
    
    public SpeedLimitInputImpl(Map<String, String> map) {
        this.x = Double.parseDouble(map.get("x"));
        this.speedlimit = Double.parseDouble(map.get("vlimit_kmh"))/3.6; 
    }


    /* (non-Javadoc)
     * @see org.movsim.input.model.simulation.impl.SpeedLimitInput#getPosition()
     */
    public double getPosition() {
        return x;
    }

    /* (non-Javadoc)
     * @see org.movsim.input.model.simulation.impl.SpeedLimitInput#getSpeedlimit()
     */
    public double getSpeedlimit() {
        return speedlimit;
    }

}