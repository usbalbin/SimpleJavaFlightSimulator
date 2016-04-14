package se.liu.ida.albhe417.tddd78.game.GameObject.Misc;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.*;
import se.liu.ida.albhe417.tddd78.game.GameObject.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

/**
 * Created by Albin_Hedman on 2016-03-30.
 */
public class ProjectileMesh extends AbstractGameObject{
    private GameObjectPart part;
    public ProjectileMesh(float radius, final int shaderProgram, DynamicsWorld physics, Game game){
        super(new Vector3(), physics, game, Float.POSITIVE_INFINITY, "");
        setup(radius, shaderProgram);

    }

    private void setup(float radius, final int shaderProgram){
        Vector3 color = new Vector3(1, 1, 0);
        int qualityFactor = 4;

        ArrayList<VertexPositionColorNormal> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        Helpers.createNormalSphere(vertices, indices, radius, color, qualityFactor);

        VertexPositionColorNormal[] vertexArray = new VertexPositionColorNormal[vertices.size()];
        int[] indexArray;

        vertices.toArray(vertexArray);
        indexArray = indices.stream().mapToInt(i -> i).toArray();

        part = new GameObjectPart(vertexArray, indexArray, shaderProgram, null);
        parts = new ArrayList<>(1);
        parts.add(part);
    }

    public void setBullet(RigidBody bullet){
        part.setPhysicsObject(bullet);
    }

    public void hit(Target target){

    }

    @Override
    public void destroy() {
        for (GameObjectPart part : parts) {
            part.destroyGraphics();
        }
        game.remove(this);
    }
}
