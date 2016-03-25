package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Albin on 14/03/2016.
 */
public class TerrainLOD extends Terrain {
    private QuadTree quadTree;

    //TODO: kolla upp
    byte[][] heights;

    private int[] indexArray;
    private VertexPositionColor[] vertexArray;
    private List<VertexPositionColor> vertices;
    private List<Integer> indices;

    private static final int MAX_EXPECTED_VERT_COUNT = 100000;

    public TerrainLOD(Vector3 position, final float heightFactor, final int shaderProgram) {
        super(position, heightFactor, shaderProgram);
        vertexArray = new VertexPositionColor[MAX_EXPECTED_VERT_COUNT];
        //indexArray = new int[5500];
        vertices = new ArrayList<>(MAX_EXPECTED_VERT_COUNT);
        indices = new ArrayList<>(150000);

        setup();
    }

    protected void setup() {
        heights = Helpers.imageToHeights("content/heightmapLarger.jpg");
        quadTree = new QuadTree(heights, 500);
        height = width = quadTree.getSize();

        ArrayList<AbstractDrawablePart> parts = new ArrayList<>(1);
        parts.add(new DrawablePartPosColor(shaderProgram, MAX_EXPECTED_VERT_COUNT));
        setupParts(parts);
    }



    public void draw(Matrix4x4 cameraMatrix, int matrixId, Vector3 cameraPos){
        prepDraw(cameraPos);

        draw(cameraMatrix, matrixId);
    }















    private void prepDraw(Vector3 cameraPos){


        vertices.clear();
        indices.clear();

        quadTree.update(cameraPos, vertices, indices);

        if(vertices.size() > vertexArray.length) {
            vertexArray = new VertexPositionColor[vertices.size()];
            System.out.println("Warning had to expand vertex array!!!");
        }
        vertices.toArray(vertexArray);
        indexArray = indices.stream().mapToInt(i -> i).toArray();




        /*ArrayList<AbstractDrawablePart> parts = new ArrayList<>(1);
        parts.add(part);
        setupParts(parts);*/

        //TODO make it look nicer
        parts.get(0).updateData(vertexArray, indexArray);
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
        float leftFront =  heights[(int)z + 0][(int)x + 0] & 0x00FF;
        float rightFront = heights[(int)z + 0][(int)x + 1] & 0x00FF;

        float leftBack =   heights[(int)z + 1][(int)x + 0] & 0x00FF;
        float rightBack =  heights[(int)z + 1][(int)x + 1] & 0x00FF;

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
