package se.liu.ida.albhe417.tddd78.game.terrain;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.GenericHeightfieldTerrainShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import se.liu.ida.albhe417.tddd78.game.Settings;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.game_object_part.GameObjectPart;
import se.liu.ida.albhe417.tddd78.game.graphics.VertexPositionColorNormal;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * TerrainLOD is a structure holding a terrain with auto adjusting level of detail depending on camera position.
 *
 * The terrain heights are loaded from a heightmap image and stored in this objects Heightmap object. The heightmap is then
 * used to create the physics object as well as the QuadTree.
 *
 * For the graphics, the main underlying structure is the QuadTree which generates the terrains mesh from the heightmap.
 * This mesh is then inserted into the terrains GameObjectPart, in order to then be drawn as usual using inherited methods
 * from AbstractGameObject.
 */
public class TerrainLOD_procedural extends AbstractGameObject{

    private final Settings settings;
    private GameObjectPart partMain;
    private final DynamicsWorld physics;

    private QuadTree_procedural quadTree;

    private int[] indexArray = null;
    private final List<VertexPositionColorNormal> vertices;
    private final List<Integer> indices;

    private static final int MAX_EXPECTED_VERTEX_COUNT = 100000;

    public TerrainLOD_procedural(Vector3 position, final float heightFactor, Settings settings, final int shaderProgram, DynamicsWorld physics) {
        super(position, physics, heightFactor, "Ground");
        this.settings = settings;
        this.vertices = new ArrayList<>(MAX_EXPECTED_VERTEX_COUNT);
        this.indices = new ArrayList<>(MAX_EXPECTED_VERTEX_COUNT * 4);
        this.physics = physics;
        setup(shaderProgram);
    }

    private void setup(int shaderProgram) {
        Heightmap_completely_procedural heightmap;


        heightmap = new Heightmap_completely_procedural(16);

        quadTree = new QuadTree_procedural(heightmap, settings);

        this.parts = new ArrayList<>();
        partMain = new GameObjectPart(shaderProgram, MAX_EXPECTED_VERTEX_COUNT, new VertexPositionColorNormal());
        this.parts.add(partMain);

        //Physics
        MotionState motionState = new DefaultMotionState();
        CollisionShape shape = new GenericHeightfieldTerrainShape(heightmap, 1, true);

        RigidBody physicsObjectMain = new RigidBody(0.0f, motionState, shape);
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

    //Must be called form main same thread that set up the graphics (currently the main thread in Game object)
    public void updateGraphics(){
        partMain.updateData(vertices, indexArray);
    }


}
