package se.liu.ida.albhe417.tddd78.game.game_object_part;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.linearmath.Transform;
import org.lwjgl.BufferUtils;
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.graphics.Vertex;
import se.liu.ida.albhe417.tddd78.game.graphics.VertexPositionColorNormal;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * GameObjectPart might be added to any game object. A part is not only graphically visible but also has working physics.
 *
 * The GameObjectPart is the lowest level of all drawable objects, it contains the rather low level stuff such as graphics
 * buffers and references for graphics related things. Thus this is the object initializing OpenGL buffers, performing the
 * draw calls and so on.
 *
 * The GameObjectPart also keeps track of its physics object to know where in the world to draw itself. It also has a method
 * physically connecting itself to another GameObjectPart via a constraint
 */
public class GameObjectPart {

    private RigidBody physicsObject;

    private int indexCount;
    private int vertexArray;
    private int vertexBuffer;
    private int indexBuffer;
    private final int shaderProgram;
    private FloatBuffer vertexBufferData = null;

    public GameObjectPart(final int shaderProgram, int bufferSize, Vertex templateVertex) {
        this.shaderProgram = shaderProgram;
        setupEmpty(templateVertex);
        vertexBufferData = BufferUtils.createFloatBuffer(bufferSize * templateVertex.getFloatCount());
        this.physicsObject = null;
        this.vertexBufferData = null;
    }

    public GameObjectPart(List<VertexPositionColorNormal> vertices, int[] indices, final int shaderProgram, RigidBody physicsObject) {
        this.physicsObject = physicsObject;
        this.indexCount = indices.length;
        this.shaderProgram = shaderProgram;

        setup(vertices, indices);
    }

    public Generic6DofConstraint attachToParentFixed(GameObjectPart parent, Vector3 parentConnectionPoint, Vector3 thisConnectionPoint, AbstractGameObject parentGameObject){
        Transform parentConnection   = new Transform(Matrix4x4.createTranslation(new Vector3(parentConnectionPoint)).toMatrix4f());
        Transform thisConnection     = new Transform(Matrix4x4.createTranslation(new Vector3(thisConnectionPoint)).toMatrix4f());

        Generic6DofConstraint constraint = new Generic6DofConstraint(parent.physicsObject, this.physicsObject, thisConnection, parentConnection, false);

        final Vector3f zero = Vector3.ZERO.toVector3f();
        constraint.setAngularLowerLimit(zero);
        constraint.setAngularUpperLimit(zero);

        constraint.setLinearLowerLimit(zero);
        constraint.setLinearUpperLimit(zero);

        parentGameObject.addConnection(constraint);

        return constraint;
    }

    private void setupEmpty(Vertex templateVertex) {

        vertexArray = glGenVertexArrays();
        glBindVertexArray(vertexArray);

        templateVertex.enableVertexAttributes();

        vertexBuffer = glGenBuffers();
        indexBuffer = glGenBuffers();

        glBindVertexArray(0);
    }

    private void setup(List<VertexPositionColorNormal> vertices, int[] indices) {


        vertexArray = glGenVertexArrays();
        glBindVertexArray(vertexArray);


        vertices.get(0).enableVertexAttributes();

        vertexBuffer = glGenBuffers();
        indexBuffer = glGenBuffers();

        glBindVertexArray(0);
        updateData(vertices, indices);
    }

    public void draw(Matrix4x4 cameraMatrix, int modelViewProjectionMatrixId, int modelMatrixId) {


        glBindVertexArray(vertexArray);
        glUseProgram(shaderProgram);


        Transform transform = new Transform();
        physicsObject.getWorldTransform(transform);

        Matrix4f modelMatrix4f = new Matrix4f();
        transform.getMatrix(modelMatrix4f);

        Matrix4x4 modelMatrix = new Matrix4x4(modelMatrix4f);
        setMatrices(modelMatrix, modelMatrixId);

        Matrix4x4 modelViewProjectionMatrix = cameraMatrix.multiply(modelMatrix);
        setMatrices(modelViewProjectionMatrix, modelViewProjectionMatrixId);


        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);

        glUseProgram(0);
        glBindVertexArray(0);
    }

    private void setMatrices(Matrix4x4 matrix, int matrixId) {
        final int matrixRows = 4;

        FloatBuffer buffer = BufferUtils.createFloatBuffer(matrixRows * matrixRows);
        for (int row = 0; row < matrixRows; row++)
            for (int column = 0; column < matrixRows; column++)
                buffer.put(matrix.values[row][column]);
        buffer.flip();

        glUniformMatrix4fv(matrixId, false, buffer);
    }

    public void setPhysicsObject(RigidBody physicsObject) {
        this.physicsObject = physicsObject;
    }

    public RigidBody getPhysicsObject() {
        return this.physicsObject;
    }

    private Matrix4f getMatrix4f() {
        Transform transform = new Transform();
        Matrix4f matrix4f = new Matrix4f();
        physicsObject.getWorldTransform(transform);
        transform.getMatrix(matrix4f);
        return matrix4f;
    }

    public Matrix4x4 getMatrix() {
        Matrix4f matrix4f = getMatrix4f();
        return new Matrix4x4(matrix4f);
    }

    Matrix4x4 getInvertedMatrix(){
        Matrix4f matrix4f = getMatrix4f();
        matrix4f.invert();
        return new Matrix4x4(matrix4f);
    }

    public void updateData(List<VertexPositionColorNormal> vertices, int[] indices) {

        if (vertices == null || indices == null)
            return;

        glBindVertexArray(vertexArray);
        //Setup vertex buffer

        vertexToFloatBuffer(vertices);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, vertexBufferData, GL_DYNAMIC_DRAW);


        //Setup index buffer
        IntBuffer indexBufferData = BufferUtils.createIntBuffer(indices.length);
        indexBufferData.put(indices);
        indexBufferData.flip();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufferData, GL_DYNAMIC_DRAW);//GL_STATIC_DRAW for non changing objects
        indexCount = indices.length;

        //Show gpu how to interpret the vertex data
        vertices.get(0).setupVertexAttributes();

        glBindVertexArray(0);
    }

    public void update(){

    }

    private void vertexToFloatBuffer(List<VertexPositionColorNormal> vertices) {
        if (vertexBufferData == null || vertices.size() * vertices.get(0).getFloatCount() > vertexBufferData.capacity())
            vertexBufferData = BufferUtils.createFloatBuffer(vertices.size() * vertices.get(0).getFloatCount());
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
