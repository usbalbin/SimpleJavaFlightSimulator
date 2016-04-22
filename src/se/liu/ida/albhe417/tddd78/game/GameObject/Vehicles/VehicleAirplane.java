package se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.GameObjectPart.Wing;
import se.liu.ida.albhe417.tddd78.game.InputHandler;
import se.liu.ida.albhe417.tddd78.game.Thruster;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;

/**
 * Project TDDD78
 * <p>
 * File created by Albin on 21/04/2016.
 */
public class VehicleAirplane extends AbstractVehicle{
    private Thruster thruster;

    public VehicleAirplane(Vector3 position, DynamicsWorld physics, Game game, String playerName, int shaderProgram) {
        super(position, 1000, 1000, physics, game, 10000, playerName + "'s Test vehicle");



        float wingThickness = 0.075f;
        float bodyMass = 5;
        Vector3 bodySize = new Vector3(0.5f, 1, 6);

        Wing body = new Wing(Vector3.ZERO, bodySize, bodyMass, modelMatrix, shaderProgram, physics, this);


        float mainWingMass = 1;
        Vector3 mainWingSize        = new Vector3(5, wingThickness, 2.5f);
        Vector3 leftMainWingPos     = new Vector3(+mainWingSize.getX() + bodySize.getX(), -bodySize.getY(), -bodySize.getZ()/100f);
        Vector3 rightMainWingPos    = new Vector3(-mainWingSize.getX() - bodySize.getX(), -bodySize.getY(), -bodySize.getZ()/100f);

        Wing leftMainWing = new Wing(leftMainWingPos, mainWingSize, mainWingMass, modelMatrix, shaderProgram, physics, this);
        TypedConstraint leftWingConstraint = leftMainWing.attachToParentFixed(body, Vector3.ZERO, leftMainWingPos, this);

        Wing rightMainWing = new Wing(leftMainWingPos, mainWingSize, mainWingMass, modelMatrix, shaderProgram, physics, this);
        TypedConstraint rightWingConstraint = rightMainWing.attachToParentFixed(body, Vector3.ZERO, rightMainWingPos, this);


        float elevatorMass = 1;
        Vector3 elevatorSize = new Vector3(2, wingThickness, 1);
        Vector3 elevatorPos = new Vector3(0, -bodySize.getY() - wingThickness, -elevatorSize.getZ() - bodySize.getZ());

        Wing elevator = new Wing(elevatorPos, elevatorSize, elevatorMass, modelMatrix, shaderProgram, physics, this);
        TypedConstraint elevatorConstraint = elevator.attachToParentFixed(body, Vector3.ZERO, elevatorPos, this);



        //float rudderMass = 1;
        //Vector3 rudderSize = new Vector3(wingThickness, 0.5f, 0.5f);
        //Vector3 rudderPos = new Vector3(0, 2, 7);

        //Wing rudder = new Wing(rudderPos, rudderSize, rudderMass, modelMatrix, shaderProgram, physics, this);
        //TypedConstraint rudderConstraint = rudder.attachToParentFixed(body, Vector3.ZERO, rudderPos, this);


        final float thrustFactor = 2000;
        final Vector3 direction = new Vector3(0, 0, -1);
        final Vector3 thrusterPosition = new Vector3(0, 0, -bodySize.getZ());

        thruster = new Thruster(thrusterPosition, direction, thrustFactor, body);

        partBody = body;
        parts.add(partBody);
        parts.add(leftMainWing);
        parts.add(rightMainWing);
        parts.add(elevator);
        //parts.add(rudder);

        constraints.add(leftWingConstraint);
        constraints.add(rightWingConstraint);
        constraints.add(elevatorConstraint);
        //constraints.add(rudderConstraint);

        constraints.forEach(physics::addConstraint);
    }

    @Override
    public void handleInput(float deltaTime) {
        float deltaThrottle = 0.0f;
        float yawValue = 0;
        float pitchValue = 0;
        float rollValue = 0;

        InputHandler input = InputHandler.getInstance();
        if(input.isPressed(GLFW_KEY_W))
            deltaThrottle += 1;
        if(input.isPressed(GLFW_KEY_S))
            deltaThrottle -= 1;

        if(input.isPressed(GLFW_KEY_A))
            yawValue += 1;
        if(input.isPressed(GLFW_KEY_D))
            yawValue -= 1;

        if(input.isPressed(GLFW_KEY_UP))
            pitchValue += 1;
        if(input.isPressed(GLFW_KEY_DOWN))
            pitchValue -= 1;

        if(input.isPressed(GLFW_KEY_RIGHT))
            rollValue += 1;
        if(input.isPressed(GLFW_KEY_LEFT))
            rollValue -= 1;

        //if(input.isPressed(GLFW_KEY_SPACE)) {
        //    weaponLeft.fire(deltaTime);
        //    weaponRight.fire(deltaTime);
        //}

        thruster.update(deltaThrottle, deltaTime);
    }

    @Override
    public void update() {

    }
}
