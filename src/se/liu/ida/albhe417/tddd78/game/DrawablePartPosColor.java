package se.liu.ida.albhe417.tddd78.game;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
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
    public DrawablePartPosColor(final VertexPositionColor[] vertices, int[] indices, final int shaderProgram) {
        this.shaderProgram = shaderProgram;
        setup(vertices, indices);
    }


    private void setup(VertexPositionColor[] vertices, int[] indices){
        final int FLOATS_PER_VECTOR = 3;


        //glBindVertexArray(vertexArrayObjRef);



        //Setup vertex buffer
        float[] floats = vertexToFloatArray(vertices);
        FloatBuffer vertexBufferData = BufferUtils.createFloatBuffer(vertices.length * VertexPositionColor.FLOAT_COUNT);
        vertexBufferData.put(floats);
        vertexBufferData.flip();

        vertexBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, vertexBufferData, GL_STATIC_DRAW);


        //Setup index buffer
        IntBuffer indexBufferData = BufferUtils.createIntBuffer(indices.length);
        indexBufferData.put(indices);
        indexBufferData.flip();

        indexBuffer = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufferData, GL_STATIC_DRAW);
        triangleCount = indices.length / 3;



        //Set up how the GPU should interprete the array of floats
        //glUseProgram(shaderProgram);

    }


    public void draw(Matrix4x4 modelMatrix){
        final int FLOATS_PER_VECTOR = 3;

        //Find pos and color attrib
        int positionAttributes = 0;//glGetAttribLocation(shaderProgram, "position");
        int colorAttributes = 1;//glGetAttribLocation(shaderProgram, "color");

        //Enable them
        glEnableVertexAttribArray(positionAttributes);
        glEnableVertexAttribArray(colorAttributes);

        //Bind vertex buffer
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);

        //Show gpu how to interprete the data
        glVertexAttribPointer(positionAttributes, FLOATS_PER_VECTOR, GL_FLOAT, false, VertexPositionColor.FLOAT_COUNT * Float.BYTES, 0);
        glVertexAttribPointer(colorAttributes, FLOATS_PER_VECTOR, GL_FLOAT, false, VertexPositionColor.FLOAT_COUNT * Float.BYTES, FLOATS_PER_VECTOR * Float.BYTES);


        //Bind index buffer and draw triangles
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        //glDrawElements(GL_TRIANGLES, triangleCount / 3, GL_UNSIGNED_INT, 0);
        glDrawArrays(GL_TRIANGLES, 0, 3);

        //Clean up
        glDisableVertexAttribArray(positionAttributes);
        glDisableVertexAttribArray(colorAttributes);
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
