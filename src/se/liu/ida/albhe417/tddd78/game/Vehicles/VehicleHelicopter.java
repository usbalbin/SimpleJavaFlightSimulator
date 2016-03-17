package se.liu.ida.albhe417.tddd78.game.Vehicles;

import se.liu.ida.albhe417.tddd78.game.Terrain;
import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Created by Albin on 11/03/2016.
 */
abstract class VehicleHelicopter extends AbstractVehicle {
    public VehicleHelicopter(final Vector3 position, float yaw, float mass, float thrustFactor, final Terrain terrain) {
        super(position, yaw, mass, thrustFactor, terrain);
    }

    //TODO: implement me
    public void updateAerodynamics(){

    }
}
