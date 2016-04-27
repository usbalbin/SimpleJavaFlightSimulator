package se.liu.ida.albhe417.tddd78.game.gameObject.misc;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import se.liu.ida.albhe417.tddd78.game.*;
import se.liu.ida.albhe417.tddd78.game.gameObject.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.gameObjectPart.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;

/**
 * Project TDDD78
 *
 * File created by Albin on 2016-03-30.
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

    @Override
    public void destroy() {
        parts.forEach(GameObjectPart::destroyGraphics);
    }
}
