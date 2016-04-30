package se.liu.ida.albhe417.tddd78.game.game_object.misc;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import se.liu.ida.albhe417.tddd78.game.*;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.game_object_part.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Project TDDD78
 *
 * File created by Albin on 2016-03-30.
 */
public class ProjectileMesh extends AbstractGameObject{
    private GameObjectPart part;
    public ProjectileMesh(float radius, final int shaderProgram, DynamicsWorld physics){
        super(new Vector3(0), physics, Float.POSITIVE_INFINITY, "");
        setup(radius, shaderProgram);
    }

    private void setup(float radius, final int shaderProgram){
        Vector3 color = new Vector3(1, 1, 0);
        int qualityFactor = 4;

        List<VertexPositionColorNormal> vertices = new ArrayList<>();
        Collection<Integer> indices = new ArrayList<>();

        Helpers.createNormalSphere(vertices, indices, radius, color, qualityFactor);

        int[] indexArray;

        indexArray = indices.stream().mapToInt(i -> i).toArray();

        part = new GameObjectPart(vertices, indexArray, shaderProgram, null);
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
