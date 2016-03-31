package se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles;

import se.liu.ida.albhe417.tddd78.game.Terrain;
import se.liu.ida.albhe417.tddd78.math.Vector3;

public abstract class VehicleAirplane extends AbstractVehicle
{
    public VehicleAirplane(final Vector3 position, float yaw, float mass, float thrustFactor) {
        super(position, yaw, mass, thrustFactor);
    }

    //TODO: implement me
    public void updateAerodynamics(){

    }
}
