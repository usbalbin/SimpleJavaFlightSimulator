package se.liu.ida.albhe417.tddd78.game.game_object.vehicles;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import se.liu.ida.albhe417.tddd78.game.game_object_part.Wing;
import se.liu.ida.albhe417.tddd78.game.InputHandler;
import se.liu.ida.albhe417.tddd78.game.Thruster;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A to-be drivable airplane. Not yet fully implemented yet!
 *
 * This class is intended to replace the box airplane when it is finished.
 *
 * The vehicle airplane is an implementation of the AbstractVehicle featuring about half a dozen of parts all with aerodynamics
 * enabled to make the best possible airplane with both good aerodynamics as well as general physics, bending wings accurate
 * collision detection and so on.
 *
 * The task of this class is to implement some airplane specific methods such as handleInput but its main task is setting up
 * all of its parts. Whereas AbstractVehicle and its parents handle most of the rest such as making it visible and holding
 * the actual physics objects.
 */
public class VehicleAirplane extends AbstractVehicle{

    /**
     * Thrust factor of the thruster in Newtons
     */
    public static final int THRUST_FACTOR = 10000;

    /**
     * Max health of the AIRPLANE described in max amount of momentum absorbed during collisions before getting destroyed.
     * Unit is kilogram * meter / sec or Newton / sec
     */
    public static final int MAX_HEALTH = 10000;
    private Thruster thruster;

    public VehicleAirplane(Vector3 position, DynamicsWorld physics, String playerName, int shaderProgram) {
        super(position, physics, MAX_HEALTH, playerName + "'s Test vehicle");
        setupParts(physics, shaderProgram);
    }

    private void setupParts(DynamicsWorld physics, int shaderProgram){
        final float wingThickness = 0.075f;
        final float bodyMass = 100;
        final Vector3 bodySize = new Vector3(1, 1, 4);
        final float wingOffsetZ = bodySize.getZ() / 2.0f;

        Wing body = new Wing(Vector3.ZERO, bodySize, bodyMass, modelMatrix, shaderProgram, physics, this);


        final float tailMass = 2;
        final Vector3 tailSize = new Vector3(0.25f, 0.25f, 3);
        final Vector3 tailPosition = new Vector3(0, 0, bodySize.getZ() + tailSize.getZ());
        final Vector3 tailPoint = new Vector3(0, 0, -tailSize.getZ());
        final Vector3 bodyToLeftPoint = new Vector3(0, 0, bodySize.getZ());

        Wing tail = new Wing(tailPosition, tailSize, tailMass, modelMatrix, shaderProgram, physics, this);
        TypedConstraint tailConstraint = tail.attachToParentFixed(body, tailPoint, bodyToLeftPoint, this);


        setupMainWing(body, bodySize, wingThickness, wingOffsetZ, shaderProgram, physics);
        setupElevator(tail, tailPosition, tailSize, wingThickness, shaderProgram, physics);
        setupRudder(tail, tailPosition, tailSize, wingThickness, shaderProgram, physics);


        final Vector3 thrustDirection = Vector3.FORWARD;

        thruster = new Thruster(thrustDirection, THRUST_FACTOR, body);

        partBody = body;
        parts.add(partBody);
        parts.add(tail);


        constraints.add(tailConstraint);
        constraints.forEach(physics::addConstraint);
    }

    private void setupMainWing(Wing body, Vector3 bodySize, float wingThickness, float wingOffsetZ, int shaderProgram, DynamicsWorld physics){
        final float mainWingMass = 1;
        final Vector3 mainWingSize        = new Vector3(6, wingThickness, 1.5f);
        final Vector3 leftMainWingPos     = new Vector3(-mainWingSize.getX(), +bodySize.getY(), +wingOffsetZ);
        final Vector3 rightMainWingPos    = new Vector3(+mainWingSize.getX(), +bodySize.getY(), +wingOffsetZ);

        final Vector3 leftMainWingPoint   = new Vector3(+mainWingSize.getX(), 0, 0);
        final Vector3 rightMainWingPoint  = new Vector3(-mainWingSize.getX(), 0, 0);

        final Vector3 bodyToLeftWingPoint  = new Vector3(0, bodySize.getY() + 2 * wingThickness, +wingOffsetZ);
        final Vector3 bodyToRightWingPoint = new Vector3(0, bodySize.getY() + 2 * wingThickness, +wingOffsetZ);


        Wing leftMainWing = new Wing(leftMainWingPos, mainWingSize, mainWingMass, modelMatrix, shaderProgram, physics, this);
        TypedConstraint leftWingConstraint = leftMainWing.attachToParentFixed(body, leftMainWingPoint, bodyToLeftWingPoint, this);

        Wing rightMainWing = new Wing(rightMainWingPos, mainWingSize, mainWingMass, modelMatrix, shaderProgram, physics, this);
        TypedConstraint rightWingConstraint = rightMainWing.attachToParentFixed(body, rightMainWingPoint, bodyToRightWingPoint, this);


        constraints.add(leftWingConstraint);
        constraints.add(rightWingConstraint);
        parts.add(leftMainWing);
        parts.add(rightMainWing);
    }

    private void setupElevator(Wing tail, Vector3 tailPosition, Vector3 tailSize, float wingThickness, int shaderProgram, DynamicsWorld physics){
        final float elevatorMass = 0.5f;
        final Vector3 elevatorSize = new Vector3(2, wingThickness, 1);
        final Vector3 elevatorPos = new Vector3(0, tailSize.getY(), elevatorSize.getZ() + tailSize.getZ()).add(tailPosition);

        final Vector3 elevatorPoint       = new Vector3(0, 0, -elevatorSize.getZ());
        final Vector3 tailToElevatorPoint = new Vector3(0, tailSize.getY(), tailSize.getZ());

        Wing elevator = new Wing(elevatorPos, elevatorSize, elevatorMass, modelMatrix, shaderProgram, physics, this);
        TypedConstraint elevatorConstraint = elevator.attachToParentFixed(tail, elevatorPoint, tailToElevatorPoint, this);

        constraints.add(elevatorConstraint);
        parts.add(elevator);
    }

    private void setupRudder(Wing tail, Vector3 tailPosition, Vector3 tailSize, float wingThickness, int shaderProgram, DynamicsWorld physics){
        final float rudderMass = 0.5f;
        final Vector3 rudderSize = new Vector3(wingThickness, 1, 1);
        final Vector3 rudderPos = new Vector3(0, tailSize.getY() + wingThickness, tailSize.getZ()).add(tailPosition);

        final Vector3 rudderPoint = new Vector3(0, 0, -rudderSize.getZ());
        final Vector3 tailToRudderPoint = new Vector3(0, tailSize.getY() + wingThickness, tailSize.getZ());

        Wing rudder = new Wing(rudderPos, rudderSize, rudderMass, modelMatrix, shaderProgram, physics, this);
        TypedConstraint rudderConstraint = rudder.attachToParentFixed(tail, rudderPoint, tailToRudderPoint, this);

        parts.add(rudder);
        constraints.add(rudderConstraint);
    }

    @Override
    public void handleInput(float deltaTime) {
        float deltaThrottle = 0.0f;

        InputHandler input = InputHandler.getInstance();
        if(input.isPressed(GLFW_KEY_W))
            deltaThrottle += 1;
        if(input.isPressed(GLFW_KEY_S))
            deltaThrottle -= 1;

        thruster.update(deltaThrottle, deltaTime);
    }

    @Override
    public void update() {

    }
}
