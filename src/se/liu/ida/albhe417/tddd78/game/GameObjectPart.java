package se.liu.ida.albhe417.tddd78.game;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import org.lwjgl.BufferUtils;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;

import javax.vecmath.Matrix4f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class GameObjectPart {
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
    protected FloatBuffer vertexBufferData;

    public GameObjectPart(final int shaderProgram, int bufferSize, Vertex templateVertex) {
        this.shaderProgram = shaderProgram;
        setupEmpty(templateVertex);
        vertexBufferData = BufferUtils.createFloatBuffer(bufferSize * templateVertex.getFloatCount());
    }

    public GameObjectPart(final Vertex[] vertices, int[] indices, final int shaderProgram, RigidBody physicsObject) {
        this.physicsObject = physicsObject;
        this.indexCount = indices.length;
        this.shaderProgram = shaderProgram;

        setup(vertices, indices);
    }

    protected void setupEmpty(Vertex templateVertex) {


        vertexArray = glGenVertexArrays();
        glBindVertexArray(vertexArray);

        templateVertex.enableVertexAttribs();

        vertexBuffer = glGenBuffers();
        indexBuffer = glGenBuffers();

        glBindVertexArray(0);
    }

    protected void setup(Vertex[] vertices, int[] indices) {


        vertexArray = glGenVertexArrays();
        glBindVertexArray(vertexArray);


        vertices[0].enableVertexAttribs();

        vertexBuffer = glGenBuffers();
        indexBuffer = glGenBuffers();

        glBindVertexArray(0);
        updateData(vertices, indices);
    }

    public void draw(Matrix4x4 cameraMatrix, int MVPmatrixId, int modelMatrixId) {


        glBindVertexArray(vertexArray);
        glUseProgram(shaderProgram);


        Transform transform = new Transform();
        physicsObject.getWorldTransform(transform);

        Matrix4f modelMatrix4f = new Matrix4f();
        transform.getMatrix(modelMatrix4f);

        Matrix4x4 modelMatrix = new Matrix4x4(modelMatrix4f);
        setMatrices(modelMatrix, modelMatrixId);

        Matrix4x4 modelViewProjectionMatrix = cameraMatrix.multiply(modelMatrix);
        setMatrices(modelViewProjectionMatrix, MVPmatrixId);


        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);

        glUseProgram(0);
        glBindVertexArray(0);
    }

    protected void setMatrices(Matrix4x4 matrix, int matrixId) {
        final int matrixRows = 4;

        FloatBuffer buffer = BufferUtils.createFloatBuffer(matrixRows * matrixRows);
        for (int row = 0; row < matrixRows; row++)
            for (int column = 0; column < matrixRows; column++)
                buffer.put(matrix.getValueAt(column, row));
        buffer.flip();

        glUniformMatrix4fv(matrixId, false, buffer);
    }

    public void setPhysicsObject(RigidBody physicsObject) {
        this.physicsObject = physicsObject;
    }

    public RigidBody getPhysicsObject() {
        return this.physicsObject;
    }

    public Matrix4x4 getMatrix() {
        Transform transform = new Transform();
        Matrix4f matrix4f = new Matrix4f();
        physicsObject.getWorldTransform(transform);
        transform.getMatrix(matrix4f);
        return new Matrix4x4(matrix4f);
    }

    public void updateData(Vertex[] vertices, int[] indices) {
        final int floatsPerVector = 3;

        if (vertices == null || indices == null)
            return;

        glBindVertexArray(vertexArray);
        //Setup vertex buffer

        vertexToFloatBuffer(vertices);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, vertexBufferData, GL_DYNAMIC_DRAW);//TODO: GL_STATIC_DRAW for nonchanging objects


        //Setup index buffer
        IntBuffer indexBufferData = BufferUtils.createIntBuffer(indices.length);
        indexBufferData.put(indices);
        indexBufferData.flip();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufferData, GL_DYNAMIC_DRAW);//GL_STATIC_DRAW for nonchanging objects
        indexCount = indices.length;

        //Show gpu how to interprete the vertex data
        vertices[0].setupVertexAttribs();
        vertices = null;
        indices = null;

        glBindVertexArray(0);
    }

    private void vertexToFloatBuffer(Vertex[] vertices) {
        if (vertexBufferData == null || vertices.length * vertices[0].getFloatCount() > vertexBufferData.capacity())
            vertexBufferData = BufferUtils.createFloatBuffer(vertices.length * vertices[0].getFloatCount());
        else
            vertexBufferData.clear();

        for (Vertex vertex : vertices) {
            if (vertex == null)
                break;
            vertexBufferData.put(vertex.getFloats());
        }

        vertexBufferData.flip();
    }

    public void destroyGraphics(){
        glDeleteBuffers(vertexBuffer);
        glDeleteBuffers(indexBuffer);
        glDeleteVertexArrays(vertexArray);
    }

    public void destroy(DynamicsWorld physics){
        destroyGraphics();
        physics.removeRigidBody(physicsObject);
    }
}
