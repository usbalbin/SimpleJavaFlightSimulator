package se.liu.ida.albhe417.tddd78.game;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.HeightfieldTerrainShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.game_object_Part.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Project TDDD78
 *
 * File created by Albin on 14/03/2016.
 */
public class TerrainLOD extends AbstractGameObject{

    private final Settings settings;
    private GameObjectPart partMain;
    private final DynamicsWorld physics;

    private QuadTree quadTree;

    private int[] indexArray;
    private final List<VertexPositionColorNormal> vertices;
    private final List<Integer> indices;

    private static final int MAX_EXPECTED_VERTEX_COUNT = 100000;

    public TerrainLOD(
        Vector3 position, final float heightFactor,
        Settings settings, final int shaderProgram, DynamicsWorld physics, Game game
    ) {
        super(position, physics, game, heightFactor, "Ground");
        this.settings = settings;
        this.vertices = new ArrayList<>(MAX_EXPECTED_VERTEX_COUNT);
        this.indices = new ArrayList<>(MAX_EXPECTED_VERTEX_COUNT * 4);
        this.physics = physics;
        setup(shaderProgram);
    }

    private void setup(int shaderProgram) {
        Heightmap heightmap;


        heightmap = new Heightmap(settings.getTerrainImage());

        quadTree = new QuadTree(heightmap, settings);
        final int size = heightmap.size;

        this.parts = new ArrayList<>();
        partMain = new GameObjectPart(shaderProgram, MAX_EXPECTED_VERTEX_COUNT, new VertexPositionColorNormal());
        this.parts.add(partMain);

        //Physics
        MotionState motionState = new DefaultMotionState();
        CollisionShape shape = new HeightfieldTerrainShape(size, size, heightmap.getHeights(),
                                                           heightmap.heightFactor, heightmap.minHeight, heightmap.maxHeight, 1, true
        );

        RigidBody physicsObjectMain = new RigidBody(0f, motionState, shape);
        physicsObjectMain.setUserPointer(this);
        partMain.setPhysicsObject(physicsObjectMain);
        physics.addRigidBody(physicsObjectMain);
    }



    public void update(Vector3 cameraPos, Matrix4x4 cameraMatrix){

        vertices.clear();
        indices.clear();

        quadTree.update(cameraPos, cameraMatrix, vertices, indices);

        indexArray = indices.parallelStream().mapToInt(i -> i).toArray();
    }

    public void updateGraphics(){
        partMain.updateData(vertices, indexArray);
    }


}
