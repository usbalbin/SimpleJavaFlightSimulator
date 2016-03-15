package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;

public abstract class VehicleAirplane extends AbstractVehicle
{
    public VehicleAirplane(final Vector3 position, float yaw, float mass, float thrustFactor, final Terrain_old terrain) {
        super(position, yaw, mass, thrustFactor, terrain);
    }

    //TODO: implement me
    public void updateAerodynamics(){

    }
}
