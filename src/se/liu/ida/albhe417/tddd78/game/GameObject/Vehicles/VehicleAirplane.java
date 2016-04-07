package se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.math.Vector3;

public abstract class VehicleAirplane extends AbstractVehicle
{
    public VehicleAirplane(final Vector3 position, float mass, float thrustFactor, DynamicsWorld physics, Game game) {
        super(position, mass, thrustFactor, physics, game);
    }

    //TODO: implement me
    public void updateAerodynamics(){

    }
}
