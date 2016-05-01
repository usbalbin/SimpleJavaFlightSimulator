package se.liu.ida.albhe417.tddd78.game.game_object_part;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import se.liu.ida.albhe417.tddd78.game.*;
import se.liu.ida.albhe417.tddd78.game.graphics.VertexPositionColorNormal;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A spherical mesh used for efficiently drawing multiple sphere-shaped bullets
 *
 * It is drawable thanks to it inheriting from GameObjectPart. Although its intended use differs from ordinary GameObjectParts.
 * The BulletMesh is best used by setting a bullet as its physics object, drawing it and repeating the process for every
 * bullet of same color and shape to prevent needing multiple meshes. Because of this its destroy method wont destroy its
 * physics object.
 */
public class BulletMesh extends GameObjectPart{
    public BulletMesh(float radius, final int shaderProgram) {
        super(shaderProgram, 0, new VertexPositionColorNormal());
        setup(radius);
    }

    private void setup(float radius){
        final Vector3 color = Vector3.createColor(0xFF, 0x77, 0x00);
        int qualityFactor = 4;

        List<VertexPositionColorNormal> vertices = new ArrayList<>();
        Collection<Integer> indices = new ArrayList<>();

        Helpers.createNormalSphere(vertices, indices, radius, color, qualityFactor);

        int[] indexArray;

        indexArray = indices.stream().mapToInt(i -> i).toArray();

        updateData(vertices, indexArray);
    }

    public void setBullet(RigidBody bullet){
        setPhysicsObject(bullet);
    }

    @Override
    public void destroy(final DynamicsWorld physics) {
        destroyGraphics();
    }
}
