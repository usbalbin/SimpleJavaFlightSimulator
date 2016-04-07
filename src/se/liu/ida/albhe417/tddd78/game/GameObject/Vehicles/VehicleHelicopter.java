package se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.Terrain;
import se.liu.ida.albhe417.tddd78.math.Vector3;

/**
 * Created by Albin on 11/03/2016.
 */
abstract class VehicleHelicopter extends AbstractVehicle {
    public VehicleHelicopter(final Vector3 position, float yaw, float mass, float thrustFactor, DynamicsWorld physics, Game game) {
        super(position, yaw, mass, thrustFactor, physics, game);
    }

    //TODO: implement me
    public void updateAerodynamics(){

    }
}
