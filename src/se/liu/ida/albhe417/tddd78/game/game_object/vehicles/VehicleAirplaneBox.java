package se.liu.ida.albhe417.tddd78.game.game_object.vehicles;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.*;
import se.liu.ida.albhe417.tddd78.game.game_object_part.Wing;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;


/**
 * Very simple box-shaped, drivable airplane
 */
public class VehicleAirplaneBox extends AbstractVehicleBox{

	/**
	 * Mass of an AIRPLANE in kilograms
	 */
	public static final float MASS = 1100.0f;

	/**
	 * Thrust factor of the thruster in Newtons
	 */
	public static final float THRUST_FACTOR = 36000.0f;

	/**
	 * Max health of the AIRPLANE described in max amount of momentum absorbed during collisions before getting destroyed.
	 * Unit is kilogram * meter / sec or Newton / sec
	 */
	public static final int MAX_HEALTH = 20000;
	protected Thruster thruster;

	public VehicleAirplaneBox(final Vector3 position, final int shaderProgram, DynamicsWorld physics, String playerName){
		super(position, MASS, THRUST_FACTOR, physics, MAX_HEALTH, playerName + "'s AIRPLANE_BOX", shaderProgram);
		setup(shaderProgram, physics);
	}

	private void setup(final int shaderProgram, DynamicsWorld physics){
		setupBoxBody(shaderProgram, physics);
		Vector3 thrustDirection = Vector3.FORWARD;
		thruster = new Thruster(thrustDirection, thrustFactor, (Wing)partBody);
	}


	protected void calcHandling(float deltaThrottle, float yawValue, float pitchValue, float rollValue, float deltaTime){
		final float yawSensitivity 		= 10000.0f;    //N/m
		final float pitchSensitivity 	= 10000.0f;
		final float rollSensitivity 	= 10000.0f;


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
