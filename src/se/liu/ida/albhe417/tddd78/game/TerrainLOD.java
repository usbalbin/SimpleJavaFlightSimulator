package se.liu.ida.albhe417.tddd78.game;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.HeightfieldTerrainShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Albin on 14/03/2016.
 */
public class TerrainLOD extends Terrain {
    private final se.liu.ida.albhe417.tddd78.game.Settings settings;
    private GameObjectPart partMain;
    private final DynamicsWorld physics;

    private QuadTree quadTree;

    //TODO: kolla upp
    private float[] heights;

    private int[] indexArray;
    private VertexPositionColorNormal[] vertexArray;
    private List<VertexPositionColorNormal> vertices;
    private final List<Integer> indices;

    private static final int MAX_EXPECTED_VERTEX_COUNT = 100000;

    public TerrainLOD(Vector3 position, final float heightFactor, Settings settings, final int shaderProgram, DynamicsWorld physics, Game game) {
        super(position, heightFactor, shaderProgram, physics, game);
        this.settings = settings;
        this.vertexArray = new VertexPositionColorNormal[MAX_EXPECTED_VERTEX_COUNT];
        this.vertices = new ArrayList<>(MAX_EXPECTED_VERTEX_COUNT);
        this.indices = new ArrayList<>(1000);
        this.physics = physics;
        setup();
    }

    protected void setup() {
        float maxHeight = 256f * 256f * HEIGHT_FACTOR;

        heights = Helpers.shortImageToFloatHeights("content/heightmap4k.png");
        quadTree = new QuadTree(heights, HEIGHT_FACTOR, maxHeight, settings);
        height = width = quadTree.getHmapSize();

        this.parts = new ArrayList<>(1);
        partMain = new GameObjectPart(shaderProgram, MAX_EXPECTED_VERTEX_COUNT, new VertexPositionColorNormal());
        this.parts.add(partMain);
        //Physics
        MotionState motionState = new DefaultMotionState();
        CollisionShape shape = new HeightfieldTerrainShape(
                width, height, heights, HEIGHT_FACTOR, 0, maxHeight, 1, true
        );

        RigidBody physicsObjectMain = new RigidBody(0f, motionState, shape);
        physicsObjectMain.setUserPointer(this);
        partMain.setPhysicsObject(physicsObjectMain);
        physics.addRigidBody(physicsObjectMain);
    }
















    public void update(Vector3 cameraPos, Matrix4x4 cameraMatrix){
        Matrix4x4 MVPmatrix = cameraMatrix.multiply(new Matrix4x4());

        vertices.clear();
        indices.clear();

        quadTree.update(cameraPos, MVPmatrix, vertices, indices);

        if(vertices.size() > vertexArray.length) {
            vertexArray = new VertexPositionColorNormal[(int)(vertices.size() * 1.1)];
            System.out.println("Warning had to expand vertex array!!!");
        }
        vertices.toArray(vertexArray);
        indexArray = indices.stream().mapToInt(i -> i).toArray();
    }

    public void updateGraphics(){
        partMain.updateData(vertexArray, indexArray);
    }


    //TODO: Make this accurate
    public float getHeight(float x, float z){
        if(x < 0)
            x = 0;
        else if(x > width - 2)
            x = width - 2;
        if(z < 0)
            z = 0;
        else if(z > height - 2)
            z = height - 2;


        //Get heights from closest vertices
        float leftFront =  heights[((int)z + 0) * width + (int)x + 0];// & 0x00FF;
        float rightFront = heights[((int)z + 0) * width + (int)x + 1];// & 0x00FF;

        float leftBack =   heights[((int)z + 1) * width + (int)x + 0];// & 0x00FF;
        float rightBack =  heights[((int)z + 1) * width + (int)x + 1];// & 0x00FF;

        float xRest = x % 1;
        float zRest = z % 1;

        //Interpolate heights depending on how close
        float height =
                leftBack * (1.0f - xRest)  * (1.0f - zRest) +
                        rightBack * (xRest)        * (1.0f - zRest) +

                        leftFront * (1.0f - xRest) * (zRest)        +
                        rightFront * (xRest)       * (zRest);

        return height * HEIGHT_FACTOR;
    }
}
