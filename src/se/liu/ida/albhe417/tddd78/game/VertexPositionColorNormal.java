package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

/**
 * VertexPositionColorNormal is a structure holding information about position color and surface normal.
 * A bunch of these are useful for representing colored and simply shaded 3D objects.
 */
public class VertexPositionColorNormal implements Vertex
{
    private static int positionIndex = -1;
    private static int colorIndex = -1;
    private static int normalIndex = -1;

    private static final int FLOATS_PER_VECTOR = 3;
    private static final int FLOAT_COUNT = FLOATS_PER_VECTOR * 3;

    /**
     * The position of the vertex
     */
    public Vector3 position;

    /**
     * The color of the vertex
     */
    public Vector3 color;

    /**
     * The surface normal in this vertex point
     */
    public Vector3 normal;

    public VertexPositionColorNormal(){
        this.position = null;
        this.color = null;
        this.normal = null;
    }

    public VertexPositionColorNormal(Vector3 position, Vector3 color){
        this.position = position;
        this.color = color;
        this.normal = new Vector3(0);
    }

    public VertexPositionColorNormal(Vector3 position, Vector3 color, Vector3 normal){
        this.position = position;
        this.color = color;
        this.normal = normal;
    }

    public static void init(int shaderProgram){
        int positionIndex = glGetAttribLocation(shaderProgram, "position");
        int colorIndex = glGetAttribLocation(shaderProgram, "color");
        int normalIndex = glGetAttribLocation(shaderProgram, "normal");

        VertexPositionColorNormal.positionIndex = positionIndex;
        VertexPositionColorNormal.colorIndex = colorIndex;
        VertexPositionColorNormal.normalIndex = normalIndex;
    }

    @Override
    public int getFloatCount(){
        return FLOAT_COUNT;
    }

    @Override
    public float[] getFloats(){
        float[] result = new float[FLOAT_COUNT];
        System.arraycopy(position.values,   0, result, 0, FLOATS_PER_VECTOR);
        System.arraycopy(color.values,      0, result, FLOATS_PER_VECTOR, FLOATS_PER_VECTOR);
        System.arraycopy(normal.values,     0, result, FLOATS_PER_VECTOR * 2, FLOATS_PER_VECTOR);


        return result;
    }

    @Override
    public void enableVertexAttributes(){
        assert positionIndex != -1 && colorIndex != -1 && normalIndex != -1 : "Forgot to run setupVertexAttributes()?";

        glEnableVertexAttribArray(positionIndex);
        glEnableVertexAttribArray(colorIndex);
        glEnableVertexAttribArray(normalIndex);
    }

    @Override
    public void setupVertexAttributes() {
        //Show gpu how to interpret the vertex data
        final int floatsPerVector = 3;
        glVertexAttribPointer(positionIndex, floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, 0L                                     );
        glVertexAttribPointer(colorIndex, floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, (long)floatsPerVector * Float.BYTES    );
        glVertexAttribPointer(normalIndex, floatsPerVector, GL_FLOAT, false, FLOAT_COUNT * Float.BYTES, (long)floatsPerVector * Float.BYTES * 2);
    }

    @Override
    public boolean equals(Object obj) {
        if(this.getClass() != obj.getClass())
            return false;
        VertexPositionColorNormal other = (VertexPositionColorNormal)obj;
        return this.position.equals(other.position);
    }

}