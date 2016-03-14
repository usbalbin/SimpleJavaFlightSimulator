package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Albin on 11/03/2016.
 */
public class VehicleHelicopterBox_withNormals extends VehicleHelicopter{
    public VehicleHelicopterBox_withNormals(final Vector3 position, float yaw, final Terrain terrain, final int shaderProgram){
        //TODO, add constants
        super(position, yaw, 1000.0f, 200000.0f, terrain);
        setup(shaderProgram);
    }

    private void setup(final int shaderProgram){
        ArrayList<AbstractDrawablePart> parts = new ArrayList<>();
        parts.add(setupBody(shaderProgram));


        setupParts(parts);
    }

    public void handleInput(float deltaTime){


        //TODO: byta ut allt mot global modelMatrix
        float deltaTrottle = 0f;
        float yawValue = 0;
        float pitchValue = 0;
        float rollValue = 0;

        InputHandler input = InputHandler.getInstance();
        if(input.isPressed(GLFW_KEY_W))
            deltaTrottle += 1;
        if(input.isPressed(GLFW_KEY_S))
            deltaTrottle -= 1;

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


        calcAerodynamics(deltaTrottle, yawValue, pitchValue, rollValue, deltaTime);
    }

    private void calcAerodynamics(float deltaTrottle, float yawValue, float pitchValue, float rollValue, float deltaTime){
        final float throttleSensetivity = 1f;
        final float yawSensetivity = 0.001f;
        final float pitchSensetivity = 0.001f;
        final float rollSensetivity = 0.001f;

        changeThrottle(deltaTrottle * throttleSensetivity * deltaTime);

        float lift = throttle * THRUST_FACTOR;

        Vector3 aeroForce = new Vector3(0, lift, 0);
        aeroForce = modelMatrix.getInverse().multiply(new Vector4(aeroForce, 0.0f)).toVector3();//

        //aeroForce = aeroForce.getRotated(yawValue, pitchValue, rollValue);
        Vector3 acceleration = aeroForce.divide(MASS);
        acceleration = acceleration.add(GRAVITY);

        System.out.println(acceleration);

        velocity = velocity.add(acceleration.multiply(deltaTime));

        Vector3 position = modelMatrix.getPosition();
        position = position.add(velocity.multiply(deltaTime));



        modelMatrix = modelMatrix.getRotated(yawValue * yawSensetivity, pitchValue * pitchSensetivity, rollValue * rollSensetivity);
        modelMatrix.setPosition(position);
    }

    private DrawablePartPosColor setupBody(final int shaderProgram){
        final Vector3 red = 	new Vector3(1, 0, 0);
        final Vector3 green =	new Vector3(0, 1, 0);
        final Vector3 blue = 	new Vector3(0, 0, 1);
        final Vector3 white = 	new Vector3(1, 1, 1);

        final float SIZE = 1f;

        //"LTR" = left top rear
        Vector3 posLTR = new Vector3(-SIZE, SIZE, SIZE);
        Vector3 posRTR = new Vector3( SIZE, SIZE, SIZE);
        Vector3 posRBR = new Vector3( SIZE,-SIZE, SIZE);
        Vector3 posLBR = new Vector3(-SIZE,-SIZE, SIZE);

        Vector3 posLTF = new Vector3(-SIZE, SIZE, -SIZE);
        Vector3 posRTF = new Vector3( SIZE, SIZE, -SIZE);
        Vector3 posRBF = new Vector3( SIZE,-SIZE, -SIZE);
        Vector3 posLBF = new Vector3(-SIZE,-SIZE, -SIZE);


        Vector3 normalLTR = new Vector3(-SIZE, SIZE, SIZE);
        Vector3 normalRTR = new Vector3( SIZE, SIZE, SIZE);
        Vector3 normalRBR = new Vector3( SIZE,-SIZE, SIZE);
        Vector3 normalLBR = new Vector3(-SIZE,-SIZE, SIZE);

        Vector3 normalLTF = new Vector3(-SIZE, SIZE, -SIZE);
        Vector3 normalRTF = new Vector3( SIZE, SIZE, -SIZE);
        Vector3 normalRBF = new Vector3( SIZE,-SIZE, -SIZE);
        Vector3 normalLBF = new Vector3(-SIZE,-SIZE, -SIZE);

        VertexPositionNormalColor LTR = new VertexPositionNormalColor(posLTR, normalLTR, red);
        VertexPositionNormalColor RTR = new VertexPositionNormalColor(posRTR, normalRTR, green);
        VertexPositionNormalColor RBR = new VertexPositionNormalColor(posRBR, normalRBR, blue);
        VertexPositionNormalColor LBR = new VertexPositionNormalColor(posLBR, normalLBR, white);

        VertexPositionNormalColor LTF = new VertexPositionNormalColor(posLTF, normalLTF, red);
        VertexPositionNormalColor RTF = new VertexPositionNormalColor(posRTF, normalRTF, green);
        VertexPositionNormalColor RBF = new VertexPositionNormalColor(posRBF, normalRBF, blue);
        VertexPositionNormalColor LBF = new VertexPositionNormalColor(posLBF, normalLBF, white);

        VertexPositionNormalColor[] vertices = {
            LTR, RTR, RBR, LBR,
            LTF, RTF, RBF, LBF
        };


        int[] indices = {
            0, 2, 3, 	0, 1, 2,//Rear
            4, 3, 7, 	4, 0, 3,//Left
            5, 7, 6, 	5, 4, 7,//Front
            1, 6, 2, 	1, 5, 6,//Right
            4, 1, 0, 	4, 5, 1,//Top
            3, 6, 7, 	3, 2, 6	//Bottom
        };

        //TODO fix me
        //return new DrawablePartPosNormalColor(vertices, indices, shaderProgram);
        return null;
    }

    public void update(float deltaTime){
        float airPlaneHeight = 1;
        Vector3 position = modelMatrix.getPosition();

        float terrainHeight = terrain.getHeight(position.getX(), position.getZ());
        if( position.getY() < terrainHeight + airPlaneHeight){
            position.setY(terrainHeight + airPlaneHeight);
            modelMatrix.setPosition(position);
            velocity.setY(0);
        }
    }
}
