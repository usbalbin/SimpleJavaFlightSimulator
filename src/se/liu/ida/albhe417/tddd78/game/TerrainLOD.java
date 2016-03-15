package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Albin on 14/03/2016.
 */
public class TerrainLOD extends Terrain {
    private QuadTree quadTree;

    public TerrainLOD(Vector3 position, final int shaderProgram) {
        super(position, 10, shaderProgram);
        setup();
    }

    protected void setup() {
        heighMap = Helpers.imageToColors("content/heightmap.png");
        quadTree = new QuadTree(heighMap, 10);
        height = width = quadTree.getSize();
    }



    @Override public void draw(Matrix4x4 cameraMatrix, int matrixId){
        Vector3 cameraPos = new Vector3(0, 0, 0);
        prepDraw(cameraPos);

        Matrix4x4 modelViewProjectionMatrix = cameraMatrix.multiply(modelMatrix);


        for(AbstractDrawablePart part : parts){
            part.draw(modelViewProjectionMatrix, matrixId);
        }
    }















    private void prepDraw(Vector3 cameraPos){
        ArrayList<AbstractDrawablePart> parts = new ArrayList<>();
        LinkedList<VertexPositionColor> vertices = new LinkedList<>();
        LinkedList<Integer> indices = new LinkedList<>();

        quadTree.update(cameraPos, vertices, indices);


        VertexPositionColor[] vertexArray = new VertexPositionColor[vertices.size()];
        vertices.toArray(vertexArray);
        int[] indexArray = indices.stream().mapToInt(i -> i).toArray();


        DrawablePartPosColor part = new DrawablePartPosColor(vertexArray, indexArray, shaderProgram);
        parts.add(part);
        setupParts(parts);
    }
}
