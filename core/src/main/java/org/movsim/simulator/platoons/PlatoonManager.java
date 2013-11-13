package org.movsim.simulator.platoons;

import java.util.Collection;

import javax.annotation.Nullable;

import org.movsim.autogen.PlatoonConfigurationType;
import org.movsim.autogen.PlatoonManagerType;
import org.movsim.simulator.SimulationTimeStep;
import org.movsim.simulator.roadnetwork.RoadNetwork;
import org.movsim.simulator.roadnetwork.RoadSegment;
import org.movsim.simulator.roadnetwork.RoadSegmentUtils;
import org.movsim.simulator.vehicles.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

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

        for (PlatoonConfigurationType platoonConfiguration : configuration.getPlatoonConfiguration()) {
            String label = platoonConfiguration.getPlatoonVehicle().getLabel();
            Predicate<Vehicle> isEquippedVehicle = new IsEquippedVehicle(label);
            for (RoadSegment roadSegment : roadNetwork) {
                LOG.info("roadSegment={}", roadSegment);
                // TODO get sorted positions over lanes from RoadSegmentUtils
                Collection<Vehicle> equippedVehicles = RoadSegmentUtils.sortVehiclesIncreasingPosition(Iterators
                        .filter(roadSegment.iterator(), isEquippedVehicle));
                // Collection<Vehicle> equippedVehicles = RoadSegmentUtils.sortVehiclesDecreasingPosition(Iterators
                // .filter(roadSegment.iterator(), isEquippedVehicle));
                showVehicles(label, equippedVehicles);

                if (iterationCount == 100) {
                    System.out.println(100);
                }
                Vehicle veh;
                // veh.getPlatoonSettings().setAlphaT(0.2); // TODO enable in vehicle acc calc
            }
        }

    }

    private static void showVehicles(String label, Collection<Vehicle> vehicles) {
        for (Vehicle veh : vehicles) {
            LOG.info("look for label={}, found equipped vehicle = {}", label, veh.toString());
        }
        
    }

    private class IsEquippedVehicle implements Predicate<Vehicle> {
        private final String label;
        public IsEquippedVehicle(String label) {
            Preconditions.checkArgument(label != null && !label.isEmpty());
            this.label = label;
        }

        @Override
        public boolean apply(@Nullable Vehicle vehicle) {
            return label.equals(vehicle.getLabel());
        }

    }

}
