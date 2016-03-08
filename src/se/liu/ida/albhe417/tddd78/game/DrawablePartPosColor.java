package se.liu.ida.albhe417.tddd78.game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.BufferUtils;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class DrawablePartPosColor extends AbstractDrawablePart
{
    final int POSITION_INDEX = 1;
    final int COLOR_INDEX = 0;
    
    public DrawablePartPosColor(final VertexPositionColor[] vertices, int[] indices, final int shaderProgram) {
        this.shaderProgram = shaderProgram;
        setup(vertices, indices);
    }


    private void setup(VertexPositionColor[] vertices, int[] indices){
        final int floatsPerVector = 3;

        vertexArray = glGenVertexArrays();
        glBindVertexArray(vertexArray);

        glEnableVertexAttribArray(POSITION_INDEX);
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
        glVertexAttribPointer(COLOR_INDEX, floatsPerVector, GL_FLOAT, false, VertexPositionColor.FLOAT_COUNT * Float.BYTES, floatsPerVector * Float.BYTES);


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


    public void draw(Matrix4x4 modelMatrix){
        glBindVertexArray(vertexArray);
        glUseProgram(shaderProgram);

        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);

        glUseProgram(0);
        glBindVertexArray(0);
    }

    private float[] vertexToFloatArray(VertexPositionColor[] vertices){
        float[] floatArray = new float[VertexPositionColor.FLOAT_COUNT * vertices.length];
        int i = 0;
        for(VertexPositionColor vertex : vertices){
            floatArray[i++] = vertex.position.getX();
            floatArray[i++] = vertex.position.getY();
            floatArray[i++] = vertex.position.getZ();

            floatArray[i++] = vertex.color.getX();
            floatArray[i++] = vertex.color.getY();
            floatArray[i++] = vertex.color.getZ();
        }
        return floatArray;
    }
}
