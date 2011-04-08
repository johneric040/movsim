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
package org.movsim.simulator.vehicles.longmodels.impl;

import org.movsim.input.model.vehicle.longModel.ModelInputDataACC;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.longmodels.LongModelCategory;
import org.movsim.simulator.vehicles.longmodels.LongitudinalModel;

// Reference for constant-acceleration heuristic:
// Arne Kesting, Martin Treiber, Dirk Helbing
// Enhanced Intelligent Driver Model to access the impact of driving strategies on traffic capacity
// Philosophical Transactions of the Royal Society A 368, 4585-4605 (2010)

// Reference for improved intelligent driver extension:
// book ... 
 
public class ACC extends LongitudinalModelImpl implements LongitudinalModel{
    
	private double v0;  //  desired velocity (m/s)
    private double T;   // time headway (s)
    private double s0; // bumper-to-bumper distance in jams or queues
    private double s1;
    private double a;   // acceleration (m/s^2)
    private double b;   // comfortable (desired) deceleration (m/s^2)
    private double delta; // acceleration exponent
    private double coolness; // factor in [0, 1]
    // coolness=0: acc1=IIDM (without CAH), coolness=1 CAH
    
    //ModelInputDataACC parameters;
    
    public ACC(String modelName, ModelInputDataACC parameters){
        super(modelName, LongModelCategory.CONTINUOUS_MODEL);
        this.v0 = parameters.getV0();
        this.T = parameters.getT();
        this.s0 = parameters.getS0();
        this.s1 = parameters.getS1();
        this.a = parameters.getA();
        this.b = parameters.getB();
        this.delta = parameters.getDelta();
        this.coolness = parameters.getCoolness();
    }
    
    
    // copy constructor
    public ACC(ACC accToCopy){
        super(accToCopy.modelName(), accToCopy.getModelCategory());
        this.v0 = accToCopy.getV0();
        this.T = accToCopy.getT();
        this.s0 = accToCopy.getS0();
        this.s1 = accToCopy.getS1();
        this.a = accToCopy.getA();
        this.b = accToCopy.getB();
        this.delta = accToCopy.getDelta();
        this.coolness = accToCopy.getCoolness(); 
    }
    
    
    
    public double acc(Vehicle me, VehicleContainer vehContainer, double alphaT, double alphaV0, double alphaA) {
        
        // TODO kommentare checken/loeschen ... Martin!
        
        // Local dynamical variables
        final Vehicle vehFront = vehContainer.getLeader(me);
        final double s  = me.netDistance(vehFront); 
        final double v  = me.speed(); 
        final double dv = me.relSpeed(vehFront);
        final double aLeadDummy = 0;  
        final double a_lead = (vehFront == null) ? aLeadDummy : vehFront.acc();

        // space dependencies modelled by speedlimits, alpha's
        // space dependencies modeled by speedlimits, alpha's

        final double Tloc  = alphaT*T; 
        final double v0Loc = Math.min(alphaV0*v0, me.speedlimit());  // consider external speedlimit
        final double aLoc = alphaA*a;

//        double sstar  = s0 + Tloc*v + s1*Math.sqrt((v+0.0001)/v0loc) + (0.5*v*dv)/Math.sqrt(a*b);
//
////        if(sstar<s0+0.2*v*Tloc){
////            sstar=s0+0.2*v*Tloc;
////        }
//        
//        
//        if(sstar<s0){ sstar=s0;}  
//        
//
//        double aWanted = a*( 1.- Math.pow((v/v0loc), delta) - (sstar/s)*(sstar/s));
//
//        //return  Math.max(aWanted, -BMAX);
//        return aWanted;

        // -------------------------------------------------------------
        
        // IDM: effectives Tmin/T selbst wenn Vorderfz s. schnell entfernt
        final double sstarmin_vT = 0.1; // 0.1 ... 0.3  bisher 0.2
        
      
        
        //########################################################
        // IDM acceleration
        //########################################################
        
        final double v0_sstar = 20;   // lediglich Normierung, dass s1 Einheit m
        double sstar  = s0 + Tloc*v + s1*Math.sqrt((v+0.01)/v0_sstar)+ (0.5*v*dv)/Math.sqrt(aLoc*b);
        
        // CAH und IDM:  Kleinster Wert von (s-s0)/s0  (seff=s-s0 im Nenner)
        // "Nicht-glattes" Verhalten falls seffmin_s0 > 0.5
        final double seffmin_s0  = 0.2;  // 0.1 ... 0.5 
        final double sstarmin=seffmin_s0*s0+sstarmin_vT*v*Tloc;
        if(sstar<sstarmin){
            sstar=sstarmin;
        }
        
        final double tau_relax=20.;  // physiolog. Relaxationszeit, falls v>v0
        double accFree= (v<v0Loc) 
          ? aLoc * (1.- Math.pow((v/v0Loc),delta))
          : (v0Loc-v)/tau_relax; 
        
        //  Unterdruecke accFree, falls dv^2/2s Bremsverzoeg. >=b impliziert
        // (Das FD bleibt unveraendert)
        final double b_kin=0.5*Math.max(dv,0.0)*dv/s;
        accFree *= Math.max(1-b_kin/b,0.0);
        
        final double accIDM = accFree - aLoc *sstar*sstar/(s*s);
        
        //########################################################
        // CAH deceleration (constant-acceleration heuristics) 
        //########################################################

        // Unterdruecke CAH- "Mitzieheffekte", falls Vordermann staerker
        // als gewuenscht beschleunigt
 
        
        // CAH
        
//        final double dvp = Math.max(dv, 0.0);
//        final double v_lead = v-dvp;
//        final double sEff = Math.max(s-s0, seffmin_s0*s0); 
//        final boolean smin_at_stop = (v_lead*dvp  < - 2 * sEff*a_lead_eff);
//        final double denom =  v_lead*v_lead - 2 * sEff*a_lead_eff;
//        
//        final double accCAH    = ( smin_at_stop && ( denom!=0 ))
//          ? v*v*a_lead_eff/denom
//          : a_lead_eff - 0.5*dvp*dvp/sEff;

        //######################################################
        // Mischen von IDM, CAH ("Beste aller Welten abzueglich b")
        //######################################################
        
        // coolness=0: acc1=acc2=accIDM; coolness=1 (VLA):  acc1=accCAH
  //      final double acc1     = coolness*accCAH + (1.-coolness)*accIDM;
        
//        // folgende 3 Zeilen erlauben schnelle (IDM-) Beschl. am Stauende
//        final double db1     = 0.2*aLoc;
//        final double shiftb1 = 0.0*aLoc;  // shift to negative values of accIDM
//        final double delta_b = b*0.5*(Math.tanh((-accIDM-shiftb1)/db1)+1);
//        //delta_b  = b;  //!!! Deaktivieren der "delta" Manipulation
//        
//        double acc2 = - delta_b + maxSmooth(accIDM, acc1, delta_b);

        //######################################################
        // Philosophical Transactions of the Royal Society A 368, 4585-4605 (2010)
        //######################################################
        

        final double accLeadMax = Math.max(0.0, 1.1*accIDM); 
        final double a_lead_eff= Math.min(accLeadMax, a_lead);
        
        final double dvp = Math.max(dv, 0.0);
        final double v_lead = v-dvp;
        
        final double denomNew =  v_lead*v_lead - 2 * s * a_lead;

        final double accCAHNew = ( (v_lead*dvp  < - 2 * s * a_lead_eff) &&(denomNew!=0))
          ? v*v*a_lead/denomNew
          : Math.min(a,a_lead) - 0.5*dvp*dvp/Math.max(s, 0.0001);

        double acc2 = (accIDM>accCAHNew)
          ? accIDM
          : (1-coolness)*accIDM + coolness*( accCAHNew+b*Math.tanh((accIDM-accCAHNew)/b));

        //######################################################
        // Verallgemeinerung der  vereinfachten Formulierung auf exaktes Einhalten
        // von s0+vT im Gleichgewicht fuer v<=v0fuer acc2=acc ohne Jerk
        // IIDM --> Book 
        //######################################################

        final double z=sstar/Math.max(s,0.01);
        final double accEmpty=(v<=v0) ? a*(1- Math.pow((v/v0),delta))
          : -b*(1- Math.pow((v0/v),a*delta/b));
        final double accPos=accEmpty*(1.- Math.pow(z, Math.min(2*a/accEmpty, 100.))  );
        final double accInt=a*(1-z*z);

        final double accIDMnew=(v<v0) 
          ?  (z<1) ? accPos : accInt 
          :  (z<1) ? accEmpty : accInt+ accEmpty;

          
        final double acc2New2=(accIDMnew>accCAHNew)
          ? accIDMnew
          : (1-coolness)*accIDMnew + coolness*( accCAHNew+b*Math.tanh((accIDMnew-accCAHNew)/b));

        acc2=acc2New2; 

        return(acc2);  
    }

    public double accSimple(double s, double v, double dv){
      double sstar  = s0 + T*v + 0.5*v*dv /Math.sqrt(a*b); // desired distance
      sstar += s1*Math.sqrt((v+0.000001)/v0);
      if(sstar<s0){ sstar=s0;}
      final double aWanted = a * ( 1.- Math.pow((v/v0),delta) - (sstar/s)*(sstar/s));
      return aWanted;
    }

//    private double maxSmooth(double x1, double x2, double dx){
//      return 0.5*(x1+x2) + Math.sqrt(0.25*(x1-x2)*(x1-x2) + dx*dx);
//    }
//
//    private double minSmooth(double x1, double x2, double dx){
//      return 0.5*(x1+x2) - Math.sqrt(0.25*(x1-x2)*(x1-x2) + dx*dx);
//    }


    
    public double getV0(){ return v0;}
    public double getT(){ return T; }
    public double getS0(){ return s0;}
    public double getS1(){ return s1;}
    public double getDelta(){ return delta;}
    public double getA(){ return a;}
    public double getB(){ return b;}

    public double parameterV0() {
        return v0;
    }

    public double getCoolness(){
        return coolness;
    }


	@Override
	public double getRequiredUpdateTime() {
		return 0; // continuous model requires no specific timestep
	}

}