package se.liu.ida.albhe417.tddd78.game.Vehicles;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.*;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Albin on 11/03/2016.
 */
public class VehicleHelicopterBox_normals extends VehicleHelicopter {

    public VehicleHelicopterBox_normals(final Vector3 position, float yaw, final Terrain terrain, final int shaderProgram, DynamicsWorld physics){
        //TODO, add constants
        super(position, yaw, 1000.0f, 20000.0f, terrain);
        setup(shaderProgram, physics);
    }

    private void setup(final int shaderProgram, DynamicsWorld physics){
        this.parts = new ArrayList<>();
        setupBody(shaderProgram, physics);
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
        final float throttleSensetivity = 100f;
        final float yawSensetivity = 100f;    //N/m
        final float pitchSensetivity = 100f;
        final float rollSensetivity = 100f;

        changeThrottle(deltaTrottle * throttleSensetivity * deltaTime);

        float lift = throttle * THRUST_FACTOR;

        Matrix4x4 partMatrix = partBody.getMatrix();
        Vector3 aeroForce = new Vector3(0, lift, 0);
        aeroForce = partMatrix.getInverse().multiply(aeroForce);//

        Vector3 forcePoint = new Vector3(0, 1, 0);
        forcePoint = partMatrix.getInverse().multiply(forcePoint);

        Vector3 torque = new Vector3(-pitchValue * pitchSensetivity, yawValue * yawSensetivity, -rollValue * rollSensetivity);
        torque = partMatrix.getInverse().multiply(torque);

        partBody.getPhysicsObject().applyForce(aeroForce.toVector3f(), forcePoint.toVector3f());
        partBody.getPhysicsObject().applyTorque(torque.toVector3f());
        partBody.getPhysicsObject().activate();
    }

    private void setupBody(final int shaderProgram, DynamicsWorld physics){
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

        VertexPositionNormalColor LTR = new VertexPositionNormalColor(posLTR, posLTR, red);
        VertexPositionNormalColor RTR = new VertexPositionNormalColor(posRTR, posRTR, green);
        VertexPositionNormalColor RBR = new VertexPositionNormalColor(posRBR, posRBR, blue);
        VertexPositionNormalColor LBR = new VertexPositionNormalColor(posLBR, posLBR, white);

        VertexPositionNormalColor LTF = new VertexPositionNormalColor(posLTF, posLTF, red);
        VertexPositionNormalColor RTF = new VertexPositionNormalColor(posRTF, posRTF, green);
        VertexPositionNormalColor RBF = new VertexPositionNormalColor(posRBF, posRBF, blue);
        VertexPositionNormalColor LBF = new VertexPositionNormalColor(posLBF, posLBF, white);

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

        //Physics

        Transform transform = new Transform(modelMatrix.toMatrix4f());
        MotionState motionState = new DefaultMotionState(transform);

        CollisionShape shape = new BoxShape(new Vector3(SIZE).toVector3f());
        Vector3f inertia = new Vector3f();
        shape.calculateLocalInertia(MASS, inertia);
        RigidBody physicsObject = new RigidBody(MASS, motionState, shape, inertia);

        physics.addRigidBody(physicsObject);

        partBody = new GameObjectPart(vertices, indices, shaderProgram, physicsObject);
        parts.add(partBody);
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
