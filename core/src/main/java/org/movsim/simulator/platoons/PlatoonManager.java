package org.movsim.simulator.platoons;

import org.movsim.autogen.PlatoonManagerType;
import org.movsim.simulator.SimulationTimeStep;
import org.movsim.simulator.roadnetwork.RoadNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class PlatoonManager implements SimulationTimeStep {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(PlatoonManager.class);

    private final PlatoonManagerType configuration;

    private final RoadNetwork roadNetwork;

    public PlatoonManager(PlatoonManagerType configuration, RoadNetwork roadNetwork) {
        this.configuration = Preconditions.checkNotNull(configuration);
        this.roadNetwork = Preconditions.checkNotNull(roadNetwork);
    }

    @Override
    public void timeStep(double dt, double simulationTime, long iterationCount) {
        LOG.info("platoon manager update of {} roadSegments", roadNetwork.size());

    }

}
