package se.liu.ida.albhe417.tddd78.game.game_object_part;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.graphics.VertexPositionColorNormal;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wing might be added to any game object where a cube-shaped part with aerodynamics are desired.
 *
 * Wing is cube-shaped implementation of the GameObjectPart it has aerodynamics based on the shape of the cube and
 * its surfaces. The drawing and rest of the physics is handled by the inherited methods.
 *
 * Thus the main task of the Wing class is to setup a wing and calculate its aerodynamics.
 */
public class Wing extends GameObjectPart {
    private final float mass;
    private final Vector3 size;
    private static final int NUM_VERTICES_PER_CUBE = 8;

    public Wing(Vector3 offsetPosition, Vector3 size, float mass, Matrix4x4 modelMatrix, int shaderProgram, DynamicsWorld physics, AbstractGameObject parentGameObject) {
        super(shaderProgram, NUM_VERTICES_PER_CUBE, new VertexPositionColorNormal());
        this.mass = mass;
        this.size = size;
        setupGraphics();
        setupPhysics(offsetPosition, modelMatrix, physics, parentGameObject);
    }

    private void setupGraphics(){
        final Vector3 color = new Vector3(0.5f, 0.5f, 0.5f);

        //"LTR" = left top rear
        Vector3 posLTR = new Vector3(-size.getX(), size.getY(), size.getZ());
        Vector3 posRTR = new Vector3(size.getX(), size.getY(), size.getZ());
        Vector3 posRBR = new Vector3(size.getX(), -size.getY(), size.getZ());
        Vector3 posLBR = new Vector3(-size.getX(), -size.getY(), size.getZ());

        Vector3 posLTF = new Vector3(-size.getX(), size.getY(), -size.getZ());
        Vector3 posRTF = new Vector3(size.getX(), size.getY(), -size.getZ());
        Vector3 posRBF = new Vector3(size.getX(), -size.getY(), -size.getZ());
        Vector3 posLBF = new Vector3(-size.getX(), -size.getY(), -size.getZ());

        VertexPositionColorNormal leftTopRear = new VertexPositionColorNormal(posLTR, color, posLTR);
        VertexPositionColorNormal rightTopRear = new VertexPositionColorNormal(posRTR, color, posRTR);
        VertexPositionColorNormal rightBottomRear = new VertexPositionColorNormal(posRBR, color, posRBR);
        VertexPositionColorNormal leftBottomRear = new VertexPositionColorNormal(posLBR, color, posLBR);

        VertexPositionColorNormal leftTopFront = new VertexPositionColorNormal(posLTF, color, posLTF);
        VertexPositionColorNormal rightTopFront = new VertexPositionColorNormal(posRTF, color, posRTF);
        VertexPositionColorNormal rearBottomFront = new VertexPositionColorNormal(posRBF, color, posRBF);
        VertexPositionColorNormal leftBottomFront = new VertexPositionColorNormal(posLBF, color, posLBF);

        List<VertexPositionColorNormal> vertices = new ArrayList<>(Arrays.asList(
            new VertexPositionColorNormal[] {
                leftTopRear, rightTopRear, rightBottomRear, leftBottomRear,
                leftTopFront,  rightTopFront, rearBottomFront, leftBottomFront
            }
        ));

        int[] indices = {
            0, 2, 3, 	0, 1, 2,//Rear
            4, 3, 7, 	4, 0, 3,//Left
            5, 7, 6, 	5, 4, 7,//Front
            1, 6, 2, 	1, 5, 6,//Right
            4, 1, 0, 	4, 5, 1,//Top
            3, 6, 7, 	3, 2, 6	//Bottom
        };

        updateData(vertices, indices);
    }

    private void setupPhysics(Vector3 offsetPosition, Matrix4x4 modelMatrix, DynamicsWorld physics, AbstractGameObject parentGameObject){
        Matrix4x4 partModelMatrix = modelMatrix.multiply(Matrix4x4.createTranslation(offsetPosition));
        Transform transform = new Transform(partModelMatrix.toMatrix4f());
        MotionState motionState = new DefaultMotionState(transform);

        CollisionShape shape = new BoxShape(size.toVector3f());
        Vector3f inertia = new Vector3f();
        shape.calculateLocalInertia(mass, inertia);
        RigidBody physicsObject = new RigidBody(mass, motionState, shape, inertia);
        physicsObject.setUserPointer(parentGameObject);

        setPhysicsObject(physicsObject);
        physics.addRigidBody(physicsObject);
    }

    private void calcAerodynamics(){
        Matrix4x4 partMatrix = getMatrix();

        Vector3f linearVelocity3f = new Vector3f();
        getPhysicsObject().getLinearVelocity(linearVelocity3f);

        float leftRightDrag = -size.getY() * size.getZ();
        float upDownDrag = -size.getX() * size.getZ();
        float frontBackDrag = -size.getX() * size.getY();
        final float dragTuningFactor = 0.1f;


        Vector3 dragCoefficent = new Vector3(leftRightDrag, upDownDrag, frontBackDrag).multiply(dragTuningFactor);
        Vector3 linearVelocity = new Vector3(linearVelocity3f);
        Vector3 modelLinearVelocity = partMatrix.multiply(linearVelocity, false);

        Vector3 modelLinearResistance = modelLinearVelocity.multiply(modelLinearVelocity.abs());
        modelLinearResistance = modelLinearResistance.multiply(dragCoefficent);

        Vector3 linearResistance = getInvertedMatrix().multiply(modelLinearResistance, false);


        Vector3f angularVelocity = new Vector3f();
        getPhysicsObject().getAngularVelocity(angularVelocity);


        getPhysicsObject().applyForce(linearResistance.toVector3f(), new Vector3f());
        if(linearResistance.length2() > 0)
            getPhysicsObject().activate();
    }

    @Override
    public void update() {
        calcAerodynamics();
    }

}