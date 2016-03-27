package se.liu.ida.albhe417.tddd78.game;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import org.lwjgl.BufferUtils;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Matrix4f;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

public abstract class AbstractGameObjectPart
{
    /*
     *  TODO: add these
     *  partMatrix
     */
    protected RigidBody physicsObject;
    protected int indexCount;
    protected int vertexArray;
    protected int vertexBuffer;
    protected int indexBuffer;
    protected int shaderProgram;

    public AbstractGameObjectPart(int shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public AbstractGameObjectPart(RigidBody physicsObject, int indexCount, int shaderProgram) {
        this.physicsObject = physicsObject;
        this.indexCount = indexCount;
        this.shaderProgram = shaderProgram;
    }

    abstract public void draw(Matrix4x4 modelMatrixm, int matrixId);

    protected void setPartMatrix(Matrix4x4 cameraMatrix, int matrixId){
        final int matrixRows = 4;
        Transform transform = new Transform();
        physicsObject.getWorldTransform(transform);

        Matrix4f modelMatrix4f = new Matrix4f();
        transform.getMatrix(modelMatrix4f);

        Matrix4x4 modelMatrix = new Matrix4x4(modelMatrix4f);
        Matrix4x4 modelViewProjectionMatrix = cameraMatrix.multiply(modelMatrix);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(matrixRows * matrixRows);
        for(int row = 0; row < matrixRows; row++)
            for(int column = 0; column < matrixRows; column++)
                buffer.put(modelViewProjectionMatrix.getValueAt(column, row));
        buffer.flip();

        glUniformMatrix4fv(matrixId, false, buffer);
    }

    public void setPhysicsObject(RigidBody physicsObject){
        this.physicsObject = physicsObject;
    }

    public RigidBody getPhysicsObject(){
        return this.physicsObject;
    }

    public Matrix4x4 getMatrix(){
        Transform transform = new Transform();
        Matrix4f matrix4f = new Matrix4f();
        physicsObject.getWorldTransform(transform);
        transform.getMatrix(matrix4f);
        return new Matrix4x4(matrix4f);
    }

    abstract public void updateData(Vertex[] vertices, int[] indices);

}
