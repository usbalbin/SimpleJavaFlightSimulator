package se.liu.ida.albhe417.tddd78.game.game_object.vehicles;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.*;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;


/**
 * Project TDDD78
 *
 * File created by Albin on 11/03/2016.
 */
public class VehicleAirplaneBox extends AbstractVehicleBox{
    public static final float MASS = 1100.0f;
    public static final float THRUST_FACTOR = 36000.0f;
    public static final int MAX_HEALTH = 20000;

    public VehicleAirplaneBox(final Vector3 position, final int shaderProgram, DynamicsWorld physics, Game game, String playerName){
		//TODO, add constants
		super(position, MASS, THRUST_FACTOR, physics, game, MAX_HEALTH, playerName + "'s AirplaneBox", shaderProgram);
		setup(shaderProgram, physics);
	}

	private void setup(final int shaderProgram, DynamicsWorld physics){
		setupBody(shaderProgram, physics);
	}


	protected void calcAerodynamics(float deltaThrottle, float yawValue, float pitchValue, float rollValue, float deltaTime){
	    final float throttleSensitivity 	= 1000.0f;
	    final float yawSensitivity 		= 10000.0f;    //N/m
	    final float pitchSensitivity 	= 10000.0f;
	    final float rollSensitivity 	= 10000.0f;

	    changeThrottle(deltaThrottle * throttleSensitivity * deltaTime);

	    float lift = throttle * thrustFactor;
	    Matrix4x4 modelMatrix = partBody.getMatrix();

	    Matrix4x4 partMatrix = partBody.getMatrix();
	    Vector3 thrustForce = new Vector3(0, 0, -lift);
	    thrustForce = modelMatrix.multiply(thrustForce, false);//

	    Vector3 thrustForcePoint = new Vector3(0, 0, 0);
	    thrustForcePoint = modelMatrix.multiply(thrustForcePoint, false);

	    Vector3f linearVelocity3f = new Vector3f();
	    partBody.getPhysicsObject().getLinearVelocity(linearVelocity3f);
	    final Vector3 dragFactor = new Vector3(-128, -512, -0.1f);
	    Vector3 linearVelocity = new Vector3(linearVelocity3f);
	    Vector3 modelLinearVelocity = modelMatrix.multiply(linearVelocity, false);

	    Vector3 modelLinearResistance = modelLinearVelocity.multiply(modelLinearVelocity.abs());
	    modelLinearResistance = modelLinearResistance.multiply(dragFactor);

	    Vector3 linearResistance = partBody.getInvertedMatrix().multiply(modelLinearResistance, false);


	    Vector3f angularVelocity = new Vector3f();
	    partBody.getPhysicsObject().getAngularVelocity(angularVelocity);

	    final Vector3 angularResistanceFactor = new Vector3(-10000.0f, -10000.0f, -10000.0f);
	    Vector3 angularResistance = new Vector3(angularVelocity);
	    angularResistance = angularResistance.multiply(angularResistance.abs()).multiply(angularResistanceFactor);

	    Vector3 torque = new Vector3(-pitchValue * pitchSensitivity, yawValue * yawSensitivity, -rollValue * rollSensitivity);
	    torque = partMatrix.multiply(torque, false);

	    partBody.getPhysicsObject().applyForce(thrustForce.toVector3f(), thrustForcePoint.toVector3f());
	    partBody.getPhysicsObject().applyCentralForce(linearResistance.toVector3f());
	    partBody.getPhysicsObject().applyTorque(angularResistance.toVector3f());
	    partBody.getPhysicsObject().applyTorque(torque.toVector3f());
	    partBody.getPhysicsObject().activate();
	}

}
