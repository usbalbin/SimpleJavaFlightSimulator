package se.liu.ida.albhe417.tddd78.game.game_object.weapons;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.game_object_part.GameObjectPart;
import se.liu.ida.albhe417.tddd78.game.Helpers;
import se.liu.ida.albhe417.tddd78.game.graphics.VertexPositionColorNormal;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Static/non-moving target good for target practice
 *
 * The target is sphere shaped non-moving implementation of game object useful for firing at.
 *
 * Due to its inheriting form AbstractGameObject it is drawable and has physics. Although the object is non-moving, so even if
 * other object can collide with it, it wont move unless destroyed.
 *
 * The most important task for this class is setting up itself, an AbstractGameObject with data to form a static sphere.
 * The rest is inherited.
 */
public class Target extends AbstractGameObject{
    public Target(final Vector3 position, final int shaderProgram, DynamicsWorld physics, String targetName){
        super(position, physics, 1, targetName);
        setup(position, shaderProgram, physics);
    }

    private void setup(Vector3 position, final int shaderProgram, DynamicsWorld physics){
        float mass = 0;
        float radius = 5;
        Vector3 color = new Vector3(0, 1, 0);
        int qualityFactor = 10;

        List<VertexPositionColorNormal> vertices = new ArrayList<>();
        Collection<Integer> indices = new ArrayList<>();

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

        GameObjectPart part = new GameObjectPart(vertices, indexArray, shaderProgram, physicsObject);
        parts = new ArrayList<>(1);
        parts.add(part);
    }
}
