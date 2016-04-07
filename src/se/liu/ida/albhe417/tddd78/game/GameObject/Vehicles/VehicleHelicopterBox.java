package se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.*;
import se.liu.ida.albhe417.tddd78.game.GameObject.Misc.Gun;
import se.liu.ida.albhe417.tddd78.game.GameObject.Misc.Weapon;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Albin on 11/03/2016.
 */
public class VehicleHelicopterBox extends VehicleHelicopter {
    private Weapon weapon;

    public VehicleHelicopterBox(final Vector3 position, float yaw, final int shaderProgram, DynamicsWorld physics, Game game){
        //TODO, add constants
        super(position, yaw, 1000.0f, 20000.0f, physics, game);
        setup(shaderProgram, physics);
        this.weapon = new Gun(new Vector3(0, 0, -2), this, physics, shaderProgram, game);
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

        if(input.isPressed(GLFW_KEY_SPACE))
            weapon.fire(deltaTime);

        calcAerodynamics(deltaTrottle, yawValue, pitchValue, rollValue, deltaTime);
    }

    private void calcAerodynamics(float deltaTrottle, float yawValue, float pitchValue, float rollValue, float deltaTime){
        final float throttleSensetivity = 500f;
        final float yawSensetivity = 500f;    //N/m
        final float pitchSensetivity = 500f;
        final float rollSensetivity = 500f;

        changeThrottle(deltaTrottle * throttleSensetivity * deltaTime);

        float lift = throttle * THRUST_FACTOR;

        Matrix4x4 partMatrix = partBody.getMatrix();
        Vector3 aeroForce = new Vector3(0, lift, 0);
        aeroForce = partMatrix.getInverse().multiply(aeroForce, false);//

        Vector3 forcePoint = new Vector3(0, 1, 0);
        forcePoint = partMatrix.getInverse().multiply(forcePoint, true);

        Vector3f linearVelocity = new Vector3f();
        partBody.getPhysicsObject().getLinearVelocity(linearVelocity);
        Vector3 v = new Vector3(-10f);
        Vector3 linearResistence = new Vector3(linearVelocity);
        linearResistence = linearResistence.multiply(linearResistence.abs());
        linearResistence = linearResistence.multiply(v);


        Vector3f angularVelocity = new Vector3f();
        partBody.getPhysicsObject().getAngularVelocity(angularVelocity);
        Vector3 angularResistence = new Vector3(angularVelocity);
        angularResistence = angularResistence.multiply(angularResistence.abs()).multiply(new Vector3(-10000f, -10000f, -10000f));

        Vector3 torque = new Vector3(-pitchValue * pitchSensetivity, yawValue * yawSensetivity, -rollValue * rollSensetivity);
        torque = partMatrix.getInverse().multiply(torque, false);

        partBody.getPhysicsObject().applyForce(aeroForce.toVector3f(), forcePoint.toVector3f());
        partBody.getPhysicsObject().applyCentralForce(linearResistence.toVector3f());
        partBody.getPhysicsObject().applyTorque(angularResistence.toVector3f());
        partBody.getPhysicsObject().applyTorque(torque.toVector3f());
        partBody.getPhysicsObject().activate();
    }

    private void setupBody(final int shaderProgram, DynamicsWorld physics){
        final Vector3 red = 	new Vector3(1, 0, 0);
        final Vector3 green =	new Vector3(0, 1, 0);
        final Vector3 blue = 	new Vector3(0, 0, 1);
        final Vector3 white = 	new Vector3(1, 1, 1);

        final Vector3 SIZE = new Vector3(2, .5f, 2);

        //"LTR" = left top rear
        Vector3 posLTR = new Vector3(-SIZE.getX(), SIZE.getY(), SIZE.getZ());
        Vector3 posRTR = new Vector3( SIZE.getX(), SIZE.getY(), SIZE.getZ());
        Vector3 posRBR = new Vector3( SIZE.getX(),-SIZE.getY(), SIZE.getZ());
        Vector3 posLBR = new Vector3(-SIZE.getX(),-SIZE.getY(), SIZE.getZ());

        Vector3 posLTF = new Vector3(-SIZE.getX(), SIZE.getY(), -SIZE.getZ());
        Vector3 posRTF = new Vector3( SIZE.getX(), SIZE.getY(), -SIZE.getZ());
        Vector3 posRBF = new Vector3( SIZE.getX(),-SIZE.getY(), -SIZE.getZ());
        Vector3 posLBF = new Vector3(-SIZE.getX(),-SIZE.getY(), -SIZE.getZ());

        VertexPositionColorNormal LTR = new VertexPositionColorNormal(posLTR, red, posLTR);
        VertexPositionColorNormal RTR = new VertexPositionColorNormal(posRTR, green, posRTR);
        VertexPositionColorNormal RBR = new VertexPositionColorNormal(posRBR, blue, posRBR);
        VertexPositionColorNormal LBR = new VertexPositionColorNormal(posLBR, white, posLBR);

        VertexPositionColorNormal LTF = new VertexPositionColorNormal(posLTF, red, posLTF);
        VertexPositionColorNormal RTF = new VertexPositionColorNormal(posRTF, green, posRTF);
        VertexPositionColorNormal RBF = new VertexPositionColorNormal(posRBF, blue, posRBF);
        VertexPositionColorNormal LBF = new VertexPositionColorNormal(posLBF, white, posLBF);

        VertexPositionColorNormal[] vertices = {
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

        CollisionShape shape = new BoxShape(SIZE.toVector3f());
        Vector3f inertia = new Vector3f();
        shape.calculateLocalInertia(MASS, inertia);
        RigidBody physicsObject = new RigidBody(MASS, motionState, shape, inertia);
        physicsObject.setUserPointer(this);

        physics.addRigidBody(physicsObject);

        partBody = new GameObjectPart(vertices, indices, shaderProgram, physicsObject);
        parts.add(partBody);
    }

    public void update(float deltaTime){

    }

    @Override public void draw(Matrix4x4 cameraMatrix, int MVPmatrixId, int modelMatrixId){
        super.draw(cameraMatrix, MVPmatrixId, modelMatrixId);
        weapon.draw(cameraMatrix, MVPmatrixId, modelMatrixId);
    }
}
