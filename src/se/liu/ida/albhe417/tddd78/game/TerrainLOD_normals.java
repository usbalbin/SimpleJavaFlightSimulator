package se.liu.ida.albhe417.tddd78.game;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.HeightfieldTerrainShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Albin on 14/03/2016.
 */
public class TerrainLOD_normals extends Terrain {
    private GameObjectPart partMain;
    private final DynamicsWorld physics;

    private QuadTree_normals quadTree;

    //TODO: kolla upp
    float[] heights;

    private int[] indexArray;
    private VertexPositionColor[] vertexArray;
    private List<VertexPositionNormalColor> vertices;
    private List<Integer> indices;

    private static final int MAX_EXPECTED_VERT_COUNT = 100000;

    public TerrainLOD_normals(Vector3 position, final int shaderProgram, final DynamicsWorld physics) {
        super(position, 10, shaderProgram);
        this.vertexArray = new VertexPositionColor[MAX_EXPECTED_VERT_COUNT];
        this.vertices = new ArrayList<>(MAX_EXPECTED_VERT_COUNT);
        this.indices = new ArrayList<>(150000);
        this.physics = physics;
        setup();
    }

    protected void setup() {
        heights = Helpers.imageToFloatHeights("content/heightmapLarger.jpg");
        quadTree = new QuadTree_normals(heights, 10);
        height = width = quadTree.getSize();

        this.parts = new ArrayList<>(1);
        partMain = new GameObjectPart(shaderProgram, MAX_EXPECTED_VERT_COUNT, new VertexPositionNormalColor());
        parts.add(partMain);

        //Physics

        MotionState motionState = new DefaultMotionState();
        CollisionShape shape = new HeightfieldTerrainShape(                     //TODO: look up the enum
                width, height, heights, 1, 0, 256, 1, false
        );

        RigidBody physicsObjectMain = new RigidBody(0f, motionState, shape);
        partMain.setPhysicsObject(physicsObjectMain);
        physics.addRigidBody(physicsObjectMain);
    }

    //public void draw(Matrix4x4 cameraMatrix, int matrixId){
    //    draw(cameraMatrix, matrixId);
    //}















    public void update(Vector3 cameraPos){


        vertices.clear();
        indices.clear();

        quadTree.update(cameraPos, vertices, indices);

        if(vertices.size() > vertexArray.length) {
            vertexArray = new VertexPositionColor[vertices.size()];
            System.out.println("Warning had to expand vertex array!!!");
        }
        vertices.toArray(vertexArray);
        indexArray = indices.stream().mapToInt(i -> i).toArray();


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

        return height / 256f * HEIGHT_FACTOR;
    }
}
