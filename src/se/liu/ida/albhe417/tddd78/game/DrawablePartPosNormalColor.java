package se.liu.ida.albhe417.tddd78.game;

import org.lwjgl.BufferUtils;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DrawablePartPosNormalColor extends AbstractDrawablePart
{
    final int POSITION_INDEX = 2;
    final int NORMAL_INDEX = 1;
    final int COLOR_INDEX = 0;

    public DrawablePartPosNormalColor(VertexPositionNormalColor[] vertices, int[] indices, final int shaderProgram) {
        this.shaderProgram = shaderProgram;
        setup(vertices, indices);
    }


    private void setup(VertexPositionNormalColor[] vertices, int[] indices){
        final int floatsPerVector = 3;

        vertexArray = glGenVertexArrays();
        glBindVertexArray(vertexArray);

        glEnableVertexAttribArray(POSITION_INDEX);
        glEnableVertexAttribArray(NORMAL_INDEX);
        glEnableVertexAttribArray(COLOR_INDEX);

        //Setup vertex buffer
        float[] floats = vertexToFloatArray(vertices);
        FloatBuffer vertexBufferData = BufferUtils.createFloatBuffer(vertices.length * VertexPositionColor.FLOAT_COUNT);
        vertexBufferData.put(floats);
        vertexBufferData.flip();

        vertexBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, vertexBufferData, GL_STATIC_DRAW);


        //Show gpu how to interprete the vertex data
        glVertexAttribPointer(POSITION_INDEX, floatsPerVector, GL_FLOAT, false, VertexPositionColor.FLOAT_COUNT * Float.BYTES, 0);
        glVertexAttribPointer(NORMAL_INDEX, floatsPerVector, GL_FLOAT, false, VertexPositionColor.FLOAT_COUNT * Float.BYTES, floatsPerVector * Float.BYTES);
        glVertexAttribPointer(COLOR_INDEX, floatsPerVector, GL_FLOAT, false, VertexPositionColor.FLOAT_COUNT * Float.BYTES, floatsPerVector * Float.BYTES * 2);


        //Setup index buffer
        IntBuffer indexBufferData = BufferUtils.createIntBuffer(indices.length);
        indexBufferData.put(indices);
        indexBufferData.flip();

        indexBuffer = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufferData, GL_STATIC_DRAW);
        indexCount = indices.length;


        glBindVertexArray(0);
    }


    public void draw(Matrix4x4 modelMatrix, int matrixId){
        Matrix4x4 modelViewProjectionMatrix = modelMatrix;


        glBindVertexArray(vertexArray);
        glUseProgram(shaderProgram);

        setPartMatrix(modelViewProjectionMatrix, matrixId);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);

        glUseProgram(0);
        glBindVertexArray(0);
    }

    private float[] vertexToFloatArray(VertexPositionNormalColor[] vertices){
        float[] floatArray = new float[VertexPositionColor.FLOAT_COUNT * vertices.length];
        int i = 0;
        for(VertexPositionNormalColor vertex : vertices){
            floatArray[i++] = vertex.position.getX();
            floatArray[i++] = vertex.position.getY();
            floatArray[i++] = vertex.position.getZ();

            floatArray[i++] = vertex.normal.getX();
            floatArray[i++] = vertex.normal.getY();
            floatArray[i++] = vertex.normal.getZ();

            floatArray[i++] = vertex.color.getX();
            floatArray[i++] = vertex.color.getY();
            floatArray[i++] = vertex.color.getZ();
        }
        return floatArray;
    }
}
