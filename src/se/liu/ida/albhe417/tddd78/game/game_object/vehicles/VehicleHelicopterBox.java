package se.liu.ida.albhe417.tddd78.game.game_object.vehicles;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.*;
import se.liu.ida.albhe417.tddd78.game.game_object_part.Wing;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;


/**
 * Project TDDD78
 *
 * File created by Albin on 11/03/2016.
 */
public class VehicleHelicopterBox extends AbstractVehicleBox{

    /**
     * Mass of an helicopter in kilograms
     */
    public static final float MASS = 1000.0f;

    /**
     * Thrust factor of the thruster in Newtons
     */
    public static final float THRUST_FACTOR = 20000.0f;

    /**
     * Max health of the AIRPLANE described in max amount of momentum absorbed during collisions before getting destroyed.
     * Unit is kilogram * meter / sec or Newton / sec
     */
    public static final int MAX_HEALTH = 10000;
    protected Thruster thruster;

    public VehicleHelicopterBox(final Vector3 position, final int shaderProgram, DynamicsWorld physics, String playerName){
        super(position, MASS, THRUST_FACTOR, physics, MAX_HEALTH, playerName + "s' HELICOPTER_BOX", shaderProgram);
        setup(shaderProgram, physics);
    }

    private void setup(final int shaderProgram, DynamicsWorld physics){
        setupBoxBody(shaderProgram, physics);
        Vector3 thrustDirection = Vector3.UP;
        thruster = new Thruster(thrustDirection, thrustFactor, (Wing)partBody);
    }


    protected void calcHandling(float deltaThrottle, float yawValue, float pitchValue, float rollValue, float deltaTime){
        final float yawSensitivity = 1000.0f;    //N/m
        final float pitchSensitivity = 1000.0f;
        final float rollSensitivity = 1000.0f;

        thruster.update(deltaThrottle, deltaTime);

        Vector3f angularVelocity = new Vector3f();
        partBody.getPhysicsObject().getAngularVelocity(angularVelocity);

        final Vector3 angularResistanceFactor = new Vector3(-10000.0f, -10000.0f, -10000.0f);
        Vector3 angularResistance = new Vector3(angularVelocity);
        angularResistance = angularResistance.multiply(angularResistance.abs()).multiply(angularResistanceFactor);

        Vector3 torque = new Vector3(-pitchValue * pitchSensitivity, yawValue * yawSensitivity, -rollValue * rollSensitivity);
        torque = partBody.getMatrix().multiply(torque, false);

        partBody.getPhysicsObject().applyTorque(angularResistance.toVector3f());
        partBody.getPhysicsObject().applyTorque(torque.toVector3f());
        partBody.getPhysicsObject().activate();
    }


}
