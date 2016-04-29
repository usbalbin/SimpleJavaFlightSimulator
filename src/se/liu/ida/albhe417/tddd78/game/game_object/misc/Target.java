package se.liu.ida.albhe417.tddd78.game.game_object.misc;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.game_object_Part.GameObjectPart;
import se.liu.ida.albhe417.tddd78.game.Helpers;
import se.liu.ida.albhe417.tddd78.game.VertexPositionColorNormal;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Project TDDD78
 *
 * File created by Albin on 2016-03-30.
 */
public class Target extends AbstractGameObject{
    public Target(final Vector3 position, final int shaderProgram, DynamicsWorld physics, Game game, String targetName){
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
