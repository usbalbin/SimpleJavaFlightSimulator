package se.liu.ida.albhe417.tddd78.game.GameObject.Misc;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.GameObject.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.GameObjectPart;
import se.liu.ida.albhe417.tddd78.game.Helpers;
import se.liu.ida.albhe417.tddd78.game.VertexPositionColorNormal;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

/**
 * Created by Albin_Hedman on 2016-03-30.
 */
public class Target extends AbstractGameObject{
    public Target(final Vector3 position, final int shaderProgram, DynamicsWorld physics){
        super(position, 0);
        setup(position, shaderProgram, physics);
    }

    private void setup(Vector3 position, final int shaderProgram, DynamicsWorld physics){
        float mass = 1;
        float radius = 1;
        Vector3 color = new Vector3(0, 1, 0);
        int qualityFactor = 10;

        ArrayList<VertexPositionColorNormal> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        Helpers.createNormalSphere(vertices, indices, radius, color, qualityFactor);

        VertexPositionColorNormal[] vertexArray = new VertexPositionColorNormal[vertices.size()];
        int[] indexArray;

        vertices.toArray(vertexArray);
        indexArray = indices.stream().mapToInt(i -> i).toArray();

        //Physics
        Matrix4x4 matrix = Matrix4x4.createTranslation(position);
        Transform transform = new Transform(matrix.toMatrix4f());
        MotionState motionState = new DefaultMotionState(transform);
        SphereShape collisionShape = new SphereShape(radius);

        Vector3f inertia = new Vector3f();
        collisionShape.calculateLocalInertia(mass, inertia);
        RigidBody physicsObject = new RigidBody(mass, motionState, collisionShape, inertia);
        physicsObject.setUserPointer(this);
        physics.addRigidBody(physicsObject);

        GameObjectPart part = new GameObjectPart(vertexArray, indexArray, shaderProgram, physicsObject);
        parts = new ArrayList<>(1);
        parts.add(part);
    }

    public void hit(DynamicsWorld physics){
        if(parts.size() == 0)
            return;
        physics.removeRigidBody(parts.get(0).getPhysicsObject());
    }
}
