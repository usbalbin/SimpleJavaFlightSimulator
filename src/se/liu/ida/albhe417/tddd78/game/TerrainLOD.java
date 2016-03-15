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

    private int[] indexArray;
    private VertexPositionColor[] vertexArray;
    private List<VertexPositionColor> vertices;
    private List<Integer> indices;

    private static final int MAX_EXPECTED_VERT_COUNT = 40000;

    public TerrainLOD(Vector3 position, final int shaderProgram) {
        super(position, 10, shaderProgram);
        vertexArray = new VertexPositionColor[MAX_EXPECTED_VERT_COUNT];
        //indexArray = new int[5500];
        vertices = new ArrayList<>(MAX_EXPECTED_VERT_COUNT);
        indices = new ArrayList<>(5500);

        setup();
    }

    protected void setup() {
        heighMap = Helpers.imageToColors("content/heightmap.png");
        quadTree = new QuadTree(heighMap, 10);
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
}
