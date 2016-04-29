package se.liu.ida.albhe417.tddd78.game;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.HeightfieldTerrainShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import se.liu.ida.albhe417.tddd78.game.gameObjectPart.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Project TDDD78
 *
 * File created by Albin on 14/03/2016.
 */
public class TerrainLOD extends Terrain {
    private final se.liu.ida.albhe417.tddd78.game.Settings settings;
    private GameObjectPart partMain;
    private final DynamicsWorld physics;

    private QuadTree quadTree;

    private int[] indexArray;
    private VertexPositionColorNormal[] vertexArray;
    private final List<VertexPositionColorNormal> vertices;
    private final List<Integer> indices;

    private static final int MAX_EXPECTED_VERTEX_COUNT = 100000;

    public TerrainLOD(
        Vector3 position, final float heightFactor,
        Settings settings, final int shaderProgram, DynamicsWorld physics, Game game
    ) {
        super(position, heightFactor, shaderProgram, physics, game);
        this.settings = settings;
        this.vertexArray = new VertexPositionColorNormal[MAX_EXPECTED_VERTEX_COUNT];
        this.vertices = new ArrayList<>(MAX_EXPECTED_VERTEX_COUNT);
        this.indices = new ArrayList<>(MAX_EXPECTED_VERTEX_COUNT * 4);
        this.physics = physics;
        setup();
    }

    private void setup() {
        Heightmap heightmap;


        heightmap = new Heightmap(settings.getTerrainImage());

        quadTree = new QuadTree(heightmap, settings);
        height = width = heightmap.SIZE;

        this.parts = new ArrayList<>();
        partMain = new GameObjectPart(shaderProgram, MAX_EXPECTED_VERTEX_COUNT, new VertexPositionColorNormal());
        this.parts.add(partMain);

        //Physics
        MotionState motionState = new DefaultMotionState();
        CollisionShape shape = new HeightfieldTerrainShape(
            width, height, heightmap.getHeights(),
            heightmap.HEIGHT_FACTOR, heightmap.MIN_HEIGHT, heightmap.MAX_HEIGHT, 1, true
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

        if(vertices.size() > vertexArray.length) {
            vertexArray = new VertexPositionColorNormal[(int)(vertices.size() * 1.1)];
            System.out.println("Warning had to expand vertex array!!!");
        }
        vertices.toArray(vertexArray);
        indexArray = indices.parallelStream().mapToInt(i -> i).toArray();
    }

    public void updateGraphics(){
        partMain.updateData(vertexArray, indexArray);
    }


}
