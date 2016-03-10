package se.liu.ida.albhe417.tddd78.game;

import org.lwjgl.BufferUtils;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

public abstract class AbstractDrawablePart
{
    /*
     *  TODO: add these
     *  partMatrix
     */
    protected Vector3 position;
    protected float yaw, pitch, roll;
    protected int indexCount;
    protected int vertexArray;
    protected int vertexBuffer;
    protected int indexBuffer;
    protected int shaderProgram;

    abstract public void draw(Matrix4x4 modelMatrixm, int matrixId);

    protected void setPartMatrix(Matrix4x4 modelViewProjectionMatrix, int matrixId){
        final int matrixRows = 4;
        Matrix4x4 partMatrix = new Matrix4x4().getRotated(yaw, pitch, roll);
        partMatrix = partMatrix.getTranslated(new Vector3());



        FloatBuffer buffer = BufferUtils.createFloatBuffer(matrixRows * matrixRows);
        for(int row = 0; row < matrixRows; row++)
            for(int column = 0; column < matrixRows; column++)
                buffer.put(modelViewProjectionMatrix.getValueAt(column, row));
        buffer.flip();
        //TODO: kolla upp booleanen vid ev fel, annars ta bort denna kommentar


        glUniformMatrix4fv(matrixId, false, buffer);
    }

}
