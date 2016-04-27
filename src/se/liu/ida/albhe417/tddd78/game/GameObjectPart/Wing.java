package se.liu.ida.albhe417.tddd78.game.gameObjectPart;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.dynamics.constraintsolver.*;
import se.liu.ida.albhe417.tddd78.game.gameObject.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.VertexPositionColorNormal;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;

/**
 * Project TDDD78
 * <p>
 * File created by Albin on 21/04/2016.
 */
public class Wing extends GameObjectPart {
    private final float MASS;
    private final Vector3 SIZE;
    private static final int NUM_VERTICES_PER_CUBE = 8;

    public Wing(Vector3 offsetPosition, Vector3 size, float mass, Matrix4x4 modelMatrix, int shaderProgram, DynamicsWorld physics, AbstractGameObject parentGameObject) {
        super(shaderProgram, NUM_VERTICES_PER_CUBE, new VertexPositionColorNormal());
        this.MASS = mass;
        this.SIZE = size;
        setup(offsetPosition, modelMatrix, physics, parentGameObject);
    }

    public Generic6DofConstraint attachToParentFixed(GameObjectPart parent, Vector3 parentConnectionPoint, Vector3 thisConnectionPoint, AbstractGameObject parentGameObject){
        Transform parentConnection   = new Transform(Matrix4x4.createTranslation(new Vector3(parentConnectionPoint)).toMatrix4f());
        Transform thisConnection     = new Transform(Matrix4x4.createTranslation(new Vector3(thisConnectionPoint)).toMatrix4f());

        Generic6DofConstraint constraint = new Generic6DofConstraint(parent.getPhysicsObject(), this.getPhysicsObject(), thisConnection, parentConnection, false);

        final Vector3f zero = Vector3.ZERO.toVector3f();
        constraint.setAngularLowerLimit(zero);
        constraint.setAngularUpperLimit(zero);

        constraint.setLinearLowerLimit(zero);
        constraint.setLinearUpperLimit(zero);

        parentGameObject.addConnection(constraint);

        return constraint;
    }

    public HingeConstraint attachToParentHinge(GameObjectPart parent, Vector3 parentConnectionPoint, Vector3 thisConnectionPoint, Vector3 pivotAxis, float maxLimit, float minLimit){
        HingeConstraint constraint = new HingeConstraint(parent.getPhysicsObject(), this.getPhysicsObject(), parentConnectionPoint.toVector3f(), thisConnectionPoint.toVector3f(), pivotAxis.toVector3f(), pivotAxis.toVector3f());

        constraint.setLimit(minLimit, maxLimit);

        return constraint;
    }

    private void setup(Vector3 offsetPosition, Matrix4x4 modelMatrix, DynamicsWorld physics, AbstractGameObject parentGameObject){
        final Vector3 red = 	new Vector3(0, 0, 0.5f);//new Vector3(1, 0, 0);
        final Vector3 green =	new Vector3(0, 0, 0.5f);//new Vector3(0, 1, 0);
        final Vector3 blue = 	new Vector3(0, 0, 0.5f);//new Vector3(0, 0, 1);
        final Vector3 white = 	new Vector3(0, 0, 0.5f);//new Vector3(1, 1, 1);

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

        updateData(vertices, indices);

        //Physics

        Matrix4x4 partModelMatrix = modelMatrix.multiply(Matrix4x4.createTranslation(offsetPosition));
        Transform transform = new Transform(partModelMatrix.toMatrix4f());
        MotionState motionState = new DefaultMotionState(transform);

        CollisionShape shape = new BoxShape(SIZE.toVector3f());
        Vector3f inertia = new Vector3f();
        shape.calculateLocalInertia(MASS, inertia);
        RigidBody physicsObject = new RigidBody(MASS, motionState, shape, inertia);
        physicsObject.setUserPointer(parentGameObject);

        setPhysicsObject(physicsObject);
        physics.addRigidBody(physicsObject);
    }

    private void calcAerodynamics(){
        Matrix4x4 partMatrix = getMatrix();

        Vector3f linearVelocity3f = new Vector3f();
        getPhysicsObject().getLinearVelocity(linearVelocity3f);

        float leftRightDrag = -SIZE.getY() * SIZE.getZ();
        float upDownDrag = -SIZE.getX() * SIZE.getZ();
        float frontBackDrag = -SIZE.getX() * SIZE.getY();
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


    public void update() {
        calcAerodynamics();
    }

}